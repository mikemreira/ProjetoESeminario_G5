package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.services.RegistersService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/registos")
class RegistersController(
    private val registersService: RegistersService,
    private val requestTokenProcessor: RequestTokenProcessor,
) {
/*
    @GetMapping("/users")
    fun getRegistersOfUser(
        @RequestHeader("Authorization") userToken: String,
        //@PathVariable uid: Int,
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.getRegistersOfUser(authUser.user.id)
        return when (res) {
            is Success ->
                ResponseEntity.status(200)
                    .body(res.value.map {
                        // model to output
                        // TODO
                    })
            is Failure ->
                when (res.value) {
                    // TODO
                }
        }
    }

    @GetMapping("/users/{uid}/obras/{oid}")
    fun getRegistersOfUserAndConstruction(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable uid: Int,
        @PathVariable oid: Int,
    ) : ResponseEntity<*>{
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        // TODO
    }

 */
}