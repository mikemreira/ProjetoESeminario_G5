package isel.pt.ps.projeto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class ProjetoApplication

fun main(args: Array<String>) {
	runApplication<ProjetoApplication>(*args)
}
