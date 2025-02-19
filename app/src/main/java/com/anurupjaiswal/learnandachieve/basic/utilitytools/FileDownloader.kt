package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

object FileDownloader {

    private const val TAG = "FileDownload"

    /**
     * Downloads a file and reports progress via the provided [listener].
     *
     * @param context The context (used to open the file when done)
     * @param fileUrl The URL of the file to download.
     * @param token The authentication token.
     * @param fileName The name to save the file as.
     * @param listener The listener for progress and download completion/failure.
     */
    fun downloadFile(
        context: Context,
        fileUrl: String,
        token: String,
        fileName: String,
        listener: FileDownloadListener
    ) {
        // Immediately signal that the download is starting.
        listener.onProgressUpdate(0)

        val apiService = RetrofitClient.client
        apiService.downloadFile(fileUrl, "Bearer $token").enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    // Save the file on a background thread
                    GlobalScope.launch(Dispatchers.IO) {
                        val file = saveFile(response.body()!!, fileName, listener)
                        withContext(Dispatchers.Main) {
                            if (file != null) {
                                listener.onDownloadComplete(file)
                                // Optionally open the file automatically:
                                openFile(context, file)
                            } else {
                                listener.onDownloadFailed("Failed to save file")
                            }
                        }
                    }
                } else {
                    listener.onDownloadFailed("Download failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onDownloadFailed("Download error: ${t.message}")
            }
        })
    }

    /**
     * Saves the file from the response body to the downloads directory,
     * updating progress via the provided [listener].
     */
    private suspend fun saveFile(
        body: ResponseBody,
        fileName: String,
        listener: FileDownloadListener
    ): File? {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            var totalBytesRead = 0L
            val fileSize = body.contentLength()

            // Immediately update progress to 0% (redundant if already called in downloadFile)
            withContext(Dispatchers.Main) { listener.onProgressUpdate(0) }

            body.byteStream().use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(4096)
                    var bytesRead = inputStream.read(buffer)
                    while (bytesRead != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        if (fileSize > 0) {
                            val progress = ((totalBytesRead * 100) / fileSize).toInt()
                            withContext(Dispatchers.Main) {
                                listener.onProgressUpdate(progress)
                            }
                        }
                        bytesRead = inputStream.read(buffer)
                    }
                    outputStream.flush()
                }
            }
            file
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { listener.onDownloadFailed("File save error: ${e.message}") }
            null
        }
    }

    /**
     * Opens the downloaded file using a FileProvider.
     */
    private fun openFile(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(file.name))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening file: ${e.message}")
        }
    }

    /**
     * Returns a MIME type based on the file extension.
     */
    private fun getMimeType(fileName: String): String {
        return when {
            fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
            fileName.endsWith(".jpg", ignoreCase = true) || fileName.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
            fileName.endsWith(".png", ignoreCase = true) -> "image/png"
            fileName.endsWith(".mp4", ignoreCase = true) -> "video/mp4"
            fileName.endsWith(".mp3", ignoreCase = true) -> "audio/mpeg"
            else -> "*/*"
        }
    }
}
