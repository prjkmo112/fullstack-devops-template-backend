package io.github.prjkmo112.mainapi.security.user;

import io.github.prjkmo112.commonmysqldb.entity.User;
import io.github.prjkmo112.mainapi.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "isLoginned", ignore = true)
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwd", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User fromDto(UserDto userDto);
}
