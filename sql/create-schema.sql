create table Utilizador (
    id int primary key,
    nome varchar(64),
    email varchar(64) unique,
    morada varchar(64),
    foto bytea
);

create table Obra (
    id int primary key,
    nome varchar(64) not null,
    localização varchar(64) not null,
    descrição varchar(260),
    data_inicio date not null default current_date,
    data_fim date default null,
    foto bytea,
    status VARCHAR(64) check (status in ('deleted', 'recoverable', 'on going', 'completed'))
);

create table Papel (
    id_utilizador int references Utilizador (id),
    id_obra int references Obra (id),
    papel varchar(64) check (papel in ('admin', 'funcionario')),
    primary key (id_utilizador, id_obra)
);

create table Registo (
    id int,
    id_utilizador int references Utilizador(id),
    id_obra int references Obra(id),
    entrada time not null default current_time,
    saida time default null,
    data_entrada date not null default current_date,
    primary key (id, id_utilizador, id_obra)
);