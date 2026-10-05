ALTER TABLE clientes
ALTER COLUMN data_ultimo_aviso_email
    TYPE DATE
    USING data_ultimo_aviso_email::DATE;