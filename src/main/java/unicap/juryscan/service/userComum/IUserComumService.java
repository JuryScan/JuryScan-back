package unicap.juryscan.service.userComum;

import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumRegisteredDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;
import unicap.juryscan.dto.userComum.UserComumUpdateDTO;

import java.util.UUID;

public interface IUserComumService {

    UserComumRegisteredDTO createUserComum(UserComumCreateDTO user);

    UserComumResponseDTO updateUserComum(UUID id, UserComumUpdateDTO dto);

    PageResponse<UserComumResponseDTO> getAllUserComums(Pageable pageable);

    UserComumResponseDTO getUserComumById(UUID id);

    void hardDeleteUserComum(UUID id);

    void softDeleteUserComum(UUID id);
}
