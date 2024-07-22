package isel.pt.ps.projeto.models.registers

import java.time.LocalDateTime

/**
 * id_utilizador int references Utilizador(id),
 *                          id_obra int references Obra(id),
 *                          entrada timestamp not null default current_timestamp,
 *                          saida timestamp default null,
 *                          status varchar(64) check (status in ('pending', 'completed')),
 */
data class Register (
    val id: Int,
    val uid: Int,
    val oid: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val status: String
)