package com.github.panpf.sketch.decode

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import java.io.Closeable

interface DrawableDecoder : Closeable {

    suspend fun decode(): DrawableDecodeResult

    fun interface Factory {

        fun create(
            sketch: Sketch,
            request: DisplayRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult,
        ): DrawableDecoder?
    }
}