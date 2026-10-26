package com.example

import com.example.data.RootController
import com.example.model.GamingTheme
import com.example.model.TweakSettings
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testRootControllerStatus() = runBlocking {
        val status = RootController.checkRootAccess()
        assertNotNull(status)
        // Check simulated root toggle
        RootController.toggleSimulatedRoot(true)
        assertTrue(RootController.rootState.value.isAccessGranted)
        val result = RootController.executeKernelCommand("echo 3 > /proc/sys/vm/drop_caches")
        assertTrue(result.contains("drop_caches"))
    }

    @Test
    fun testTweakSettingsDefaults() {
        val tweaks = TweakSettings()
        assertTrue(tweaks.cpuGpuOverclockEnabled)
        assertEquals("performance", tweaks.cpuGovernor)
        assertTrue(tweaks.touchResponseOverclock)
        assertEquals(480, tweaks.touchSamplingRateHz)
        assertTrue(tweaks.cedsiiAiLatencyEngine)
        assertTrue(tweaks.gyroZeroDriftStabilizer)
        assertTrue(tweaks.antiStutterVSyncLock)
        assertTrue(tweaks.floatingGamingOrbEnabled)
    }

    @Test
    fun testGamingThemes() {
        assertEquals(4, GamingTheme.values().size)
        assertEquals("Cyber Neon", GamingTheme.CYBER_NEON.displayName)
    }
}
