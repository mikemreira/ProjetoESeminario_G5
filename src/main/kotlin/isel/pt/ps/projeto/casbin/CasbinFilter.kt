package isel.pt.ps.projeto.casbin

import isel.pt.ps.projeto.services.UsersService
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.casbin.jcasbin.main.Enforcer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*

@Component
@Profile("!test")
class CasbinFilter(private val enforcer: Enforcer, private val usersService: UsersService) : Filter {

    private val excludedPaths = setOf(
        "/users/signin",
        "/users/signup",
        "/users/signout",
        "/users/forget-password",
        "/users/set-password",
    )

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig?) {
        logger.info("initializing ...")
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpServletRequest = request as HttpServletRequest
        if (excludedPaths.contains(httpServletRequest.requestURI)) {
            chain.doFilter(request, response)
            return
        }

        val token = httpServletRequest.getHeader("Authorization").trim().split(" ")[1]
        val user = usersService.getUserByToken(token)
        if (user != null) {
            val method = httpServletRequest.method
            val path = httpServletRequest.requestURI
            enforcer.loadPolicy()
            if (enforcer.enforce(user.email, path, method)) {
                logger.info("session is authorized: {} {} {}", user.email, method, path)

                val rolesForUser = enforcer.getRolesForUser(user.email)
                val securityContext = SecurityContextHolder.getContext()
                securityContext.authentication = AuthenticationImpl(user.email, rolesForUser)
                val session = httpServletRequest.session
                session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext)

                chain.doFilter(request, response)
            } else {
                logger.error("session is not authorized: {} {} {}", user.email, method, path)
                val httpServletResponse = response as HttpServletResponse
                httpServletResponse.status = HttpStatus.FORBIDDEN.value()
            }
        } else {
            logger.error("session is not authenticated")
            val httpServletResponse = response as HttpServletResponse
            httpServletResponse.status = HttpStatus.FORBIDDEN.value()
        }
    }

    override fun destroy() {
        logger.info("destroy.")
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CasbinFilter::class.java)
    }
}

// jdbc:postgresql://postgredb:5432/postgres?username=postgres&password=postgres