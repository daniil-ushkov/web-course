package ru.itmo.wp.model.domain;

import java.util.Date;

public class Talk {
    private long id;
    private long sourceUserId;
    private long targetUserId;
    private String text;
    private Date creationTime;

    public Talk(long id, long sourceUserId, long targetUserId, String text, Date creationTime) {
        this.id = id;
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.text = text;
        this.creationTime = creationTime;
    }

    public Talk() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(long sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
