# QuickQR 📱⚡

> **Zero-footprint, instant Contact to QR Code generator for Android.**  
> Hidden from your app drawer. Appears only when you need to share a contact.

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android)](https://www.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Toolkit](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Size](https://img.shields.io/badge/APK%20Size-<1MB%20(919KB)-brightgreen?style=flat)](#download--installation)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 🌟 Overview

**QuickQR** is an ultra-lightweight, utility-first Android application designed to convert contact cards into scannable QR codes in a single tap. 

Instead of cluttering your app drawer or home screen, QuickQR lives quietly as a native system share target. When you share any contact or vCard from Google Contacts, Samsung Contacts, WhatsApp, or your file manager, QuickQR opens instantly with a high-contrast QR code ready to scan with any smartphone camera.

<p align="center">
  <img src="docs/images/app_preview.png" alt="QuickQR Preview" width="340" style="border-radius: 18px; box-shadow: 0 8px 30px rgba(0,0,0,0.12);" />
</p>

---

## ✨ Features

- 🥷 **Ghost / Hidden in App Drawer**: Operates strictly via Android's native share sheet. No app drawer clutter, no unnecessary launcher icons.
- ⚡ **Instant QR Generation**: Converts vCard 2.1, 3.0, and 4.0 data into standardized, high-density QR codes instantly.
- 📦 **Ultra Lightweight (< 1 MB)**: Production release package is just **919 KB**, optimized with R8 full-mode byte-code stripping and zero bloated dependencies.
- 🔒 **100% Offline & Private**: Zero internet permissions. Your contact data never leaves your device.
- 💾 **Save to Gallery**: Export high-resolution PNG copies directly to `Pictures/QuickQR` for offline badges or business cards.
- 📤 **Share QR Directly**: Forward the generated QR image to WhatsApp, Telegram, email, or nearby share.
- 🎨 **Material 3 Interface**: Clean, dark-mode native Jetpack Compose UI with adaptive dynamic contrast.

---

## 🚀 How to Use (User Manual)

### Step 1: Open Your Contacts App
Open **Google Contacts**, your phone's built-in Contacts app, or any messaging app.

### Step 2: Choose a Contact & Tap Share
1. Select the contact you want to share.
2. Tap the **Share** button.
3. If prompted for format, select **vCard (.vcf)**.

### Step 3: Select QuickQR
On the Android system Share Sheet, select **QuickQR**.

```
[ Contacts App ] ➡️ [ Share Contact (vCard) ] ➡️ [ Select QuickQR ] ➡️ [ Instant QR Code Ready! ]
```

### Step 4: Scan or Save
- **Scan**: Point any phone's default camera at your screen. It will instantly prompt to save the contact.
- **Save Image**: Tap **Save Image** to save the QR code to your device's photo gallery under `Pictures/QuickQR`.
- **Share QR**: Tap **Share QR** to send the QR image to anyone via chat or social apps.
- **Done / Close**: Tap **Done** or the **✕** button at the top to dismiss the screen immediately.

---

## 📥 Download & Installation

### Option 1: GitHub Releases (Recommended)
Download the standalone APK directly on your phone from the [GitHub Releases](../../releases/latest) section:

👉 **[Download QuickQR.apk (919 KB)](../../releases/latest/download/QuickQR.apk)**

### Option 2: Manual Installation via ADB
```bash
adb install QuickQR.apk
```

> **Note on Permissions**:  
> QuickQR requires **no sensitive permissions** (no contacts read permission, no location, no internet). It only receives the specific vCard data that you explicitly choose to share.

---

## 🛠️ Architecture & Tech Stack

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel) with Kotlin Coroutines & StateFlow
- **QR Encoding Engine**: High-performance ZXing QR core with byte matrix rendering
- **Minification**: Android R8 ProGuard with resource shrinking enabled
- **Target SDK**: Android 14 (API 34)
- **Minimum SDK**: Android 8.0 (API 26)

---

## 🔧 Building from Source

To build QuickQR locally using Android Studio or Gradle:

```bash
# Clone the repository
git clone https://github.com/your-username/QuickQR.git
cd QuickQR

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
