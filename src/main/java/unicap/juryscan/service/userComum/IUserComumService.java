package unicap.juryscan.service.userComum;

import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IUserComumService {

    UserComumResponseDTO createUserComum(UserComumCreateDTO user);

    List<UserComumResponseDTO> getAllUserComums();

    UserComumResponseDTO getUserComumById(UUID id);

    void hardDeleteUserComum(UUID id);

    void softDeleteUserComum(UUID id);
}
