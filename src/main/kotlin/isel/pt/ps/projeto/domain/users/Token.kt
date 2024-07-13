package isel.pt.ps.projeto.domain.users

import kotlinx.datetime.Instant

class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int
)
