package isel.pt.ps.projeto.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UsersController {
    @RequestMapping("/me")
    fun getUserMe() = ResponseEntity.status(200).body("Hello me!")
}