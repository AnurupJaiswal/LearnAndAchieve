package com.anurupjaiswal.learnandachieve.basic.utilitytools

import java.io.File

interface FileDownloadListener {
    fun onProgressUpdate(progress: Int)  // progress in percentage (0–100)
    fun onDownloadComplete(file: File)
    fun onDownloadFailed(error: String)
}