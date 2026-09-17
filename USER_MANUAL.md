# QuickQR User Manual & Operational Guide

## 1. Introduction

**QuickQR** is an ultra-lightweight Android utility that converts contact cards into scannable vCard QR codes instantly. It is purpose-built to operate invisibly in your phone without filling your app drawer with extra icons.

---

## 2. Key Concepts

### Why isn't QuickQR in my App Drawer?
QuickQR is engineered as a **utility service extension**. It registers itself with Android's system share menu rather than having a standalone launcher shortcut. This keeps your home screen tidy and ensures the tool only activates when you are actively looking to share a contact.

---

## 3. Step-by-Step Usage

### Sharing a Contact via Google Contacts / Native Contacts
1. Open your **Contacts** app.
2. Tap on any contact.
3. Tap the **Share** icon (or tap `⋮` (More) → **Share**).
4. If asked to choose a share format, select **vCard** or **File** (`.vcf`).
5. In the Android share sheet, tap **QuickQR**.
6. QuickQR appears immediately displaying:
   - A high-contrast QR code optimized for all phone cameras.
   - Contact name, telephone numbers, and email addresses.
   - Expandable raw vCard text inspection.

### Actions Available on the Screen:
- **Point to Scan**: The other person opens their phone camera or Google Lens to immediately import the contact into their address book.
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
| **Internet Access** | None (Zero network requests) |
| **Address Book Access** | None (Only processes contacts you explicitly share) |
| **Analytics / Tracking** | None |
| **Storage Footprint** | Less than 1 MB (919 KB) |
