package com.contacts.java_personal_contacts.contacts.forms;

public class ContactForm {
    private String text;
    private String tag;
    private String file;

    public ContactForm() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ContactForm(String text, String tag) {
        this.text = text;
        this.tag = tag;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
