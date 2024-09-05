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

package com.github.panpf.sketch.core.android.test.drawable

import android.R.drawable
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable1
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable2
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable3
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class AnimatableDrawableTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        AnimatableDrawable(
            TestAnimatableDrawable1(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
        )
        if (Build.VERSION.SDK_INT >= 23) {
            AnimatableDrawable(
                TestAnimatableDrawable2(
                    BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
                ),
            )
        }
        AnimatableDrawable(
            TestAnimatableDrawable3(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
        )
        assertThrow(IllegalArgumentException::class) {
            AnimatableDrawable(
                BitmapDrawable(
                    context.resources,
                    Bitmap.createBitmap(100, 100, ARGB_8888)
                ),
            )
        }
    }

    @Test
    fun testStartStopIsRunning() = runTest {
        val context = getTestContext()

        AnimatableDrawable(
            TestAnimatableDrawable1(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
        ).apply {
            val callbackAction = mutableListOf<String>()
            val callback3 = object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    callbackAction.add("onAnimationStart")
                }

                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    callbackAction.add("onAnimationEnd")
                }
            }
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback3)
            }

            assertFalse(isRunning)
            assertEquals(listOf<String>(), callbackAction)

            start()
            Thread.sleep(100)
            assertTrue(isRunning)
            assertEquals(listOf("onAnimationStart"), callbackAction)

            stop()
            Thread.sleep(100)
            assertFalse(isRunning)
            assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackAction)
        }

        if (Build.VERSION.SDK_INT >= 23) {
            AnimatableDrawable(
                TestAnimatableDrawable2(
                    BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
                )
            ).apply {
                val callbackAction = mutableListOf<String>()
                val callback3 = object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationStart(drawable: Drawable?) {
                        super.onAnimationStart(drawable)
                        callbackAction.add("onAnimationStart")
                    }

                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)
                        callbackAction.add("onAnimationEnd")
                    }
                }
                withContext(Dispatchers.Main) {
                    registerAnimationCallback(callback3)
                }

                assertFalse(isRunning)
                assertEquals(listOf<String>(), callbackAction)

                start()
                Thread.sleep(100)
                assertTrue(isRunning)
                assertEquals(listOf("onAnimationStart"), callbackAction)

                stop()
                Thread.sleep(100)
                assertFalse(isRunning)
                assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackAction)
            }
        }

        AnimatableDrawable(
            TestAnimatableDrawable3(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
        ).apply {
            val callbackAction = mutableListOf<String>()
            val callback3 = object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    callbackAction.add("onAnimationStart")
                }

                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    callbackAction.add("onAnimationEnd")
                }
            }
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback3)
            }

            assertFalse(isRunning)
            assertEquals(listOf<String>(), callbackAction)

            start()
            Thread.sleep(100)
            assertTrue(isRunning)
            assertEquals(listOf("onAnimationStart"), callbackAction)

            stop()
            Thread.sleep(100)
            assertFalse(isRunning)
            assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackAction)
        }
    }

    @Test
    fun testCallback() = runTest {
        val context = getTestContext()

        AnimatableDrawable(
            TestAnimatableDrawable1(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
        ).apply {
            val callback = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            unregisterAnimationCallback(callback)
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            clearAnimationCallbacks()
        }


        if (Build.VERSION.SDK_INT >= 23) {
            AnimatableDrawable(
                TestAnimatableDrawable2(
                    BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
                )
            ).apply {
                val callback = object : Animatable2Compat.AnimationCallback() {}
                withContext(Dispatchers.Main) {
                    registerAnimationCallback(callback)
                }
                unregisterAnimationCallback(callback)
                withContext(Dispatchers.Main) {
                    registerAnimationCallback(callback)
                }
                clearAnimationCallbacks()
            }
        }

        AnimatableDrawable(
            TestAnimatableDrawable3(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
        ).apply {
            val callback = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            unregisterAnimationCallback(callback)
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            clearAnimationCallbacks()
        }
    }

    @Test
    fun testMutate() {
        val context = getTestContext()

        AnimatableDrawable(
            TestAnimatableDrawable3(
                context.getDrawableCompat(drawable.bottom_bar)
            ),
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        AnimatableDrawable(
            TestAnimatableDrawable3(
                TestNewMutateDrawable(context.getDrawableCompat(drawable.bottom_bar))
            ),
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()

        val drawable = TestAnimatableDrawable3(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )
        val drawable1 = TestAnimatableDrawable3(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )

        val element1 = AnimatableDrawable(drawable)
        val element11 = AnimatableDrawable(drawable)
        val element2 = AnimatableDrawable(
            drawable1,
        )

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()

        val drawable = TestAnimatableDrawable3(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )

        val animatableDrawable = AnimatableDrawable(drawable)
        assertEquals(
            "AnimatableDrawable(drawable=${drawable.toLogString()})",
            animatableDrawable.toString()
        )
    }
}