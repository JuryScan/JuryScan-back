package unicap.juryscan.unitTests.mapper.UserAdvogado;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoResponseDTO;
import unicap.juryscan.mapper.UserAdvogadoMapper;
import unicap.juryscan.model.Address;
import unicap.juryscan.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserAdvogadoMapperTest {

    private UserAdvogadoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserAdvogadoMapper();
    }

    @Nested
    @DisplayName("Tests for toResponseDTO")
    class ToResponseDTOTests {

        @Test
        @DisplayName("Should map all basic fields correctly")
        void shouldMapBasicFields() {
            // Arrange
            User entity = new User();
            entity.setId(UUID.randomUUID());
            entity.setNomeCompleto("Dev Teste");
            entity.setEmail("test@email.com");
            entity.setNumeroOab("123456/PE");


            UserAdvogadoResponseDTO dto = mapper.toResponseDTO(entity);


            assertEquals(entity.getId(), dto.getId());
            assertEquals(entity.getNomeCompleto(), dto.getNomeCompleto());
            assertEquals(entity.getEmail(), dto.getEmail());
            assertEquals(entity.getNumeroOab(), dto.getNumeroOab());
        }

        @Test
        @DisplayName("Should generate address URL when address is present")
        void shouldGenerateAddressUrl_WhenAddressIsPresent() {

            UUID userId = UUID.randomUUID();
            User entity = new User();
            entity.setId(userId);
            entity.setEndereco(new Address());


            UserAdvogadoResponseDTO dto = mapper.toResponseDTO(entity);


            assertEquals("/addresses/" + userId, dto.getEnderecoUrl());
        }

        @Test
        @DisplayName("Should set address URL as null when address is missing")
        void shouldSetAddressUrlAsNull_WhenAddressIsNull() {
            // Arrange
            User entity = new User();
            entity.setId(UUID.randomUUID());
            entity.setEndereco(null);


            UserAdvogadoResponseDTO dto = mapper.toResponseDTO(entity);


            assertNull(dto.getEnderecoUrl());
        }
    }

    @Nested
    @DisplayName("Tests for toEntity")
    class ToEntityTests {

        @Test
        @DisplayName("Should map CreateDTO to User entity correctly")
        void shouldMapDtoToEntity() {
            // Arrange
            UserAdvogadoCreateDTO dto = new UserAdvogadoCreateDTO();
            dto.setNomeCompleto("Advogado Criado");
            dto.setCpf("123.456.789-00");


            User entity = mapper.toEntity(dto);


            assertEquals(dto.getNomeCompleto(), entity.getNomeCompleto());
            assertEquals(dto.getCpf(), entity.getCpf());
        }
    }
}