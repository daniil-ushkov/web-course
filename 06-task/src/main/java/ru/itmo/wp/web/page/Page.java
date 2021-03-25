package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.EventService;
import ru.itmo.wp.model.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class Page {
    protected final UserService userService = new UserService();
    protected final EventService eventService = new EventService();

    private HttpServletRequest savedRequest;

    public void before(HttpServletRequest request, Map<String, Object> view) {
        savedRequest = request;
        putUser(view);
        putMessage(view);
        view.put("userCount", new UserService().findCount());
    }

    public void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    public void after(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    public void setUser(User user) {
        savedRequest.getSession().setAttribute("user", user);
    }

    public User getUser() {
        return (User) savedRequest.getSession().getAttribute("user");
    }

    public void removeUser() {
        savedRequest.getSession().removeAttribute("user");
    }

    public void putUser(Map<String, Object> view) {
        User user = getUser();
        if (user != null) {
            view.put("user", user);
        }
    }

    public void setMessage(String message) {
        savedRequest.getSession().setAttribute("message", message);
    }

    private void putMessage(Map<String, Object> view) {
        String message = (String) savedRequest.getSession().getAttribute("message");
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            savedRequest.getSession().removeAttribute("message");
        }
    }
}
