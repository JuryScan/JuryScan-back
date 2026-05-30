package unicap.juryscan.enums;

public enum StatusLeadEnum {
    DISPONIVEL("disponivel"),
    ADQUIRIDO("adquirido"),
    EXPIRADO("expirado"),
    CANCELADO("cancelado");

    private String status;

    StatusLeadEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

