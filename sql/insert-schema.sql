-- Inserting data into Utilizador table
INSERT INTO Utilizador (nome, pass, email, morada)
VALUES
    ('John Doe', '123','john@example.com', '123 Main Street'),
    ('Jane Smith', '123','jane@example.com', '456 Elm Street'),
    ('Michael Brown', '123','michael@example.com', '789 Oak Avenue'),
    ('Emily Johnson', '123','emily@example.com', '101 Pine Road'),
    ('Daniel Lee', '123','daniel@example.com', '222 Maple Lane');

-- Inserting data into Obra table
INSERT INTO Obra (nome, localização, descrição, data_inicio, data_fim, status)
VALUES
    ('Construction Project', 'City Center', 'Building a new commercial complex', '2024-01-01', '2024-12-31', 'on going'),
    ('Renovation Project', 'Suburb', 'Renovating an old library', '2024-03-15', '2024-09-30', 'completed');

-- Inserting data into Papel table
INSERT INTO Papel (id_utilizador, id_obra, papel)
VALUES
    (1, 1, 'admin'),
    (2, 1, 'funcionario'),
    (2, 2, 'admin'),
    (3, 2, 'funcionario'),
    (4, 2, 'admin'),
    (5, 1, 'funcionario');

-- Inserting data into Registo table
INSERT INTO Registo (id, id_utilizador, id_obra, entrada, saida, data_entrada)
VALUES
    (1, 1, 1, '08:00:00', '17:00:00', '2024-05-13'),
    (2, 2, 1, '09:00:00', '18:00:00', '2024-05-13'),
    (3, 1, 2, '10:00:00', '16:00:00', '2024-05-13'),
    (4, 3, 1, '08:30:00', '16:30:00', '2024-05-13'),
    (5, 4, 2, '09:30:00', '17:30:00', '2024-05-13'),
    (6, 5, 1, '07:30:00', '16:30:00', '2024-05-13');