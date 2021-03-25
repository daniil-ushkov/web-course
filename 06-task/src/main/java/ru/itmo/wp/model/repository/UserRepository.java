package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    User find(long id);
    User findByLogin(String login);
    User findByEmail(String email);
    User findBy(Map<String, Object> map);
    long findCount();
    List<User> findAll();
    void save(User user, String passwordSha);
}
