package pt.isel.projetoeseminario.services

import android.util.Log
import pt.isel.projetoeseminario.http.RetrofitClient
import pt.isel.projetoeseminario.model.RegistoInputModel
import pt.isel.projetoeseminario.model.RegistoOutputModel
import pt.isel.projetoeseminario.model.RegistoPostOutputModel
import pt.isel.projetoeseminario.model.UserRegisterOutputModel
import pt.isel.projetoeseminario.repository.users.RegistoRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class RegistoService {
    private val instance = RetrofitClient.instance.create(RegistoRepository::class.java)

    fun getUserRegisters(token: String, onResult: (UserRegisterOutputModel?) -> Unit) {
        val call = instance.getUserRegisters(token)
        call.enqueue(object : Callback<UserRegisterOutputModel> {
            override fun onResponse(
                call: Call<UserRegisterOutputModel>,
                response: Response<UserRegisterOutputModel>
            ) {
                Log.d("REGISTER", response.code().toString())
                Log.d("REGISTER", "${response.body().toString()} at least was successful")
                onResult(response.body())
            }

            override fun onFailure(call: Call<UserRegisterOutputModel>, t: Throwable) {
                Log.d("REGISTER", "${t.message} at least was successful")
                onResult(null)
            }
        })
    }

    fun addUserRegisterEntrada(token: String, time: LocalDateTime, obraId: Int, onResult: (RegistoPostOutputModel?) -> Unit) {
        val call = instance.addUserRegisterEntrada(token, RegistoInputModel(time, obraId))

        call.enqueue(object : Callback<RegistoPostOutputModel> {
            override fun onResponse(
                call: Call<RegistoPostOutputModel>,
                response: Response<RegistoPostOutputModel>
            ) {
                Log.d("REGISTER", response.code().toString())
                Log.d("REGISTER", "${response.body().toString()} at least was successful")
                onResult(response.body())
            }

            override fun onFailure(call: Call<RegistoPostOutputModel>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun addUserRegisterSaida(token: String, time: LocalDateTime, obraId: Int, onResult: (RegistoPostOutputModel?) -> Unit) {
        val call = instance.addUserRegisterSaida(token, RegistoInputModel(time, obraId))

        call.enqueue(object : Callback<RegistoPostOutputModel> {
            override fun onResponse(
                call: Call<RegistoPostOutputModel>,
                response: Response<RegistoPostOutputModel>
            ) {
                Log.d("REGISTER", response.code().toString())
                Log.d("REGISTER", "${response.body().toString()} at least was successful")
                onResult(response.body())
            }

            override fun onFailure(call: Call<RegistoPostOutputModel>, t: Throwable) {
                onResult(null)
            }
        })
    }
}