package com.flashback.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** 處理 RECORD_AUDIO、CAMERA、POST_NOTIFICATIONS 權限請求 */
@Composable
fun PermissionHandler(
    onAllGranted: () -> Unit,
    onDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    var requested by remember { mutableStateOf(false) }

    val permissions = buildList {
        add(Manifest.permission.RECORD_AUDIO)
        add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val audioGranted = results[Manifest.permission.RECORD_AUDIO] == true
        if (audioGranted) {
            onAllGranted()
        } else {
            onDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (!requested) {
            requested = true
            val allGranted = permissions.all { perm ->
                ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
            }
            if (allGranted) {
                onAllGranted()
            } else {
                launcher.launch(permissions)
            }
        }
    }
}
