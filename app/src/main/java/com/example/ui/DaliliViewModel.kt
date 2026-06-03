package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DaliliViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    init {
        // Enable offline persistence
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings

        // Initialize snapshot listeners for instant real-time synchronization
        setupCategoriesListener()
        setupProvidersListener()
        setupPendingProvidersListener()
        setupReviewsListener()
        setupSettingsListener()
        setupSupervisorsListener()
    }

    // STATE FLOWS
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers.asStateFlow()

    private val _pendingProviders = MutableStateFlow<List<Provider>>(emptyList())
    val pendingProviders: StateFlow<List<Provider>> = _pendingProviders.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _supervisors = MutableStateFlow<List<Supervisor>>(emptyList())
    val supervisors: StateFlow<List<Supervisor>> = _supervisors.asStateFlow()

    // AUTHENTICATION & SESSION STATE
    private val _currentRole = MutableStateFlow<String>("Guest") // "Guest", "Admin", "Supervisor"
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _currentUsername = MutableStateFlow<String>("")
    val currentUsername: StateFlow<String> = _currentUsername.asStateFlow()

    // NAVIGATION STATE
    private val _currentScreen = MutableStateFlow<String>("home") // home, login, register, category, detail, admin, secret
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String>("")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _selectedProviderId = MutableStateFlow<String>("")
    val selectedProviderId: StateFlow<String> = _selectedProviderId.asStateFlow()

    // SNAPSHOT LISTENERS FOR REAL-TIME SYNC
    private fun setupCategoriesListener() {
        db.collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<Category>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.id
                            val nameAr = doc.getString("nameAr") ?: ""
                            val nameEn = doc.getString("nameEn") ?: ""
                            val imageUrl = doc.getString("imageUrl") ?: ""
                            val sortOrder = doc.getLong("sortOrder")?.toInt() ?: 0
                            
                            // Parse list of subcategories
                            val subRaw = doc.get("subcategories") as? List<Map<String, Any>>
                            val subList = subRaw?.map { subMap ->
                                Subcategory(
                                    id = subMap["id"] as? String ?: "",
                                    nameAr = subMap["nameAr"] as? String ?: "",
                                    nameEn = subMap["nameEn"] as? String ?: ""
                                )
                            } ?: emptyList()

                            list.add(Category(id, nameAr, nameEn, imageUrl, sortOrder, subList))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _categories.value = list.sortedBy { it.sortOrder }
                }
            }
    }

    private fun setupProvidersListener() {
        db.collection("service_providers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<Provider>()
                    for (doc in snapshot.documents) {
                        try {
                            val provider = parseProvider(doc.id, doc.data)
                            if (provider != null) list.add(provider)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _providers.value = list
                }
            }
    }

    private fun setupPendingProvidersListener() {
        db.collection("pending_providers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<Provider>()
                    for (doc in snapshot.documents) {
                        try {
                            val provider = parseProvider(doc.id, doc.data)
                            if (provider != null) list.add(provider)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _pendingProviders.value = list
                }
            }
    }

    private fun setupReviewsListener() {
        db.collection("reviews")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<Review>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.id
                            val providerId = doc.getString("providerId") ?: ""
                            val userName = doc.getString("userName") ?: ""
                            val rating = doc.getDouble("rating")?.toFloat() ?: 5.0f
                            val comment = doc.getString("comment") ?: ""
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            list.add(Review(id, providerId, userName, rating, comment, timestamp))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _reviews.value = list
                }
            }
    }

    private fun setupSettingsListener() {
        db.collection("settings").document("general")
            .addSnapshotListener { doc, error ->
                if (error != null) return@addSnapshotListener
                if (doc != null && doc.exists()) {
                    try {
                        val appName = doc.getString("appName") ?: "دليلي - Dalili"
                        val primaryColorOption = doc.getString("primaryColorHex") ?: "#6200EE"
                        val secondaryColorOption = doc.getString("secondaryColorHex") ?: "#03DAC5"
                        val welcomeMessage = doc.getString("welcomeMessage") ?: "مرحباً بك في دليلي - دليل الموثوقين الموحد لجميع الخدمات المباشرة!"
                        val footerText = doc.getString("footerText") ?: "MAW 777644670"
                        val supportNumber = doc.getString("supportNumber") ?: "777644670"
                        val supportEmail = doc.getString("supportEmail") ?: "support@dalili.com"
                        val adminPasswordHex = doc.getString("adminPasswordHex") ?: "maher736462"

                        _appSettings.value = AppSettings(
                            appName = appName,
                            primaryColorHex = primaryColorOption,
                            secondaryColorHex = secondaryColorOption,
                            welcomeMessage = welcomeMessage,
                            footerText = footerText,
                            supportNumber = supportNumber,
                            supportEmail = supportEmail,
                            adminPasswordHex = adminPasswordHex
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    // Seed initial default settings if empty
                    seedDefaultSettings()
                }
            }
    }

    private fun setupSupervisorsListener() {
        db.collection("supervisors")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<Supervisor>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.id
                            val username = doc.getString("username") ?: ""
                            val password = doc.getString("password") ?: ""
                            list.add(Supervisor(id, username, password))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _supervisors.value = list
                }
            }
    }

    private fun parseProvider(id: String, data: Map<String, Any>?): Provider? {
        if (data == null) return null
        return Provider(
            id = id,
            name = data["name"] as? String ?: "",
            phone = data["phone"] as? String ?: "",
            categoryId = data["categoryId"] as? String ?: "",
            subcategoryId = data["subcategoryId"] as? String ?: "",
            workAddress = data["workAddress"] as? String ?: "",
            district = data["district"] as? String ?: "",
            gpsCoordinates = data["gpsCoordinates"] as? String ?: "",
            personalPhotoUrl = data["personalPhotoUrl"] as? String ?: "",
            idCardPhotoUrl = data["idCardPhotoUrl"] as? String ?: "",
            isPinned = data["pinned"] as? Boolean ?: (data["isPinned"] as? Boolean ?: false),
            isRecommended = data["recommended"] as? Boolean ?: (data["isRecommended"] as? Boolean ?: false),
            status = data["status"] as? String ?: "approved",
            rating = (data["rating"] as? Number)?.toFloat() ?: 5.0f,
            reviewCount = (data["reviewCount"] as? Number)?.toInt() ?: 0,
            rejectionReason = data["rejectionReason"] as? String ?: ""
        )
    }

    private fun seedDefaultSettings() {
        val defaultSettings = AppSettings()
        db.collection("settings").document("general").set(defaultSettings)
    }

    // AUTHENTICATION
    fun login(username: String, password: String): Boolean {
        if (username == "WAM2026" && password == _appSettings.value.adminPasswordHex) {
            _currentRole.value = "Admin"
            _currentUsername.value = username
            _currentScreen.value = "admin"
            return true
        }

        // Check if supervisor
        val matchedSupervisor = _supervisors.value.any { it.username == username && it.password == password }
        if (matchedSupervisor) {
            _currentRole.value = "Supervisor"
            _currentUsername.value = username
            _currentScreen.value = "admin"
            return true
        }

        return false
    }

    fun logout() {
        _currentRole.value = "Guest"
        _currentUsername.value = ""
        _currentScreen.value = "home"
    }

    // NAVIGATION
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun navigateToCategory(catId: String) {
        _selectedCategoryId.value = catId
        _currentScreen.value = "category"
    }

    fun navigateToProvider(provId: String) {
        _selectedProviderId.value = provId
        _currentScreen.value = "detail"
    }

    // ACTIONS: CATEGORIES
    fun addMainCategory(nameAr: String, nameEn: String, imageUrl: String, sortOrder: Int) {
        val id = db.collection("categories").document().id
        val cat = Category(id, nameAr, nameEn, imageUrl, sortOrder, emptyList())
        db.collection("categories").document(id).set(cat)
    }

    fun addSubcategory(categoryId: String, nameAr: String, nameEn: String) {
        val category = _categories.value.find { it.id == categoryId } ?: return
        val newSubList = category.subcategories.toMutableList().apply {
            add(Subcategory(id = "sub_${System.currentTimeMillis()}", nameAr = nameAr, nameEn = nameEn))
        }
        db.collection("categories").document(categoryId).update("subcategories", newSubList)
    }

    fun deleteCategory(categoryId: String) {
        db.collection("categories").document(categoryId).delete()
    }

    // ACTIONS: PROVIDERS DIRECT ADD
    fun addDirectProvider(
        name: String,
        phone: String,
        categoryId: String,
        subcategoryId: String,
        workAddress: String,
        district: String,
        gpsCoordinates: String,
        personalPhotoUrl: String
    ) {
        val id = "prov_${System.currentTimeMillis()}"
        val prov = Provider(
            id = id,
            name = name,
            phone = phone,
            categoryId = categoryId,
            subcategoryId = subcategoryId,
            workAddress = workAddress,
            district = district,
            gpsCoordinates = gpsCoordinates,
            personalPhotoUrl = if (personalPhotoUrl.isBlank()) "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150" else personalPhotoUrl,
            status = "approved"
        )
        db.collection("service_providers").document(id).set(prov)
    }

    // ACTIONS: REGISTER PROFESSIONAL REQ (PENDING)
    fun submitProfessionalRequest(
        name: String,
        phone: String,
        categoryId: String,
        workAddress: String,
        district: String,
        gpsCoordinates: String,
        personalPhotoUrl: String,
        idCardPhotoUrl: String
    ) {
        val id = "pending_${System.currentTimeMillis()}"
        val prov = Provider(
            id = id,
            name = name,
            phone = phone,
            categoryId = categoryId,
            workAddress = workAddress,
            district = district,
            gpsCoordinates = gpsCoordinates,
            personalPhotoUrl = if (personalPhotoUrl.isBlank()) "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150" else personalPhotoUrl,
            idCardPhotoUrl = if (idCardPhotoUrl.isBlank()) "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=300" else idCardPhotoUrl,
            status = "pending"
        )
        db.collection("pending_providers").document(id).set(prov)
    }

    // ACTIONS: MANAGE REGISTRATION REQUESTS
    fun acceptPendingRequest(requestId: String) {
        val req = _pendingProviders.value.find { it.id == requestId } ?: return
        val approvedId = "prov_${System.currentTimeMillis()}"
        val approvedProv = req.copy(id = approvedId, status = "approved")
        
        // Save to service providers
        db.collection("service_providers").document(approvedId).set(approvedProv)
            .addOnSuccessListener {
                // Delete from pending
                db.collection("pending_providers").document(requestId).delete()
            }
    }

    fun rejectPendingRequest(requestId: String, reason: String) {
        db.collection("pending_providers").document(requestId).delete()
        // Alternatively, update status to rejected so applicant can view. Let's delete to satisfy the workflow fully.
    }

    // ACTIONS: PIN & RECOMMEND
    fun togglePin(providerId: String) {
        val provider = _providers.value.find { it.id == providerId } ?: return
        db.collection("service_providers").document(providerId).update("isPinned", !provider.isPinned)
    }

    fun toggleRecommend(providerId: String) {
        val provider = _providers.value.find { it.id == providerId } ?: return
        db.collection("service_providers").document(providerId).update("isRecommended", !provider.isRecommended)
    }

    fun deleteProvider(providerId: String) {
        db.collection("service_providers").document(providerId).delete()
    }

    // ACTIONS: REVIEWS
    fun addReview(providerId: String, userName: String, rating: Float, comment: String) {
        val reviewId = "rev_${System.currentTimeMillis()}"
        val newReview = Review(reviewId, providerId, userName, rating, comment, System.currentTimeMillis())
        db.collection("reviews").document(reviewId).set(newReview)
            .addOnSuccessListener {
                // Recompute provider average ratings
                recomputeStats(providerId)
            }
    }

    private fun recomputeStats(providerId: String) {
        db.collection("reviews").whereEqualTo("providerId", providerId).get()
            .addOnSuccessListener { qSnap ->
                val reviewsList = qSnap.documents.mapNotNull { doc ->
                    val r = doc.getDouble("rating")?.toFloat()
                    r
                }
                if (reviewsList.isNotEmpty()) {
                    val count = reviewsList.size
                    val avg = reviewsList.average().toFloat()
                    db.collection("service_providers").document(providerId).update(
                        mapOf(
                            "rating" to avg,
                            "reviewCount" to count
                        )
                    )
                }
            }
    }

    // ACTIONS: SUPERVISORS
    fun addSupervisor(username: String, password: String) {
        val id = "sup_${System.currentTimeMillis()}"
        val supervisor = Supervisor(id, username, password)
        db.collection("supervisors").document(id).set(supervisor)
    }

    fun deleteSupervisor(id: String) {
        db.collection("supervisors").document(id).delete()
    }

    // SECRET SETTINGS & GATEWAY ACTION
    fun updateSecretSettings(
        appName: String,
        primaryHex: String,
        secondaryHex: String,
        footerText: String,
        welcomeMsg: String,
        supportNum: String,
        supportEmail: String,
        adminPass: String
    ) {
        val update = mapOf(
            "appName" to appName,
            "primaryColorHex" to primaryHex,
            "secondaryColorHex" to secondaryHex,
            "footerText" to footerText,
            "welcomeMessage" to welcomeMsg,
            "supportNumber" to supportNum,
            "supportEmail" to supportEmail,
            "adminPasswordHex" to adminPass
        )
        db.collection("settings").document("general").set(update)
    }

    fun triggerRefresh() {
        // Trigger manual refresh by forcing snapshot sync update (or simple simulation success)
        setupCategoriesListener()
        setupProvidersListener()
        setupPendingProvidersListener()
        setupReviewsListener()
        setupSettingsListener()
        setupSupervisorsListener()
    }
}
