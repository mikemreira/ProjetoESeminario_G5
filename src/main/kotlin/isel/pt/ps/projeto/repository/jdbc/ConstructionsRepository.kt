package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterFilters
import isel.pt.ps.projeto.models.users.SimpleUser
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.repository.ConstructionRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDate
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException
import java.sql.Timestamp


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

    override fun getConstructionsUsers(oid: Int): List<SimpleUser> {
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
                val list = mutableListOf<SimpleUser>()
                while (result.next()) {
                    list.add(SimpleUser(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("email"),
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
                            result.getDate("data_fim").toString().toLocalDate(),
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
                    "INSERT INTO Obra (nome, localização, descrição, data_inicio, data_fim)\n" +
                    "VALUES (?,?,?,?,?) ", generatedColumns
                )
                insertStatement.setString(1,name)
                insertStatement.setString(2,location)
                insertStatement.setString(3,description)
                insertStatement.setDate(4,Date.valueOf(startDate.toString()))
                insertStatement.setDate(5, if (endDate !=null) Date.valueOf(endDate.toString()) else null)
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

    override fun getUserByEmailFromConstructions(oid: Int, email: String): SimpleUser? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement("select u.id, u.nome, u.email, u.morada from utilizador u\n" +
                    "inner join papel p on p.id_utilizador = u.id\n" +
                    "inner join obra o on o.id = p.id_obra\n" +
                    "where u.email = ?")
                pStatement.setString(1, email)
                val result = pStatement.executeQuery()
                if (!result.next())
                    null
                else
                    SimpleUser(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("email"),
                        result.getString("morada")
                    )
            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }

    }

    override fun inviteToConstruction(oid: Int, email: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement("insert into Convite (email, obra) values (?,?)")
                pStatement.setString(1, email)
                pStatement.setInt(2, oid)
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

    override fun registerIntoConstruction(userId: Int, oid: Int, startTime: LocalDateTime, endTime: LocalDateTime, role: String) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                val pStatement = it.prepareStatement("Insert into Registo (id_utilizador, id_obra, entrada, saida, status) values (?,?,?,?,?)")
                pStatement.setInt(1, userId)
                pStatement.setInt(2, oid)
                pStatement.setTimestamp(3, Timestamp.valueOf(startTime.toJavaLocalDateTime()))
                pStatement.setTimestamp(4, Timestamp.valueOf(endTime.toJavaLocalDateTime()))
                if (role == "admin")
                    pStatement.setString(5, "completed")
                else
                    pStatement.setString(5, "pending")
                pStatement.executeUpdate()
            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getRegisters(userId: Int, oid: Int, role: String, filters: RegisterFilters): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status \n" +
                        "FROM Utilizador u\n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "WHERE 1=1\n" +
                        "  AND (r.id_obra = COALESCE(?, r.id_obra) OR r.id_obra IS NULL)\n" +
                        "  AND (u.id = COALESCE(?, u.id) OR u.id IS NULL)\n" +
                        "  AND (u.nome = COALESCE(?, u.nome) OR u.nome IS NULL)\n" +
                        "  AND (r.entrada = COALESCE(?::timestamp, r.entrada) OR r.entrada IS NULL)\n" +
                        "  AND (r.saida = COALESCE(?::timestamp, r.saida) OR r.saida IS NULL)\n" +
                        "  AND (r.status = COALESCE(?, r.status) OR r.status IS NULL)"
                )
                pStatement.setInt(1, oid)
                if (role == "admin") {
                    if (filters.userId != null)
                        if (filters.mine)
                            pStatement.setInt(2, userId)
                        else
                            pStatement.setInt(2, filters.userId)
                    else pStatement.setNull(2, java.sql.Types.INTEGER)
                    pStatement.setString(3, filters.name)
                    pStatement.setTimestamp(4, if (filters.startDate == null) null else Timestamp.valueOf( filters.startDate.atStartOfDay()))
                    pStatement.setTimestamp(5, if (filters.endDate == null) null else Timestamp.valueOf( filters.endDate.atStartOfDay()))
                    pStatement.setString(6, filters.status)
                } else {
                    pStatement.setInt(2, userId)
                    pStatement.setString(3, filters.name)
                    pStatement.setTimestamp(4, if (filters.startDate == null) null else Timestamp.valueOf( filters.startDate.atStartOfDay()))
                    pStatement.setTimestamp(5, if (filters.endDate == null) null else Timestamp.valueOf( filters.endDate.atStartOfDay()))
                    pStatement.setString(6, filters.status)
                }
                val result = pStatement.executeQuery()
                val registers = mutableListOf<RegisterAndUser>()
                while (result.next()){
                    val saida = result.getTimestamp("saida")
                    registers.add(
                        RegisterAndUser(
                            result.getString("nome"),
                            result.getInt("rid"),
                            oid,
                            result.getInt("uid"),
                            result.getTimestamp("entrada").toLocalDateTime(),
                            if (saida == null) null else saida.toLocalDateTime(),
                            result.getString("status")
                        )
                    )
                }
                registers
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
