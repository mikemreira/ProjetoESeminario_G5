package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.models.UserAndToken
import isel.pt.ps.projeto.models.UserSignIn
import isel.pt.ps.projeto.models.UserSignUp
import isel.pt.ps.projeto.models.UserSignUpOutputModel
import isel.pt.ps.projeto.services.UsersService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/signup", consumes=["application/json", "text/plain;charset=UTF-8"], produces=["application/json"])
    fun signUp(
        @RequestBody input: UserSignUp,
    ): ResponseEntity<*> {
        usersService.signUp(input.name, input.email, input.password)
        return ResponseEntity.status(201).body(UserSignUpOutputModel("User added"))
    }

    @PostMapping("/signin")
    fun signIn(
        @RequestBody input: UserSignIn,
    ): ResponseEntity<*> {
        val userAndToken = usersService.signIn(input.email, input.password)
        return ResponseEntity.status(201).body(UserAndToken(userAndToken.user, userAndToken.token))
    }
}
