package unicap.juryscan.exception;

public class RecaptchaException extends RuntimeException{
    public RecaptchaException(String message){
        super(message);
    }
}