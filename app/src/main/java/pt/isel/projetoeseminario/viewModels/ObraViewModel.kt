package pt.isel.projetoeseminario.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.isel.projetoeseminario.model.ObrasOutputModel
import pt.isel.projetoeseminario.services.ObraService

sealed class FetchObraState {
    data object Idle : FetchObraState()
    data object Loading : FetchObraState()
    data class Success(val obra: List<ObrasOutputModel>? = null) : FetchObraState()
    data class Error(val message: String) : FetchObraState()
}

class ObraViewModel: ViewModel() {
    private val service = ObraService()
    private val _fetchObrasState = MutableStateFlow<FetchObraState>(FetchObraState.Idle)
    val fetchObrasState: StateFlow<FetchObraState> = _fetchObrasState

    fun getUserObra(token: String) {
        viewModelScope.launch {

        }
    }
}