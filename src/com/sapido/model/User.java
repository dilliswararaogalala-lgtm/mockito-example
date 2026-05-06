package com.sapido.model;

public class User {
    private String id;
    private String name;
    private String email;
    private double rating;

    public User(String id, String name, String email, double rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
