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

        val emailDoesntExists = Problem("Email doesn't exists")

        val insecurePassword = Problem("Insecure Password")

        val userOrPasswordAreInvalid = Problem("User or password invalid")

        val invalidRequestContent = Problem("Invalid request content")

        val unauthorizedUser = Problem("Unauthorized user")

        val invalidToken = Problem("Invalid Token")

        val constructionNotFound = Problem("Construction not found")

        val noConstructions = Problem("No constructions")

        val noAccessToConstruction = Problem("You dont have access to that construction")

        val constructionAlreadyExists = Problem("Construction with that name already exists")

        val invalidConstruction = Problem("Invalid construction")

        val emptyEmployees = Problem("No employees in construction")

        val noRegisters = Problem("No registers")

        val invalidRegister = Problem("Invalid register")

        val userNotFound = Problem("User not found")

        val alreadyExists = Problem("Already Exists")

        val invalidConstructionInput = Problem("Invalid construction input")

        val constructionSuspended = Problem("Construction is suspended")

        val userNotAdmin = Problem("User does not have permission")

        val invalidQuery = Problem("Invalid Query")

        val invalidParams = Problem("Invalid Params")

    }
}
