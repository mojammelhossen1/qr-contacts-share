package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr.QrCodeGenerator
import com.example.vcard.ContactDetails
import com.example.vcard.VCardParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

enum class QrMode {
    CALL_NOW,
    VCARD
}

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(
        val contact: ContactDetails,
        val qrBitmap: Bitmap,
        val mode: QrMode = QrMode.CALL_NOW,
        val isCondensed: Boolean = false
    ) : UiState
    data class Error(val message: String) : UiState
}

class ContactQrViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Cache generated bitmaps so toggling is instant
    private var cachedContact: ContactDetails? = null
    private var cachedCallBitmap: Bitmap? = null
    private var cachedVCardBitmap: Bitmap? = null
    private var cachedVCardIsCondensed: Boolean = false

    fun processRawVCard(rawText: String) {
        if (rawText.isBlank()) {
            _uiState.value = UiState.Error("No contact data received.")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val contact = withContext(Dispatchers.Default) {
                VCardParser.parse(rawText)
            }

            if (contact == null) {
                _uiState.value = UiState.Error("Could not parse contact data.")
                return@launch
            }

            setupContact(contact)
        }
    }

    fun processUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val rawContent = withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).readText()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            if (rawContent.isNullOrBlank()) {
                _uiState.value = UiState.Error("Could not read contact file.")
                return@launch
            }

            val contact = withContext(Dispatchers.Default) {
                VCardParser.parse(rawContent)
            }

            if (contact == null) {
                _uiState.value = UiState.Error("Could not parse contact data.")
                return@launch
            }

            setupContact(contact)
        }
    }

    private suspend fun setupContact(contact: ContactDetails) {
        cachedContact = contact
        cachedCallBitmap = null
        cachedVCardBitmap = null
        cachedVCardIsCondensed = false

        val hasPhone = contact.phoneNumbers.isNotEmpty()
        val defaultMode = if (hasPhone) QrMode.CALL_NOW else QrMode.VCARD

        switchToMode(defaultMode)
    }

    fun selectMode(mode: QrMode) {
        viewModelScope.launch {
            switchToMode(mode)
        }
    }

    private suspend fun switchToMode(mode: QrMode) {
        val contact = cachedContact ?: return

        when (mode) {
            QrMode.CALL_NOW -> {
                val primaryPhone = contact.phoneNumbers.firstOrNull()
                if (primaryPhone.isNullOrBlank()) {
                    // Fall back to vCard if no phone number available
                    switchToMode(QrMode.VCARD)
                    return
                }

                var bitmap = cachedCallBitmap
                if (bitmap == null) {
                    val sanitizedPhone = primaryPhone.replace(Regex("[^0-9+]"), "")
                    val telUri = "tel:$sanitizedPhone"
                    bitmap = withContext(Dispatchers.Default) {
                        QrCodeGenerator.generateRawQrBitmap(telUri, sizePx = 1000)
                    }
                    cachedCallBitmap = bitmap
                }

                if (bitmap == null) {
                    _uiState.value = UiState.Error("Failed to encode call QR code.")
                } else {
                    _uiState.value = UiState.Success(
                        contact = contact,
                        qrBitmap = bitmap,
                        mode = QrMode.CALL_NOW,
                        isCondensed = false
                    )
                }
            }

            QrMode.VCARD -> {
                var bitmap = cachedVCardBitmap
                var isCondensed = cachedVCardIsCondensed
                if (bitmap == null) {
                    val result = withContext(Dispatchers.Default) {
                        QrCodeGenerator.generateContactQrBitmap(contact, sizePx = 1000)
                    }
                    bitmap = result.first
                    isCondensed = result.second
                    cachedVCardBitmap = bitmap
                    cachedVCardIsCondensed = isCondensed
                }

                if (bitmap == null) {
                    _uiState.value = UiState.Error("Failed to encode contact into QR code.")
                } else {
                    _uiState.value = UiState.Success(
                        contact = contact,
                        qrBitmap = bitmap,
                        mode = QrMode.VCARD,
                        isCondensed = isCondensed
                    )
                }
            }
        }
    }
}
