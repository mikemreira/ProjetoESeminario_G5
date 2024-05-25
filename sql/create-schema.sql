DROP TABLE IF EXISTS Registo;
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
                       primary key (id_utilizador, id_obra)
);

create table if not exists Registo (
                         id int,
                         id_utilizador int references Utilizador(id),
                         id_obra int references Obra(id),
                         entrada time not null default current_time,
                         saida time default null,
                         data_entrada date not null default current_date,
                         primary key (id, id_utilizador, id_obra)
);