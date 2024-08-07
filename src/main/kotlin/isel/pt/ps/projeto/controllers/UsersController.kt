package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.casbin.AuthorizationService
import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.domain.users.AuthenticatedUser
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.images.ImageOutputModel
import isel.pt.ps.projeto.models.users.*
import isel.pt.ps.projeto.services.*
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/users")
@Profile("!test")
class UsersController(
    private val usersService: UsersService,
    private val requestTokenProcessor: RequestTokenProcessor,
    private val authorizationService: AuthorizationService,
    private val utils: UtilsController
) {
    private val logger: Logger = LoggerFactory.getLogger(UsersController::class.java)

    @GetMapping("/me")
    fun getUserByToken(
        @RequestHeader("Authorization") token : String
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
                return ResponseEntity.status(200).body(
                    UserOutputModel(
                        authUser.user.id,
                        authUser.user.nome,
                        authUser.user.email,
                        authUser.user.morada,
                        "/users/me/imagem"
                    )
                )
    }

    @PostMapping("/forget-password")
    fun forgetPassword(
        @RequestBody email: String
    ): ResponseEntity<*> {
        val res = usersService.forgetPassword(email)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(res.value)
            is Failure ->
                when (res.value) {
                    ForgetPasswordError.InvalidEmail -> Problem.response(404, Problem.emailDoesntExists)
                }
        }
    }

    @PutMapping("/set-password")
    fun setNewPassword(
        @RequestParam email: String,
        @RequestParam token: String,
        @RequestBody password: String
    ): ResponseEntity<*> {
        val res = usersService.setNewPassword(email, token, password)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(res.value)
            is Failure ->
                when (res.value) {
                    SetNewPasswordError.InvalidQuery-> Problem.response(404, Problem.invalidQuery)
                    SetNewPasswordError.NoUserWithThatEmail -> Problem.response(404, Problem.emailDoesntExists)
                    SetNewPasswordError.InsecurePassword -> Problem.response(422, Problem.insecurePassword)
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
                    .body(
                        UserOutputModel(
                            res.value.id,
                            res.value.nome,
                            res.value.email,
                            res.value.morada,
                            "/users/me/imagem"
                        )
                    )
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
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
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
        val listOfUsers = usersService.getUsers()
        return ResponseEntity.status(200).body(listOfUsers)
    }

    @GetMapping("/me/imagem")
    fun getImage(
        @RequestHeader("Authorization") token : String,
        @RequestParam type: String,
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(token)?: return ResponseEntity.status(404).body(Problem.invalidToken)
        val res = usersService.getImage(authUser.user.id, type)
        return when (res) {
            is Success -> {
                val foto = if (res.value != null) utils.byteArrayToBase64(res.value) else null
                ResponseEntity.status(200)
                    .body(ImageOutputModel(foto))
            }

            is Failure ->
                when (res.value) {
                    ImageError.InvalidQuery -> Problem.response(400, Problem.invalidQuery)
                    ImageError.UserDoesntExist -> Problem.response(404, Problem.userNotFound)
                }
        }
    }

    @PostMapping("/signup")
    fun signUp(
        @RequestBody input: UserSignUp,
    ): ResponseEntity<*> {
        val res = usersService.signUp(input.name, input.email, input.password)
        return when (res) {
            is Success -> {
                authorizationService.saveUserRole(input.email)
                authorizationService.enforcer()
                ResponseEntity.status(201)
                    .body(UserSignUpOutputModel("User added"))
            }

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
            is Success -> {
                ResponseEntity.status(201).body(UserTokenCreateOutputModel(res.value.tokenValue, "/users/me/imagem?type=icon"))
            }

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
