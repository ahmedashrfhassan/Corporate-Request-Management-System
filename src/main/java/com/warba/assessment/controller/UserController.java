package com.warba.assessment.controller;

import com.warba.assessment.base.ApiResponse;
import com.warba.assessment.dto.request.CreateUserDto;
import com.warba.assessment.dto.request.UpdateUserDto;
import com.warba.assessment.dto.response.UserDto;
import com.warba.assessment.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import static com.warba.assessment.constants.ResponseMessages.VALIDATION_FAILED;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createUser(@RequestBody @Valid CreateUserDto dto,
                                                       @Parameter(hidden = true) Errors errors) {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(VALIDATION_FAILED, errors));
        }

        Long userId = userService.createUser(dto);

        return ResponseEntity.status(CREATED).body(ApiResponse.created(userId, "User created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable("id") Long id) {
        UserDto dto = userService.getUserById(id);
        var response = ApiResponse.ok(dto, "User fetched successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> updateUser(@PathVariable("id") Long id,
                                                           @RequestBody @Valid UpdateUserDto dto,
                                                           @Parameter(hidden = true) Errors errors) {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(VALIDATION_FAILED, errors));
        }
        Boolean updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(ApiResponse.ok(updated, "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
