package unicap.juryscan.dto.lead;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LeadCreateRequestDTO {
    private UUID analysisId;
}

