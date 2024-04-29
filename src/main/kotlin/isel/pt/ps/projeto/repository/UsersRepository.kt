package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.User
import org.springframework.stereotype.Component
import java.sql.DriverManager

const val JDBC_URL = "jdbc:postgresql://localhost:5432/postgres"

@Component
class UsersRepository {
    fun getUsers(): List<User> {
        val conn = DriverManager.getConnection(JDBC_URL, "postgres", "postgres")
        val statement = conn.createStatement()
        val result = statement.executeQuery("select * from utilizador")
        val list = mutableListOf<User>()
        while (result.next()) {
            list.add(User(result.getInt("id"), result.getString("nome"), result.getString("email"), result.getString("morada")))
        }
        return list
    }
}
