package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Admin(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "username") val username: String,
    @Json(name = "password_hash") val passwordHash: String,
    @Json(name = "role") val role: String,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name_ar") val nameAr: String,
    @Json(name = "icon") val icon: String, // can be URL or Emoji
    @Json(name = "order_index") val orderIndex: Int,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SubCategory(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "parent_category_id") val parentCategoryId: Int,
    @Json(name = "name_ar") val nameAr: String,
    @Json(name = "icon") val icon: String,
    @Json(name = "order_index") val orderIndex: Int,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class ServiceProvider(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "category_id") val categoryId: Int,
    @Json(name = "sub_category_id") val subCategoryId: Int? = null,
    @Json(name = "rating") val rating: Double = 5.0,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "lat") val lat: Double? = null,
    @Json(name = "lng") val lng: Double? = null,
    @Json(name = "price_category") val priceCategory: String = "medium", // "low", "medium", "high"
    @Json(name = "distance_category") val distanceCategory: String = "medium" // "near", "medium", "far"
)

@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "provider_id") val providerId: Int,
    @Json(name = "user_name") val userName: String,
    @Json(name = "comment") val comment: String,
    @Json(name = "rating") val rating: Double,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class PendingProvider(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "category_id") val categoryId: Int,
    @Json(name = "sub_category_id") val subCategoryId: Int? = null,
    @Json(name = "region") val region: String,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "status") val status: String = "pending",
    @Json(name = "created_at") val createdAt: String? = null
)
