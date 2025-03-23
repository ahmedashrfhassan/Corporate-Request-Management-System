package com.warba.assessment.builder;

import com.warba.assessment.dto.response.UserDto;
import com.warba.assessment.entity.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class UserDtos {

    public static UserDto.UserDtoBuilder userBuilder() {
        return UserDto.builder()
                .id(ThreadLocalRandom.current().nextLong(1, 10000))  // Random long id
                .name(RandomStringUtils.secure().next(20))
                .civilId(UUID.randomUUID().toString())
                .expiryDate(LocalDate.now().plusYears(1));
    }

    public static UserDto.UserDtoBuilder userBuilder(Long id) {
        return UserDto.builder()
                .id(id)  // Random long id
                .name(RandomStringUtils.secure().next(20))
                .civilId(UUID.randomUUID().toString())
                .expiryDate(LocalDate.now().plusYears(1));
    }

}
