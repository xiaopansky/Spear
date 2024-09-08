/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.core.android.test.util

import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.produceFingerPrint
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.circleCropped
import com.github.panpf.sketch.util.getBytesPerPixel
import com.github.panpf.sketch.util.mask
import com.github.panpf.sketch.util.rotated
import com.github.panpf.sketch.util.roundedCornered
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toInfoString
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class AndroidBitmapsTest {

    @Test
    fun testAllocationByteCountCompat() {
        assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).allocationByteCountCompat
        )

        assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).allocationByteCountCompat
        )

        assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.O) 0 else 110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .allocationByteCountCompat
        )
    }

    @Test
    fun testConfigOrNull() {
        // TODO test
    }

    @Test
    fun testIsImmutable() {
        // TODO test
    }

    @Test
    fun testSafeConfig() {
        assertEquals(
            Bitmap.Config.ARGB_8888,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).safeConfig
        )

        assertEquals(
            Bitmap.Config.RGB_565,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).safeConfig
        )

        // Unable to create Bitmap with null config
    }

    @Test
    fun testGetMutableCopy() {
        // TODO test
    }

    @Test
    fun testToInfoString() {
        assertEquals(
            "AndroidBitmap(width=110, height=210, config=ARGB_8888)",
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).toInfoString()
        )

        assertEquals(
            "AndroidBitmap(width=210, height=110, config=RGB_565)",
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565).toInfoString()
        )

        // TODO Unable to create Bitmap with null config
    }

    @Test
    fun testToShortInfoString() {
        assertEquals(
            "AndroidBitmap(110x210,ARGB_8888)",
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).toShortInfoString()
        )

        assertEquals(
            "AndroidBitmap(210x110,RGB_565)",
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565).toShortInfoString()
        )

        // TODO Unable to create Bitmap with null config
    }

    @Test
    fun testToLogString() {
        // TODO test
    }

    @Test
    fun testBackgrounded() {
        // TODO test
    }

    @Test
    fun testBlur() {
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(expected = Size(1291, 1936), actual = size)
        }

        sourceBitmap.copy(Bitmap.Config.ARGB_8888, true).apply { blur(15) }.apply {
            assertEquals(sourceBitmap.toShortInfoString(), this.toShortInfoString())
            assertNotEquals(sourceBitmap.corners(), this.corners())
            assertNotSame(sourceBitmap, this)
        }

        val scaledBitmap = sourceBitmap.scaled(0.5f)
        val scaledBitmapCorners = scaledBitmap.corners()
        scaledBitmap.apply { blur(15) }.apply {
            assertSame(scaledBitmap, this)
            assertNotEquals(scaledBitmapCorners, this.corners())
        }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testCircleCropped() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val startCropBitmapFinger: String
        val startCropBitmapCorners: List<Int>
        val startCropBitmap = sourceBitmap.circleCropped(Scale.START_CROP).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1291,ARGB_8888)",
                actual = toShortInfoString()
            )
            startCropBitmapFinger = produceFingerPrint(this)
            startCropBitmapCorners = corners()
        }

        val centerCropBitmapFinger: String
        val centerCropBitmapCorners: List<Int>
        val centerCropBitmap = sourceBitmap.circleCropped(Scale.CENTER_CROP).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1291,ARGB_8888)",
                actual = toShortInfoString()
            )
            centerCropBitmapFinger = produceFingerPrint(this)
            centerCropBitmapCorners = corners()
        }

        val endCropBitmapFinger: String
        val endCropBitmapCorners: List<Int>
        val endCropBitmap = sourceBitmap.circleCropped(Scale.END_CROP).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1291,ARGB_8888)",
                actual = toShortInfoString()
            )
            endCropBitmapFinger = produceFingerPrint(this)
            endCropBitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = startCropBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = centerCropBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = endCropBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = startCropBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = centerCropBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = endCropBitmapCorners)
        assertEquals(expected = startCropBitmapCorners, actual = centerCropBitmapCorners)
        assertEquals(expected = startCropBitmapCorners, actual = endCropBitmapCorners)
        assertEquals(expected = centerCropBitmapCorners, actual = endCropBitmapCorners)

        assertNotEquals(illegal = sourceBitmapFinger, actual = startCropBitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = centerCropBitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = endCropBitmapFinger)
        assertNotEquals(illegal = startCropBitmapFinger, actual = centerCropBitmapFinger)
        assertNotEquals(illegal = startCropBitmapFinger, actual = endCropBitmapFinger)
        assertNotEquals(illegal = centerCropBitmapFinger, actual = endCropBitmapFinger)
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testMask() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val redMaskBitmapFinger: String
        val redMaskBitmapCorners: List<Int>
        val redMaskBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
            .apply { mask(TestColor.withA(TestColor.RED, a = 100)) }.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = toShortInfoString()
                )
                redMaskBitmapFinger = produceFingerPrint(this)
                redMaskBitmapCorners = corners()
            }

        val greenMaskBitmapFinger: String
        val greenMaskBitmapCorners: List<Int>
        val greenMaskBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
            .apply { mask(TestColor.withA(TestColor.GREEN, a = 100)) }.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = toShortInfoString()
                )
                greenMaskBitmapFinger = produceFingerPrint(this)
                greenMaskBitmapCorners = corners()
            }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = redMaskBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = greenMaskBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = redMaskBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = greenMaskBitmapCorners)
        assertNotEquals(illegal = redMaskBitmapCorners, actual = greenMaskBitmapCorners)

        // Fingerprints ignore color, so it's all the same
        assertEquals(expected = sourceBitmapFinger, actual = redMaskBitmapFinger)
        assertEquals(expected = sourceBitmapFinger, actual = greenMaskBitmapFinger)
        assertEquals(expected = redMaskBitmapFinger, actual = greenMaskBitmapFinger)
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testRoundedCornered() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val smallRoundedCorneredBitmapFinger: String
        val smallRoundedCorneredBitmapCorners: List<Int>
        val smallRoundedCorneredBitmap =
            sourceBitmap.roundedCornered(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))
                .apply {
                    assertEquals(
                        expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                        actual = toShortInfoString()
                    )
                    smallRoundedCorneredBitmapFinger = produceFingerPrint(this)
                    smallRoundedCorneredBitmapCorners = corners()
                }

        val bigRoundedCorneredBitmapFinger: String
        val bigRoundedCorneredBitmapCorners: List<Int>
        val bigRoundedCorneredBitmap =
            sourceBitmap.roundedCornered(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f))
                .apply {
                    assertEquals(
                        expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                        actual = toShortInfoString()
                    )
                    bigRoundedCorneredBitmapFinger = produceFingerPrint(this)
                    bigRoundedCorneredBitmapCorners = corners()
                }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = smallRoundedCorneredBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = bigRoundedCorneredBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = smallRoundedCorneredBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = bigRoundedCorneredBitmapCorners)
        assertEquals(
            expected = smallRoundedCorneredBitmapCorners,
            actual = bigRoundedCorneredBitmapCorners
        )

        // TODO It seems like it shouldn't be the same here
        assertEquals(expected = sourceBitmapFinger, actual = smallRoundedCorneredBitmapFinger)
        assertEquals(expected = sourceBitmapFinger, actual = bigRoundedCorneredBitmapFinger)
        assertEquals(
            expected = smallRoundedCorneredBitmapFinger,
            actual = bigRoundedCorneredBitmapFinger
        )
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testRotated() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val rotate90BitmapFinger: String
        val rotate90BitmapCorners: List<Int>
        val rotate90Bitmap = sourceBitmap.rotated(90).apply {
            assertEquals(
                expected = "AndroidBitmap(1936x1291,ARGB_8888)",
                actual = toShortInfoString()
            )
            rotate90BitmapFinger = produceFingerPrint(this)
            rotate90BitmapCorners = corners()
        }

        val rotate180BitmapFinger: String
        val rotate180BitmapCorners: List<Int>
        val rotate180Bitmap = sourceBitmap.rotated(180).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                actual = toShortInfoString()
            )
            rotate180BitmapFinger = produceFingerPrint(this)
            rotate180BitmapCorners = corners()
        }

        val rotate270BitmapFinger: String
        val rotate270BitmapCorners: List<Int>
        val rotate270Bitmap = sourceBitmap.rotated(270).apply {
            assertEquals(
                expected = "AndroidBitmap(1936x1291,ARGB_8888)",
                actual = toShortInfoString()
            )
            rotate270BitmapFinger = produceFingerPrint(this)
            rotate270BitmapCorners = corners()
        }

        val rotate360BitmapFinger: String
        val rotate360BitmapCorners: List<Int>
        val rotate360Bitmap = sourceBitmap.rotated(360).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                actual = toShortInfoString()
            )
            rotate360BitmapFinger = produceFingerPrint(this)
            rotate360BitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = rotate90BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = rotate180BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = rotate270BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = rotate360BitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = rotate90BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = rotate180BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = rotate270BitmapCorners)
        assertEquals(expected = sourceBitmapCorners, actual = rotate360BitmapCorners)
        assertNotEquals(illegal = rotate90BitmapCorners, actual = rotate180BitmapCorners)
        assertNotEquals(illegal = rotate90BitmapCorners, actual = rotate270BitmapCorners)
        assertNotEquals(illegal = rotate90BitmapCorners, actual = rotate360BitmapCorners)
        assertNotEquals(illegal = rotate180BitmapCorners, actual = rotate270BitmapCorners)
        assertNotEquals(illegal = rotate180BitmapCorners, actual = rotate360BitmapCorners)
        assertNotEquals(illegal = rotate270BitmapCorners, actual = rotate360BitmapCorners)

        assertNotEquals(illegal = sourceBitmapFinger, actual = rotate90BitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = rotate180BitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = rotate270BitmapFinger)
        assertEquals(expected = sourceBitmapFinger, actual = rotate360BitmapFinger)
        assertNotEquals(illegal = rotate90BitmapFinger, actual = rotate180BitmapFinger)
        assertNotEquals(illegal = rotate90BitmapFinger, actual = rotate270BitmapFinger)
        assertNotEquals(illegal = rotate90BitmapFinger, actual = rotate360BitmapFinger)
        assertNotEquals(illegal = rotate180BitmapFinger, actual = rotate270BitmapFinger)
        assertNotEquals(illegal = rotate180BitmapFinger, actual = rotate360BitmapFinger)
        assertNotEquals(illegal = rotate270BitmapFinger, actual = rotate360BitmapFinger)
    }

    @Test
    fun testMapping() {
        // TODO test
    }

    @Test
    fun testScaled() {
        val bitmap = ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
            assertEquals("AndroidBitmap(1291x1936,ARGB_8888)", toShortInfoString())
        }
        bitmap.scaled(1.5f).apply {
            assertEquals("AndroidBitmap(1937x2904,ARGB_8888)", toShortInfoString())
        }
        bitmap.scaled(0.5f).apply {
            assertEquals("AndroidBitmap(646x968,ARGB_8888)", toShortInfoString())
        }
    }

    @Test
    fun testIsHardware() {
        // TODO test
    }

    @Test
    fun testGetBytesPerPixel() {
        assertEquals(4, Bitmap.Config.ARGB_8888.getBytesPerPixel())
        @Suppress("DEPRECATION")
        assertEquals(2, Bitmap.Config.ARGB_4444.getBytesPerPixel())
        assertEquals(1, Bitmap.Config.ALPHA_8.getBytesPerPixel())
        assertEquals(2, Bitmap.Config.RGB_565.getBytesPerPixel())
        assertEquals(4, null.getBytesPerPixel())
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            assertEquals(8, Bitmap.Config.RGBA_F16.getBytesPerPixel())
            assertEquals(4, Bitmap.Config.HARDWARE.getBytesPerPixel())
        }
    }

    @Test
    fun testCalculateBitmapByteCount() {
        // TODO test
    }
}