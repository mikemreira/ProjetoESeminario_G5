package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.constructions.ConstructionAndRole
import isel.pt.ps.projeto.models.role.Role
import isel.pt.ps.projeto.models.users.SimpleUser
import isel.pt.ps.projeto.repository.InviteRepository
import kotlinx.datetime.toLocalDate
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException

@Component
class InviteRepository() : InviteRepository {

    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseUrl)
        return dataSource.connection
    }

    override fun getInvitedToConstruction(oid: Int): List<SimpleUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select * from Utilizador u\n" +
                    "inner join convite c on c.id_utilizador = u.id\n" +
                    "where c.id_obra = ?"
                )
                pStatement.setInt(1, oid)
                val result = pStatement.executeQuery()
                val list = mutableListOf<SimpleUser>()
                while (result.next()) {
                    list.add(
                        SimpleUser(
                            result.getInt("id"),
                            result.getString("nome"),
                            result.getString("email"),
                            result.getString("morada"),
                            result.getString("foto")
                        )
                    )
                }
                list
            }  catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun inviteToConstruction(userId: Int, oid: Int, email: String, function: String?): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement("insert into Convite (id_utilizador, email, id_obra, funcao) values (?,?,?,?)")
                pStatement.setInt(1, userId)
                pStatement.setString(2, email)
                pStatement.setInt(3, oid)
                pStatement.setString(4, function)
                pStatement.executeUpdate()
                true
            }  catch (e: SQLException) {
                if (e.sqlState == "23505") { // SQL state for unique violation
                    println("Duplicate entry: $email, $oid")
                    false
                } else {
                    throw e
                }
            } finally {
                it.commit()
            }
        }
    }

    override fun invited(userId: Int): List<ConstructionAndRole> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select * from Convite c\n" +
                    "inner join Obra o on c.id_obra = o.id\n" +
                    "where c.id_utilizador = ?"
                )
                pStatement.setInt(1, userId)
                val result = pStatement.executeQuery()
                val list = mutableListOf<ConstructionAndRole>()
                while (result.next()) {
                    val dt_fim = result.getString("data_fim")
                    list.add(
                        ConstructionAndRole(
                            Construction(
                                result.getInt("id"),
                                result.getString("nome"),
                                result.getString("localização"),
                                result.getString("descrição"),
                                result.getString("data_inicio").toLocalDate(),
                                if(dt_fim != null) result.getString("data_fim").toLocalDate() else null,
                                result.getString("status"),
                                result.getBytes("foto")
                            ),
                            Role(
                                "funcionario",
                                result.getString("funcao")
                            )
                        )
                    )
                }
                list
            }  catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun acceptOrDeny(userId: Int, oid: Int, response: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "UPDATE Convite \n" +
                        "SET status = ? \n" +
                        "WHERE id_utilizador = ? AND id_obra = ?"
                )
                println(response)
                pStatement.setString(1, response)
                pStatement.setInt(2, userId)
                pStatement.setInt(3, oid)
                pStatement.executeUpdate()
                true
            }  catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }
}