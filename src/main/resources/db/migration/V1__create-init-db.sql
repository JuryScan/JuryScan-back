-- Criação das tabelas =================================
CREATE TABLE IF NOT EXISTS tb_usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_completo VARCHAR(100) NOT NULL,
    email VARCHAR(30) NOT NULL UNIQUE,
    telefone VARCHAR(11) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    numero_oab VARCHAR(9),
    experiencia VARCHAR(15),
    email_recuperacao VARCHAR(30),
    data_nascimento DATE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    id_endereco UUID,
    status VARCHAR(10) NOT NULL DEFAULT 'ATIVO',
    email_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    descricao VARCHAR(300),
    tipo_usuario VARCHAR(10) NOT NULL,
    data_criacao TIMESTAMP(3) DEFAULT now(),
    data_ultima_atualizacao TIMESTAMP(3) DEFAULT now(),

    CONSTRAINT chk_tipo_usuario CHECK (tipo_usuario IN ('COMUM', 'ADVOGADO', 'ADMIN')),
    CONSTRAINT chk_tipo_status CHECK (status IN ('ATIVO', 'INATIVO'))
    );

CREATE TABLE IF NOT EXISTS tb_endereco (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo_endereco VARCHAR(15) NOT NULL,
    logradouro VARCHAR(80) NOT NULL,
    cidade VARCHAR(80) NOT NULL,
    bairro VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    cep VARCHAR(8) NOT NULL,

    CONSTRAINT chk_tipo_endereco CHECK (tipo_endereco IN ('RESIDENCIAL', 'COMERCIAL'))
    );

CREATE TABLE IF NOT EXISTS tb_analise (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario UUID NOT NULL,
    titulo VARCHAR(30) NOT NULL,
    descricao_geral TEXT NOT NULL,
    data_criacao TIMESTAMP(3) DEFAULT now(),

    CONSTRAINT fk_analise_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS tb_falha (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_analise UUID NOT NULL,
    titulo VARCHAR(30) NOT NULL,
    severidade VARCHAR(10) NOT NULL,
    descricao TEXT NOT NULL,
    confianca REAL NOT NULL,
    sugestao_correcao TEXT NOT NULL,

    CONSTRAINT fk_falha_analise FOREIGN KEY (id_analise) REFERENCES tb_analise (id) ON DELETE CASCADE,

    CONSTRAINT chk_severidade CHECK (severidade IN ('ALTA', 'MEDIA', 'BAIXA', 'INFO'))
    );

-- Funções auxiliares ======================================
CREATE OR REPLACE FUNCTION atualizar_data_atualizacao()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.data_ultima_atualizacao := now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criação dos triggers ====================================
CREATE TRIGGER TRG_atualizar_data_atualizacao
    BEFORE UPDATE ON tb_usuario
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_data_atualizacao();