package ru.itmo.wp.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.exception.ValidationException;
import ru.itmo.wp.form.CommentForm;
import ru.itmo.wp.form.validator.CommentFromValidator;
import ru.itmo.wp.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("api/1/comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentFromValidator commentFromValidator;

    public CommentController(CommentService commentService, CommentFromValidator commentFromValidator) {
        this.commentService = commentService;
        this.commentFromValidator = commentFromValidator;
    }

    @InitBinder("commentForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(commentFromValidator);
    }

    @PostMapping("")
    public Comment writeComment(@RequestBody @Valid CommentForm commentForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        return commentService.save(commentForm);
    }
}
