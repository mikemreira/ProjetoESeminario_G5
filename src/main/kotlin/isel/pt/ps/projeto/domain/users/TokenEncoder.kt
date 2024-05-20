package isel.pt.ps.projeto.domain.users

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
