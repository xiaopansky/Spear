package com.github.panpf.sketch.compose.state

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.state.ColorFetcher
import com.github.panpf.sketch.state.ColorStateImage
import com.github.panpf.sketch.state.IntColor


@Composable
fun rememberColorStateImage(colorFetcher: ColorFetcher): ColorStateImage =
    remember(colorFetcher) { ColorStateImage(colorFetcher) }

@Composable
fun rememberColorStateImage(intColor: IntColor): ColorStateImage =
    remember(intColor) { ColorStateImage(intColor) }

@Composable
fun rememberColorStateImage(@ColorRes colorRes: Int): ColorStateImage =
    remember(colorRes) { ColorStateImage(colorRes) }