package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.constructions.ConstructionEditInputModel
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterQuery
import isel.pt.ps.projeto.models.role.Role
import isel.pt.ps.projeto.models.users.SimpleUser
import isel.pt.ps.projeto.models.users.SimpleUserAndFunc
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface ConstructionRepository {

    /**
     * TODO( check if user in the to alter function to obtain role that is being used to check if user in construction )
     */
    fun getConstruction(oid: Int): Construction?
    fun getConstructionByNFCID(nfcId: String): Construction?
    fun getConstructionsUsers(oid: Int, page: Int): List<SimpleUserAndFunc>
    fun getSizeOfUsersInConstructions(oid: Int): Int
    fun getConstructionUser(oid: Int, uid: Int): SimpleUserAndFunc?
    fun getConstructionsOfUser(id: Int, status: String? = null, page: Int?): List<Construction>
    fun getSizeOfConstructions(uid: Int): Int
    fun createConstruction(userId: Int, name: String, location: String, description: String, startDate: LocalDate, endDate: LocalDate?, foto: ByteArray?, status: String?, function: String): Int
    fun getUserRoleFromConstruction(id: Int, oid: Int): Role?
    fun getUserByEmailFromConstructions(oid: Int, email: String): SimpleUser?
    fun inviteToConstruction(oid: Int, email: String): Boolean
    fun checkConstructionByName(name: String): Boolean
    fun registerIntoConstruction(userId: Int, oid: Int, startTime: LocalDateTime, endTime: LocalDateTime, role: String)
    fun getRegisters(userId: Int, oid: Int, role: String, filters: RegisterQuery): List<RegisterAndUser>
    fun isUserAssociatedWithConstructionByEmail(oid: Int, email: String): Boolean
    fun editConstruction(uid:Int, oid: Int, inputModel: ConstructionEditInputModel): Construction?
    fun removeConstructionUser(oid: Int, uid: Int): Boolean
    fun getNfc(oid: Int): String?
    fun editNfc(oid: Int, nfcId: String): String
    fun isAdmin(uid: Int): Boolean

}
