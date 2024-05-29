package isel.pt.ps.projeto.repository.jdbc

import isel.pt.ps.projeto.models.registers.Register
import isel.pt.ps.projeto.repository.RegistersRepository
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import java.sql.Connection


@Component
class RegistersRepository : RegistersRepository {
    private fun initializeConnection(): Connection {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseUrl)
        return dataSource.connection
    }

    override fun getRegistersOfUser(uid: Int): List<Register> {
        TODO("Not yet implemented")
    }
}