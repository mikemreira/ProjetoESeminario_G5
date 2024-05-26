package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.constructions.ConstructionIdOutputModel
import isel.pt.ps.projeto.models.constructions.ConstructionInputModel
import isel.pt.ps.projeto.models.constructions.ConstructionOutputModel
import isel.pt.ps.projeto.models.constructions.ListOfConstructionsOutputModel
import isel.pt.ps.projeto.services.ConstructionCreationError
import isel.pt.ps.projeto.services.ConstructionInfoError
import isel.pt.ps.projeto.services.ConstructionsService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import kotlinx.datetime.toLocalDate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
    fun getConstructionAndRole(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        val res = constructionService.getConstruction(oid)

        return when (res) {
            is Success ->
                ResponseEntity.status(200)
                    .body(
                        ConstructionOutputModel(
                            res.value.oid,
                            res.value.nome,
                            res.value.localizacao,
                            res.value.descricao,
                            res.value.data_inicio,
                            res.value.data_fim,
                            null, // res.value.foto
                            res.value.status
                        )
                    )
            is Failure ->
                when (res.value) {
                    ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                    ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                    ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                }
        }
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
        val res = constructionService.getConstructionsOfUser(authUser.user.id)
        return when (res) {
            is Success ->
                ResponseEntity.status(200)
                    .body(
                        ListOfConstructionsOutputModel(
                            res.value.map {
                                ConstructionOutputModel(
                                    it.oid,
                                    it.nome,
                                    it.localizacao,
                                    it.descricao,
                                    it.data_inicio,
                                    it.data_fim,
                                    null, // it.foto
                                    it.status
                                )
                            }
                        )
                    )
            is Failure ->
                when (res.value) {
                    ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                    ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                    ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                }
        }
    }

    @PostMapping("")
    fun createConstruction(
        @RequestBody input: ConstructionInputModel,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.createConstruction(
            authUser.user.id,
            input.name,
            input.location,
            input.description,
            input.startDate.toLocalDate(),
            input.endDate?.toLocalDate(),
            input.image,
            input.status
        )
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(ConstructionIdOutputModel(res.value))
            is Failure ->
                when (res.value) {
                    //ConstructionCreationError.ConstructionAlreadyExists -> Problem.response(400, Problem.constructionAlreadyExists)
                    ConstructionCreationError.InvalidConstruction -> Problem.response(400, Problem.invalidConstruction)
                }
        }
    }
}
