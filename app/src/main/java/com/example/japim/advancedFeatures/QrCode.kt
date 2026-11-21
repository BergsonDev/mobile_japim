package com.example.japim.advancedFeatures

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set


fun generateQrCode(codePresence: String, width: Int = 512, height: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()

        val bitMatrix = writer.encode(EncriptionQrCodeContent.encrypt(codePresence), BarcodeFormat.QR_CODE, width, height)

        val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bmp
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}