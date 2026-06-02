package com.example.data

data class Category(
    val id: Int? = null,
    val nameAr: String = "",
    val icon: String = "",
    val orderIndex: Int = 0,
    val createdAt: String? = null,
    val isPinned: Boolean = false
)

data class SubCategory(
    val id: Int? = null,
    val parentCategoryId: Int = 0,
    val nameAr: String = "",
    val icon: String = "",
    val orderIndex: Int = 0,
    val createdAt: String? = null
)

data class ServiceProvider(
    val id: Int? = null,
    val name: String = "",
    val phone: String = "",
    val categoryId: Int = 0,
    val subCategoryId: Int? = null,
    val rating: Double = 0.0,
    val imageUrl: String = "",
    val idCardUrl: String? = null,
    val isActive: Boolean = true,
    val isPinned: Boolean = false,
    val isRecommended: Boolean = false,
    val lat: Double? = null,
    val lng: Double? = null,
    val priceCategory: String? = "medium",
    val distanceCategory: String? = "near",
    val createdAt: String? = null,
    // New parameters appended at the end
    val isPinnedToSearch: Boolean = false,
    val isPinnedToCategory: Boolean = false,
    val workplaceAddress: String = "",
    val residenceArea: String = ""
)

data class PendingProvider(
    val id: String? = null,
    val name: String = "",
    val phone: String = "",
    val categoryId: Int = 0,
    val subCategoryId: Int? = null,
    val imageUrl: String = "",
    val idCardUrl: String? = null,
    val status: String = "pending",
    val region: String? = "",
    val createdAt: String? = null,
    // New parameters appended at the end
    val workplaceAddress: String = "",
    val residenceArea: String = "",
    val lat: Double? = null,
    val lng: Double? = null
)

data class Review(
    val id: Int? = null,
    val providerId: Int = 0,
    val userName: String = "",
    val comment: String = "",
    val rating: Double = 5.0,
    val createdAt: String? = null
)

data class Admin(
    val id: String? = null,
    val username: String = "",
    val passwordHash: String = "",
    val role: String = "admin",
    val createdAt: String? = null,
    // New parameters appended at the end
    val canApprove: Boolean = true,
    val canAddProviders: Boolean = true,
    val canEditSettings: Boolean = false,
    val canManageCategories: Boolean = false
)
