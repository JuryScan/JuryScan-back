package unicap.juryscan.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private int status;

    // Response de sucesso na requisição
    public ApiResponse(boolean success, String message, Object data, int status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
    }

    // Response de erro na requisição
    public ApiResponse(boolean success, String message, int status)
    {
        this.success = success;
        this.message = message;
        this.status = status;
    }
}
