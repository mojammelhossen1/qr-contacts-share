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
    assertEquals("QuickQR", appName)
  }

  @Test
  fun `generate QR code bitmap`() {
    val sampleVCard = "BEGIN:VCARD\r\nVERSION:3.0\r\nFN:Test User\r\nTEL:+15551234\r\nEND:VCARD"
    val bitmap = com.example.qr.QrCodeGenerator.generateRawQrBitmap(sampleVCard, sizePx = 256)
    org.junit.Assert.assertNotNull(bitmap)
    assertEquals(256, bitmap?.width)
    assertEquals(256, bitmap?.height)
  }

  @Test
  fun `viewModel defaults to CALL_NOW when phone is present`() = kotlinx.coroutines.runBlocking {
    val viewModel = com.example.ui.ContactQrViewModel()
    val sampleVCard = "BEGIN:VCARD\r\nVERSION:3.0\r\nFN:John Doe\r\nTEL:+123456789\r\nEND:VCARD"
    viewModel.processRawVCard(sampleVCard)

    // Advance Robolectric main looper and allow background coroutines to dispatch
    var state = viewModel.uiState.value
    var attempts = 0
    while (state !is com.example.ui.UiState.Success && attempts < 50) {
      org.robolectric.shadows.ShadowLooper.idleMainLooper()
      kotlinx.coroutines.delay(50)
      org.robolectric.shadows.ShadowLooper.idleMainLooper()
      state = viewModel.uiState.value
      attempts++
    }

    org.junit.Assert.assertTrue("Expected state to be Success but was $state", state is com.example.ui.UiState.Success)
    val success = state as com.example.ui.UiState.Success
    assertEquals(com.example.ui.QrMode.CALL_NOW, success.mode)

    // Switch to VCARD mode
    viewModel.selectMode(com.example.ui.QrMode.VCARD)
    attempts = 0
    while ((viewModel.uiState.value as? com.example.ui.UiState.Success)?.mode != com.example.ui.QrMode.VCARD && attempts < 50) {
      org.robolectric.shadows.ShadowLooper.idleMainLooper()
      kotlinx.coroutines.delay(50)
      org.robolectric.shadows.ShadowLooper.idleMainLooper()
      attempts++
    }
    val vcardState = viewModel.uiState.value as com.example.ui.UiState.Success
    assertEquals(com.example.ui.QrMode.VCARD, vcardState.mode)
  }
}

