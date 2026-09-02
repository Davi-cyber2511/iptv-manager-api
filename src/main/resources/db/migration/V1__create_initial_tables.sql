CREATE TABLE clientes (
                          id VARCHAR(36) NOT NULL,
                          nome VARCHAR(255) NOT NULL,
                          telefone VARCHAR(255) NOT NULL,
                          servidor_iptv VARCHAR(255) NOT NULL,
                          observacoes TEXT,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP,

                          CONSTRAINT pk_clientes PRIMARY KEY (id)
);

CREATE TABLE renovacoes (
                            id VARCHAR(36) NOT NULL,
                            cliente_id VARCHAR(36) NOT NULL,
                            data_inicio DATE NOT NULL,
                            duracao_quantidade INTEGER NOT NULL,
                            duracao_unidade VARCHAR(20) NOT NULL,
                            data_vencimento DATE NOT NULL,
                            valor NUMERIC(10, 2),
                            created_at TIMESTAMP NOT NULL,

                            CONSTRAINT pk_renovacoes PRIMARY KEY (id),

                            CONSTRAINT fk_renovacoes_cliente
                                FOREIGN KEY (cliente_id)
                                    REFERENCES clientes (id)
);