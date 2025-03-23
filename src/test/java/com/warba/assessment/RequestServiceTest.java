package com.warba.assessment;

import com.warba.assessment.builder.UserDtos;
import com.warba.assessment.builder.Users;
import com.warba.assessment.dto.request.CreateRequestDto;
import com.warba.assessment.dto.response.RequestDto;
import com.warba.assessment.dto.response.UserDto;
import com.warba.assessment.entity.*;
import com.warba.assessment.exception.BusinessValidationException;
import com.warba.assessment.mapper.RequestMapper;
import com.warba.assessment.repository.*;
import com.warba.assessment.service.RequestServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private RequestMapper requestMapper;
    @InjectMocks
    private RequestServiceImpl requestService;

    private User testUser;
    private UserDto testUserDto;
    private Status testStatus;
    private AttachmentType testAttachmentType1;
    private AttachmentType testAttachmentType2;
    private Request testRequest;
    private List<Attachment> testAttachments;
    private RequestDto testRequestDTO;
    private CreateRequestDto createRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = Users.userBuilder().build();
        testUserDto = UserDtos.userBuilder().build();

        testStatus = new Status();
        testStatus.setId(1L);
        testStatus.setName(Status.Statuses.DRAFT);

        testAttachmentType1 = new AttachmentType();
        testAttachmentType1.setId(2L);
        testAttachmentType1.setName("ID_CARD");

        testAttachmentType2 = new AttachmentType();
        testAttachmentType2.setId(1L);
        testAttachmentType2.setName("PASSPORT");

        testAttachments = new ArrayList<>();
        Attachment attachment1 = new Attachment();
        attachment1.setId(1L);
        attachment1.setFileName("id.pdf");
        attachment1.setFileType("application/pdf");
        attachment1.setAttachmentType(testAttachmentType1);

        Attachment attachment2 = new Attachment();
        attachment2.setId(2L);
        attachment2.setFileName("passport.pdf");
        attachment2.setFileType("application/pdf");
        attachment2.setAttachmentType(testAttachmentType2);

        testAttachments.add(attachment1);
        testAttachments.add(attachment2);

        testRequest = new Request();
        testRequest.setId(1L);
        testRequest.setRequestName("Test Request");
        testRequest.setStatus(testStatus);
        testRequest.setOwner(testUser);
        testRequest.setAttachments(testAttachments);

        // Set up DTOs
        List<Long> attachmentIds = List.of(1L, 2L);

        testRequestDTO = RequestDto.builder()
                .id(1L)
                .requestName("Test Request")
                .statusId(1L)
                .owner(testUserDto)
                .attachmentIds(attachmentIds)
                .build();

        createRequestDTO = CreateRequestDto.builder()
                .requestName("Test Request")
                .userId(testUser.getId())
                .statusId(testStatus.getId())
                .attachmentIds(attachmentIds)
                .build();

    }

    @Test
    void createRequest_WithValidData_ShouldCreateRequest() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(statusRepository.findById(anyLong())).thenReturn(Optional.of(testStatus));
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);
        when(attachmentRepository.findAllById(anyList())).thenReturn(testAttachments);
        // Act
        Long id = requestService.createRequest(createRequestDTO);

        // Assert
        assertNotNull(id);
        verify(userRepository, times(1)).findById(anyLong());
        verify(statusRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void createRequest_WithExpiredCivilId_ShouldThrowBusinessValidationException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(Users.expiredUserBuilder().build()));

        // Act & Assert
        BusinessValidationException exception = assertThrows(BusinessValidationException.class,
                () -> requestService.createRequest(createRequestDTO));

        assertTrue(exception.getMessage().contains("expired"));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void createRequest_WithInsufficientAttachments_ShouldThrowBusinessValidationException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(statusRepository.findById(anyLong())).thenReturn(Optional.of(testStatus));
        when(attachmentRepository.findAllById(anyList())).thenReturn(Arrays.asList(testAttachments.get(0)));

        // Act & Assert
        BusinessValidationException exception = assertThrows(BusinessValidationException.class,
                () -> requestService.createRequest(createRequestDTO));

        assertTrue(exception.getMessage().contains("At least 2 attachments are required"));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void createRequest_WithMissingRequiredAttachments_ShouldThrowBusinessValidationException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(statusRepository.findById(anyLong())).thenReturn(Optional.of(testStatus));

        createRequestDTO.setAttachmentIds(new ArrayList<>());

        // Act & Assert
        BusinessValidationException exception = assertThrows(BusinessValidationException.class,
                () -> requestService.createRequest(createRequestDTO));
        assertTrue(exception.getMessage().contains("At least 2 attachments are required"));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void getRequestById_WhenRequestExists_ShouldReturnRequest() {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(requestMapper.mapRequestToRequestDto(testRequest)).thenReturn(testRequestDTO);

        // Act
        RequestDto result = requestService.getRequest(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testRequestDTO.getId(), result.getId());
        assertEquals(testRequestDTO.getRequestName(), result.getRequestName());
        verify(requestRepository, times(1)).findById(1L);
    }

    @Test
    void getRequestById_WhenRequestDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> requestService.getRequest(1L));
        verify(requestRepository, times(1)).findById(1L);
    }
}