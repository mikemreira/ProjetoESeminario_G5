package isel.pt.ps.projeto.Users

import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import isel.pt.ps.projeto.repository.jdbc.InviteRepository
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


@SpringBootTest
@ActiveProfiles("test")
@Sql(
    scripts = ["/testScheme.sql", "/testInsert.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class InviteTests(
    @Autowired
    private val inviteRepository: InviteRepository
) {


    @Test
    fun `test inviteToConstruction adds new invite`() {
        // Given
        val obraId = 1
        val email = "test3@example.com"
        val function = "Outro"
        val role = "admin"

        // When
        val result = inviteRepository.inviteToConstruction(obraId, email, function, role)

        // Then
        assertTrue(result)

        // Verify that the invite was added
        val invited = inviteRepository.invited(email)
        assertTrue(invited.any { it.construction.oid == obraId })
    }

    // teste desnecesário
    @Test
    fun `test inviteToConstruction handles duplicate entry`() {
        // Given
        val obraId = 2
        val email = "test1@example.com"
        val function = "Manager"
        val role = "admin"

        // When
        val result = inviteRepository.inviteToConstruction(obraId, email, function, role)

        // Then
        assertFalse(result)
    }

    @Test
    fun `test invited returns correct constructions and roles`() {
        // Given
        val email = "test1@example.com"  // Assuming this email has invitations

        // When
        val invites = inviteRepository.invited(email)

        // Then
        assertNotNull(invites)
        assertTrue(invites.isNotEmpty())
        assertTrue(invites.all { it.construction.oid in listOf(2) })  // Assuming construction IDs 1 and 2
    }

}
