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
