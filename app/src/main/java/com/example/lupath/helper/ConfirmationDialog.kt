package com.example.lupath.helper

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,    // Standard M3 name for dismiss action
    onConfirmation: () -> Unit,    // Standard M3 name for confirm action
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector? = null,       // Optional icon for the dialog title area
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = if (icon != null) { // Conditionally provide the icon composable
            { Icon(icon, contentDescription = "Dialog Icon", tint = MaterialTheme.colorScheme.primary) }
        } else {
            null // No icon if null is passed
        },
        title = {
            Text(text = dialogTitle, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text(text = dialogText, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                     // Usually, the caller handles dismissing after confirmation if needed
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(dismissButtonText)
            }
        }
        // Material 3 AlertDialog uses theme colors by default for container, title, text.
        // For more advanced color customization, you'd use the 'colors' parameter:
//         colors = AlertDialogDefaults.alertDialogColors(
//             containerColor = MaterialTheme.colorScheme.surface,
//             titleContentColor = MaterialTheme.colorScheme.onSurface,
//             textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
//             iconContentColor = MaterialTheme.colorScheme.primary
//         ),
//         shape = MaterialTheme.shapes.extraLarge // Example shape
    )
}