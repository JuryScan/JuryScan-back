package unicap.juryscan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserComumCreateDTO dto);
}
