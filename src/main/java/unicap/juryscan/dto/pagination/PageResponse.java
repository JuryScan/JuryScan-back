package unicap.juryscan.dto.pagination;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int totalPages;
    private int pageSize;
    private long totalElements;
}
