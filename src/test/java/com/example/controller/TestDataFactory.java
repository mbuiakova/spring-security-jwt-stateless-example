package com.example.controller;

import com.example.domain.UserDetailAuth;
import com.example.model.Role;
import com.example.model.User;
import liquibase.repackaged.org.apache.commons.lang3.RandomUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

import static liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class TestDataFactory {

    public static User getUser() {
        User user = new User();
        user.setId(RandomUtils.nextInt());
        user.setName(randomAlphabetic(8));
        user.setSurname(randomAlphabetic(8));
        user.setEmail("bb@email.com");
        user.setPassword("$2a$10$usUFLZMTOUPk/Yk2jXZFBeDG.BL.TAXQvH.2n31EcgoyzKD1Apx0S");
        user.setEnabled(true);
        user.setRoles(Set.of(new Role(1, "USER")));
        return user;
    }

    public static UserDetailAuth getUserDetails() {
        UserDetailAuth userDetails = new UserDetailAuth(getUser());
        userDetails.setEnabled(true);
        return userDetails;
    }

}
