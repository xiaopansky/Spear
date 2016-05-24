/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.download;

import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import me.xiaopan.sketch.util.SketchUtils;

public class HttpUrlConnectionImageDownloader implements ImageDownloader {
    private static final String NAME = "HttpUrlConnectionImageDownloader";

    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int maxRetryCount = DEFAULT_MAX_RETRY_COUNT;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private String userAgent = DEFAULT_USER_AGENT;

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public HttpUrlConnectionImageDownloader setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
        return this;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public HttpUrlConnectionImageDownloader setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public HttpUrlConnectionImageDownloader setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public HttpUrlConnectionImageDownloader setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public boolean canRetry(Throwable throwable) {
        return throwable instanceof SocketTimeoutException || throwable instanceof InterruptedIOException;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME)
                .append("(")
                .append("maxRetryCount").append("=").append(maxRetryCount)
                .append(",")
                .append("connectTimeout").append("=").append(connectTimeout)
                .append(",")
                .append("readTimeout").append("=").append(readTimeout)
                .append(",")
                .append("userAgent").append("=").append(userAgent)
                .append(")");
    }

    @Override
    public ImageHttpResponse getHttpResponse(String uri) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setRequestProperty("User-Agent", userAgent);

        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            connection.setRequestProperty("http.keepAlive", "false");
        }

        processRequest(uri, connection);

        return new HttpUrlConnectionResponse(connection);
    }

    @SuppressWarnings("WeakerAccess")
    protected void processRequest(@SuppressWarnings("UnusedParameters") String uri,
                                  @SuppressWarnings("UnusedParameters") HttpURLConnection connection){

    }

    private static class HttpUrlConnectionResponse implements ImageHttpResponse {
        private HttpURLConnection connection;

        HttpUrlConnectionResponse(HttpURLConnection connection) {
            this.connection = connection;
        }

        @Override
        public int getResponseCode() throws IOException {
            return connection.getResponseCode();
        }

        @Override
        public String getResponseMessage() throws IOException {
            return connection.getResponseMessage();
        }

        @Override
        public long getContentLength() {
            return connection.getHeaderFieldInt("Content-Length", -1);
        }

        @Override
        public String getResponseHeadersString() {
            Map<String, List<String>> headers = connection.getHeaderFields();
            if (headers == null || headers.size() == 0) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                if (stringBuilder.length() != 1) {
                    stringBuilder.append(", ");
                }

                stringBuilder.append("{");

                stringBuilder.append(entry.getKey());

                stringBuilder.append(":");

                List<String> values = entry.getValue();
                if (values.size() == 0) {
                    stringBuilder.append("");
                } else if (values.size() == 1) {
                    stringBuilder.append(values.get(0));
                } else {
                    stringBuilder.append(values.toString());
                }

                stringBuilder.append("}");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        @Override
        public InputStream getContent() throws IOException {
            return connection.getInputStream();
        }

        @Override
        public void releaseConnection() {
            try {
                SketchUtils.close(getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
