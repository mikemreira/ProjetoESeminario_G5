package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.models.ConstructionOutputModel
import isel.pt.ps.projeto.services.ConstructionsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/obras")
class ConstructionsController(private val constructionService: ConstructionsService) {
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
}
