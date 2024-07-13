package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.services.UtilsServices
import isel.pt.ps.projeto.domain.users.PasswordValidationInfo
import isel.pt.ps.projeto.domain.users.Token
import isel.pt.ps.projeto.domain.users.TokenValidationInfo
import isel.pt.ps.projeto.models.users.SimpleUser
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.models.users.UserAndToken
import isel.pt.ps.projeto.repository.UserRepository
import kotlinx.datetime.Instant
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

const val JDBC_URL = "jdbc:postgresql://localhost:5432/postgres"
const val jdbcDatabaseUrl = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"

@Component
class UsersRepository(
    private val utils: UtilsServices
) : UserRepository {
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
                    result.getBytes("foto")
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
                            "select u.id, u.nome, u.password, u.email, u.morada from token t\n" +
                            "inner join utilizador u on u.id = t.id_utilizador\n" +
                            "where t.token_validation = ?",
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
                        "",
                        result.getBytes("foto")
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

    override fun getTokenByTokenValidationInfo(token: TokenValidationInfo): Pair<User, Token>? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select id, nome, email, password, morada, foto, token_validation \n" +
                        "    from utilizador u \n" +
                        "    inner join Token t\n" +
                        "    on u.id = t.id_utilizador\n" +
                        "    where token_validation = ?"
                )
                pStatement.setString(1, token.validationInfo)
                val result = pStatement.executeQuery()
                result.next()
                UserAndTokenModel(
                    result.getInt("id"),
                    result.getString("nome"),
                    result.getString("email"),
                    PasswordValidationInfo(result.getString("password")),
                    result.getString("morada"),
                    result.getBytes("foto"),
                    TokenValidationInfo(result.getString("token_validation"))
                ).userAndToken
            } catch(e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }

        }
    }


    override fun updateTokenLastUsed(token: Token, now: Instant) {
        initializeConnection().use {
            it.autoCommit = false
            val pStatement = it.prepareStatement(
                "update Token\n" +
                "set last_used_at = ?\n" +
                "where token_validation = ?"
            )
            pStatement.setLong(1, now.epochSeconds)
            pStatement.setString(2, token.tokenValidationInfo.validationInfo)
            pStatement.executeUpdate()
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
                        result.getBytes("foto")
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
                    "",
                    result.getBytes("foto")
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
                    result.getBytes("foto")
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

    override fun signOut(tokenValidationInfo: TokenValidationInfo) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                val pStatement = it.prepareStatement("delete from token where token_validation = ?")
                pStatement.setString(1, tokenValidationInfo.validationInfo)
                pStatement.executeUpdate()
            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun setForgetPassword(email: String, token: String) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                val pStatement = it.prepareStatement(
                    "Insert into PasswordEsquecida values (?,?)"
                )
                pStatement.setString(1, email)
                pStatement.setString(2, token)
                pStatement.executeUpdate()
            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun validateEmailAndTokenForForgottenPassword(email: String, token: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "select exists (" +
                        "select * " +
                        "from PasswordEsquecida " +
                        "where email = ? and token_check = ? " +
                        ")"
                )
                pStatement.setString(1, email)
                pStatement.setString(2, token)
                val result = pStatement.executeQuery()
                result.next()
                result.getBoolean("exists")
            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun editPasswordIfForgotten(userId: Int, email: String, pass: PasswordValidationInfo) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                val pStatement = it.prepareStatement(
                    "UPDATE Utilizador\n" +
                        "SET password = ?\n" +
                        "WHERE id = ? \n"
                )
                pStatement.setString(1, pass.validationInfo)
                pStatement.setInt(2, userId)
                pStatement.executeUpdate()
                val tokenStatement = it.prepareStatement(
                    "delete from token where id_utilizador = ?"
                )
                tokenStatement.setInt(1, userId)
                tokenStatement.executeUpdate()
                val forgetPasswordStatement = it.prepareStatement(
                    "delete from PasswordEsquecida where email = ?"
                )
                forgetPasswordStatement.setString(1, email)
                forgetPasswordStatement.executeUpdate()
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
                val pStatement2 = it.prepareStatement(
                    "insert into Token(id_utilizador, token_validation)\n" +
                    "                     values (?, ?)"
                )
                pStatement2.setInt(1, token.userId)
                pStatement2.setString(2, token.tokenValidationInfo.validationInfo)
                pStatement2.executeUpdate()
            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun editUser(id: Int, nome: String, morada: String?, foto: ByteArray?): SimpleUser {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "UPDATE Utilizador\n" +
                        "SET nome = ?,\n" +
                        "    morada = ?,\n" +
                        "    foto = ?\n" +
                        "WHERE id = ?;\n"
                )
                pStatement.setString(1, nome)
                pStatement.setString(2, morada)
                pStatement.setBytes(3, foto)
                pStatement.setInt(4, id)
                pStatement.executeUpdate()

                val selectStatement = it.prepareStatement(
                    "SELECT id, nome, email, morada, foto FROM Utilizador WHERE id = ?"
                )
                selectStatement.setInt(1, id)

                val resultSet = selectStatement.executeQuery()
                resultSet.next()
                SimpleUser(
                        id = resultSet.getInt("id"),
                        nome = resultSet.getString("nome"),
                        email = resultSet.getString("email"),
                        morada = resultSet.getString("morada"),
                        foto = resultSet.getString("foto")
                )

            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun editPassword(id: Int, password: PasswordValidationInfo): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "UPDATE Utilizador\n" +
                        "SET password = ?\n" +
                        "WHERE id = ? \n"
                )
                pStatement.setString(1, password.validationInfo)
                pStatement.setInt(2, id)
                pStatement.executeUpdate()
                true
            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun deleteUser(id: Int): User {
        TODO("Not yet implemented")
    }

    private data class UserAndTokenModel(
        val id: Int,
        val nome: String,
        val email: String,
        val passwordValidation: PasswordValidationInfo,
        val morada: String?,
        val foto: ByteArray?,
        val tokenValidation: TokenValidationInfo
    ) {
        val userAndToken: Pair<User, Token>
            get() = Pair(
                User(id, nome, email, passwordValidation, morada, foto),
                Token(
                    tokenValidation,
                    id,
                )
            )
    }
}
