package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class Page {
    protected final UserService userService = new UserService();
    private HttpServletRequest savedRequest;

    protected void before(HttpServletRequest request, Map<String, Object> view) {
        savedRequest = request;
        putUser(view);
    }

    protected void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    protected void after(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    protected void setUser(User user) {
        savedRequest.getSession().setAttribute("user", user);
    }

    protected User getUser() {
        return (User) savedRequest.getSession().getAttribute("user");
    }

    protected boolean isAdmin() {
        return getUser() != null && userService.find(getUser().getId()).isAdmin();
    }

    protected void removeUser() {
        savedRequest.getSession().removeAttribute("user");
    }

    protected void putUser(Map<String, Object> view) {
        User user = getUser();
        if (user != null) {
            view.put("user", user);
        }
    }

    protected void setMessage(String message) {
        savedRequest.getSession().setAttribute("message", message);
    }

    protected void putMessage(Map<String, Object> view) {
        String message = (String) savedRequest.getSession().getAttribute("message");
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            savedRequest.getSession().removeAttribute("message");
        }
    }
}
