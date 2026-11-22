package com.example.japim.util

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.japim.graphql.MyCertificatesQuery
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun rememberCertificateDownload():
            (MyCertificatesQuery.MyCertificate) -> Unit {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedCertificate by remember {
        mutableStateOf<MyCertificatesQuery.MyCertificate?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/pdf"
        )
    ) { uri ->

        val certificate = selectedCertificate
        selectedCertificate = null

        if (uri != null && certificate != null) {

            scope.launch {
                try {
                    withContext(Dispatchers.IO) {

                        val outputStream =
                            context.contentResolver.openOutputStream(uri)
                                ?: throw IllegalStateException(
                                    "Unable to open output stream"
                                )

                        outputStream.use { output ->
                            CertificatePdfGenerator.generate(
                                certificate,
                                output
                            )
                        }
                    }

                    Toast.makeText(
                        context,
                        "Certificado salvo com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: CancellationException) {
                    throw e

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Erro ao salvar certificado.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    return { certificate ->

        selectedCertificate = certificate

        launcher.launch(
            "certificado_${certificate.id}.pdf"
        )
    }
}