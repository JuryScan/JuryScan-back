package unicap.juryscan.mapper;

import org.springframework.stereotype.Component;
import unicap.juryscan.dto.address.AddressCreateDTO;
import unicap.juryscan.dto.address.AddressResponseDTO;
import unicap.juryscan.model.Address;

@Component
public class AddressMapper {

    public AddressResponseDTO toResponseDTO(Address address) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(address.getId());
        dto.setTipoEndereco(address.getTipoEndereco());
        dto.setLogradouro(address.getLogradouro());
        dto.setCidade(address.getCidade());
        dto.setBairro(address.getBairro());
        dto.setEstado(address.getEstado());
        dto.setCep(address.getCep());

        return dto;
    }

    public Address toEntity(AddressCreateDTO dto) {
        Address entity = new Address();
        entity.setTipoEndereco(dto.getTipoEndereco());
        entity.setLogradouro(dto.getLogradouro());
        entity.setCidade(dto.getCidade());
        entity.setBairro(dto.getBairro());
        entity.setEstado(dto.getEstado());
        entity.setCep(dto.getCep());

        return entity;
    }
}
