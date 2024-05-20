package isel.pt.ps.projeto.models

import org.springframework.http.ResponseEntity

class Problem(
    typeString: String,
) {
    val error = typeString

    companion object {
        const val MEDIA_TYPE = "application/problem+json"

        fun response(
            status: Int,
            problem: Problem,
        ) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val userAlreadyExists = Problem("Username already exists")

        val insecurePassword = Problem("Insecure Password")

        val userOrPasswordAreInvalid = Problem("User or password invalid")

        val invalidRequestContent = Problem("Invalid request content")

        val unauthorizedUser = Problem("Unauthorized user")

        val alreadyInQueue = Problem("Already in queue")

        val invalidSettings = Problem("Invalid settings")

        val alreadyInGame = Problem("Already in game")

        val notYourTurn = Problem("Not your turn")

        val gameNotFound = Problem("Game not found")

        val invalidPlay = Problem("Invalid play")

        val playerNotInGame = Problem("Player is not in game")

        val playerNotFound = Problem("Player not found")

        val gameFinished = Problem("Game is already over")
    }
}
