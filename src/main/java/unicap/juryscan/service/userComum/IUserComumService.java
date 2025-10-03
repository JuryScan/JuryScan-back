package unicap.juryscan.service.userComum;

import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;
import unicap.juryscan.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserComumService {

    UserComumResponseDTO createUserComum(UserComumCreateDTO user);

    List<UserComumResponseDTO> getAllUserComums();

    UserComumResponseDTO getUserComumById(UUID id);
}
