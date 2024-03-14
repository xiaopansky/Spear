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
package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult

class EngineDecodeInterceptor : DecodeInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 100

    @WorkerThread
    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        val request = chain.request
        val components = chain.sketch.components
        val fetchResult = chain.fetchResult
            ?: kotlin.runCatching { components.newFetcherOrThrow(request) }
                .let { it.getOrNull() ?: return Result.failure(it.exceptionOrNull()!!) }
                .fetch()
                .let { it.getOrNull() ?: return Result.failure(it.exceptionOrNull()!!) }
        val decoder = kotlin.runCatching {
            components.newDecoderOrThrow(chain.requestContext, fetchResult)
        }.let { it.getOrNull() ?: return Result.failure(it.exceptionOrNull()!!) }
        return decoder.decode()
    }

    override fun toString(): String = "EngineDecodeInterceptor(sortWeight=$sortWeight)"

    @Suppress("RedundantOverride")
    override fun equals(other: Any?): Boolean {
        // If you add construction parameters to this class, you need to change it here
        return super.equals(other)
    }

    @Suppress("RedundantOverride")
    override fun hashCode(): Int {
        // If you add construction parameters to this class, you need to change it here
        return super.hashCode()
    }
}