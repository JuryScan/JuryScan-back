-- tabela de carteira para armazenar o saldo de créditos dos usuários
CREATE TABLE IF NOT EXISTS tb_carteira (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario UUID NOT NULL,
    saldo INT NOT NULL DEFAULT 0,
    data_criacao TIMESTAMP(3) DEFAULT now(),
    data_ultima_atualizacao TIMESTAMP(3) DEFAULT now(),

    CONSTRAINT fk_carteira_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id) ON DELETE CASCADE
);

-- tabela de transações para registrar compras e consumos de créditos
CREATE TABLE IF NOT EXISTS tb_transacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario UUID NOT NULL,
    tipo_transacao VARCHAR(10) NOT NULL,
    quantidade INT NOT NULL,
    stripe_checkout_id VARCHAR(255),
    data_criacao TIMESTAMP(3) DEFAULT now(),

    CONSTRAINT fk_transacao_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id) ON DELETE CASCADE,
    CONSTRAINT chk_tipo_transacao CHECK (tipo_transacao IN ('COMPRA', 'CONSUMO'))
);

CREATE TRIGGER TRG_atualizar_data_atualizacao_carteira
    BEFORE UPDATE ON tb_carteira
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_data_atualizacao();
