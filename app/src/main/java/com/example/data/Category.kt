package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "nameAr") val nameAr: String,
    @Json(name = "icon") val icon: String,
    @Json(name = "orderIndex") val orderIndex: Int = 0,
    @Json(name = "created_at") val createdAt: String? = null
)
