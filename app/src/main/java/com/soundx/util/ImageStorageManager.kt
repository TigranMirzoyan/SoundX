package com.soundx.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ImageStorageManager {
    suspend fun saveImageToInternalStorage(uri: Uri, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream =
                    context.contentResolver.openInputStream(uri) ?: return@withContext null
                val fileName = "playlist_image_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, fileName)

                file.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }

                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun deleteImageFromInternalStorage(filePath: String) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                if (file.exists()) file.delete()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}