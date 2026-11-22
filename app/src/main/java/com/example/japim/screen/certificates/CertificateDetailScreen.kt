package com.example.japim.screen.certificates

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.japim.graphql.MyCertificatesQuery
import com.example.japim.util.CertificatePdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun CertificateDetailScreen(
    certificate: MyCertificatesQuery.MyCertificate,
    onBack: () -> Unit,
    onDownload: () -> Unit
) {
    val context = LocalContext.current

    var bitmap by remember(certificate) {
        mutableStateOf<Bitmap?>(null)
    }

    var isLoading by remember(certificate) {
        mutableStateOf(true)
    }

    var error by remember(certificate) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(certificate) {
        try {
            bitmap = withContext(Dispatchers.IO) {

                val file = File.createTempFile(
                    "certificate_",
                    ".pdf",
                    context.cacheDir
                )

                try {
                    file.outputStream().use { output ->
                        CertificatePdfGenerator.generate(
                            certificate,
                            output
                        )
                    }

                    ParcelFileDescriptor.open(
                        file,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    ).use { descriptor ->

                        PdfRenderer(descriptor).use { renderer ->

                            renderer.openPage(0).use { page ->

                                val preview = Bitmap.createBitmap(
                                    page.width * 2,
                                    page.height * 2,
                                    Bitmap.Config.ARGB_8888
                                )

                                page.render(
                                    preview,
                                    null,
                                    null,
                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                )

                                preview
                            }
                        }
                    }
                } finally {
                    file.delete()
                }
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = e.message ?: "Erro ao visualizar certificado."
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Visualizar Certificado",
            color = Color.Yellow,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Color.Yellow
                    )
                }

                error != null -> {
                    Text(
                        text = error.orEmpty(),
                        color = Color.Red
                    )
                }

                else -> {
                    bitmap?.let { image ->
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = "Certificado",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onDownload,
            enabled = bitmap != null && !isLoading && error == null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text(
                text = "Baixar Certificado",
                color = Color.White
            )
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Voltar",
                color = Color.Yellow
            )
        }
    }
}