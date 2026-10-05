-- V7__alter_notificacoes_table.sql

-- 1. Renomear a coluna data_envio para data_envio_programada
ALTER TABLE notificacoes
    RENAME COLUMN data_envio TO data_envio_programada;

-- 2. Alterar a coluna data_envio_programada para ser NULLABLE
ALTER TABLE notificacoes
    ALTER COLUMN data_envio_programada DROP NOT NULL;

-- 3. Adicionar a nova coluna data_envio_realizada (nullable)
ALTER TABLE notificacoes
    ADD COLUMN data_envio_realizada TIMESTAMP NULL;