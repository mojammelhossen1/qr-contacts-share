package com.example.qr

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.example.vcard.ContactDetails
import com.example.vcard.VCardParser
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

object QrCodeGenerator {

    /**
     * Generates a high-contrast, crisp QR code Bitmap from contact details.
     * If the payload is too large, it automatically falls back to an essential vCard payload.
     */
    fun generateContactQrBitmap(
        contactDetails: ContactDetails,
        sizePx: Int = 1000
    ): Pair<Bitmap?, Boolean> {
        // Attempt full clean payload first
        val firstAttempt = generateRawQrBitmap(contactDetails.qrPayload, sizePx)
        if (firstAttempt != null) {
            return Pair(firstAttempt, false)
        }

        // If too large or failed, construct a minimized vCard with only essential fields
        val condensedPayload = VCardParser.buildStandardVCard(
            name = contactDetails.fullName,
            phones = contactDetails.phoneNumbers.take(2),
            emails = contactDetails.emails.take(1),
            org = contactDetails.organization,
            title = contactDetails.title
        )

        val secondAttempt = generateRawQrBitmap(condensedPayload, sizePx)
        return Pair(secondAttempt, true)
    }

    /**
     * Encodes a string into a standard QR code matrix using ZXing Core.
     */
    fun generateRawQrBitmap(content: String, sizePx: Int = 1000): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
                put(EncodeHintType.MARGIN, 1) // 1-module margin for maximum QR size inside card
            }

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            val colorDark = AndroidColor.BLACK
            val colorLight = AndroidColor.WHITE

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) colorDark else colorLight
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
