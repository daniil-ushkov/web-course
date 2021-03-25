package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class EnterPage extends Page {
    public void enter(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        String identifier = request.getParameter("loginOrEmail");
        String password = request.getParameter("password");

        String identifierName = identifier.matches(".*@.*") ? "email" : "login";
        userService.validateEnterBy(identifierName, identifier, password);
        User user = userService.findByIdentifierAndPassword(identifierName, identifier, password);

        eventService.saveEvent(Event.newEnterEvent(user));

        setUser(user);
        setMessage("Hello, " + user.getLogin());

        throw new RedirectException("/index");
    }
}
