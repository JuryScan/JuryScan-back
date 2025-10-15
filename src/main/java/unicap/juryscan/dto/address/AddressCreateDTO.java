package unicap.juryscan.dto.address;

import lombok.*;
import unicap.juryscan.enums.TipoEnderecoEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AddressCreateDTO {
    private TipoEnderecoEnum tipoEndereco;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
