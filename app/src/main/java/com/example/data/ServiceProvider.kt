package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceProvider(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "categoryId") val categoryId: Int,
    @Json(name = "subCategoryId") val subCategoryId: Int? = null,
    @Json(name = "rating") val rating: Double = 0.0,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "idCardUrl") val idCardUrl: String? = null,
    @Json(name = "isActive") val isActive: Boolean = true,
    @Json(name = "isPinned") val isPinned: Boolean = false,
    @Json(name = "isRecommended") val isRecommended: Boolean = false,
    @Json(name = "lat") val lat: Double? = null,
    @Json(name = "lng") val lng: Double? = null,
    @Json(name = "priceCategory") val priceCategory: String? = null,
    @Json(name = "distanceCategory") val distanceCategory: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)
