package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.Role;
import ru.itmo.wp.security.AnyRole;
import ru.itmo.wp.security.Guest;
import ru.itmo.wp.service.CommentService;
import ru.itmo.wp.service.PostService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class PostPage extends Page {
    private final PostService postService;
    private final CommentService commentService;

    public PostPage(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @Guest
    @GetMapping("/post/{strId}")
    public String postByIdGet(@PathVariable String strId, Model model) {
        return ifCorrectId(strId, "/posts", id -> {
            Optional<Post> optionalPost = postService.findById(id);
            optionalPost.ifPresent(post -> {
                model.addAttribute("post", post);
                model.addAttribute("comment", new Comment());
            });
            return "PostPage";
        });
    }

    @AnyRole({Role.Name.WRITER, Role.Name.ADMIN})
    @PostMapping("/post/{strId}")
    public String postByIdPost(@PathVariable String strId,
                               @Valid @ModelAttribute("comment") Comment comment,
                               BindingResult bindingResult,
                               HttpSession httpSession,
                               Model model) {
        return ifCorrectId(strId, "/posts", id -> {
            Optional<Post> optionalPost = postService.findById(id);
            Post post;
            if (optionalPost.isEmpty()) {
                return "PostPage";
            } else {
                post = optionalPost.get();
            }
            if (bindingResult.hasErrors()) {
                model.addAttribute("post", post);
                return "PostPage";
            }
            try {
                commentService.writeComment(getUser(httpSession), post, comment);
                putMessage(httpSession, "You published new comment");
            } catch (Exception e) {
                putMessage(httpSession, "Comment publishing failed");
                return "PostPage";
            }
            return "redirect:/post/" + strId;
        });
    }
}
