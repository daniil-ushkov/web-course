package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/** @noinspection unused*/
public class UsersPage extends Page {
    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        List<User> users = userService.findAll();
        if (!isAdmin()) {
            users.forEach(u -> u.setAdmin(false));
            view.put("admin", false);
        } else {
            view.put("admin", true);
            view.put("userId", getUser().getId());
        }
        view.put("users", users);
    }

    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        view.put("user", userService.find(Long.parseLong(request.getParameter("userId"))));
    }

    private void updateAdmin(HttpServletRequest request, Map<String, Object> view) {
        if (!isAdmin()) {
            throw new RedirectException("/index");
        }
        view.put("admin", userService.updateAdmin(Long.parseLong(request.getParameter("userId")),
                request.getParameter("admin").equals("enable")));
    }
}
