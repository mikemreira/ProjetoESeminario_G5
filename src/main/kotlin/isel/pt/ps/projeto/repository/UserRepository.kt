package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.User
import isel.pt.ps.projeto.models.UserAndToken

interface UserRepository {
    /**
     * Get
     */
    fun getUsers(): List<User> // Is will be updated to depend on construction

    fun getUserById(id: Int): User

    /**
     * Post
     */
    fun signUp(
        nome: String,
        email: String,
        pass: String,
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
