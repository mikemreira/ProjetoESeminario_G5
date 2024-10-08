package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.domain.constructions.ConstructionsDomain
import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.constructions.ConstructionAndRole
import isel.pt.ps.projeto.models.constructions.ConstructionEditInputModel
import isel.pt.ps.projeto.models.constructions.ConstructionsListAndSize
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.users.ListOfUsersAndSize
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
    object InvalidConstruction : ConstructionCreationError()
}

sealed class ConstructionInfoError {
    object ConstructionNotFound : ConstructionInfoError()
    object NoConstructions : ConstructionInfoError()
    object EmptyEmployees : ConstructionInfoError()
    object NoAccessToConstruction: ConstructionInfoError()
    object NoPermission: ConstructionInfoError()
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

sealed class NfcError {
    object NoPermission: NfcError()
    object NoConstruction: NfcError()
}

sealed class IsAdminError {
    object UserNotFound: IsAdminError()
}

typealias NfcResult = Either<NfcError, String?>

typealias ConstructionAndRoleResult = Either<ConstructionInfoError, ConstructionAndRole>
typealias ConstructionCreationResult = Either<ConstructionCreationError, Int>
typealias ConstructionsInfoResult = Either<ConstructionInfoError, ConstructionsListAndSize>
typealias ConstructionEditResult = Either<ConstructionEditError, Construction?>

typealias RegisterInfoResult = Either<ConstructionInfoError, Boolean>
typealias RemoveUserFromConstructionResult = Either<ConstructionUserError, Boolean>

typealias EmployeesInConstructionResult = Either<ConstructionInfoError, ListOfUsersAndSize>
typealias EmployeeInConstructionResult = Either<ConstructionUserError, SimpleUserAndFunc>

typealias IsAdminResult = Either<IsAdminError, Boolean>

@Component
class ConstructionsService(
    private val constructionsRepository: ConstructionsRepository,
    private val usersRepository: UsersRepository,
    private val utilsService: UtilsServices,
    private val constructionsDomain: ConstructionsDomain
) {

    fun getConstructionUsers(userId: Int, oid: Int, page: Int): EmployeesInConstructionResult{
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(ConstructionInfoError.ConstructionNotFound)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(ConstructionInfoError.NoAccessToConstruction)

        if (role.role == "admin") {
            val users = constructionsRepository.getConstructionsUsers(oid, if (page<=0) 1 else page)
            val size = constructionsRepository.getSizeOfUsersInConstructions(oid)
            val res = ListOfUsersAndSize(users, size)
            return if (users.isEmpty()) {
                failure(ConstructionInfoError.EmptyEmployees)
            } else {
                success(res)
            }
        }
        return failure(ConstructionInfoError.NoPermission)
    }

    fun getConstructionsOfUser(uid: Int, page: Int): ConstructionsInfoResult {
        val constructions = constructionsRepository.getConstructionsOfUser(uid, page = if (page <= 0) 1 else page)
        val size = constructionsRepository.getSizeOfConstructions(uid)
        val res = ConstructionsListAndSize(constructions, size)
        return if (res.list.isEmpty()) {
            failure(ConstructionInfoError.NoConstructions)
        } else {
            success(res)
        }
    }

    fun getOnGoingConstructionsOfUser(uid: Int): ConstructionsInfoResult {
        val construction = constructionsRepository.getConstructionsOfUser(uid, "on going", null)
        val res = ConstructionsListAndSize(construction, 0)
        return if (construction.isEmpty()) {
            failure(ConstructionInfoError.NoConstructions)
        } else {
            success(res)
        }
    }

    fun removeConstructionUser(authId: Int, oid: Int, uid: Int): RemoveUserFromConstructionResult {
        val role = constructionsRepository.getUserRoleFromConstruction(authId, oid)
            ?: return failure(ConstructionUserError.NoPermission)

        val userToBeRemoved = constructionsRepository.getUserRoleFromConstruction(uid, oid)
            ?: return failure(ConstructionUserError.UserNotFound)

        if (userToBeRemoved.role == "admin") return failure(ConstructionUserError.NoPermission)
        if (role.role != "admin") return failure(ConstructionUserError.NoPermission)
        
        return success(constructionsRepository.removeConstructionUser(oid, uid))
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

    fun getNfc(userId: Int, oid: Int): NfcResult {
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(NfcError.NoConstruction)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(NfcError.NoPermission)

        if (role.role != "admin")
            return failure(NfcError.NoPermission)

        return success(constructionsRepository.getNfc(oid))
    }

    fun editNfc(userId: Int, oid: Int, nfc: String): NfcResult {
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(NfcError.NoConstruction)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(NfcError.NoPermission)

        if (role.role != "admin")
            return failure(NfcError.NoPermission)

        return success(constructionsRepository.editNfc(oid, nfc))
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

        val updatedConstruction = constructionsRepository.editConstruction(userId, oid, inputModel)
        return success(updatedConstruction)
    }

    fun registerIntoConstruction(userId: Int, oid: Int, startTime: LocalDateTime, endTime: LocalDateTime): RegisterInfoResult {
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(ConstructionInfoError.ConstructionNotFound)

        if (construction.status == "recoverable")
            return failure(ConstructionInfoError.ConstructionSuspended)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(ConstructionInfoError.NoAccessToConstruction)
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

    fun isAdmin(userId: Int): IsAdminResult {
        return success(constructionsRepository.isAdmin(userId))
    }

}
