package unicap.juryscan.service.address;

import org.springframework.stereotype.Service;
import unicap.juryscan.dto.address.AddressCreateDTO;
import unicap.juryscan.dto.address.AddressResponseDTO;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.mapper.AddressMapper;
import unicap.juryscan.model.Address;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AddressRepository;
import unicap.juryscan.repository.UserRepository;

import java.util.UUID;

@Service
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.userRepository = userRepository;
    }

    @Override
    public AddressResponseDTO createAddress(AddressCreateDTO addressCreateDTO, UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Address address = addressMapper.toEntity(addressCreateDTO);
        address.setUsuario(user);
        user.setEndereco(address);
        addressRepository.save(address);
        userRepository.save(user);

        return addressMapper.toResponseDTO(address);
    }

    @Override
    public AddressResponseDTO getAddressById(UUID id) {
        return addressRepository
                .findById(id)
                .map(addressMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));
    }
}
