package ru.itmo.wp.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.itmo.wp.domain.Post;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


public class CommentForm {
    private String jwt;

    private Post post;

    @NotEmpty
    @Size(min = 1, max = 100)
    private String text;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
