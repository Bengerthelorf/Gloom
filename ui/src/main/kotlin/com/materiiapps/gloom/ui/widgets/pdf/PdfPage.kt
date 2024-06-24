package com.materiiapps.gloom.ui.widgets.pdf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.materiiapps.gloom.ui.R
import com.materiiapps.gloom.ui.components.ErrorMessage
import dev.zt64.compose.pdf.RemotePdfState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun PdfPage(
    pdfState: RemotePdfState,
    page: Int
) {
    val zoomState = rememberZoomState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        dev.zt64.compose.pdf.component.PdfPage(
            state = pdfState,
            index = page,
            errorIndicator = {
                ErrorMessage(
                    message = stringResource(R.string.msg_file_load_error),
                    onRetryClick = { pdfState.loadPdf() },
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            loadingIndicator = {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            modifier = Modifier
                .align(Alignment.Center)
                .zoomable(zoomState)
        )
    }
}