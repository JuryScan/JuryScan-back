package unicap.juryscan.service.userAdvogado;

import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoResponseDTO;

import java.util.UUID;

public interface IUserAdvogadoService {

    UserAdvogadoResponseDTO createUserAdvogado(UserAdvogadoCreateDTO user);

    PageResponse<UserAdvogadoResponseDTO> getAllUserAdvogados(Pageable pageable);

    UserAdvogadoResponseDTO getUserAdvogadoById(UUID id);

    void hardDeleteUserAdvogado(UUID id);

    void softDeleteUserAdvogado(UUID id);

}
