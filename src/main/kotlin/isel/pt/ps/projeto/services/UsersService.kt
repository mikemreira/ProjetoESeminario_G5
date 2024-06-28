package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.controllers.UtilsController
import isel.pt.ps.projeto.domain.users.Token
import isel.pt.ps.projeto.domain.users.UsersDomain
import isel.pt.ps.projeto.models.users.SimpleUser
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
    val tokenExpiration: Instant,
    val user: SimpleUser
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
typealias SimpleUserResult = Either<UserError, SimpleUser>

typealias TokenResult = Either<TokenError, TokenExternalInfo>

@Component
class UsersService(
    private val usersRepository: UsersRepository,
    private val usersDomain: UsersDomain,
    private val clock: Clock,
    private val utils: UtilsController
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

        val fotoString = utils.byteArrayToBase64(user.foto)
        val simpleUser = SimpleUser(user.id, user.nome, user.email, user.morada, fotoString)
        return Either.Right(
            TokenExternalInfo(
                tokenValue,
                usersDomain.getTokenExpiration(newToken),
                simpleUser
            )
        )
    }

    fun signOut(token: String): Boolean {
        println("Hello")
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        usersRepository.signOut(tokenValidationInfo)
        return true
    }

    fun getUserByToken(token: String): User? {
        if(!usersDomain.canBeToken(token)) {
            return null
        }
        val validToken = usersDomain.createTokenValidationInformation(token)
        val userAndToken = usersRepository.getTokenByTokenValidationInfo(validToken)
        if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
            usersRepository.updateTokenLastUsed(userAndToken.second, clock.now())
            return userAndToken.first
        } else {
            return null
        }
    }


    fun editUser(id: Int, nome: String, morada: String?, foto: String?): SimpleUserResult {
        val res = usersRepository.editUser(id, nome, morada, foto)
        return success(res)
    }

    fun checkUserByEmail(email: String): Boolean {
        return usersRepository.checkUserByEmail(email)
    }
}
