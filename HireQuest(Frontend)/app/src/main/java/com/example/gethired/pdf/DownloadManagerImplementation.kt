package com.example.gethired.pdf

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gethired.R
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService

class DownloadManagerImplementation(private val context: Context, private val tokenManager: TokenManager) {

    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val retrofitAPI: ApiService =
        RetrofitClient(tokenManager).getApiService()

    fun downloadCvWithProgress(cvId: Long) {
//        val call = retrofitAPI.downloadPdf(cvId)
//        call.enqueue(object : Callback<String> {
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                if (response.isSuccessful) {
//                    val downloadUrl = response.body()!!
//                    downloadCvWithProgress(downloadUrl, "cv.pdf")
//                } else {
//                    // Handle error
//                }
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                // Handle error
//            }
//        })
    }

    private fun downloadCvWithProgress(downloadUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        val downloadId = downloadManager.enqueue(request)
        showDownloadProgress(downloadId)

        context.registerReceiver(
            DownloadReceiver(downloadId, context),
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    @SuppressLint("Range")
    private fun showDownloadProgress(downloadId: Long) {
        Thread {
            while (true) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                    val progress = ((downloadedBytes / totalBytes.toDouble()) * 100).toInt()

                    updateProgressBar(progress)
                    updateNotification(progress)
                }
                cursor.close()

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun updateProgressBar(progress: Int) {
        // Update your UI progress bar here
        Log.d("DownloadProgress", "Progress: $progress")
    }

    private fun updateNotification(progress: Int) {
        val notificationBuilder = NotificationCompat.Builder(context, "download_channel")
            .setSmallIcon(R.drawable.icon_download)
            .setContentTitle("Downloading CV...")
            .setContentText("Downloading CV...")
            .setProgress(100, progress, false)

        notificationManager.notify(1, notificationBuilder.build())
    }
}
