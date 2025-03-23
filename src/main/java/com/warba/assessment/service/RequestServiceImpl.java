package com.warba.assessment.service;

import com.warba.assessment.dto.request.CreateRequestDto;
import com.warba.assessment.dto.request.UpdateRequestDTO;
import com.warba.assessment.dto.response.RequestDto;
import com.warba.assessment.entity.Attachment;
import com.warba.assessment.entity.Request;
import com.warba.assessment.entity.Status;
import com.warba.assessment.entity.User;
import com.warba.assessment.exception.BusinessValidationException;
import com.warba.assessment.exception.Messages;
import com.warba.assessment.mapper.RequestMapper;
import com.warba.assessment.repository.AttachmentRepository;
import com.warba.assessment.repository.RequestRepository;
import com.warba.assessment.repository.StatusRepository;
import com.warba.assessment.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.warba.assessment.exception.Messages.*;
import static com.warba.assessment.exception.suppliers.ResourceNotFoundSupplier.entityNotFoundSupplier;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final AttachmentRepository attachmentRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public Long createRequest(CreateRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(entityNotFoundSupplier(USER_NOT_FOUND.evaluated(dto.getUserId())));

        if (user.isCivilIdExpired()) {
            throw new BusinessValidationException(Messages.EXPIRED_CIVIL_ID.value());
        }
        Status status = statusRepository.findById(dto.getStatusId())
                .orElseThrow(entityNotFoundSupplier("Status not found with ID: " + dto.getStatusId()));

        //todo Validate attachments (attachment Validator) and congure required
        List<Attachment> attachments = attachmentRepository
                .findAllById(dto.getAttachmentIds());
        if (attachments.size() < 2) {
            throw new BusinessValidationException("At least 2 attachments are required. Only ");
        }
        Request request = buildRequest(dto, user, status, attachments);
        return requestRepository.save(request).getId();
    }

    private static Request buildRequest(CreateRequestDto dto,
                                        User user,
                                        Status status,
                                        List<Attachment> attachments) {
        return Request.builder()
                .requestName(dto.getRequestName())
                .owner(user)
                .status(status)
                .attachments(attachments).build();
    }

    @Override
    public RequestDto getRequest(Long id) {
        Request req = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + id));
        return requestMapper.mapRequestToRequestDto(req);
    }

    @Override
    public List<RequestDto> getRequestsByUser(Long userId) {
        //todo validate that assocciations of user aren't retreived if user is safely deleted
        if (userRepository.existsById(userId)) {
            throw entityNotFoundSupplier(USER_NOT_FOUND.evaluated(userId)).get();
        }

        return requestMapper.mapToRequestDtoList(requestRepository.findByOwnerId(userId));
    }


    @Override
    @Transactional
    public void updateRequest(Long id, UpdateRequestDTO dto) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + id));

        //todo move to validator
        checkUserCivilIdIsNotExpired(request);
        Status status = statusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException(STATUS_NOT_FOUND.evaluated(dto.getStatusId())));
        request.setStatus(status);
        request.setRequestName(dto.getRequestName());

        // Update attachments if provided
        if (dto.getAttachmentIds() != null && !dto.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = attachmentRepository.findAllById(dto.getAttachmentIds());

            if (attachments.size() < 2) {
                throw new BusinessValidationException("At least 2 attachments are required. Only " + attachments.size() + " provided.");
            }

            request.setAttachments(attachments);
        }
//        TODO request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    private void checkUserCivilIdIsNotExpired(Request request) {
        User owner = request.getOwner();
        if (owner.isCivilIdExpired()) {
            throw new BusinessValidationException(Messages.EXPIRED_CIVIL_ID.value());
        }
    }

    @Override
    @Transactional
    public void cancelRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(entityNotFoundSupplier(REQUEST_NOT_FOUND.evaluated(id)));
        Status deleted = statusRepository.findByName(Status.Statuses.CANCELLED);
        request.setStatus(deleted);
        requestRepository.save(request);
    }
}