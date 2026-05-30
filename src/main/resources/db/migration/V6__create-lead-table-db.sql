-- tabela de leads para conectar clientes com advogados
CREATE TABLE IF NOT EXISTS tb_lead (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario_cliente UUID NOT NULL,
    id_analise UUID NOT NULL,
    id_advogado UUID,
    status VARCHAR(15) NOT NULL DEFAULT 'DISPONIVEL',
    custo_creditos INT NOT NULL DEFAULT 10,
    data_criacao TIMESTAMP(3) DEFAULT now(),
    data_aquisicao TIMESTAMP(3),
    data_expiracao TIMESTAMP(3),

    CONSTRAINT fk_lead_usuario_cliente FOREIGN KEY (id_usuario_cliente) REFERENCES tb_usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_lead_analise FOREIGN KEY (id_analise) REFERENCES tb_analise (id) ON DELETE CASCADE,
    CONSTRAINT fk_lead_advogado FOREIGN KEY (id_advogado) REFERENCES tb_usuario (id) ON DELETE SET NULL,
    CONSTRAINT chk_status_lead CHECK (status IN ('DISPONIVEL', 'ADQUIRIDO', 'EXPIRADO', 'CANCELADO')),
    CONSTRAINT uq_lead_analise UNIQUE (id_analise)
);

-- índices para melhorar performance de consultas
CREATE INDEX idx_lead_status ON tb_lead (status);
CREATE INDEX idx_lead_advogado ON tb_lead (id_advogado);
CREATE INDEX idx_lead_cliente ON tb_lead (id_usuario_cliente);
CREATE INDEX idx_lead_data_criacao ON tb_lead (data_criacao DESC);
