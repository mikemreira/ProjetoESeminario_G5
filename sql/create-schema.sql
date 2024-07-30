DROP TABLE IF EXISTS Registo;
DROP TABLE IF EXISTS Convite;
DROP TABLE IF EXISTS Papel;
DROP TABLE IF EXISTS Obra;
DROP TABLE IF EXISTS Token;
DROP TABLE IF EXISTS PasswordEsquecida;
DROP TABLE IF EXISTS UtilizadorImagem;
DROP TABLE IF EXISTS Utilizador;

create table if not exists Utilizador (
                            id int generated always as identity primary key,
                            password varchar(64),
                            nome varchar(64),
                            email varchar(64) unique,
                            morada varchar(64) default null
);

-- Ainda em Testes
CREATE TABLE IF NOT EXISTS UtilizadorImagem(
	id_utilizador int references Utilizador(id),
	thumbnail bytea default null,
	icon bytea default null,
	list bytea default null,
	Primary key (id_utilizador)
);

create table if not exists PasswordEsquecida (
        email varchar(64) references Utilizador(email),
		token_check VARCHAR(256),
		primary key (email, token_check)
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
                      status VARCHAR(64) check (status in ('deleted', 'recoverable', 'on going', 'completed')) default 'on going',
                      id_nfc varchar(64) default null
);

create table if not exists Papel (
                       id_utilizador int references Utilizador (id),
                       id_obra int references Obra (id),
                       papel varchar(64) check (papel in ('admin', 'funcionario')),
                       funcao varchar(64), check (funcao in ('Ajudante', 'Apontador', 'Armador de ferro', 'Arvorado', 'Calceteiro', 'Canalizador', 'Carpinteiro', 'Chefe de equipa', 'Condutor Manobrador', 'Diretor de serviços', 'Eletricista', 'Encarregado', 'Escriturário', 'Estucador',  'Ferramenteiro', 'Gruista', 'Impermeabilizador', 'Ladrilhador', 'Marteleiro', 'Montador de andaimes', 'Pedreiro', 'Pintor', 'Serralheiro', 'Servente', 'Soldador', 'Técnico de manutenção', 'Tubista', 'Outro')),
                       primary key (id_utilizador, id_obra)
);

create table if not exists Registo (
                         id serial,
                         id_utilizador int references Utilizador(id),
                         id_obra int references Obra(id),
                         entrada timestamp not null default current_timestamp,
                         saida timestamp default null,
                         status varchar(64) check (status in ('pending', 'completed', 'rejected', 'unfinished', 'unfinished_nfc')),
                         primary key (id, id_utilizador, id_obra)
);

CREATE TABLE if not exists Convite (
                        email VARCHAR(255) NOT NULL,
                        funcao VARCHAR(255),
                        status VARCHAR(10) NOT NULL DEFAULT 'pending' CHECK (status IN ('rejected', 'pending', 'accepted')),
                        id_obra INT NOT NULL,
                        papel VARCHAR(255) CHECK (papel IN ('admin', 'funcionario')),
                        PRIMARY KEY (email, id_obra),
                        CONSTRAINT fk_obra
                            FOREIGN KEY (id_obra)
                            REFERENCES Obra(id)
);

-- Create the trigger function
CREATE OR REPLACE FUNCTION handle_convite_status_update()
RETURNS TRIGGER AS $$
DECLARE
    user_id int;
BEGIN
    -- Check if the status is being updated to 'accepted'
    IF NEW.status = 'accepted' THEN
        -- Check if a user with the given email exists
        SELECT id INTO user_id FROM Utilizador WHERE email = NEW.email;

        INSERT INTO Papel (id_utilizador, id_obra, papel, funcao)
        VALUES (user_id, NEW.id_obra, NEW.papel, NEW.funcao);


    -- Check if the status is being updated to 'rejected'
    ELSIF NEW.status = 'rejected' THEN
        -- Delete the row from Convite
        DELETE FROM Convite WHERE email = NEW.email AND id_obra = NEW.id_obra;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger
CREATE OR REPLACE TRIGGER convite_status_update_trigger
AFTER UPDATE ON Convite
FOR EACH ROW
EXECUTE FUNCTION handle_convite_status_update();

-- Trigger to delete register rejected
CREATE OR REPLACE FUNCTION handle_registo_status_update()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'rejected' THEN
        DELETE FROM Registo WHERE id = NEW.id AND id_utilizador = NEW.id_utilizador AND id_obra = NEW.id_obra;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER registo_status_update_trigger
AFTER UPDATE ON Registo
FOR EACH ROW
WHEN (OLD.status IS DISTINCT FROM NEW.status AND NEW.status = 'rejected')
EXECUTE FUNCTION handle_registo_status_update();

-- Trigger foi deletion
CREATE OR REPLACE FUNCTION delete_related_records() RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM Registo WHERE id_obra = OLD.id;
    DELETE FROM Papel WHERE id_obra = OLD.id;
    DELETE FROM Convite WHERE id_obra = OLD.id;
    DELETE FROM Obra WHERE id = OLD.id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trigger_delete_related_records
AFTER UPDATE OF status ON Obra
FOR EACH ROW
WHEN (NEW.status = 'deleted')
EXECUTE FUNCTION delete_related_records();


-- Ainda em testes (Daqui para baixo)
CREATE OR REPLACE FUNCTION create_utilizador_imagem()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO UtilizadorImagem (id_utilizador) VALUES (NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_utilizador_insert
AFTER INSERT ON Utilizador
FOR EACH ROW
EXECUTE FUNCTION create_utilizador_imagem();