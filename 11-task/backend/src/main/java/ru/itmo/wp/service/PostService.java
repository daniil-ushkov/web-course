package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.form.PostForm;
import ru.itmo.wp.repository.PostRepository;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final JwtService jwtService;

    public PostService(PostRepository postRepository, JwtService jwtService) {
        this.postRepository = postRepository;
        this.jwtService = jwtService;
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreationTimeDesc();
    }

    public void create(PostForm postForm) {
        Post post = new Post();
        post.setUser(jwtService.find(postForm.getJwt()));
        post.setTitle(postForm.getTitle());
        post.setText(postForm.getText());
        postRepository.save(post);
    }
}
