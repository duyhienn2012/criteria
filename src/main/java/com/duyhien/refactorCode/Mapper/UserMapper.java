package com.duyhien.refactorCode.Mapper;

import com.duyhien.refactorCode.Dto.Request.UserCreationRequest;
import com.duyhien.refactorCode.Dto.Request.UserUpdateRequest;
import com.duyhien.refactorCode.Dto.Response.UserResponse;
import com.duyhien.refactorCode.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
