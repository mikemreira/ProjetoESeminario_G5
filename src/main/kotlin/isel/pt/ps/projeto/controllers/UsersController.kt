package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.users.UserAndToken
import isel.pt.ps.projeto.models.users.UserSignIn
import isel.pt.ps.projeto.models.users.UserSignUp
import isel.pt.ps.projeto.models.users.UserSignUpOutputModel
import isel.pt.ps.projeto.services.TokenError
import isel.pt.ps.projeto.services.UserError
import isel.pt.ps.projeto.services.UsersService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UsersController(private val usersService: UsersService) {
    private val logger: Logger = LoggerFactory.getLogger(UsersController::class.java)

    @RequestMapping("/me")
    fun getUserMe() = ResponseEntity.status(200).body("Hello me!")

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
                ResponseEntity.status(200)
                    .body(UserSignUpOutputModel("User added"))
            is Failure ->
                when (res.value) {
                    UserError.InsecurePassword -> ResponseEntity.status(400).body(UserSignUpOutputModel("Insecure password"))
                    UserError.UserAlreadyExists -> ResponseEntity.status(400).body(UserSignUpOutputModel("User already exists"))
                }
        }
        // return ResponseEntity.status(201).body(UserSignUpOutputModel("User added"))
    }

    @PostMapping("/signin")
    fun signIn(
        @RequestBody input: UserSignIn,
    ): ResponseEntity<*> {
        val res = usersService.signIn(input.email, input.password)
        return when (res) {
            is Success ->
                ResponseEntity.status(200).body(UserAndToken(res.value.user, res.value.token))
            is Failure ->
                when (res.value) {
                    TokenError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
                }
        }
        // return ResponseEntity.status(201).body(UserAndToken(userAndToken.user, userAndToken.token))
    }
}
