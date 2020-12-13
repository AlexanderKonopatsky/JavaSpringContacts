package com.contacts.java_personal_contacts.contacts.service;

import com.contacts.java_personal_contacts.contacts.models.User;
import com.contacts.java_personal_contacts.contacts.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userSevice;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void addUser() {
        User user = new User();

        boolean isUserCreated = userSevice.addUser(user);

        Assert.assertTrue(isUserCreated);
        Assert.assertNotNull(user.getActivationCode());
    }

    @Test
    public void addUserFailTest() {
        User user = new User();

        user.setUsername("John");

        Mockito.doReturn(new User())
                .when(userRepository)
                .findByUsername("John");

        boolean isUserCreated = userSevice.addUser(user);

        Assert.assertFalse(isUserCreated);
    }

    @Test
    public void activateUser() {
        User user = new User();

        user.setActivationCode("code");

        Mockito.doReturn(user)
                .when(userRepository)
                .findByActivationCode("activate");

        boolean isUserActivated = userSevice.activateUser("activate");

        Assert.assertTrue(isUserActivated);
        Assert.assertNull(user.getActivationCode());
    }

    @Test
    public void activateUserFailTest() {
        boolean isUserActivated = userSevice.activateUser("activate me");

        Assert.assertFalse(isUserActivated);
    }
}