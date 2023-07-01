package com.materiiapps.gloom.ui.widgets.release.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.materiiapps.gloom.R
import com.materiiapps.gloom.utils.getFileSizeString

@Composable
fun ReleaseAssetInstallDialog(
    fileName: String,
    onClose: (dontShowAgain: Boolean?) -> Unit,
    onConfirm: (dontShowAgain: Boolean) -> Unit
) {
    var checked by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = { onClose(null) },
        confirmButton = {
            FilledTonalButton(onClick = { onConfirm(checked) }) {
                Text(stringResource(R.string.action_install))
            }
        },
        dismissButton = {
            TextButton(onClick = { onClose(checked) }) {
                Text(stringResource(R.string.dismiss_no_thanks))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.title_install_app),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.msg_install_dialog_body, fileName),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(role = Role.Checkbox) { checked = !checked }
                        .fillMaxWidth()
                ) {
                    Checkbox(checked = checked, onCheckedChange = { checked = it })
                    Text(stringResource(R.string.label_dont_show_again))
                }
            }
        }
    )
}