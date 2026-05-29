package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.utils.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

class DaliliViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("dalili_prefs", Context.MODE_PRIVATE)
    private val firestore = FirebaseFirestore.getInstance()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _serviceProviders = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val serviceProviders = _serviceProviders.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _admins = MutableStateFlow<List<Admin>>(emptyList())
    val admins = _admins.asStateFlow()

    private val _pendingProviders = MutableStateFlow<List<PendingProvider>>(emptyList())
    val pendingProviders = _pendingProviders.asStateFlow()

    private val _currentUser = MutableStateFlow<Admin?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _currentTheme = MutableStateFlow(getAppTheme())
    val currentTheme = _currentTheme.asStateFlow()

    private val _customAppName = MutableStateFlow(sharedPrefs.getString("custom_app_name", "دليلي - Dalili") ?: "دليلي - Dalili")
    val customAppName = _customAppName.asStateFlow()

    private val _customAppLogo = MutableStateFlow(sharedPrefs.getString("custom_app_logo", "📡") ?: "📡")
    val customAppLogo = _customAppLogo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isCloudConnected = MutableStateFlow(true)
    val isCloudConnected = _isCloudConnected.asStateFlow()

    private val _supportPhone = MutableStateFlow("777644670")
    val supportPhone = _supportPhone.asStateFlow()

    private val _supportEmail = MutableStateFlow("support@dalili.com")
    val supportEmail = _supportEmail.asStateFlow()

    private val _supportWhatsapp = MutableStateFlow("777644670")
    val supportWhatsapp = _supportWhatsapp.asStateFlow()

    private val _footerText = MutableStateFlow("MAW 777644670")
    val footerText = _footerText.asStateFlow()

    private val _showFooter = MutableStateFlow(true)
    val showFooter = _showFooter.asStateFlow()

    private val _userLaunches = MutableStateFlow(0)
    val userLaunches = _userLaunches.asStateFlow()

    private val _callsCount = MutableStateFlow(0)
    val callsCount = _callsCount.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _chatHistory = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val chatHistory = _chatHistory.asStateFlow()

    private val _isAssistantLoading = MutableStateFlow(false)
    val isAssistantLoading = _isAssistantLoading.asStateFlow()

    private val _isArabic = MutableStateFlow(sharedPrefs.getBoolean("app_lang_ar", true))
    val isArabic = _isArabic.asStateFlow()

    init {
        // Recover logged in user session if present
        val savedUsername = sharedPrefs.getString("saved_username", null)
        val savedRole = sharedPrefs.getString("saved_role", null)
        if (savedUsername != null && savedRole != null) {
            _currentUser.value = Admin(
                username = savedUsername,
                role = savedRole,
                passwordHash = ""
            )
        }

        // Start Firebase Realtime listeners
        startListeningFirebase()
    }

    private fun startListeningFirebase() {
        // 1. Categories Listener
        firestore.collection("categories")
            .orderBy("order_index", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DaliliViewModel", "Listen categories failed", error)
                    _isCloudConnected.value = false
                    return@addSnapshotListener
                }
                _isCloudConnected.value = true
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: doc.id.hashCode()
                            val nameAr = doc.getString("name_ar") ?: ""
                            val icon = doc.getString("icon") ?: "📁"
                            val orderIndex = doc.getLong("order_index")?.toInt() ?: 0
                            Category(id = id, nameAr = nameAr, icon = icon, orderIndex = orderIndex)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isEmpty()) {
                        _categories.value = getDefaultCategories()
                        autoPopulateDefaultCategories()
                    } else {
                        _categories.value = list
                    }
                }
            }

        // 2. Service Providers Listener
        firestore.collection("service_providers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DaliliViewModel", "Listen providers failed", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: doc.id.hashCode()
                            val name = doc.getString("name") ?: ""
                            val phone = doc.getString("phone") ?: ""
                            val categoryId = doc.getLong("category_id")?.toInt() ?: 0
                            val rating = doc.getDouble("rating") ?: 5.0
                            val imageUrl = doc.getString("image_url")
                            val isActive = doc.getBoolean("is_active") ?: true
                            val lat = doc.getDouble("lat")
                            val lng = doc.getDouble("lng")
                            val priceCategory = doc.getString("price_category") ?: "medium"
                            val distanceCategory = doc.getString("distance_category") ?: "medium"
                            ServiceProvider(
                                id = id,
                                name = name,
                                phone = phone,
                                categoryId = categoryId,
                                rating = rating,
                                imageUrl = imageUrl,
                                isActive = isActive,
                                lat = lat,
                                lng = lng,
                                priceCategory = priceCategory,
                                distanceCategory = distanceCategory
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isEmpty()) {
                        _serviceProviders.value = getDefaultProviders()
                        autoPopulateDefaultProviders()
                    } else {
                        _serviceProviders.value = list
                    }
                }
            }

        // 3. Reviews Listener
        firestore.collection("reviews")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: doc.id.hashCode()
                            val providerId = doc.getLong("provider_id")?.toInt() ?: 0
                            val userName = doc.getString("user_name") ?: ""
                            val comment = doc.getString("comment") ?: ""
                            val rating = doc.getDouble("rating") ?: 5.0
                            val createdAt = doc.getString("created_at")
                            Review(id = id, providerId = providerId, userName = userName, comment = comment, rating = rating, createdAt = createdAt)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isEmpty()) {
                        _reviews.value = getDefaultReviews()
                        autoPopulateDefaultReviews()
                    } else {
                        _reviews.value = list
                    }
                }
            }

        // 4. Admins Listener
        firestore.collection("admins")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: doc.id.hashCode()
                            val username = doc.getString("username") ?: ""
                            val passwordHash = doc.getString("password_hash") ?: ""
                            val role = doc.getString("role") ?: "admin"
                            val createdAt = doc.getString("created_at")
                            Admin(id = id, username = username, passwordHash = passwordHash, role = role, createdAt = createdAt)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isEmpty()) {
                        autoPopulateDefaultAdmins()
                    } else {
                        _admins.value = list
                    }
                }
            }

        // 5. Pending Providers Listener
        firestore.collection("pending_providers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.id
                            val name = doc.getString("name") ?: ""
                            val phone = doc.getString("phone") ?: ""
                            val categoryId = doc.getLong("category_id")?.toInt() ?: 0
                            val region = doc.getString("region") ?: ""
                            val imageUrl = doc.getString("image_url")
                            val status = doc.getString("status") ?: "pending"
                            val createdAt = doc.getString("created_at")
                            PendingProvider(id = id, name = name, phone = phone, categoryId = categoryId, region = region, imageUrl = imageUrl, status = status, createdAt = createdAt)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    _pendingProviders.value = list
                }
            }

        // 6. System Settings Listener
        firestore.collection("settings").document("app_config")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    _supportPhone.value = snapshot.getString("support_phone") ?: "777644670"
                    _supportEmail.value = snapshot.getString("support_email") ?: "support@dalili.com"
                    _supportWhatsapp.value = snapshot.getString("support_whatsapp") ?: "777644670"
                    _footerText.value = snapshot.getString("footer_text") ?: "MAW 777644670"
                    _showFooter.value = snapshot.getBoolean("show_footer") ?: true
                } else {
                    val initData = mapOf(
                        "support_phone" to "777644670",
                        "support_email" to "support@dalili.com",
                        "support_whatsapp" to "777644670",
                        "footer_text" to "MAW 777644670",
                        "show_footer" to true
                    )
                    firestore.collection("settings").document("app_config").set(initData)
                }
            }

        // 7. App Stats Listener
        firestore.collection("stats").document("app_usage")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    _userLaunches.value = snapshot.getLong("user_launches")?.toInt() ?: 0
                    _callsCount.value = snapshot.getLong("calls_count")?.toInt() ?: 0
                } else {
                    val initStats = mapOf(
                        "user_launches" to 0,
                        "calls_count" to 0
                    )
                    firestore.collection("stats").document("app_usage").set(initStats)
                }
            }
    }

    // Auto-population functions
    private fun autoPopulateDefaultCategories() {
        val defaults = getDefaultCategories()
        for (cat in defaults) {
            val data = mapOf(
                "id" to cat.id,
                "name_ar" to cat.nameAr,
                "icon" to cat.icon,
                "order_index" to cat.orderIndex,
                "created_at" to System.currentTimeMillis().toString()
            )
            firestore.collection("categories").document(cat.id.toString()).set(data)
        }
    }

    private fun autoPopulateDefaultProviders() {
        val defaults = getDefaultProviders()
        for (p in defaults) {
            val data = mapOf(
                "id" to p.id,
                "name" to p.name,
                "phone" to p.phone,
                "category_id" to p.categoryId,
                "rating" to p.rating,
                "image_url" to p.imageUrl,
                "is_active" to p.isActive,
                "created_at" to System.currentTimeMillis().toString()
            )
            firestore.collection("service_providers").document(p.id.toString()).set(data)
        }
    }

    private fun autoPopulateDefaultReviews() {
        val defaults = getDefaultReviews()
        for (rev in defaults) {
            val data = mapOf(
                "id" to rev.id,
                "provider_id" to rev.providerId,
                "user_name" to rev.userName,
                "comment" to rev.comment,
                "rating" to rev.rating,
                "created_at" to rev.createdAt
            )
            firestore.collection("reviews").document(rev.id.toString()).set(data)
        }
    }

    private fun autoPopulateDefaultAdmins() {
        val superAdmin = Admin(
            id = 1,
            username = "admin",
            passwordHash = "maher736462", // can be checked literally as requested
            role = "super_admin",
            createdAt = System.currentTimeMillis().toString()
        )
        val data = mapOf(
            "id" to superAdmin.id,
            "username" to superAdmin.username,
            "password_hash" to superAdmin.passwordHash,
            "role" to superAdmin.role,
            "created_at" to superAdmin.createdAt
        )
        firestore.collection("admins").document("1").set(data)
    }

    // SHARED PREFERENCES THEME & LANGUAGE CONFIG
    fun getAppTheme(): String {
        return sharedPrefs.getString("app_theme_choice", "light") ?: "light"
    }

    fun setAppTheme(theme: String) {
        sharedPrefs.edit().putString("app_theme_choice", theme).apply()
        _currentTheme.value = theme
    }

    fun toggleDarkMode() {
        val next = if (_currentTheme.value == "dark") "light" else "dark"
        setAppTheme(next)
    }

    fun toggleLanguage() {
        val next = !_isArabic.value
        sharedPrefs.edit().putBoolean("app_lang_ar", next).apply()
        _isArabic.value = next
    }

    fun refreshAll() {
        startListeningFirebase()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // AUTHENTICATION
    fun login(usernameInput: String, passwordInput: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Hardcoded fallback override for Super Admin
                if (usernameInput.trim() == "admin" && passwordInput == "maher736462") {
                    val rootAdmin = Admin(
                        id = 1,
                        username = "admin",
                        passwordHash = passwordInput,
                        role = "super_admin"
                    )
                    _currentUser.value = rootAdmin
                    persistSession(rootAdmin)
                    handlerSuccessOnMain { onResult(true, null) }
                    return@launch
                }

                val adminsFromDb = _admins.value
                val matchedAdmin = adminsFromDb.firstOrNull { it.username.trim().equals(usernameInput.trim(), ignoreCase = true) }

                if (matchedAdmin != null) {
                    val hashedInput = hashPasswordHelper(passwordInput)
                    val matchSuccessful = matchedAdmin.passwordHash == passwordInput || 
                                          matchedAdmin.passwordHash == hashedInput

                    if (matchSuccessful) {
                        _currentUser.value = matchedAdmin
                        persistSession(matchedAdmin)
                        handlerSuccessOnMain { onResult(true, null) }
                    } else {
                        handlerSuccessOnMain { onResult(false, "كلمة المرور غير صحيحة") }
                    }
                } else {
                    handlerSuccessOnMain { onResult(false, "اسم المستخدم غير موجود") }
                }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Login error", e)
                handlerSuccessOnMain { onResult(false, "خطأ بالاتصال: ${e.localizedMessage}") }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        val theme = getAppTheme()
        val lang = _isArabic.value
        val appName = _customAppName.value
        val appLogo = _customAppLogo.value

        sharedPrefs.edit().clear().apply()

        // Restore custom preferences on logout
        sharedPrefs.edit()
            .putString("app_theme_choice", theme)
            .putBoolean("app_lang_ar", lang)
            .putString("custom_app_name", appName)
            .putString("custom_app_logo", appLogo)
            .apply()
    }

    fun persistSession(admin: Admin) {
        sharedPrefs.edit()
            .putString("saved_username", admin.username)
            .putString("saved_role", admin.role)
            .apply()
    }

    fun clearSavedSession() {
        sharedPrefs.edit()
            .remove("saved_username")
            .remove("saved_role")
            .apply()
    }

    private fun handlerSuccessOnMain(action: Runnable) {
        android.os.Handler(android.os.Looper.getMainLooper()).post(action)
    }

    // MANAGE ADMINS (Super Admin only)
    fun addAdmin(usernameInput: String, passwordInput: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val hashed = hashPasswordHelper(passwordInput)
                val newId = (_admins.value.mapNotNull { it.id }.maxOrNull() ?: 10) + 1
                val newAdmin = Admin(
                    id = newId,
                    username = usernameInput,
                    passwordHash = hashed,
                    role = "admin",
                    createdAt = System.currentTimeMillis().toString()
                )

                firestore.collection("admins").document(newId.toString())
                    .set(newAdmin)
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Add admin failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changeAdminPassword(adminId: Int, newPasswordInput: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val hashed = hashPasswordHelper(newPasswordInput)
                firestore.collection("admins").document(adminId.toString())
                    .update("password_hash", hashed)
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Change pass failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAdmin(adminId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                firestore.collection("admins").document(adminId.toString())
                    .delete()
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Delete admin failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // REVIEWS AND RATINGS MANAGEMENT
    fun addReview(providerId: Int, userName: String, comment: String, rating: Double, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nextId = (_reviews.value.mapNotNull { it.id }.maxOrNull() ?: 3000) + 1
                val review = Review(
                    id = nextId,
                    providerId = providerId,
                    userName = userName,
                    comment = comment,
                    rating = rating,
                    createdAt = System.currentTimeMillis().toString()
                )

                firestore.collection("reviews").document(nextId.toString())
                    .set(review)
                    .addOnSuccessListener {
                        recalculateAndSaveProviderRating(providerId)
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Add review error", e)
                handlerSuccessOnMain { onComplete(false) }
            }
        }
    }

    fun deleteReview(reviewId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val reviewTarget = _reviews.value.firstOrNull { it.id == reviewId }
                firestore.collection("reviews").document(reviewId.toString())
                    .delete()
                    .addOnSuccessListener {
                        if (reviewTarget != null) {
                            recalculateAndSaveProviderRating(reviewTarget.providerId)
                        }
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Delete review error", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun recalculateAndSaveProviderRating(providerId: Int) {
        val matchingReviews = _reviews.value.filter { it.providerId == providerId }
        val finalRating = if (matchingReviews.isEmpty()) {
            5.0
        } else {
            val avg = matchingReviews.map { it.rating }.average()
            String.format("%.1f", avg).toDoubleOrNull() ?: 5.0
        }

        firestore.collection("service_providers").document(providerId.toString())
            .update("rating", finalRating)
    }

    // MANAGE CATEGORIES (Admins and Super Admins)
    fun addCategory(nameAr: String, icon: String, orderIndex: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val nextId = (_categories.value.mapNotNull { it.id }.maxOrNull() ?: 1000) + 1
                val category = Category(
                    id = nextId,
                    nameAr = nameAr,
                    icon = icon,
                    orderIndex = orderIndex,
                    createdAt = System.currentTimeMillis().toString()
                )

                firestore.collection("categories").document(nextId.toString())
                    .set(category)
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Add category failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCategory(categoryId: Int, nameAr: String, icon: String, orderIndex: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val updates = mapOf(
                    "name_ar" to nameAr,
                    "icon" to icon,
                    "order_index" to orderIndex
                )
                firestore.collection("categories").document(categoryId.toString())
                    .update(updates)
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Update category failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCategory(categoryId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                firestore.collection("categories").document(categoryId.toString())
                    .delete()
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Delete category failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MANAGE SERVICE PROVIDERS (Admins and Super Admins)
    fun addServiceProvider(
        name: String, 
        phone: String, 
        categoryId: Int, 
        rating: Double, 
        imageUrl: String, 
        isActive: Boolean, 
        lat: Double? = null,
        lng: Double? = null,
        priceCategory: String = "medium",
        distanceCategory: String = "medium",
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val nextId = (_serviceProviders.value.mapNotNull { it.id }.maxOrNull() ?: 2000) + 1
                val provider = ServiceProvider(
                    id = nextId,
                    name = name,
                    phone = phone,
                    categoryId = categoryId,
                    rating = rating,
                    imageUrl = imageUrl,
                    isActive = isActive,
                    createdAt = System.currentTimeMillis().toString(),
                    lat = lat,
                    lng = lng,
                    priceCategory = priceCategory,
                    distanceCategory = distanceCategory
                )

                firestore.collection("service_providers").document(nextId.toString())
                    .set(provider)
                    .addOnSuccessListener {
                        try {
                            NotificationHelper.scheduleNotification(
                                getApplication(),
                                "تم إضافة خدمة جديدة! 🎉",
                                "تم إضافة مقدم الخدمة: $name بنجاح!"
                            )
                        } catch (e: Exception) {
                            Log.e("DaliliViewModel", "Notification scheduling failed", e)
                        }
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Add service provider fail", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateServiceProvider(
        providerId: Int,
        name: String,
        phone: String,
        categoryId: Int,
        rating: Double,
        imageUrl: String?,
        isActive: Boolean,
        lat: Double? = null,
        lng: Double? = null,
        priceCategory: String = "medium",
        distanceCategory: String = "medium",
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val updates = mapOf(
                    "name" to name,
                    "phone" to phone,
                    "category_id" to categoryId,
                    "rating" to rating,
                    "image_url" to imageUrl,
                    "is_active" to isActive,
                    "lat" to lat,
                    "lng" to lng,
                    "price_category" to priceCategory,
                    "distance_category" to distanceCategory
                )

                firestore.collection("service_providers").document(providerId.toString())
                    .update(updates)
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Update provider failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteServiceProvider(providerId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                firestore.collection("service_providers").document(providerId.toString())
                    .delete()
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Delete provider failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // PENDING PROVIDERS (REGISTRATION REQUESTS)
    fun addPendingProvider(name: String, phone: String, categoryId: Int, region: String, imageUrl: String?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = firestore.collection("pending_providers").document().id
                val pending = PendingProvider(
                    id = id,
                    name = name,
                    phone = phone,
                    categoryId = categoryId,
                    region = region,
                    imageUrl = imageUrl,
                    status = "pending",
                    createdAt = System.currentTimeMillis().toString()
                )

                firestore.collection("pending_providers").document(id)
                    .set(pending)
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Add pending provider fail", e)
                handlerSuccessOnMain { onComplete(false) }
            }
        }
    }

    fun approvePendingProvider(pending: PendingProvider, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nextId = (_serviceProviders.value.mapNotNull { it.id }.maxOrNull() ?: 2000) + 1
                val provider = ServiceProvider(
                    id = nextId,
                    name = pending.name,
                    phone = pending.phone,
                    categoryId = pending.categoryId,
                    rating = 5.0,
                    imageUrl = pending.imageUrl ?: "",
                    isActive = true,
                    createdAt = System.currentTimeMillis().toString()
                )

                firestore.collection("service_providers").document(nextId.toString())
                    .set(provider)
                    .addOnSuccessListener {
                        try {
                            NotificationHelper.scheduleNotification(
                                getApplication(),
                                "تهانينا! تم تفعيل حسابك 🏆",
                                "مرحباً بك ${pending.name}، تم الموافقة على انضمامك كمهني!"
                            )
                        } catch (e: Exception) {
                            Log.e("DaliliViewModel", "Approval notification failed", e)
                        }

                        if (pending.id != null) {
                            firestore.collection("pending_providers").document(pending.id)
                                .delete()
                                .addOnSuccessListener { handlerSuccessOnMain { onComplete(true) } }
                                .addOnFailureListener { handlerSuccessOnMain { onComplete(true) } }
                        } else {
                            handlerSuccessOnMain { onComplete(true) }
                        }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Approve pending failed", e)
                handlerSuccessOnMain { onComplete(false) }
            }
        }
    }

    fun rejectPendingProvider(pending: PendingProvider, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (pending.id != null) {
                firestore.collection("pending_providers").document(pending.id)
                    .delete()
                    .addOnSuccessListener {
                        handlerSuccessOnMain { onComplete(true) }
                    }
                    .addOnFailureListener {
                        handlerSuccessOnMain { onComplete(false) }
                    }
            } else {
                handlerSuccessOnMain { onComplete(false) }
            }
        }
    }

    // BRANDING CUSTOMIZATION
    fun updateAppNameAndLogo(name: String, logo: String) {
        sharedPrefs.edit()
            .putString("custom_app_name", name)
            .putString("custom_app_logo", logo)
            .apply()
        _customAppName.value = name
        _customAppLogo.value = logo
    }

    fun updateSystemConfig(phone: String, email: String, whatsapp: String, footer: String, showF: Boolean, onComplete: (Boolean) -> Unit = {}) {
        val data = mapOf(
            "support_phone" to phone,
            "support_email" to email,
            "support_whatsapp" to whatsapp,
            "footer_text" to footer,
            "show_footer" to showF
        )
        firestore.collection("settings").document("app_config")
            .set(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun incrementUserLaunches() {
        val docRef = firestore.collection("stats").document("app_usage")
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentLaunches = snapshot.getLong("user_launches") ?: 0
            val currentCalls = snapshot.getLong("calls_count") ?: 0
            transaction.set(docRef, mapOf(
                "user_launches" to currentLaunches + 1,
                "calls_count" to currentCalls
            ))
            null
        }.addOnFailureListener {
            firestore.collection("stats").document("app_usage").set(mapOf(
                "user_launches" to 1,
                "calls_count" to 0
            ))
        }
    }

    fun incrementCallsCount() {
        val docRef = firestore.collection("stats").document("app_usage")
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentLaunches = snapshot.getLong("user_launches") ?: 0
            val currentCalls = snapshot.getLong("calls_count") ?: 0
            transaction.set(docRef, mapOf(
                "user_launches" to currentLaunches,
                "calls_count" to currentCalls + 1
            ))
            null
        }.addOnFailureListener {
            firestore.collection("stats").document("app_usage").set(mapOf(
                "user_launches" to 1,
                "calls_count" to 1
            ))
        }
    }

    // IMAGE FILE UPLOAD
    fun uploadImageToFirebaseStorage(uri: Uri, folder: String, onComplete: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val storageRef = FirebaseStorage.getInstance().reference
                val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment ?: "image"}.jpg"
                val fileRef = storageRef.child("$folder/$fileName")

                fileRef.putFile(uri)
                    .addOnSuccessListener {
                        fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            onComplete(downloadUri.toString())
                        }.addOnFailureListener {
                            onComplete(null)
                        }
                    }
                    .addOnFailureListener {
                        Log.e("DaliliViewModel", "Image upload failed to Storage", it)
                        onComplete(null)
                    }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Storage upload catch", e)
                onComplete(null)
            }
        }
    }

    // SMART ASSISTANT METHODS
    fun addChatMessage(message: String, isUser: Boolean) {
        val current = _chatHistory.value.toMutableList()
        current.add(Pair(message, isUser))
        _chatHistory.value = current
    }

    fun askAssistant(question: String) {
        if (question.trim().isEmpty()) return

        val current = _chatHistory.value.toMutableList()
        current.add(Pair(question, true))
        _chatHistory.value = current

        _isAssistantLoading.value = true

        viewModelScope.launch {
            try {
                val response = callGeminiApiDirect(question)
                val updated = _chatHistory.value.toMutableList()
                updated.add(Pair(response, false))
                _chatHistory.value = updated
            } catch (e: Exception) {
                val response = getOfflineAnswer(question)
                val updated = _chatHistory.value.toMutableList()
                updated.add(Pair(response, false))
                _chatHistory.value = updated
            } finally {
                _isAssistantLoading.value = false
            }
        }
    }

    private suspend fun callGeminiApiDirect(question: String): String = withContext(Dispatchers.IO) {
        val apiKey = com.example.BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "عذراً، لم يتم العثور على مفتاح Gemini API في إعدادات التطبيق. يرجى إدخال المفتاح في لوحة تحكم المشرفين لتفعيل المساعد الذكي بالكامل."
        }

        val categoriesText = _categories.value.map { "${it.nameAr} (معرف القسم: ${it.id})" }.joinToString(", ")
        val providersText = _serviceProviders.value.filter { it.isActive }.map { "${it.name} (الهاتف: ${it.phone}, القسم: ${it.categoryId})" }.joinToString("\n")

        val systemPrompt = "أنت مساعد دليل الخدمات الذكي (دليلي - Dalili) في اليمن. تجيب بلغة عربية يمنية ودودة ومباشرة وتساعد في العثور على أرقام الهواتف والتواصل مع مقدمي الخدمة.\n" +
                "الأقسام المتاحة حالياً:\n$categoriesText\n\nمقدمو الخدمات النشطون حالياً:\n$providersText\n\nأجب باختصار واذكر معلومات الاتصال ومميزات مقدمي الخدمة لتفيد المستخدم."

        val requestJson = JSONObject()
        val contentsArray = JSONArray()

        val historyToUse = _chatHistory.value.takeLast(10)
        for (turn in historyToUse) {
            val contentObject = JSONObject()
            val role = if (turn.second) "user" else "model"
            contentObject.put("role", role)

            val partsArray = JSONArray()
            val partObject = JSONObject()
            partObject.put("text", turn.first)
            partsArray.put(partObject)
            contentObject.put("parts", partsArray)

            contentsArray.put(contentObject)
        }

        requestJson.put("contents", contentsArray)

        val sysInstructionObject = JSONObject()
        val sysPartsArray = JSONArray()
        val sysPartObject = JSONObject()
        sysPartObject.put("text", systemPrompt)
        sysPartsArray.put(sysPartObject)
        sysInstructionObject.put("parts", sysPartsArray)
        requestJson.put("systemInstruction", sysInstructionObject)

        val genConfig = JSONObject()
        genConfig.put("temperature", 0.7)
        requestJson.put("generationConfig", genConfig)

        val client = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = RequestBody.create(mediaType, requestJson.toString())

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            val response: Response = client.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""
            if (response.isSuccessful && responseBodyString.isNotEmpty()) {
                val responseJson = JSONObject(responseBodyString)
                val candidatesArray = responseJson.optJSONArray("candidates")
                if (candidatesArray != null && candidatesArray.length() > 0) {
                    val firstCandidate = candidatesArray.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    if (contentObj != null) {
                        val partsArr = contentObj.optJSONArray("parts")
                        if (partsArr != null && partsArr.length() > 0) {
                            return@withContext partsArr.getJSONObject(0).optString("text")
                        }
                    }
                }
            }
            Log.e("DaliliViewModel", "API error: ${response.code} body: $responseBodyString")
            throw Exception("API code ${response.code}")
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Gemini general call exception", e)
            throw e
        }
    }

    private fun getOfflineAnswer(question: String): String {
        val providers = _serviceProviders.value.filter { it.isActive }
        val categories = _categories.value
        val qTrim = question.trim()

        // Handle specific offline template queries requested by the user
        if (qTrim.contains("أقسم") || qTrim.contains("الاقسام") || qTrim.contains("الأقسام") || qTrim.contains("اقسم")) {
            return buildString {
                append("📶 [الوضع المحلي] مرحباً بك! لا يتوفر اتصال بالإنترنت حالياً.\n")
                append("الأقسام المتاحة حالياً في تطبيق دليلي هي:\n")
                categories.forEach { append("📁 ${it.nameAr}\n") }
                append("\nيمكنك تصفح أي قسم من الصفحة الرئيسية مباشرة بالضغط عليه.")
            }
        }
        if (qTrim.contains("أتصل") || qTrim.contains("اتصل") || qTrim.contains("تواصل") || qTrim.contains("كيف")) {
            return buildString {
                append("📶 [الوضع المحلي] مرحباً بك! لا يتوفر اتصال بالإنترنت حالياً.\n")
                append("كيفية الاتصال والتواصل بمقدمي الخدمة:\n")
                append("1. اضغط على أي قسم من واجهة التطبيق الرئيسية.\n")
                append("2. سيظهر لك مقدمو الخدمات المسجلين، وللتواصل المباشر يحتوي كل مقدم خدمة على:\n")
                append("   📞 زر اتصال مباشر لإجراء مكالمة هاتفية.\n")
                append("   💬 زر واتساب لفتح محادثة مباشرة وسريعة.\n")
                append("   ✉️ زر إرسال رسالة SMS لإرسال رسالة نصية.")
            }
        }
        if (qTrim.contains("دعم") || qTrim.contains("رقم") || qTrim.contains("مساعدة")) {
            return buildString {
                append("📶 [الوضع المحلي] مرحباً بك! لا يتوفر اتصال بالإنترنت حالياً.\n")
                append("رقم الدعم الفني والمساعدة هو:\n")
                append("📞 777644670\n")
                append("يسعدنا تواصلكم معنا للاستفسار أو تعديل نص التذييل.")
            }
        }

        val matchedProviders = providers.filter { 
            it.name.contains(question, ignoreCase = true) || 
            question.contains(it.name, ignoreCase = true)
        }

        val matchedCategories = categories.filter { 
            it.nameAr.contains(question, ignoreCase = true) || 
            question.contains(it.nameAr, ignoreCase = true)
        }

        return buildString {
            append("📶 [الوضع المحلي] مرحباً بك! لا يتوفر اتصال بالإنترنت حالياً.\n")
            if (matchedProviders.isNotEmpty()) {
                append("وجدنا مقدمي الخدمات المحليين التاليين لعرضهم:\n")
                matchedProviders.forEach {
                    append("- ${it.name} 📞 الهاتف: ${it.phone}\n")
                }
            } else if (matchedCategories.isNotEmpty()) {
                val cat = matchedCategories.first()
                append("بالنسبة لقسم \"${cat.nameAr}\"، يمكنك تصفح مقدمي خدماته بالاسم والهاتف في الصفحة الرئيسية للقسم.\n")
                val catProviders = providers.filter { it.categoryId == cat.id }
                if (catProviders.isNotEmpty()) {
                    append("إليك بعض الأرقام في هذا القسم:\n")
                    catProviders.take(3).forEach {
                        append("- ${it.name} 📞 الهاتف: ${it.phone}\n")
                    }
                }
            } else {
                append("يمكنني البحث محلياً بالأقسام ومقدمي الخدمات. اكتب مثلاً 'طبيب' أو 'أحمد' أو 'صيانة'.\nالأقسام المتاحة: ")
                append(categories.joinToString("، ") { it.nameAr })
            }
        }
    }

    fun clearChatHistory() {
        _chatHistory.value = emptyList()
    }

    // Default static initializers in case Firestore is unreachable
    private fun getDefaultCategories(): List<Category> {
        return listOf(
            Category(id = 1001, nameAr = "خدمات الاتصالات والنت", icon = "📱", orderIndex = 1),
            Category(id = 1002, nameAr = "الهندسة والصيانة المنزلية", icon = "🛠️", orderIndex = 2),
            Category(id = 1003, nameAr = "الطب والتمريض والعيادات", icon = "🩺", orderIndex = 3),
            Category(id = 1004, nameAr = "سيارات وسائقين وأجرة", icon = "🚕", orderIndex = 4),
            Category(id = 1005, nameAr = "خدمات التعليم والتدريس", icon = "📚", orderIndex = 5),
            Category(id = 1006, nameAr = "خدمات الطعام وتوصيل الطلبات", icon = "🍕", orderIndex = 6)
        )
    }

    private fun getDefaultProviders(): List<ServiceProvider> {
        return listOf(
            ServiceProvider(id = 2001, name = "مؤسسة الاتصالات والشبكات والإنترنت المتكاملة", phone = "777644670", categoryId = 1001, rating = 5.0, imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c", isActive = true, lat = 15.3694, lng = 44.1910, priceCategory = "low", distanceCategory = "near"),
            ServiceProvider(id = 2002, name = "المهندس أحمد لصيانة التكييف والأجهزة المنزلية", phone = "711223344", categoryId = 1002, rating = 4.8, imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758", isActive = true, lat = 15.3500, lng = 44.2000, priceCategory = "medium", distanceCategory = "medium"),
            ServiceProvider(id = 2003, name = "أخصائي الطقس والتمريض المنزلي السريع", phone = "770011223", categoryId = 1003, rating = 5.0, imageUrl = "https://images.unsplash.com/photo-1559839734-2b71ea197ec2", isActive = true, lat = 15.3600, lng = 44.1800, priceCategory = "high", distanceCategory = "far"),
            ServiceProvider(id = 2004, name = "تاكسي المشوار السريع للتنقل والرحلات", phone = "777644670", categoryId = 1004, rating = 4.9, imageUrl = "https://images.unsplash.com/photo-1549417229-aa67d3263c09", isActive = true, lat = 15.3700, lng = 44.2100, priceCategory = "medium", distanceCategory = "near"),
            ServiceProvider(id = 2005, name = "أستاذ الرياضيات والفيزياء الخصوصي", phone = "733445566", categoryId = 1005, rating = 4.7, imageUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173", isActive = true, lat = 15.3340, lng = 44.2010, priceCategory = "low", distanceCategory = "medium"),
            ServiceProvider(id = 2006, name = "مطعم الطاهي اليمني للوجبات السريعة والتوصيل", phone = "775566778", categoryId = 1006, rating = 4.6, imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38", isActive = true, lat = 15.3660, lng = 44.1750, priceCategory = "medium", distanceCategory = "near")
        )
    }

    private fun getDefaultReviews(): List<Review> {
        return listOf(
            Review(id = 3001, providerId = 2001, userName = "أبو ماجد", comment = "خدمة ممتازة وسريعة، وتغطية شبكة جيدة جداً في كل المناطق.", rating = 5.0, createdAt = "2026-05-27T10:00:00Z"),
            Review(id = 3002, providerId = 2001, userName = "فيصل الحربي", comment = "الدعم الفني متعاون للغاية وسرعة في استجابة المشكلات.", rating = 4.0, createdAt = "2026-05-27T12:30:00Z"),
            Review(id = 3003, providerId = 2003, userName = "د. علي الخالدي", comment = "أبطال الإسعاف، استجابة سريعة جداً في وقت الطوارئ شكراً لكم.", rating = 5.0, createdAt = "2026-05-27T11:15:00Z"),
            Review(id = 3004, providerId = 2004, userName = "سارة أحمد", comment = "سائق محترم والسيارة نظيفة ووصلت بالوقت المحدد.", rating = 5.0, createdAt = "2026-05-27T14:45:00Z")
        )
    }

    private fun hashPasswordHelper(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
            hash.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            password
        }
    }
}
