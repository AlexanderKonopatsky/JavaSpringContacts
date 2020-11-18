package com.contacts.java_personal_contacts.contacts.controller;

import com.contacts.java_personal_contacts.contacts.forms.ContactForm;
import com.contacts.java_personal_contacts.contacts.models.Contact;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.ContactRepository;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
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
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public ModelAndView greeting(Model model){
        ModelAndView modelAndView = new ModelAndView("greeting");
        return modelAndView;
    }

//    @GetMapping("main")
//    public ModelAndView getAllContact(Model model) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("main");
//        model.addAttribute("contacts", contactRepository.findAll());
//        return modelAndView;
//    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String tag, Model model) {
        Iterable<Contact> contacts = contactRepository.findAll();

        if (tag != null && !tag.isEmpty()) {
            contacts = contactRepository.findByTag(tag);
        } else {
            contacts = contactRepository.findAll();
        }

        model.addAttribute("contacts", contacts);
        model.addAttribute("filter", tag);

        return "main";
    }

    @GetMapping("addContact")
    public ModelAndView showAddContact(Model model) {
        ModelAndView modelAndView = new ModelAndView("addContact");
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return modelAndView;
    }


    @PostMapping(value = "/addContact")
    public ModelAndView addNewContact(Principal user, @RequestParam String text, @RequestParam String tag, Model model) {
        String currentPrincipalName = user.getName();
        User userFromDB = userRepository.findByUsername(currentPrincipalName);
        Contact contact = new Contact(text, tag, userFromDB);
        contactRepository.save(contact);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");

        model.addAttribute("contacts", contactRepository.findAll());
        return modelAndView;
    }


//    @PostMapping("filter")
//    public ModelAndView filter(@RequestParam String tag, Model model) {
//        Iterable<Contact> contacts;
//        if (tag != null && !tag.isEmpty()) {
//            contacts = contactRepository.findByTag(tag);
//        } else {
//            contacts = contactRepository.findAll();
//        }
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("main");
//        model.addAttribute("contacts", contacts);
//        return modelAndView;
//    }

}
