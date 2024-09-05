package isel.pt.ps.projeto.casbin

import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import jakarta.annotation.PostConstruct
import org.casbin.adapter.JDBCAdapter
import org.casbin.jcasbin.main.Enforcer
import org.casbin.jcasbin.main.SyncedEnforcer
import org.casbin.jcasbin.model.Model
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class AuthorizationService(
    private val usersRepository: UsersRepository,
    private val constructionsRepository: ConstructionsRepository,
    private val config: DataSourceProperties
) {

    @PostConstruct
    fun initialize() {
        // Delete casbin data to ensure data consistency after initialization
        val list = emptyList<MutableList<String>>().toMutableList()
        enforcer.policy.forEach {  list.add(it) }
        enforcer.removePolicies(list)

        val subList = emptyList<String>().toMutableList()
        enforcer.getUsersForRole("user").forEach { subList.add(it) }

        subList.forEach { enforcer.deleteUser(it) }

        savePolicy("user", "/obras", "GET")
        savePolicy("user", "/obras", "POST")
        savePolicy("user", "/obras/ongoing", "GET")
        savePolicy("user", "/obras/isAdmin", "GET")
        savePolicy("user", "/registos", "GET")
        savePolicy("user", "/registos", "POST")
        savePolicy("user", "/registos", "PUT")
        savePolicy("user", "/registos/nfc", "POST")
        savePolicy("user", "/registos/pendente", "GET")
        savePolicy("user", "/users/me", "GET")
        savePolicy("user", "/users/me", "PUT")
        savePolicy("user", "/convites", "GET")
        savePolicy("user", "/convites", "PUT")
        savePolicy("user", "/profile", "GET")
        savePolicy("user", "/users/me/changepassword", "PUT")
        savePolicy("user", "/users/me/imagem", "GET")
        savePolicy("user", "/registos/incompletos", "GET")
        savePolicy("user", "/registos/incompletos", "PUT")
        usersRepository.getUsers().map { user ->
            saveUserRole(user.email)
            enforcer.addRoleForUser(user.email, "user")
            constructionsRepository.getConstructionsOfUser(user.id, null, null).map { construction ->
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}", "POST")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}", "PUT")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/nfc", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/nfc", "PUT")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos", "PUT")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos/*", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/register", "POST")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos/me", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/convite", "POST")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/users", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/user/*", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/user/*", "DELETE")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos/pendente", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos/**", "DELETE")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos/incompletos", "GET")
                savePolicy("construction_${construction.oid}_funcionario", "/obras/${construction.oid}/registos/incompletos", "GET")
                savePolicy("construction_${construction.oid}_funcionario", "/obras/${construction.oid}/registos/**", "DELETE")
                savePolicy("construction_${construction.oid}_funcionario", "/obras/${construction.oid}", "GET")
                savePolicy("construction_${construction.oid}_funcionario", "/obras/${construction.oid}/registos/me", "GET")
                savePolicy("construction_${construction.oid}_funcionario", "/obras/${construction.oid}/register", "POST")
                saveConstructionUserRole(user.email, construction.oid, constructionsRepository.getUserRoleFromConstruction(user.id, construction.oid)?.role ?: "funcionario")
            }
        }

        (enforcer.adapter as JDBCAdapter).close()

    }

    fun savePolicy(subject: String, obj: String, act: String) {
        enforcer.addPolicy(subject, obj, act)
    }

    fun saveConstructionUserRole(email: String, oid: Int, role: String) {
        enforcer.addRoleForUser(email, "construction_${oid}_$role")
    }

    fun createConstructionPolicies(oid: Int) {
        savePolicy("construction_${oid}_admin", "/obras/${oid}", "GET")
        savePolicy("construction_${oid}_admin", "/obras/${oid}", "POST")
        savePolicy("construction_${oid}_admin", "/obras/${oid}", "PUT")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/nfc", "GET")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/nfc", "PUT")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/registos", "GET")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/registos", "PUT")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/registos/**", "GET")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/register", "POST")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/registos/me", "GET")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/convite", "POST")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/users", "GET")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/user/**", "GET")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/user/**", "DELETE")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/registos/pendente", "GET")
        savePolicy("construction_${oid}_admin", "/obras/${oid}/registos/**", "DELETE")
        savePolicy("construction_${oid}_funcionario", "/obras/${oid}/registos/**", "DELETE")
        savePolicy("construction_${oid}_funcionario", "/obras/${oid}", "GET")
        savePolicy("construction_${oid}_funcionario", "/obras/${oid}/registos/me", "GET")
        savePolicy("construction_${oid}_funcionario", "/obras/${oid}/register", "POST")
    }

    fun saveUserRole(email: String) {
        enforcer.addRoleForUser(email, "user")
    }
    @Bean
    final fun enforcer(): Enforcer {
        val modelText = this::class.java.classLoader.getResourceAsStream("model.conf")!!.bufferedReader().use { it.readText() }

        // Create a Casbin model from the model string
        val model = Model()
        model.loadModelFromText(modelText)

        val dataSource = PGSimpleDataSource().apply {
            setURL(config.url)
            user = config.username
            password = config.password
        }

        val jdbcAdapter = JDBCAdapter(dataSource)

        // Initialize the Enforcer with the model and policy files
        val enforcer = SyncedEnforcer(model, jdbcAdapter)
        enforcer.enableAutoSave(true)
        enforcer.enableLog(false)
        return enforcer
    }

    private val enforcer = enforcer()

}
