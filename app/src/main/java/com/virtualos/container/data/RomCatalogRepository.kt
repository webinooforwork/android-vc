package com.virtualos.container.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.concurrent.TimeUnit

/** Configure a trusted HTTPS JSON manifest mirror here. Empty by default: no ROM files are falsely advertised as downloadable. */
class RomCatalogRepository(context: Context) {
    private val dao = ContainerDatabase.get(context).romDao()
    val roms: Flow<List<RomImageEntity>> = dao.observeAll()
    private val client = OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(45, TimeUnit.SECONDS).build()

    suspend fun refresh(manifestUrl: String) = withContext(Dispatchers.IO) {
        require(manifestUrl.startsWith("https://")) { "Catalog URL must use HTTPS" }
        val request = Request.Builder().url(manifestUrl).build()
        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "Catalog request failed: ${response.code}" }
            val array = JSONArray(response.body?.string().orEmpty())
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val rom = RomImageEntity(item.getString("id"), item.getString("versionName"), item.getInt("apiLevel"), item.getString("architecture"), item.getLong("fileSizeBytes"), item.getString("sha256Checksum"), item.getString("downloadUrl"), item.getString("rootfsLayoutType"))
                require(rom.downloadUrl.startsWith("https://")) { "ROM URL must use HTTPS" }
                require(rom.sha256Checksum.matches(Regex("[a-fA-F0-9]{64}"))) { "Invalid SHA-256 in catalog" }
                dao.put(rom)
            }
        }
    }
}
