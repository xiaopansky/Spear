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

package com.github.panpf.sketch.request

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor


/**
 * Use the screen size as the resize size
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testSizeWithDisplay
 */
fun ImageOptions.Builder.sizeWithDisplay(context: Context): ImageOptions.Builder =
    apply {
        val displayMetrics = context.resources.displayMetrics
        size(width = displayMetrics.widthPixels, height = displayMetrics.heightPixels)
    }


/**
 * Set Drawable placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testPlaceholder
 */
fun ImageOptions.Builder.placeholder(drawable: DrawableEqualizer): ImageOptions.Builder =
    placeholder(DrawableStateImage(drawable))

/**
 * Set Drawable res placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testPlaceholder
 */
fun ImageOptions.Builder.placeholder(@DrawableRes resId: Int): ImageOptions.Builder =
    placeholder(DrawableStateImage(resId))

/**
 * Set Color placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testPlaceholder
 */
fun ImageOptions.Builder.placeholder(color: IntColor): ImageOptions.Builder =
    placeholder(ColorDrawableStateImage(color))

/**
 * Set Color placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testPlaceholder
 */
fun ImageOptions.Builder.placeholder(color: ResColor): ImageOptions.Builder =
    placeholder(ColorDrawableStateImage(color))


/**
 * Set Drawable placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testFallback
 */
fun ImageOptions.Builder.fallback(drawable: DrawableEqualizer): ImageOptions.Builder =
    fallback(DrawableStateImage(drawable))

/**
 * Set Drawable res placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testFallback
 */
fun ImageOptions.Builder.fallback(@DrawableRes resId: Int): ImageOptions.Builder =
    fallback(DrawableStateImage(resId))

/**
 * Set Color placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testFallback
 */
fun ImageOptions.Builder.fallback(color: IntColor): ImageOptions.Builder =
    fallback(ColorDrawableStateImage(color))

/**
 * Set Color placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testFallback
 */
fun ImageOptions.Builder.fallback(color: ResColor): ImageOptions.Builder =
    fallback(ColorDrawableStateImage(color))


/**
 * Set Drawable image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testError
 */
fun ImageOptions.Builder.error(
    defaultDrawable: DrawableEqualizer,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(DrawableStateImage(defaultDrawable), configBlock)

/**
 * Set Drawable res image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testError
 */
fun ImageOptions.Builder.error(
    @DrawableRes defaultResId: Int,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(DrawableStateImage(defaultResId), configBlock)

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testError
 */
fun ImageOptions.Builder.error(
    color: IntColor,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(ColorDrawableStateImage(color), configBlock)

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testError
 */
fun ImageOptions.Builder.error(
    color: ResColor,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(ColorDrawableStateImage(color), configBlock)


/**
 * Set [Bitmap.Config] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testColorType
 */
fun ImageOptions.Builder.colorType(colorType: Bitmap.Config?): ImageOptions.Builder =
    colorType(colorType?.let { BitmapColorType(it) })


/**
 * Set preferred [Bitmap]'s [ColorSpace]
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testColorSpace
 */
@RequiresApi(Build.VERSION_CODES.O)
fun ImageOptions.Builder.colorSpace(colorSpace: ColorSpace.Named?): ImageOptions.Builder =
    this.colorSpace(colorSpace?.let { BitmapColorSpace(it) })


/**
 * From Android N (API 24), this is ignored.  The output will always be high quality.
 *
 * In [android.os.Build.VERSION_CODES.M] and below, if
 * inPreferQualityOverSpeed is set to true, the decoder will try to
 * decode the reconstructed image to a higher quality even at the
 * expense of the decoding speed. Currently the field only affects JPEG
 * decode, in the case of which a more accurate, but slightly slower,
 * IDCT method will be used instead.
 *
 * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testPreferQualityOverSpeed
 */
@Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
fun ImageOptions.Builder.preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean? = true): ImageOptions.Builder =
    apply {
        if (inPreferQualityOverSpeed == true) {
            setExtra(key = PREFER_QUALITY_OVER_SPEED_KEY, value = true.toString())
        } else {
            removeExtra(PREFER_QUALITY_OVER_SPEED_KEY)
        }
    }

/**
 * From Android N (API 24), this is ignored.  The output will always be high quality.
 *
 * In [android.os.Build.VERSION_CODES.M] and below, if
 * inPreferQualityOverSpeed is set to true, the decoder will try to
 * decode the reconstructed image to a higher quality even at the
 * expense of the decoding speed. Currently the field only affects JPEG
 * decode, in the case of which a more accurate, but slightly slower,
 * IDCT method will be used instead.
 *
 * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageOptionsAndroidTest.testPreferQualityOverSpeed
 */
@Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
val ImageOptions.preferQualityOverSpeed: Boolean?
    get() = extras?.value<String>(PREFER_QUALITY_OVER_SPEED_KEY)?.toBoolean()