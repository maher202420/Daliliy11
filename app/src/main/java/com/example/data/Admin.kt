package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Admin(
    @Json(name = "id") val id: String? = null,
    @Json(name = "username") val username: String,
    @Json(name = "passwordHash") val passwordHash: String,
    @Json(name = "role") val role: String = "admin",
    @Json(name = "created_at") val createdAt: String? = null
)
