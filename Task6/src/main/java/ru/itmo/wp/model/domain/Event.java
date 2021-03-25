package ru.itmo.wp.model.domain;

import java.util.Date;

public class Event {
    public long id;
    public long userId;
    public Type type;
    Date creationTime;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public Type getType() {
        return type;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public static Event newEnterEvent(User user) {
        Event event = new Event();
        event.setUserId(user.getId());
        event.setType(Type.ENTER);
        return event;
    }

    public static Event newLogoutEvent(User user) {
        Event event = new Event();
        event.setUserId(user.getId());
        event.setType(Type.LOGOUT);
        return event;
    }

    public enum Type {
        ENTER, LOGOUT;
    }
}


