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
    private var lastScanTime = 0L
    private val cooldownMillis = 3000L

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val currentTime = System.currentTimeMillis()
        if (mediaImage != null && isScanning && (currentTime - lastScanTime > cooldownMillis)) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { rawContent ->
                            if (isScanning && (System.currentTimeMillis() - lastScanTime > cooldownMillis)) {
                                lastScanTime = System.currentTimeMillis()
                                isScanning = false
                                onQrCodeRead(rawContent)
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    isScanning = true
                                }, cooldownMillis)
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