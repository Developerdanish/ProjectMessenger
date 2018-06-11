package com.example.danish.projectmessenger;

public class Users {

    private String displayName;
    private String image;
    private String online;
    private String status;
    private String thumbnail;
    private Long time;


    public Users() {

    }

    public Users(String displayName, String image, String online, String status, String thumbnail , Long time) {
        this.displayName = displayName;
        this.image = image;
        this.online = online;
        this.status = status;
        this.thumbnail = thumbnail;
        this.time = time;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
