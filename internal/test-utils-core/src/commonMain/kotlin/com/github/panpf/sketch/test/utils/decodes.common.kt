package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.internal.DecodeHelper
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource

expect suspend fun ImageRequest.decode(
    sketch: Sketch,
    factory: Decoder.Factory? = null
): DecodeResult

expect fun createDecodeHelper(request: ImageRequest, dataSource: DataSource): DecodeHelper

fun ResourceImageFile.decode(
    colorType: BitmapColorType? = null,
    colorSpace: String? = null
): BitmapImage {
    val context = getTestContext()
    val request = ImageRequest(context, uri) {
        colorType(colorType)
        colorSpace(colorSpace)
    }
    val dataSource = toDataSource(context)
    val decoderHelper = createDecodeHelper(request, dataSource)
    return decoderHelper.decode(1) as BitmapImage
}