package com.tangping.androidpractice.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetworkUtils {
    suspend fun downloadAndSaveJson(context: Context, url: String, fileName: String) =
        suspendCoroutine { continuation ->
            val connection: HttpURLConnection?
            val outputStream: FileOutputStream?

            val file = File(context.cacheDir, fileName)

            connection = URL(url).openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                continuation.resumeWithException(
                    IOException("HTTP error code: ${connection.responseCode}")
                )
            }

            val inputStream: InputStream? = connection.inputStream
            outputStream = FileOutputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int

            if (inputStream != null) {
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()
                connection.disconnect()

                continuation.resume(true)
            } else {
                outputStream.close()
                connection.disconnect()

                continuation.resume(false)
            }
        }
}