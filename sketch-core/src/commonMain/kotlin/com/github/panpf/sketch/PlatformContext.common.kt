/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch

/**
 * Represents a platform-specific context that acts as an interface to
 * global information about an application environment.
 *
 * @see com.github.panpf.sketch.core.android.test.PlatformContextAndroidTest.testPlatformContext
 * @see com.github.panpf.sketch.core.nonandroid.test.PlatformContextNonAndroidTest.testPlatformContext
 */
expect abstract class PlatformContext

/**
 * Check that the platform context is as expected
 *
 * @see com.github.panpf.sketch.core.android.test.PlatformContextAndroidTest.testCheckPlatformContext
 * @see com.github.panpf.sketch.core.nonandroid.test.PlatformContextNonAndroidTest.testCheckPlatformContext
 * @see
 */
expect fun checkPlatformContext(context: PlatformContext)