/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.viewability

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.request.ImageResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Set to enable the data source identification function
 */
fun ViewAbilityContainer.showDataFromLogo(sizeDp: Float = DataFromLogoAbility.DEFAULT_SIZE_DP) {
    removeDataFromLogo()
    addViewAbility(DataFromLogoAbility(sizeDp))
}

/**
 * Remove the data source identification function
 */
fun ViewAbilityContainer.removeDataFromLogo() {
    viewAbilityList
        .find { it is DataFromLogoAbility }
        ?.let { removeViewAbility(it) }
}

/**
 * Returns true if data source identification feature is enabled
 */
val ViewAbilityContainer.isShowDataFromLogo: Boolean
    get() = viewAbilityList.find { it is DataFromLogoAbility } != null

/**
 * In the upper right corner of the View, a semi-transparent color block called Samsung is displayed to indicate where the image is loaded this time.
 */
class DataFromLogoAbility(
    sizeDp: Float = DEFAULT_SIZE_DP
) : ViewAbility, AttachObserver, DrawObserver, LayoutObserver, DrawableObserver {

    companion object {
        const val DEFAULT_SIZE_DP = 20f
        private const val FROM_FLAG_COLOR_MEMORY = 0x77008800   // dark green
        private const val FROM_FLAG_COLOR_MEMORY_CACHE = 0x7700FF00   // green
        private const val FROM_FLAG_COLOR_RESULT_CACHE = 0x77FFFF00 // yellow
        private const val FROM_FLAG_COLOR_LOCAL = 0x771E90FF   // dodger blue
        private const val FROM_FLAG_COLOR_DOWNLOAD_CACHE = 0x77FF8800 // dark yellow
        private const val FROM_FLAG_COLOR_NETWORK = 0x77FF0000  // red
    }

    private var path: Path = Path()
    private var coroutineScope: CoroutineScope? = null
    private val paint = Paint().apply { isAntiAlias = true }
    private val realSize = (sizeDp * Resources.getSystem().displayMetrics.density + 0.5f)

    override var host: Host? = null

    override fun onAttachedToWindow() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        this.coroutineScope = coroutineScope
        val host = host!!
        coroutineScope.launch {
            host.container.requestState.loadState.collectLatest {
                reset()
                host.view.invalidate()
            }
        }
    }

    override fun onDetachedFromWindow() {
        coroutineScope?.cancel()
    }

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        reset()
        host?.view?.invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        reset()
        host?.view?.invalidate()
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        val path = path.takeIf { !it.isEmpty } ?: return
        canvas.drawPath(path, paint)
    }

    private fun reset(): Boolean {
        // Execute first, path will remain empty if subsequent conditions are not met, and the onDraw method will not execute
        path.reset()
        val host = host ?: return false
        host.container.getDrawable() ?: return false

        val result = host.container.requestState.resultState.value
        if (result !is ImageResult.Success) return false
        when (result.dataFrom) {
            DataFrom.MEMORY_CACHE -> paint.color = FROM_FLAG_COLOR_MEMORY_CACHE
            DataFrom.MEMORY -> paint.color = FROM_FLAG_COLOR_MEMORY
            DataFrom.RESULT_CACHE -> paint.color = FROM_FLAG_COLOR_RESULT_CACHE
            DataFrom.DOWNLOAD_CACHE -> paint.color = FROM_FLAG_COLOR_DOWNLOAD_CACHE
            DataFrom.LOCAL -> paint.color = FROM_FLAG_COLOR_LOCAL
            DataFrom.NETWORK -> paint.color = FROM_FLAG_COLOR_NETWORK
        }

        val view = host.view
        val viewWidth = host.view.width
        path.apply {
            moveTo(
                viewWidth - view.paddingRight - realSize,
                view.paddingTop.toFloat()
            )
            lineTo(
                viewWidth - view.paddingRight.toFloat(),
                view.paddingTop.toFloat()
            )
            lineTo(
                viewWidth - view.paddingRight.toFloat(),
                view.paddingTop.toFloat() + realSize
            )
            close()
        }
        return true
    }
}