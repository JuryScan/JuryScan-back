package unicap.juryscan.service.userAdvogado;

import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoRegisteredDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoResponseDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoUpdateDTO;

import java.util.UUID;

public interface IUserAdvogadoService {

    UserAdvogadoRegisteredDTO createUserAdvogado(UserAdvogadoCreateDTO user);

    UserAdvogadoResponseDTO updateUserAdvogado(UUID id, UserAdvogadoUpdateDTO dto);

    PageResponse<UserAdvogadoResponseDTO> getAllUserAdvogados(Pageable pageable);

    PageResponse<UserAdvogadoResponseDTO> searchAdvogados(String busca, String cidade, String estado, Pageable pageable);

    UserAdvogadoResponseDTO getUserAdvogadoById(UUID id);

    void hardDeleteUserAdvogado(UUID id);

    void softDeleteUserAdvogado(UUID id);

}
