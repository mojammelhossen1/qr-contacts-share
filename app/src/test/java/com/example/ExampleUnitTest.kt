package com.example

import com.example.qr.QrCodeGenerator
import com.example.vcard.VCardParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testVCardParser_standardVCard() {
    val rawVCard = """
      BEGIN:VCARD
      VERSION:3.0
      FN:Alex Mercer
      N:Mercer;Alex;;;
      ORG:Gentek Labs
      TITLE:Chief Research Scientist
      TEL;TYPE=CELL:+1 555-0199
      EMAIL:alex.mercer@gentek.org
      NOTE:Emergency contact
      END:VCARD
    """.trimIndent()

    val details = VCardParser.parse(rawVCard)
    assertNotNull(details)
    assertEquals("Alex Mercer", details?.fullName)
    assertEquals(listOf("+1 555-0199"), details?.phoneNumbers)
    assertEquals(listOf("alex.mercer@gentek.org"), details?.emails)
    assertEquals("Gentek Labs", details?.organization)
    assertEquals("Chief Research Scientist", details?.title)
    assertEquals("Emergency contact", details?.note)
    assertTrue(details!!.qrPayload.contains("BEGIN:VCARD"))
    assertTrue(details.qrPayload.contains("FN:Alex Mercer"))
  }

  @Test
  fun testVCardParser_plainTextFallback() {
    val plainText = """
      Jane Smith
      jane@smith.io
      +1 415-555-2671
    """.trimIndent()

    val details = VCardParser.parse(plainText)
    assertNotNull(details)
    assertEquals("Jane Smith", details?.fullName)
    assertTrue(details?.emails?.contains("jane@smith.io") == true)
    assertTrue(details?.phoneNumbers?.any { it.contains("415-555-2671") } == true)
  }
}


