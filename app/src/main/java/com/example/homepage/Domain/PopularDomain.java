package com.example.homepage.Domain;

import java.io.Serializable;

public class PopularDomain implements Serializable {
    private String id;
    private String title;
    private String description;
    private String picUrl;
    private int review;
    private int score;
    private int NumberinCart;
    private double price;
    private String documentId;
    // Constructor for Admin (with ID)
    public PopularDomain(String id, String title, String description, String picUrl, int review, int score, double price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.review = review;
        this.score = score;
        this.price = price;
    }
    public PopularDomain() {
    }

    // Constructor for normal use (without ID)
    public PopularDomain(String title, String description, String picUrl, int review, int score, double price) {
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.review = review;
        this.score = score;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumberinCart() {
        return NumberinCart;
    }

    public void setNumberinCart(int NumberinCart) {
        this.NumberinCart = NumberinCart;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
}
