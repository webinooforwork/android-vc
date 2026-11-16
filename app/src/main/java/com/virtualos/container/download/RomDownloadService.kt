package com.virtualos.container.download

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.virtualos.container.data.ContainerDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/** HTTPS, Range-resumable artifact downloader. Downloaded files are not bootable in this project. */
class RomDownloadService : Service() {
    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(0, TimeUnit.MILLISECONDS).build()
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra(EXTRA_URL) ?: return START_NOT_STICKY
        val sha = intent.getStringExtra(EXTRA_SHA256)?.lowercase() ?: return START_NOT_STICKY
        val romId = intent.getStringExtra(EXTRA_ROM_ID) ?: return START_NOT_STICKY
        val name = File(intent.getStringExtra(EXTRA_NAME) ?: "rom.zip").name
        val directory = getExternalFilesDir(null) ?: filesDir
        val partial = File(directory, "$name.part")
        val destination = File(directory, name)
        startForeground(NOTIFICATION_ID, notification("Downloading $name"))
        Thread {
            try {
                require(url.startsWith("https://")) { "Only HTTPS downloads are allowed" }
                val offset = partial.length()
                val request = Request.Builder().url(url).apply { if (offset > 0) header("Range", "bytes=$offset-") }.build()
                client.newCall(request).execute().use { response ->
                    check(response.isSuccessful) { "HTTP ${response.code}" }
                    val append = offset > 0 && response.code == 206
                    if (offset > 0 && !append) partial.delete()
                    val startBytes = if (append) offset else 0L
                    val total = response.body!!.contentLength().let { if (it < 0) -1 else it + startBytes }
                    val started = System.nanoTime()
                    var downloaded = startBytes
                    var lastPublished = started
                    response.body!!.byteStream().use { input -> FileOutputStream(partial, append).use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        while (true) {
                            val count = input.read(buffer); if (count < 0) break
                            output.write(buffer, 0, count); downloaded += count
                            val now = System.nanoTime()
                            if (now - lastPublished >= 300_000_000L) {
                                val seconds = (now - started) / 1e9
                                val speed = if (seconds > 0) (downloaded - startBytes) / seconds else 0.0
                                val eta = if (speed > 0 && total > downloaded) ((total - downloaded) / speed).toLong() else null
                                mutableProgress.value = DownloadProgress(romId, downloaded, total, speed, eta, "Downloading")
                                lastPublished = now
                            }
                        }
                    } }
                }
                val actual = sha256(partial)
                check(actual == sha) { "SHA-256 mismatch" }
                check(partial.renameTo(destination)) { "Could not finalize download" }
                runBlocking {
                    val dao = ContainerDatabase.get(this@RomDownloadService).romDao()
                    dao.get(romId)?.let { dao.put(it.copy(localPath = destination.absolutePath, isDownloaded = true)) }
                }
                mutableProgress.value = DownloadProgress(romId, destination.length(), destination.length(), 0.0, 0, "Verified")
                getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification("Verified: $name"))
            } catch (error: Exception) {
                mutableProgress.value = DownloadProgress(romId, partial.length(), -1, 0.0, null, "Failed: ${error.message}")
                getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification("Download failed: ${error.message}"))
            } finally { stopForeground(STOP_FOREGROUND_DETACH); stopSelf(startId) }
        }.start()
        return START_NOT_STICKY
    }
    private fun notification(message: String) = NotificationCompat.Builder(this, CHANNEL).setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle("VirtualOS ROM downloader").setContentText(message).setOngoing(message.startsWith("Downloading")).build()
    override fun onCreate() { super.onCreate(); if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(CHANNEL, "ROM downloads", NotificationManager.IMPORTANCE_LOW)) }
    companion object {
        const val EXTRA_URL="url"; const val EXTRA_SHA256="sha256"; const val EXTRA_NAME="name"; const val EXTRA_ROM_ID="rom_id"
        private const val CHANNEL="rom-downloads"; private const val NOTIFICATION_ID=81
        private val mutableProgress=MutableStateFlow<DownloadProgress?>(null)
        val progress:StateFlow<DownloadProgress?> = mutableProgress
    }
}
data class DownloadProgress(val romId:String,val bytesDownloaded:Long,val totalBytes:Long,val bytesPerSecond:Double,val etaSeconds:Long?,val status:String)

private fun sha256(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().buffered().use { input ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) { val count = input.read(buffer); if (count < 0) break; digest.update(buffer, 0, count) }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}
