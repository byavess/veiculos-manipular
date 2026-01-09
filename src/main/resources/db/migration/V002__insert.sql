-- Insert admin user for vehicle management
-- Username: admin
-- Password: admin123
INSERT INTO users (username, password, email, role, enabled,nome_completo)
VALUES ('admin', '$2a$10$n/.0Z8lXDOorwgUOc2WujOGgOLDXtnTh0MngWsdflili.6qWbd2SO', 'admin@veiculos.com', 'ROLE_ADMIN', true, 'Administrador');
