-- A coluna tipo_transacao foi criada na V3 como VARCHAR(10) com CHECK restrito a ('COMPRA', 'CONSUMO').
-- O enum TipoTransacaoEnum passou a incluir AQUISICAO_LEAD (14 caracteres), persistido em
-- LeadService.acquireLead(). Sem este ajuste, toda aquisicao de lead falha no PostgreSQL: o valor
-- estoura o tamanho da coluna e ainda viola a constraint de CHECK.
ALTER TABLE tb_transacao ALTER COLUMN tipo_transacao TYPE VARCHAR(20);

ALTER TABLE tb_transacao DROP CONSTRAINT IF EXISTS chk_tipo_transacao;

ALTER TABLE tb_transacao ADD CONSTRAINT chk_tipo_transacao
    CHECK (tipo_transacao IN ('COMPRA', 'CONSUMO', 'AQUISICAO_LEAD'));
