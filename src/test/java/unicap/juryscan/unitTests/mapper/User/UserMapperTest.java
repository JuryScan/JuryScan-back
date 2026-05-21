package unicap.juryscan.unitTests.mapper.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import unicap.juryscan.dto.auth.UserAuthenticatedDTO;
import unicap.juryscan.mapper.UserMapper;
import unicap.juryscan.model.Address;
import unicap.juryscan.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    @Nested
    @DisplayName("Tests for toUserAuthenticatedDTO")
    class ToUserAuthenticatedDTotests {

        @Test
        @DisplayName("Should map all basic fields correctly")
        void shouldMapBasicFields() {
            User entity = new User();
            entity.setId(UUID.randomUUID());
            entity.setNomeCompleto("Authenticated User");
            entity.setEmail("auth@test.com");

            UserAuthenticatedDTO dto = mapper.toUserAuthenticatedDTO(entity);

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

            UserAuthenticatedDTO dto = mapper.toUserAuthenticatedDTO(entity);

            assertEquals("/addresses/" + addressId, dto.getEnderecoUrl());
        }

        @Test
        @DisplayName("Should set address URL as null when address is missing")
        void shouldSetAddressUrlAsNull_WhenAddressIsNull() {
            User entity = new User();
            entity.setEndereco(null);

            UserAuthenticatedDTO dto = mapper.toUserAuthenticatedDTO(entity);

            assertNull(dto.getEnderecoUrl());
        }
    }
}