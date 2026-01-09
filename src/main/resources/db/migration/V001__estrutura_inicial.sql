CREATE TABLE marca (
                       id BIGSERIAL PRIMARY KEY,
                       nome VARCHAR(100) UNIQUE NOT NULL,
                       valor VARCHAR(100)
);

CREATE TABLE modelo (
                        id BIGSERIAL PRIMARY KEY,
                        modelo VARCHAR(400) UNIQUE NOT NULL,
                        marca_id BIGINT NOT NULL,
                        CONSTRAINT fk_modelo_marca FOREIGN KEY (marca_id) REFERENCES marca(id)
);
CREATE TABLE veiculo (
                         id BIGSERIAL PRIMARY KEY,
                         marca_id BIGINT,
                         modelo_id BIGINT,
                         ano INT,
                         km INT DEFAULT 0,
                         preco DECIMAL(10,2),
                         descricao TEXT,
                         cor VARCHAR(50),
                         motor VARCHAR(50),
                         cambio VARCHAR(20),
                         combustivel VARCHAR(20),
                         urls_fotos TEXT,
                         em_oferta BOOLEAN DEFAULT FALSE,
                         vendido BOOLEAN DEFAULT FALSE,
                         placa VARCHAR(20) UNIQUE NOT NULL,
                         info_venda TEXT,
                         tipo_veiculo VARCHAR(20),
                         CONSTRAINT fk_veiculo_marca FOREIGN KEY (marca_id) REFERENCES marca(id),
                         CONSTRAINT fk_veiculo_modelo FOREIGN KEY (modelo_id) REFERENCES modelo(id)
);
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       nome_completo VARCHAR(300)
);

CREATE TABLE IF NOT EXISTS token (
                                     id BIGSERIAL PRIMARY KEY,
                                     token VARCHAR(512) NOT NULL UNIQUE,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id)
    );