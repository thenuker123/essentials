package com.sameerasw.essentials.utils // Change this to your actual package path if different

import java.io.BufferedReader
import java.io.InputStreamReader
import android.util.Log

object StandbyBucketManager {
    private const val TAG = "StandbyBucketManager"

    /**
     * Executes a terminal command using the project's internal runtime processor.
     */
    private fun executeShell(command: String): List<String> {
        val output = mutableListOf<String>()
        try {
            // Replaces basic command invocation; adapts to the app's Shizuku/Root wrapper if needed
            val process = Runtime.getRuntime().exec(command)
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let { output.add(it) }
                }
            }
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Shell execution failed for command: $command", e)
        }
        return output
    }

    /**
     * Retrieves the current standby bucket integer for a package.
     * Returns 10 (Active) as a baseline default if the system execution fails.
     */
    fun getBucket(packageName: String): Int {
        val result = executeShell("am get-standby-bucket $packageName")
        return result.firstOrNull()?.trim()?.toIntOrNull() ?: 10
    }

    /**
     * Shifts an application into a target standby bucket.
     */
    fun setBucket(packageName: String, bucketValue: Int): Boolean {
        val command = "am set-standby-bucket $packageName $bucketValue"
        executeShell(command)
        // Verify if it successfully applied
        return getBucket(packageName) == bucketValue
    }
}
