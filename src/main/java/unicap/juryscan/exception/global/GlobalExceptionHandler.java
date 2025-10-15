package unicap.juryscan.exception.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.exception.model.ApiResponseError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseError> handleGenericException(Exception e){
        e.printStackTrace();
        ApiResponseError responseError = new ApiResponseError(500, e);
        return ResponseEntity.status(500).body(responseError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseError> handleResourceNotFoundException(ResourceNotFoundException e){
        ApiResponseError responseError = new ApiResponseError(404, e);
        return ResponseEntity.status(404).body(responseError);
    }
}
