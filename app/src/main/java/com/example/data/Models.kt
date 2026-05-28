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
data class ServiceProvider(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "category_id") val categoryId: Int,
    @Json(name = "rating") val rating: Double = 5.0,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "created_at") val createdAt: String? = null
)
