package unicap.juryscan.dto.payment;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductRequest {
    private Long amount;
    private Long quantity;
    private String name;
    private String currency;
}