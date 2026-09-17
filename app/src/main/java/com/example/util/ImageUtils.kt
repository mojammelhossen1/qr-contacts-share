package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.vcard.ContactDetails
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    /**
     * Saves the QR code bitmap into the device's Pictures/ContactQR collection using MediaStore.
     */
    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        contactName: String
    ): Uri? {
        val sanitizedName = contactName
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
            .take(30)
            .ifBlank { "Contact" }
        val filename = "QR_${sanitizedName}_${System.currentTimeMillis()}.png"

        val resolver = context.contentResolver
        val imageUriCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QuickQR")

                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(imageUriCollection, contentValues) ?: return null

        return try {
            resolver.openOutputStream(uri)?.use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    throw IllegalStateException("Failed to compress bitmap")
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                resolver.delete(uri, null, null)
            } catch (_: Exception) {}
            null
        }
    }

    /**
     * Shares the QR code bitmap and contact details via native system share menu.
     */
    fun shareQrBitmap(
        context: Context,
        bitmap: Bitmap,
        contact: ContactDetails
    ): Boolean {
        return try {
            val cacheImagesDir = File(context.cacheDir, "images")
            if (!cacheImagesDir.exists()) {
                cacheImagesDir.mkdirs()
            }
            val imageFile = File(cacheImagesDir, "contact_qr_share.png")
            FileOutputStream(imageFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            val authority = "${context.packageName}.fileprovider"
            val contentUri = FileProvider.getUriForFile(context, authority, imageFile)

            val shareSubject = "QR Contact: ${contact.fullName}"
            val shareTextBuilder = StringBuilder()
            shareTextBuilder.append("Contact QR Code for ${contact.fullName}\n")
            if (contact.phoneNumbers.isNotEmpty()) {
                shareTextBuilder.append("Phone: ${contact.phoneNumbers.joinToString(", ")}\n")
            }
            if (contact.emails.isNotEmpty()) {
                shareTextBuilder.append("Email: ${contact.emails.joinToString(", ")}\n")
            }
            if (!contact.organization.isNullOrBlank()) {
                shareTextBuilder.append("Company: ${contact.organization}\n")
            }
            shareTextBuilder.append("\nScan the attached QR code with any camera or scanner app to import directly to contacts.")

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                putExtra(Intent.EXTRA_TEXT, shareTextBuilder.toString())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Contact QR Code")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
