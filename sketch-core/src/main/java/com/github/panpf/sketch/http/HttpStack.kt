/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.http

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DownloadRequest
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException

/**
 * 负责发送 HTTP 请求，并返回响应
 */
interface HttpStack {

    companion object {
        const val DEFAULT_READ_TIMEOUT = 7 * 1000 // 默认读取超时时间
        const val DEFAULT_CONNECT_TIMEOUT = 7 * 1000 // 默认连接超时时间
        const val DEFAULT_MAX_RETRY_COUNT = 0 // 默认最大重试次数
    }

    /**
     * 发送请求并获取响应
     *
     * @param url http url
     * @return [Response]
     */
    @Throws(IOException::class)
    fun getResponse(sketch: Sketch, request: DownloadRequest, url: String): Response

    /**
     * 是否可以重试
     */
    fun canRetry(throwable: Throwable): Boolean {
        return throwable is SocketTimeoutException
    }

    /**
     * 统一响应接口
     */
    interface Response {
        /**
         * 获取响应状态码
         *
         * @throws IOException IO
         */
        @get:Throws(IOException::class)
        val code: Int

        /**
         * 获取响应消息
         *
         * @throws IOException IO
         */
        @get:Throws(IOException::class)
        val message: String?

        /**
         * 获取内容长度
         */
        val contentLength: Long

        /**
         * 获取内容类型
         */
        val contentType: String?

        /**
         * 内容是否是分块的？
         */
        val isContentChunked: Boolean

        /**
         * 获取内容编码
         */
        val contentEncoding: String?

        /**
         * 获取响应头
         */
        fun getHeaderField(name: String): String?

        /**
         * 获取响应头并转换成 int
         */
        fun getHeaderFieldInt(name: String, defaultValue: Int): Int

        /**
         * 获取响应头并转换成 long
         */
        fun getHeaderFieldLong(name: String, defaultValue: Long): Long

        /**
         * 获取所有的响应头
         */
        val headersString: String?

        /**
         * 获取内容输入流
         *
         * @throws IOException IO
         */
        @get:Throws(IOException::class)
        val content: InputStream

        /**
         * 释放连接
         */
        fun releaseConnection()
    }
}