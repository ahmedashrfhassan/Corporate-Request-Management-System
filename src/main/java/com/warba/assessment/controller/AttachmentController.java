package com.warba.assessment.controller;

import com.warba.assessment.entity.Attachment;
import com.warba.assessment.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;


    @PostMapping("/upload")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        Long attachmentId = attachmentService.saveAttachment(file, type);
        return new ResponseEntity<>(attachmentId, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attachment> getAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(attachmentService.getAttachment(id));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        String fileName = attachmentService.getAttachment(id).getFileName();
        byte[] resource = attachmentService.loadFileAsResource(fileName);

        // Try to determine content type
        String contentType = "application/octet-stream";
        if (fileName.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            contentType = "image/png";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}