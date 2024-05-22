package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.domain.users.Token
import isel.pt.ps.projeto.domain.users.TokenValidationInfo
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.models.users.UserAndToken
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
            list.add(
                User(
                    result.getInt("id"),
                    result.getString("nome"),
                    result.getString("email"),
                    PasswordValidationInfo(result.getString("password")),
                    result.getString("morada"),
                )
            )
        }
        return list
    }

    override fun getUserById(id: Int): User {
        TODO("Not yet implemented")
    }

    override fun getUserByToken(token: String): User? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement =
                    it.prepareStatement(
                        "" +
                            "select u.id, u.nome, u.email, u.morada from token t\n" +
                            "inner join utilizador u on u.id = t.id_utilizador\n" +
                            "where t.id_utilizador = ?",
                    )
                pStatement.setString(1, token)
                val result = pStatement.executeQuery()
                if (!result.next()) {
                    null
                } else {
                    User(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("email"),
                        PasswordValidationInfo(result.getString("password")),
                        result.getString("morada"),
                    )
                }
            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getUserByEmail(email: String): User? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select * from utilizador u\n" +
                        "where email = ?"
                )
                pStatement.setString(1, email)
                val result = pStatement.executeQuery()
                if (!result.next()) {
                    null
                } else {
                    User(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("email"),
                        PasswordValidationInfo(result.getString("password")),
                        "",
                    )
                }
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

    override fun checkUserByEmail(email: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select exists (select email from utilizador u\n" +
                    "where email = ?)"
                )
                pStatement.setString(1, email)
                val result = pStatement.executeQuery()
                result.next()
                result.getBoolean("exists")
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

    override fun signUp(
        nome: String,
        email: String,
        pass: PasswordValidationInfo,
    ): User {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement("insert into utilizador (nome, email, password) values(?, ?, ?)")
                pStatement.setString(1, nome)
                pStatement.setString(2, email)
                pStatement.setString(3, pass.validationInfo)
                pStatement.executeUpdate()
                val statement = it.prepareStatement("select * from utilizador where email=?")
                statement.setString(1, email)
                val result = statement.executeQuery()
                result.next()
                User(
                    result.getInt("id"),
                    result.getString("nome"),
                    result.getString("email"),
                    PasswordValidationInfo(result.getString("password")),
                    ""
                )
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
    ): UserAndToken {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val statement = it.prepareStatement("select * from utilizador where email=?")
                statement.setString(1, email)
                val result = statement.executeQuery()
                result.next()
                val token = UUID.randomUUID()
                val pStatement2 = it.prepareStatement("insert into token values(?, ?)")
                val id = result.getInt("id")
                pStatement2.setString(1, token.toString())
                pStatement2.setInt(2, id)
                pStatement2.executeUpdate()
                val user = User(
                    result.getInt("id"),
                    result.getString("nome"),
                    result.getString("email"),
                    PasswordValidationInfo(result.getString("password")),
                    "",
                )
                UserAndToken(user, token.toString())
            } catch (e: SQLException) {
                it.rollback()
                if (e.sqlState == "23505") {
                    throw IllegalArgumentException("Unique key violation")
                }
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun signOut(token: TokenValidationInfo) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                val pStatement = it.prepareStatement("delete from tokens where token_validation = ?")
                pStatement.setString(1, token.validationInfo)
                pStatement.executeUpdate()
            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }


    override fun createToken(
        token: Token,
        maxTokens: Int
    ) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                println("3 :"+token.tokenValidationInfo.validationInfo)
                val pStatement = it.prepareStatement(
                    "delete from Tokens\n" +
                    "                 where id_utilizador = ?\n" +
                    "                     and token_validation in (\n" +
                    "                         select token_validation from Tokens where id_utilizador = ?\n" +
                    "                             order by last_used_at desc offset ?\n" +
                    "                   )"
                )
                println("HELLO")
                pStatement.setInt(1, token.userId)
                pStatement.setInt(2, token.userId)
                pStatement.setInt(3, maxTokens-1)
                pStatement.executeUpdate()
                println("HELLO")
                val pStatement2 = it.prepareStatement(
                    "insert into Tokens(id_utilizador, token_validation, created_at, last_used_at)\n" +
                    "                     values (?, ?, ?, ?)"
                )
                pStatement2.setInt(1, token.userId)
                pStatement2.setString(2, token.tokenValidationInfo.validationInfo)
                pStatement2.setLong(3, token.createdAt.epochSeconds)
                pStatement2.setLong(4, token.lastUsedAt.epochSeconds)
                pStatement2.executeUpdate()
            } catch (e: SQLException) {
                it.rollback()
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
