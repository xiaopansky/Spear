package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.ListenerRequest
import com.github.panpf.sketch.request.internal.LoadableRequest
import com.github.panpf.sketch.transform.Transformation

class LoadRequest private constructor(
    override val url: String,
    _depth: RequestDepth?,
    override val parameters: Parameters?,
    override val httpHeaders: Map<String, String>?,
    _diskCacheKey: String?,
    _diskCachePolicy: CachePolicy?,
    _resultDiskCacheKey: String?,
    _resultDiskCachePolicy: CachePolicy?,
    override val maxSize: MaxSize?,
    override val bitmapConfig: BitmapConfig?,
    override val colorSpace: ColorSpace?,
    override val preferQualityOverSpeed: Boolean?,
    override val resize: Resize?,
    override val transformations: List<Transformation>?,
    override val disabledBitmapPool: Boolean?,
    override val disabledCorrectExifOrientation: Boolean?,
    override val listener: Listener<LoadRequest, LoadResult>?,
    override val networkProgressListener: ProgressListener<LoadRequest>?,
) : LoadableRequest, ListenerRequest<LoadRequest, LoadResult> {

    override val depth: RequestDepth = _depth ?: NETWORK

    override val diskCacheKey: String = _diskCacheKey ?: url

    override val diskCachePolicy: CachePolicy = _diskCachePolicy ?: CachePolicy.ENABLED

    override val resultDiskCacheKey: String? by lazy {
        _resultDiskCacheKey ?: qualityKey?.let { "${url}_$it" }
    }

    override val resultDiskCachePolicy: CachePolicy = _resultDiskCachePolicy ?: CachePolicy.ENABLED

    override val qualityKey: String? by lazy {
        LoadableRequest.newQualityKey(this)
    }

    override val key: String by lazy {
        val parametersInfo = parameters?.let { "_${it.key}" } ?: ""
        val qualityKey = qualityKey?.let { "_${it}" } ?: ""
        "Load_${url}${parametersInfo})_diskCacheKey($diskCacheKey)_diskCachePolicy($diskCachePolicy)${qualityKey}"
    }

    override fun newDecodeOptionsByQualityParams(mimeType: String): BitmapFactory.Options =
        LoadableRequest.newDecodeOptionsByQualityParams(this, mimeType)

    fun toDownloadRequest(): DownloadRequest = DownloadRequest.new(url) {
        depth(depth)
        parameters(parameters)
        httpHeaders(httpHeaders)
        diskCacheKey(diskCacheKey)
        diskCachePolicy(diskCachePolicy)
        networkProgressListener?.let {
            networkProgressListener { _, totalLength, completedLength ->
                it.onUpdateProgress(this@LoadRequest, totalLength, completedLength)
            }
        }
    }

    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun new(
        configBlock: (Builder.() -> Unit)? = null
    ): LoadRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    companion object {
        fun new(
            url: String,
            configBlock: (Builder.() -> Unit)? = null
        ): LoadRequest = Builder(url).apply {
            configBlock?.invoke(this)
        }.build()

        fun newBuilder(
            url: String,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder(url).apply {
            configBlock?.invoke(this)
        }
    }

    class Builder(private val url: String) {

        private var depth: RequestDepth? = null
        private var parameters: Parameters? = null
        private var httpHeaders: Map<String, String>? = null
        private var diskCacheKey: String? = null
        private var diskCachePolicy: CachePolicy? = null
        private var resultDiskCacheKey: String? = null
        private var resultDiskCachePolicy: CachePolicy? = null
        private var maxSize: MaxSize? = null
        private var bitmapConfig: BitmapConfig? = null
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resize: Resize? = null
        private var transformations: List<Transformation>? = null
        private var disabledBitmapPool: Boolean? = null
        private var disabledCorrectExifOrientation: Boolean? = null
        private var listener: Listener<LoadRequest, LoadResult>? = null
        private var networkProgressListener: ProgressListener<LoadRequest>? = null

        internal constructor(request: LoadRequest) : this(request.url) {
            this.depth = request.depth
            this.parameters = request.parameters
            this.httpHeaders = request.httpHeaders
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
            this.resultDiskCacheKey = request.resultDiskCacheKey
            this.resultDiskCachePolicy = request.resultDiskCachePolicy
            this.maxSize = request.maxSize
            this.bitmapConfig = request.bitmapConfig
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.colorSpace = request.colorSpace
            }
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resize = request.resize
            this.transformations = request.transformations
            this.disabledBitmapPool = request.disabledBitmapPool
            this.disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
            this.listener = request.listener
            this.networkProgressListener = request.networkProgressListener
        }

        fun depth(depth: RequestDepth?): Builder = apply {
            this.depth = depth
        }

        fun parameters(parameters: Parameters?): Builder = apply {
            this.parameters = parameters
        }

        fun httpHeaders(httpHeaders: Map<String, String>?): Builder = apply {
            this.httpHeaders = httpHeaders
        }

        fun diskCacheKey(diskCacheKey: String?): Builder = apply {
            this.diskCacheKey = diskCacheKey
        }

        fun diskCachePolicy(diskCachePolicy: CachePolicy?): Builder = apply {
            this.diskCachePolicy = diskCachePolicy
        }

        fun resultDiskCacheKey(resultDiskCacheKey: String?): Builder = apply {
            this.resultDiskCacheKey = resultDiskCacheKey
        }

        fun resultDiskCachePolicy(resultDiskCachePolicy: CachePolicy?): Builder = apply {
            this.resultDiskCachePolicy = resultDiskCachePolicy
        }

        fun maxSize(maxSize: MaxSize?): Builder = apply {
            this.maxSize = maxSize
        }

        fun maxSize(width: Int, height: Int): Builder = apply {
            this.maxSize = MaxSize(width, height)
        }

        fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            this.bitmapConfig = bitmapConfig
        }

        fun bitmapConfig(bitmapConfig: Bitmap.Config?): Builder = apply {
            this.bitmapConfig = if (bitmapConfig != null) BitmapConfig(bitmapConfig) else null
        }

        fun lowQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.LOW_QUALITY
        }

        fun middenQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.MIDDEN_QUALITY
        }

        fun highQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.HIGH_QUALITY
        }

        @RequiresApi(26)
        fun colorSpace(colorSpace: ColorSpace?): Builder = apply {
            this.colorSpace = colorSpace
        }

        /**
         * From Android N (API 24), this is ignored.  The output will always be high quality.
         *
         * In {@link android.os.Build.VERSION_CODES#M} and below, if
         * inPreferQualityOverSpeed is set to true, the decoder will try to
         * decode the reconstructed image to a higher quality even at the
         * expense of the decoding speed. Currently the field only affects JPEG
         * decode, in the case of which a more accurate, but slightly slower,
         * IDCT method will be used instead.
         *
         * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
         */
        @Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
        fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean?): Builder = apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                this.preferQualityOverSpeed = inPreferQualityOverSpeed
            }
        }

        fun resize(resize: Resize?): Builder = apply {
            this.resize = resize
        }

        fun resize(
            @Px width: Int,
            @Px height: Int,
            mode: Resize.Mode = Resize.Mode.EXACTLY_SAME
        ): Builder = apply {
            this.resize = Resize(width, height, mode)
        }

        fun transformations(transformations: List<Transformation>?): Builder = apply {
            this.transformations = transformations
        }

        fun transformations(vararg transformations: Transformation): Builder = apply {
            this.transformations = transformations.toList()
        }

        fun disabledBitmapPool(disabledBitmapPool: Boolean? = true): Builder = apply {
            this.disabledBitmapPool = disabledBitmapPool
        }

        fun disabledCorrectExifOrientation(disabledCorrectExifOrientation: Boolean? = true): Builder =
            apply {
                this.disabledCorrectExifOrientation = disabledCorrectExifOrientation
            }

        fun listener(listener: Listener<LoadRequest, LoadResult>?): Builder =
            apply {
                this.listener = listener
            }

        fun networkProgressListener(networkProgressListener: ProgressListener<LoadRequest>?): Builder =
            apply {
                this.networkProgressListener = networkProgressListener
            }

        fun build(): LoadRequest = LoadRequest(
            url = url,
            _depth = depth,
            parameters = parameters,
            httpHeaders = httpHeaders,
            _diskCacheKey = diskCacheKey,
            _diskCachePolicy = diskCachePolicy,
            _resultDiskCacheKey = resultDiskCacheKey,
            _resultDiskCachePolicy = resultDiskCachePolicy,
            maxSize = maxSize,
            bitmapConfig = bitmapConfig,
            colorSpace = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) colorSpace else null,
            preferQualityOverSpeed = preferQualityOverSpeed,
            resize = resize,
            transformations = transformations,
            disabledBitmapPool = disabledBitmapPool,
            disabledCorrectExifOrientation = disabledCorrectExifOrientation,
            listener = listener,
            networkProgressListener = networkProgressListener,
        )
    }
}