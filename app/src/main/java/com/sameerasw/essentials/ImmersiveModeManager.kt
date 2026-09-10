package com.sameerasw.essentials.utils

import android.util.Log
import com.sameerasw.essentials.shizuku.ShizukuProcessHelper

object ImmersiveModeManager {
    private const val TAG = "ImmersiveModeManager"
    private const val KEY = "policy_control"

    private fun runShizukuCommand(command: String): String {
        return try {
            ShizukuProcessHelper.runCommand(command)?.trim() ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Immersive command execution dropped", e)
            ""
        }
    }

    /**
     * Captures and filters out packages actively hidden inside the immersive config.
     */
    fun getImmersiveApps(): List<String> {
        val currentSetting = runShizukuCommand("settings get secure $KEY")
        if (currentSetting.isEmpty() || currentSetting == "null" || !currentSetting.startsWith("immersive.full=")) {
            return emptyList()
        }
        return currentSetting.removePrefix("immersive.full=").split(",")
    }

    /**
     * Appends or pulls a target package name from the core immersive policy string.
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
        
        runShizukuCommand(command)
        return true
    }
}
