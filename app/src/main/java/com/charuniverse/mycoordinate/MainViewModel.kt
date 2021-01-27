package com.charuniverse.mycoordinate

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.mycoordinate.utils.Constants
import com.charuniverse.mycoordinate.utils.Constants.MAIN_VIEW_MODEL_TAG
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pub.devrel.easypermissions.EasyPermissions

class MainViewModel : ViewModel() {

    private lateinit var activity: Activity

    private lateinit var mFusedLocationClient:
            FusedLocationProviderClient

    private val _mainUiState = MutableLiveData<MainUiState>()
    val mainUiState: LiveData<MainUiState> = _mainUiState

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    fun initViewModel(activity: Activity) {
        this.activity = activity
        mFusedLocationClient = LocationServices
            .getFusedLocationProviderClient(activity)
    }

    fun checkLocationPermission() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }
        if (!hasLocationEnable()) {
            requestEnableLocation()
            return
        }
    }

    fun startLocationUpdateListener() {
        listenToLocationUpdates()
    }

    private fun setUiState(state: String, errorMessage: String = "") {
        _mainUiState.value = when (state) {
            Constants.IDLE_STATE        -> MainUiState.Idle
            Constants.LOADING_STATE     -> MainUiState.Loading
            Constants.SUCCESS_STATE     -> MainUiState.Success
            Constants.ERROR_STATE       -> MainUiState.Error(errorMessage)
            else                        -> MainUiState.Idle
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

    private fun hasLocationEnable(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestEnableLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val result = LocationServices.getSettingsClient(activity)
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable = exception as ResolvableApiException
                            resolvable.startResolutionForResult(
                                activity, LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(MAIN_VIEW_MODEL_TAG, "requestEnableLocation: ${e.message}", e)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() = viewModelScope.launch {
        try {
            _location.value = mFusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.e(MAIN_VIEW_MODEL_TAG, "getLastLocation: ${e.message}", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun listenToLocationUpdates() {
        val mLocationRequest = LocationRequest().also {
            it.priority         = LocationRequest.PRIORITY_HIGH_ACCURACY
            it.interval         = Constants.DEFAULT_INTERVAL
            it.fastestInterval  = Constants.FASTEST_INTERVAL
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            _location.value = locationResult.lastLocation
        }
    }

    abstract class MainUiState {
        object Idle                             : MainUiState()
        object Loading                          : MainUiState()
        object Success                          : MainUiState()
        data class Error(val message: String)   : MainUiState()
    }
}