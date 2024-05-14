package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.User

interface UserRepository {
    /**
     * Get
     */
    fun getUsers(): List<User>  // Is will be updated to depend on construction
    fun getUserById(id: Int): User
    /**
     * Post
     */
    //fun login()
    fun addUser(id: Int, nome: String, email: String, morada: String) : User

    /**
     * Put
     */
    fun editUser(nome: String, email: String, morada: String) : User

    /**
     * Delete
     */
    fun deleteUser(id: Int) : User
}