package com.github.panpf.sketch3

import android.content.Context
import com.github.panpf.sketch3.common.ComponentRegistry
import com.github.panpf.sketch3.common.Disposable
import com.github.panpf.sketch3.common.OneShotDisposable
import com.github.panpf.sketch3.common.cache.disk.DiskCache
import com.github.panpf.sketch3.common.cache.disk.LruDiskCache
import com.github.panpf.sketch3.common.fetch.HttpUriFetcher
import com.github.panpf.sketch3.common.http.HttpStack
import com.github.panpf.sketch3.common.http.HurlStack
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.DownloadResult
import com.github.panpf.sketch3.download.internal.DownloadExecutor
import kotlinx.coroutines.*
import java.io.File

class Sketch3 private constructor(
    context: Context,
    val diskCache: DiskCache,
    componentRegistry: ComponentRegistry,
    val httpStack: HttpStack,
) {
    val appContext = context.applicationContext
    val componentRegistry = (componentRegistry.newBuilder()).apply {
        addFetcher(HttpUriFetcher.Factory())
    }.build()
    private val downloadExecutor: DownloadExecutor = DownloadExecutor(context)

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
//          logger?.log(TAG, throwable)
            SLog.wmt("scope", throwable, "exception")
        }
    )

    init {
        if (diskCache is LruDiskCache) {
            val wrapperErrorCallback = diskCache.errorCallback
            diskCache.errorCallback =
                LruDiskCache.ErrorCallback { dir: File, throwable: Throwable ->
                    wrapperErrorCallback?.onInstallDiskCacheError(dir, throwable)
                    // todo
//                configuration.callback.onError(InstallDiskCacheException(e, cacheDir))
                }
        }
    }

    fun enqueueDownload(downloadRequest: DownloadRequest): Disposable<DownloadResult> {
        val job = scope.async {
            downloadExecutor.executeOnMain(downloadRequest)
        }
        return OneShotDisposable(job)
    }

    suspend fun executeDownload(downloadRequest: DownloadRequest): DownloadResult =
        coroutineScope {
            val job = scope.async {
                downloadExecutor.executeOnMain(downloadRequest)
            }
            return@coroutineScope job.await()
        }

    class Builder(context: Context) {

        private val appContext: Context = context.applicationContext
        private var diskCache: DiskCache? = null
        private var componentRegistry: ComponentRegistry? = null
        private var httpStack: HttpStack? = null

        fun diskCache(diskCache: DiskCache): Builder = apply {
            this.diskCache = diskCache
        }

        fun components(components: ComponentRegistry): Builder = apply {
            this.componentRegistry = components
        }

        fun components(block: ComponentRegistry.Builder.() -> Unit): Builder = apply {
            this.componentRegistry = ComponentRegistry.Builder().apply(block).build()
        }

        fun httpStack(httpStack: HttpStack): Builder = apply {
            this.httpStack = httpStack
        }

        fun build(): Sketch3 = Sketch3(
            context = appContext,
            diskCache = diskCache ?: LruDiskCache(appContext),
            componentRegistry = componentRegistry ?: ComponentRegistry.Builder().build(),
            httpStack = httpStack ?: HurlStack.Builder().build()
        )
    }
}