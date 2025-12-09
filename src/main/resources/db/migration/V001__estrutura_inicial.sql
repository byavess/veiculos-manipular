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
                         em_oferta BOOLEAN DEFAULT FALSE
);
