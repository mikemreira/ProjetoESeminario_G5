package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.models.User
import isel.pt.ps.projeto.models.UserAndToken
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import org.springframework.stereotype.Component

@Component
class UsersService(private val usersRepository: UsersRepository) {
    fun getUsers(): List<User> = usersRepository.getUsers()

    fun signUp(
        name: String,
        email: String,
        password: String,
    ): User = usersRepository.signUp(name, email, password)

    fun signIn(
        email: String,
        password: String,
    ): UserAndToken {
        require(email.isNotEmpty() && password.isNotEmpty()) { "Email and password must not be empty" }
        return usersRepository.signIn(email, password)
    }
}
