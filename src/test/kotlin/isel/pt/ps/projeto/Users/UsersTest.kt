package isel.pt.ps.projeto.Users

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.domain.users.Token
import isel.pt.ps.projeto.domain.users.TokenValidationInfo
import isel.pt.ps.projeto.domain.users.UsersDomain
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import kotlinx.datetime.Clock
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@SpringBootTest
@ActiveProfiles("test")
@Sql(
    scripts = ["/testScheme.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
    scripts = ["/cleanTestDB.sql"],
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class UsersTest(
    @Autowired
    private val usersRepository: UsersRepository,
    @Autowired
    private val usersDomain: UsersDomain,
    @Autowired
    private val clock: Clock,
    ) {

    @Test
    fun `test getUsers`() {

        val pass = usersDomain.createPasswordValidationInformation("password")

        usersRepository.signUp("John Doe", "john.doe@example.com", pass)
        usersRepository.signUp("Jane Smith", "jane.smith@example.com", PasswordValidationInfo("password123"))

        val users = usersRepository.getUsers()
        assertThat(users).hasSize(2)
    }

    @Test
    fun `test getUserById`() {
        val user = usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))

        val retrievedUser = usersRepository.getUserById(user.id)
        assertThat(retrievedUser).isNotNull
        assertThat(retrievedUser!!.nome).isEqualTo("John Doe")
    }

    @Test
    fun `test getUserByToken`() {
        val user = usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))

        val tokenValue = usersDomain.generateTokenValue()
        val now = clock.now()
        val newToken = Token(
            usersDomain.createTokenValidationInformation(tokenValue),
            user.id,
            createdAt = now,
            lastUsedAt = now
        )
        usersRepository.createToken(newToken, 5)

        val retrievedUser = usersRepository.getUserByToken(newToken.tokenValidationInfo.validationInfo)
        assertThat(retrievedUser).isNotNull
        assertThat(retrievedUser!!.nome).isEqualTo("John Doe")
    }

    @Test
    fun `test getTokenByTokenValidationInfo`() {
        val user = usersRepository.signUp("Jane Smith", "jane.smith@example.com", PasswordValidationInfo("password123"))
        val tokenValue = usersDomain.generateTokenValue()
        val now = clock.now()
        val newToken = Token(
            usersDomain.createTokenValidationInformation(tokenValue),
            user.id,
            createdAt = now,
            lastUsedAt = now
        )
        usersRepository.createToken(newToken, 5)
        val userTokenPair = usersRepository.getTokenByTokenValidationInfo(newToken.tokenValidationInfo)
        assertThat(userTokenPair).isNotNull
        val (retrievedUser, token) = userTokenPair!!
        assertThat(retrievedUser.nome).isEqualTo("Jane Smith")
        assertThat(token.tokenValidationInfo.validationInfo).isEqualTo(newToken.tokenValidationInfo.validationInfo)
    }

    /*
    @Test
    fun `test updateTokenLastUsed`() {
        val user = usersRepository.signUp("Jane Smith", "jane.smith@example.com", PasswordValidationInfo("password123"))
        val userAndToken = usersRepository.signIn("jane.smith@example.com")
        val tokenValidationInfo = TokenValidationInfo(userAndToken.token)
        val token = Token(tokenValidationInfo, userAndToken.user.id, clock.now(), clock.now())

        usersRepository.updateTokenLastUsed(token, clock.now())
    }

     */

    @Test
    fun `test getUserByEmail`() {
        usersRepository.signUp("Jane Smith", "jane.smith@example.com", PasswordValidationInfo("password123"))

        val user = usersRepository.getUserByEmail("jane.smith@example.com")
        assertThat(user).isNotNull
        assertThat(user!!.nome).isEqualTo("Jane Smith")
    }

    @Test
    fun `test checkUserByEmail`() {
        usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))

        val exists = usersRepository.checkUserByEmail("john.doe@example.com")
        assertThat(exists).isTrue
    }

    @Test
    fun `test signUp`() {
        val user = usersRepository.signUp("Alice Johnson", "alice.johnson@example.com", PasswordValidationInfo("mypassword"))
        assertThat(user.nome).isEqualTo("Alice Johnson")
    }

    @Test
    fun `test signIn`() {
        val user = usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))

        val tokenValue = usersDomain.generateTokenValue()
        val now = clock.now()
        val newToken = Token(
            usersDomain.createTokenValidationInformation(tokenValue),
            user.id,
            createdAt = now,
            lastUsedAt = now
        )
        usersRepository.createToken(newToken, 5)

        val userAndToken = usersRepository.getTokenByTokenValidationInfo(newToken.tokenValidationInfo)
        assertThat(userAndToken!!.first.nome).isEqualTo("John Doe")
    }

    @Test
    fun `test signOut`() {
        val userSignUp = usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))
        val tokenValue = usersDomain.generateTokenValue()
        val now = clock.now()
        val newToken = Token(
            usersDomain.createTokenValidationInformation(tokenValue),
            userSignUp.id,
            createdAt = now,
            lastUsedAt = now
        )
        usersRepository.createToken(newToken, 5)
        usersRepository.signOut(newToken.tokenValidationInfo)

        val user = usersRepository.getUserByToken(newToken.tokenValidationInfo.validationInfo)
        assertThat(user).isNull()
    }

    @Test
    fun `test setForgetPassword`() {
        usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))

        usersRepository.setForgetPassword("john.doe@example.com", "forgetToken")
    }

    @Test
    fun `test validateEmailAndTokenForForgottenPassword`() {
        usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))
        usersRepository.setForgetPassword("john.doe@example.com", "forgetToken")

        val valid = usersRepository.validateEmailAndTokenForForgottenPassword("john.doe@example.com", "forgetToken")
        assertThat(valid).isTrue
    }

    /*
    @Test
    fun `test editPasswordIfForgotten`() {
        usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))
        usersRepository.setForgetPassword("john.doe@example.com", "forgetToken")

        usersRepository.editPasswordIfForgotten(1, "john.doe@example.com", PasswordValidationInfo("newpassword"))
        val user = usersRepository.getUserByEmail("john.doe@example.com")
        assertThat(user.password.validationInfo).isEqualTo("newpassword")
    }

     */

    /*
    @Test
    fun `test getImage`() {
        // Assume there's an image for user with id 1 and type 'thumbnail'
        usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))

        val image = usersRepository.getImage(1, "thumbnail")
        assertThat(image).isNotNull
    }

     */


    @Test
    fun `test createToken`() {
        val user = usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))
        val token = Token(TokenValidationInfo("tokenValidation"), user.id, clock.now(), clock.now())

        usersRepository.createToken(token, 5)
    }

    @Test
    fun `test editUser`() {
        val user = usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))
        val updatedUser = usersRepository.editUser(user.id, "Johnathan Doe", "New Address", null)

        assertThat(updatedUser.nome).isEqualTo("Johnathan Doe")
        assertThat(updatedUser.morada).isEqualTo("New Address")
    }

    /*
    @Test
    fun `test editPassword`() {
        val user = usersRepository.signUp("John Doe", "john.doe@example.com", PasswordValidationInfo("password"))

        val result = usersRepository.editPassword(user.id, PasswordValidationInfo("updatedPassword"))
        assertThat(result).isTrue
        val updatedUser = usersRepository.getUserById(user.id)
        assertThat(updatedUser!!.password.validationInfo).isEqualTo("updatedPassword")
    }

     */
}
