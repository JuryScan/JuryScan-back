package unicap.juryscan.enums;

import lombok.Getter;

@Getter
public enum SeveridadeFalhaEnum {
    ALTA("alta"), MEDIA("media"), BAIXA("baixa"), INFO("info");

    private String severidade;

    SeveridadeFalhaEnum(String severidade) {
        this.severidade = severidade;
    }

}
