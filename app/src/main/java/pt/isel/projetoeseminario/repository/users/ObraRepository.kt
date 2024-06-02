package pt.isel.projetoeseminario.repository.users

import pt.isel.projetoeseminario.model.ObrasOutputModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ObraRepository {
    @GET("/obras")
    fun getUserObras(@Header("Accept-Language") token: String): Call<ObrasOutputModel>
}