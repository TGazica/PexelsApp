package org.tgazica.pexelsapp.data.cache

import android.util.Log
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.cache.storage.CachedResponseData
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.util.collections.ConcurrentMap
import io.ktor.util.date.GMTDate
import io.ktor.util.flattenEntries
import io.ktor.util.hex
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.close
import io.ktor.utils.io.core.use
import io.ktor.utils.io.discard
import io.ktor.utils.io.jvm.javaio.copyTo
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

interface AppCacheStorage : CacheStorage {
    suspend fun clearCache()
}

fun AppCacheStorage(
    directory: LocalCache,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): AppCacheStorage = KtorCache(KtorFileCacheStorage(directory, dispatcher))

class KtorCache(
    private val delegate: AppCacheStorage
) : AppCacheStorage {

    private val store = ConcurrentMap<Url, Set<CachedResponseData>>()

    override suspend fun store(url: Url, data: CachedResponseData) {
        delegate.store(url, data)
        store[url] = delegate.findAll(url)
    }

    override suspend fun find(url: Url, varyKeys: Map<String, String>): CachedResponseData? {
        if (!store.containsKey(url)) {
            store[url] = delegate.findAll(url)
        }
        val data = store.getValue(url)
        return data.find {
            varyKeys.all { (key, value) -> it.varyKeys[key] == value }
        }
    }

    override suspend fun findAll(url: Url): Set<CachedResponseData> {
        if (!store.containsKey(url)) {
            store[url] = delegate.findAll(url)
        }
        return store.getValue(url)
    }

    override suspend fun clearCache() {
        store.clear()
        delegate.clearCache()
    }
}

private class KtorFileCacheStorage(
    private val directory: File,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppCacheStorage {

    private val mutexes = ConcurrentMap<String, Mutex>()

    init {
        directory.mkdirs()
    }

    override suspend fun clearCache() {
        directory.listFiles()?.forEach { it.deleteRecursively() }
    }

    override suspend fun store(url: Url, data: CachedResponseData): Unit = withContext(dispatcher) {
        val urlHex = key(url)
        val caches = readCache(urlHex).filterNot { it.varyKeys == data.varyKeys } + data
        writeCache(urlHex, caches)
    }

    override suspend fun findAll(url: Url): Set<CachedResponseData> {
        return readCache(key(url)).toSet()
    }

    override suspend fun find(url: Url, varyKeys: Map<String, String>): CachedResponseData? {
        val data = readCache(key(url))
        return data.find {
            varyKeys.all { (key, value) -> it.varyKeys[key] == value }
        }
    }

    private fun key(url: Url) =
        hex(MessageDigest.getInstance("MD5").digest(url.toString().encodeToByteArray()))

    private suspend fun writeCache(urlHex: String, caches: List<CachedResponseData>) =
        coroutineScope {
            val mutex = mutexes.computeIfAbsent(urlHex) { Mutex() }
            mutex.withLock {
                val channel = ByteChannel()
                try {
                    File(directory, urlHex).outputStream().buffered().use { output ->
                        launch {
                            channel.writeInt(caches.size)
                            for (cache in caches) {
                                writeCache(channel, cache)
                            }
                            channel.close()
                        }
                        channel.copyTo(output)
                    }
                } catch (cause: Exception) {
                    Log.e(
                        this::class.java.simpleName,
                        "Exception during saving a cache to a file: ${cause.stackTraceToString()}"
                    )
                }
            }
        }

    private suspend fun readCache(urlHex: String): Set<CachedResponseData> {
        val mutex = mutexes.computeIfAbsent(urlHex) { Mutex() }
        mutex.withLock {
            val file = File(directory, urlHex)
            if (!file.exists()) return emptySet()

            try {
                file.inputStream().buffered().use {
                    val channel = it.toByteReadChannel()
                    val requestsCount = channel.readInt()
                    val caches = mutableSetOf<CachedResponseData>()
                    for (i in 0 until requestsCount) {
                        caches.add(readCache(channel))
                    }
                    channel.discard()
                    return caches
                }
            } catch (cause: Exception) {
                Log.e(
                    this::class.java.simpleName,
                    "Exception during cache lookup in a file: ${cause.stackTraceToString()}"
                )
                return emptySet()
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun writeCache(channel: ByteChannel, cache: CachedResponseData) {
        channel.writeStringUtf8(cache.url.toString() + "\n")
        channel.writeInt(cache.statusCode.value)
        channel.writeStringUtf8(cache.statusCode.description + "\n")
        channel.writeStringUtf8(cache.version.toString() + "\n")
        val headers = cache.headers.flattenEntries()
        channel.writeInt(headers.size)
        for ((key, value) in headers) {
            channel.writeStringUtf8(key + "\n")
            channel.writeStringUtf8(value + "\n")
        }
        channel.writeLong(cache.requestTime.timestamp)
        channel.writeLong(cache.responseTime.timestamp)
        channel.writeLong(cache.expires.timestamp)
        channel.writeInt(cache.varyKeys.size)
        for ((key, value) in cache.varyKeys) {
            channel.writeStringUtf8(key + "\n")
            channel.writeStringUtf8(value + "\n")
        }
        channel.writeInt(cache.body.size)
        channel.writeFully(cache.body)
    }

    private suspend fun readCache(channel: ByteReadChannel): CachedResponseData {
        val url = channel.readUTF8Line()!!
        val status = HttpStatusCode(channel.readInt(), channel.readUTF8Line()!!)
        val version = HttpProtocolVersion.parse(channel.readUTF8Line()!!)
        val headersCount = channel.readInt()
        val headers = HeadersBuilder()
        for (j in 0 until headersCount) {
            val key = channel.readUTF8Line()!!
            val value = channel.readUTF8Line()!!
            headers.append(key, value)
        }
        val requestTime = GMTDate(channel.readLong())
        val responseTime = GMTDate(channel.readLong())
        val expirationTime = GMTDate(channel.readLong())
        val varyKeysCount = channel.readInt()
        val varyKeys = buildMap {
            for (j in 0 until varyKeysCount) {
                val key = channel.readUTF8Line()!!
                val value = channel.readUTF8Line()!!
                put(key, value)
            }
        }
        val bodyCount = channel.readInt()
        val body = ByteArray(bodyCount)
        channel.readFully(body)
        return CachedResponseData(
            url = Url(url),
            statusCode = status,
            requestTime = requestTime,
            responseTime = responseTime,
            version = version,
            expires = expirationTime,
            headers = headers.build(),
            varyKeys = varyKeys,
            body = body
        )
    }
}
