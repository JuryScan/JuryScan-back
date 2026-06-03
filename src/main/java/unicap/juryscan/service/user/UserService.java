package unicap.juryscan.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import unicap.juryscan.dto.user.ChangePasswordDTO;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;

import java.io.IOException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${api.base-url}")
    private String baseUrl;

    @Value("${api.uri}")
    private String apiUri;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (dto.getCurrentPassword() == null || !passwordEncoder.matches(dto.getCurrentPassword(), user.getSenha())) {
            throw new IllegalStateException("Senha atual incorreta");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 8) {
            throw new IllegalStateException("A nova senha deve ter pelo menos 8 caracteres");
        }

        user.setSenha(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void updateAvatar(UUID id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("Arquivo de imagem vazio");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalStateException("Tipo de arquivo inválido. Envie uma imagem.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        try {
            user.setFotoPerfil(file.getBytes());
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler a imagem enviada");
        }
        user.setFotoContentType(contentType);
        user.setFotoUrl(baseUrl + apiUri + "/users/avatar/" + id);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}
