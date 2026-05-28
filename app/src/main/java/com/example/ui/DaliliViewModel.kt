package com.example.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
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

        // Initialize Realtime WebSocket Sync
        realtimeClient = RealtimeClient {
            refreshDataSilent()
        }
        realtimeClient?.start()

        // Initial Data Fetch
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
                _errorMessage.value = "خطأ في الاتصال بالخادم: ${e.localizedMessage}"
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
                Log.e("DaliliViewModel", "Silent refresh failed", e)
            }
        }
    }

    private suspend fun fetchCategories() {
        try {
            val fetched = SupabaseClient.api.getCategories()
            _categories.value = fetched.sortedBy { it.orderIndex }
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Failed to get categories", e)
            throw e
        }
    }

    private suspend fun fetchServiceProviders() {
        try {
            val fetched = SupabaseClient.api.getServiceProviders()
            _serviceProviders.value = fetched
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Failed to get service providers", e)
            throw e
        }
    }

    private suspend fun fetchAdmins() {
        try {
            val fetched = SupabaseClient.api.getAdmins()
            _admins.value = fetched
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
                // Hardcoded fallback override for Super Admin as specified in requirements
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
                    handlerSuccessOnMain({ onResult(true, null) })
                    return@launch
                }

                // Query database
                val adminsFromDb = SupabaseClient.api.getAdmins()
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
                        handlerSuccessOnMain({ onResult(true, null) })
                    } else {
                        handlerSuccessOnMain({ onResult(false, "كلمة المرور غير صحيحة") })
                    }
                } else {
                    handlerSuccessOnMain({ onResult(false, "اسم المستخدم غير موجود") })
                }
            } catch (e: Exception) {
                Log.e("DaliliViewModel", "Login error", e)
                handlerSuccessOnMain({ onResult(false, "خطأ بالاتصال: ${e.localizedMessage}") })
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
                // Hash the password by default to store securely
                val hashed = hashPasswordHelper(passwordInput)
                val newAdmin = Admin(
                    username = usernameInput,
                    passwordHash = hashed,
                    role = "admin" // standard admin
                )
                SupabaseClient.api.createAdmin(newAdmin)
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
                val updates = mapOf("password_hash" to hashed)
                val response = SupabaseClient.api.updateAdmin("eq.$adminId", updates)
                if (response.isSuccessful) {
                    fetchAdmins()
                    handlerSuccessOnMain { onComplete(true) }
                } else {
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
                val response = SupabaseClient.api.deleteAdmin("eq.$adminId")
                if (response.isSuccessful) {
                    fetchAdmins()
                    handlerSuccessOnMain { onComplete(true) }
                } else {
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
                SupabaseClient.api.createCategory(category)
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
                val updates = mapOf(
                    "name_ar" to nameAr,
                    "icon" to icon,
                    "order_index" to orderIndex
                )
                val response = SupabaseClient.api.updateCategory("eq.$categoryId", updates)
                if (response.isSuccessful) {
                    fetchCategories()
                    handlerSuccessOnMain { onComplete(true) }
                } else {
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
                val response = SupabaseClient.api.deleteCategory("eq.$categoryId")
                if (response.isSuccessful) {
                    fetchCategories()
                    // Delete orphaned providers as helper or let Cascade handle it
                    fetchServiceProviders()
                    handlerSuccessOnMain { onComplete(true) }
                } else {
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
                SupabaseClient.api.createServiceProvider(provider)
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
                val updates = mapOf(
                    "name" to name,
                    "phone" to phone,
                    "category_id" to categoryId,
                    "rating" to rating,
                    "image_url" to (if (imageUrl.isNull_or_Empty()) "" else imageUrl!!),
                    "is_active" to isActive
                )
                val response = SupabaseClient.api.updateServiceProvider("eq.$providerId", updates)
                if (response.isSuccessful) {
                    fetchServiceProviders()
                    handlerSuccessOnMain { onComplete(true) }
                } else {
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
                val response = SupabaseClient.api.deleteServiceProvider("eq.$providerId")
                if (response.isSuccessful) {
                    fetchServiceProviders()
                    handlerSuccessOnMain { onComplete(true) }
                } else {
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

    // Hashing helper
    private fun hashPasswordHelper(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
            hash.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            password // Fallback to plain in case of weird device failures
        }
    }

    private fun String?.isNull_or_Empty(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
