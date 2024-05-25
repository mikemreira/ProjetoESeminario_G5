package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.users.User
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
                val pStatement =
                    it.prepareStatement(
                        "select * from obra \n" +
                            "where id = ?",
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
                    result.getString("status"),
                )
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

    override fun createConstruction(userId: Int, construction: Construction): Int {
        TODO("Not yet implemented")
    }

    override fun checkConstructionByName(name: String): Boolean {
        TODO("Not yet implemented")
    }
}
