package isel.pt.ps.projeto.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSignUp(val name: String, val email: String, val password: String)
