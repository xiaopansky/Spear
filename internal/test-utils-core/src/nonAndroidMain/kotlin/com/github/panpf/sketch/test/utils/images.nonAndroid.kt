package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.source.DataSource
import okio.buffer
import okio.use

actual fun createBitmapImage(width: Int, height: Int): BitmapImage =
    SkiaBitmap(width, height).asImage()

fun DataSource.toSkiaImage(): SkiaImage {
    val exifBytes = openSource().buffer().use { it.readByteArray() }
    return SkiaImage.makeFromEncoded(exifBytes)
}