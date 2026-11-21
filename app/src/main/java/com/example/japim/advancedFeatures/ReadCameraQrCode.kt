package com.example.japim.advancedFeatures

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class ReadCameraQrCode(
    private val onQrCodeRead: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private var isScanning = true

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && isScanning) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { rawContent ->
                            if (isScanning) {
                                isScanning = false // Trava para evitar leituras múltiplas consecutivas
                                onQrCodeRead(rawContent) // Retorna o texto lido
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    // Tratar falha de leitura se necessário
                }
                .addOnCompleteListener {
                    imageProxy.close() // Libera o frame da câmera
                }
        } else {
            imageProxy.close()
        }
    }
}