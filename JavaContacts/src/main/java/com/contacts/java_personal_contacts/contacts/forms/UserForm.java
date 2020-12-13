package com.contacts.java_personal_contacts.contacts.forms;

public class UserForm {

    private String username;

    private String password;

    private boolean active;

    private String email;

    public UserForm() {
    }

    public UserForm(String username, String password, boolean active, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}