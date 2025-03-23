package com.warba.assessment.builder;

import com.warba.assessment.dto.request.CreateUserDto;
import com.warba.assessment.entity.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.UUID;

public class CreateUserDtos {

    public static CreateUserDto.CreateUserDtoBuilder createUserDtoBuilder() {
        return CreateUserDto.builder()
                .name(RandomStringUtils.secure().next(10))
                .civilId(RandomStringUtils.secure().nextNumeric(7))
                .expiryDate(LocalDate.now().plusYears(1));
    }

    public static CreateUserDto.CreateUserDtoBuilder expiredCreateUserDtoBuilder() {
        return CreateUserDto.builder()
                .name(RandomStringUtils.secure().next(10))
                .civilId(RandomStringUtils.secure().nextNumeric(7))
                .expiryDate(LocalDate.now().minusDays(1));
    }

}
