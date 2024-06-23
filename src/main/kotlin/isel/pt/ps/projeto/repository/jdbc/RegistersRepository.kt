package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import isel.pt.ps.projeto.repository.RegistersRepository
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
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
                val pStatement2 = it.prepareStatement("select * from registo as R\n"
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
                                res2.getString("nome"),
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
                                res2.getString("nome"),
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
}