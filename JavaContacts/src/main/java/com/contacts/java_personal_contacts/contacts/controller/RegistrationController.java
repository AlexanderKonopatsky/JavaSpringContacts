package com.contacts.java_personal_contacts.contacts.controller;

import com.contacts.java_personal_contacts.contacts.forms.UserForm;
import com.contacts.java_personal_contacts.contacts.models.Role;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registration")
    public ModelAndView showRegistrationPage(Model model) {
        ModelAndView modelAndView = new ModelAndView("registration");
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return modelAndView;
    }

    @PostMapping("/registration")
    public ModelAndView AddNewUser(Model model, @ModelAttribute("userForm") UserForm userForm) {
        ModelAndView modelAndView = new ModelAndView();

        String userName = userForm.getUsername();
        String password = userForm.getPassword();

        User userFromDB = userRepository.findByUsername(userName);
        if(userFromDB != null){
            model.addAttribute("errorMessage", "ERROR USER IS ALREADY EXIST");
            modelAndView.setViewName("registration");
            return modelAndView;
        }
        modelAndView.setViewName("login");
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);

        user.setRoles(Collections.singleton(Role.USER));
        model.addAttribute("errorMessage", "ERROR USER IS ALREADY EXIST");
        userRepository.save(user);
        return modelAndView;
    }
}