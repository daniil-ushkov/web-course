package ru.itmo.web.lesson4.model;

import java.util.List;

public class User {
    private final Long id;
    private final String handle;
    private final String name;
    private final List<Post> posts;
    private Color color;

    public User(long id, String handle, String name, List<Post> posts, Color color) {
        this.id = id;
        this.handle = handle;
        this.name = name;
        this.posts = posts;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }

    public String getName() {
        return name;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public Color getColor() {
        return color;
    }

    public enum Color {
        RED, GREEN, BLUE;
    }
}
