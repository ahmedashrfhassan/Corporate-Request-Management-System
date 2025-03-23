package com.warba.assessment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestDto {
    @NotBlank(message = "Request name is required")
    private String requestName;
    
    @NotNull(message = "Status ID is required")
    private Long statusId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @Size(min = 2, message = "At least 2 attachments are required")
    private List<Long> attachmentIds;
}