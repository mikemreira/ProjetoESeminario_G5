package isel.pt.ps.projeto

import isel.pt.ps.projeto.domain.users.Sha256TokenEncoder
import isel.pt.ps.projeto.domain.users.UsersDomainConfig
import kotlinx.datetime.Clock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.time.Duration.Companion.hours

@SpringBootApplication
class ProjetoApplication {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun clock() = Clock.System

    @Bean
    fun usersDomainConfig() =
        UsersDomainConfig(
            tokenSizeInBytes = 256 / 8,
            tokenTtl = 24.hours,
            tokenRollingTtl = 1.hours,
            maxTokensPerUser = 3
        )
}

fun main(args: Array<String>) {
    runApplication<ProjetoApplication>(*args)
}
