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
package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult

class TestDecodeInterceptor2 : DecodeInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 0

    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        throw UnsupportedOperationException()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is TestDecodeInterceptor2
    }

    override fun hashCode(): Int {
        return this@TestDecodeInterceptor2::class.hashCode()
    }

    override fun toString(): String {
        return "Test2DecodeInterceptor(sortWeight=$sortWeight)"
    }
}