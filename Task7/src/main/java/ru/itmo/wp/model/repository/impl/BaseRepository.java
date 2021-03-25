package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.database.DatabaseUtils;
import ru.itmo.wp.model.domain.Domain;
import ru.itmo.wp.model.exception.RepositoryException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseRepository<T extends Domain> {
    protected final DataSource DATA_SOURCE = DatabaseUtils.getDataSource();

    protected String repositoryName;
    protected Class<T> clazz;

    protected T convert(PreparedStatement statement, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        T object;
        try {
            object = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RepositoryException("Repository can't access to constructor of " + clazz.getName());
        }
        for (int i = 1; i <= statement.getMetaData().getColumnCount(); i++) {
            try {
                Field field;
                try {
                    field = clazz.getDeclaredField(statement.getMetaData().getColumnName(i));
                } catch (NoSuchFieldException e) {
                    field = Domain.class.getDeclaredField(statement.getMetaData().getColumnName(i));
                }
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
                    field.set(object, resultSet.getString(i));
                } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                    field.set(object, resultSet.getLong(i));
                } else if (field.getType().equals(Date.class)) {
                    field.set(object, resultSet.getTimestamp(i));
                } else if (field.getType().isEnum()) {
                    field.set(object, clazz.getMethod("valueOf", String.class).invoke(null, resultSet.getString(i)));
                } else if (field.getType().equals(boolean.class)) {
                    field.set(object, resultSet.getBoolean(i));
                } else {
                    throw new RepositoryException("Fields of " + clazz.getName() +
                            " supposed to be of String, Long or Date type");
                }
            } catch (IllegalAccessException e) {
                throw new RepositoryException("Repository can't access to field of " + clazz.getName());
            } catch (NoSuchFieldException | NoSuchMethodException e) {
                // No operations.
            } catch (InvocationTargetException e) {
                throw new RepositoryException("Repository can't set value to enum field of " + clazz.getName());
            }
        }

        return object;
    }

    public T find(long id) {
        return findBy(Map.of("id", id));
    }

    public List<T> findAll(Map<String, Object> map) {
        List<Map.Entry<String, Object>> entries = new ArrayList<>(map.entrySet());
        String SQLRequest = "SELECT * FROM " + repositoryName +
                (!map.isEmpty() ?" WHERE " + toString(entries.stream()) : "") + " ORDER BY id DESC";
        return doFind(SQLRequest,
                SQLSetter.newSQLSetter(toArray(entries.stream())),
                (statement, resultSet) -> {
                    List<T> tList = new ArrayList<>();
                    T t;
                    while ((t = convert(statement, resultSet)) != null) {
                        tList.add(t);
                    }
                    return tList;
                },
                "Can't list rows of " + repositoryName);
    }

    public T findBy(Map<String, Object> map) {
        List<Map.Entry<String, Object>> entries = new ArrayList<>(map.entrySet());
        String SQLRequest = "SELECT * FROM " + repositoryName + " WHERE " +
                toString(entries.stream());
        return doFind(SQLRequest, SQLSetter.newSQLSetter(
                toArray(entries.stream())),
                this::convert,
                "Can't find row in " + repositoryName);
    }

    public long findCount() {
        return doFind("SELECT COUNT(*) FROM " + repositoryName, SQLSetter.EMPTY_SQL_SETTER,
                (statement, resultSet) -> {
                    if (!resultSet.next()) {
                        throw new RepositoryException("Can't count number of rows in " + repositoryName);
                    }
                    return resultSet.getLong(1);
                }, "Can't count number of rows in " + repositoryName);
    }

    public void save(T article, Map<String, Object> additional) {
        Field[] declaredFields = clazz.getDeclaredFields();
        String SQLRequest = "INSERT INTO `" +
                repositoryName +
                "` (" + Arrays.stream(declaredFields)
                                .map(f -> '`' + f.getName() + "`, ")
                                .collect(Collectors.joining()) +
                additional.keySet().stream()
                        .map(s -> '`' + s + "`, ")
                        .collect(Collectors.joining()) +
                "`creationTime`" + ") " +
                "VALUES (" + "?, ".repeat(declaredFields.length + additional.size()) + "NOW())";
        doSQLQuery(
                SQLRequest,
                SQLSetter.newSQLSetter(Stream.concat(Arrays.stream(declaredFields)
                                .map(f -> {
                                    f.setAccessible(true);
                                    try {
                                        return f.get(article);
                                    } catch (IllegalAccessException e) {
                                        throw new RepositoryException("Can't save " + clazz.getName() + " [no autogenerated fields].");
                                    }
                                }),
                        additional.values().stream()).toArray()),
                statement -> {
                    if (statement.executeUpdate() != 1) {
                        throw new RepositoryException("Can't save Article.");
                    } else {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            article.setId(generatedKeys.getLong(1));
                            article.setCreationTime(find(article.getId()).getCreationTime());
                        } else {
                            throw new RepositoryException("Can't save " + clazz.getName() + " [no autogenerated fields].");
                        }
                        return null;
                    }
                },
                "Can't save " + clazz.getName() + ".",
                Statement.RETURN_GENERATED_KEYS);
    }

    public void update(Map<String, Object> set, Map<String, Object> indexes) {
        List<Map.Entry<String, Object>> entriesSet = new ArrayList<>(set.entrySet());
        List<Map.Entry<String, Object>> entriesIndexes = new ArrayList<>(indexes.entrySet());
        String SQLRequest = "UPDATE " + repositoryName +
                " SET " +
                toString(entriesSet.stream()) +
                " WHERE " +
                toString(entriesIndexes.stream());
        doSQLQuery(
                SQLRequest,
                SQLSetter.newSQLSetter(toArray(Stream.concat(entriesSet.stream(), entriesIndexes.stream()))),
                PreparedStatement::executeUpdate,
                "Can't update " + repositoryName);
    }

    protected <U> U doFind(String SQLRequest,
                           SQLSetter setter,
                           SQLResultConverter<U> converter,
                           String exceptionMessage) {
        return doSQLQuery(
                SQLRequest,
                setter,
                statement -> {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        return converter.convert(statement, resultSet);
                    }
                },
                exceptionMessage);
    }

    protected <U> U doSQLQuery(String SQLRequest,
                               SQLSetter setter,
                               SQLExecutor<U> executor,
                               String exceptionMessage,
                               int... requestParams) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQLRequest, requestParams)) {
                setter.set(statement);
                return executor.execute(statement);
            }
        } catch (SQLException e) {
            throw new RepositoryException(exceptionMessage, e);
        }
    }

    @FunctionalInterface
    protected interface SQLSetter {
        SQLSetter EMPTY_SQL_SETTER = statement -> {
        };

        void set(PreparedStatement statement) throws SQLException;

        static SQLSetter newSQLSetter(Object... values) {
            return statement -> {
                for (int i = 0; i < values.length; ++i) {
                    Object value = values[i];
                    if (value instanceof String) {
                        statement.setString(i + 1, (String) value);
                    } else if (value instanceof Long) {
                        statement.setLong(i + 1, (Long) value);
                    } else if (value.getClass().isEnum()) {
                        statement.setString(i + 1, value.toString());
                    } else if (value instanceof Boolean) {
                        statement.setBoolean(i + 1, (Boolean) value);
                    } else {
                        throw new IllegalArgumentException("Values of map are supposed to be String or Long");
                    }
                }
            };
        }
    }

    private String toString(Stream<Map.Entry<String, Object>> stream) {
        return stream.map(Map.Entry::getKey)
                .map(k -> k + "=?")
                .collect(Collectors.joining(" AND "));
    }

    private Object[] toArray(Stream<Map.Entry<String, Object>> stream) {
        return stream.map(Map.Entry::getValue)
                .toArray(Object[]::new);
    }

    @FunctionalInterface
    protected interface SQLExecutor<T> {
        T execute(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    protected interface SQLResultConverter<T> {
        T convert(PreparedStatement statement, ResultSet resultSet) throws SQLException;
    }
}
