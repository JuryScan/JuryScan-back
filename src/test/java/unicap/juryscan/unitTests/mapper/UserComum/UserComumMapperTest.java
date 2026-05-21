package unicap.juryscan.unitTests.mapper.UserComum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;
import unicap.juryscan.mapper.UserComumMapper;
import unicap.juryscan.model.Address;
import unicap.juryscan.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserComumMapperTest {

    private UserComumMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserComumMapper();
    }

    @Nested
    @DisplayName("Tests for toResponseDTO")
    class ToResponseDTOTests {

        @Test
        @DisplayName("Should map all basic fields correctly")
        void shouldMapBasicFields() {
            User entity = new User();
            entity.setId(UUID.randomUUID());
            entity.setNomeCompleto("User Teste");
            entity.setEmail("comum@test.com");

            UserComumResponseDTO dto = mapper.toResponseDTO(entity);

            assertEquals(entity.getId(), dto.getId());
            assertEquals(entity.getNomeCompleto(), dto.getNomeCompleto());
            assertEquals(entity.getEmail(), dto.getEmail());
        }

        @Test
        @DisplayName("Should generate address URL when address is present")
        void shouldGenerateAddressUrl_WhenAddressIsPresent() {
            UUID addressId = UUID.randomUUID();
            Address address = new Address();
            address.setId(addressId);

            User entity = new User();
            entity.setEndereco(address);

            UserComumResponseDTO dto = mapper.toResponseDTO(entity);

            assertEquals("/addresses/" + addressId, dto.getEnderecoUrl());
        }

        @Test
        @DisplayName("Should set address URL as null when address is missing")
        void shouldSetAddressUrlAsNull_WhenAddressIsNull() {
            User entity = new User();
            entity.setEndereco(null);

            UserComumResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getEnderecoUrl());
        }
    }

    @Nested
    @DisplayName("Tests for toEntity")
    class ToEntityTests {

        @Test
        @DisplayName("Should map UserComumCreateDTO to User entity correctly")
        void shouldMapDtoToEntity() {
            UserComumCreateDTO dto = new UserComumCreateDTO();
            dto.setNomeCompleto("Common User");
            dto.setCpf("000.000.000-00");

            User entity = mapper.toEntity(dto);

            assertEquals(dto.getNomeCompleto(), entity.getNomeCompleto());
            assertEquals(dto.getCpf(), entity.getCpf());
        }
    }
}