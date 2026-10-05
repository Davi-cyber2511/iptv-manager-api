CREATE TABLE notificacoes (
   id VARCHAR(36) NOT NULL,
   cliente_id VARCHAR(36) NOT NULL,
   tipo_notificacao VARCHAR( 50) NOT NULL,
    canal_notificacao VARCHAR(50) NOT NULL,
    data_vencimento_referencia DATE NOT NULL,
    data_envio TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    mensagem_enviada TEXT,
    detalhes_erro TEXT,

    CONSTRAINT pk_notificacoes
        PRIMARY KEY (id),

    CONSTRAINT fk_notificacoes_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes (id)
        ON DELETE CASCADE,

    CONSTRAINT uk_notificacao_cliente_canal_vencimento
        UNIQUE (
            cliente_id,
            tipo_notificacao,
            canal_notificacao,
            data_vencimento_referencia
        )
);