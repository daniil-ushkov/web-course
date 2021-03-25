package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArticleService {
    ArticleRepository articleRepository = new ArticleRepositoryImpl();
    UserService userService = new UserService();

    public void save(Article article) {
        articleRepository.save(article);
    }

    public List<Map<String, Object>> findAllWithUserName() {
        List<Article> articles = articleRepository.findAll(Map.of("hidden", false));
        return articles.stream()
                .map(a -> Map.of("article", a, "userName", userService.find(a.getUserId()).getLogin()))
                .collect(Collectors.toList());
    }

    public List<Article> findAllByUser(User user) {
        return articleRepository.findAll(Map.of("userId", user.getId()));
    }

    public boolean updateHidden(long id, boolean set) {
        articleRepository.update(Map.of("hidden", set), Map.of("id", id));
        return set;
    }

    public Article find(long id) {
        return articleRepository.find(id);
    }
}
