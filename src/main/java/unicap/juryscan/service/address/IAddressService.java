package unicap.juryscan.service.address;

import unicap.juryscan.dto.address.AddressCreateDTO;
import unicap.juryscan.dto.address.AddressResponseDTO;

import java.util.UUID;

public interface IAddressService {

    AddressResponseDTO createAddress(AddressCreateDTO address, UUID userId);

    AddressResponseDTO getAddressById(UUID addressId);
}
