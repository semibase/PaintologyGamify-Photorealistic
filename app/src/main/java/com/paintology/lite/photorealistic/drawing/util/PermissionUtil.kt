package com.paintology.lite.photorealistic.drawing.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.paintology.lite.photorealistic.drawing.R
import com.permissionx.guolindev.PermissionX

object PermissionUtil {

    val allPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        )
    } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    val permissionCamera = arrayOf(Manifest.permission.CAMERA)

    fun Context.checkPermissions(permissions: Array<String>): Boolean {
        var allGranted = true
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false
            }
        }
        return allGranted
    }

    fun Activity.requestPermission(
        permission: Array<String>, activity: FragmentActivity, click: ((Boolean) -> Unit)? = null,
    ) {
        PermissionX.init(activity).permissions(
            *permission
        ).onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                getString(R.string.permission_necessary),
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                getString(R.string.cant_read_permission_is_not_enabled),
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.request { allGranted, _, _ ->
            if (allGranted) {
                click?.invoke(true)
            }
        }
    }
}