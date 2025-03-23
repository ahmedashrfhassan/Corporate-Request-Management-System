package com.warba.assessment.service;

import com.warba.assessment.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Long saveAttachment(MultipartFile file, String type);
    Attachment getAttachment(Long id);
    byte[] loadFileAsResource(String fileName);
}