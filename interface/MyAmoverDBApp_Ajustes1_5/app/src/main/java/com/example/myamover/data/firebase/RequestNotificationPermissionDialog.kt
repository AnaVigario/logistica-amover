package com.example.myamover.data.firebase

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(

    modifier: Modifier = Modifier,
    openDialog: MutableState<Boolean>,
    permissionState: PermissionState
) {

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                permissionState.launchPermissionRequest()
            },
            title = {
                Text(text = "Permission required")
            },
            text = { Text("This app reuires notofication permission to show notifications") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null
                )
            }, confirmButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    permissionState.launchPermissionRequest()
                }) {
                    Text(text = "Ok")
                }
            }
        )
    }
}