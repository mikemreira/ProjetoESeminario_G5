package pt.isel.projetoeseminario.services

import pt.isel.projetoeseminario.http.RetrofitClient
import pt.isel.projetoeseminario.repository.users.ObraRepository

class ObraService {
    private val instance = RetrofitClient.instance.create(ObraRepository::class.java)

    fun getUserObra(token: String) {
        val call = instance.getUserObras(token)

    }
}