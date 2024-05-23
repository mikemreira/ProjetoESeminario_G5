package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.constructions.ConstructionOutputModel
import isel.pt.ps.projeto.models.constructions.ObrasOutputModel
import isel.pt.ps.projeto.services.ConstructionsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/obras")
class ConstructionsController(
    private val constructionService: ConstructionsService,
    private val requestTokenProcessor: RequestTokenProcessor,
) {
    @GetMapping("/{oid}")
    fun getConstruction(
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        val construction = constructionService.getConstruction(oid)
        return ResponseEntity.status(200).body(
            ConstructionOutputModel(
                construction.oid,
                construction.nome,
                construction.localizacao,
                construction.descricao,
                construction.data_inicio,
                construction.data_fim,
                construction.status,
            ),
        )
    }

    @GetMapping("/{oid}/users")
    fun getConstructionsUsers(
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        val users = constructionService.getConstructionUsers(oid)
        return ResponseEntity.status(200).body(users)
    }

    @GetMapping("")
    fun getConstructions(
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val constructions = constructionService.getConstructionsOfUser(authUser.user.id)
        return ResponseEntity.status(200).body(ObrasOutputModel(constructions))
    }
}
