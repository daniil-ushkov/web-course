package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.database.DatabaseUtils;
import ru.itmo.wp.model.exception.RepositoryException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseRepository<T> {
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
                Field field = clazz.getDeclaredField(statement.getMetaData().getColumnName(i));
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
                    field.set(object, resultSet.getString(i));
                } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                    field.set(object, resultSet.getLong(i));
                } else if (field.getType().equals(Date.class)) {
                    field.set(object, resultSet.getTimestamp(i));
                } else if (field.getType().isEnum()) {
                    field.set(object, clazz.getMethod("valueOf", String.class).invoke(null, resultSet.getString(i)));
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

    public List<T> findAll() {
        return doFind(
                "SELECT * FROM " + repositoryName + " ORDER BY id DESC",
                SQLSetter.EMPTY_SQL_SETTER, (statement, resultSet) -> {
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
                entries.stream()
                        .map(Map.Entry::getKey)
                        .map(k -> k + "=?")
                        .collect(Collectors.joining(" AND "));
        return doFind(SQLRequest, SQLSetter.newSQLSetter(
                entries.stream()
                        .map(Map.Entry::getValue)
                        .toArray(Object[]::new)),
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
                    } else {
                        throw new IllegalArgumentException("Values of map are supposed to be String or Long");
                    }
                }
            };
        }
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
