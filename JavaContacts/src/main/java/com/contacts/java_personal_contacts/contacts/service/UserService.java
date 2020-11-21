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

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username);
//        user.getUsername();
//        user.getPassword();
//        if (user == null){
//            throw new UsernameNotFoundException(username);
//        }
//        return userRepository.findByUsername(username);
//    }
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


        if( !org.apache.commons.lang3.StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
              "Hello, %s! \n" +
                      " Welcome to SpringContacts. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(), user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
        }

        userRepository.save(user);
        return true;
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
}
