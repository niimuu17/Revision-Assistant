package com.example.revision_assistant;

/**
 * Represents a registered user in Study Buddy.
 */
public class User {
    private final int id;
    private final String username;
    private final String email;

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
