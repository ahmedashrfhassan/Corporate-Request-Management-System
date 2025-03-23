package com.warba.assessment.controller;

import com.warba.assessment.base.ApiResponse;
import com.warba.assessment.dto.request.CreateRequestDto;
import com.warba.assessment.dto.response.RequestDto;
import com.warba.assessment.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createRequest(@RequestBody @Valid CreateRequestDto requestDTO) {
        Long requestId = requestService.createRequest(requestDTO);
        return ResponseEntity.status(CREATED)
                .body(ApiResponse.created(requestId, "Request created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RequestDto>> getRequest(@PathVariable Long id) {
        RequestDto request = requestService.getRequest(id);
        return ResponseEntity.ok(ApiResponse.ok(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RequestDto>>> getRequestsByUser(@PathVariable Long userId) {
        List<RequestDto> requests = requestService.getRequestsByUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(requests));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
