package pt.isel.projetoeseminario.services

import android.util.Log
import pt.isel.projetoeseminario.http.RetrofitClient
import pt.isel.projetoeseminario.model.ObrasOutputModel
import pt.isel.projetoeseminario.model.UserAuthInputModel
import pt.isel.projetoeseminario.model.UserLoginInputModel
import pt.isel.projetoeseminario.model.UserLoginOutputModel
import pt.isel.projetoeseminario.model.UserOutputModel
import pt.isel.projetoeseminario.model.UserSignupInputModel
import pt.isel.projetoeseminario.model.UserSignupOutputModel
import pt.isel.projetoeseminario.repository.users.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserService {
    private val instance = RetrofitClient.instance.create(UserRepository::class.java)

    fun login(email: String, password: String, onResult: (UserLoginOutputModel?) -> Unit) {
        val call = instance.login(UserLoginInputModel(email, password))
        call.enqueue(object : Callback<UserLoginOutputModel> {
            override fun onResponse(
                call: Call<UserLoginOutputModel>,
                response: Response<UserLoginOutputModel>
            ) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<UserLoginOutputModel>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun signup(username: String, email: String, password: String, onResult: (UserSignupOutputModel?) -> Unit) {
        val call = instance.signup(UserSignupInputModel(username, email, password))
        call.enqueue(object : Callback<UserSignupOutputModel> {
            override fun onResponse(
                call: Call<UserSignupOutputModel>,
                response: Response<UserSignupOutputModel>
            ) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<UserSignupOutputModel>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun getUserDetails(token: String, onResult: (UserOutputModel?) -> Unit) {
        val call = instance.getUserDetails("Bearer $token")
        call.enqueue(object : Callback<UserOutputModel> {
            override fun onResponse(
                call: Call<UserOutputModel>,
                response: Response<UserOutputModel>
            ) {
                Log.d("BODY", response.code().toString())
                Log.d("BODY", "${response.body().toString()} at least was successful")
                onResult(response.body())
            }

            override fun onFailure(call: Call<UserOutputModel>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun getUserConstructions(token: String, onResult: (ObrasOutputModel?) -> Unit) {
        val call = instance.getUserConstructions("Bearer $token")
        call.enqueue(object : Callback<ObrasOutputModel> {
            override fun onResponse(
                call: Call<ObrasOutputModel>,
                response: Response<ObrasOutputModel>
            ) {
                Log.d("BODY", response.code().toString())
                Log.d("BODY", "${response.body().toString()} at least was successful")
                onResult(response.body())
            }

            override fun onFailure(call: Call<ObrasOutputModel>, t: Throwable) {
                onResult(null)
            }
        })
    }
}