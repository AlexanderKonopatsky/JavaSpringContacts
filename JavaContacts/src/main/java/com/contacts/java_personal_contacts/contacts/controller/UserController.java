package com.contacts.java_personal_contacts.contacts.controller;

import com.contacts.java_personal_contacts.contacts.models.Role;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String UserList(Model model){
        model.addAttribute("users", userRepository.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    public ModelAndView userEditForm(@PathVariable User user, Model model){
        ModelAndView modelAndView = new ModelAndView("userEdit");
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return modelAndView;
    }

    @PostMapping
    public RedirectView userSave(@RequestParam String username, @RequestParam("idUser") User user){
        user.setUsername(username);
        userRepository.save(user);
        return new RedirectView("/user");
    }
}
