package isel.pt.ps.projeto.controllers

import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class UtilsController {
    fun byteArrayToBase64(byteArray: ByteArray?): String {
        val base64String = Base64.getEncoder().encodeToString(byteArray)
        return "data:image/jpeg;base64,$base64String"
    }

    //val path = "https://spring-api-ps-isel.test.azuremicroservices.io/spring-app-api/default"
    //val path = "https://spring-api-ps-isel-spring-app-api.azuremicroservices.io"
    val path = "/api"
}