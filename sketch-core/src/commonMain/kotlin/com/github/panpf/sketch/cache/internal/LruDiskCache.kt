/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UnnecessaryVariable")

package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.DiskCache.Editor
import com.github.panpf.sketch.cache.DiskCache.Snapshot
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.intMerged
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import okio.ByteString.Companion.encodeUtf8
import okio.FileSystem
import okio.Path
import java.util.WeakHashMap

expect fun checkDiskCacheDirectory(context: PlatformContext, directory: Path): Path

/**
 * A disk cache that manages the cache according to a least-used rule
 */
@Suppress("FoldInitializerAndIfToElvis")
class LruDiskCache constructor(
    context: PlatformContext,
    override val fileSystem: FileSystem,
    override val maxSize: Long,
    directory: Path,
    /* Version, used to delete the old cache, update this value when you want to actively delete the old cache */
    val appVersion: Int,
    /* Controlled by sketch, sketch fixes the wrong disk cache generated by the old version and will modify this version to update the cache */
    val internalVersion: Int,
) : DiskCache {

    companion object {
        private const val MODULE = "LruDiskCache"
        private const val ENTRY_DATA = 0
        private const val ENTRY_METADATA = 1

        @JvmStatic
        private val editLockLock = Any()
    }

    override val directory: Path by lazy { checkDiskCacheDirectory(context, directory) }

    private val keyMapperCache = KeyMapperCache { it.encodeUtf8().sha256().hex() }
    private val editLockMap: MutableMap<String, Mutex> = WeakHashMap()
    private val cache: DiskLruCache by lazy {
        val unionVersion = intMerged(appVersion, internalVersion)
        DiskLruCache(
            fileSystem = fileSystem,
            directory = this@LruDiskCache.directory,
            cleanupDispatcher = ioCoroutineDispatcher(),
            maxSize = maxSize,
            appVersion = unionVersion,
            valueCount = 2,  // data and metadata
            checkValueCount = 1,    // metadata not required
        )
    }

    override var logger: Logger? = null

    override val size: Long get() = cache.size()

    override fun openSnapshot(key: String): Snapshot? {
        val encodedKey = keyMapperCache.mapKey(key)
        val snapshot = cache[encodedKey]
        if (snapshot == null) return null   // for debug
        return MySnapshot(snapshot)
    }

    override fun openEditor(key: String): Editor? {
        val encodedKey = keyMapperCache.mapKey(key)
        val editor = cache.edit(encodedKey)
        if (editor == null) return null   // for debug
        return MyEditor(editor = editor)
    }

    override fun remove(key: String): Boolean {
        val encodedKey = keyMapperCache.mapKey(key)
        val removed = cache.remove(encodedKey)   // for debug
        return removed
    }

    override fun clear() {
        val oldSize = size
        runCatching {
            cache.evictAll()
        }.onFailure {
            it.printStackTrace()
        }
        logger?.d(MODULE) {
            "clear. cleared ${oldSize.formatFileSize()}"
        }
    }

    override fun editLock(key: String): Mutex = synchronized(editLockLock) {
        val encodedKey = keyMapperCache.mapKey(key)
        editLockMap[encodedKey] ?: Mutex().apply {
            this@LruDiskCache.editLockMap[encodedKey] = this
        }
    }

    /**
     * It can still be used after closing, and will reopen a new DiskLruCache
     */
    override fun close() {
        runCatching {
            cache.close()
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun toString(): String = buildString {
        append("$MODULE(")
        append("maxSize=${maxSize.formatFileSize()},")
        append("appVersion=${appVersion},")
        append("internalVersion=${internalVersion},")
        append("directory='${directory}")
        append("')")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LruDiskCache
        if (maxSize != other.maxSize) return false
        if (directory != other.directory) return false
        if (appVersion != other.appVersion) return false
        if (internalVersion != other.internalVersion) return false
        return true
    }

    override fun hashCode(): Int {
        var result = maxSize.hashCode()
        result = 31 * result + directory.hashCode()
        result = 31 * result + appVersion
        result = 31 * result + internalVersion
        return result
    }

    private class MySnapshot(private val snapshot: DiskLruCache.Snapshot) : Snapshot {

        override val data: Path = snapshot.file(ENTRY_DATA)

        override val metadata: Path = snapshot.file(ENTRY_METADATA)

        override fun close() {
            snapshot.close()
        }

        override fun closeAndOpenEditor(): Editor? {
            val editor = snapshot.closeAndEdit()
            if (editor == null) return null
            return MyEditor(editor)
        }
    }

    private class MyEditor(private val editor: DiskLruCache.Editor) : Editor {

        override val data: Path = editor.file(ENTRY_DATA)

        override val metadata: Path = editor.file(ENTRY_METADATA)

        override fun commit() = editor.commit()

        override fun commitAndOpenSnapshot(): Snapshot? {
            val snapshot = editor.commitAndGet()
            if (snapshot == null) return null
            return MySnapshot(snapshot)
        }

        override fun abort() = editor.abort()
    }
}