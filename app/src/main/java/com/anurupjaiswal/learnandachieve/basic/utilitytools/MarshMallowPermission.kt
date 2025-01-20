/*
 * *
 *  * Created by labhansh Gupta on 29/03/23, 6:42 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 29/03/23, 10:40 AM
 *  * All rights reserved.
 *
 */
package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MarshMallowPermission(var activity: Activity) {
    fun checkPermissionForRecord(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionForExternalStorage(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissionForExternalStorage()
            false
        }
    }

    fun checkPermissionForLocation(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionForCamera(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissionForCamera()
            false
        }
    }

    fun requestPermissionForRecord() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            //Toast.makeText(activity, "Microphone permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun requestPermissionForLocation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //Toast.makeText(activity, "Microphone permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RECORD_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun requestPermissionForExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                activity,
                "External Storage permission needed. Please allow in App Settings for additional functionality.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun requestPermissionForCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
        ) {
            Toast.makeText(
                activity,
                "Camera permission needed. Please allow in App Settings for additional functionality.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        const val RECORD_PERMISSION_REQUEST_CODE = 1
        const val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2
        const val CAMERA_PERMISSION_REQUEST_CODE = 3
    }
}