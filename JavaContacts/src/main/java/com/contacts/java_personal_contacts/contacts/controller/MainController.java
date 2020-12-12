package com.contacts.java_personal_contacts.contacts.controller;

import com.contacts.java_personal_contacts.contacts.forms.ContactForm;
import com.contacts.java_personal_contacts.contacts.models.Contact;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.ContactRepository;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.Message;
import javax.validation.Valid;
import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.IntStream;

@Controller
public class MainController {
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;


    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public ModelAndView greeting(Model model){
        ModelAndView modelAndView = new ModelAndView("greeting");
        return modelAndView;
    }


    @GetMapping("/main")
    public String main(
            @RequestParam(required = false, defaultValue = "") String tag,
            @AuthenticationPrincipal User user,
            Model model,
            //@RequestParam(value = "size", required = false, defaultValue = "0") Integer size,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
            ) {
        Page<Contact> contacts ;


        if (tag != null && !tag.isEmpty()) {
            contacts = contactRepository.findByTag(tag, PageRequest.of(page, 3));
        } else {
            contacts = contactRepository.findByAuthor(user, PageRequest.of(page, 3));
        }

        model.addAttribute("contacts", contacts);
        model.addAttribute("numbers", IntStream.range(0, contacts.getTotalPages()).toArray());
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
    public ModelAndView addNewContact(
            @Valid Contact vcontact,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user,
            @RequestParam String name,
            @RequestParam String tag,
            @RequestParam String description,
            @RequestParam("file") MultipartFile file,
            Model model,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) throws IOException {
        Contact contact = new Contact(description, tag, user, name);

//        if (bindingResult.hasErrors()) {
//            List<FieldError> errors = bindingResult.getFieldErrors();
//            List<String> message = new ArrayList<>();
//            for (FieldError e : errors){
//                message.add(e.getDefaultMessage());
//            }
//            model.addAttribute("errValidate", message);
//            modelAndView.setViewName("registration");
//            return modelAndView;
//        }

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if(uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            contact.setFilename(resultFileName);
        }

        contactRepository.save(contact);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        Page<Contact> contacts = contactRepository.findByAuthor(user, PageRequest.of(page, 3));
        model.addAttribute("numbers", IntStream.range(0, contacts.getTotalPages()).toArray());
        model.addAttribute("contacts", contacts);
        return modelAndView;
    }
}
