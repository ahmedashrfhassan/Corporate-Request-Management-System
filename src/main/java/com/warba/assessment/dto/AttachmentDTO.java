package com.warba.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private Long id;
    
    @NotBlank(message = "File name is required")
    private String fileName;
    
    private String fileType;
    
    private String downloadUrl;
    
    @NotNull(message = "Attachment type ID is required")
    private Long attachmentTypeId;
}