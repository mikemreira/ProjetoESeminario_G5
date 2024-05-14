package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.User
import isel.pt.ps.projeto.repository.UserRepository
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager

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

    override fun addUser(id: Int, nome: String, email: String, morada: String): User {
        TODO("Not yet implemented")
    }

    override fun editUser(nome: String, email: String, morada: String): User {
        TODO("Not yet implemented")
    }

    override fun deleteUser(id: Int): User {
        TODO("Not yet implemented")
    }
}
