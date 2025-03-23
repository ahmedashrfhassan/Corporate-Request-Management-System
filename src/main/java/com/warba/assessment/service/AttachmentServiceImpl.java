package com.warba.assessment.service;

import com.warba.assessment.entity.Attachment;
import com.warba.assessment.exception.BusinessValidationException;
import com.warba.assessment.exception.FileStorageException;
import com.warba.assessment.repository.AttachmentRepository;
import com.warba.assessment.repository.AttachmentTypeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    @Value("${file.storage.location:uploads}")
    private String fileStorageLocation;

    private Path fileStoragePath;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentTypeRepository attachmentTypeRepository;

    @PostConstruct
    public void init() {
        this.fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStoragePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }

    }

    @Override
    @Transactional
    public Long saveAttachment(MultipartFile file, String type) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + fileName);
            }
            String uniqueFileName = UUID.randomUUID() + "_" + fileName;
            saveToFileSystem(file, uniqueFileName);
            Attachment attachment = getAttachment(file, type, uniqueFileName);
            return attachmentRepository.save(attachment).getId();

        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("attachment not found")
        );
    }

    private Attachment getAttachment(MultipartFile file, String type, String uniqueFileName) {
        Attachment attachment = new Attachment();
        attachment.setFileName(uniqueFileName);
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = extractContentType(file);
        }
        attachment.setFileType(contentType);
        var attType = attachmentTypeRepository.findByName(type).orElseThrow(
                () -> new BusinessValidationException("incorrect attachment type")
        );
        attachment.setAttachmentType(attType);
        return attachment;
    }

    private void saveToFileSystem(MultipartFile file, String fileName) throws IOException {
        Path targetLocation = this.fileStoragePath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void deleteAttachment(Long id) {
        Optional<Attachment> attachmentOpt = attachmentRepository.findById(id);

        if (attachmentOpt.isPresent()) {
            Attachment attachment = attachmentOpt.get();
            String fileName = attachment.getFileName();

            try {
                Path filePath = this.fileStoragePath.resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
                attachmentRepository.deleteById(id);
            } catch (Exception ex) {
                throw new FileStorageException("Failed to delete attachment: " + fileName, ex);
            }
        }
    }

    @Override
    public byte[] loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStoragePath.resolve(fileName).normalize();
            if (!Files.exists(filePath)) {
                throw new FileStorageException("File not found: " + fileName);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not load file: " + fileName, ex);
        }
    }

    private String extractContentType(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            return fileName != null ? Files.probeContentType(Paths.get(fileName)) : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream"; // Default when MIME type cannot be determined
        }
    }
}