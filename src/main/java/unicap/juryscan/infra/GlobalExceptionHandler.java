package unicap.juryscan.infra;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import unicap.juryscan.exception.InsufficientCreditsException;
import unicap.juryscan.exception.RecaptchaException;
import unicap.juryscan.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseError> handleBadCredentialsException(BadCredentialsException e){
        ApiResponseError responseError = new ApiResponseError(401, "Email ou senha incorretos");
        return ResponseEntity.status(401).body(responseError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseError> handleUsernameNotFoundException(UsernameNotFoundException e){
        ApiResponseError responseError = new ApiResponseError(401, "Email ou senha incorretos");
        return ResponseEntity.status(401).body(responseError);
    }

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

    @ExceptionHandler(RecaptchaException.class)
    public ResponseEntity<ApiResponseError> handleRecaptchaException(RecaptchaException e){
        ApiResponseError responseError = new ApiResponseError(403, e);
        return ResponseEntity.status(403).body(responseError);
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<ApiResponseError> handleSignatureVerificationException(SignatureVerificationException e){
        ApiResponseError responseError = new ApiResponseError(401, "Falha na verificação da assinatura do webhook. Requisição não autorizada.");
        return ResponseEntity.status(401).body(responseError);
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ApiResponseError> handleStripeException(StripeException e){
        ApiResponseError responseError = new ApiResponseError(502, "Erro ao processar pagamento: " + e.getMessage());
        return ResponseEntity.status(502).body(responseError);
    }

    @ExceptionHandler(InsufficientCreditsException.class)
    public ResponseEntity<ApiResponseError> handleInsufficientCreditsException(InsufficientCreditsException e){
        ApiResponseError responseError = new ApiResponseError(402, e.getMessage());
        return ResponseEntity.status(402).body(responseError);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseError> handleIllegalStateException(IllegalStateException e){
        ApiResponseError responseError = new ApiResponseError(400, e.getMessage());
        return ResponseEntity.status(400).body(responseError);
    }
}
