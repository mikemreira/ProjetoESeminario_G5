package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import jakarta.annotation.PostConstruct
import org.casbin.jcasbin.main.Enforcer
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    private val usersRepository: UsersRepository,
    private val constructionsRepository: ConstructionsRepository
) {

    @PostConstruct
    fun initialize() {
        println("RAN")
        savePolicy("user", "/obras", "GET")
        savePolicy("user", "/obras", "POST")
        savePolicy("user", "/obras/ongoing", "GET")
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
        usersRepository.getUsers().map { user ->
            saveUserRole(user.email)
            constructionsRepository.getConstructionsOfUser(user.id).map { construction ->
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}", "POST")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}", "PUT")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/nfc", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/nfc", "PUT")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos", "PUT")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos/${user.id}", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/register", "POST")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos/me", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/convite", "POST")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/users", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/user/${user.id}", "GET")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/user/${user.id}", "DELETE")
                savePolicy("construction_${construction.oid}_admin", "/obras/${construction.oid}/registos/pendente", "GET")
                savePolicy("construction_${construction.oid}_funcionario", "/obras/${construction.oid}", "GET")
                savePolicy("construction_${construction.oid}_funcionario", "/obras/${construction.oid}/registos/me", "GET")
                savePolicy("construction_${construction.oid}_funcionario", "/obras/${construction.oid}/register", "POST")
                saveConstructionUserRole(user.email, construction.oid, constructionsRepository.getUserRoleFromConstruction(user.id, construction.oid)?.role ?: "user")
            }
        }
    }

    fun savePolicy(subject: String, obj: String, act: String) {
        enforcer.addPolicy(subject, obj, act)
        enforcer.savePolicy()
    }

    fun saveConstructionUserRole(email: String, oid: Int, role: String) {
        enforcer.addRoleForUser(email, "construction_${oid}_$role")
        enforcer.savePolicy()
    }

    fun saveUserRole(email: String) {
        enforcer.addRoleForUser(email, "user")
    }

    @Bean
    final fun enforcer(): Enforcer = Enforcer("src/main/resources/model.conf", "src/main/resources/policy.csv")

    private val enforcer = enforcer()

}
