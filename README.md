# QuickQR 📱⚡

> **Zero-footprint, single-screen Contact to QR Code utility for Android.**  
> Hidden from your app drawer. Appears instantly when you share a contact, with zero vertical scrolling.

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android)](https://www.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Toolkit](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Size](https://img.shields.io/badge/APK%20Size-<1MB%20(903KB)-brightgreen?style=flat)](#download--installation)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 🌟 Overview

**QuickQR** is an ultra-lightweight, utility-first Android application designed to convert contact cards into scannable QR codes in a single tap. 

Instead of cluttering your app drawer or home screen, QuickQR lives quietly as a native system share target. When you share any contact or vCard from Google Contacts, Samsung Contacts, WhatsApp, or your file manager, QuickQR opens instantly with a high-contrast QR code formatted to fit completely on a single viewport without any scrolling.

<p align="center">
  <img src="docs/images/app_preview.png" alt="QuickQR Preview" width="340" style="border-radius: 18px; box-shadow: 0 8px 30px rgba(0,0,0,0.12);" />
</p>

---

## ✨ Features

- 📱 **Single Screen Fit (Zero Scrolling)**: The entire presentation—mode selector tabs, dynamic high-density QR code, contact summary chips, and primary action buttons—is engineered to fit comfortably within the viewport with no vertical scrollbar.
- 🥷 **Ghost / Hidden in App Drawer**: Operates strictly via Android's native share sheet. No app drawer clutter, no unnecessary launcher icons.
- ⚡ **Dual QR Modes**:
  - **Call Now QR (`tel:` URI)**: Automatically selects when phone numbers are present. Scanning triggers immediate direct phone dialing.
  - **Full Contact Card (vCard)**: One-tap toggle to generate standard vCard 2.1/3.0/4.0 QR codes that add contacts directly into address books.
- 📦 **Ultra Lightweight (< 1 MB)**: Production release package is just **903 KB**, optimized with R8 full-mode byte-code stripping and zero bloated dependencies.
- 🔒 **100% Offline & Private**: Zero internet permissions. Your contact data never leaves your device.
- 💾 **Save to Gallery**: Export high-resolution PNG copies directly to `Pictures/QuickQR` for offline badges or business cards.
- 📤 **Share QR Directly**: Forward the generated QR image to WhatsApp, Telegram, email, or nearby share.
- 📋 **Quick Copy**: Tap phone numbers or email addresses on the contact card to copy them instantly to your clipboard.
- 🎨 **Material Design 3**: Clean Jetpack Compose UI with adaptive contrast in both portrait and landscape orientations.

---

## 🚀 How to Use

### Step 1: Open Your Contacts App
Open **Google Contacts**, your phone's built-in Contacts app, or any messaging app.

### Step 2: Choose a Contact & Tap Share
1. Select the contact you want to share.
2. Tap the **Share** button.
3. If prompted for format, select **vCard (.vcf)** or **File**.

### Step 3: Select QuickQR
On the Android system Share Sheet, select **QuickQR**.

```
[ Contacts App ] ➡️ [ Share Contact (vCard) ] ➡️ [ Select QuickQR ] ➡️ [ Instant Single-Screen QR ]
```

### Step 4: Scan, Switch Modes, or Export
- **Scan**: Point any phone camera or Google Lens at the screen to immediately call or save the contact.
- **Switch Mode**: Tap **Call Now QR** for direct calling or **Full Contact Card** for full address book import.
- **Save Image**: Tap **Save Image** to save the QR code to `Pictures/QuickQR`.
- **Share QR**: Tap **Share QR** to forward the QR image to anyone via chat or social apps.
- **Done / Close**: Tap **Done** or the **✕** button at the top to dismiss the screen immediately.

---

## 📥 Download & Installation

### Option 1: Standalone APK (Included in Repository)
Download the standalone APK directly from this repository:

👉 **[Download QuickQR.apk (903 KB)](./QuickQR.apk)**

Or from GitHub Releases:
👉 **[GitHub Releases](../../releases/latest)**

### Option 2: Manual Installation via ADB
```bash
adb install QuickQR.apk
```

> **Note on Permissions**:  
> QuickQR requires **no sensitive permissions** (no contacts read permission, no background service, no location, no internet). It only receives the specific vCard data that you explicitly choose to share.

---

## 🛠️ Architecture & Tech Stack

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel) with Kotlin Coroutines & StateFlow
- **QR Encoding Engine**: High-performance ZXing QR core with byte matrix rendering
- **Minification**: Android R8 ProGuard with resource shrinking enabled
- **Target SDK**: Android 16 (API 36)
- **Minimum SDK**: Android 7.0 (API 24)

---

## 🔧 Building from Source

To build QuickQR locally using Android Studio or Gradle:

```bash
# Clone the repository
git clone https://github.com/mojammelhossen1/qr-contacts-share.git
cd qr-contacts-share

# Build debug APK
gradle :app:assembleDebug

# Build release APK
gradle :app:assembleRelease
```

The compiled APK will be located at:
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🛡️ Privacy Policy

- **No Data Collection**: QuickQR does not collect, log, track, or transmit any user data.
- **No Internet Access**: The `android.permission.INTERNET` permission is completely omitted from the manifest.
- **Local Processing**: Contact details are parsed entirely in local memory and discarded when dismissed.

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.
