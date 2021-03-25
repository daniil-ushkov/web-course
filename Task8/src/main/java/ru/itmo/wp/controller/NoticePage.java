package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.domain.Notice;
import ru.itmo.wp.service.NoticeService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class NoticePage extends Page {
    private final NoticeService noticeService;

    public NoticePage(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/notices")
    public String form(Model model) {
        model.addAttribute("noticeForm", new Notice());
        return "NoticePage";
    }

    @PostMapping("/notices")
    public String createNotice(@Valid @ModelAttribute("noticeForm") Notice notice,
                               BindingResult bindingResult,
                               HttpSession httpSession
                               ) {
        if (bindingResult.hasErrors()) {
            return "NoticePage";
        }
        noticeService.save(notice);
        putMessage(httpSession, "Notice created.");
        return "redirect:/notices";
    }
}
