package unicap.juryscan.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import unicap.juryscan.dto.address.AddressCreateDTO;
import unicap.juryscan.dto.address.AddressResponseDTO;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.mapper.AddressMapper;
import unicap.juryscan.model.Address;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AddressRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.address.AddressService;
import unicap.juryscan.service.address.IAddressService;


import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

   
    @Test
    void createAddress_success() {
        UUID userId = UUID.randomUUID();

        AddressCreateDTO requestDTO = mock(AddressCreateDTO.class);
        AddressResponseDTO responseDTO = mock(AddressResponseDTO.class);

        User user = mock(User.class);
        Address address = mock(Address.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressMapper.toEntity(requestDTO)).thenReturn(address);
        when(addressMapper.toResponseDTO(address)).thenReturn(responseDTO);

        AddressResponseDTO result = addressService.createAddress(requestDTO, userId);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(addressRepository, times(1)).save(address);
        verify(userRepository, times(1)).save(user);
    }

  
    @Test
    void createAddress_userNotFound() {
        UUID userId = UUID.randomUUID();
        AddressCreateDTO requestDTO = mock(AddressCreateDTO.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.createAddress(requestDTO, userId));
    }


    @Test
    void getAddressById_success() {
        UUID id = UUID.randomUUID();

        Address address = mock(Address.class);
        AddressResponseDTO responseDTO = mock(AddressResponseDTO.class);

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));
        when(addressMapper.toResponseDTO(address)).thenReturn(responseDTO);

        AddressResponseDTO result = addressService.getAddressById(id);

        assertNotNull(result);
        assertEquals(responseDTO, result);
    }

   
    @Test
    void getAddressById_notFound() {
        UUID id = UUID.randomUUID();

        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.getAddressById(id));
    }
}
