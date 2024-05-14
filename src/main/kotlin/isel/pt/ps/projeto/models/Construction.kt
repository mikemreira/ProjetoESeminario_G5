package isel.pt.ps.projeto.models

import kotlinx.datetime.LocalDate


/**
 * id int primary key,
 *     nome varchar(64) not null,
 *     localização varchar(64) not null,
 *     descrição varchar(260),
 *     data_inicio date not null default current_date,
 *     data_fim date default null,
 *     foto bytea default null,
 *     status
 */

data class Construction(
    val oid: Int,
    val nome: String,
    val localizacao: String,
    val descricao: String,
    val data_inicio: LocalDate,
    val data_fim: LocalDate? = null,
    val status: String
)