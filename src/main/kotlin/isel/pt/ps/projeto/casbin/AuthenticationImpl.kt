
package isel.pt.ps.projeto.casbin
/*
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class AuthenticationImpl(
    private val name: String,
    roles: List<String>
) : Authentication {

    private val grantedAuthorities: Collection<GrantedAuthority>

    private var isAuthenticated: Boolean = false

    init {
        val authorities = roles.map { GrantedAuthorityImpl(it) }
        this.grantedAuthorities = authorities.toList()
        this.isAuthenticated = true
    }

    override fun getAuthorities(): Collection<out GrantedAuthority> {
        return grantedAuthorities
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return name
    }

    override fun isAuthenticated(): Boolean {
        return isAuthenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.isAuthenticated = isAuthenticated
    }

    override fun getName(): String {
        return name
    }
}*/