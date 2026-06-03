package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String = "",
    val nameAr: String = "",
    val nameEn: String = "",
    val iconName: String = "", // e.g. "build", "school", "medical_services"
    val isPinned: Boolean = false,
    val order: Int = 0
)

@Serializable
data class Subcategory(
    val id: String = "",
    val categoryId: String = "",
    val nameAr: String = "",
    val nameEn: String = ""
)

@Serializable
data class Provider(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val categoryId: String = "",
    val subcategoryId: String = "",
    val personalPhotoUrl: String = "",
    val workspacePhotoUrl: String = "",
    val city: String = "",
    val neighborhood: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isPinned: Boolean = false,
    val viewsCount: Int = 0,
    val responseTimeMs: Long = 300000, // 5 mins default
    val verificationDocUrl: String = "",
    val pointsRedeemOption: Int = 50 // Points needed for a discount
)

@Serializable
data class Review(
    val id: String = "",
    val providerId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class LoyaltyPoints(
    val id: String = "",
    val userId: String = "",
    val points: Int = 0,
    val reasonAr: String = "",
    val reasonEn: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ChatRoom(
    val id: String = "",
    val userEmail: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val isMuted: Boolean = false
)

@Serializable
data class ChatMessage(
    val id: String = "",
    val roomId: String = "",
    val senderId: String = "", // "user" or "provider"
    val senderName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class Invoice(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val userEmail: String = "",
    val amount: Double = 0.0,
    val serviceDetails: String = "",
    val status: String = "Pending", // Pending, Paid, Unpaid
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class PromotionBanner(
    val id: String = "",
    val imageUrl: String = "",
    val redirectLink: String = "",
    val durationSeconds: Int = 5,
    val size: String = "Medium", // Small, Medium, Large
    val type: String = "Standard", // Special, Standard, Regular
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class VerificationDocument(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val documentType: String = "Commercial Register",
    val fileUrl: String = "",
    val status: String = "Pending", // Pending, Approved, Rejected
    val submissionDate: Long = System.currentTimeMillis()
)

@Serializable
data class Appointment(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val userEmail: String = "",
    val dateTimeAr: String = "",
    val dateTimeEn: String = "",
    val timestamp: Long = 0L,
    val status: String = "Scheduled", // Scheduled, Completed, Cancelled
    val reminderSent: Boolean = false
)

@Serializable
data class Moderator(
    val id: String = "",
    val email: String = "",
    val passwordPlain: String = "", // Simple plain text for mock demo/admin update
    val isBlocked: Boolean = false
)

@Serializable
data class AdminActivityLog(
    val id: String = "",
    val modEmail: String = "",
    val actionAr: String = "",
    val actionEn: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AppSettings(
    val id: String = "global_settings",
    val appTheme: String = "Cosmic Slate", // Cosmic Slate, Charcoal Gold, Royal Emerald
    val primaryColorHex: String = "#3B82F6",
    val welcomeText: String = "مرحباً بكم في تطبيق دليلي",
    val welcomeTextEn: String = "Welcome to Dalili App",
    val welcomeTextSize: Float = 22f, // sp
    val welcomeTextColorHex: String = "#FFFFFF",
    val welcomeBgUrl: String = "",
    // Top Bar Customizer
    val showRefreshIcon: Boolean = true,
    val showLanguageIcon: Boolean = true,
    val showThemeToggleIcon: Boolean = true,
    val topBarTitleAr: String = "دليلي",
    val topBarTitleEn: String = "Dalili",
    val refreshIconTitleAr: String = "تحديث",
    val refreshIconTitleEn: String = "Sync",
    val languageIconTitleAr: String = "لغة",
    val languageIconTitleEn: String = "Lang",
    // Radius Search
    val maxSearchRadiusKm: Double = 50.0,
    val defaultSearchRadiusKm: Double = 10.0,
    // FAQ / Manual Search
    val isFaqEnabled: Boolean = true,
    
    // Smart Assistant floating button configuration
    val showAssistant: Boolean = true,
    val assistantIconName: String = "SupportAgent", // SupportAgent, Chat, Help, Support
    val assistantPosition: String = "BottomRight", // BottomRight, BottomLeft, TopRight, TopLeft
    
    // WAM777 Footer configuration
    val showFooter: Boolean = true,
    val footerText: String = "WAM777",
    val footerSize: Float = 14f, // sp
    val footerPosition: String = "Bottom" // Bottom, Top
)

@Serializable
data class SectionVisit(
    val id: String = "",
    val userEmail: String = "",
    val categoryId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class FaqItem(
    val id: String = "",
    val questionAr: String = "",
    val answerAr: String = "",
    val questionEn: String = "",
    val answerEn: String = "",
    val order: Int = 0
)

// LOCAL ROOM ENTITIES FOR OFFLINE CACHING
@Entity(tableName = "cached_categories")
data class CachedCategory(
    @PrimaryKey val id: String,
    val nameAr: String,
    val nameEn: String,
    val iconName: String,
    val isPinned: Boolean,
    val order: Int
)

@Entity(tableName = "cached_providers")
data class CachedProvider(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val categoryId: String,
    val subcategoryId: String,
    val personalPhotoUrl: String,
    val workspacePhotoUrl: String,
    val city: String,
    val neighborhood: String,
    val latitude: Double,
    val longitude: Double,
    val isVerified: Boolean,
    val isPremium: Boolean,
    val rating: Float,
    val reviewCount: Int,
    val isPinned: Boolean,
    val viewsCount: Int,
    val responseTimeMs: Long,
    val pointsRedeemOption: Int
)
