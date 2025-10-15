package unicap.juryscan.exception.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseError {
    private int status;
    private final boolean success = false;
    private String message;
    private String error;

    public ApiResponseError(int status, Exception e) {
        this.status = status;
        this.error = e.getStackTrace()[0].toString();
        this.message = e.getMessage();
    }
}
