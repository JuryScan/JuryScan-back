package unicap.juryscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.address.AddressCreateDTO;
import unicap.juryscan.dto.address.AddressResponseDTO;
import unicap.juryscan.service.address.AddressService;
import unicap.juryscan.utils.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getAddressById(@PathVariable UUID id) {
        AddressResponseDTO address = addressService.getAddressById(id);
        ApiResponse response = new ApiResponse(true, "Endereço encontrado com sucesso", address, 200);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> createAddress(@PathVariable UUID userId, @RequestBody AddressCreateDTO addressRequest){
        AddressResponseDTO createdAddress = addressService.createAddress(addressRequest, userId);
        ApiResponse response = new ApiResponse(true, "Endereço criado com sucesso", createdAddress, 201);
        return ResponseEntity.status(201).body(response);
    }
}
