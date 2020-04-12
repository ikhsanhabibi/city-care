package com.example.citycare;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Complaint {

    private String title;
    private String description;
    private @ServerTimestamp Date timestamp;
    private String name;
    private String email;
    private String status;
    private String category;

    public Complaint(String title, String description, Date timestamp, String name, String email, String status, String category) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.name = name;
        this.email = email;
        this.status = status;
        this.category = category;
    }

    public Complaint() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
