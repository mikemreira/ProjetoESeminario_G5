package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.domain.users.AuthenticatedUser
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.users.*
import isel.pt.ps.projeto.services.TokenError
import isel.pt.ps.projeto.services.UserError
import isel.pt.ps.projeto.services.UsersService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/users")
class UsersController(
    private val usersService: UsersService,
    private val requestTokenProcessor: RequestTokenProcessor,
    private val utils: UtilsController
) {
    private val logger: Logger = LoggerFactory.getLogger(UsersController::class.java)

    @GetMapping("/me")
    fun getUserByToken(@RequestHeader("Authorization") token : String): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
        val fotoString = if (authUser.user.foto != null) utils.byteArrayToBase64(authUser.user.foto) else null
                return ResponseEntity.status(200).body(
                    UserOutputModel(
                        authUser.user.id,
                        authUser.user.nome,
                        authUser.user.email,
                        authUser.user.morada,
                        fotoString
                    )
                )
    }

    @PutMapping("/me")
    fun editUserByToken(
        @RequestHeader("Authorization") token : String,
        @RequestBody input: UserEditInputModel
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
        println("entrou 1")
        val res = usersService.editUser(authUser.user.id, input.nome, input.morada, input.foto)
        println("entrou 2")
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(UserOutputModel(res.value.id, res.value.nome, res.value.email, res.value.morada, res.value.foto))
            is Failure ->
               when (res.value) {
                   UserError.UserAlreadyExists -> Problem.response(400, Problem.userAlreadyExists)
                   UserError.InvalidEmail -> Problem.response(400, Problem.emailAlreadyExists)
                   UserError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
               }
        }
    }

    @GetMapping
    fun getUsers(): ResponseEntity<*> {
        println(usersService.getUsers())
        val listOfUsers = usersService.getUsers()
        return ResponseEntity.status(200).body(listOfUsers)
    }

    @PostMapping("/signup", consumes = ["application/json", "text/plain;charset=UTF-8"], produces = ["application/json"])
    fun signUp(
        @RequestBody input: UserSignUp,
    ): ResponseEntity<*> {
        val res = usersService.signUp(input.name, input.email, input.password)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(UserSignUpOutputModel("User added"))
            is Failure ->
                when (res.value) {
                    UserError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                    UserError.UserAlreadyExists -> Problem.response(400, Problem.userAlreadyExists)
                    UserError.InvalidEmail -> Problem.response(400, Problem.emailAlreadyExists)
                }
        }
    }

    @PostMapping("/signin")
    fun signIn(
        @RequestBody input: UserSignIn,
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        val res = usersService.signIn(input.email, input.password)
        return when (res) {
            is Success ->
                ResponseEntity.status(201).body(UserTokenCreateOutputModel(res.value.tokenValue, res.value.user.foto))
            is Failure ->
                when (res.value) {
                    TokenError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
                }
        }
    }

    @PostMapping("/signout")
    fun signOut(
        user: AuthenticatedUser,
        response: HttpServletResponse,
    ){
        usersService.signOut(user.token)
    }

}
