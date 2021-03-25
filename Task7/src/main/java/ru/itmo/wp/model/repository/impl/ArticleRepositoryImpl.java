package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.repository.ArticleRepository;

import java.util.List;
import java.util.Map;

public class ArticleRepositoryImpl extends BaseRepository<Article> implements ArticleRepository {

    {
        repositoryName = "Article_hidden";
        clazz = Article.class;
    }

    public void save(Article article) {
        super.save(article, Map.of());
    }
}
