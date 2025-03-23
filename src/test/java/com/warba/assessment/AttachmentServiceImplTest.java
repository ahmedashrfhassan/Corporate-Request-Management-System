package com.warba.assessment;

import com.warba.assessment.entity.Attachment;
import com.warba.assessment.entity.AttachmentType;
import com.warba.assessment.exception.BusinessValidationException;
import com.warba.assessment.exception.FileStorageException;
import com.warba.assessment.exception.ResourceNotFoundException;
import com.warba.assessment.repository.AttachmentRepository;
import com.warba.assessment.repository.AttachmentTypeRepository;
import com.warba.assessment.service.AttachmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceImplTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private AttachmentTypeRepository attachmentTypeRepository;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private Path tempDir;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        
        // Create temporary directory for file storage
        tempDir = Files.createTempDirectory("test-uploads");
        ReflectionTestUtils.setField(attachmentService, "fileStorageLocation", tempDir.toString());
        
        // Initialize the service manually
        attachmentService.init();
    }

    @Test
    public void testInit_CreatesDirectory() {
        // Verify directory is created during initialization
        assertTrue(Files.exists(tempDir));
    }

    @Test
    public void testSaveAttachment_Success() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.txt",
                "text/plain", 
                "Hello, World!".getBytes()
        );
        
        AttachmentType attachmentType = new AttachmentType();
        attachmentType.setId(1L);
        attachmentType.setName("document");
        
        Attachment savedAttachment = new Attachment();
        savedAttachment.setId(1L);
        savedAttachment.setFileName("uuid_test.txt");
        savedAttachment.setFileType("text/plain");
        savedAttachment.setAttachmentType(attachmentType);
        
        when(attachmentTypeRepository.findByName("document")).thenReturn(Optional.of(attachmentType));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(savedAttachment);
        
        // Act
        Long attachmentId = attachmentService.saveAttachment(file, "document");
        
        // Assert
        assertEquals(1L, attachmentId);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
        verify(attachmentTypeRepository, times(1)).findByName("document");
    }

    @Test
    public void testSaveAttachment_InvalidFileName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "../malicious.txt",
                "text/plain", 
                "Malicious content".getBytes()
        );
        
        // Act & Assert
        FileStorageException exception = assertThrows(
                FileStorageException.class,
                () -> attachmentService.saveAttachment(file, "document")
        );
        
        assertTrue(exception.getMessage().contains("invalid path sequence"));
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    public void testSaveAttachment_InvalidAttachmentType() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.txt",
                "text/plain", 
                "Hello, World!".getBytes()
        );
        
        when(attachmentTypeRepository.findByName("invalid-type")).thenReturn(Optional.empty());
        
        // Act & Assert
        BusinessValidationException exception = assertThrows(
                BusinessValidationException.class,
                () -> attachmentService.saveAttachment(file, "invalid-type")
        );
        
        assertEquals("incorrect attachment type", exception.getMessage());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    public void testSaveAttachment_IOExceptionDuringFileSave() throws IOException {
        // Arrange
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getContentType()).thenReturn("text/plain");
        
        AttachmentType attachmentType = new AttachmentType();
        attachmentType.setId(1L);
        attachmentType.setName("document");
        
        when(attachmentTypeRepository.findByName("document")).thenReturn(Optional.of(attachmentType));
        when(file.getInputStream()).thenThrow(new IOException("Simulated IO error"));
        
        // Act & Assert
        FileStorageException exception = assertThrows(
                FileStorageException.class,
                () -> attachmentService.saveAttachment(file, "document")
        );
        
        assertTrue(exception.getMessage().contains("Could not store file"));
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    public void testSaveAttachment_NullContentType() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.txt",
                null, 
                "Hello, World!".getBytes()
        );
        
        AttachmentType attachmentType = new AttachmentType();
        attachmentType.setId(1L);
        attachmentType.setName("document");
        
        Attachment savedAttachment = new Attachment();
        savedAttachment.setId(1L);
        savedAttachment.setFileType("text/plain");
        
        when(attachmentTypeRepository.findByName("document")).thenReturn(Optional.of(attachmentType));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(savedAttachment);
        
        // Act
        Long attachmentId = attachmentService.saveAttachment(file, "document");
        
        // Assert
        assertEquals(1L, attachmentId);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    public void testGetAttachment_Success() {
        // Arrange
        Long attachmentId = 1L;
        Attachment attachment = new Attachment();
        attachment.setId(attachmentId);
        attachment.setFileName("test.txt");
        
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));
        
        // Act
        Attachment result = attachmentService.getAttachment(attachmentId);
        
        // Assert
        assertEquals(attachmentId, result.getId());
        assertEquals("test.txt", result.getFileName());
        verify(attachmentRepository, times(1)).findById(attachmentId);
    }

    @Test
    public void testGetAttachment_NotFound() {
        // Arrange
        Long attachmentId = 999L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> attachmentService.getAttachment(attachmentId)
        );
        
        assertEquals("attachment not found", exception.getMessage());
        verify(attachmentRepository, times(1)).findById(attachmentId);
    }

    @Test
    public void testLoadFileAsResource_Success() throws IOException {
        // Arrange
        String fileName = "test-file.txt";
        Path filePath = tempDir.resolve(fileName);
        String content = "Test content";
        Files.write(filePath, content.getBytes());
        
        // Act
        byte[] result = attachmentService.loadFileAsResource(fileName);
        
        // Assert
        assertArrayEquals(content.getBytes(), result);
    }

    @Test
    public void testLoadFileAsResource_FileNotFound() {
        // Arrange
        String fileName = "non-existent-file.txt";
        
        // Act & Assert
        FileStorageException exception = assertThrows(
                FileStorageException.class,
                () -> attachmentService.loadFileAsResource(fileName)
        );
        
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    public void testExtractContentType_NullFileName() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);
        
        // Create a new attachment to access the private method via reflection
        Attachment attachment = new Attachment();
        
        // Act
        String contentType = (String) ReflectionTestUtils.invokeMethod(
                attachmentService, 
                "extractContentType",
                file
        );
        
        // Assert
        assertEquals("application/octet-stream", contentType);
    }

    @Test
    public void testExtractContentType_WithIOException() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(Files.probeContentType(any(Path.class))).thenThrow(new IOException("Simulated IO error"));
        
        // Act
        String contentType = (String) ReflectionTestUtils.invokeMethod(
                attachmentService, 
                "extractContentType",
                file
        );
        
        // Assert
        assertEquals("application/octet-stream", contentType);
    }
}