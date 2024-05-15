package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.User
import isel.pt.ps.projeto.models.UserAndToken
import isel.pt.ps.projeto.repository.UserRepository
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.UUID

const val JDBC_URL = "jdbc:postgresql://localhost:5432/postgres"
const val jdbcDatabaseUrl = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"

@Component
class UsersRepository : UserRepository {
    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseUrl)
        return dataSource.connection
    }

    override fun getUsers(): List<User> {
        val conn = DriverManager.getConnection(JDBC_URL, "postgres", "postgres")
        val statement = conn.createStatement()
        val result = statement.executeQuery("select * from utilizador")
        val list = mutableListOf<User>()
        while (result.next()) {
            list.add(User(result.getInt("id"), result.getString("nome"), result.getString("email"), result.getString("morada")))
        }
        return list
    }

    override fun getUserById(id: Int): User {
        TODO("Not yet implemented")
    }

    override fun signUp(
        nome: String,
        email: String,
        pass: String,
    ): User {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement("insert into utilizador (nome, email, pass) values(?, ?, ?)")
                pStatement.setString(1, nome)
                pStatement.setString(2, email)
                pStatement.setString(3, pass)
                pStatement.executeUpdate()
                val statement = it.prepareStatement("select * from utilizador where email=?")
                statement.setString(1, email)
                val result = statement.executeQuery()
                result.next()
                User(result.getInt("id"), result.getString("nome"), result.getString("email"), "")
            } catch (e: SQLException) {
                it.rollback()
                if (e.sqlState == "23505") {
                    throw IllegalArgumentException("User email already exists")
                }
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun signIn(
        email: String,
        password: String,
    ): UserAndToken {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val statement = it.prepareStatement("select * from utilizador where email=? and pass=?")
                statement.setString(1, email)
                statement.setString(2, password)
                val result = statement.executeQuery()
                result.next()
                val token = UUID.randomUUID()
                val pStatement2 = it.prepareStatement("insert into token values(?, ?)")
                val id = result.getInt("id")
                pStatement2.setString(1, token.toString())
                pStatement2.setInt(2, id)
                pStatement2.executeUpdate()
                val user = User(result.getInt("id"), result.getString("nome"), result.getString("email"), "")
                UserAndToken(user, token.toString())
            } catch (e: SQLException) {
                it.rollback()
                if (e.sqlState == "23505") {
                    throw IllegalArgumentException("User email already exists")
                }
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun editUser(
        nome: String,
        email: String,
        morada: String,
    ): User {
        TODO("Not yet implemented")
    }

    override fun deleteUser(id: Int): User {
        TODO("Not yet implemented")
    }
}
