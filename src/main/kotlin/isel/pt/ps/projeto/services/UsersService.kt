package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.controllers.UtilsController
import isel.pt.ps.projeto.domain.users.Token
import isel.pt.ps.projeto.domain.users.UsersDomain
import isel.pt.ps.projeto.models.users.SimpleUser
import isel.pt.ps.projeto.models.users.User
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

sealed class ForgetPasswordError {
    object InvalidEmail : ForgetPasswordError()
}

sealed class SetNewPasswordError {
    object InvalidQuery : SetNewPasswordError()
    object NoUserWithThatEmail : SetNewPasswordError()
    object InsecurePassword : SetNewPasswordError()
}

typealias SetNewPasswordResult = Either<SetNewPasswordError, String>

typealias ForgetPasswordResult = Either<ForgetPasswordError, String>

typealias UserResult = Either<UserError, User>
typealias SimpleUserResult = Either<UserError, SimpleUser>
typealias ChangePasswordResult = Either<UserError, Boolean>

typealias TokenResult = Either<TokenError, TokenExternalInfo>

@Component
class UsersService(
    private val usersRepository: UsersRepository,
    private val usersDomain: UsersDomain,
    private val utilsService: UtilsServices,
    private val clock: Clock,
    private val emailSenderService: EmailSenderService,
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
        println("SIGN IN USER : $user")
        println(usersDomain.createPasswordValidationInformation(password))
        if (!usersDomain.validatePassword(password, user.passwordValidation)) {
            println("PASS DIDNT MATCH")
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

        val fotoString = if (user.foto != null ) utils.byteArrayToBase64(user.foto) else null
        val simpleUser = SimpleUser(user.id, user.nome, user.email, user.morada, fotoString)
        println("Simple USER : $simpleUser")
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

    fun forgetPassword(email: String): ForgetPasswordResult {
        println(email)
        println(usersRepository.checkUserByEmail(email))
        if (!usersRepository.checkUserByEmail(email)) {
            return failure(ForgetPasswordError.InvalidEmail)
        }
        val token = usersDomain.generateTokenValue()
        usersRepository.setForgetPassword(email, token)
        emailSenderService.sendEmail(email, "Forgot Password", "Hello $email, \n" +
            "Click in this link to reset your password http://localhost:5173/set-password?email=$email&token=$token"
        )
        return success("To reset your pass go to your email")
    }

    fun setNewPassword(email: String, token: String, password: String): SetNewPasswordResult {
        if (!usersRepository.validateEmailAndTokenForForgottenPassword(email, token))
            return failure(SetNewPasswordError.InvalidQuery)
        val user = usersRepository.getUserByEmail(email) ?: return failure(SetNewPasswordError.NoUserWithThatEmail)
        if (!usersDomain.isSafePassword(password)) {
            println("Insecure password")
            return failure(SetNewPasswordError.InsecurePassword)
        }
        val passVal = usersDomain.createPasswordValidationInformation(password)
        usersRepository.editPasswordIfForgotten(user.id, email, passVal)
        return success("Password changed")
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
        val fotoBytes = if (foto!=null) utilsService.base64ToByteArray(foto) else null
        val res = usersRepository.editUser(id, nome, morada, fotoBytes)
        return success(res)
    }

    fun editPassword(id: Int, password: String): ChangePasswordResult {
        if (!usersDomain.isSafePassword(password)) {
            println("Insecure password")
            return failure(UserError.InsecurePassword)
        }
        println("Safe password")
        val passwordValidationInfo = usersDomain.createPasswordValidationInformation(password)
        println("Password validation info: $passwordValidationInfo")
        val res = usersRepository.editPassword(id, passwordValidationInfo)
        return success(res)
    }

    fun checkUserByEmail(email: String): Boolean {
        return usersRepository.checkUserByEmail(email)
    }
}
