package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.RepositoryException;
import ru.itmo.wp.model.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {

    {
        repositoryName = "User_admin";
        clazz = User.class;
    }

    @Override
    public User findByLogin(String login) {
        return findBy(Map.of("login", login));
    }

    @Override
    public User findByLoginAndPasswordSha(String login, String passwordSha) {
        return findBy(Map.of("login", login, "passwordSha", passwordSha));
    }

    @Override
    public List<User> findAll() {
        return findAll(Map.of());
    }

    @Override
    public void save(User user, String passwordSha) {
        save(user, Map.of("passwordSha", passwordSha));
    }
}
