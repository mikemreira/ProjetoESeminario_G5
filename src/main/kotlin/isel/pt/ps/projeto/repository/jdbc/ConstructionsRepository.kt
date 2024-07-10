package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.services.UtilsServices
import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.constructions.ConstructionEditInputModel
import isel.pt.ps.projeto.models.constructions.ConstructionImage
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterQuery
import isel.pt.ps.projeto.models.role.Role
import isel.pt.ps.projeto.models.users.SimpleUser
import isel.pt.ps.projeto.models.users.SimpleUserAndFunc
import isel.pt.ps.projeto.repository.ConstructionRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDate
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException
import java.sql.Timestamp


@Component
class ConstructionsRepository(
    private val utils: UtilsServices,
) : ConstructionRepository {
    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseUrl)
        return dataSource.connection
    }

    override fun getConstruction(oid: Int): Construction? {
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
                if (!result.next())
                    null
                else {
                    val dateFim = result.getDate("data_fim")
                    Construction(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("localização"),
                        result.getString("descrição"),
                        result.getDate("data_inicio").toString().toLocalDate(),
                        if (dateFim == null) dateFim else dateFim.toString().toLocalDate(),
                        result.getString("status"),
                        result.getBytes("foto")
                    )
                }
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getConstructionsUsers(oid: Int): List<SimpleUserAndFunc> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement =
                    it.prepareStatement(
                        "select ut.id, ut.nome, ut.email, ut.morada, ut.foto, pa.funcao from utilizador ut\n" +
                            "inner join papel pa on pa.id_utilizador = ut.id\n" +
                            "inner join obra o on o.id = pa.id_obra\n" +
                            "where o.id = ?",
                    )
                pStatement.setInt(1, oid)
                val result = pStatement.executeQuery()
                val list = mutableListOf<SimpleUserAndFunc>()
                while (result.next()) {
                    val uid = result.getInt("id")
                    list.add(SimpleUserAndFunc(
                        uid,
                        result.getString("nome"),
                        result.getString("email"),
                        result.getString("morada"),
                        result.getString("funcao"),
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

    override fun getImage(oid: Int, typeOfImage: String): String? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement(
                    "Select * \n" +
                        "From ObraImagem\n" +
                        "Where id_obra = ? "
                )

                pStatement.setInt(1, oid)
                val result = pStatement.executeQuery()
                result.next()
                result.getString(typeOfImage)
            } catch (e: SQLException) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getConstructionUser(oid: Int, uid: Int): SimpleUserAndFunc? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement =
                    it.prepareStatement(
                        "select ut.id, ut.nome, ut.email, ut.morada, pa.funcao from utilizador ut\n" +
                            "inner join papel pa on pa.id_utilizador = ut.id\n" +
                            "inner join obra o on o.id = pa.id_obra\n" +
                            "where o.id = ? and ut.id = ?",
                    )
                pStatement.setInt(1, oid)
                pStatement.setInt(2, uid)
                val result = pStatement.executeQuery()
                if (!result.next())
                    null
                else
                    SimpleUserAndFunc(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("email"),
                        result.getString("morada"),
                        result.getString("funcao"),
                    )
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }


    override fun getConstructionsOfUser(id: Int, status: String?): List<Construction> {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement =
                    it.prepareStatement(
                        "select o.id, o.nome, o.localização, o.descrição, o.data_inicio, o.data_fim, o.status, o.foto from utilizador u\n" +
                            "inner join papel p on p.id_utilizador = u.id\n" +
                            "inner join obra o on o.id = p.id_obra\n" +
                            "where 1=1 \n" +
                            "   AND (o.status = COALESCE(?, o.status))\n" +
                            "   AND (u.id = COALESCE(?, u.id))",
                    )
                pStatement.setString(1, status)
                pStatement.setInt(2, id)
                val result = pStatement.executeQuery()
                val list = mutableListOf<Construction>()
                while (result.next()) {
                    val dataF = result.getDate("data_fim")
                    list.add(
                        Construction(
                            result.getInt("id"),
                            result.getString("nome"),
                            result.getString("localização"),
                            result.getString("descrição"),
                            result.getDate("data_inicio").toString().toLocalDate(),
                            if (dataF != null) dataF.toString().toLocalDate() else null,
                            result.getString("status"),
                            result.getBytes("foto")
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

    override fun createConstruction(
        userId: Int,
        name: String,
        location: String,
        description: String,
        startDate: LocalDate,
        endDate: LocalDate?,
        foto: ConstructionImage?,
        status: String?,
        function: String
    ): Int {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                println("foto: $foto")
                val generatedColumns = arrayOf("id")
                val insertStatement = it.prepareStatement(
                    "INSERT INTO Obra (nome, localização, descrição, data_inicio, data_fim)\n" +
                    "VALUES (?,?,?,?,?) ", generatedColumns
                )
                insertStatement.setString(1,name)
                insertStatement.setString(2,location)
                insertStatement.setString(3,description)
                insertStatement.setDate(4,Date.valueOf(startDate.toString()))
                insertStatement.setDate(5, if (endDate !=null) Date.valueOf(endDate.toString()) else null)
                insertStatement.executeUpdate()
                insertStatement.generatedKeys.use { generatedKeys ->
                    if (generatedKeys.next()) {
                        val oid = generatedKeys.getInt(1)
                        val insertStatementRole = it.prepareStatement(
                            "INSERT INTO Papel (id_utilizador, id_obra, papel, funcao)\n" +
                            "VALUES\n" +
                            " (?, ?, 'admin',?)")
                        insertStatementRole.setInt(1, userId)
                        insertStatementRole.setInt(2, oid)
                        insertStatementRole.setString(3, function)
                        insertStatementRole.executeUpdate()
                        if (foto != null) {
                            val imagemStatement = it.prepareStatement(
                                "INSERT INTO ObraImagem values (?,?)\n" +
                                    "Values (?,?)"
                            )
                            imagemStatement.setBytes(1, foto.thumbnail)
                            imagemStatement.setBytes(2, foto.infoList)
                            imagemStatement.executeUpdate()
                        }
                        oid
                    } else {
                        throw SQLException("Creating Obra failed, no ID obtained.")
                    }
                }

            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getUserRoleFromConstruction(id: Int, oid: Int): Role? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val generatedColumns = arrayOf("id")
                val pStatement = it.prepareStatement(
                    "Select * from Papel\n" +
                        "where id_utilizador = ? and id_obra = ?"
                )
                pStatement.setInt(1, id)
                pStatement.setInt(2, oid)
                val result = pStatement.executeQuery()
                if (!result.next())
                    null
                else {
                    Role (
                        result.getString("papel"),
                        result.getString("funcao")
                    )
                }
            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getUserByEmailFromConstructions(oid: Int, email: String): SimpleUser? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement("select u.id, u.nome, u.email, u.morada from utilizador u\n" +
                    "inner join papel p on p.id_utilizador = u.id\n" +
                    "inner join obra o on o.id = p.id_obra\n" +
                    "where u.email = ?  and o.id = ?")
                pStatement.setString(1, email)
                pStatement.setInt(2, oid)
                val result = pStatement.executeQuery()
                if (!result.next())
                    null
                else
                    SimpleUser(
                        result.getInt("id"),
                        result.getString("nome"),
                        result.getString("email"),
                        result.getString("morada"),
                    )
            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun isUserAssociatedWithConstructionByEmail(oid: Int, email: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val query = """
                    SELECT EXISTS (
                        SELECT *
                        FROM utilizador u
                        INNER JOIN papel p ON p.id_utilizador = u.id
                        INNER JOIN obra o ON o.id = p.id_obra
                        WHERE u.email = ? AND o.id = ?
                    ) as resp
                """
                val pstmt = it.prepareStatement(query)
                pstmt.setString(1, email)
                pstmt.setInt(2, oid)
                val result = pstmt.executeQuery()
                result.next()
                result.getBoolean("resp")
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun editConstruction(oid: Int, inputModel: ConstructionEditInputModel): Construction? {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val fotoBytes = if (inputModel.foto != null) utils.base64ToByteArray(inputModel.foto) else null
                val pStatement = it.prepareStatement(
                    "UPDATE Obra\n" +
                        "SET nome = ?, localização = ?, descrição = ?, data_inicio = ?, data_fim = ?, foto = ?, status = ? \n" +
                        "WHERE id = ?"
                )
                pStatement.setString(1, inputModel.name)
                pStatement.setString(2, inputModel.location)
                pStatement.setString(3, inputModel.description)
                pStatement.setDate(4, Date.valueOf(inputModel.startDate))
                pStatement.setDate(5, if (inputModel.endDate != null) Date.valueOf(inputModel.endDate) else null)
                pStatement.setBytes(6, fotoBytes)
                pStatement.setString(7, inputModel.status)
                pStatement.setInt(8, oid)
                pStatement.executeUpdate()
                if (inputModel.status == "deleted")
                    return null
                val selectConst = it.prepareStatement("SELECT * FROM Obra WHERE id = ?")
                selectConst.setInt(1, oid)
                val result = selectConst.executeQuery()
                result.next()
                val dataF = result.getDate("data_fim")
                Construction(
                    result.getInt("id"),
                    result.getString("nome"),
                    result.getString("localização"),
                    result.getString("descrição"),
                    result.getDate("data_inicio").toString().toLocalDate(),
                    if (dataF != null) dataF.toString().toLocalDate() else null,
                    result.getString("status"),
                    result.getBytes("foto")
                )
            } catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()

            }
        }
    }

    override fun removeConstructionUser(oid: Int, uid: Int): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val papelStatement = it.prepareStatement(
                    "delete from papel\n" +
                    "where id_obra = ? and id_utilizador = ?\n"
                )
                papelStatement.setInt(1, oid)
                papelStatement.setInt(2, uid)
                papelStatement.executeUpdate()
                val registosStatement = it.prepareStatement(
                    "delete from registo\n" +
                        "where id_obra = ? and id_utilizador = ?\n"
                )
                registosStatement.setInt(1, oid)
                registosStatement.setInt(2, uid)
                registosStatement.executeUpdate()
                true
            }  catch (e: SQLException) {
                throw e
            } finally {
                it.commit()
            }
        }    }

    override fun inviteToConstruction(oid: Int, email: String): Boolean {
        initializeConnection().use {
            it.autoCommit = false
            return try {
                val pStatement = it.prepareStatement("insert into Convite (email, obra) values (?,?)")
                pStatement.setString(1, email)
                pStatement.setInt(2, oid)
                pStatement.executeUpdate()
                true
            }  catch (e: SQLException) {
                if (e.sqlState == "23505") { // SQL state for unique violation
                    println("Duplicate entry: $email, $oid")
                    false
                } else {
                    throw e
                }
            } finally {
                it.commit()
            }
        }
    }

    override fun registerIntoConstruction(userId: Int, oid: Int, startTime: LocalDateTime, endTime: LocalDateTime, role: String) {
        initializeConnection().use {
            it.autoCommit = false
            try {
                val pStatement = it.prepareStatement("Insert into Registo (id_utilizador, id_obra, entrada, saida, status) values (?,?,?,?,?)")
                pStatement.setInt(1, userId)
                pStatement.setInt(2, oid)
                pStatement.setTimestamp(3, Timestamp.valueOf(startTime.toJavaLocalDateTime()))
                pStatement.setTimestamp(4, Timestamp.valueOf(endTime.toJavaLocalDateTime()))
                if (role == "admin")
                    pStatement.setString(5, "completed")
                else
                    pStatement.setString(5, "pending")
                pStatement.executeUpdate()
            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun getRegisters(userId: Int, oid: Int, role: String, filters: RegisterQuery): List<RegisterAndUser> {
        initializeConnection().use {
            it.autoCommit = false
            return try {

                val pStatement = it.prepareStatement(
                    "SELECT r.id as rid, u.nome as nome, u.id as uid, r.entrada as entrada, r.saida as saida, r.status as status\n" +
                        "FROM Utilizador u \n" +
                        "INNER JOIN Registo r ON r.id_utilizador = u.id\n" +
                        "WHERE 1=1\n" +
                        "\tAND (r.id_obra = COALESCE(?, r.id_obra) OR r.id_obra IS NULL)\n" +
                        "\tAND (u.id = COALESCE(?, u.id) OR u.id IS NULL)\n" +
                        "LIMIT 10 offset ?"
                )
                pStatement.setInt(1, oid)
                if (role == "admin") {
                    if (filters.me)
                        pStatement.setInt(2, userId)
                    else if (filters.userId == null)
                        pStatement.setNull(2, java.sql.Types.INTEGER)
                    else
                        pStatement.setInt(2, filters.userId)
                } else
                    pStatement.setInt(2, userId)

                pStatement.setInt(3, filters.page*10)

                val result = pStatement.executeQuery()
                val registers = mutableListOf<RegisterAndUser>()
                while (result.next()){
                    val saida = result.getTimestamp("saida")
                    registers.add(
                        RegisterAndUser(
                            result.getString("nome"),
                            result.getInt("rid"),
                            oid,
                            result.getInt("uid"),
                            result.getTimestamp("entrada").toLocalDateTime(),
                            if (saida == null) null else saida.toLocalDateTime(),
                            result.getString("status"),
                        )
                    )
                }
                registers
            }  catch (e: Exception) {
                it.rollback()
                throw e
            } finally {
                it.commit()
            }
        }
    }

    override fun checkConstructionByName(name: String): Boolean {
        TODO("Not yet implemented")
    }
}
