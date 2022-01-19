package com.github.panpf.sketch.test.datasource

import android.net.Uri
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class ContentDataSourceTest {

    @Test
    fun testConstructor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val contentUri = runBlocking {
            val file = AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg")),
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
            Uri.fromFile(file)
        }
        val request = LoadRequest(contentUri.toString())
        ContentDataSource(
            sketch = sketch,
            request = request,
            contentUri = contentUri,
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(contentUri, this.contentUri)
            Assert.assertEquals(DataFrom.LOCAL, this.from)
        }
    }

    @Test
    fun testLength() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val contentUri = runBlocking {
            val file = AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg")),
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
            Uri.fromFile(file)
        }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            Assert.assertEquals(540456, length())
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri = runBlocking {
                Uri.fromFile(File("/sdcard/error.jpeg"))
            }
            ContentDataSource(
                sketch = sketch,
                request = LoadRequest(errorContentUri.toString()),
                contentUri = errorContentUri,
            ).apply {
                length()
            }
        }
    }

    @Test
    fun testNewFileDescriptor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val contentUri = runBlocking {
            val file = AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg")),
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
            Uri.fromFile(file)
        }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            Assert.assertNotNull(newFileDescriptor())
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri = runBlocking {
                Uri.fromFile(File("/sdcard/error.jpeg"))
            }
            ContentDataSource(
                sketch = sketch,
                request = LoadRequest(errorContentUri.toString()),
                contentUri = errorContentUri,
            ).apply {
                newFileDescriptor()
            }
        }
    }

    @Test
    fun testNewInputStream() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val contentUri = runBlocking {
            val file = AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg")),
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
            Uri.fromFile(file)
        }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            newInputStream().close()
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri = runBlocking {
                Uri.fromFile(File("/sdcard/error.jpeg"))
            }
            ContentDataSource(
                sketch = sketch,
                request = LoadRequest(errorContentUri.toString()),
                contentUri = errorContentUri,
            ).apply {
                newInputStream()
            }
        }
    }

    @Test
    fun testFile() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val contentUri = runBlocking {
            val file = AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg")),
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
            Uri.fromFile(file)
        }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            val file = runBlocking {
                file()
            }
            Assert.assertEquals(
                "/storage/emulated/0/Android/data/com.github.panpf.sketch.core.test/cache/sketch/7b39d374d441edcbc5674ad92c2f1f18.0",
                file.path
            )
        }

        val errorContentUri = runBlocking {
            Uri.fromFile(File("/sdcard/error.jpeg"))
        }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(errorContentUri.toString()),
            contentUri = errorContentUri,
        ).apply {
            val file = runBlocking {
                file()
            }
            Assert.assertEquals("/sdcard/error.jpeg", file.path)
        }
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val contentUri = runBlocking {
            val file = AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg")),
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
            Uri.fromFile(file)
        }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            Assert.assertEquals(
                "ContentDataSource(contentUri='file:///storage/emulated/0/Android/data/com.github.panpf.sketch.core.test/cache/sketch/7b39d374d441edcbc5674ad92c2f1f18.0')",
                toString()
            )
        }

        val errorContentUri = runBlocking {
            Uri.fromFile(File("/sdcard/error.jpeg"))
        }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(errorContentUri.toString()),
            contentUri = errorContentUri,
        ).apply {
            Assert.assertEquals(
                "ContentDataSource(contentUri='file:///sdcard/error.jpeg')",
                toString()
            )
        }
    }
}