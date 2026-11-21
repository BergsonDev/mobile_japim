package com.example.japim.advancedFeatures

import android.util.Base64
import com.example.japim.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object DecryptionQrCodeContent {
    private const val ALGORITHM = "AES/ECB/PKCS5Padding"
    private const val SECRET_KEY_QR_CODE = BuildConfig.SECRET_KEY_QR_CODE

    fun decrypt(encryptedValue: String): String? {
        return try {
            val keySpec = SecretKeySpec(SECRET_KEY_QR_CODE.toByteArray(Charsets.UTF_8), ALGORITHM)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decodedBytes = Base64.decode(encryptedValue, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}