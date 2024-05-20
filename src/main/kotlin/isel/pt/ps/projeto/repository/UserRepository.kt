package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.models.users.UserAndToken

interface UserRepository {
    /**
     * Get
     */
    fun getUsers(): List<User> // Is will be updated to depend on construction

    fun getUserById(id: Int): User

    fun getUserByToken(token: String): User?

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
        pass: String,
    ): UserAndToken

    /**
     * Put
     */
    fun editUser(
        nome: String,
        email: String,
        morada: String,
    ): User

    /**
     * Delete
     */
    fun deleteUser(id: Int): User
}
