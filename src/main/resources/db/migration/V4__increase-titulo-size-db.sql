-- Aumentar o tamanho do campo titulo para acomodar títulos maiores vindo da IA
ALTER TABLE tb_analise
ALTER COLUMN titulo TYPE VARCHAR(500);

ALTER TABLE tb_falha
ALTER COLUMN titulo TYPE VARCHAR(500);

