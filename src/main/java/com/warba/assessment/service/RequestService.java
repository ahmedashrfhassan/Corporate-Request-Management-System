package com.warba.assessment.service;

import com.warba.assessment.dto.request.CreateRequestDto;
import com.warba.assessment.dto.response.RequestDto;

import java.util.List;

public interface RequestService {
    Long createRequest(CreateRequestDto requestDTO);
    RequestDto getRequest(Long id);
    List<RequestDto> getRequestsByUser(Long userId);
    void deleteRequest(Long id);
}