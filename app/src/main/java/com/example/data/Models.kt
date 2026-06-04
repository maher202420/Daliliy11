package com.example.data

import java.io.Serializable

data class Category(
    val id: String = "",
    val nameAr: String = "",
    val nameEn: String = "",
    val imageUrl: String = "",
    val order: Int = 0
) : Serializable

data class ServiceProvider(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val address: String = "",
    val region: String = "",
    val gpsLat: Double = 0.0,
    val gpsLng: Double = 0.0,
    val personalPhoto: String = "",
    val idCard: String = "",
    val isPinned: Boolean = false,
    val isRecommended: Boolean = false,
    val isVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val rating: Float = 5.0f,
    val ratingCount: Int = 0,
    val loyaltyPoints: Int = 0,
    val isPremium: Boolean = false,
    val premiumApproved: Boolean = false,
    val registeredAt: Long = 0L
) : Serializable

data class PendingProvider(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val address: String = "",
    val region: String = "",
    val gpsLat: Double = 0.0,
    val gpsLng: Double = 0.0,
    val personalPhoto: String = "",
    val idCard: String = "",
    val rejectReason: String = "",
    val submittedAt: Long = 0L
) : Serializable

data class Review(
    val id: String = "",
    val providerId: String = "",
    val userName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val timestamp: Long = 0L
) : Serializable

data class ChatMessage(
    val id: String = "",
    val providerId: String = "",
    val userId: String = "",
    val senderName: String = "",
    val senderType: String = "guest", // admin, provider, guest, user
    val message: String = "",
    val timestamp: Long = 0L
) : Serializable

data class Banner(
    val id: String = "",
    val title: String = "",
    val type: String = "image", // image, video, text
    val contentUrl: String = "",
    val textMessage: String = "",
    val durationSeconds: Int = 5,
    val linkUrl: String = "",
    val isSponsored: Boolean = false,
    val providerId: String = ""
) : Serializable

data class AppSettings(
    val id: String = "global",
    val appNameAr: String = "دليلي",
    val appNameEn: String = "Dalili",
    val primaryColor: String = "#FFD700", // Default gold/emerald/silver/custom
    val secondaryColor: String = "#1A1A1A",
    val themeChoice: String = "gold", // silver, gold, emerald, custom
    val appLogoUrl: String = "",
    val promoFooterText: String = "MAW 777644670",
    val welcomeMessage: String = "أهلاً بك في دليلي - بوابتك لجميع الخدمات المحترفة ونظام المزامنة الذكي!",
    val welcomeMessageEn: String = "Welcome to Dalili - Your gate to all professional services!",
    val supportPhone: String = "777644670",
    val supportEmail: String = "support@dalili.com",
    val supportWhatsapp: String = "https://wa.me/967777644670",
    
    // Assistant settings
    val assistantEnabled: Boolean = true,
    val assistantAlignLeft: Boolean = false,
    val assistantSize: String = "medium", // small, medium, large
    val assistantIcon: String = "🤖",
    
    // Notifications toggles
    val fcmJoinRequests: Boolean = true,
    val fcmComplaints: Boolean = true,
    val voiceSearchEnabled: Boolean = true,
    
    // Security & Operation Modes
    val maintenanceMode: Boolean = false,
    val dataSaverMode: Boolean = false,
    val supervisor2FAEnabled: Boolean = false,
    val maxRadiusKm: Int = 50,
    
    // Cities/Regions
    val citiesList: List<String> = listOf("صنعاء", "عدن", "تعز", "حضرموت", "إب", "الحديدة"),
    
    // Customization config for headers / icons
    val topBarLayout: List<String> = listOf("home", "login", "register", "language", "sync")
) : Serializable

data class Complaint(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val text: String = "",
    val timestamp: Long = 0L
) : Serializable

data class ServiceOrder(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val providerPhone: String = "",
    val clientName: String = "",
    val clientPhone: String = "",
    val serviceDetails: String = "",
    val status: String = "completed", // pending, processing, completed
    val timestamp: Long = 0L
) : Serializable
