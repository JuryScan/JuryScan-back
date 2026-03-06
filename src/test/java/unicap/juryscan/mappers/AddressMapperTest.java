package unicap.juryscan.mappers;

import org.junit.jupiter.api.Test;
import unicap.juryscan.dto.address.AddressCreateDTO;
import unicap.juryscan.dto.address.AddressResponseDTO;
import unicap.juryscan.enums.TipoEnderecoEnum;
import unicap.juryscan.mapper.AddressMapper;
import unicap.juryscan.model.Address;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressMapperTest {

    private final AddressMapper mapper = new AddressMapper();

    @Test
    void testToEntity() {
        AddressCreateDTO dto = new AddressCreateDTO();
        dto.setTipoEndereco(TipoEnderecoEnum.RESIDENCIAL);
        dto.setLogradouro("Rua A");
        dto.setCidade("Recife");
        dto.setBairro("Boa Vista");
        dto.setEstado("PE");
        dto.setCep("50000-000");

        Address entity = mapper.toEntity(dto);

        assertEquals(TipoEnderecoEnum.RESIDENCIAL, entity.getTipoEndereco());
        assertEquals("Rua A", entity.getLogradouro());
        assertEquals("Recife", entity.getCidade());
        assertEquals("Boa Vista", entity.getBairro());
        assertEquals("PE", entity.getEstado());
        assertEquals("50000-000", entity.getCep());
    }

    @Test
    void testToResponseDTO() {
        Address address = new Address();
        UUID uuid = UUID.randomUUID();
        address.setId(uuid);
        address.setTipoEndereco(TipoEnderecoEnum.COMERCIAL);
        address.setLogradouro("Av. B");
        address.setCidade("Olinda");
        address.setBairro("Centro");
        address.setEstado("PE");
        address.setCep("53000-000");

        AddressResponseDTO dto = mapper.toResponseDTO(address);

        assertEquals(uuid, dto.getId());
        assertEquals(TipoEnderecoEnum.COMERCIAL, dto.getTipoEndereco());
        assertEquals("Av. B", dto.getLogradouro());
        assertEquals("Olinda", dto.getCidade());
        assertEquals("Centro", dto.getBairro());
        assertEquals("PE", dto.getEstado());
        assertEquals("53000-000", dto.getCep());
    }
}