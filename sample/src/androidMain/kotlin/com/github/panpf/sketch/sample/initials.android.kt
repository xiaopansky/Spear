package com.github.panpf.sketch.sample

import android.os.Build
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportAnimatedGif
import com.github.panpf.sketch.decode.supportAnimatedHeif
import com.github.panpf.sketch.decode.supportAnimatedWebp
import com.github.panpf.sketch.decode.supportApkIcon
import com.github.panpf.sketch.decode.supportFFmpegVideoFrame
import com.github.panpf.sketch.decode.supportKoralGif
import com.github.panpf.sketch.decode.supportMovieGif
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.fetch.supportAppIcon
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.http.KtorStack
import com.github.panpf.sketch.http.OkHttpStack

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    val appSettings = context.appSettings
    val httpStack = when (appSettings.httpClient.value) {
        "Ktor" -> KtorStack()
        "OkHttp" -> OkHttpStack.Builder().build()
        "HttpURLConnection" -> HurlStack.Builder().build()
        else -> throw IllegalArgumentException("Unknown httpClient: ${appSettings.httpClient.value}")
    }
    httpStack(httpStack)
    addComponents {
        supportAppIcon()
        supportApkIcon()

        // video
        when (appSettings.videoFrameDecoder.value) {
            "FFmpeg" -> supportFFmpegVideoFrame()
            "AndroidBuiltIn" -> supportVideoFrame()
            else -> throw IllegalArgumentException("Unknown videoFrameDecoder: ${appSettings.videoFrameDecoder.value}")
        }

        // gif
        when (appSettings.gifDecoder.value) {
            "KoralGif" -> supportKoralGif()
            "Movie" -> supportMovieGif()
            "Movie+ImageDecoder" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) supportAnimatedGif() else supportMovieGif()
            else -> throw IllegalArgumentException("Unknown animatedDecoder: ${appSettings.gifDecoder.value}")
        }

        // webp animated
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            supportAnimatedWebp()
        }

        // heif animated
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            supportAnimatedHeif()
        }
    }
}