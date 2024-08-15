package isel.pt.ps.projeto.Users

import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import isel.pt.ps.projeto.repository.jdbc.RegistersRepository
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime


@SpringBootTest
@ActiveProfiles("test")
@Sql(
    scripts = ["/testScheme.sql", "/testInsert.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class RegistersTests(
    @Autowired
    private val usersRepository: UsersRepository,
    @Autowired
    private val registersRepository: RegistersRepository,

    ) {

    @Test
    fun `test getUserRegisters retrieves correct registers`() {
        // Given
        val userId = 1

        // When
        val registers = registersRepository.getUserRegisters(userId)

        // Then
        assertNotNull(registers)
        assertEquals(3, registers.size)
    }

    @Test
    fun `test addUserRegisterEntry adds new register`() {
        // Given
        val userId = 1
        val obraId = 1
        val time = LocalDateTime.now()

        // When
        val result = registersRepository.addUserRegisterEntry(userId, obraId, time)

        // Then
        assertTrue(result)

        // Verify that the register was added
        val registers = registersRepository.getUserRegisters(userId)

        assertTrue(registers.first().saida == null)
    }

    @Test
    fun `test addUserRegisterExit updates existing register`() {
        // Given
        val userId = 1
        val obraId = 1
        val regId = 7
        val exitTime = LocalDateTime.now()

        // When
        registersRepository.addUserRegisterEntry(userId, obraId, exitTime)
        val result = registersRepository.addUserRegisterExit(regId, userId, obraId, exitTime)

        // Then
        assertTrue(result)

        // Verify that the register was updated
        val registers = registersRepository.getUserRegisters(userId)
        val updatedRegister = registers.firstOrNull { it.id == regId }
        assertNotNull(updatedRegister)
        assertEquals("pending", updatedRegister?.status)
    }

    @Test
    fun `test getIncompleteRegisters returns only incomplete registers`() {
        // Given
        val userId = 1
        val obraId = 1
        val time = LocalDateTime.now()

        // When
        registersRepository.addUserRegisterEntry(userId, obraId, time)
        val incompleteRegisters = registersRepository.getIncompleteRegisters(userId)

        // Then
        assertTrue(incompleteRegisters.isNotEmpty())
        assertTrue(incompleteRegisters.all { it.status in listOf("unfinished", "unfinished_nfc") })
    }

    @Test
    fun `test insertExitOnWeb with admin role`() {
        // Given
        val userId = 1
        val regId = 7
        val obraId = 1
        val role = "admin"
        val time = LocalDateTime.now()

        // When
        registersRepository.addUserRegisterEntry(userId, obraId, time)
        val result = registersRepository.insertExitOnWeb(userId, regId, obraId, role, time)

        // Then
        assertTrue(result)

        // Verify that the register was updated
        val registers = registersRepository.getUserRegisters(userId)
        val updatedRegister = registers.firstOrNull { it.id == regId }
        assertNotNull(updatedRegister)
        assertEquals("completed", updatedRegister?.status)
    }

    @Test
    fun `test insertExitOnWeb with non-admin role`() {
        // Given
        val userId = 1
        val regId = 7
        val obraId = 1
        val role = "user"
        val time = LocalDateTime.now()

        // When
        registersRepository.addUserRegisterEntry(userId, obraId, time)
        val result = registersRepository.insertExitOnWeb(userId, regId, obraId, role, time)

        // Then
        assertTrue(result)

        // Verify that the register was updated
        val registers = registersRepository.getUserRegisters(userId)
        val updatedRegister = registers.firstOrNull { it.id == regId }
        assertNotNull(updatedRegister)
        assertEquals("pending", updatedRegister?.status)
    }

    @Test
    fun `test getUsersRegistersFromConstruction returns correct registers`() {
        // Given
        val obraId = 1
        val page = 1

        // When
        val registers = registersRepository.getUsersRegistersFromConstruction(obraId, page)

        // Then
        assertNotNull(registers)
        assertEquals(3, registers.size)
        assertTrue(registers.all { it.oid == obraId })
    }

    @Test
    fun `test getUserRegisterFromConstruction returns user's registers`() {
        // Given
        val userId = 1
        val obraId = 1
        val page = 1

        // When
        val registers = registersRepository.getUserRegisterFromConstruction(userId, obraId, page)

        // Then
        assertNotNull(registers)
        assertTrue(registers.all { it.uid == userId && it.oid == obraId })
    }

    @Test
    fun `test getPendingRegistersFromConstruction returns only pending registers`() {
        // Given
        val obraId = 1
        val page = 1

        // When
        val registers = registersRepository.getPendingRegistersFromConstruction(obraId, page)

        // Then
        assertNotNull(registers)
        assertTrue(registers.all { it.status == "pending" && it.oid == obraId })
    }

    @Test
    fun `test getPendingRegisters returns pending registers for admin`() {
        // Given
        val adminUserId = 1  // Assuming this user is an admin
        val page = 1

        // When
        val registers = registersRepository.getPendingRegisters(adminUserId, page)

        // Then
        assertNotNull(registers)
        assertTrue(registers.all { it.status == "pending" })
    }
/*
    @Test
    fun `test deleteRegister removes the register`() {
        // Given
        val userId = 1
        val obraId = 1
        val registerId = 2 // Assuming testInsert.sql inserts a register with this ID

        // When
        val result = registersRepository.deleteRegister(userId, obraId, registerId)

        // Then
        assertTrue(result)

        // Verify that the register was deleted
        val registers = registersRepository.getUserRegisters(userId)
        assertTrue(registers.none { it.id == registerId })
    }

 */

    @Test
    fun `test acceptOrDeny changes the status of a register`() {
        // Given
        val userId = 1
        val obraId = 1
        val response = "completed"
        val regId = 7
        val exitTime = LocalDateTime.now()

        // When
        registersRepository.addUserRegisterEntry(userId, obraId, exitTime)
        registersRepository.addUserRegisterExit(regId, userId, obraId, exitTime)
        val result = registersRepository.acceptOrDeny(userId, obraId, regId, response)

        // Then
        assertTrue(result)

        // Verify that the status was updated
        val registers = registersRepository.getUserRegisters(userId)
        val updatedRegister = registers.firstOrNull { it.id == regId }
        assertNotNull(updatedRegister)
        assertEquals(response, updatedRegister?.status)
    }
}
