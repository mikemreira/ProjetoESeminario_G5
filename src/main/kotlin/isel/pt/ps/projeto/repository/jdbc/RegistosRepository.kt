package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.registers.RegistoOutputModel
import isel.pt.ps.projeto.repository.RegistosRepository
import kotlinx.datetime.LocalDateTime
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection

@Component
class RegistosRepository: RegistosRepository {

    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseUrl)
        return dataSource.connection
    }
    override fun getUserRegisters(id: Int): List<RegistoOutputModel> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement2 = it.prepareStatement("select * from registo as R\n"
                    + "join obra as O on R.id_obra = O.id\n"
                    + "where id_utilizador = ?")
                pStatement2.setInt(1, id)
                val res2 = pStatement2.executeQuery()
                val list = mutableListOf<RegistoOutputModel>()
                while (res2.next()) {
                    list.add(RegistoOutputModel(
                        res2.getTimestamp("entrada").toLocalDateTime(),
                        res2.getTimestamp("saida").toLocalDateTime(),
                        res2.getString("nome")
                    ))
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
}