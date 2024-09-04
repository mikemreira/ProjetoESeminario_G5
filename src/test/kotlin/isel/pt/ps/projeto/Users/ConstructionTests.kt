package isel.pt.ps.projeto.Users

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.domain.users.UsersDomain
import isel.pt.ps.projeto.models.constructions.ConstructionEditInputModel
import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql


@SpringBootTest
@ActiveProfiles("test")
@Sql(
    scripts = ["/testScheme.sql", "/testInsert.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class ConstructionTests(
    @Autowired
    private val usersRepository: UsersRepository,
    @Autowired
    private val constructionsRepository: ConstructionsRepository
) {

    @BeforeEach
    fun setUp() {
        // Assuming that UsersRepository has a method to save users.
        usersRepository.signUp("Test User 4", "test4@example.com", PasswordValidationInfo("mypassword"))
        usersRepository.signUp("Test User 5", "test5@example.com", PasswordValidationInfo("mypassword"))

        // Additionally, you can create initial constructions, if necessary.
        constructionsRepository.createConstruction(
            userId = 3,
            name = "Test Construction",
            location = "Test Location",
            description = "Test Description",
            startDate = "2024-07-21".toLocalDate(),
            endDate = "2024-09-21".toLocalDate(),
            foto = null,
            status = "active",
            function = "Outro"
        )
    }

    @Test
    fun `test getConstruction by ID`() {
        val construction = constructionsRepository.getConstruction(3)
        assertNotNull(construction)
        assertEquals("Test Construction", construction?.nome)
    }

    @Test
    fun `test getConstructionByNFCID`() {
        val nfcId = "testNFC"
        constructionsRepository.editNfc(3, nfcId)
        val construction = constructionsRepository.getConstructionByNFCID(nfcId)
        assertNotNull(construction)
        assertEquals("Test Construction", construction?.nome)
    }

    @Test
    fun `test getConstructionsUsers`() {
        val users = constructionsRepository.getConstructionsUsers(1)
        assertNotNull(users)
        assertTrue(users.isNotEmpty())
        assertEquals(1, users[0].id)
    }

    @Test
    fun `test getConstructionUser`() {
        val user = constructionsRepository.getConstructionUser(1, 1)
        assertNotNull(user)
        assertEquals("Test User 1", user?.nome)
    }

    @Test
    fun `test getConstructionsOfUser`() {
        val constructions = constructionsRepository.getConstructionsOfUser(1, "on going")
        assertNotNull(constructions)
        assertTrue(constructions.isNotEmpty())
        assertEquals("Obra A", constructions[0].nome)
    }

    @Test
    fun `test createConstruction`() {
        val newConstructionId = constructionsRepository.createConstruction(
            userId = 1,
            name = "New Construction",
            location = "New Location",
            description = "New Description",
            startDate = "2024-07-21".toLocalDate(),
            endDate = "2024-09-21".toLocalDate(),
            foto = null,
            status = "active",
            function = "Outro"
        )
        val newConstruction = constructionsRepository.getConstruction(newConstructionId)
        assertNotNull(newConstruction)
        assertEquals("New Construction", newConstruction?.nome)
    }

    @Test
    fun `test getUserRoleFromConstruction`() {
        val role = constructionsRepository.getUserRoleFromConstruction(1, 1)
        assertNotNull(role)
        assertEquals("admin", role?.role)
    }
    /*
    @Test
    fun `test getUserByEmailFromConstructions`() {
        val user = constructionsRepository.getUserByEmailFromConstructions(1, "test1@example.com")
        assertNotNull(user)
        assertEquals("Test User 1", user?.nome)
    }

     */

    @Test
    fun `test isUserAssociatedWithConstructionByEmail`() {
        val isAssociated = constructionsRepository.isUserAssociatedWithConstructionByEmail(1, "test1@example.com")
        assertTrue(isAssociated)
    }

    @Test
    fun `test editConstruction`() {
        val editModel = ConstructionEditInputModel(
            name = "Updated Construction",
            location = "Updated Location",
            description = "Updated Description",
            startDate = "2024-07-21".toLocalDate().toJavaLocalDate(),
            endDate = "2024-09-21".toLocalDate().toJavaLocalDate(),
            foto = null,
            status = "on going",
            function = "Outro"
        )
        val updatedConstruction = constructionsRepository.editConstruction(1, 1, editModel)
        assertNotNull(updatedConstruction)
        println(updatedConstruction?.data_fim)
        assertEquals("Updated Construction", updatedConstruction?.nome)
    }

    @Test
    fun `test removeConstructionUser`() {
        val result = constructionsRepository.removeConstructionUser(1, 1)
        assertTrue(result)
        val user = constructionsRepository.getConstructionUser(1, 1)
        assertNull(user)
    }

    @Test
    fun `test getNfc`() {
        val nfcId = "testNFC"
        constructionsRepository.editNfc(1, nfcId)
        val fetchedNfcId = constructionsRepository.getNfc(1)
        assertEquals(nfcId, fetchedNfcId)
    }

/*
    @Test
    fun `test inviteToConstruction`() {
        val result = constructionsRepository.inviteToConstruction(1, "invitee@example.com")
        assertTrue(result)
    }



    @Test
    fun `test registerIntoConstruction`() {
        constructionsRepository.registerIntoConstruction(
            userId = 2,
            oid = 1,
            startTime = LocalDateTime.now().minusHours(1),
            endTime = LocalDateTime.now(),
            role = "worker"
        )
        val registers = constructionsRepository.getRegisters(2, 1, "worker", RegisterQuery())
        assertNotNull(registers)
        assertTrue(registers.isNotEmpty())
    }

 */

}
