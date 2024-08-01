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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest

class TestAssetFetcherFactory : Fetcher.Factory {

    override fun create(sketch: Sketch, request: ImageRequest): Fetcher? {
        val uri = request.uri
        if (AssetUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)
            && uri.authority?.takeIf { it.isNotEmpty() } == null
            && "test_asset".equals(uri.pathSegments.firstOrNull(), ignoreCase = true)) {
            val fileName = uri.pathSegments.drop(1).joinToString("/")
            return AssetUriFetcher(sketch, request, fileName)
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is TestAssetFetcherFactory
    }

    override fun hashCode(): Int {
        return this@TestAssetFetcherFactory::class.hashCode()
    }

    override fun toString(): String {
        return "TestAssetFetcher"
    }
}