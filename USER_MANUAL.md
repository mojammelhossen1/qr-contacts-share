# QuickQR User Manual & Operational Guide

## 1. Introduction

**QuickQR** is an ultra-lightweight Android utility that converts contact cards into scannable QR codes instantly. It is purpose-built to operate cleanly with a **single-screen fit (no scrolling required)** and operates invisibly in your phone without filling your app drawer with extra icons.

---

## 2. Key Concepts

### Single-Screen Viewport Fit
The entire user interface is dynamically proportioned:
- **Mode Selector**: Clean tab bar at top.
- **Dynamic QR Code**: Scales gracefully within the viewport.
- **Contact Summary**: Displays key name, phone, and email information with one-tap copy.
- **Action Buttons**: Save Image, Share QR, and Done all visible at once without scrolling.

### Dual QR Modes
1. **Call Now QR (`tel:` format)**:
   - Selected automatically whenever phone numbers are present.
   - When another phone scans this QR code, it immediately opens the phone dialer with the number pre-filled and ready to call.
2. **Full Contact Card (vCard format)**:
   - One tap on the mode selector generates a full standard vCard QR code.
   - Compatible with iOS Camera, Google Lens, and all standard contact scanners for adding directly to the phone's address book.

### Why isn't QuickQR in my App Drawer?
QuickQR is engineered as a **system share target utility**. It registers itself with Android's system share sheet and file viewers rather than having a standalone launcher shortcut. This keeps your home screen tidy and ensures the tool only activates when you are actively looking to share a contact.

---

## 3. Step-by-Step Usage

### Sharing a Contact via Contacts App
1. Open your **Contacts** app (Google Contacts, Samsung Contacts, etc.).
2. Tap on any contact.
3. Tap the **Share** icon (or tap `⋮` (More) → **Share**).
4. If asked to choose a share format, select **vCard** or **File** (`.vcf`).
5. In the Android share sheet, tap **QuickQR**.
6. QuickQR opens instantly in full screen:
   - Mode selector at the top (**Call Now QR** / **Full Contact Card**).
   - High-contrast QR code centered and sized to fit.
   - Contact name, telephone numbers, and email addresses.
   - Tap any phone number or email to copy it to your clipboard.
   - Tap the chevron `∨` icon to inspect raw vCard data if needed.

### Actions Available on the Screen:
- **Point to Scan**: The other person opens their phone camera or Google Lens to immediately dial or import the contact into their address book.
- **Save Image**: Tap **Save Image** to store a high-res PNG of the QR code in `Pictures/QuickQR`.
- **Share QR**: Tap **Share QR** to forward the image directly through messaging apps.
- **Done**: Tap **Done** or the **✕** button in the top bar to finish and dismiss the screen.

---

## 4. Opening .vcf Files Directly
If you receive a contact file (`.vcf`) in WhatsApp, Telegram, or your phone's File Manager:
1. Tap on the `.vcf` file.
2. Select **Open with** → **QuickQR**.
3. The QR code will be generated immediately.

---

## 5. Security & Privacy Highlights

| Feature | Status |
|---|---|
| **Internet Access** | None (Zero network requests, zero telemetry) |
| **Address Book Access** | None (Only processes contacts you explicitly share) |
| **Analytics / Tracking** | None |
| **Storage Footprint** | Less than 1 MB (903 KB) |
| **Background Processes** | None (Shuts down immediately when closed) |
