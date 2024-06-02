package pt.isel.projetoeseminario.model

import android.graphics.Bitmap

data class User(
    val id: Int
)

data class UserLoginInputModel(
    val email: String,
    val password: String
)

data class UserLoginOutputModel(
    val token: String
)

data class UserSignupInputModel(
    val name: String,
    val email: String,
    val password: String
)

data class UserSignupOutputModel(
    val message: String
)

data class UserOutputModel(
    val nome: String,
    val email: String,
    val foto: Bitmap
)

data class UserAuthInputModel(
    val token: String
)
