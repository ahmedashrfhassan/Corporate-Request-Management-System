package com.warba.assessment;

import com.warba.assessment.mapper.UserMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = "com.warba.assessment",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = UserMapper.class))public class UserManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
    }

}
