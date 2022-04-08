package com.github.panpf.sketch.request

import androidx.annotation.ColorInt

private const val SVG_BACKGROUND_COLOR_KEY = "sketch#svgBackgroundColor"

fun LoadRequest.Builder.svgBackgroundColor(@ColorInt color: Int) = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

fun DisplayRequest.Builder.svgBackgroundColor(@ColorInt color: Int) = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

fun ImageOptions.Builder.svgBackgroundColor(@ColorInt color: Int) = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

val ImageRequest.svgBackgroundColor: Int?
    get() = parameters?.value<Int>(SVG_BACKGROUND_COLOR_KEY)

val ImageOptions.svgBackgroundColor: Int?
    get() = parameters?.value<Int>(SVG_BACKGROUND_COLOR_KEY)