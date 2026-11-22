package com.example.japim.advancedFeatures

import android.util.Base64
import com.example.japim.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object EncriptionQrCodeContent {
    private const val ALGORITHM = "AES/ECB/PKCS5Padding"
    private const val SECRET_KEY_QR_CODE = BuildConfig.SECRET_KEY_QR_CODE

    fun encrypt(value: String): String? {
        return try {
            val keySpec = SecretKeySpec(SECRET_KEY_QR_CODE.toByteArray(Charsets.UTF_8), ALGORITHM)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}