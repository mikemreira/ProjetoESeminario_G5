package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.repository.ConstructionRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException


@Component
class ConstructionsRepository : ConstructionRepository {
    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseUrl)
        return dataSource.connection
    }

    override fun getConstruction(oid: Int): Construction? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement =
                    it.prepareStatement(
                        "select * from obra \n" +
                            "where id = ?",
                    )
                pStatement.setInt(1, oid)
                val result = pStatement.executeQuery()
                if (!result.next())
                    null
                else {
                    val dateFim = result.getDate("data_fim")
                    Construction(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("localização"),
                        result.getString("descrição"),
                        result.getDate("data_inicio").toString().toLocalDate(),
                        if (dateFim == null) dateFim else dateFim.toString().toLocalDate(),
                        result.getString("status"),
                    )
                }
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getConstructionsUsers(oid: Int): List<User> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement =
                    it.prepareStatement(
                        "select ut.id, ut.nome, ut.email, ut.morada from utilizador ut\n" +
                            "inner join papel pa on pa.id_utilizador = ut.id\n" +
                            "inner join obra o on o.id = pa.id_obra\n" +
                            "where o.id = ?",
                    )
                pStatement.setInt(1, oid)
                val result = pStatement.executeQuery()
                val list = mutableListOf<User>()
                while (result.next()) {
                    list.add(User(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("email"),
                        PasswordValidationInfo(result.getString("pass")),
                        result.getString("morada")))
                }
                list
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getConstructionsOfUser(id: Int): List<Construction> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement =
                    it.prepareStatement(
                        "select o.id, o.nome, o.localização, o.descrição, o.data_inicio, o.data_fim, o.status from utilizador u\n" +
                            "inner join papel p on p.id_utilizador = u.id\n" +
                            "inner join obra o on o.id = p.id_obra\n" +
                            "where u.id = ?",
                    )
                pStatement.setInt(1, id)
                val result = pStatement.executeQuery()
                val list = mutableListOf<Construction>()
                while (result.next()) {
                    list.add(
                        Construction(
                            result.getInt("id"),
                            result.getString("nome"),
                            result.getString("localização"),
                            result.getString("descrição"),
                            result.getDate("data_inicio").toString().toLocalDate(),
                            null,
                            result.getString("status"),
                        ),
                    )
                }
                list
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun createConstruction(
        userId: Int,
        name: String,
        location: String,
        description: String,
        startDate: LocalDate,
        endDate: LocalDate?,
        foto: String?,
        status: String?
    ): Int {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val generatedColumns = arrayOf("id")
                val insertStatement = it.prepareStatement(
                    "INSERT INTO Obra (nome, localização, descrição, data_inicio)\n" +
                    "VALUES (?,?,?,?) ", generatedColumns
                )
                insertStatement.setString(1,name)
                insertStatement.setString(2,location)
                insertStatement.setString(3,description)
                insertStatement.setDate(4,Date.valueOf(startDate.toString()))
                insertStatement.executeUpdate()
                insertStatement.generatedKeys.use { generatedKeys ->
                    if (generatedKeys.next()) {
                        val oid = generatedKeys.getInt(1)
                        val insertStatementRole = it.prepareStatement(
                            "INSERT INTO Papel (id_utilizador, id_obra, papel)\n" +
                            "VALUES\n" +
                            " (?, ?, 'admin')")
                        insertStatementRole.setInt(1, userId)
                        insertStatementRole.setInt(2, oid)
                        insertStatementRole.executeUpdate()
                        oid
                    } else {
                        throw SQLException("Creating Obra failed, no ID obtained.")
                    }
                }

            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getUserRoleFromConstruction(id: Int, oid: Int): String? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val generatedColumns = arrayOf("id")
                val pStatement = it.prepareStatement(
                    "Select * from Papel\n" +
                        "where id_utilizador = ? and id_obra = ?"
                )
                pStatement.setInt(1, id)
                pStatement.setInt(2, oid)
                val result = pStatement.executeQuery()
                if (!result.next())
                    null
                else
                    result.getString("papel")
            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }


    override fun checkConstructionByName(name: String): Boolean {
        TODO("Not yet implemented")
    }
}
