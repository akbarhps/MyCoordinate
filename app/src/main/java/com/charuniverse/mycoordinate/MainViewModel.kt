package com.charuniverse.mycoordinate

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.charuniverse.mycoordinate.utils.Constants
import pub.devrel.easypermissions.EasyPermissions

class MainViewModel : ViewModel() {

    private lateinit var activity: Activity

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

    fun initViewModel(activity: Activity) {
        this.activity = activity
    }

    fun checkLocationPermission() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission(): Boolean =
        EasyPermissions.hasPermissions(
            activity.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    abstract class MainUiState {
        object Idle                             : MainUiState()
        object Loading                          : MainUiState()
        object Success                          : MainUiState()
        data class Error(val message: String)   : MainUiState()
    }
}