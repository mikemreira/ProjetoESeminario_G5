package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.registers.Register
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import isel.pt.ps.projeto.repository.RegistersRepository
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

// repoee
@Component
class RegistersRepository(
    private val config: DataSourceProperties
) : RegistersRepository {

    private fun initializeConnection(): Connection {
    val dataSource = PGSimpleDataSource()
    dataSource.setURL(config.url)
    dataSource.user = config.username
    dataSource.password = config.password
    return dataSource.connection
}
    override fun getUserRegisters(userId: Int, pg: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterOutputModel> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select R.id, R.id_utilizador, R.id_obra, R.entrada, R.saida, R.status, O.nome as nome_obra from registo as R\n" +
                        "join obra as O on R.id_obra = O.id\n" +
                        "where id_utilizador = ? and (R.entrada >= COALESCE(?, R.entrada)) and (R.entrada <= COALESCE(?, R.entrada))\n" +
                        "order by R.entrada desc\n" +
                        "limit 5 offset ?\n"
                )
                pStatement.setInt(1, userId)
                pStatement.setTimestamp(2, if (startDate == null) null  else Timestamp.valueOf(startDate))
                pStatement.setTimestamp(3, if (endDate == null) null else Timestamp.valueOf(endDate))
                pStatement.setInt(4, (pg-1)*5)
                val res2 = pStatement.executeQuery()
                val list = mutableListOf<RegisterOutputModel>()
                while (res2.next()) {
                    if (res2.getTimestamp("saida") != null)
                        list.add(
                            RegisterOutputModel(
                                res2.getInt("id"),
                                res2.getInt("id_utilizador"),
                                res2.getInt("id_obra"),
                                res2.getString("nome_obra"),
                                res2.getTimestamp("entrada").toLocalDateTime(),
                                res2.getTimestamp("saida").toLocalDateTime(),
                                res2.getString("status"),
                            )
                        )
                    else {
                        list.add(
                            RegisterOutputModel(
                                res2.getInt("id"),
                                res2.getInt("id_utilizador"),
                                res2.getInt("id_obra"),
                                res2.getString("nome_obra"),
                                res2.getTimestamp("entrada").toLocalDateTime(),
                                null,
                                res2.getString("status")
                            )
                        )
                    }
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

    override fun getUserRegistersSize(userId: Int, type: String, oid: Int?, forAdmin: Boolean, startDate: LocalDateTime?, endDate: LocalDateTime?): Int {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                if (type == "unfinished") {
                    val pStatement = it.prepareStatement(
                        "select Count(*) from registo R\n" +
                            "inner join Obra o on o.id = R.id_obra\n" +
                            "where id_utilizador = COALESCE(?, id_utilizador)\n" +
                            "      and (R.status = ? or R.status = ?) and o.id = COALESCE(?, o.id)\n" +
                            "      and (R.entrada >= COALESCE(?, R.entrada)) and (R.entrada <= COALESCE(?, R.entrada))"
                    )
                    if (!forAdmin)
                        pStatement.setInt(1, userId)
                    else
                        pStatement.setNull(1, java.sql.Types.INTEGER)

                    pStatement.setString(2, type)
                    pStatement.setString(3, "unfinished_nfc")
                    if (oid != null) {
                        pStatement.setInt(4, oid)
                    } else {
                        pStatement.setNull(4, java.sql.Types.INTEGER)
                    }

                    pStatement.setTimestamp(5, if (startDate == null) null  else Timestamp.valueOf(startDate))
                    pStatement.setTimestamp(6, if (endDate == null) null else Timestamp.valueOf(endDate))
                    val res = pStatement.executeQuery()
                    res.next()
                    res.getInt("Count")

                } else {
                    val pStatement = it.prepareStatement(
                        "select Count(*) from registo R\n" +
                            "where id_utilizador = COALESCE(?, id_utilizador)\n" +
                            "      and R.status = COALESCE(?, R.status) and R.id_obra = COALESCE(?, R.id_obra)\n" +
                            "      and (R.entrada >= COALESCE(?, R.entrada)) and (R.entrada <= COALESCE(?, R.entrada))"
                    )
                    if (!forAdmin)
                        pStatement.setInt(1, userId)
                    else
                        pStatement.setNull(1, java.sql.Types.INTEGER)

                    pStatement.setString(2, if (type == "total") null else type)
                    if (oid != null) {
                        pStatement.setInt(3, oid)
                    } else {
                        pStatement.setNull(3, java.sql.Types.INTEGER)
                    }
                    pStatement.setTimestamp(4, if (startDate == null) null  else Timestamp.valueOf(startDate))
                    pStatement.setTimestamp(5, if (endDate == null) null else Timestamp.valueOf(endDate))
                    val res = pStatement.executeQuery()
                    res.next()
                    res.getInt("Count")
                }


            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun addUserRegisterEntry(userId: Int, obraId: Int, time: LocalDateTime, type: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {

                val pStatement = it.prepareStatement(
                    "insert into registo(id_utilizador, id_obra, entrada, status) values(?, ?, ?, ?)"
                )
                pStatement.setInt(1, userId)
                pStatement.setInt(2, obraId)

                val stamp = Timestamp.valueOf(time)
                pStatement.setTimestamp(3, stamp)
                pStatement.setString(4, type)
                pStatement.executeUpdate()
                true
            } catch (e: Exception) {
                it.rollback()
                false
            } finally {
                it.commit()
            }
        }
    }

    override fun getLatestEntryRegisterId(userId: Int, oid: Int): Register? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select * from registo " +
                        "where id_utilizador=? and id_obra=? \n" +
                        "order by entrada desc\n" +
                        "limit 1 "
                )
                pStatement.setInt(1, userId)
                pStatement.setInt(2, oid)
                val res = pStatement.executeQuery()
                if (res.next()) {
                    val saida = res.getTimestamp("saida")
                    Register(
                        res.getInt("id"),
                        res.getInt("id_utilizador"),
                        res.getInt("id_obra"),
                        res.getTimestamp("entrada").toLocalDateTime(),
                        if (saida != null) saida.toLocalDateTime() else null,
                        res.getString("status")
                    )
                }
                else
                    null
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getIncompleteRegistersFromConstruction(
        userId: Int,
        oid: Int,
        role: String,
        page: Int,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pg = (page-1)*5
                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status\n" +
                        "FROM Utilizador u \n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "WHERE r.id_utilizador = COALESCE(?, r.id_utilizador) and r.id_obra = ? and r.status in ('unfinished', 'unfinished_nfc') and (R.entrada >= COALESCE(?, R.entrada)) and (R.entrada <= COALESCE(?, R.entrada))\n" +
                        "order by R.entrada desc\n" +
                        "LIMIT 5 offset ?"
                )
                if (role=="admin")
                    pStatement.setNull(1,java.sql.Types.INTEGER)
                else
                    pStatement.setInt(1, userId)
                pStatement.setInt(2, oid)
                pStatement.setTimestamp(3, if (startDate == null) null  else Timestamp.valueOf(startDate))
                pStatement.setTimestamp(4, if (endDate == null) null else Timestamp.valueOf(endDate))
                pStatement.setInt(5, pg)
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
        }    }

    override fun addUserRegisterExit(regId: Int,userId: Int, obraId: Int, role: String, time: LocalDateTime) : Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "update registo " +
                        "set saida=?, " +
                        "    status= ? " +
                        "where id_utilizador=? and id_obra=? and id = ?"
                )
                val stamp = Timestamp.valueOf(time)
                pStatement.setTimestamp(1, stamp)
                pStatement.setString(2, if (role == "admin") "completed" else "pending")
                pStatement.setInt(3, userId)
                pStatement.setInt(4, obraId)
                pStatement.setInt(5, regId)
                pStatement.executeUpdate()

                true
            } catch (e: Exception) {
                it.rollback()
                false
            } finally {
                it.commit()
            }
        }
    }

    override fun addUserRegisterNFC(reg: Register?, userId: Int, obraId: Int, time: LocalDateTime, role: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                if(reg != null){
                    val saidaStatement = it.prepareStatement("" +
                        "update registo\n" +
                        "set saida = ?, \n" +
                        "    status = ? \n" +
                        "where id_utilizador = ? and id_obra = ? and id = ?"
                    )
                    val stamp = Timestamp.valueOf(time)
                    saidaStatement.setTimestamp(1, stamp)
                    if (reg.status == "unfinished_nfc" || role == "admin")
                        saidaStatement.setString(2, "completed")
                    else
                        saidaStatement.setString(2, "pending")
                    saidaStatement.setInt(3, userId)
                    saidaStatement.setInt(4, obraId)
                    saidaStatement.setInt(5, reg.id)
                    saidaStatement.executeUpdate()
                } else {
                    val pStatement = it.prepareStatement(
                        "insert into registo(id_utilizador, id_obra, entrada, status) values(?, ?, ?, 'unfinished_nfc')"
                    )
                    pStatement.setInt(1, userId)
                    pStatement.setInt(2, obraId)
                    val stamp = Timestamp.valueOf(time)
                    pStatement.setTimestamp(3, stamp)
                    pStatement.executeUpdate()
                }
                true
            } catch (e: Exception) {
                it.rollback()
                false
            } finally {
                it.commit()
            }
        }
    }

    override fun insertExitOnWeb(userId: Int, regId: Int, obraId: Int, role: String, endTime: LocalDateTime): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {

                val pStatement = it.prepareStatement(
                    "update registo " +
                        "set saida=?, " +
                        "    status= ? " +
                        "where id_utilizador=? and id_obra=? and id = ?"
                )
                val stamp = Timestamp.valueOf(endTime)
                pStatement.setTimestamp(1, stamp)
                if (role == "admin")
                    pStatement.setString(2, "completed")
                else
                    pStatement.setString(2, "pending")
                pStatement.setInt(3, userId)
                pStatement.setInt(4, obraId)
                pStatement.setInt(5, regId)
                pStatement.executeUpdate()

                true
            } catch (e: Exception) {
                it.rollback()
                false
            } finally {
                it.commit()
            }
        }    }

    override fun getUsersRegistersFromConstruction(oid: Int, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pg = (page-1)*5
                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status, o.status as obraStatus\n" +
                        "FROM Utilizador u \n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "INNER JOIN Obra o on o.id = r.id_obra \n" +
                        "WHERE r.id_obra = ? and (R.entrada >= COALESCE(?, R.entrada)) and (R.entrada <= COALESCE(?, R.entrada))\n" +
                        "order by R.entrada desc\n" +
                        "LIMIT 5 offset ?"
                )
                pStatement.setInt(1, oid)
                pStatement.setTimestamp(2, if (startDate == null) null  else Timestamp.valueOf(startDate))
                pStatement.setTimestamp(3, if (endDate == null) null else Timestamp.valueOf(endDate))
                pStatement.setInt(4, pg)
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

    override fun getUserRegisterFromConstruction(userId: Int, oid: Int, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status\n" +
                        "FROM Utilizador u \n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "WHERE r.id_obra = ? and u.id = ? and (R.entrada >= COALESCE(?, R.entrada)) and (R.entrada <= COALESCE(?, R.entrada))\n" +
                        "order by R.entrada desc\n" +
                        "LIMIT 5 offset ?"
                )
                pStatement.setInt(1, oid)
                pStatement.setInt(2, userId)
                pStatement.setTimestamp(3, if (startDate == null) null  else Timestamp.valueOf(startDate))
                pStatement.setTimestamp(4, if (endDate == null) null else Timestamp.valueOf(endDate))
                pStatement.setInt(5, (page-1)*5)

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

    override fun getPendingRegistersFromConstruction(oid: Int, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pg = (page-1)*5
                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status\n" +
                        "FROM Utilizador u \n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "WHERE r.id_obra = ? and r.status = 'pending' and (R.entrada >= COALESCE(?, R.entrada)) and (R.entrada <= COALESCE(?, R.entrada))\n" +
                        "order by R.entrada desc\n" +
                        "LIMIT 5 offset ?"
                )
                pStatement.setInt(1, oid)
                pStatement.setTimestamp(2, if (startDate == null) null  else Timestamp.valueOf(startDate))
                pStatement.setTimestamp(3, if (endDate == null) null else Timestamp.valueOf(endDate))
                pStatement.setInt(4, pg)
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

    override fun getPendingRegisters(userId: Int): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "Select * from Registo r \n" +
                        "inner join utilizador u on r.id_utilizador = u.id \n" +
                        "where r.id_obra in (Select id_obra From Utilizador u2 \n" +
                        "Inner join Papel p on u2.id = p.id_utilizador \n" +
                        "where u2.id = ? and papel = 'admin') and r.status = 'pending'\n" +
                        "order by R.entrada desc\n"
                )
                pStatement.setInt(1, userId)
                val result = pStatement.executeQuery()
                val registers = mutableListOf<RegisterAndUser>()
                while (result.next()){
                    val saida = result.getTimestamp("saida")
                    registers.add(
                        RegisterAndUser(
                            result.getString("nome"),
                            result.getInt("id"),
                            result.getInt("id_obra"),
                            result.getInt("id_utilizador"),
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

    override fun acceptOrDeny(userId: Int, oid: Int, registerId: Int, response: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "UPDATE Registo \n" +
                        "SET status = ? \n" +
                        "WHERE id_utilizador = ? AND id_obra = ? AND id = ?"
                )
                println(response)
                pStatement.setString(1, response)
                pStatement.setInt(2, userId)
                pStatement.setInt(3, oid)
                pStatement.setInt(4, registerId)
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

    override fun getIncompleteRegisters(userId: Int, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterOutputModel> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select R.id, R.id_utilizador, R.id_obra, R.entrada, R.saida, R.status, O.nome as nome_obra from registo as R\n"
                        + "join obra as O on R.id_obra = O.id\n"
                        + "where id_utilizador = ? and R.status in ('unfinished','unfinished_nfc') and (R.entrada >= COALESCE(?, R.entrada)) and (R.entrada <= COALESCE(?, R.entrada))\n"
                        + "order by R.entrada desc\n"
                        + "limit 5 offset ?\n"
                )
                pStatement.setInt(1, userId)
                pStatement.setTimestamp(2, if (startDate == null) null  else Timestamp.valueOf(startDate))
                pStatement.setTimestamp(3, if (endDate == null) null else Timestamp.valueOf(endDate))
                pStatement.setInt(4, (page-1)*5)
                val res2 = pStatement.executeQuery()
                val list = mutableListOf<RegisterOutputModel>()
                while (res2.next()) {
                    //res2.getTimestamp("saida") != null
                    val saida = res2.getTimestamp("saida")
                        list.add(
                            RegisterOutputModel(
                                res2.getInt("id"),
                                res2.getInt("id_utilizador"),
                                res2.getInt("id_obra"),
                                res2.getString("nome_obra"),
                                res2.getTimestamp("entrada").toLocalDateTime(),
                                if(saida != null) saida.toLocalDateTime() else null,
                                res2.getString("status"),
                            )
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

    override fun deleteRegister(userId: Int, obraId: Int, registerId: Int): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "delete from Registo " +
                        "where id = ? and id_utilizador = ? and id_obra = ? "
                )
                pStatement.setInt(1, registerId)
                pStatement.setInt(2, userId)
                pStatement.setInt(3, obraId)
                pStatement.executeUpdate()
                true
            } catch (e: Exception) {
                it.rollback()
                false
            } finally {
                it.commit()
            }
        }
    }

}