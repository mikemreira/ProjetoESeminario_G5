package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.constructions.ConstructionAndRole
import isel.pt.ps.projeto.models.role.Role
import isel.pt.ps.projeto.models.users.SimpleUser
import isel.pt.ps.projeto.repository.InviteRepository
import kotlinx.datetime.toLocalDate
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException

@Component
class InviteRepository(
    private val config: DataSourceProperties
) : InviteRepository {

    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(config.url)
        dataSource.user = config.username
        dataSource.password = config.password
        return dataSource.connection
    }
/*
    override fun getInvitedToConstruction(oid: Int): List<SimpleUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select * from Utilizador u\n" +
                        "inner join UtilizadorImagem ui on ui.id_utilizador = u.id\n" +
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
                            result.getString("list")
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

 */

    override fun inviteToConstruction(oid: Int, email: String, function: String, role: String): String {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement("insert into Convite (email, id_obra, funcao, papel) values (?,?,?,?)")
                pStatement.setString(1, email)
                pStatement.setInt(2, oid)
                pStatement.setString(3, function)
                pStatement.setString(4, role)
                pStatement.executeUpdate()
                "invited"
            }  catch (e: SQLException) {
                if (e.sqlState == "23505") { // SQL state for unique violation
                    println("Duplicate entry: $email, $oid")
                    ""
                } else {
                    throw e
                }
            } finally {
                it.commit()
            }
        }
    }

    override fun invited(email: String): List<ConstructionAndRole> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select * from Convite c\n" +
                    "inner join Obra o on c.id_obra = o.id\n" +
                    "where c.email = ?"
                )
                println(email)
                pStatement.setString(1, email)
                val result = pStatement.executeQuery()
                val list = mutableListOf<ConstructionAndRole>()
                while (result.next()) {
                    val dt_fim = result.getString("data_fim")
                    list.add(
                        ConstructionAndRole(
                            Construction(
                                result.getInt("id"),
                                result.getString("nome"),
                                result.getString("localizacao"),
                                result.getString("descricao"),
                                result.getString("data_inicio").toLocalDate(),
                                if(dt_fim != null) result.getString("data_fim").toLocalDate() else null,
                                result.getString("status"),
                                result.getBytes("foto")
                            ),
                            Role(
                                result.getString("papel"),
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

    override fun acceptOrDeny(email: String, oid: Int, response: String): String {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val statement = it.prepareStatement("SELECT papel FROM Convite WHERE email=?;")
                statement.setString(1, email)
                val rs = statement.executeQuery()

                var papel: String? = null

                if (rs.next()) papel = rs.getString("papel")

                val pStatement = it.prepareStatement(
                    "UPDATE Convite \n" +
                        "SET status = ? \n" +
                        "WHERE email = ? AND id_obra = ?"
                )
                println(response)
                pStatement.setString(1, response)
                pStatement.setString(2, email)
                pStatement.setInt(3, oid)
                pStatement.executeUpdate()
                papel ?: ""
            }  catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }
}