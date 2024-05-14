package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.models.User
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import org.springframework.stereotype.Component

@Component
class UsersService(private val usersRepository: UsersRepository) {
    fun getUsers(): List<User> = usersRepository.getUsers()
}
