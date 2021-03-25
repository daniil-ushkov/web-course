package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.wp.form.Id;
import ru.itmo.wp.form.validator.IdValidator;
import ru.itmo.wp.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class UsersPage extends Page {
    private final UserService userService;

    public UsersPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/all")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "UsersPage";
    }

    @GetMapping("/user/{id}")
    public String userById(@PathVariable String id, Model model) {
        long idValue;
        try {
            idValue = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return "redirect:/users/all";
        }
        model.addAttribute("user", userService.findById(idValue));
        return "UserPage";
    }

    @GetMapping("/enable/{id}")
    public String enable(@PathVariable String id, HttpSession httpSession) {
        long idValue;
        try {
            idValue = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return "redirect:/users/all";
        }
        if (getUser(httpSession).getId() == idValue) {
            putMessage(httpSession, "You can not change your abilities.");
            return "redirect:/users/all";
        }
        if (userService.findById(idValue) == null) {
            putMessage(httpSession, "User with id=" + id + " does not exists.");
            return "redirect:/users/all";
        }
        userService.updateAbilities(idValue, false);
        putMessage(httpSession, "User with id=" + id + " enabled.");
        return "redirect:/users/all";
    }

    @GetMapping("/disable/{id}")
    public String disable(@PathVariable String id, HttpSession httpSession) {
        long idValue;
        try {
            idValue = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return "redirect:/users/all";
        }
        if (getUser(httpSession).getId() == idValue) {
            putMessage(httpSession, "You can not change your abilities.");
            return "redirect:/users/all";
        }
        if (userService.findById(idValue) == null) {
            putMessage(httpSession, "User with id=" + id + " does not exists.");
            return "redirect:/users/all";
        }
        userService.updateAbilities(idValue, true);
        putMessage(httpSession, "User with id=" + id + " disabled.");
        return "redirect:/users/all";
    }
}
