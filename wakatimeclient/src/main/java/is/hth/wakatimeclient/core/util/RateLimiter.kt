/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package `is`.hth.wakatimeclient.core.util

import android.os.SystemClock
import androidx.collection.ArrayMap

import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
class RateLimiter<in KEY>(timeout: Int, timeUnit: TimeUnit) {
    private val timestamps = ArrayMap<KEY, Long>()
    private val timeout = timeUnit.toMillis(timeout.toLong())

    /**
     * Determines if the supplied [key] should be considered to have expired
     */
    @Synchronized
    fun shouldFetch(key: KEY): Boolean {
        val lastFetched = timestamps[key]
        val now = now()
        return lastFetched == null || now - lastFetched > timeout
    }

    /**
     * Marks the supplied [key] timestamped as of now for the duration
     * of the configured max timeout
     */
    @Synchronized
    fun mark(key: KEY) {
        timestamps[key] = now()
    }

    /**
     * Resets the timestamp for the supplied [key]
     */
    @Synchronized
    fun reset(key: KEY) {
        timestamps.remove(key)
    }

    private fun now() = SystemClock.uptimeMillis()
}