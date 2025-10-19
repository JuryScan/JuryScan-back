package unicap.juryscan;

import org.junit.jupiter.api.Test;
import unicap.juryscan.enums.TipoEnderecoEnum;
import unicap.juryscan.model.Address;
import unicap.juryscan.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressModelTest {

    @Test
    void testAddressCreationAndFields() {
        Address address = new Address();
        address.setTipoEndereco(TipoEnderecoEnum.RESIDENCIAL);
        address.setLogradouro("Rua das Flores, 123");
        address.setCidade("Recife");
        address.setBairro("Boa Vista");
        address.setEstado("PE");
        address.setCep("50000-000");


        assertEquals(TipoEnderecoEnum.RESIDENCIAL, address.getTipoEndereco());
        assertEquals("Rua das Flores, 123", address.getLogradouro());
        assertEquals("Recife", address.getCidade());
        assertEquals("Boa Vista", address.getBairro());
        assertEquals("PE", address.getEstado());
        assertEquals("50000-000", address.getCep());
    }
}
