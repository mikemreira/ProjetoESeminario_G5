package pt.isel.projetoeseminario.repository.users

import pt.isel.projetoeseminario.model.RegistoInputModel
import pt.isel.projetoeseminario.model.RegistoOutputModel
import pt.isel.projetoeseminario.model.RegistoPostOutputModel
import pt.isel.projetoeseminario.model.UserRegisterOutputModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface RegistoRepository {
    @GET("/registos")
    fun getUserRegisters(@Header("Authorization") token: String): Call<UserRegisterOutputModel>

    @POST("/registos")
    fun addUserRegisterEntrada(@Header("Authorization") token: String, @Body registerBody: RegistoInputModel): Call<RegistoPostOutputModel>

    @PUT("/registos")
    fun addUserRegisterSaida(@Header("Authorization") token: String, @Body registerBody: RegistoInputModel): Call<RegistoPostOutputModel>
}