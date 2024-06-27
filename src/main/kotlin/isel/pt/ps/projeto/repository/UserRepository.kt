package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.domain.users.Token
import isel.pt.ps.projeto.domain.users.TokenValidationInfo
import isel.pt.ps.projeto.models.users.SimpleUser
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.models.users.UserAndToken
import kotlinx.datetime.Instant

interface UserRepository {
    /**
     * Get
     */
    fun getUsers(): List<User> // Is will be updated to depend on construction

    fun getUserById(id: Int): User

    fun getUserByToken(token: String): User?

    fun getTokenByTokenValidationInfo(token: TokenValidationInfo): Pair<User, Token>?

    fun updateTokenLastUsed(token: Token, now: Instant)

    fun getUserByEmail(email: String): User?

    fun checkUserByEmail(email: String): Boolean
    /**
     * Post
     */
    fun signUp(
        nome: String,
        email: String,
        pass: PasswordValidationInfo,
    ): User

    fun signIn(
        email: String,
    ): UserAndToken

    fun signOut(
        tokenValidationInfo: TokenValidationInfo
    )

    fun createToken(
        token: Token,
        maxTokens: Int
    )
    /**
     * Put
     */
    fun editUser(
        id: Int,
        nome: String,
        morada: String?,
        foto: String?,
    ): SimpleUser

    /**
     * Delete
     */
    fun deleteUser(id: Int): User
}
