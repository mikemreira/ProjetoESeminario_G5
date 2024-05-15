package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.models.UserSignIn
import isel.pt.ps.projeto.models.UserSignUp
import isel.pt.ps.projeto.services.UsersService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UsersController(private val usersService: UsersService) {
    @RequestMapping("/me")
    fun getUserMe() = ResponseEntity.status(200).body("Hello me!")

    @GetMapping
    fun getUsers(): ResponseEntity<*> {
        println(usersService.getUsers())
        val listOfUsers = usersService.getUsers()
        return ResponseEntity.status(200).body(listOfUsers)
    }

    @PostMapping("/signup")
    fun signUp(
        @RequestBody input: UserSignUp,
    ): ResponseEntity<*> {
        usersService.signUp(input.name, input.email, input.password)
        return ResponseEntity.status(200).body("User added")
    }

    @PostMapping("/signin")
    fun signIn(
        @RequestBody input: UserSignIn,
    ): ResponseEntity<*> {
        usersService.signIn(input.email, input.password)
        return ResponseEntity.status(200).body("signed in")
    }
}
