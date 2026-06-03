package com.example.data

import androidx.compose.ui.graphics.Color

data class Category(
    val id: String = "",
    val nameAr: String = "",
    val nameEn: String = "",
    val imageUrl: String = "",
    val sortOrder: Int = 0,
    val subcategories: List<Subcategory> = emptyList()
)

data class Subcategory(
    val id: String = "",
    val nameAr: String = "",
    val nameEn: String = ""
)

data class Provider(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val categoryId: String = "",
    val subcategoryId: String = "",
    val workAddress: String = "",
    val district: String = "",
    val gpsCoordinates: String = "",
    val personalPhotoUrl: String = "",
    val idCardPhotoUrl: String = "",
    val isPinned: Boolean = false,
    val isRecommended: Boolean = false,
    val status: String = "approved", // "pending", "approved", "rejected"
    val rating: Float = 5.0f,
    val reviewCount: Int = 0,
    val rejectionReason: String = "",
    // Subscription / Premium features
    val isPremium: Boolean = false,
    val premiumExpiryTimestamp: Long = 0L
)

data class Review(
    val id: String = "",
    val providerId: String = "",
    val userName: String = "",
    val rating: Float = 5.0f,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Supervisor(
    val id: String = "",
    val username: String = "",
    val password: String = "",
    val tfaEnabled: Boolean = false,
    val tfaSecret: String = ""
)

data class Complaint(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending" // "pending", "resolved", "dismissed"
)

data class BannerAd(
    val id: String = "",
    val imageUrl: String = "",
    val targetUrl: String = "",
    val title: String = "",
    val durationDays: Int = 7,
    val sizeType: String = "medium", // "small", "medium", "large"
    val bannerType: String = "image_alert", // "image_alert", "text_alert"
    val timestamp: Long = System.currentTimeMillis()
)

data class LoyaltyAccount(
    val id: String = "",
    val userName: String = "",
    val phone: String = "",
    val points: Int = 0,
    val historyLogs: List<String> = emptyList()
)

data class CityOption(
    val id: String = "",
    val nameAr: String = "",
    val nameEn: String = ""
)

data class SubscriptionPayment(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val receiptPhotoUrl: String = "",
    val notes: String = "",
    val status: String = "pending", // "pending", "approved", "rejected"
    val timestamp: Long = System.currentTimeMillis()
)

data class AppSettings(
    val appName: String = "دليلي - Dalili",
    val primaryColorHex: String = "#CCCCCC",
    val secondaryColorHex: String = "#78909C",
    val welcomeMessage: String = "مرحباً بك في دليلي - دليل الموثوقين الموحد لجميع الخدمات المباشرة!",
    val footerText: String = "WAM777644670",
    val supportNumber: String = "777644670",
    val supportEmail: String = "support@dalili.com",
    val supportWhatsapp: String = "777644670",
    val adminPasswordHex: String = "maher736462",
    // Premium themes configs
    val themePreset: String = "cosmic_slate", // "cosmic_slate", "charcoal_gold", "royal_emerald", "custom"
    val backgroundColorHex: String = "#121824",
    val textColorPreset: String = "bright_white", // "bright_white", "light_gold", "vibrant_silver"
    val textColorHex: String = "#FFFFFF",
    // Smart assistant configs
    val smartAssistantSize: String = "medium", // "small", "medium", "large"
    val smartAssistantColorHex: String = "#6200EE",
    val smartAssistantAlignLeft: Boolean = false,
    val smartAssistantEnabled: Boolean = true,
    // Extra options
    val maintenanceMode: Boolean = false,
    val dataSaverMode: Boolean = false,
    val maxRadiusDefault: Int = 10,
    // FCM toggles
    val fcmJoinRequests: Boolean = true,
    val fcmComplaints: Boolean = true,
    // Loyalty Points multiplier configs
    val pointsPerReview: Int = 10,
    val pointsPerShare: Int = 20,
    val scaleFactor: Float = 1.0f,
    // Premium sub feature
    val isSubscriptionEnabled: Boolean = true,
    // Top bar buttons config schema string e.g. "home,login,register,about"
    val topBarConfig: String = "home,login,register"
)
