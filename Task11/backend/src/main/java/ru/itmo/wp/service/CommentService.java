package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.form.CommentForm;
import ru.itmo.wp.repository.CommentRepository;

@Service
public class CommentService {
    private final JwtService jwtService;
    private final CommentRepository commentRepository;

    public CommentService(JwtService jwtService, CommentRepository commentRepository) {
        this.jwtService = jwtService;
        this.commentRepository = commentRepository;
    }

    public Comment save(CommentForm commentForm) {
        Comment comment = new Comment();
        comment.setText(commentForm.getText());
        comment.setUser(jwtService.find(commentForm.getJwt()));
        comment.setPost(commentForm.getPost());
        commentRepository.save(comment);
        return comment;
    }
}
