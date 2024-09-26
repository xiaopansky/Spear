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

package com.github.panpf.sketch.core.android.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.ResColorDrawableStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

@RunWith(AndroidJUnit4::class)
class ColorDrawableStateImageTest {

    @Test
    fun testIntColorDrawableStateImage() {
        assertEquals(
            expected = ColorDrawableStateImage(IntColor(TestColor.RED)),
            actual = IntColorDrawableStateImage(TestColor.RED)
        )
    }

    @Test
    fun testResColorDrawableStateImage() {
        assertEquals(
            expected = ColorDrawableStateImage(ResColor(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)),
            actual = ResColorDrawableStateImage(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
        )
    }

    @Test
    fun testColorDrawableStateImageIntColor() {
        assertEquals(
            expected = ColorDrawableStateImage(IntColor(TestColor.RED)),
            actual = ColorDrawableStateImage(IntColor(TestColor.RED))
        )
    }

    @Test
    fun testColorDrawableStateImageResColor() {
        assertEquals(
            expected = ColorDrawableStateImage(ResColor(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)),
            actual = ColorDrawableStateImage(ResColor(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated))
        )
    }

    @Test
    fun testKey() {
        IntColorDrawableStateImage(Color.RED).apply {
            assertEquals("ColorDrawableStateImage(${IntColor(Color.RED).key})", key)
        }
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        IntColorDrawableStateImage(Color.BLUE).apply {
            assertEquals(
                Color.BLUE,
                getImage(sketch, request, null)
                    .asOrThrow<DrawableImage>().drawable
                    .asOrNull<ColorDrawable>()!!.color
            )
        }

        IntColorDrawableStateImage(Color.RED).apply {
            assertEquals(
                Color.RED,
                getImage(sketch, request, null)
                    .asOrThrow<DrawableImage>().drawable
                    .asOrNull<ColorDrawable>()!!.color
            )
        }

        IntColorDrawableStateImage(Color.GREEN).apply {
            assertEquals(
                Color.GREEN,
                getImage(sketch, request, null)
                    .asOrThrow<DrawableImage>().drawable
                    .asOrNull<ColorDrawable>()!!.color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IntColorDrawableStateImage(Color.RED)
        val element11 = IntColorDrawableStateImage(Color.RED)
        val element2 = IntColorDrawableStateImage(Color.GREEN)
        val element3 = IntColorDrawableStateImage(Color.BLUE)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        IntColorDrawableStateImage(Color.RED).apply {
            assertEquals("ColorDrawableStateImage(IntColor(${Color.RED}))", toString())
        }
        IntColorDrawableStateImage(Color.GREEN).apply {
            assertEquals("ColorDrawableStateImage(IntColor(${Color.GREEN}))", toString())
        }
    }
}