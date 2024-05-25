package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.domain.constructions.ConstructionsDomain
import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success
import kotlinx.datetime.LocalDate
import org.springframework.stereotype.Component
import kotlin.random.Random

sealed class ConstructionCreationError {
    //object ConstructionAlreadyExists : ConstructionCreationError()
    object InvalidConstruction : ConstructionCreationError()
}

sealed class ConstructionInfoError {
    object ConstructionNotFound : ConstructionInfoError()
    object NoConstructions : ConstructionInfoError()
    object EmptyEmployees : ConstructionInfoError()
}

typealias ConstructionCreationResult = Either<ConstructionCreationError, Int>
typealias ConstructionInfoResult = Either<ConstructionInfoError, Construction>
typealias ConstructionsInfoResult = Either<ConstructionInfoError, List<Construction>>
typealias EmployeesInConstructionResult = Either<ConstructionInfoError, List<User>>

@Component
class ConstructionsService(
    private val constructionsRepository: ConstructionsRepository,
    private val constructionsDomain: ConstructionsDomain
) {
    fun getConstruction(oid: Int): ConstructionInfoResult {
        val construction = constructionsRepository.getConstruction(oid)
        return if (construction == null) { // se nao existir obra deviamos de retornar null
            failure(ConstructionInfoError.ConstructionNotFound)
        } else {
            success(construction)
        }
    }

    fun getConstructionUsers(oid: Int): EmployeesInConstructionResult{
        val users = constructionsRepository.getConstructionsUsers(oid)
        return if (users.isEmpty()) {  // se nao existirem users deviamos de retornar uma lista vazia
            failure(ConstructionInfoError.EmptyEmployees)
        } else {
            success(users)
        }
    }

    fun getConstructionsOfUser(id: Int): ConstructionsInfoResult {
        val construction = constructionsRepository.getConstructionsOfUser(id)
        return if (construction.isEmpty()) {  // se nao existirem obras deviamos de retornar uma lista vazia
            failure(ConstructionInfoError.NoConstructions)
        } else {
            success(construction)
        }
    }

    fun createConstruction(
        userId: Int,
        name: String,
        location: String,
        description: String,
        startDate: LocalDate,
        endDate: LocalDate?,
        foto: String?,
        status: String
    ): ConstructionCreationResult {
        if (!constructionsDomain.checkValidConstruction(name, location, description, startDate, endDate, status)) {
            return failure(ConstructionCreationError.InvalidConstruction)
        }
        return success(constructionsRepository.createConstruction(userId, name, location, description, startDate, endDate, foto, status))
    }

    //fun checkConstructionByName(name: String): Boolean = constructionsRepository.checkConstructionByName(name)
}
