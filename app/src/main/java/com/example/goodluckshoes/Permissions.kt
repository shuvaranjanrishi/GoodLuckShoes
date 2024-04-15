package com.example.goodluckshoes

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object Permissions {

    private const val REQUEST_CODE_PERMISSIONS = 100

    private val PERMISSIONS_STORAGE = arrayOf(
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE
    )

    //check permissions.
    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have read or write permission
        val writePermission = ActivityCompat.checkSelfPermission(
            activity!!,
         WRITE_EXTERNAL_STORAGE
        )
        val readPermission =
            ActivityCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE)
        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }
}