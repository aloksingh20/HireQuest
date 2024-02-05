package com.example.gethired.pdf

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.util.Log

class DownloadReceiver(private val downloadId: Long, private val context: Context) : BroadcastReceiver() {

    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    @SuppressLint("Range")
    override fun onReceive(context: Context, intent: Intent) {
        val receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        if (receivedDownloadId == downloadId) {
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
            if (cursor.moveToFirst()) {
                // ...
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        Log.d("DownloadManager", "Download successful")

                        // Open downloaded PDF/Resume
                        val downloadUri = downloadManager.getUriForDownloadedFile(downloadId)
                        val intent = Intent(Intent.ACTION_VIEW).setDataAndType(downloadUri, "application/pdf")
                        context.startActivity(intent)

                        // Hide notification and unregister receiver
                        notificationManager.cancel(1)
                        context.unregisterReceiver(this)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        Log.e("DownloadManager", "Download failed")

                        // Hide notification and unregister receiver
                        notificationManager.cancel(1)
                        context.unregisterReceiver(this)
                    }
                }
            }
            cursor.close()

        }
    }
}
