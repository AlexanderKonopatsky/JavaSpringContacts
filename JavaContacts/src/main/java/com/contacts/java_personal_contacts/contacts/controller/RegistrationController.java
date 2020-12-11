package com.contacts.java_personal_contacts.contacts.controller;

import com.contacts.java_personal_contacts.contacts.forms.UserForm;
import com.contacts.java_personal_contacts.contacts.models.Role;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import com.contacts.java_personal_contacts.contacts.service.UserService;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public ModelAndView showRegistrationPage(Model model) {
        ModelAndView modelAndView = new ModelAndView("registration");
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return modelAndView;
    }

    @PostMapping("/registration")
    public ModelAndView AddNewUser(
            @Valid User user,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes atts,
            @ModelAttribute("userForm") UserForm userForm) {
        ModelAndView modelAndView = new ModelAndView();

        String userName = userForm.getUsername();
        String password = userForm.getPassword();
        String email = userForm.getEmail();

        User currUser = new User();
        currUser.setUsername(userName);
        currUser.setPassword(password);
        currUser.setEmail(email);

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            List<String> message = new ArrayList<>();
            for (FieldError e : errors){
                message.add(e.getDefaultMessage());
            }
            model.addAttribute("errValidate", message);
            modelAndView.setViewName("registration");
            return modelAndView;
        }

        if(!userService.addUser(currUser)){
            model.addAttribute("errorMessage", "User exists!");
            modelAndView.setViewName("registration");
            return modelAndView;
        }
        model.addAttribute("message", "A confirmation letter has been sent to your mail!");
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);
        if(isActivated) {
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("message", "Activation code is not found");
        }

        return "login";
    }
}