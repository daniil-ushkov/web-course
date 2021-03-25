package ru.itmo.wp.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.exception.ValidationException;
import ru.itmo.wp.form.PostForm;
import ru.itmo.wp.form.validator.CommentFromValidator;
import ru.itmo.wp.form.validator.PostFormValidator;
import ru.itmo.wp.service.PostService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/1/posts")
public class PostController {
    private final PostService postService;
    private final PostFormValidator postFormValidator;

    public PostController(PostService postService, PostFormValidator postFormValidator, CommentFromValidator commentFromValidator) {
        this.postService = postService;
        this.postFormValidator = postFormValidator;
    }

    @InitBinder("postForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(postFormValidator);
    }


    @GetMapping("")
    public List<Post> findPosts() {
        return postService.findAll();
    }

    @PostMapping("")
    public void create(@RequestBody @Valid PostForm postForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        postService.create(postForm);
    }
}
