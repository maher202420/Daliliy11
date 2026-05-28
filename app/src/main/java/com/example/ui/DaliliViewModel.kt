package com.example.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class DaliliViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("dalili_prefs", Context.MODE_PRIVATE)

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _serviceProviders = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val serviceProviders = _serviceProviders.asStateFlow()

    private val _admins = MutableStateFlow<List<Admin>>(emptyList())
    val admins = _admins.asStateFlow()

    private val _currentUser = MutableStateFlow<Admin?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var realtimeClient: RealtimeClient? = null

    // Moshi serializers for caching lists securely to SharedPreferences
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val categoriesAdapter by lazy {
        val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, Category::class.java)
        moshi.adapter<List<Category>>(type)
    }

    private val providersAdapter by lazy {
        val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, ServiceProvider::class.java)
        moshi.adapter<List<ServiceProvider>>(type)
    }

    private val adminsAdapter by lazy {
        val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, Admin::class.java)
        moshi.adapter<List<Admin>>(type)
    }

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

        // Load Cached lists or Local Arabian Defaults state so the screen is never blank on boot!
        _categories.value = loadCategoriesCache()
        _serviceProviders.value = loadProvidersCache()
        _admins.value = loadAdminsCache()

        // Initialize Realtime WebSocket Sync
        realtimeClient = RealtimeClient {
            refreshDataSilent()
        }
        realtimeClient?.start()

        // Initial live server fetch
        refreshAll()

        // Start safety periodic refresh (fallback in case socket drops)
        viewModelScope.launch {
            while (true) {
                delay(12000) // 12 seconds
                refreshDataSilent()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        realtimeClient?.stop()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshAll() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                fetchCategories()
                fetchServiceProviders()
                if (_currentUser.value?.role == "super_admin") {
                    fetchAdmins()
                }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Error refreshing data", e)
                // Do not clear the list, just show a message to notify them but keep displaying cached items
                _errorMessage.value = "ملاحظة: تصفح أوفلاين (خطأ بالاتصال: ${e.localizedMessage})"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun refreshDataSilent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fetchCategories()
                fetchServiceProviders()
                if (_currentUser.value?.role == "super_admin") {
                    fetchAdmins()
                }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Silent refresh failed (offline mode)", e)
            }
        }
    }

    private suspend fun fetchCategories() {
        try {
            val fetched = SupabaseClient.api.getCategories()
            if (fetched.isNotEmpty()) {
                val sorted = fetched.sortedBy { it.orderIndex }
                _categories.value = sorted
                saveCategoriesCache(sorted)
            }
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Failed to get categories from server", e)
            throw e
        }
    }

    private suspend fun fetchServiceProviders() {
        try {
            val fetched = SupabaseClient.api.getServiceProviders()
            if (fetched.isNotEmpty()) {
                _serviceProviders.value = fetched
                saveProvidersCache(fetched)
            }
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Failed to get service providers from server", e)
            throw e
        }
    }

    private suspend fun fetchAdmins() {
        try {
            val fetched = SupabaseClient.api.getAdmins()
            if (fetched.isNotEmpty()) {
                _admins.value = fetched
                saveAdminsCache(fetched)
            }
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Failed to get admins list", e)
        }
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
                        id = null,
                        username = "admin",
                        passwordHash = passwordInput,
                        role = "super_admin"
                    )
                    _currentUser.value = rootAdmin
                    persistSession(rootAdmin)
                    fetchAdmins()
                    handlerSuccessOnMain { onResult(true, null) }
                    return@launch
                }

                // Query database, falling back automatically to offline cache list if network error!
                val adminsFromDb = try {
                    SupabaseClient.api.getAdmins()
                } catch (e: Exception) {
                    Log.e("DaliliViewModel", "Admins network fetch failed. Authenticating with local lists.", e)
                    _admins.value
                }

                val matchedAdmin = adminsFromDb.firstOrNull { it.username.trim() == usernameInput.trim() }

                if (matchedAdmin != null) {
                    val hashedInput = hashPasswordHelper(passwordInput)
                    val matchSuccessful = matchedAdmin.passwordHash == passwordInput || 
                                          matchedAdmin.passwordHash == hashedInput

                    if (matchSuccessful) {
                        _currentUser.value = matchedAdmin
                        persistSession(matchedAdmin)
                        if (matchedAdmin.role == "super_admin" || matchedAdmin.username == "admin") {
                            fetchAdmins()
                        }
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
        _admins.value = emptyList()
        sharedPrefs.edit().clear().apply()
    }

    private fun persistSession(admin: Admin) {
        sharedPrefs.edit()
            .putString("saved_username", admin.username)
            .putString("saved_role", admin.role)
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
                val newAdmin = Admin(
                    username = usernameInput,
                    passwordHash = hashed,
                    role = "admin"
                )

                // Try uploading to cloud first
                try {
                    SupabaseClient.api.createAdmin(newAdmin)
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote addAdmin failed, fallback local write", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم الحفظ محلياً لعدم توفر إنترنت"
                    }
                }

                // Ensure local data state is fully modified offline-ready
                val currentList = _admins.value.toMutableList()
                val nextId = (currentList.mapNotNull { it.id }.maxOrNull() ?: 10) + 1
                currentList.add(newAdmin.copy(id = nextId))
                _admins.value = currentList
                saveAdminsCache(currentList)

                fetchAdmins()
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
                
                try {
                    val updates = mapOf("password_hash" to hashed)
                    SupabaseClient.api.updateAdmin("eq.$adminId", updates)
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote password update failed, fallback local update", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم تغيير كلمة المرور محلياً"
                    }
                }

                // Apply update to local persistence
                val currentList = _admins.value.map {
                    if (it.id == adminId) it.copy(passwordHash = hashed) else it
                }
                _admins.value = currentList
                saveAdminsCache(currentList)

                fetchAdmins()
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
                try {
                    SupabaseClient.api.deleteAdmin("eq.$adminId")
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote deleteAdmin failed, fallback local wipe", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم المسح محلياً لعدم توفر إنترنت"
                    }
                }

                val currentList = _admins.value.filter { it.id != adminId }
                _admins.value = currentList
                saveAdminsCache(currentList)

                fetchAdmins()
                handlerSuccessOnMain { onComplete(true) }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Delete admin failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MANAGE CATEGORIES (Admins and Super Admins)
    fun addCategory(nameAr: String, icon: String, orderIndex: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val category = Category(
                    nameAr = nameAr,
                    icon = icon,
                    orderIndex = orderIndex
                )

                try {
                    SupabaseClient.api.createCategory(category)
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote createCategory failed, fallback local write", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم إضافة القسم محلياً دليلي"
                    }
                }

                // Store locally and update state flows dynamically
                val currentList = _categories.value.toMutableList()
                val nextId = (currentList.mapNotNull { it.id }.maxOrNull() ?: 1000) + 1
                currentList.add(category.copy(id = nextId))
                val sorted = currentList.sortedBy { it.orderIndex }
                _categories.value = sorted
                saveCategoriesCache(sorted)

                fetchCategories()
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
                try {
                    val updates = mapOf(
                        "name_ar" to nameAr,
                        "icon" to icon,
                        "order_index" to orderIndex
                    )
                    SupabaseClient.api.updateCategory("eq.$categoryId", updates)
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote updateCategory failed, fallback local write", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم تعديل القسم محلياً بنجاح"
                    }
                }

                // Store locally and update state flows dynamically
                val currentList = _categories.value.map {
                    if (it.id == categoryId) {
                        it.copy(nameAr = nameAr, icon = icon, orderIndex = orderIndex)
                    } else it
                }.sortedBy { it.orderIndex }
                _categories.value = currentList
                saveCategoriesCache(currentList)

                fetchCategories()
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
                try {
                    SupabaseClient.api.deleteCategory("eq.$categoryId")
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote deleteCategory failed, fallback local wipe", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم حذف القسم محلياً بنجاح"
                    }
                }

                val currentList = _categories.value.filter { it.id != categoryId }
                _categories.value = currentList
                saveCategoriesCache(currentList)

                // Flush local orphaned service providers
                val cleanProvidersList = _serviceProviders.value.filter { it.categoryId != categoryId }
                _serviceProviders.value = cleanProvidersList
                saveProvidersCache(cleanProvidersList)

                fetchCategories()
                fetchServiceProviders()
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
    fun addServiceProvider(name: String, phone: String, categoryId: Int, rating: Double, imageUrl: String?, isActive: Boolean, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val provider = ServiceProvider(
                    name = name,
                    phone = phone,
                    categoryId = categoryId,
                    rating = rating,
                    imageUrl = if (imageUrl.isNull_or_Empty()) null else imageUrl,
                    isActive = isActive
                )

                try {
                    SupabaseClient.api.createServiceProvider(provider)
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote createServiceProvider failed, fallback local write", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم إضافة مقدم الخدمة محلياً"
                    }
                }

                // Update local configuration state lists
                val currentList = _serviceProviders.value.toMutableList()
                val nextId = (currentList.mapNotNull { it.id }.maxOrNull() ?: 2000) + 1
                currentList.add(provider.copy(id = nextId))
                _serviceProviders.value = currentList
                saveProvidersCache(currentList)

                fetchServiceProviders()
                handlerSuccessOnMain { onComplete(true) }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Add provider failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateServiceProvider(providerId: Int, name: String, phone: String, categoryId: Int, rating: Double, imageUrl: String?, isActive: Boolean, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                try {
                    val updates = mapOf(
                        "name" to name,
                        "phone" to phone,
                        "category_id" to categoryId,
                        "rating" to rating,
                        "image_url" to (if (imageUrl.isNull_or_Empty()) "" else imageUrl!!),
                        "is_active" to isActive
                    )
                    SupabaseClient.api.updateServiceProvider("eq.$providerId", updates)
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote updateServiceProvider failed, fallback local write", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم تعديل مقدم الخدمة محلياً"
                    }
                }

                val currentList = _serviceProviders.value.map {
                    if (it.id == providerId) {
                        it.copy(
                            name = name,
                            phone = phone,
                            categoryId = categoryId,
                            rating = rating,
                            imageUrl = if (imageUrl.isNull_or_Empty()) null else imageUrl,
                            isActive = isActive
                        )
                    } else it
                }
                _serviceProviders.value = currentList
                saveProvidersCache(currentList)

                fetchServiceProviders()
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
                try {
                    SupabaseClient.api.deleteServiceProvider("eq.$providerId")
                } catch (apiError: Exception) {
                    Log.e("DaliliViewModel", "Remote deleteServiceProvider failed, fallback local wipe", apiError)
                    handlerSuccessOnMain {
                        _errorMessage.value = "تم حذف مقدم الخدمة محلياً"
                    }
                }

                val currentList = _serviceProviders.value.filter { it.id != providerId }
                _serviceProviders.value = currentList
                saveProvidersCache(currentList)

                fetchServiceProviders()
                handlerSuccessOnMain { onComplete(true) }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Delete provider failed", e)
                handlerSuccessOnMain { onComplete(false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // LOCAL JSON PARSERS (Saves lists directly as json text)
    private fun saveCategoriesCache(list: List<Category>) {
        try {
            val json = categoriesAdapter.toJson(list)
            sharedPrefs.edit().putString("cache_categories", json).apply()
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Write categories cache failed", e)
        }
    }

    private fun loadCategoriesCache(): List<Category> {
        val json = sharedPrefs.getString("cache_categories", null)
        if (json != null) {
            try {
                return categoriesAdapter.fromJson(json) ?: emptyList()
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Read categories cache failed", e)
            }
        }
        return getDefaultCategories()
    }

    private fun saveProvidersCache(list: List<ServiceProvider>) {
        try {
            val json = providersAdapter.toJson(list)
            sharedPrefs.edit().putString("cache_providers", json).apply()
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Write providers cache failed", e)
        }
    }

    private fun loadProvidersCache(): List<ServiceProvider> {
        val json = sharedPrefs.getString("cache_providers", null)
        if (json != null) {
            try {
                return providersAdapter.fromJson(json) ?: emptyList()
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Read providers cache failed", e)
            }
        }
        return getDefaultProviders()
    }

    private fun saveAdminsCache(list: List<Admin>) {
        try {
            val json = adminsAdapter.toJson(list)
            sharedPrefs.edit().putString("cache_admins", json).apply()
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Write admins cache failed", e)
        }
    }

    private fun loadAdminsCache(): List<Admin> {
        val json = sharedPrefs.getString("cache_admins", null)
        if (json != null) {
            try {
                return adminsAdapter.fromJson(json) ?: emptyList()
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Read admins cache failed", e)
            }
        }
        return emptyList()
    }

    // Default Pre-loaded arabian services for a flawless first run offline!
    private fun getDefaultCategories(): List<Category> {
        return listOf(
            Category(id = 1001, nameAr = "خدمات الاتصالات", icon = "📱", orderIndex = 1),
            Category(id = 1002, nameAr = "الطوارئ العامة", icon = "⚡", orderIndex = 2),
            Category(id = 1003, nameAr = "الصحة والطب", icon = "🩺", orderIndex = 3),
            Category(id = 1004, nameAr = "سيارات وأجرة", icon = "🚕", orderIndex = 4),
            Category(id = 1005, nameAr = "الصيانة المنزلية", icon = "🛠️", orderIndex = 5)
        )
    }

    private fun getDefaultProviders(): List<ServiceProvider> {
        return listOf(
            ServiceProvider(id = 2001, name = "مؤسسة الاتصالات والإنترنت", phone = "777644670", categoryId = 1001, rating = 5.0, isActive = true),
            ServiceProvider(id = 2002, name = "طوارئ الكهرباء والمياه", phone = "191", categoryId = 1002, rating = 5.0, isActive = true),
            ServiceProvider(id = 2003, name = "الهلال الأحمر والإسعاف", phone = "199", categoryId = 1003, rating = 5.0, isActive = true),
            ServiceProvider(id = 2004, name = "تاكسي المشوار السريع", phone = "777644670", categoryId = 1004, rating = 4.5, isActive = true),
            ServiceProvider(id = 2005, name = "خدمات السباكة والكهرباء المنزلية", phone = "777644670", categoryId = 1005, rating = 4.8, isActive = true)
        )
    }

    // Hashing helper
    private fun hashPasswordHelper(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
            hash.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            password
        }
    }

    private fun String?.isNull_or_Empty(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
