package unicap.juryscan.enums;

public enum TipoUserEnum {
    COMUM("comum"), ADMIN("admin"), ADVOGADO("advogado");

    private String role;

    TipoUserEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
