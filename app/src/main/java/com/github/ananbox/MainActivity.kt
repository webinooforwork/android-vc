package com.github.ananbox

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.virtualos.container.R
import com.virtualos.container.databinding.ActivityMainBinding
import com.hzy.libp7zip.P7ZipApi
import java.io.File
import java.io.FilterInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.Locale
import kotlin.concurrent.thread
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val READ_REQUEST_CODE = 2
    private val DEFAULT_ROOTFS_URL = "https://github.com/Ananbox/ananbox/releases/download/v0.0.2/rootfs_arm64.7z"
    private val DEFAULT_ROOTFS_SHA256 = "31c7976b6b3b6f6df1028b8dde67fffc2d7b3b7a1abb5f948de3d6517860fe4b"
    private lateinit var mSurfaceView: SurfaceView
    private val mSurfaceCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            val surface = holder.surface
            val windowManager = windowManager
            val defaultDisplay = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            defaultDisplay.getRealMetrics(displayMetrics)
            val dpi = displayMetrics.densityDpi
            Log.i(TAG, "Runtime initializing..")
            if(Anbox.initRuntime(mSurfaceView.width, mSurfaceView.height, dpi)) {
                Anbox.createSurface(surface)
                Anbox.startRuntime()
                Anbox.startContainer(applicationContext.applicationInfo.nativeLibraryDir + "/libproot.so")
            }
            else {
                Anbox.createSurface(surface)
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.i(
                TAG,
                "surfaceChanged: " + mSurfaceView.width + "x" + mSurfaceView.height
            )
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
//            Renderer.removeWindow(holder.surface)
            Anbox.destroySurface()
            Log.i(TAG, "surfaceDestroyed!")
        }
    }

    private lateinit var binding: ActivityMainBinding
    private val receiver = BinderReceiver()
    private val handlerThread = HandlerThread("BinderReceiverThread")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handlerThread.start()
        ContextCompat.registerReceiver(this ,receiver,
            IntentFilter("com.github.ananbox.BINDER"),
            null, Handler(handlerThread.looper),
            ContextCompat.RECEIVER_EXPORTED)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!File(filesDir, "rootfs/init").isFile) {
            AlertDialog.Builder(this)
                .setTitle(R.string.rom_installer_title)
                .setMessage(R.string.rom_installer_message)
                .setPositiveButton(R.string.download_arm64_rootfs) { _, _ ->
                    installRootfs({
                        val client = OkHttpClient.Builder()
                            .readTimeout(0, TimeUnit.MILLISECONDS)
                            .callTimeout(30, TimeUnit.MINUTES)
                            .build()
                        val response = client.newCall(Request.Builder().url(DEFAULT_ROOTFS_URL).build()).execute()
                        if (!response.isSuccessful) {
                            response.close()
                            error("Download failed with HTTP ${response.code}")
                        }
                        val body = response.body
                        if (body == null) {
                            response.close()
                            error("The download response was empty")
                        }
                        object : FilterInputStream(body.byteStream()) {
                            override fun close() {
                                try { super.close() } finally { response.close() }
                            }
                        }
                    }, DEFAULT_ROOTFS_SHA256)
                }
                .setNeutralButton(R.string.rom_installer_install) { _, _ -> chooseRootfsFile() }
                .setNegativeButton(R.string.cancel) { _, _ -> finish() }
                .setCancelable(false)
                .show()
            return
        }

        Anbox.setPath(filesDir.path)

        mSurfaceView = SurfaceView(this)
        mSurfaceView.getHolder().addCallback(mSurfaceCallback)
        binding.root.addView(mSurfaceView, 0)

        // put in onResume?
        mSurfaceView.setOnTouchListener(Anbox)
        binding.fab.setOnClickListener {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Anbox.stopRuntime()
        // Leaving for good (back / recents swipe) used to leave the container
        // running. Rotation and other config changes recreate the activity
        // too, so only tear the guest down when actually finishing.
        if (isFinishing) {
            thread { Anbox.shutdown() }
        }
        unregisterReceiver(receiver)
    }

    private fun chooseRootfsFile() {
        startActivityForResult(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                setType("application/x-7z-compressed")
            },
            READ_REQUEST_CODE
        )
    }

    private fun installRootfs(openSource: () -> InputStream?, expectedSha256: String? = null) {
        val progressDialog = ProgressDialog(this).apply {
            setTitle(getString(R.string.rom_installer_extracting_title))
            setMessage(getString(R.string.rom_installer_extracting_msg))
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            max = 1000
            setProgress(0)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }

        thread(name = "install-ananbox-rootfs") {
            val archive = File(filesDir, "rootfs.7z")
            val tempArchive = File(filesDir, "rootfs.7z.partial")
            val failure = runCatching {
                val digest = MessageDigest.getInstance("SHA-256")
                val source = openSource() ?: error("Could not open the selected rootfs archive")
                source.use { input ->
                    tempArchive.outputStream().buffered().use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var downloaded = 0L
                        var count = input.read(buffer)
                        while (count != -1) {
                            output.write(buffer, 0, count)
                            digest.update(buffer, 0, count)
                            downloaded += count
                            val progress = ((downloaded * 1000L) / 189_561_958L).toInt().coerceIn(0, 1000)
                            if (progress % 10 == 0) runOnUiThread { progressDialog.progress = progress }
                            count = input.read(buffer)
                        }
                    }
                }
                archive.delete()
                check(tempArchive.renameTo(archive)) { "Could not save the rootfs archive" }
                val actualSha256 = digest.digest().joinToString("") { "%02x".format(it.toInt() and 0xff) }
                if (expectedSha256 != null) {
                    check(archive.length() == 189_561_958L && actualSha256 == expectedSha256) {
                        "The downloaded rootfs size or SHA-256 checksum is invalid"
                    }
                }
                val rootfs = File(filesDir, "rootfs")
                if (rootfs.exists() && !File(rootfs, "init").isFile) rootfs.deleteRecursively()
                val cpu = Runtime.getRuntime().availableProcessors()
                val result = P7ZipApi.executeCommand(
                    java.lang.String.format(Locale.US, "7z x -mmt=%d -aoa '%s' '-o%s'", cpu, archive.absolutePath, filesDir)
                )
                check(result == 0 && File(rootfs, "init").isFile) {
                    "The selected archive is not a usable Ananbox rootfs (7z exit code $result)"
                }
                val captured = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    RawParcelCapture.capture(File(rootfs, "localBroadcastIntent").absolutePath, "local") &&
                    RawParcelCapture.capture(File(rootfs, "binderBroadcastIntent").absolutePath, "binder")
                File(rootfs, "trans_code").delete()
                if (!captured) Log.e(TAG, "Raw parcel capture failed; guest binder startup may fail")
                archive.delete()
                File(filesDir, "tmp").mkdirs()
            }.exceptionOrNull()
            tempArchive.delete()
            if (failure != null) archive.delete()

            runOnUiThread {
                progressDialog.dismiss()
                if (failure != null) {
                    Log.e(TAG, "Could not install Ananbox rootfs", failure)
                    AlertDialog.Builder(this)
                        .setTitle(R.string.rootfs_install_failed_title)
                        .setMessage(getString(R.string.rootfs_install_failed_message, failure.message ?: failure.javaClass.simpleName))
                        .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
                        .show()
                } else {
                    recreate()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK || data?.data == null) {
                finish()
                return
            }
            val uri: Uri = data.data!!
            installRootfs(openSource = { contentResolver.openInputStream(uri) })
        }
    }
}
