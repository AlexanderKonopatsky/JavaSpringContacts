package com.contacts.java_personal_contacts.contacts.service;

import com.contacts.java_personal_contacts.contacts.models.Role;
import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        sendMessage(user);

        userRepository.save(user);
        log.info("user with username: {} successfully added", user.getUsername());
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
        log.info("send activate message to user: {} successfully sent", user.getUsername());
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        }
        user.setActive(true);
        user.setActivationCode(null);

        userRepository.save(user);
        log.info("the user {} followed the activation link: user {} activated", user.getUsername(), user.getUsername());
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
                user.setPassword(passwordEncoder.encode(password));
            }

            userRepository.save(user);

            if (isEmailChanged){
                //sendMessage(user);
            }
        }
    }

    public void sendMessageToUser( String to, String subject, String message) {
            mailSender.send(to, subject, message);
            log.info("user sent a message to {}", to);
    }
}
