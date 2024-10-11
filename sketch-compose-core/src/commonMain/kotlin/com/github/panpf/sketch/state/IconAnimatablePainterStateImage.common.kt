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

package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.IconAnimatablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.request.ImageRequest

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatablePainterStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize) {
    IconAnimatablePainterStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize) {
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatablePainterStateImage(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
): IconAnimatablePainterStateImage = remember(icon, background) {
    IconAnimatablePainterStateImage(
        icon = icon,
        background = background,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background) {
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, iconSize) {
    IconAnimatablePainterStateImage(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
): IconAnimatablePainterStateImage = remember(icon) {
    IconAnimatablePainterStateImage(
        icon = icon,
        background = null,
        iconSize = null,
        iconTint = null
    )
}

/**
 * StateImage implemented by IconAnimatablePainter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest
 */
@Stable
data class IconAnimatablePainterStateImage(
    val icon: EquitablePainter,
    val background: EquitablePainter? = null,
    val iconSize: Size? = null,
    val iconTint: Color? = null,
) : StateImage {

    override val key: String = "IconAnimatablePainterStateImage(" +
            "icon=${icon.key}," +
            "background=${background?.key}," +
            "iconSize=$iconSize," +
            "iconTint=${iconTint?.value}" +
            ")"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return IconAnimatablePainter(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        ).asImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as IconAnimatablePainterStateImage
        if (icon != other.icon) return false
        if (background != other.background) return false
        if (iconSize != other.iconSize) return false
        if (iconTint != other.iconTint) return false
        return true
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (background?.hashCode() ?: 0)
        // Because iconSize are value classes, they will be replaced by long.
        // Long will lose precision when converting hashCode, causing the hashCode generated by different srcOffset and srcSize to be the same.
        result = 31 * result + (iconSize?.toString()?.hashCode() ?: 0)
        result = 31 * result + (iconTint?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "IconAnimatablePainterStateImage(" +
            "icon=$icon, " +
            "background=$background, " +
            "iconSize=$iconSize, " +
            "iconTint=${iconTint?.value}" +
            ")"
}