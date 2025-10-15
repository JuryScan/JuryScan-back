package unicap.juryscan.dto.address;

import lombok.*;
import unicap.juryscan.enums.TipoEnderecoEnum;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AddressResponseDTO {
    private UUID id;
    private TipoEnderecoEnum tipoEndereco;
    private String logradouro;
    private String cidade;
    private String bairro;
    private String estado;
    private String cep;
}
