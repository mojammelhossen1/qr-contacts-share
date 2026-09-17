# Project ProGuard rules
-keep class com.google.zxing.qrcode.** { *; }
-keep class com.google.zxing.BarcodeFormat { *; }
-keep class com.google.zxing.EncodeHintType { *; }
-keep class com.google.zxing.WriterException { *; }
-keep class com.google.zxing.Writer { *; }
-keep class com.google.zxing.common.BitMatrix { *; }
-dontwarn com.google.zxing.**
