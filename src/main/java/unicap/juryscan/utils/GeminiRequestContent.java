package unicap.juryscan.utils;

import com.google.genai.types.Schema;

import java.util.Map;

public class GeminiRequestContent {
    public static String PROMPT =
        """
        Você é um especialista em direito previdenciário brasileiro e sua função é analisar extratos do CNIS (Cadastro Nacional de Informações Sociais) para identificar problemas, inconsistências e oportunidades de melhoria para o segurado. Use a estrutura que foi fornecida no schema para formatar sua resposta. Utilize como base um CNIS fictício, mas que contenha erros comuns encontrados em CNIS reais. Utilize como titlo da análise um texto que caracterize o arquivo que estou enviando, para saber que você está conseguindo ter acesso ao documento que estou enviando.
        """;

    public static Schema buildSchema() {
        return Schema.builder()
                .type("object")
                .properties(Map.of(
                        "titulo", Schema.builder().type("string").build(),
                        "descricaoGeral", Schema.builder().type("string").build(),
                        "failures", Schema.builder()
                                .type("array")
                                .items(Schema.builder()
                                        .type("object")
                                        .properties(Map.of(
                                                "titulo", Schema.builder().type("string").maxLength(50L).build(),
                                                "severidade", Schema.builder().type("string").enum_("ALTA", "MEDIA", "BAIXA", "INFO").build(),
                                                "descricao", Schema.builder().type("string").build(),
                                                "sugestaoCorrecao", Schema.builder().type("string").build(),
                                                "confianca", Schema.builder().type("number").build()
                                        ))
                                        .required("titulo", "severidade", "descricao", "sugestaoCorrecao")
                                        .build()
                                )
                                .build()
                ))
                .required("titulo", "descricaoGeral", "failures")
                .build();
    }

}
