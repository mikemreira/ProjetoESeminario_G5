package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.domain.users.Token
import isel.pt.ps.projeto.domain.users.UsersDomain
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.models.users.UserAndToken
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component

data class TokenExternalInfo(
    val tokenValue: String,
    val tokenExpiration: Instant
)

sealed class UserError {
    object UserAlreadyExists : UserError()
    object InsecurePassword : UserError()
    object InvalidEmail : UserError()
}

sealed class TokenError {
    object UserOrPasswordAreInvalid : TokenError()
}

typealias UserResult = Either<UserError, User>

typealias TokenResult = Either<TokenError, TokenExternalInfo>

@Component
class UsersService(
    private val usersRepository: UsersRepository,
    private val usersDomain: UsersDomain,
    private val clock: Clock,
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
        if (checkUserByEmail(email)) {
            return failure(UserError.InvalidEmail)
        }

        val passwordValidationInfo = usersDomain.createPasswordValidationInformation(password)
        return success(usersRepository.signUp(name, email, passwordValidationInfo))
    }

    fun signIn(
        email: String,
        password: String,
    ): TokenResult {
        require(email.isNotEmpty() && password.isNotEmpty()) { return failure(TokenError.UserOrPasswordAreInvalid) }
        val user: User = usersRepository.getUserByEmail(email) ?: return failure(TokenError.UserOrPasswordAreInvalid)
        if (!usersDomain.validatePassword(password, user.passwordValidation)) {
            return failure(TokenError.UserOrPasswordAreInvalid)
        }
        val tokenValue = usersDomain.generateTokenValue()
        println("1 :"+tokenValue)
        val now = clock.now()
        val newToken = Token(
            usersDomain.createTokenValidationInformation(tokenValue),
            user.id,
            createdAt = now,
            lastUsedAt = now
        )
        println("2 :"+newToken.tokenValidationInfo.validationInfo)

        usersRepository.createToken(newToken, usersDomain.maxNumberOfTokensPerUser)
        return Either.Right(
            TokenExternalInfo(
                tokenValue,
                usersDomain.getTokenExpiration(newToken)
            )
        )
    }

    fun signOut(token: String): Boolean {
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        usersRepository.signOut(tokenValidationInfo)
        return true
    }

    fun getUserByToken(token: String): User? {
        return usersRepository.getUserByToken(token)
    }

    fun checkUserByEmail(email: String): Boolean {
        return usersRepository.checkUserByEmail(email)
    }
}
