package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.service.TalkService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class TalksPage extends Page {
    TalkService talkService = new TalkService();
    @Override
    public void before(HttpServletRequest request, Map<String, Object> view) {
        super.before(request, view);
        if (getUser() == null) {
            setMessage("Enter your account to have an access to talks");
            throw new RedirectException("/index");
        }
        view.put("users", userService.findAll());
        view.put("userService", userService);
    }

    public void submit(HttpServletRequest request, Map<String, Object> view) {
        Talk talk = new Talk();
        talk.setSourceUserId(getUser().getId());
        talk.setTargetUserId(userService.findBy("login", request.getParameter("to")).getId());
        talk.setText(request.getParameter("message"));
        talkService.save(talk);
    }

    @Override
    public void after(HttpServletRequest request, Map<String, Object> view) {
        view.put("talks", talkService.findByUser(getUser()));
        super.after(request, view);
    }
}
