package com.example.vcard

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import java.util.regex.Pattern

data class ContactDetails(
    val fullName: String,
    val phoneNumbers: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    val organization: String? = null,
    val title: String? = null,
    val note: String? = null,
    val rawVCard: String,
    val qrPayload: String
)

object VCardParser {

    private val EMAIL_REGEX = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val PHONE_REGEX = Pattern.compile("(\\+?[0-9][0-9\\-\\s()]{6,}[0-9])")

    /**
     * Parse raw string (either standard vCard or plain text contact).
     */
    fun parse(rawText: String): ContactDetails? {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty()) return null

        return if (trimmed.contains("BEGIN:VCARD", ignoreCase = true)) {
            parseVCardString(trimmed)
        } else {
            parsePlainTextContact(trimmed)
        }
    }

    /**
     * Queries contact details from a system Contact Content URI (from PickContact contract).
     */
    fun parseFromContactUri(contentResolver: ContentResolver, contactUri: Uri): ContactDetails? {
        return try {
            var contactId: String? = null
            var displayName: String? = null

            // 1. Query contact display name and ID
            contentResolver.query(
                contactUri,
                arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                    val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    if (idIndex != -1) contactId = cursor.getString(idIndex)
                    if (nameIndex != -1) displayName = cursor.getString(nameIndex)
                }
            }

            val finalName = displayName?.takeIf { it.isNotBlank() } ?: "Contact"
            val phones = mutableListOf<String>()
            val emails = mutableListOf<String>()

            // 2. Query phones
            if (contactId != null) {
                contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(contactId),
                    null
                )?.use { cursor ->
                    val numIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (cursor.moveToNext()) {
                        if (numIndex != -1) {
                            val num = cursor.getString(numIndex)
                            if (!num.isNullOrBlank() && !phones.contains(num)) {
                                phones.add(num.trim())
                            }
                        }
                    }
                }

                // 3. Query emails
                contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                    "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                    arrayOf(contactId),
                    null
                )?.use { cursor ->
                    val emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                    while (cursor.moveToNext()) {
                        if (emailIndex != -1) {
                            val email = cursor.getString(emailIndex)
                            if (!email.isNullOrBlank() && !emails.contains(email)) {
                                emails.add(email.trim())
                            }
                        }
                    }
                }
            }

            val standardVCard = buildStandardVCard(
                name = finalName,
                phones = phones,
                emails = emails
            )

            ContactDetails(
                fullName = finalName,
                phoneNumbers = phones,
                emails = emails,
                rawVCard = standardVCard,
                qrPayload = standardVCard
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseVCardString(rawVCard: String): ContactDetails {
        // Unfold lines per RFC 2426 (remove CRLF/LF followed by space or tab)
        val unfolded = rawVCard
            .replace("\r\n ", "")
            .replace("\n ", "")
            .replace("\r\n\t", "")
            .replace("\n\t", "")

        val lines = unfolded.lines()

        var formattedName: String? = null
        var structuredName: String? = null
        val phones = mutableListOf<String>()
        val emails = mutableListOf<String>()
        var organization: String? = null
        var title: String? = null
        var note: String? = null

        // Collect lines for clean QR payload (strip bulky photos / binaries)
        val cleanQrLines = mutableListOf<String>()
        cleanQrLines.add("BEGIN:VCARD")
        cleanQrLines.add("VERSION:3.0")

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            val colonIndex = line.indexOf(':')
            if (colonIndex <= 0) continue

            val propPart = line.substring(0, colonIndex).uppercase()
            val valuePart = line.substring(colonIndex + 1).trim()
            val unescapedValue = unescapeVCard(valuePart)

            // Extract property name (before any parameters like ;TYPE=CELL)
            val propName = if (propPart.contains(';')) {
                propPart.substring(0, propPart.indexOf(';'))
            } else {
                propPart
            }

            when (propName) {
                "FN" -> {
                    formattedName = unescapedValue
                    cleanQrLines.add("FN:$valuePart")
                }
                "N" -> {
                    structuredName = parseStructuredName(unescapedValue)
                    cleanQrLines.add("N:$valuePart")
                }
                "TEL" -> {
                    if (unescapedValue.isNotBlank() && !phones.contains(unescapedValue)) {
                        phones.add(unescapedValue)
                    }
                    cleanQrLines.add(line)
                }
                "EMAIL" -> {
                    if (unescapedValue.isNotBlank() && !emails.contains(unescapedValue)) {
                        emails.add(unescapedValue)
                    }
                    cleanQrLines.add(line)
                }
                "ORG" -> {
                    organization = unescapedValue
                    cleanQrLines.add("ORG:$valuePart")
                }
                "TITLE" -> {
                    title = unescapedValue
                    cleanQrLines.add("TITLE:$valuePart")
                }
                "NOTE" -> {
                    note = unescapedValue
                    cleanQrLines.add("NOTE:$valuePart")
                }
                "URL", "ADR" -> {
                    // Include web addresses and physical addresses if present, very lightweight
                    cleanQrLines.add(line)
                }
                // Skip PHOTO, LOGO, SOUND and huge binary fields so QR matrix remains small and scannable
                "PHOTO", "LOGO", "SOUND", "KEY" -> {
                    // bulky binary skipped
                }
            }
        }
        cleanQrLines.add("END:VCARD")

        val finalName = formattedName?.takeIf { it.isNotBlank() }
            ?: structuredName?.takeIf { it.isNotBlank() }
            ?: phones.firstOrNull()
            ?: emails.firstOrNull()
            ?: "Shared Contact"

        val qrPayload = cleanQrLines.joinToString("\r\n")

        return ContactDetails(
            fullName = finalName,
            phoneNumbers = phones,
            emails = emails,
            organization = organization,
            title = title,
            note = note,
            rawVCard = rawVCard,
            qrPayload = qrPayload
        )
    }

    private fun parsePlainTextContact(plainText: String): ContactDetails {
        val lines = plainText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val phones = mutableListOf<String>()
        val emails = mutableListOf<String>()
        var nameCandidate: String? = null

        for (line in lines) {
            val emailMatcher = EMAIL_REGEX.matcher(line)
            if (emailMatcher.find()) {
                val email = emailMatcher.group()
                if (!emails.contains(email)) emails.add(email)
                continue
            }

            val phoneMatcher = PHONE_REGEX.matcher(line)
            if (phoneMatcher.find()) {
                val phone = phoneMatcher.group()
                if (!phones.contains(phone)) phones.add(phone)
                continue
            }

            if (nameCandidate == null && line.length < 50) {
                nameCandidate = line
            }
        }

        val finalName = nameCandidate ?: phones.firstOrNull() ?: "Contact"
        val standardVCard = buildStandardVCard(
            name = finalName,
            phones = phones,
            emails = emails,
            note = if (nameCandidate != null && lines.size > 1) plainText else null
        )

        return ContactDetails(
            fullName = finalName,
            phoneNumbers = phones,
            emails = emails,
            rawVCard = standardVCard,
            qrPayload = standardVCard
        )
    }

    private fun parseStructuredName(nValue: String): String {
        // N:Family;Given;Middle;Prefix;Suffix
        val parts = nValue.split(";").map { it.trim() }
        val family = parts.getOrNull(0) ?: ""
        val given = parts.getOrNull(1) ?: ""
        val middle = parts.getOrNull(2) ?: ""
        return listOf(given, middle, family).filter { it.isNotBlank() }.joinToString(" ")
    }

    fun buildStandardVCard(
        name: String,
        phones: List<String>,
        emails: List<String>,
        org: String? = null,
        title: String? = null,
        note: String? = null
    ): String {
        val sb = StringBuilder()
        sb.append("BEGIN:VCARD\r\n")
        sb.append("VERSION:3.0\r\n")
        sb.append("FN:${escapeVCard(name)}\r\n")
        sb.append("N:;${escapeVCard(name)};;;\r\n")
        for (phone in phones) {
            sb.append("TEL;TYPE=CELL:$phone\r\n")
        }
        for (email in emails) {
            sb.append("EMAIL:$email\r\n")
        }
        if (!org.isNullOrBlank()) {
            sb.append("ORG:${escapeVCard(org)}\r\n")
        }
        if (!title.isNullOrBlank()) {
            sb.append("TITLE:${escapeVCard(title)}\r\n")
        }
        if (!note.isNullOrBlank()) {
            sb.append("NOTE:${escapeVCard(note)}\r\n")
        }
        sb.append("END:VCARD\r\n")
        return sb.toString()
    }

    private fun unescapeVCard(text: String): String {
        return text
            .replace("\\n", "\n")
            .replace("\\N", "\n")
            .replace("\\,", ",")
            .replace("\\;", ";")
            .replace("\\\\", "\\")
    }

    private fun escapeVCard(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace(",", "\\,")
            .replace(";", "\\;")
            .replace("\n", "\\n")
    }
}
