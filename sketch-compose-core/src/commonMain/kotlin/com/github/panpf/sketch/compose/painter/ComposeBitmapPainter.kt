package com.github.panpf.sketch.compose.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.compose.ComposeBitmap
import com.github.panpf.sketch.compose.painter.internal.SketchPainter

fun ComposeBitmap.asPainter(): Painter = ComposeBitmapPainter(this)

fun ComposeBitmap.toLogString(): String =
    "ComposeBitmap@${hashCode().toString(16)}(${width.toFloat()}x${height.toFloat()},$config)"

class ComposeBitmapPainter(val imageBitmap: ComposeBitmap) : Painter(), SketchPainter {

    override val intrinsicSize = Size(imageBitmap.width.toFloat(), imageBitmap.height.toFloat())

    override fun DrawScope.onDraw() {
        val intSize = IntSize(size.width.toInt(), size.height.toInt())
        drawImage(imageBitmap, dstSize = intSize)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComposeBitmapPainter) return false
        return imageBitmap == other.imageBitmap
    }

    override fun hashCode(): Int {
        return imageBitmap.hashCode()
    }

    override fun toString(): String {
        return "ComposeBitmapPainter(imageBitmap=${imageBitmap.toLogString()})"
    }
}