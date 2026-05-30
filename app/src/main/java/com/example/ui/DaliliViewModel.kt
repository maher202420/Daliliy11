package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
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

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _subCategories = MutableStateFlow<List<SubCategory>>(emptyList())
    val subCategories = _subCategories.asStateFlow()

    private val _welcomeText = MutableStateFlow(sharedPrefs.getString("welcome_text", "المساعد الفوري للوصول إلى الخدمات ومقدمي الخدمات المحليين بلحظة واحدة.") ?: "المساعد الفوري للوصول إلى الخدمات ومقدمي الخدمات المحليين بلحظة واحدة.")
    val welcomeText = _welcomeText.asStateFlow()

    private val _welcomeImage = MutableStateFlow(sharedPrefs.getString("welcome_image", "📡") ?: "📡")
    val welcomeImage = _welcomeImage.asStateFlow()

    private val _welcomeSize = MutableStateFlow(sharedPrefs.getInt("welcome_size", 13))
    val welcomeSize = _welcomeSize.asStateFlow()

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

    private val _isCloudConnected = MutableStateFlow(false)
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

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

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

        // Load local persistent cache instead of Firebase Realtime listeners
        loadLocalData()
    }

    private fun loadLocalData() {
        try {
            // Load Settings from shared preferences (or use defaults)
            _supportPhone.value = sharedPrefs.getString("support_phone", "777644670") ?: "777644670"
            _supportEmail.value = sharedPrefs.getString("support_email", "support@dalili.com") ?: "support@dalili.com"
            _supportWhatsapp.value = sharedPrefs.getString("support_whatsapp", "777644670") ?: "777644670"
            _footerText.value = sharedPrefs.getString("footer_text", "MAW 777644670") ?: "MAW 777644670"
            _showFooter.value = sharedPrefs.getBoolean("show_footer", true)
            _userLaunches.value = sharedPrefs.getInt("user_launches", 0)
            _callsCount.value = sharedPrefs.getInt("calls_count", 0)

            // 1. Categories
            val categoriesJson = sharedPrefs.getString("local_categories", null)
            if (categoriesJson != null) {
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Category::class.java)
                _categories.value = moshi.adapter<List<Category>>(listType).fromJson(categoriesJson) ?: getDefaultCategories()
            } else {
                _categories.value = getDefaultCategories()
                saveCategories()
            }

            // 1.5. Subcategories
            val subCategoriesJson = sharedPrefs.getString("local_subcategories", null)
            if (subCategoriesJson != null) {
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, SubCategory::class.java)
                _subCategories.value = moshi.adapter<List<SubCategory>>(listType).fromJson(subCategoriesJson) ?: getDefaultSubCategories()
            } else {
                _subCategories.value = getDefaultSubCategories()
                saveSubCategories()
            }

            // 2. Service Providers
            val providersJson = sharedPrefs.getString("local_service_providers", null)
            if (providersJson != null) {
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, ServiceProvider::class.java)
                _serviceProviders.value = moshi.adapter<List<ServiceProvider>>(listType).fromJson(providersJson) ?: getDefaultProviders()
            } else {
                _serviceProviders.value = getDefaultProviders()
                saveServiceProviders()
            }

            // 3. Reviews
            val reviewsJson = sharedPrefs.getString("local_reviews", null)
            if (reviewsJson != null) {
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Review::class.java)
                _reviews.value = moshi.adapter<List<Review>>(listType).fromJson(reviewsJson) ?: getDefaultReviews()
            } else {
                _reviews.value = getDefaultReviews()
                saveReviews()
            }

            // 4. Admins
            val adminsJson = sharedPrefs.getString("local_admins", null)
            if (adminsJson != null) {
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Admin::class.java)
                _admins.value = moshi.adapter<List<Admin>>(listType).fromJson(adminsJson) ?: emptyList()
            }
            if (_admins.value.isEmpty()) {
                val defaultAdmin = Admin(
                    id = 1,
                    username = "admin",
                    passwordHash = "maher736462",
                    role = "super_admin",
                    createdAt = System.currentTimeMillis().toString()
                )
                _admins.value = listOf(defaultAdmin)
                saveAdmins()
            }

            // 5. Pending Providers
            val pendingJson = sharedPrefs.getString("local_pending_providers", null)
            if (pendingJson != null) {
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, PendingProvider::class.java)
                _pendingProviders.value = moshi.adapter<List<PendingProvider>>(listType).fromJson(pendingJson) ?: emptyList()
            } else {
                _pendingProviders.value = emptyList()
                savePendingProviders()
            }
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Failed to load local data", e)
        }
    }

    private fun saveCategories() {
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Category::class.java)
        val json = moshi.adapter<List<Category>>(listType).toJson(_categories.value)
        sharedPrefs.edit().putString("local_categories", json).apply()
    }

    private fun saveSubCategories() {
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, SubCategory::class.java)
        val json = moshi.adapter<List<SubCategory>>(listType).toJson(_subCategories.value)
        sharedPrefs.edit().putString("local_subcategories", json).apply()
    }

    private fun saveServiceProviders() {
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, ServiceProvider::class.java)
        val json = moshi.adapter<List<ServiceProvider>>(listType).toJson(_serviceProviders.value)
        sharedPrefs.edit().putString("local_service_providers", json).apply()
    }

    private fun saveReviews() {
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Review::class.java)
        val json = moshi.adapter<List<Review>>(listType).toJson(_reviews.value)
        sharedPrefs.edit().putString("local_reviews", json).apply()
    }

    private fun saveAdmins() {
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Admin::class.java)
        val json = moshi.adapter<List<Admin>>(listType).toJson(_admins.value)
        sharedPrefs.edit().putString("local_admins", json).apply()
    }

    private fun savePendingProviders() {
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, PendingProvider::class.java)
        val json = moshi.adapter<List<PendingProvider>>(listType).toJson(_pendingProviders.value)
        sharedPrefs.edit().putString("local_pending_providers", json).apply()
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
        loadLocalData()
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
                val list = _admins.value.toMutableList()
                list.add(newAdmin)
                _admins.value = list
                saveAdmins()
                handlerSuccessOnMain { onComplete(true) }
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
                val list = _admins.value.map { admin ->
                    if (admin.id == adminId) {
                        admin.copy(passwordHash = hashed)
                    } else {
                        admin
                    }
                }
                _admins.value = list
                saveAdmins()
                handlerSuccessOnMain { onComplete(true) }
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
                val list = _admins.value.filter { it.id != adminId }
                _admins.value = list
                saveAdmins()
                handlerSuccessOnMain { onComplete(true) }
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
                val list = _reviews.value.toMutableList()
                list.add(review)
                _reviews.value = list
                saveReviews()

                recalculateAndSaveProviderRating(providerId)
                handlerSuccessOnMain { onComplete(true) }
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
                val list = _reviews.value.filter { it.id != reviewId }
                _reviews.value = list
                saveReviews()

                if (reviewTarget != null) {
                    recalculateAndSaveProviderRating(reviewTarget.providerId)
                }
                handlerSuccessOnMain { onComplete(true) }
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

        val list = _serviceProviders.value.map { provider ->
            if (provider.id == providerId) {
                provider.copy(rating = finalRating)
            } else {
                provider
            }
        }
        _serviceProviders.value = list
        saveServiceProviders()
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
                val list = _categories.value.toMutableList()
                list.add(category)
                _categories.value = list
                saveCategories()
                handlerSuccessOnMain { onComplete(true) }
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
                val list = _categories.value.map { cat ->
                    if (cat.id == categoryId) {
                        cat.copy(nameAr = nameAr, icon = icon, orderIndex = orderIndex)
                    } else {
                        cat
                    }
                }
                _categories.value = list
                saveCategories()
                handlerSuccessOnMain { onComplete(true) }
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
                val list = _categories.value.filter { it.id != categoryId }
                _categories.value = list
                saveCategories()
                handlerSuccessOnMain { onComplete(true) }
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
        subCategoryId: Int? = null,
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
                    subCategoryId = subCategoryId,
                    rating = rating,
                    imageUrl = imageUrl,
                    isActive = isActive,
                    createdAt = System.currentTimeMillis().toString(),
                    lat = lat,
                    lng = lng,
                    priceCategory = priceCategory,
                    distanceCategory = distanceCategory
                )

                val list = _serviceProviders.value.toMutableList()
                list.add(provider)
                _serviceProviders.value = list
                saveServiceProviders()

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
        subCategoryId: Int? = null,
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
                val list = _serviceProviders.value.map { provider ->
                    if (provider.id == providerId) {
                        provider.copy(
                            name = name,
                            phone = phone,
                            categoryId = categoryId,
                            subCategoryId = subCategoryId,
                            rating = rating,
                            imageUrl = imageUrl,
                            isActive = isActive,
                            lat = lat,
                            lng = lng,
                            priceCategory = priceCategory,
                            distanceCategory = distanceCategory
                        )
                    } else {
                        provider
                    }
                }
                _serviceProviders.value = list
                saveServiceProviders()
                handlerSuccessOnMain { onComplete(true) }
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
                val list = _serviceProviders.value.filter { it.id != providerId }
                _serviceProviders.value = list
                saveServiceProviders()
                handlerSuccessOnMain { onComplete(true) }
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
                val id = java.util.UUID.randomUUID().toString()
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

                val list = _pendingProviders.value.toMutableList()
                list.add(pending)
                _pendingProviders.value = list
                savePendingProviders()
                handlerSuccessOnMain { onComplete(true) }
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

                val providersList = _serviceProviders.value.toMutableList()
                providersList.add(provider)
                _serviceProviders.value = providersList
                saveServiceProviders()

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
                    val pendingList = _pendingProviders.value.filter { it.id != pending.id }
                    _pendingProviders.value = pendingList
                    savePendingProviders()
                }
                handlerSuccessOnMain { onComplete(true) }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Approve pending failed", e)
                handlerSuccessOnMain { onComplete(false) }
            }
        }
    }

    fun rejectPendingProvider(pending: PendingProvider, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (pending.id != null) {
                val pendingList = _pendingProviders.value.filter { it.id != pending.id }
                _pendingProviders.value = pendingList
                savePendingProviders()
                handlerSuccessOnMain { onComplete(true) }
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
        sharedPrefs.edit()
            .putString("support_phone", phone)
            .putString("support_email", email)
            .putString("support_whatsapp", whatsapp)
            .putString("footer_text", footer)
            .putBoolean("show_footer", showF)
            .apply()

        _supportPhone.value = phone
        _supportEmail.value = email
        _supportWhatsapp.value = whatsapp
        _footerText.value = footer
        _showFooter.value = showF

        onComplete(true)
    }

    fun incrementUserLaunches() {
        val next = _userLaunches.value + 1
        sharedPrefs.edit().putInt("user_launches", next).apply()
        _userLaunches.value = next
    }

    fun incrementCallsCount() {
        val next = _callsCount.value + 1
        sharedPrefs.edit().putInt("calls_count", next).apply()
        _callsCount.value = next
    }

    // IMAGE FILE UPLOAD
    fun uploadImageToFirebaseStorage(uri: Uri, folder: String, onComplete: (String?) -> Unit) {
        onComplete(uri.toString())
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

    private fun getDefaultSubCategories(): List<SubCategory> {
        return listOf(
            SubCategory(id = 5001, parentCategoryId = 1003, nameAr = "عيادات العظام", icon = "🦴", orderIndex = 1),
            SubCategory(id = 5002, parentCategoryId = 1003, nameAr = "عيادات العيون", icon = "👁️", orderIndex = 2),
            SubCategory(id = 5003, parentCategoryId = 1003, nameAr = "الجراحة العامة", icon = "✂️", orderIndex = 3),
            SubCategory(id = 5004, parentCategoryId = 1003, nameAr = "تمريض منزلي", icon = "🩹", orderIndex = 4),
            SubCategory(id = 5005, parentCategoryId = 1002, nameAr = "كهرباء منزلي", icon = "🔌", orderIndex = 1),
            SubCategory(id = 5006, parentCategoryId = 1002, nameAr = "أعمال السباكة", icon = "🪠", orderIndex = 2),
            SubCategory(id = 5007, parentCategoryId = 1002, nameAr = "صيانة مكيفات", icon = "❄️", orderIndex = 3),
            SubCategory(id = 5008, parentCategoryId = 1001, nameAr = "تمديد شبكات", icon = "🎛️", orderIndex = 1),
            SubCategory(id = 5009, parentCategoryId = 1001, nameAr = "برمجة وبطاقات", icon = "💳", orderIndex = 2)
        )
    }

    fun addSubCategory(parentCategoryId: Int, nameAr: String, icon: String, orderIndex: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val nextId = (_subCategories.value.mapNotNull { it.id }.maxOfOrNull { it } ?: 5000) + 1
                val subCat = SubCategory(
                    id = nextId,
                    parentCategoryId = parentCategoryId,
                    nameAr = nameAr,
                    icon = icon,
                    orderIndex = orderIndex,
                    createdAt = System.currentTimeMillis().toString()
                )
                val list = _subCategories.value.toMutableList()
                list.add(subCat)
                _subCategories.value = list
                saveSubCategories()
                handlerSuccessOnMain { onComplete(true) }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Add subcategory failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateWelcomeConfig(text: String, image: String, size: Int, onComplete: (Boolean) -> Unit = {}) {
        sharedPrefs.edit()
            .putString("welcome_text", text)
            .putString("welcome_image", image)
            .putInt("welcome_size", size)
            .apply()
        _welcomeText.value = text
        _welcomeImage.value = image
        _welcomeSize.value = size
        onComplete(true)
    }
}
