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
    val rejectionReason: String = ""
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
    val password: String = ""
)

data class AppSettings(
    val appName: String = "دليلي - Dalili",
    val primaryColorHex: String = "#6200EE",
    val secondaryColorHex: String = "#03DAC5",
    val welcomeMessage: String = "مرحباً بك في دليلي - دليل الموثوقين الموحد لجميع الخدمات المباشرة!",
    val footerText: String = "MAW 777644670",
    val supportNumber: String = "777644670",
    val supportEmail: String = "support@dalili.com",
    val adminPasswordHex: String = "maher736462"
)
