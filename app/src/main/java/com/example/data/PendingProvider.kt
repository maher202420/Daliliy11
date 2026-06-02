package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PendingProvider(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "categoryId") val categoryId: Int,
    @Json(name = "subCategoryId") val subCategoryId: Int? = null,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "idCardUrl") val idCardUrl: String? = null,
    @Json(name = "status") val status: String = "pending",
    @Json(name = "region") val region: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)
