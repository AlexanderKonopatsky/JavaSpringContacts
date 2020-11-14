package com.contacts.java_personal_contacts.contacts.controller;

import com.contacts.java_personal_contacts.contacts.forms.ContactForm;
import com.contacts.java_personal_contacts.contacts.models.Contact;
import com.contacts.java_personal_contacts.contacts.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/")
    public ModelAndView greeting(Model model){
        ModelAndView modelAndView = new ModelAndView("greeting");
        return modelAndView;
    }

    @GetMapping("main")
    public ModelAndView getAllContact(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        model.addAttribute("contacts", contactRepository.findAll());
        return modelAndView;
    }

    @GetMapping("addContact")
    public ModelAndView showAddContact(Model model) {
        ModelAndView modelAndView = new ModelAndView("addContact");
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return modelAndView;
    }


    @PostMapping(value = "/addContact")
    public ModelAndView addNewContact(@RequestParam String text, @RequestParam String tag, Model model) {
        Contact contact = new Contact(text, tag);
        contactRepository.save(contact);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");

        model.addAttribute("contacts", contactRepository.findAll());
        return modelAndView;
    }



}
