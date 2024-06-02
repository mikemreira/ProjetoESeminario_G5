package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.registers.RegistoOutputModel
import isel.pt.ps.projeto.repository.RegistosRepository
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Timestamp
import java.time.Instant

@Component
class RegistosRepository: RegistosRepository {

    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseUrl)
        return dataSource.connection
    }
    override fun getUserRegisters(userId: Int): List<RegistoOutputModel> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement2 = it.prepareStatement("select * from registo as R\n"
                    + "join obra as O on R.id_obra = O.id\n"
                    + "where id_utilizador = ?")
                pStatement2.setInt(1, userId)
                val res2 = pStatement2.executeQuery()
                val list = mutableListOf<RegistoOutputModel>()
                while (res2.next()) {
                    if (res2.getTimestamp("saida") != null)
                        list.add(RegistoOutputModel(
                            res2.getTimestamp("entrada").toLocalDateTime(),
                            res2.getTimestamp("saida").toLocalDateTime(),
                            res2.getString("nome")
                        ))
                    else {
                        list.add(RegistoOutputModel(
                            res2.getTimestamp("entrada").toLocalDateTime(),
                            null,
                            res2.getString("nome")
                            ))
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

    override fun addUserRegisterEntrance(userId: Int, obraId: Int, time: LocalDateTime) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                val pStatement = it.prepareStatement(
                    "insert into registo(id_utilizador, id_obra, entrada) values(?, ?, ?)"
                )
                pStatement.setInt(1, userId)
                pStatement.setInt(2, obraId)

                val stamp = Timestamp.valueOf(time.toJavaLocalDateTime())
                pStatement.setTimestamp(3, stamp)

                pStatement.executeUpdate()
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun addUserRegisterSaida(userId: Int, obraId: Int, time: LocalDateTime) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                val pStatement = it.prepareStatement(
                    "update registo set saida=? where id_utilizador=? and id_obra=?"
                )
                val stamp = Timestamp.valueOf(time.toJavaLocalDateTime())
                pStatement.setTimestamp(1, stamp)
                pStatement.setInt(2, userId)
                pStatement.setInt(3, obraId)

                pStatement.executeUpdate()
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }
}