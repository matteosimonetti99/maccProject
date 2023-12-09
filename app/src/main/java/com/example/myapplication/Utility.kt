package com.example.myapplication

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.io.InputStream


class Utility {

    companion object {
        val bootstrapBlue = Color(0xFF007BFF)
        val bootstrapGreen = Color(0xFF28A745)
        val bootstrapRed = Color(0xFFDC3545)
        val bootstrapSecondary = Color(0xFF6C757D)
        val bootstrapInfo = Color(0xFF17A2B8)
        val bootstrapWarning = Color(0xFFFFC107)
        val bootstrapLight = Color(0xFFF8F9FA)
        val bootstrapDark = Color(0xFF343A40)

        fun base64ToBitmap(base64: String): Bitmap {
            var newbase64=base64
            val prefixesToRemove = arrayOf(
                "data:image/jpeg;base64,",
                "data:image/jpg;base64,",
                "data:image/png;base64,"
            )
            
            // Iterate through each prefix
            for (prefix in prefixesToRemove) {
                // Check if the Base64 string starts with the current prefix
                if (base64.startsWith(prefix)) {
                    // Remove the prefix
                    newbase64 = base64.substring(prefix.length)
                }
            }

            val decodedString: ByteArray = Base64.decode(newbase64, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            return decodedByte
        }



        fun convertImageUriToBase64(contentResolver: ContentResolver, imageUri: Uri?): String? {
            try {
                // Step 1: Read image data from URI
                val uri2 = imageUri!!
                val inputStream: InputStream? = contentResolver.openInputStream(uri2)

                // Step 2: Convert to byte array
                val byteArray: ByteArray? = inputStream?.readBytes()

                // Step 3: Determine image extension
                val extension: String? = contentResolver.getType(uri2)?.substringAfterLast('/')
                val mimeType = "image/$extension"

                // Step 4: Convert to Base64
                if (byteArray != null) {
                    val base64String = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
                    return "data:$mimeType;base64,$base64String"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun validateEmail(email: String): Boolean {
            val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
            return email.matches(emailRegex)
        }


    }
}
