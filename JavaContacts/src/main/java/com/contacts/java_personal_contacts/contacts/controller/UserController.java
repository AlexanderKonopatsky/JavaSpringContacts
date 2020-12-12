package com.contacts.java_personal_contacts.contacts.controller;

import com.contacts.java_personal_contacts.contacts.models.Role;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import com.contacts.java_personal_contacts.contacts.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String UserList(Model model){
        model.addAttribute("users", userRepository.findAll());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public ModelAndView userEditForm(@PathVariable User user, Model model){
        ModelAndView modelAndView = new ModelAndView("userEdit");
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return modelAndView;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public RedirectView userSave(@RequestParam String username, @RequestParam String email, @RequestParam("idUser") User user){
        log.info("username {}", username);
        log.info("user {}", user);
        log.info("email {}", user.getEmail());
        user.setUsername(username);
        user.setEmail(email);
        userRepository.save(user);
        log.info("admin changed username or email", user.getUsername());
        return new RedirectView("/user");
    }

    @GetMapping("profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());

        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email
            )
    {
        userService.updateProfile(user, password, email);
        log.info("user ({}) profile changed email ({})", user, email);
        return "redirect:/user/profile";
    }
}
