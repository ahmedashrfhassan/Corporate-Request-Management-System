package com.warba.assessment.mapper;

import com.warba.assessment.dto.response.RequestDto;
import com.warba.assessment.entity.Attachment;
import com.warba.assessment.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = UserMapper.class, componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "attachments", target = "attachmentIds", qualifiedByName = "toIds")
    @Mapping(source = "status.id", target = "statusId")
    RequestDto mapRequestToRequestDto(Request request);

    List<RequestDto> mapToRequestDtoList(List<Request> requests);

    @Named("toIds")
    static Long bToId(Attachment a) {
        return a.getId();
    }
}
