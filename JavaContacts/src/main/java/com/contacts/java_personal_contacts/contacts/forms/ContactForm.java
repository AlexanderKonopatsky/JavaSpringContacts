package com.contacts.java_personal_contacts.contacts.forms;

public class ContactForm {
    private String description;
    private String tag;
    private String file;
    private String name;

    public ContactForm() {
    }

    public ContactForm(String description, String tag, String name) {
        this.description = description;
        this.tag = tag;
        this.name = name;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
