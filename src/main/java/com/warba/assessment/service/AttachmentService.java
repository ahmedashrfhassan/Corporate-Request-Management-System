package com.warba.assessment.service;

import com.warba.assessment.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface AttachmentService {
    Long saveAttachment(MultipartFile file, String type);
    Attachment getAttachment(Long id);
    void deleteAttachment(Long id);
    byte[] loadFileAsResource(String fileName);
}