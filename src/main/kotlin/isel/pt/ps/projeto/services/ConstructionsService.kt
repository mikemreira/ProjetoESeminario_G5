package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.domain.constructions.ConstructionsDomain
import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.constructions.ConstructionAndRole
import isel.pt.ps.projeto.models.constructions.ConstructionEditInputModel
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.users.SimpleUserAndFunc
import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.springframework.stereotype.Component

sealed class ConstructionCreationError {
    //object ConstructionAlreadyExists : ConstructionCreationError()
    object InvalidConstruction : ConstructionCreationError()
}

sealed class ConstructionInfoError {
    object ConstructionNotFound : ConstructionInfoError()
    object NoConstructions : ConstructionInfoError()
    object EmptyEmployees : ConstructionInfoError()
    object NoAccessToConstruction: ConstructionInfoError()
    object InvalidRegister: ConstructionInfoError()
    object NoPermission: ConstructionInfoError()
    object AlreadyInConstruction: ConstructionInfoError()
    object ConstructionSuspended: ConstructionInfoError()

}

sealed class ConstructionUserError {
    object NoPermission: ConstructionUserError()
    object UserNotFound: ConstructionUserError()
}

sealed class ConstructionEditError {
    object ConstructionNotFound : ConstructionEditError()
    object NoPermission: ConstructionEditError()
    object InvalidInput: ConstructionEditError()
}

typealias ConstructionAndRoleResult = Either<ConstructionInfoError, ConstructionAndRole>
typealias ConstructionCreationResult = Either<ConstructionCreationError, Int>
typealias ConstructionInfoResult = Either<ConstructionInfoError, Construction>
typealias ConstructionsInfoResult = Either<ConstructionInfoError, List<Construction>>
typealias ConstructionEditResult = Either<ConstructionEditError, Construction?>

typealias RegisterInfoResult = Either<ConstructionInfoError, Boolean>

typealias ListOfRegisterInfoResult = Either<ConstructionInfoError, List<RegisterAndUser>>

typealias EmployeesInConstructionResult = Either<ConstructionInfoError, List<SimpleUserAndFunc>>
typealias EmployeeInConstructionResult = Either<ConstructionUserError, SimpleUserAndFunc>

@Component
class ConstructionsService(
    private val constructionsRepository: ConstructionsRepository,
    private val usersRepository: UsersRepository,
    private val utilsService: UtilsServices,
    private val constructionsDomain: ConstructionsDomain
) {
    fun getConstruction(oid: Int): ConstructionInfoResult {
        // val role = constructionsRepository.getRole(uid)
        val construction = constructionsRepository.getConstruction(oid)
        return if (construction == null) { // se nao existir obra deviamos de retornar null
            failure(ConstructionInfoError.ConstructionNotFound)
        } else {
            success(construction)
        }
    }

    //fun getConstructionAndUserRole(userId: Int, oid: Int): Con

    fun getConstructionUsers(userId: Int, oid: Int): EmployeesInConstructionResult{
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(ConstructionInfoError.ConstructionNotFound)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(ConstructionInfoError.NoAccessToConstruction)

        if (role.role == "admin") {
            val users = constructionsRepository.getConstructionsUsers(oid)
            return if (users.isEmpty()) {  // se nao existirem users deviamos de retornar uma lista vazia
                failure(ConstructionInfoError.EmptyEmployees)
            } else {
                success(users)
            }
        }
        return failure(ConstructionInfoError.NoPermission)
    }

    fun getConstructionsOfUser(uid: Int): ConstructionsInfoResult {
        val construction = constructionsRepository.getConstructionsOfUser(uid)
        return if (construction.isEmpty()) {
            failure(ConstructionInfoError.NoConstructions)
        } else {
            success(construction)
        }
    }

    fun getOnGoingConstructionsOfUser(uid: Int): ConstructionsInfoResult {
        val construction = constructionsRepository.getConstructionsOfUser(uid, "on going")
        return if (construction.isEmpty()) {
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
        status: String?,
        function: String
    ): ConstructionCreationResult {
        if (!constructionsDomain.checkValidConstruction(name, location, description, startDate)) {
            return failure(ConstructionCreationError.InvalidConstruction)
        }
        val fotoBytes = if (foto!=null) utilsService.resizeAndCompressImageBase64(foto, 800, 600, 0.8f) else null
        return success(constructionsRepository.createConstruction(userId, name, location, description, startDate, endDate, fotoBytes, status, function))
    }

    fun getUserRoleOnConstruction(userId: Int, oid: Int): ConstructionAndRoleResult {
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(ConstructionInfoError.ConstructionNotFound)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(ConstructionInfoError.NoAccessToConstruction)

        val constructionAndRole = ConstructionAndRole(construction, role)
        return success(constructionAndRole)
    }

    fun editConstruction(userId: Int, oid: Int, inputModel: ConstructionEditInputModel): ConstructionEditResult {
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(ConstructionEditError.ConstructionNotFound)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(ConstructionEditError.NoPermission)

        if (role.role != "admin")
            return failure(ConstructionEditError.NoPermission)

        if (inputModel.name.isEmpty() || inputModel.location.isEmpty() || inputModel.description.isEmpty())
            return failure(ConstructionEditError.InvalidInput)

        val updatedConstruction = constructionsRepository.editConstruction(oid, inputModel)
        return success(updatedConstruction)
    }
/*
    fun inviteToConstruction(userId: Int, oid: Int, invite: Invite): InviteInfoResult {
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(ConstructionInfoError.ConstructionNotFound)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(ConstructionInfoError.NoAccessToConstruction)

        if (role.role != "admin")
            return failure(ConstructionInfoError.NoPermission)

        val user = constructionsRepository.getUserByEmailFromConstructions(oid, invite.email)

        if (user != null)
            return failure(ConstructionInfoError.AlreadyInConstruction)

        // TODO(SEND MAIL)

        val res = constructionsRepository.inviteToConstruction(oid, invite.email)
        return success(res)
    }

 */

    fun registerIntoConstruction(userId: Int, oid: Int, startTime: LocalDateTime, endTime: LocalDateTime): RegisterInfoResult {
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(ConstructionInfoError.ConstructionNotFound)

        if (construction.status == "recoverable")
            return failure(ConstructionInfoError.ConstructionSuspended)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(ConstructionInfoError.NoAccessToConstruction)
        // Could be changed to return register
        constructionsRepository.registerIntoConstruction(userId, oid, startTime, endTime, role.role)
        return success(true)
    }

    fun getConstructionUser(authId: Int, oid: Int, uid: Int): EmployeeInConstructionResult {
        val role = constructionsRepository.getUserRoleFromConstruction(authId, oid)
            ?: return failure(ConstructionUserError.NoPermission)

        if (role.role == "admin") {
            val user = constructionsRepository.getConstructionUser(oid, uid)
            return if (user == null) {
                failure(ConstructionUserError.UserNotFound)
            } else {
                success(user)
            }
        }
        return failure(ConstructionUserError.NoPermission)
    }


    //fun checkConstructionByName(name: String): Boolean = constructionsRepository.checkConstructionByName(name)
}
