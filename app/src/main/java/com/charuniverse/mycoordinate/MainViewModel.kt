package com.charuniverse.mycoordinate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _mainUiState = MutableLiveData<MainUiState>()
    val mainUiState: LiveData<MainUiState> = _mainUiState

    abstract class MainUiState {
        object Idle : MainUiState()
        object Loading : MainUiState()
        object Success : MainUiState()
        data class Error(val message: String) : MainUiState()
    }
}