package isel.pt.ps.projeto.repository.jdbc

import org.springframework.stereotype.Component
import java.util.*

@Component
class UtlisRepository {

    fun base64ToByteArray(base64String: String): ByteArray {
        var base64Image = base64String
        if (base64String.startsWith("data:image/jpeg;base64,")) {
            base64Image = base64String.substringAfter("data:image/jpeg;base64,")
        }
        return Base64.getDecoder().decode(base64Image)
    }

}