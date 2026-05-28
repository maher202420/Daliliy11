package com.example.data

import retrofit2.Response
import retrofit2.http.*

interface SupabaseApi {
    
    // ADMINS
    @GET("admins")
    suspend fun getAdmins(
        @Query("select") select: String = "*"
    ): List<Admin>

    @POST("admins")
    @Headers("Prefer: return=representation")
    suspend fun createAdmin(
        @Body admin: Admin
    ): List<Admin>

    @PATCH("admins")
    suspend fun updateAdmin(
        @Query("id") idFilter: String, // e.g. "eq.5"
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @DELETE("admins")
    suspend fun deleteAdmin(
        @Query("id") idFilter: String // e.g. "eq.5"
    ): Response<Unit>

    // CATEGORIES
    @GET("categories")
    suspend fun getCategories(
        @Query("select") select: String = "*",
        @Query("order") order: String = "order_index.asc"
    ): List<Category>

    @POST("categories")
    @Headers("Prefer: return=representation")
    suspend fun createCategory(
        @Body category: Category
    ): List<Category>

    @PATCH("categories")
    suspend fun updateCategory(
        @Query("id") idFilter: String, // e.g. "eq.5"
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @DELETE("categories")
    suspend fun deleteCategory(
        @Query("id") idFilter: String
    ): Response<Unit>

    // SERVICE PROVIDERS
    @GET("service_providers")
    suspend fun getServiceProviders(
        @Query("select") select: String = "*",
        @Query("order") order: String = "id.asc"
    ): List<ServiceProvider>

    @POST("service_providers")
    @Headers("Prefer: return=representation")
    suspend fun createServiceProvider(
        @Body provider: ServiceProvider
    ): List<ServiceProvider>

    @PATCH("service_providers")
    suspend fun updateServiceProvider(
        @Query("id") idFilter: String, // e.g. "eq.5"
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @DELETE("service_providers")
    suspend fun deleteServiceProvider(
        @Query("id") idFilter: String
    ): Response<Unit>

    // REVIEWS & RATINGS
    @GET("reviews")
    suspend fun getReviews(
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<Review>

    @POST("reviews")
    @Headers("Prefer: return=representation")
    suspend fun createReview(
        @Body review: Review
    ): List<Review>

    @DELETE("reviews")
    suspend fun deleteReview(
        @Query("id") idFilter: String
    ): Response<Unit>
}
