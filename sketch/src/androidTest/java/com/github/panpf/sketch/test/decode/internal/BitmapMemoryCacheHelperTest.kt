package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.widget.ImageView
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.newBitmapMemoryCacheEditor
import com.github.panpf.sketch.decode.internal.newBitmapMemoryCacheHelper
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestDepth
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapMemoryCacheHelperTest {

    @Test
    fun testNewBitmapMemoryCacheHelper() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val imageView = ImageView(context)
        val request = DisplayRequest(newAssetUri("sample.jpeg"), imageView)

        Assert.assertNotNull(
            newBitmapMemoryCacheEditor(sketch, request)
        )
        Assert.assertNotNull(
            newBitmapMemoryCacheEditor(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(ENABLED)
            })
        )
        Assert.assertNull(
            newBitmapMemoryCacheEditor(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(DISABLED)
            })
        )
        Assert.assertNotNull(
            newBitmapMemoryCacheEditor(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(READ_ONLY)
            })
        )
        Assert.assertNotNull(
            newBitmapMemoryCacheEditor(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(WRITE_ONLY)
            })
        )
    }

    @Test
    fun testRead() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val imageView = ImageView(context)
        val request = DisplayRequest(newAssetUri("sample.jpeg"), imageView)

        // Is there really no
        val helper = newBitmapMemoryCacheHelper(sketch, request)
        Assert.assertNull(helper.read())

        Assert.assertNull(
            newBitmapMemoryCacheHelper(sketch, request.newDisplayRequest {
                depth(RequestDepth.LOCAL)
            }).read()
        )

        // There are the
        val bitmapDecodeResult = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            LOCAL,
            null
        )
        helper.write(bitmapDecodeResult)
        Assert.assertNotNull(helper.read())
        Assert.assertNotNull(helper.read())

        Assert.assertNotNull(
            newBitmapMemoryCacheHelper(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(ENABLED)
            }).read()
        )
        Assert.assertNotNull(
            newBitmapMemoryCacheHelper(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(READ_ONLY)
            }).read()
        )
        Assert.assertNull(
            newBitmapMemoryCacheHelper(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(WRITE_ONLY)
            }).read()
        )
    }

    @Test
    fun testWrite() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val imageView = ImageView(context)
        val request = DisplayRequest(newAssetUri("sample.jpeg"), imageView)

        Assert.assertNull(newBitmapMemoryCacheHelper(sketch, request).read())

        val bitmapDecodeResult = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            LOCAL,
            null
        )
        Assert.assertNotNull(
            newBitmapMemoryCacheHelper(sketch, request).write(bitmapDecodeResult)
        )

        Assert.assertNotNull(newBitmapMemoryCacheHelper(sketch, request).read())

        Assert.assertNotNull(
            newBitmapMemoryCacheHelper(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(ENABLED)
            }).write(bitmapDecodeResult)
        )
        Assert.assertNull(
            newBitmapMemoryCacheHelper(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(READ_ONLY)
            }).write(bitmapDecodeResult)
        )
        Assert.assertNotNull(
            newBitmapMemoryCacheHelper(sketch, request.newDisplayRequest {
                bitmapMemoryCachePolicy(WRITE_ONLY)
            }).write(bitmapDecodeResult)
        )

        Assert.assertNotNull(newBitmapMemoryCacheHelper(sketch, request).read())
    }
}