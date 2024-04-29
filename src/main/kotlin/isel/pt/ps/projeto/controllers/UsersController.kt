package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.models.UserOutputModel
import isel.pt.ps.projeto.services.UsersService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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
        val user = usersService.getUsers()
        return ResponseEntity.status(200).body(UserOutputModel(user[0].id, user[0].nome, user[0].email, user[0].morada))
    }
}
