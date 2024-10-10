package com.vietqradminbe.application.mappers;

import com.vietqradminbe.domain.models.User;
import com.vietqradminbe.web.dto.request.UserCreationRequest;
import com.vietqradminbe.web.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    //void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
