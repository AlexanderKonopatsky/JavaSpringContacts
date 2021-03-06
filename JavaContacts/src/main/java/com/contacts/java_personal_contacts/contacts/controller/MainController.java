package com.contacts.java_personal_contacts.contacts.controller;

import com.contacts.java_personal_contacts.contacts.forms.ContactForm;
import com.contacts.java_personal_contacts.contacts.models.Contact;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.ContactRepository;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import com.contacts.java_personal_contacts.contacts.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Controller
public class MainController {
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public ModelAndView greeting(Model model, @AuthenticationPrincipal User user){
        ModelAndView modelAndView = new ModelAndView("greeting");
        if (user!= null) {
            model.addAttribute("user", user.getUsername());
        }
        else {
            model.addAttribute("user", "guest");
        }
        return modelAndView;
    }

    @GetMapping("/main")
    public String main(
            @RequestParam(required = false, defaultValue = "") String tag,
            @RequestParam(required = false, defaultValue = "") String name,
            @AuthenticationPrincipal User user,
            Model model,
            //@RequestParam(value = "size", required = false, defaultValue = "0") Integer size,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
            ) {

        Page<Contact> contacts = contactRepository.findByAuthor(user, PageRequest.of(page, 3));;

        if (tag != null && !tag.isEmpty()) {
            contacts = contactRepository.findByTagAndAuthor(tag, user, PageRequest.of(page, 3));
        }
        if (name != null && !name.isEmpty()) {
            contacts = contactRepository.findByNameAndAuthor(name, user, PageRequest.of(page, 3));
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
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam("file") MultipartFile file,
            Model model,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) throws IOException {
        Contact contact = new Contact(description, tag, user, name, email, phone);

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
            log.info("Image uploaded successfully");
        }

        contactRepository.save(contact);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        Page<Contact> contacts = contactRepository.findByAuthor(user, PageRequest.of(page, 3));
        model.addAttribute("numbers", IntStream.range(0, contacts.getTotalPages()).toArray());
        model.addAttribute("contacts", contacts);
        log.info("post request: /addContact");
        return modelAndView;
    }

    @GetMapping("sendMessage")
    public ModelAndView showSendMessageForm(
            Model model,
            @RequestParam(required = false, defaultValue = "") String email
    ) {
        if (email != null && !email.isEmpty()) {
            model.addAttribute("email", email);
        }
        ModelAndView modelAndView = new ModelAndView("sendMessage");
        return modelAndView;
    }

    @PostMapping(value = "/sendMessage")
    public ModelAndView sendNewMessage(
            @Valid Contact vcontact,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user,
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message,
            Model model
    ) throws IOException {

        if (to != null && !to.isEmpty() && subject != null && !subject.isEmpty() && message !=null  && !message.isEmpty()) {
            userService.sendMessageToUser(to, subject, message);
            model.addAttribute("result", "Success!");
            log.info("sending a message to {} success", to);
        }
        else {
            model.addAttribute("result", "Fail!");
            log.info("sending a message to {} fail", to);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sendMessage");
        log.info("post request: /sendMessage");
        return modelAndView;
    }

    @GetMapping("/changeContact")
    public String userMessges(
            Model model,
            @RequestParam(required = false) Integer id
    ) {
        Contact contact = contactRepository.findById(id);
        model.addAttribute("id", id);
        model.addAttribute("contact", contact);

        return "changeContact";
    }

    @PostMapping("/changeContact")
    public ModelAndView updateMessage(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Integer id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String email,
            @RequestParam String tag,
            @RequestParam String phone,
            Model model,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) throws IOException {
        Contact contact = contactRepository.findById(id);

        if (contact.getAuthorName().equals(currentUser.getUsername())) {
            if (!StringUtils.isEmpty(name)) {
                contact.setName(name);
            }

            if (!StringUtils.isEmpty(description)) {
                contact.setDescription(description);
            }

            if (!StringUtils.isEmpty(email)) {
                contact.setEmail(email);
            }

            if (!StringUtils.isEmpty(tag)) {
                contact.setTag(tag);
            }

            if (!StringUtils.isEmpty(phone)) {
                contact.setPhone(phone);
            }

            if (file != null && !file.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);

                if(uploadDir.exists()){
                    uploadDir.mkdir();
                }

                String uuidFile = UUID.randomUUID().toString();
                String resultFileName = uuidFile + "." + file.getOriginalFilename();

                file.transferTo(new File(uploadPath + "/" + resultFileName));

                contact.setFilename(resultFileName);
                log.info("Image uploaded successfully!");
            }

            contactRepository.save(contact);
            log.info("contact with name {} is edited", contact.getAuthorName());
        }
        else {

        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");

        Page<Contact> contacts ;
        contacts = contactRepository.findByAuthor(currentUser, PageRequest.of(page, 3));
        model.addAttribute("contacts", contacts);
        model.addAttribute("numbers", IntStream.range(0, contacts.getTotalPages()).toArray());

        return modelAndView;
    }

    @PostMapping("/contact/{id}/remove")
    public String deleteById(
            Model model,
            @AuthenticationPrincipal User currentUser,
            @PathVariable(value = "id") Integer id,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        Contact contact = contactRepository.findById(id);
        if (contact.getAuthorName().equals(currentUser.getUsername())) {
            contactRepository.delete(contact);
            log.info("contact with name {} is deleted", contact.getAuthorName());
        }

        Page<Contact> contacts ;
        contacts = contactRepository.findByAuthor(currentUser, PageRequest.of(page, 3));
        model.addAttribute("contacts", contacts);
        model.addAttribute("numbers", IntStream.range(0, contacts.getTotalPages()).toArray());

        return "main";
    }
}
