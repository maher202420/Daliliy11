package com.example.data

import java.io.Serializable

// AppSettings contains editable configurations
data class AppSettings(
    var appNameAr: String = "دليلي",
    var appNameEn: String = "Dalili",
    var promoFooterText: String = "MAW 777644670",
    var supportPhone: String = "777644670",
    var supportEmail: String = "support@dalili.com",
    var supportWhatsapp: String = "777644670",
    var primaryColor: String = "#1B5E20", // Emerald Green
    var secondaryColor: String = "#FFC700", // Gold Accent
    var themeChoice: String = "emerald", // emerald, dark, light, cosmic
    var assistantEnabled: Boolean = true,
    var assistantIconUrl: String = "", // empty means default robotic emoji
    var assistantSize: String = "medium", // small, medium, large
    var maxRadiusDefault: Float = 30f,
    var isMaintenanceMode: Boolean = false,
    var maintenanceMessage: String = "التطبيق في وضع الصيانة حالياً لخدمتكم بشكل أفضل.",
    var layoutFormat: String = "standard",
    var topBarIconOrder: String = "home,login,register,lang",
    var isChatEnabled: Boolean = true,
    var chatIconSize: Int = 28, // Default 50% smaller than 56.dp
    var chatIconColor: String = "#0288D1", // default blue
    var chatVisibility: String = "visible", // visible, hidden, deleted
    var chatDisabledMessage: String = "نعتذر، خدمة المحادثة الفورية معطلة حالياً بطلب من الإدارة.",
    var globalTextSize: Int = 16,
    var globalTextColor: String = "#FFFFFF",
    var globalFontFamily: String = "Cairo",
    var logoUrl: String = "", 
    var welcomeMessage: String = "مرحباً بكم في دليلي - دليل الخدمات الشامل في اليمن 🇾🇪",
    var voiceSearchEnabled: Boolean = true,
    var maxSearchRadius: Float = 50f,
    var isDataSavingMode: Boolean = false,
    var registrationTerms: String = "1. الالتزام بالمهنية والأمانة.\n2. إدخال بيانات هوية صحيحة.\n3. الحفاظ على جودة وسرعة تقديم الخدمة للعملاء.",
    var appSharingLink: String = "https://dalili.com/share",
    var aboutCoverUrl: String = "",
    var aboutCoverText: String = "🏢",
    var aboutCoverType: String = "text"
) : Serializable

// ServiceProvider represents professionals on our main list
data class ServiceProvider(
    var id: String = "",
    var name: String = "",
    var phone: String = "",
    var categoryId: String = "",
    var categoryName: String = "",
    var subCategoryId: String = "",
    var subCategoryName: String = "",
    var region: String = "",
    var address: String = "",
    var personalPhoto: String = "",
    var identityPhoto: String = "",
    var isVerified: Boolean = false,
    var isPinned: Boolean = false, // Shows at first of categories
    var isRecommended: Boolean = false, // Shows in top section
    var isPremium: Boolean = false, // Has monthly subscription
    var isBlocked: Boolean = false,
    var rating: Float = 5.0f,
    var ratingCount: Int = 1,
    var latitude: Double = 15.3694, //Default Sana'a
    var longitude: Double = 44.1910,
    var subscriptionExpiry: Long = 0L, // Expiry timestamp
    var subscriptionStatus: String = "none", // none, pending_approval, active
    var inspectionCost: String = "" // service inspection cost by technician
) : Serializable

// Category representation
data class Category(
    var id: String = "",
    var nameAr: String = "",
    var nameEn: String = "",
    var imageUrl: String = "",
    var displayOrder: Int = 0
) : Serializable

// SubCategory representation
data class SubCategory(
    var id: String = "",
    var categoryId: String = "",
    var nameAr: String = "",
    var nameEn: String = ""
) : Serializable

// Banners for top main sliders
data class Banner(
    var id: String = "",
    var textMessage: String = "",
    var type: String = "text", // text, image, video
    var contentUrl: String = "",
    var redirectionUrl: String = "",
    var durationSeconds: Int = 5,
    var isSponsored: Boolean = false,
    var sizeChoice: String = "medium", // small, medium, large
    var expiryTimestamp: Long = 0L
) : Serializable

// Complaints/Reports sent on provider profiles
data class Complaint(
    var id: String = "",
    var providerId: String = "",
    var providerName: String = "",
    var reporterName: String = "زائر",
    var reporterPhone: String = "",
    var reasonText: String = "",
    var timestamp: Long = System.currentTimeMillis()
) : Serializable

// Real-time Chat message
data class ChatMessage(
    var id: String = "",
    var senderId: String = "",
    var senderName: String = "",
    var receiverId: String = "", // empty means broadcast or Admin chat
    var receiverName: String = "",
    var text: String = "",
    var timestamp: Long = System.currentTimeMillis()
) : Serializable

// Device whitelist for secure admin gate
data class WhitelistedDevice(
    var deviceId: String = "",
    var deviceLabel: String = "",
    var authorizedBy: String = "",
    var isAllowed: Boolean = true
) : Serializable

// Admin / Supervisor accounts
data class AdminUser(
    var id: String = "",
    var username: String = "",
    var password: String = "",
    var role: String = "admin" // admin, manager, owner
) : Serializable

// Activity activityLogs/notifs for real-time manager updates
data class ActivityLog(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "security", // security, subscription, reports, registration
    var timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false
) : Serializable

// Service order tracking (for user dashboard)
data class ServiceOrder(
    var id: String = "",
    var userId: String = "",
    var providerId: String = "",
    var providerName: String = "",
    var categoryName: String = "",
    var orderDate: Long = System.currentTimeMillis(),
    var status: String = "completed" // pending, completed
) : Serializable

data class City(
    var id: String = "",
    var nameAr: String = "",
    var nameEn: String = ""
) : Serializable

