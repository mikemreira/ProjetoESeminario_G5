package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import isel.pt.ps.projeto.repository.RegistersRepository
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.sql.Timestamp


@Component
class RegistersRepository : RegistersRepository {

    private fun initializeConnection(): Connection {
    val dataSource = PGSimpleDataSource()
    dataSource.setURL(jdbcDatabaseUrl)
    return dataSource.connection
}
    override fun getUserRegisters(userId: Int): List<RegisterOutputModel> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement2 = it.prepareStatement(
                    "select R.id, R.id_utilizador, R.id_obra, R.entrada, R.saida, R.status, O.nome as nome_obra from registo as R\n"
                    + "join obra as O on R.id_obra = O.id\n"
                    + "where id_utilizador = ?")
                pStatement2.setInt(1, userId)
                val res2 = pStatement2.executeQuery()
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

    override fun addUserRegisterEntry(userId: Int, obraId: Int, time: java.time.LocalDateTime): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "insert into registo(id_utilizador, id_obra, entrada) values(?, ?, ?)"
                )
                pStatement.setInt(1, userId)
                pStatement.setInt(2, obraId)

                val stamp = Timestamp.valueOf(time)
                pStatement.setTimestamp(3, stamp)

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

    override fun addUserRegisterExit(userId: Int, obraId: Int, time: java.time.LocalDateTime) : Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "update registo set saida=? where id_utilizador=? and id_obra=?"
                )
                val stamp = Timestamp.valueOf(time)
                pStatement.setTimestamp(1, stamp)
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

    override fun getUsersRegistersFromConstruction(oid: Int, page: Int): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pg = (page-1)*10
                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status, o.status as obraStatus\n" +
                        "FROM Utilizador u \n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "INNER JOIN Obra o on o.id = r.id_obra \n" +
                        "WHERE r.id_obra = ?\n"+
                        "LIMIT 10 offset ?"
                )
                pStatement.setInt(1, oid)
                pStatement.setInt(2, pg)
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

    override fun getUserRegisterFromConstruction(userId: Int, oid: Int, page: Int): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status\n" +
                        "FROM Utilizador u \n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "WHERE r.id_obra = ? and u.id = ?\n"+
                        "LIMIT 10 offset ?"
                )
                pStatement.setInt(1, oid)
                pStatement.setInt(2, userId)
                pStatement.setInt(3, (page-1)*10)

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

    override fun getPendingRegistersFromConstruction(oid: Int, page: Int): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pg = (page-1)*10
                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status\n" +
                        "FROM Utilizador u \n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "WHERE r.id_obra = ? and r.status = 'pending' \n"+
                        "LIMIT 10 offset ?"
                )
                pStatement.setInt(1, oid)
                pStatement.setInt(2, pg)
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

    override fun getPendingRegisters(userId: Int, page: Int): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pg = (page-1)*10
                val pStatement = it.prepareStatement(
                    "Select * from Registo r \n" +
                        "inner join utilizador u on r.id_utilizador = u.id \n" +
                        "where r.id_obra =(Select id_obra From Utilizador u2 \n" +
                        "Inner join Papel p on u2.id = p.id_utilizador \n" +
                        "where u2.id = ? and papel = 'admin') and r.status = 'pending'\n" +
                        "LIMIT 10 offset ?"
                )
                pStatement.setInt(1, userId)
                pStatement.setInt(2, pg)
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


}