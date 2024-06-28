package isel.pt.ps.projeto.casbin

import org.casbin.jcasbin.main.Enforcer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@EnableWebSecurity
@Configuration
class SecurityConfiguration {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity, casbinFilter: CasbinFilter): SecurityFilterChain {
        http {
            csrf { disable() }
            cors {  }
            authorizeHttpRequests {
                authorize("/users/*", permitAll)
                //authorize("/users/signup", permitAll)
                authorize("/registos", permitAll)
                authorize("/obras", permitAll)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(casbinFilter)
        }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:5173") // Adjust as needed
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type")
            allowCredentials = true
        }
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun enforcer(): Enforcer {
        return Enforcer("src/main/resources/model.conf", "src/main/resources/policy.csv")
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(): InMemoryUserDetailsManager {
        val user: UserDetails = User.withUsername("admin")
            .password(encoder().encode("admin"))
            .roles("admin")
            .build()
        return InMemoryUserDetailsManager(user)
    }
}