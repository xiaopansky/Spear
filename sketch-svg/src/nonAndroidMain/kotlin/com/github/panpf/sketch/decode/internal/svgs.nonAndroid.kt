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
package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.SvgDecoder.Companion.MIME_TYPE
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.isNotEmpty
import okio.buffer
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Data
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.svg.SVGDOM
import org.jetbrains.skia.svg.SVGLength
import org.jetbrains.skia.svg.SVGLengthUnit
import org.jetbrains.skiko.toBufferedImage
import kotlin.math.min
import kotlin.math.roundToInt


actual suspend fun decodeSvg(
    requestContext: RequestContext,
    dataSource: DataSource,
    useViewBoundsAsIntrinsicSize: Boolean,
    backgroundColor: Int?,
    css: String?,
): DecodeResult {
    val bytes = dataSource.openSource().buffer().use { it.readByteArray() }
    val svg = SVGDOM(Data.makeFromBytes(bytes))

    val svgWidth: Float
    val svgHeight: Float
    val viewBox: Rect? = svg.root?.viewBox
    if (useViewBoundsAsIntrinsicSize && viewBox != null) {
        svgWidth = viewBox.width
        svgHeight = viewBox.height
    } else {
        svgWidth = svg.root?.width?.value ?: 0f
        svgHeight = svg.root?.height?.value ?: 0f
    }
    if (svgWidth <= 0f || svgHeight <= 0f) {
        throw ImageInvalidException("Invalid svg image, width or height is less than or equal to 0")
    }
    val imageInfo = ImageInfo(
        width = svgWidth.roundToInt(),
        height = svgHeight.roundToInt(),
        mimeType = MIME_TYPE,
        exifOrientation = ExifOrientation.UNDEFINED
    )

    val bitmapWidth: Int
    val bitmapHeight: Int
    val size = requestContext.size!!
    var transformedList: List<String>? = null
//    val request = requestContext.request
//        if (request.sizeResolver is DisplaySizeResolver) {
//            val imageSize = Size(imageInfo.width, imageInfo.height)
//            val precision = request.precisionDecider.get(
//                imageSize = imageSize,
//                targetSize = size,
//            )
//            val inSampleSize = calculateSampleSize(
//                imageSize = imageSize,
//                targetSize = size,
//                smallerSizeMode = precision.isSmallerSizeMode(),
//                mimeType = null
//            )
//            bitmapWidth = (svgWidth / inSampleSize).roundToInt()
//            bitmapHeight = (svgHeight / inSampleSize).roundToInt()
//            if (inSampleSize > 1) {
//                transformedList = listOf(createInSampledTransformed(inSampleSize))
//            }
//        } else {
    // TODO Rethink size calculation
    val scale: Float = if (size.isNotEmpty) {
        min(size.width / svgWidth, size.height / svgHeight)
    } else {
        1f
    }
    bitmapWidth = (svgWidth * scale).roundToInt()
    bitmapHeight = (svgHeight * scale).roundToInt()
    if (scale != 1f) {
        transformedList = listOf(createScaledTransformed(scale))
    }
//        }

    // Set the SVG's view box to enable scaling if it is not set.
    if (viewBox == null && svgWidth > 0f && svgHeight > 0f) {
        svg.root?.viewBox = Rect.makeWH(svgWidth, svgHeight)
    }

    svg.root?.width = SVGLength(
        value = 100f,
        unit = SVGLengthUnit.PERCENTAGE,
    )

    svg.root?.height = SVGLength(
        value = 100f,
        unit = SVGLengthUnit.PERCENTAGE,
    )

    svg.setContainerSize(bitmapWidth.toFloat(), bitmapHeight.toFloat())

    val bitmap = Bitmap().apply {
        allocN32Pixels(bitmapWidth, bitmapHeight)
    }
    val canvas = Canvas(bitmap)
    if (backgroundColor != null) {
        val rect = Rect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val paint = Paint().apply { color = backgroundColor }
        canvas.drawRect(rect, paint)
    }
    // TODO css
    svg.render(canvas)

    // TODO Create and use SkiaBitmapImage
    val toBufferedImage = bitmap.toBufferedImage()
//        val toBufferedImage = bitmap.asComposeImageBitmap()
    return DecodeResult(
        image = toBufferedImage.asSketchImage(),
        imageInfo = imageInfo,
        dataFrom = dataSource.dataFrom,
        transformedList = transformedList,
        extras = null
    ).appliedResize(requestContext)
}