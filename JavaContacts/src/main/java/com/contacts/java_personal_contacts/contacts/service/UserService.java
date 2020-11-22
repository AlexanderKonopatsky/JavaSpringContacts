package com.contacts.java_personal_contacts.contacts.service;

import antlr.StringUtils;
import com.contacts.java_personal_contacts.contacts.forms.UserForm;
import com.contacts.java_personal_contacts.contacts.models.Role;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public boolean addUser(User user){
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());



        sendMessage(user);

        userRepository.save(user);
        return true;
    }

    private void sendMessage(User user) {
        if( !org.apache.commons.lang3.StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
              "Hello, %s! \n" +
                      " Welcome to SpringContacts. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(), user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        }
        user.setActive(true);
        user.setActivationCode(null);

        userRepository.save(user);

        return true;

    }

    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();

        boolean isEmailChanged = ((email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email)));
        if (isEmailChanged) {
            user.setEmail(email);

            if (!org.apache.commons.lang3.StringUtils.isEmpty(email)){
                user.setActivationCode(UUID.randomUUID().toString());
            }
            if (!org.apache.commons.lang3.StringUtils.isEmpty(password)){
                user.setPassword(password);
            }

            userRepository.save(user);

            if (isEmailChanged){
                sendMessage(user);
            }

        }
    }
}
