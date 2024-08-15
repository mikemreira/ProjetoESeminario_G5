-- Insert data into Utilizador
INSERT INTO Utilizador (password, nome, email, morada) VALUES
('password123', 'Test User 1', 'test1@example.com', '123 Maple Street'),
('password456', 'Test User 2', 'test2@example.com', '456 Oak Avenue'),
('password789', 'Test User 3', 'test3@example.com', NULL);

-- Insert data into UtilizadorImagem
INSERT INTO UtilizadorImagem (id_utilizador, thumbnail, icon, list) VALUES
(1, NULL, NULL, NULL),
(2, NULL, NULL, NULL);

-- Insert data into PasswordEsquecida
INSERT INTO PasswordEsquecida (email, token_check) VALUES
('test1@example.com', 'token123'),
('test2@example.com', 'token456');

-- Insert data into Token
INSERT INTO Token (token_validation, id_utilizador, created_at, last_used_at) VALUES
('token_abc', 1, 1627916400, 1628002800),
('token_def', 2, 1627916400, 1628002800);

-- Insert data into Obra
INSERT INTO Obra (nome, localizacao, descricao, data_inicio, data_fim, foto, status, id_nfc) VALUES
('Obra A', 'Lisbon', 'Construction of a new building', '2024-01-01', '2024-12-31', NULL, 'on going', 'nfc_001'),
('Obra B', 'Porto', 'Renovation of an old bridge', '2024-02-01', NULL, NULL, 'on going', 'nfc_002');

-- Insert data into Papel
INSERT INTO Papel (id_utilizador, id_obra, papel, funcao) VALUES
(1, 1, 'admin', 'Diretor de serviços'),
(2, 2, 'funcionario', 'Pedreiro');

-- Insert data into Registo
-- Insert multiple data into Registo in a single statement ensuring correct user-to-obra relationships
INSERT INTO Registo (id_utilizador, id_obra, entrada, saida, status) VALUES
(1, 1, '2024-07-06 08:00:00', NULL, 'pending'),  -- Pending record for id_utilizador 1 in obra 1
(2, 2, '2024-07-07 08:00:00', NULL, 'pending'),  -- Pending record for id_utilizador 2 in obra 2
(1, 1, '2024-07-08 08:00:00', NULL, 'unfinished'),  -- Unfinished record for id_utilizador 1 in obra 1
(2, 2, '2024-07-09 08:00:00', NULL, 'unfinished'),  -- Unfinished record for id_utilizador 2 in obra 2
(1, 1, '2024-07-10 08:00:00', NULL, 'unfinished_nfc'),  -- Unfinished_nfc record for id_utilizador 1 in obra 1
(2, 2, '2024-07-11 08:00:00', NULL, 'unfinished_nfc');  -- Unfinished_nfc record for id_utilizador 2 in obra 2


-- Insert data into Convite
INSERT INTO Convite (email, funcao, status, id_obra, papel) VALUES
('test2@example.com', 'Gruista', 'pending', 1, 'funcionario'),
('test1@example.com', 'Escriturário', 'accepted', 2, 'admin');
