package com.warba.assessment.builder;

import com.warba.assessment.entity.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Users {

    public static User.UserBuilder userBuilder() {
        return User.builder()
                .id(ThreadLocalRandom.current().nextLong(1, 10000))  // Random long id
                .name(RandomStringUtils.secure().next(20))
                .civilId(UUID.randomUUID().toString())
                .expiryDate(LocalDate.now().plusYears(1));
    }

    public static User.UserBuilder expiredUserBuilder() {
        return User.builder()
                .id(ThreadLocalRandom.current().nextLong(1, 10000))  // Random long id
                .name(RandomStringUtils.secure().next(20))
                .civilId(UUID.randomUUID().toString())
                .expiryDate(LocalDate.now().minusDays(1));
    }

    public static User.UserBuilder userBuilder(Long id) {
        return User.builder()
                .id(id)  // Random long id
                .name(RandomStringUtils.secure().next(20))
                .civilId(UUID.randomUUID().toString())
                .expiryDate(LocalDate.now().plusYears(1));
    }

}
