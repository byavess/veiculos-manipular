CREATE TABLE veiculo (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         marca VARCHAR(100),
                         modelo VARCHAR(100),
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
                         info_venda TEXT
);
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       nome_completo VARCHAR(300)
);

CREATE TABLE IF NOT EXISTS token (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     token VARCHAR(512) NOT NULL UNIQUE,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id)
    );