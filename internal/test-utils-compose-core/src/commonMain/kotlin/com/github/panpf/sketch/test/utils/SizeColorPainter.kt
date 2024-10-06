package com.github.panpf.sketch.test.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.EquitablePainter

/**
 * Wrap a ColorPainter into a Painter with equality
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testAsEquitable
 */
fun SizeColorPainter.asEquitable(): EquitablePainter =
    EquitablePainter(painter = this, equalityKey = this)

class SizeColorPainter(val color: Color, val size: Size = Size.Unspecified) : Painter() {

    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null

    /**
     * Drawing a color does not have an intrinsic size, return [Size.Unspecified] here
     */
    override val intrinsicSize: Size = size

    override fun DrawScope.onDraw() {
        drawRect(color = color, alpha = alpha, colorFilter = colorFilter)
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SizeColorPainter
        if (color != other.color) return false
        if (size != other.size) return false
        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

    override fun toString(): String {
        return "SizeColorPainter(color=$color, size=$size)"
    }
}