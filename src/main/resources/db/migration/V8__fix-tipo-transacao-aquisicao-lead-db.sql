-- Corrige a coluna tipo_transacao para comportar o tipo 'AQUISICAO_LEAD' (14 caracteres),
-- gravado por LeadService.acquireLead() ao adquirir um lead no marketplace.
-- O schema original (V3) definia VARCHAR(10) e um CHECK restrito a ('COMPRA','CONSUMO'),
-- o que fazia toda aquisição de lead falhar em PostgreSQL (estouro de tamanho + violação de CHECK).

-- 1) Amplia o tamanho da coluna para acomodar todos os valores do enum TipoTransacaoEnum.
ALTER TABLE tb_transacao
    ALTER COLUMN tipo_transacao TYPE VARCHAR(20);

-- 2) Substitui o CHECK para incluir o tipo de aquisição de lead.
ALTER TABLE tb_transacao
    DROP CONSTRAINT IF EXISTS chk_tipo_transacao;

ALTER TABLE tb_transacao
    ADD CONSTRAINT chk_tipo_transacao CHECK (tipo_transacao IN ('COMPRA', 'CONSUMO', 'AQUISICAO_LEAD'));
