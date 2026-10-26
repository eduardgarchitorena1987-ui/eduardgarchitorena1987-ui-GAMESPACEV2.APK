package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CEDSII SPACE", appName)
  }

  @Test
  fun `test gpu utilization monitor sampling`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val gpuMonitor = com.example.data.GpuUtilizationMonitor(context)
    val metrics = gpuMonitor.sampleGpuMetrics(
      isOverclocked = true,
      frameTimeMs = 8.3f,
      liveFps = 120,
      batteryTempCelsius = 35.0f
    )
    org.junit.Assert.assertNotNull(metrics)
    org.junit.Assert.assertTrue(metrics.utilizationPercent in 10..100)
    org.junit.Assert.assertTrue(metrics.frequencyMhz > 0)
    org.junit.Assert.assertTrue(metrics.history.isNotEmpty())
  }

  @Test
  fun `test app repository vault status toggle`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.AppRepository(context)
    repo.loadApps()
    val codmPkg = "com.garena.game.codm"
    val initialApp = repo.gameApps.value.find { it.packageName == codmPkg }
    org.junit.Assert.assertNotNull(initialApp)
    val initialVaultStatus = initialApp!!.isInVault

    repo.toggleVaultStatus(codmPkg)
    val updatedApp = repo.gameApps.value.find { it.packageName == codmPkg }
    assertEquals(!initialVaultStatus, updatedApp?.isInVault)
  }
}
