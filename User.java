package com.example.mathsprout;

public class User {

    public String name;
    public String email;
    public String role;
    public String uid;
    public long createdAt;
    public String phone;
    public String address;

    // Default constructor required for Firebase
    public User() {}

    // Constructor used in SignupActivity
    public User(String name, String email, String role, String uid, long createdAt) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.uid = uid;
        this.createdAt = createdAt;
        // Initializing optional fields to prevent null entries in database
        this.phone = "";
        this.address = "";
    }

    // Optional extended constructor
    public User(String name, String email, String role, String uid, long createdAt, String phone, String address) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.uid = uid;
        this.createdAt = createdAt;
        this.phone = phone;
        this.address = address;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}