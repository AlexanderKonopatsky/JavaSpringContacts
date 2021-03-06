package com.contacts.java_personal_contacts.contacts.models;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @NotBlank(message = "Please fill the name")
    private String name;
    @Length(max = 2048, message = "Description too long")
    private String description;
    @Email(message = "Email should be valid")
    private String email;
    private String tag;
    private String filename;
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    public Contact(){
    }

    public Contact(String description, String tag, User author, String name, String email, String phone) {
        this.description = description;
        this.tag = tag;
        this.author = author;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getAuthorName() {
        return author != null ? author.getUsername() : "<none>";
    }

    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
