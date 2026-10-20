package com.example.masterai

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConsentDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Grant automation permission?") },
        text = {
            Text("This action will access device data or trigger a system action. Do you approve?")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Allow")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Decline")
            }
        }
    )
}
