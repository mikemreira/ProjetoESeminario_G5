package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.Construction
import isel.pt.ps.projeto.models.User
import isel.pt.ps.projeto.repository.ConstructionRepository
import kotlinx.datetime.toLocalDate
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection



@Component
class ConstructionsRepository : ConstructionRepository {

    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseUrl)
        return dataSource.connection
    }

    override fun getConstruction(oid: Int): Construction {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select * from obra \n"+
                        "where id = ?"
                )
                pStatement.setInt(1, oid)
                val result = pStatement.executeQuery()
                result.next()

                Construction(
                    result.getInt("id"),
                    result.getString("nome"),
                    result.getString("localização"),
                    result.getString("descrição"),
                    result.getDate("data_inicio").toString().toLocalDate(),
                    result.getDate("data_fim").toString().toLocalDate(),
                    result.getString("status")
                )
            } catch (e: Exception){
                it.rollback()
                throw e
            }finally {
                it.commit()
            }
        }
    }

    override fun getConstructionsUsers(oid: Int): List<User> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select ut.id, ut.nome, ut.email, ut.morada from utilizador ut\n" +
                        "inner join papel pa on pa.id_utilizador = ut.id\n" +
                        "inner join obra o on o.id = pa.id_obra\n" +
                        "where o.id = ?"
                )
                pStatement.setInt(1, oid)
                val result = pStatement.executeQuery()
                val list = mutableListOf<User>()
                while (result.next()) {
                    list.add(User(result.getInt("id"), result.getString("nome"), result.getString("email"), result.getString("morada")))
                }
                list
            } catch (e: Exception){
                it.rollback()
                throw e
            }finally {
                it.commit()
            }
        }    }
}