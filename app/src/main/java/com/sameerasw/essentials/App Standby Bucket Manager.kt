package com.sameerasw.essentials.utils

import android.util.Log
import com.sameerasw.essentials.shizuku.ShizukuProcessHelper

object StandbyBucketManager {
    private const val TAG = "StandbyBucketManager"

    /**
     * Executes an ADB command via the app's internal Shizuku processor
     */
    private fun runShizukuCommand(command: String): String {
        return try {
            // Uses the fork's native Shizuku execution layer
            val result = ShizukuProcessHelper.runCommand(command)
            result?.trim() ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Failed to route command via Shizuku: $command", e)
            ""
        }
    }

    /**
     * Obtains the integer representing the active background bucket.
     * Returns a baseline safe default (10 = Active) if processing drops.
     */
    fun getBucket(packageName: String): Int {
        val output = runShizukuCommand("am get-standby-bucket $packageName")
        return output.toIntOrNull() ?: 10
    }

    /**
     * Forces the designated application package into a target standby state.
     */
    fun setBucket(packageName: String, bucketValue: Int): Boolean {
        val command = "am set-standby-bucket $packageName $bucketValue"
        runShizukuCommand(command)
        // Self-verify that the system accepted the shift
        return getBucket(packageName) == bucketValue
    }
}
