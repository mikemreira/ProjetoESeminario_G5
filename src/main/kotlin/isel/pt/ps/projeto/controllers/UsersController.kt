package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.domain.users.AuthenticatedUser
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.users.*
import isel.pt.ps.projeto.services.ImageError
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
    //private val authorizationService: AuthorizationService,
    private val utils: UtilsController
) {
    private val logger: Logger = LoggerFactory.getLogger(UsersController::class.java)

    @GetMapping("/me")
    fun getUserByToken(@RequestHeader("Authorization") token : String): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
                return ResponseEntity.status(200).body(
                    UserOutputModel(
                        authUser.user.id,
                        authUser.user.nome,
                        authUser.user.email,
                        authUser.user.morada,
                        "api/users/me/image?type=thumbnail",
                        "api/users/me/image?type=icon"
                    )
                )
    }

    @GetMapping("/me/image")
    fun getUserImage(
        @RequestHeader("Authorization") token : String,
        @RequestParam type: String
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
        val res = usersService.getImages(authUser.user.id, type)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(res.value)
            is Failure ->
                when (res.value) {
                    ImageError.InvalidQuery -> Problem.response(400, Problem.invalidQuery)
                }
        }
    }

    @GetMapping("/{uid}/image")
    fun getUserImage(
        @RequestHeader("Authorization") token : String,
        @RequestParam type: String,
        @PathVariable uid: Int
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
        val res = usersService.getImages(uid, type)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(res.value)
            is Failure ->
                when (res.value) {
                    ImageError.InvalidQuery -> Problem.response(400, Problem.invalidQuery)
                }
        }
    }

    @PutMapping("/me")
    fun editUserByToken(
        @RequestHeader("Authorization") token : String,
        @RequestBody input: UserEditInputModel
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
        val res = usersService.editUser(authUser.user.id, input.nome, input.morada, input.foto)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(UserOutputModel(res.value.id, res.value.nome, res.value.email, res.value.morada, "api/users/me/image?type=thumbnail", "api/users/me/image?type=icon"))
            is Failure ->
               when (res.value) {
                   UserError.UserAlreadyExists -> Problem.response(400, Problem.userAlreadyExists)
                   UserError.InvalidEmail -> Problem.response(400, Problem.emailAlreadyExists)
                   UserError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
               }
        }
    }

    @PutMapping("/me/changepassword")
    fun editPassword(
        @RequestHeader("Authorization") token : String,
        @RequestBody password: String
    ): ResponseEntity<*> {
        println("entrou")
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
        println("PASS : "+password)
        val res = usersService. editPassword(authUser.user.id, password)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(UserChangePasswordOutputModel(res.value.toString()))
            is Failure ->
                when (res.value) {
                    UserError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                    UserError.UserAlreadyExists -> Problem.response(400, Problem.userAlreadyExists)
                    UserError.InvalidEmail -> Problem.response(400, Problem.emailAlreadyExists)
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
                ResponseEntity.status(201).body(UserTokenCreateOutputModel(res.value.tokenValue, "api/users/me/image?type=icon"))
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
        println("USER : $user")
        usersService.signOut(user.token)
    }

}
