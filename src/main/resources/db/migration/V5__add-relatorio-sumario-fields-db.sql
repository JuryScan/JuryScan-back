-- Adicionar campos de relatório e sumário à tabela tb_analise
ALTER TABLE tb_analise
ADD COLUMN relatorio_sumario_juridico TEXT,
ADD COLUMN sumario TEXT;

