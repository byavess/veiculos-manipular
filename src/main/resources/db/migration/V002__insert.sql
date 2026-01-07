-- Padrão de nomenclatura de imagens: 0-{id} para principal, {ordem}-{id} para demais
INSERT INTO veiculo (id, marca, modelo, ano, km, preco, descricao, cor, motor, cambio, combustivel, urls_fotos, em_oferta, vendido, placa, info_venda) VALUES
(1, 'BMW', '320i', 2022, 48000, 290000.00,
 'BMW Série 3 320i M Sport 2022 - Sedan esportivo alemão com motor 2.0 TwinPower Turbo de 184cv. Pacote M Sport com aerofólio, rodas 19" M, para-choque esportivo e badges M. Interior com bancos esportivos em couro Dakota, painel Live Cockpit Professional 12,3", tela central 10,25" e volante M multifuncional. Performance: 0-100km/h em 7,1s, suspensão M Sport e freios azuis M.',
 'branco', '2.0 TwinPower Turbo', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-1-320i.webp","veiculos/1-1-bmw.webp","veiculos/2-1-bmw1.webp","veiculos/3-1-bmw2.webp"]', true, false, 'HIJ2J56', null),

(2, 'BMW', 'M4', 2023, 15000, 520000.00,
 'BMW M4 Competition 2023 - Coupé de alta performance com motor 3.0 biturbo de 510cv. Aceleração de 0-100km/h em 3,9s. Pacote M completo com suspensão adaptativa, diferencial M, freios carbocerâmicos e escape esportivo. Interior racing com bancos M carbon, painel digital M específico e volante M multifuncional.',
 'azul', '3.0 Biturbo', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-2-m4.webp"]', true, false, 'BMW1M23', null),

(3, 'BMW', 'X5', 2024, 8000, 680000.00,
 'BMW X5 xDrive40i M Sport 2024 - SUV de luxo com motor 3.0 turbo de 340cv. Tração integral xDrive, suspensão a ar adaptativa e modo off-road. Interior premium com bancos Vernasca em couro, tela central 14,9", painel digital 12,3" e teto panorâmico. Tecnologia: assistente de direção autônomo nível 2, estacionamento remoto e sistema de som Harman Kardon.',
 'preto', '3.0 Turbo', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-3-x5.webp"]', false, false, 'BMW2X56', null),

(4, 'Audi', 'A3', 2023, 29000, 260000.00,
 'Audi A3 Sportback 40 TFSI S line 2023 - Hatch premium com motor 2.0 TFSI de 204cv e câmbio S tronic de 7 marchas. Design esportivo com grade Singleframe preta, faróis Matrix LED e rodas 18" S line. Interior com bancos esportivos em couro, Virtual Cockpit Plus 12,3", tela MMI 10,1" e acabamento em alumínio. Performance: 0-100km/h em 6,8s, suspensão esportiva e direção progressiva.',
 'vermelho', '2.0 TFSI', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-4-a3.webp"]', true, false, 'CDE0F12', null),

(5, 'Audi', 'Q5', 2024, 12000, 420000.00,
 'Audi Q5 45 TFSI Quattro S line 2024 - SUV premium com motor 2.0 TFSI de 249cv e tração integral quattro. Design sofisticado com grade octogonal, faróis Matrix LED e rodas 20" S line. Interior com bancos em couro ventilados, Virtual Cockpit Plus, tela MMI touch 10,1" e Bang & Olufsen 3D sound. Tecnologia: assistente de estacionamento plus e suspensão adaptativa.',
 'cinza', '2.0 TFSI', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-5-q5.webp"]', false, false, 'AUD5Q78', null),

(6, 'Audi', 'TT', 2023, 18000, 380000.00,
 'Audi TT Coupé 45 TFSI Quattro 2023 - Coupé esportivo com motor 2.0 TFSI de 245cv. Design icônico com grade hexagonal, faróis LED Matrix e saídas de escape duplas. Interior esportivo com bancos S em couro/Alcantara, Virtual Cockpit 12,3" exclusivo TT e volante S multifuncional. Performance: 0-100km/h em 5,3s.',
 'vermelho', '2.0 TFSI', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-6-tt.webp"]', true, false, 'AUD6TT9', null),

(7, 'Chevrolet', 'Onix', 2025, 12000, 85000.00,
 'Chevrolet Onix 2025 Turbo - Hatch compacto mais vendido do Brasil, agora com motor turbo 1.0 de 116cv. Design moderno com grade dianteira black piano, faróis full LED e rodas de liga leve 16". Interior com central multimídia de 10,1", volante multifuncional e bancos em tecido premium. Oferece conectividade MyLink com 4G Wi-Fi, 6 airbags e controle de estabilidade. Ideal para uso urbano com excelente desempenho.',
 'vermelho', '1.0 Turbo', 'MANUAL', 'FLEX', '["veiculos/0-7-onix.webp"]', true, false, 'DEF2G45', null),

(8, 'Chevrolet', 'Tracker', 2024, 15000, 145000.00,
 'Chevrolet Tracker Premier 2024 - SUV compacto com motor turbo 1.0 de 116cv. Design arrojado com grade dupla, faróis LED e rodas 17". Interior premium com bancos em couro, tela 10,1", cluster digital 8" e carregador sem fio. Tecnologia: MyLink com Alexa integrada, OnStar e 6 airbags. Espaçoso e confortável.',
 'branco', '1.0 Turbo', 'AUTOMATICO', 'FLEX', '["veiculos/0-8-tracker.webp"]', false, false, 'CHV8TRK', null),

(9, 'Chevrolet', 'Celta', 2015, 85000, 28000.00,
 'Chevrolet Celta LT 2015 - Hatch compacto econômico. Motor 1.0 flex de 78cv, consumo de 14 km/l. Design simples e funcional. Interior básico com ar-condicionado, direção hidráulica e vidros elétricos. Ideal para primeiro carro ou uso urbano. Baixo custo de manutenção e peças acessíveis.',
 'prata', '1.0 Flex', 'MANUAL', 'FLEX', '["veiculos/0-9-celta.webp"]', false, true, 'CHV9CLT', 'Vendido em 05/01/2026 - Cliente João Silva'),

(10, 'Chevrolet', 'Corsa', 2010, 120000, 22000.00,
 'Chevrolet Corsa Sedan Classic 2010 - Sedan compacto com motor 1.0 flex de 78cv. Design clássico e atemporal. Interior simples com ar-condicionado e direção hidráulica. Porta-malas de 510L. Excelente para uso diário, muito econômico e confiável.',
 'branco', '1.0 Flex', 'MANUAL', 'FLEX', '["veiculos/0-10-corsa.webp"]', false, false, 'CHV0CRS', null),

(11, 'Chevrolet', 'Prisma', 2016, 72000, 48000.00,
 'Chevrolet Prisma LT 2016 - Sedan compacto com motor 1.4 flex de 106cv. Design moderno com grade dupla e faróis em máscara negra. Interior com MyLink 7", ar-condicionado e bancos em tecido. Porta-malas de 500L. Econômico e espaçoso para a família.',
 'prata', '1.4 Flex', 'MANUAL', 'FLEX', '["veiculos/0-11-prisma.webp"]', false, false, 'CHV1PRS', null),

(12, 'Chevrolet', 'Vectra', 2009, 145000, 25000.00,
 'Chevrolet Vectra GT 2.0 2009 - Sedan médio com motor 2.0 flex de 140cv. Design elegante e esportivo. Interior em couro, ar digital e som premium. Porta-malas de 510L. Perfeito para quem busca conforto e espaço. Manutenção acessível.',
 'preto', '2.0 Flex', 'MANUAL', 'FLEX', '["veiculos/0-12-vectra.webp"]', false, false, 'CHV2VCT', null),

(13, 'BYD', 'Dolphin', 2024, 5000, 150000.00,
 'BYD Dolphin 2024 - Hatch elétrico com autonomia de 291km. Motor elétrico de 95cv, aceleração de 0-100km/h em 10,9s. Design moderno wave-style, faróis LED e rodas 16". Interior tech com tela giratória 12,8", cluster digital 5" e ar-condicionado automático. Carregamento: 0-80% em 29min (DC) ou 10h (AC). Zero emissões.',
 'azul', 'Elétrico', 'AUTOMATICO', 'ELETRICO', '["veiculos/0-13-dolphin.webp"]', true, false, 'BYD3DLP', null),

(14, 'Alfa Romeo', 'Giulia', 2023, 22000, 380000.00,
 'Alfa Romeo Giulia Veloce 2.0 2023 - Sedan esportivo italiano com motor 2.0 turbo de 280cv. Design italiano com grade triângulo, faróis 3+3 LED e rodas 19". Interior luxuoso com bancos em couro e Alcantara, painel digital 12,3" e volante em couro perfurado. Performance: 0-100km/h em 5,7s, distribuição de peso 50/50.',
 'vermelho', '2.0 Turbo', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-14-giulia.webp"]', true, false, 'ALF4GLI', null),

(15, 'Acura', 'RDX', 2023, 18000, 420000.00,
 'Acura RDX A-Spec 2023 - SUV premium japonês com motor 2.0 turbo de 272cv. Design agressivo com grade Diamond Pentagon, faróis JewelEye LED e rodas 20" A-Spec. Interior premium com bancos Milano em couro, tela dual 10,2" e sistema de som ELS Studio 16 alto-falantes. Tecnologia AcuraWatch com 10 assistências de direção.',
 'branco', '2.0 Turbo', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-15-rdx.webp"]', false, false, 'ACU5RDX', null),

(16, 'Cadillac', 'XT5', 2023, 25000, 480000.00,
 'Cadillac XT5 Premium Luxury 2023 - SUV de luxo americano com motor 2.0 turbo de 237cv. Design imponente com grade vertical, faróis LED e rodas 20". Interior luxuoso com bancos Semi-Aniline em couro, tela 8" com Cadillac User Experience, painel digital e teto panorâmico. Sistema de som Bose Performance Series 8 alto-falantes.',
 'preto', '2.0 Turbo', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-16-xt5.webp"]', false, false, 'CAD6XT5', null),

(17, 'Chery', 'Tiggo 8', 2024, 8000, 185000.00,
 'Chery Tiggo 8 Pro Max 2024 - SUV de 7 lugares com motor 1.6 turbo de 197cv. Design robusto com grade Matrix, faróis LED e rodas 19". Interior premium com 3 fileiras, bancos em couro, tela 12,3" dupla (painel + central) e teto panorâmico. Tecnologia: 540° Camera, ADAS com 18 assistências e carregador sem fio.',
 'cinza', '1.6 Turbo', 'AUTOMATICO', 'GASOLINA', '["veiculos/0-17-tiggo8.webp"]', true, false, 'CHR7TG8', null),

(18, 'Citroën', 'C4 Cactus', 2021, 70000, 115000.00,
 'Citroën C4 Cactus Feel 1.6 2021 - SUV crossover com design exclusivo Airbumps nas laterais. Motor 1.6 flex de 118cv, câmbio automático de 6 marchas. Conforto excepcional com suspensão Progressive Hydraulic Cushions, bancos Advanced Comfort e isolamento acústico. Tecnologia: Head-up Display, tela 10"" e sistema de conectividade com 4G. Diferencial único no mercado.',
 'branco', '1.6 Flex', 'MANUAL', 'FLEX', '["veiculos/0-18-c4cactus.webp"]', false, false, 'VWX9X90', null);


-- Insert admin user for vehicle management
-- Username: admin
-- Password: admin123
INSERT INTO users (username, password, email, role, enabled,nome_completo)
VALUES ('admin', '$2a$10$n/.0Z8lXDOorwgUOc2WujOGgOLDXtnTh0MngWsdflili.6qWbd2SO', 'admin@veiculos.com', 'ROLE_ADMIN', true, 'Administrador');
