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

        val emailAlreadyExists = Problem("Email already exists")

        val insecurePassword = Problem("Insecure Password")

        val userOrPasswordAreInvalid = Problem("User or password invalid")

        val invalidRequestContent = Problem("Invalid request content")

        val unauthorizedUser = Problem("Unauthorized user")

        val invalidToken = Problem("Invalid Token")

        val constructionNotFound = Problem("Construction not found")

        val noConstructions = Problem("No constructions")

        val constructionAlreadyExists = Problem("Construction with that name already exists")

        val invalidConstruction = Problem("Invalid construction")

        val emptyEmployees = Problem("No employees in construction")
    }
}
