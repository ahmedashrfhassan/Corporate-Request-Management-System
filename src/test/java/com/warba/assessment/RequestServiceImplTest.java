package com.warba.assessment;

import com.warba.assessment.builder.Users;
import com.warba.assessment.dto.request.CreateRequestDto;
import com.warba.assessment.dto.response.RequestDto;
import com.warba.assessment.entity.Attachment;
import com.warba.assessment.entity.Request;
import com.warba.assessment.entity.Status;
import com.warba.assessment.entity.User;
import com.warba.assessment.exception.BusinessValidationException;
import com.warba.assessment.exception.ResourceNotFoundException;
import com.warba.assessment.mapper.RequestMapper;
import com.warba.assessment.repository.AttachmentRepository;
import com.warba.assessment.repository.RequestRepository;
import com.warba.assessment.repository.StatusRepository;
import com.warba.assessment.repository.UserRepository;
import com.warba.assessment.service.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

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

    private User validUser;
    private Status validStatus;
    private List<Attachment> validAttachments;
    private Request validRequest;
    private CreateRequestDto validCreateRequestDto;
    private RequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        // Setup valid user
        validUser = Users.userBuilder().build();
        // Setup valid status
        validStatus = new Status();
        validStatus.setId(1L);
        validStatus.setName(Status.Statuses.IN_PROGRESS);

        // Setup valid attachments
        Attachment attachment1 = new Attachment();
        attachment1.setId(1L);

        Attachment attachment2 = new Attachment();
        attachment2.setId(2L);

        validAttachments = Arrays.asList(attachment1, attachment2);

        // Setup valid request
        validRequest = new Request();
        validRequest.setId(1L);
        validRequest.setRequestName("Test Request");
        validRequest.setOwner(validUser);
        validRequest.setStatus(validStatus);
        validRequest.setAttachments(validAttachments);

        // Setup valid CreateRequestDto
        validCreateRequestDto = new CreateRequestDto();
        validCreateRequestDto.setRequestName("Test Request");
        validCreateRequestDto.setUserId(1L);
        validCreateRequestDto.setStatusId(1L);
        validCreateRequestDto.setAttachmentIds(Arrays.asList(1L, 2L));

        // Setup valid RequestDto
        validRequestDto = new RequestDto();
        validRequestDto.setId(1L);
        validRequestDto.setRequestName("Test Request");
    }

    @Test
    void createRequest_ValidInputs_CreatesSuccessfully() {
        // Arrange
        when(userRepository.findById(validCreateRequestDto.getUserId())).thenReturn(Optional.of(validUser));
        when(statusRepository.findById(validCreateRequestDto.getStatusId())).thenReturn(Optional.of(validStatus));
        when(attachmentRepository.findAllById(validCreateRequestDto.getAttachmentIds())).thenReturn(validAttachments);
        when(requestRepository.save(any(Request.class))).thenReturn(validRequest);

        // Act
        Long resultId = requestService.createRequest(validCreateRequestDto);

        // Assert
        assertEquals(validRequest.getId(), resultId);
        verify(userRepository).findById(validCreateRequestDto.getUserId());
        verify(statusRepository).findById(validCreateRequestDto.getStatusId());
        verify(attachmentRepository).findAllById(validCreateRequestDto.getAttachmentIds());
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void createRequest_UserDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(validCreateRequestDto.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                requestService.createRequest(validCreateRequestDto)
        );

        verify(userRepository).findById(validCreateRequestDto.getUserId());
        verify(statusRepository, never()).findById(any());
        verify(attachmentRepository, never()).findAllById(any());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void createRequest_UserHasExpiredCivilId_ThrowsBusinessValidationException() {
        // Arrange
        User userWithExpiredId = Users.expiredUserBuilder().build();
        when(userRepository.findById(validCreateRequestDto.getUserId())).thenReturn(Optional.of(userWithExpiredId));

        // Act & Assert
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () ->
                requestService.createRequest(validCreateRequestDto)
        );

        assertTrue(exception.getMessage().contains("expired"));
        verify(userRepository).findById(validCreateRequestDto.getUserId());
        verify(statusRepository, never()).findById(any());
        verify(attachmentRepository, never()).findAllById(any());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void createRequest_StatusDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(validCreateRequestDto.getUserId())).thenReturn(Optional.of(validUser));
        when(statusRepository.findById(validCreateRequestDto.getStatusId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                requestService.createRequest(validCreateRequestDto)
        );

        verify(userRepository).findById(validCreateRequestDto.getUserId());
        verify(statusRepository).findById(validCreateRequestDto.getStatusId());
        verify(attachmentRepository, never()).findAllById(any());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void createRequest_NotEnoughAttachments_ThrowsBusinessValidationException() {
        // Arrange
        List<Attachment> insufficientAttachments = Collections.singletonList(new Attachment());

        when(userRepository.findById(validCreateRequestDto.getUserId())).thenReturn(Optional.of(validUser));
        when(statusRepository.findById(validCreateRequestDto.getStatusId())).thenReturn(Optional.of(validStatus));
        when(attachmentRepository.findAllById(validCreateRequestDto.getAttachmentIds())).thenReturn(insufficientAttachments);

        // Act & Assert
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () ->
                requestService.createRequest(validCreateRequestDto)
        );

        assertTrue(exception.getMessage().contains("At least 2 attachments are required"));
        verify(userRepository).findById(validCreateRequestDto.getUserId());
        verify(statusRepository).findById(validCreateRequestDto.getStatusId());
        verify(attachmentRepository).findAllById(validCreateRequestDto.getAttachmentIds());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getRequest_ExistingRequest_ReturnsSuccessfully() {
        // Arrange
        Long requestId = 1L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(validRequest));
        when(requestMapper.mapRequestToRequestDto(validRequest)).thenReturn(validRequestDto);

        // Act
        RequestDto result = requestService.getRequest(requestId);

        // Assert
        assertNotNull(result);
        assertEquals(validRequestDto.getId(), result.getId());
        assertEquals(validRequestDto.getRequestName(), result.getRequestName());
        verify(requestRepository).findById(requestId);
        verify(requestMapper).mapRequestToRequestDto(validRequest);
    }

    @Test
    void getRequest_RequestDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        Long requestId = 999L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                requestService.getRequest(requestId)
        );

        verify(requestRepository).findById(requestId);
        verify(requestMapper, never()).mapRequestToRequestDto(any());
    }

    @Test
    void getRequestsByUser_ValidUserId_ReturnsRequests() {
        // Arrange
        Long userId = 1L;
        List<Request> userRequests = Collections.singletonList(validRequest);
        List<RequestDto> expectedDtos = Collections.singletonList(validRequestDto);

        when(userRepository.existsByIdAndDeletedFalse(userId)).thenReturn(true);
        when(requestRepository.findByOwnerId(userId)).thenReturn(userRequests);
        when(requestMapper.mapToRequestDtoList(userRequests)).thenReturn(expectedDtos);

        // Act
        List<RequestDto> result = requestService.getRequestsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(validRequestDto.getId(), result.get(0).getId());
        verify(userRepository).existsByIdAndDeletedFalse(userId);
        verify(requestRepository).findByOwnerId(userId);
        verify(requestMapper).mapToRequestDtoList(userRequests);
    }

    @Test
    void getRequestsByUser_UserDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsByIdAndDeletedFalse(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                requestService.getRequestsByUser(userId)
        );

        verify(userRepository).existsByIdAndDeletedFalse(userId);
        verify(requestRepository, never()).findByOwnerId(any());
        verify(requestMapper, never()).mapToRequestDtoList(any());
    }

    @Test
    void deleteRequest_ExistingRequest_DeletesSuccessfully() {
        // Arrange
        Long requestId = 1L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(validRequest));

        // Act
        requestService.deleteRequest(requestId);

        // Assert
        verify(requestRepository).findById(requestId);
        verify(requestRepository).delete(validRequest);
    }

    @Test
    void deleteRequest_RequestDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        Long requestId = 999L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                requestService.deleteRequest(requestId)
        );

        verify(requestRepository).findById(requestId);
        verify(requestRepository, never()).delete(any());
    }
}