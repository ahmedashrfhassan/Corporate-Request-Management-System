package com.warba.assessment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warba.assessment.base.ApiResponse;
import com.warba.assessment.dto.request.CreateUserDto;
import com.warba.assessment.dto.response.UserDto;
import com.warba.assessment.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.warba.assessment.builder.CreateUserDtos.createUserDtoBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateAndGetUser() throws Exception {
        CreateUserDto userDTO = createUserDtoBuilder().build();

        String createdUserIdJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ApiResponse<Long> apiResponse = objectMapper.readValue(createdUserIdJson,
                new TypeReference<>() {
                });
        Long createdUserId = apiResponse.getPayload();

        String userDtoAsString = mockMvc.perform(get("/api/users/" + createdUserId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<UserDto> userDto = objectMapper.readValue(userDtoAsString,
                new TypeReference<>() {
                });
        UserDto user = userDto.getPayload();
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertNotNull(user.getName());
        Assertions.assertNotNull(user.getCivilId());
        Assertions.assertNotNull(user.getExpiryDate());
        Assertions.assertEquals(createdUserId, user.getId());
        Assertions.assertEquals(userDTO.getName(), user.getName());
        Assertions.assertEquals(userDTO.getCivilId(), user.getCivilId());
        Assertions.assertEquals(userDTO.getExpiryDate(), user.getExpiryDate());
    }

}