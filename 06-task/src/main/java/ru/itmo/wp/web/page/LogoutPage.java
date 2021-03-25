package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class LogoutPage extends Page {
    @Override
    public void action(HttpServletRequest request, Map<String, Object> view) {
        User user = getUser();
        removeUser();
        eventService.saveEvent(Event.newLogoutEvent(user));

        setMessage("Good bye. Hope to see you soon!");
        throw new RedirectException("/index");
    }
}
