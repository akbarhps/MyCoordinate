package com.charuniverse.mycoordinate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.charuniverse.mycoordinate.dialogs.LocationDialog
import com.charuniverse.mycoordinate.dialogs.PermissionDialog
import com.google.android.gms.location.LocationRequest
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)
            .get(MainViewModel::class.java)
            .also {
                it.initViewModel(this)
                it.checkLocationAvailability()
            }

        buttonClickListener()
        locationListener()
    }

    private fun buttonClickListener() {
        btnGetLastLoc.setOnClickListener {
            viewModel.getLastLocation()
        }

        btnListenLocUpdates.setOnClickListener {
            viewModel.startLocationUpdateListener()
        }

        btnRemoveLocUpdates.setOnClickListener {
            viewModel.removeLocationUpdateListener()
        }
    }

    private fun locationListener() {
        viewModel.location.observe(this, {
            val date = convertMilisToDate(it.time)
            val formattedLocation = """
                Time : $date
                Latitude : ${it.latitude}
                Longitude : ${it.longitude}
            """.trimIndent()
            textView.text = formattedLocation
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertMilisToDate(timeInMillis: Long): String {
        val sdf     = SimpleDateFormat("MMMM/dd/yyyy H:mm:ss")
        val netDate = Date(timeInMillis)
        return sdf.format(netDate)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val hasUserEnableLocation = resultCode == Activity.RESULT_OK
        if (requestCode == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            if (!hasUserEnableLocation) {
                LocationDialog().show(supportFragmentManager, null)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            PermissionDialog().show(supportFragmentManager, null)
        }
    }
}