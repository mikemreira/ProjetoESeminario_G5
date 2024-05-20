package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.domain.users.UsersDomain
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.models.users.UserAndToken
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success
import org.springframework.stereotype.Component

sealed class UserError {
    object UserAlreadyExists : UserError()

    object InsecurePassword : UserError()
}

sealed class TokenError {
    object UserOrPasswordAreInvalid : TokenError()
}
typealias UserResult = Either<UserError, User>
typealias TokenResult = Either<TokenError, UserAndToken>

@Component
class UsersService(
    private val usersRepository: UsersRepository,
    private val usersDomain: UsersDomain,
) {
    fun getUsers(): List<User> = usersRepository.getUsers()

    fun signUp(
        name: String,
        email: String,
        password: String,
    ): UserResult {
        if (!usersDomain.isSafePassword(password)) {
            return failure(UserError.InsecurePassword)
        }
        val passwordValidationInfo = usersDomain.createPasswordValidationInformation(password)
        return success(usersRepository.signUp(name, email, passwordValidationInfo))
    }

    fun signIn(
        email: String,
        password: String,
    ): TokenResult {
        if (email.isBlank() || password.isBlank()) {
            return failure(TokenError.UserOrPasswordAreInvalid)
        }
        // require(email.isNotEmpty() && password.isNotEmpty()) { "Email and password must not be empty" }
        return success(usersRepository.signIn(email, password))
    }

    fun getUserByToken(token: String): User? {
        return usersRepository.getUserByToken(token)
    }
}
