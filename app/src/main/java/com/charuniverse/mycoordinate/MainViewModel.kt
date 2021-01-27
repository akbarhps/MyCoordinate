package com.charuniverse.mycoordinate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.charuniverse.mycoordinate.utils.Constants

class MainViewModel : ViewModel() {

    private val _mainUiState = MutableLiveData<MainUiState>()
    val mainUiState: LiveData<MainUiState> = _mainUiState

    private fun setUiState(state: String, errorMessage: String = "") {
        _mainUiState.value = when (state) {
            Constants.IDLE_STATE        -> MainUiState.Idle
            Constants.LOADING_STATE     -> MainUiState.Loading
            Constants.SUCCESS_STATE     -> MainUiState.Success
            Constants.ERROR_STATE       -> MainUiState.Error(errorMessage)
            else                        -> MainUiState.Idle
        }
    }

    abstract class MainUiState {
        object Idle                             : MainUiState()
        object Loading                          : MainUiState()
        object Success                          : MainUiState()
        data class Error(val message: String)   : MainUiState()
    }
}