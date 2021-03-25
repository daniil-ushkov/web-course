package ru.itmo.wp.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.UserCredentialsEnter;
import ru.itmo.wp.form.UserCredentialsRegister;
import ru.itmo.wp.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(UserCredentialsRegister userCredentials) {
        User user = new User();
        user.setName(userCredentials.getName());
        user.setLogin(Strings.isEmpty(userCredentials.getLogin()) ? "None" : userCredentials.getLogin());
        user.setAdmin(false);
        userRepository.save(user);
        userRepository.updatePasswordSha(user.getId(), userCredentials.getLogin(), userCredentials.getPassword());
    }

    public User findByLoginAndPassword(String login, String password) {
        return login == null || password == null ? null : userRepository.findByLoginAndPassword(login, password);
    }

    public User findByLogin(String login) {
        return login == null ? null : userRepository.findByLogin(login);
    }

    public User findById(Long id) {
        return id == null ? null : userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByIdDesc();
    }

    public void writePost(User user, Post post) {
        user.addPost(post);
        userRepository.save(user);
    }
}
