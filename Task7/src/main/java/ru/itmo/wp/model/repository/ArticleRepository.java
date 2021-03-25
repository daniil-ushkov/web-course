package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Article;

import java.util.List;
import java.util.Map;

public interface ArticleRepository {
    Article find(long id);
    void save(Article article);
    List<Article> findAll(Map<String, Object> map);
    void update(Map<String, Object> set, Map<String, Object> indexes);
}
