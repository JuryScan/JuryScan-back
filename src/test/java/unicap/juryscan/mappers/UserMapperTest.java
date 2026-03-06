package unicap.juryscan.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import unicap.juryscan.dto.auth.UserAuthenticatedDTO;
import unicap.juryscan.mapper.UserMapper;
import unicap.juryscan.model.Address;
import unicap.juryscan.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @DisplayName("Should map User to UserAuthenticatedDTO correctly with address")
    void shouldMapUserToDtoWithAddress() {

        UUID addressId = UUID.randomUUID();
        Address address = new Address();
        address.setId(addressId);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setNomeCompleto("Teste User");
        user.setEmail("teste@email.com");
        user.setEndereco(address);


        UserAuthenticatedDTO dto = userMapper.toUserAuthenticatedDTO(user);


        assertNotNull(dto);
        assertEquals(user.getNomeCompleto(), dto.getNomeCompleto());
        assertEquals("/addresses/" + addressId, dto.getEnderecoUrl());
    }

    @Test
    @DisplayName("Should map User to UserAuthenticatedDTO correctly without address")
    void shouldMapUserToDtoWithoutAddress() {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setNomeCompleto("Sem Endereco");
        user.setEndereco(null);


        UserAuthenticatedDTO dto = userMapper.toUserAuthenticatedDTO(user);


        assertNotNull(dto);
        assertNull(dto.getEnderecoUrl());
    }
}