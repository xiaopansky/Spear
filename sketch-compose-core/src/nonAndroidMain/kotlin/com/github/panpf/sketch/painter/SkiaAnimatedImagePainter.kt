package com.github.panpf.sketch.painter

import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.github.panpf.sketch.ComposeBitmap
import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Color
import kotlin.math.roundToInt
import kotlin.time.measureTime

class SkiaAnimatedImagePainter constructor(
    private val animatedImage: SkiaAnimatedImage,
    private val srcOffset: IntOffset = IntOffset.Zero,
    private val srcSize: IntSize = IntSize(animatedImage.width, animatedImage.height),
    private val filterQuality: FilterQuality = FilterQuality.Low,
) : Painter(), AnimatablePainter, RememberObserver {

    /*
     * Why do you need to remember to count?
     *
     * Because when RememberObserver is passed as a parameter of the Composable function, the onRemembered method
     * will be called when the Composable function is executed for the first time, causing it to be remembered multiple times.
     */
    private var rememberedCount = 0
    private val codec = animatedImage.codec
    private var coroutineScope: CoroutineScope? = null
    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null
    private var invalidateTick by mutableIntStateOf(0)
    private var composeBitmap: ComposeBitmap? = null
    private var animatedPlayer = AnimatedPlayer(
        codec = codec,
        cacheDecodeTimeoutFrame = animatedImage.cacheDecodeTimeoutFrame,
        repeatCount = animatedImage.repeatCount ?: codec.repetitionCount
    ) {
        composeBitmap = it
        invalidateSelf()
    }

    override val intrinsicSize: Size = srcSize.toSize()

    init {
        validateSize(srcOffset = srcOffset, srcSize = srcSize)
    }

    override fun DrawScope.onDraw() {
        invalidateTick // Invalidate the scope when invalidateTick changes.
        val composeBitmap = composeBitmap
        if (composeBitmap != null) {
            val dstSize = IntSize(
                this@onDraw.size.width.roundToInt(),
                this@onDraw.size.height.roundToInt()
            )
            drawImage(
                image = composeBitmap,
                srcOffset = srcOffset,
                srcSize = srcSize,
                dstOffset = IntOffset.Zero,
                dstSize = dstSize,
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality
            )
        }
    }

    private fun startAnimation() {
        coroutineScope ?: return
        if (animatedPlayer.running) return
        animatedPlayer.start(coroutineScope!!)
        animatedImage.animationStartCallback?.invoke()
    }

    private fun stopAnimation() {
        coroutineScope ?: return
        if (!animatedPlayer.running) return
        animatedPlayer.stop()
        animatedImage.animationEndCallback?.invoke()
    }

    /**
     * Note: Do not actively call its onRemembered method because this will destroy the rememberedCount count.
     */
    override fun onRemembered() {
        rememberedCount++
        if (rememberedCount != 1) return
        onFirstRemembered()
    }

    private fun onFirstRemembered() {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        startAnimation()
    }

    override fun onAbandoned() = onForgotten()
    override fun onForgotten() {
        if (rememberedCount <= 0) return
        rememberedCount--
        if (rememberedCount != 0) return
        onLastRemembered()
    }

    private fun onLastRemembered() {
        stopAnimation()
        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun invalidateSelf() {
        if (invalidateTick == Int.MAX_VALUE) {
            invalidateTick = 0
        } else {
            invalidateTick++
        }
    }

    private fun validateSize(srcOffset: IntOffset, srcSize: IntSize): IntSize {
        require(
            srcOffset.x >= 0 &&
                    srcOffset.y >= 0 &&
                    srcSize.width >= 0 &&
                    srcSize.height >= 0 &&
                    srcSize.width <= codec.width &&
                    srcSize.height <= codec.height
        )
        return srcSize
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun start() {
        if (rememberedCount > 0) {
            startAnimation()
        }
    }

    override fun stop() {
        if (rememberedCount > 0) {
            stopAnimation()
        }
    }

    override fun isRunning(): Boolean = animatedPlayer.running

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SkiaAnimatedImagePainter) return false
        if (codec != other.codec) return false
        if (srcOffset != other.srcOffset) return false
        if (srcSize != other.srcSize) return false
        if (filterQuality != other.filterQuality) return false
        return true
    }

    override fun hashCode(): Int {
        var result = codec.hashCode()
        result = 31 * result + srcOffset.hashCode()
        result = 31 * result + srcSize.hashCode()
        result = 31 * result + filterQuality.hashCode()
        return result
    }

    override fun toString(): String {
        return "SkiaAnimatedImagePainter(codec=$codec, srcOffset=$srcOffset, srcSize=$srcSize, " +
                "filterQuality=$filterQuality)"
    }

    class AnimatedPlayer(
        private val codec: Codec,
        private val repeatCount: Int,
        private val cacheDecodeTimeoutFrame: Boolean,
        private val onFrame: (ComposeBitmap) -> Unit,
    ) {

        private val frameInfos = codec.framesInfo
        private var frameCaches: MutableMap<Int, SkiaBitmap>? = null
        private val nextFrameChannel = Channel<Frame>()
        private val renderChannel = Channel<Frame>()
        private val decodeChannel = Channel<Frame>()
        private var nextFrameJob: Job? = null
        private var renderJob: Job? = null
        private var decodeJob: Job? = null
        private var repeatIndex = 0

        private var currentFrame: Frame? = null

        val running: Boolean
            get() = renderJob?.isActive == true || decodeJob?.isActive == true || nextFrameJob?.isActive == true

        fun start(coroutineScope: CoroutineScope) {
            if (running) {
                return
            }
            if (codec.frameCount <= 0) {
                val blackBitmap = SkiaBitmap().apply {
                    allocPixels(imageInfo)
                    erase(Color.BLACK)
                }
                onFrame(blackBitmap.asComposeImageBitmap())
                return
            }

            repeatIndex = 0
            renderJob = coroutineScope.launch(Dispatchers.Main) {
                for (newFrame in renderChannel) {
                    val lastFrame = currentFrame
                    currentFrame = newFrame
                    onFrame(newFrame.bitmap.composeBitmap)

                    // Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
                    if ((repeatCount < 0 || repeatIndex <= repeatCount)) {
                        if (newFrame.index == codec.frameCount - 1) {
                            repeatIndex++
                        }

                        /*
                         * Why use [nextFrameChannel] instead of sending the next frame directly in [renderChannel]?
                         * Because you have to wait for the onFrame callback to complete before decoding the next frame,
                         * otherwise the old bitmap may be overwritten by decoding before it has been replaced.
                         */
                        val nextItem = nextFrame(current = newFrame, last = lastFrame)
                        nextFrameChannel.send(nextItem)

                        val fameDuration = frameDuration(newFrame.index)
                        delay(fameDuration)
                    }
                }
            }
            decodeJob = coroutineScope.launch(ioCoroutineDispatcher()) {
                for (frame in decodeChannel) {
                    frame.bitmap.bitmap.erase(Color.TRANSPARENT)

                    val cacheBitmap = frameCaches?.get(frame.index)
                    if (cacheBitmap != null) {
                        frame.bitmap.bitmap.installPixels(cacheBitmap.readPixels())
                    } else {
                        val decodeElapsedTime = measureTime {
                            try {
                                codec.readPixels(frame.bitmap.bitmap, frame.index)
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }

                        /*
                         * The closer Codec is to the last frame when decoding animations,
                         * the longer the decoding time will be.
                         * This will cause the animation playback speed to become slower and slower,
                         * so here the frames whose decoding time exceeds the duration of the previous frame are cached.
                         */
                        if (cacheDecodeTimeoutFrame) {
                            val lastFrameIndex = if (frame.index > 0)
                                frame.index - 1 else codec.frameCount - 1
                            val lastFrameDuration = frameDuration(lastFrameIndex)
                            val needCache =
                                decodeElapsedTime.inWholeMilliseconds > lastFrameDuration
                            if (needCache) {
                                val byteArray = frame.bitmap.bitmap.readPixels()
                                val bitmap = SkiaBitmap(codec.imageInfo)
                                bitmap.installPixels(byteArray)
                                val frameCaches =
                                    frameCaches ?: mutableMapOf<Int, SkiaBitmap>().apply {
                                        this@AnimatedPlayer.frameCaches = this
                                    }
                                frameCaches[frame.index] = bitmap
                            }
                        }
                    }

                    renderChannel.send(frame)
                }
            }
            nextFrameJob = coroutineScope.launch(Dispatchers.Main) {
                decodeChannel.send(nextFrame(current = currentFrame, last = null))

                for (nextFrame in nextFrameChannel) {
                    decodeChannel.send(nextFrame)
                }
            }
        }

        fun stop() {
            if (!running) {
                return
            }
            nextFrameJob?.cancel()
            renderJob?.cancel()
            decodeJob?.cancel()
            frameCaches?.clear()
        }

        private fun nextFrame(current: Frame?, last: Frame?): Frame {
            val nextFrameIndex = if (current != null) {
                (current.index + 1) % codec.frameCount
            } else {
                0
            }
            return last?.copy(index = nextFrameIndex)
                ?: Frame(
                    index = nextFrameIndex,
                    bitmap = FrameBitmap(SkiaBitmap(codec.imageInfo))
                )
        }

        private fun frameDuration(index: Int): Long {
            return if (index >= 0 && index < frameInfos.size) {
                frameInfos[index].duration.toLong()
            } else {
                50
            }
        }

        private class FrameBitmap(val bitmap: SkiaBitmap) {
            val composeBitmap: ComposeBitmap = bitmap.asComposeImageBitmap()
        }

        private data class Frame(val index: Int, val bitmap: FrameBitmap)
    }
}