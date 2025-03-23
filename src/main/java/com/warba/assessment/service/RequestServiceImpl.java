package com.warba.assessment.service;

import com.warba.assessment.dto.request.CreateRequestDto;
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

import static com.warba.assessment.exception.Messages.REQUEST_NOT_FOUND;
import static com.warba.assessment.exception.Messages.USER_NOT_FOUND;
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
        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw entityNotFoundSupplier(USER_NOT_FOUND.evaluated(userId)).get();
        }

        return requestMapper.mapToRequestDtoList(requestRepository.findByOwnerId(userId));
    }

    @Override
    @Transactional
    public void deleteRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(entityNotFoundSupplier(REQUEST_NOT_FOUND.evaluated(id)));
        requestRepository.delete(request);
    }
}