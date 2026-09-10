package com.sameerasw.essentials.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import android.util.Log

object ImmersiveModeManager {
    private const val TAG = "ImmersiveModeManager"
    private const val KEY = "policy_control"

    private fun executeShell(command: String): String? {
        return try {
            val process = Runtime.getRuntime().exec(command)
            val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            process.waitFor()
            output.trim()
        } catch (e: Exception) {
            Log.e(TAG, "Shell execution failed", e)
            null
        }
    }

    /**
     * Extracts packages actively inside the immersive setting rule.
     */
    fun getImmersiveApps(): List<String> {
        val currentSetting = executeShell("settings get secure $KEY")
        if (currentSetting.isNullOrEmpty() || currentSetting == "null" || !currentSetting.startsWith("immersive.full=")) {
            return emptyList()
        }
        return currentSetting.removePrefix("immersive.full=").split(",")
    }

    /**
     * Toggles an app inside the system-wide immersive rule configuration.
     */
    fun toggleImmersiveMode(packageName: String, enable: Boolean): Boolean {
        val currentApps = getImmersiveApps().toMutableList()
        
        if (enable && !currentApps.contains(packageName)) {
            currentApps.add(packageName)
        } else if (!enable) {
            currentApps.remove(packageName)
        }
        
        val newValue = if (currentApps.isEmpty()) "null" else "immersive.full=${currentApps.joinToString(",")}"
        
        val command = if (newValue == "null") {
            "settings delete secure $KEY"
        } else {
            "settings put secure $KEY $newValue"
        }
        
        executeShell(command)
        return true
    }
}
