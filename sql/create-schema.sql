DROP TABLE IF EXISTS Registo;
DROP TABLE IF EXISTS Convite;
DROP TABLE IF EXISTS Papel;
DROP TABLE IF EXISTS Obra;
DROP TABLE IF EXISTS Token;
DROP TABLE IF EXISTS Utilizador;

create table if not exists Utilizador (
                            id int generated always as identity primary key,
                            password varchar(64),
                            nome varchar(64),
                            email varchar(64) unique,
                            morada varchar(64) default null,
                            foto bytea default null
);

create table if not exists Token(
    token_validation VARCHAR(256) primary key,
    id_utilizador int references utilizador(id),
    created_at bigint not null,
    last_used_at bigint not null
);

create table if not exists Obra (
                      id int generated always as identity primary key,
                      nome varchar(64) not null,
                      localização varchar(64) not null,
                      descrição varchar(260),
                      data_inicio date not null default current_date,
                      data_fim date default null,
                      foto bytea default null,
                      status VARCHAR(64) check (status in ('deleted', 'recoverable', 'on going', 'completed')) default 'on going'
);

create table if not exists Papel (
                       id_utilizador int references Utilizador (id),
                       id_obra int references Obra (id),
                       papel varchar(64) check (papel in ('admin', 'funcionario')),
                       funcao varchar(64), check (funcao in ('Ajudante', 'Apontador', 'Armador de ferro', 'Arvorado', 'Calceteiro', 'Canalisador', 'Carpinteiro', 'Chefe de equipa', 'Condutor Manobrador', 'Diretor de serviços', 'Eletricista', 'Encarregado', 'Escriturário', 'Estucador',  'Ferramenteiro', 'Gruista', 'Impermiabilizador', 'Ladrilhador', 'Marteleiro', 'Montador de andaimes', 'Pedreiro', 'Pintor', 'Serralheiro', 'Servente', 'Soldador', 'Técnico de manutenção', 'Tubista', 'Outro')),
                       primary key (id_utilizador, id_obra)
);

create table if not exists Registo (
                         id serial,
                         id_utilizador int references Utilizador(id),
                         id_obra int references Obra(id),
                         entrada timestamp not null default current_timestamp,
                         saida timestamp default null,
                         status varchar(64) check (status in ('pending', 'completed', 'rejected')),
                         primary key (id, id_utilizador, id_obra)
);

CREATE TABLE if not exists Convite (
                        id_utilizador INT NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        funcao VARCHAR(255),
                        status VARCHAR(10) NOT NULL DEFAULT 'pending' CHECK (status IN ('rejected', 'pending', 'accepted')),
                        id_obra INT NOT NULL,
                        PRIMARY KEY (id_utilizador, id_obra),
                        CONSTRAINT fk_obra
                            FOREIGN KEY (id_obra)
                            REFERENCES Obra(id),
                        CONSTRAINT fk_utilizador
                            FOREIGN KEY (id_utilizador)
                            REFERENCES Utilizador(id)
);

CREATE OR REPLACE FUNCTION handle_convite_status_update()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'accepted' THEN
        INSERT INTO Papel (id_utilizador, id_obra, papel, funcao)
        VALUES (NEW.id_utilizador, NEW.id_obra, 'funcionario', NEW.funcao);
        DELETE FROM Convite WHERE id_utilizador = NEW.id_utilizador AND id_obra = NEW.id_obra;
    ELSIF NEW.status = 'rejected' THEN
        DELETE FROM Convite WHERE id_utilizador = NEW.id_utilizador AND id_obra = NEW.id_obra;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER convite_status_update_trigger
AFTER UPDATE ON Convite
FOR EACH ROW
WHEN (OLD.status IS DISTINCT FROM NEW.status)
EXECUTE FUNCTION handle_convite_status_update();

CREATE OR REPLACE FUNCTION handle_registo_status_update()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'rejected' THEN
        DELETE FROM Registo WHERE id = NEW.id AND id_utilizador = NEW.id_utilizador AND id_obra = NEW.id_obra;
    END IF;
    RETURN NULL; -- Returning NULL prevents any operation on the record as it has been deleted.
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER registo_status_update_trigger
AFTER UPDATE ON Registo
FOR EACH ROW
WHEN (OLD.status IS DISTINCT FROM NEW.status AND NEW.status = 'rejected')
EXECUTE FUNCTION handle_registo_status_update();