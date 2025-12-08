CREATE TABLE veiculo (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         marca VARCHAR(100),
                         modelo VARCHAR(100),
                         ano INT,
                         preco DECIMAL(10,2),
                         descricao TEXT,
                         cor VARCHAR(50),
                         urls_fotos TEXT
);
