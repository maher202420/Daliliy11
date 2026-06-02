package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "providerId") val providerId: Int,
    @Json(name = "userName") val userName: String,
    @Json(name = "comment") val comment: String,
    @Json(name = "rating") val rating: Double,
    @Json(name = "created_at") val createdAt: String? = null
)
