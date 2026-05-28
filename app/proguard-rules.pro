# Add project specific ProGuard rules here.
# Control optimization, obfuscation, and shrinking to shield against clones.

# General Obfuscation Protection
-repackageclasses 'com.example.secure.obf'
-allowaccessmodification

# Retrofit & OkHttp Protections and Keeps
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Moshi keeps for proper Reflection & Kotlins compatibility
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# Keep our models safe so Moshi can serialize/deserialize them
-keep class com.example.data.** { *; }
-keepclassmembers class com.example.data.** {
    <fields>;
    <methods>;
}

# Preserve the line-number table for structured bug telemetry but hide source file names
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SecureGuard
