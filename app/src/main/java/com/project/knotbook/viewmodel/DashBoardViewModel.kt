package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.model.DashboardResponse
import com.project.knotbook.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val data: DashboardResponse) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}


@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val repository: DashboardRepository
): ViewModel() {

    private val _uiState =
        MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

     fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            try{
                val response = repository.getDashboard()
                _uiState.value = DashboardUiState.Success(response)
            }catch (e: Exception){
                _uiState.value = DashboardUiState.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
    }


}