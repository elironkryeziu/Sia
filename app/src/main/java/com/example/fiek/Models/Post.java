package com.example.fiek.Models;

import com.google.firebase.database.ServerValue;

public class Post {
    private String postKey;
    private String title;
    private String description;
    private String userId;
    private String userName;
    private Object timeStamp;

    public Post(String title, String description, String userId, String userName){
        this.title=title;
        this.description=description;
        this.userId=userId;
        this.userName=userName;
        this.timeStamp= ServerValue.TIMESTAMP;
    }

    public Post() {
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
