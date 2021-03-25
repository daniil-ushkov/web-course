package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class MyArticlesPage extends Page {
    ArticleService articleService = new ArticleService();

    @Override
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        if (getUser() == null) {
            throw new RedirectException("/index");
        }
    }

    @Json
    public void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("articles", articleService.findAllByUser((User) request.getSession().getAttribute("user")));
    }

    @Json
    public void hideOrShow(HttpServletRequest request, Map<String, Object> view) {
        long id = Long.parseLong(request.getParameter("id"));
        if (getUser() == null || getUser().getId() != articleService.find(id).getUserId()) {
            throw new RedirectException("/index");
        }
        boolean hide = articleService.updateHidden(id, request.getParameter("hide").equals("Hide"));
        view.put("hide", hide);
        view.put("id", id);
    }
}
