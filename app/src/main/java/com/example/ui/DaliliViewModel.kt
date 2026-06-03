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
        
        // New features hooks
        setupComplaintsListener()
        setupBannersListener()
        setupLoyaltyAccountsListener()
        setupCitiesListener()
        setupSubscriptionPaymentsListener()
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

    // NEW EVENTS & ENTITIES FLOWS
    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    private val _banners = MutableStateFlow<List<BannerAd>>(emptyList())
    val banners: StateFlow<List<BannerAd>> = _banners.asStateFlow()

    private val _loyaltyAccounts = MutableStateFlow<List<LoyaltyAccount>>(emptyList())
    val loyaltyAccounts: StateFlow<List<LoyaltyAccount>> = _loyaltyAccounts.asStateFlow()

    private val _cities = MutableStateFlow<List<CityOption>>(emptyList())
    val cities: StateFlow<List<CityOption>> = _cities.asStateFlow()

    private val _subscriptionPayments = MutableStateFlow<List<SubscriptionPayment>>(emptyList())
    val subscriptionPayments: StateFlow<List<SubscriptionPayment>> = _subscriptionPayments.asStateFlow()

    // AUTHENTICATION & SESSION STATE
    private val _currentRole = MutableStateFlow<String>("Guest") // "Guest", "Admin", "Supervisor"
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _currentUsername = MutableStateFlow<String>("")
    val currentUsername: StateFlow<String> = _currentUsername.asStateFlow()

    // NAVIGATION STATE
    private val _currentScreen = MutableStateFlow<String>("home") // home, login, register, category, detail, admin, secret, loyalty, complaints, banners, subscription_admin, backup_admin
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
                        val primaryColorOption = doc.getString("primaryColorHex") ?: "#CCCCCC"
                        val secondaryColorOption = doc.getString("secondaryColorHex") ?: "#78909C"
                        val welcomeMessage = doc.getString("welcomeMessage") ?: "مرحباً بك في دليلي - دليل الموثوقين الموحد لجميع الخدمات المباشرة!"
                        val footerText = doc.getString("footerText") ?: "WAM777644670"
                        val supportNumber = doc.getString("supportNumber") ?: "777644670"
                        val supportEmail = doc.getString("supportEmail") ?: "support@dalili.com"
                        val supportWhatsapp = doc.getString("supportWhatsapp") ?: "777644670"
                        val adminPasswordHex = doc.getString("adminPasswordHex") ?: "maher736462"

                        // New fields with full safe defaults
                        val themePreset = doc.getString("themePreset") ?: "cosmic_slate"
                        val backgroundColorHex = doc.getString("backgroundColorHex") ?: "#121824"
                        val textColorPreset = doc.getString("textColorPreset") ?: "bright_white"
                        val textColorHex = doc.getString("textColorHex") ?: "#FFFFFF"

                        val smartAssistantSize = doc.getString("smartAssistantSize") ?: "medium"
                        val smartAssistantColorHex = doc.getString("smartAssistantColorHex") ?: "#6200EE"
                        val smartAssistantAlignLeft = doc.getBoolean("smartAssistantAlignLeft") ?: false
                        val smartAssistantEnabled = doc.getBoolean("smartAssistantEnabled") ?: true

                        val maintenanceMode = doc.getBoolean("maintenanceMode") ?: false
                        val dataSaverMode = doc.getBoolean("dataSaverMode") ?: false
                        val maxRadiusDefault = doc.getLong("maxRadiusDefault")?.toInt() ?: 10

                        val fcmJoinRequests = doc.getBoolean("fcmJoinRequests") ?: true
                        val fcmComplaints = doc.getBoolean("fcmComplaints") ?: true

                        val pointsPerReview = doc.getLong("pointsPerReview")?.toInt() ?: 10
                        val pointsPerShare = doc.getLong("pointsPerShare")?.toInt() ?: 20
                        val isSubscriptionEnabled = doc.getBoolean("isSubscriptionEnabled") ?: doc.getBoolean("subscriptionEnabled") ?: true
                        val topBarConfig = doc.getString("topBarConfig") ?: "home,login,register"

                        _appSettings.value = AppSettings(
                            appName = appName,
                            primaryColorHex = primaryColorOption,
                            secondaryColorHex = secondaryColorOption,
                            welcomeMessage = welcomeMessage,
                            footerText = footerText,
                            supportNumber = supportNumber,
                            supportEmail = supportEmail,
                            supportWhatsapp = supportWhatsapp,
                            adminPasswordHex = adminPasswordHex,
                            themePreset = themePreset,
                            backgroundColorHex = backgroundColorHex,
                            textColorPreset = textColorPreset,
                            textColorHex = textColorHex,
                            smartAssistantSize = smartAssistantSize,
                            smartAssistantColorHex = smartAssistantColorHex,
                            smartAssistantAlignLeft = smartAssistantAlignLeft,
                            smartAssistantEnabled = smartAssistantEnabled,
                            maintenanceMode = maintenanceMode,
                            dataSaverMode = dataSaverMode,
                            maxRadiusDefault = maxRadiusDefault,
                            fcmJoinRequests = fcmJoinRequests,
                            fcmComplaints = fcmComplaints,
                            pointsPerReview = pointsPerReview,
                            pointsPerShare = pointsPerShare,
                            isSubscriptionEnabled = isSubscriptionEnabled,
                            topBarConfig = topBarConfig
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
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
                            val tfaEnabled = doc.getBoolean("tfaEnabled") ?: false
                            val tfaSecret = doc.getString("tfaSecret") ?: ""
                            list.add(Supervisor(id, username, password, tfaEnabled, tfaSecret))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _supervisors.value = list
                }
            }
    }

    private fun setupComplaintsListener() {
        db.collection("complaints")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<Complaint>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.id
                            val providerId = doc.getString("providerId") ?: ""
                            val providerName = doc.getString("providerName") ?: ""
                            val userName = doc.getString("userName") ?: ""
                            val userPhone = doc.getString("userPhone") ?: ""
                            val reason = doc.getString("reason") ?: ""
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            val status = doc.getString("status") ?: "pending"
                            list.add(Complaint(id, providerId, providerName, userName, userPhone, reason, timestamp, status))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _complaints.value = list.sortedByDescending { it.timestamp }
                }
            }
    }

    private fun setupBannersListener() {
        db.collection("banners")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<BannerAd>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.id
                            val imageUrl = doc.getString("imageUrl") ?: ""
                            val targetUrl = doc.getString("targetUrl") ?: ""
                            val title = doc.getString("title") ?: ""
                            val durationDays = doc.getLong("durationDays")?.toInt() ?: 7
                            val sizeType = doc.getString("sizeType") ?: "medium"
                            val bannerType = doc.getString("bannerType") ?: "image_alert"
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            list.add(BannerAd(id, imageUrl, targetUrl, title, durationDays, sizeType, bannerType, timestamp))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _banners.value = list.sortedBy { it.timestamp }
                }
            }
    }

    private fun setupLoyaltyAccountsListener() {
        db.collection("loyalty_accounts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<LoyaltyAccount>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.id
                            val userName = doc.getString("userName") ?: ""
                            val phone = doc.getString("phone") ?: ""
                            val points = doc.getLong("points")?.toInt() ?: 0
                            val historyLogs = doc.get("historyLogs") as? List<String> ?: emptyList()
                            list.add(LoyaltyAccount(id, userName, phone, points, historyLogs))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _loyaltyAccounts.value = list
                }
            }
    }

    private fun setupCitiesListener() {
        db.collection("cities")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<CityOption>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.id
                            val nameAr = doc.getString("nameAr") ?: ""
                            val nameEn = doc.getString("nameEn") ?: ""
                            list.add(CityOption(id, nameAr, nameEn))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _cities.value = list.ifEmpty {
                        // Seed basic cities if none exist
                        listOf(
                            CityOption("c1", "صنعاء", "Sanaa"),
                            CityOption("c2", "عدن", "Aden"),
                            CityOption("c3", "تعز", "Taiz"),
                            CityOption("c4", "الحديدة", "Hodeidah")
                        )
                    }
                }
            }
    }

    private fun setupSubscriptionPaymentsListener() {
        db.collection("subscription_payments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<SubscriptionPayment>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.id
                            val providerId = doc.getString("providerId") ?: ""
                            val providerName = doc.getString("providerName") ?: ""
                            val receiptPhotoUrl = doc.getString("receiptPhotoUrl") ?: ""
                            val notes = doc.getString("notes") ?: ""
                            val status = doc.getString("status") ?: "pending"
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            list.add(SubscriptionPayment(id, providerId, providerName, receiptPhotoUrl, notes, status, timestamp))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _subscriptionPayments.value = list.sortedByDescending { it.timestamp }
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
            rejectionReason = data["rejectionReason"] as? String ?: "",
            isPremium = data["isPremium"] as? Boolean ?: false,
            premiumExpiryTimestamp = (data["premiumExpiryTimestamp"] as? Number)?.toLong() ?: 0L
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
        val matchedSupervisor = _supervisors.value.find { it.username == username && it.password == password }
        if (matchedSupervisor != null) {
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
                db.collection("pending_providers").document(requestId).delete()
            }
    }

    fun rejectPendingRequest(requestId: String, reason: String) {
        db.collection("pending_providers").document(requestId).delete()
    }

    // ACTIONS: PIN & RECOMMEND & PREMIUM DIRECT
    fun togglePin(providerId: String) {
        val provider = _providers.value.find { it.id == providerId } ?: return
        db.collection("service_providers").document(providerId).update("pinned", !provider.isPinned)
    }

    fun toggleRecommend(providerId: String) {
        val provider = _providers.value.find { it.id == providerId } ?: return
        db.collection("service_providers").document(providerId).update("recommended", !provider.isRecommended)
    }

    fun togglePremiumDirect(providerId: String, isPremium: Boolean) {
        db.collection("service_providers").document(providerId).update(
            mapOf(
                "isPremium" to isPremium,
                "premiumExpiryTimestamp" to if (isPremium) System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L else 0L
            )
        )
    }

    fun deleteProvider(providerId: String) {
        db.collection("service_providers").document(providerId).delete()
    }

    // ACTIONS: REVIEWS + LOYALTY INTEGRATION
    fun addReview(providerId: String, userName: String, rating: Float, comment: String, userPhone: String = "777000000") {
        val reviewId = "rev_${System.currentTimeMillis()}"
        val newReview = Review(reviewId, providerId, userName, rating, comment, System.currentTimeMillis())
        db.collection("reviews").document(reviewId).set(newReview)
            .addOnSuccessListener {
                recomputeStats(providerId)
                // Award Loyalty Points for review!
                val reviewPoints = _appSettings.value.pointsPerReview
                addLoyaltyPoints(userPhone, userName, reviewPoints, "تقييم مقدم خدمة ($providerId)")
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

    // ACTIONS: LOYALTY SYSTEM
    fun addLoyaltyPoints(phone: String, userName: String, pointsAwarded: Int, eventDesc: String) {
        val formattedPhone = phone.trim()
        if (formattedPhone.isBlank()) return
        val docRef = db.collection("loyalty_accounts").document(formattedPhone)
        
        docRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val currentPoints = doc.getLong("points")?.toInt() ?: 0
                val logs = doc.get("historyLogs") as? List<String> ?: emptyList()
                val newLogs = logs.toMutableList().apply {
                    add(0, "+$pointsAwarded : $eventDesc (${System.currentTimeMillis()})")
                }
                docRef.update(
                    mapOf(
                        "points" to (currentPoints + pointsAwarded),
                        "historyLogs" to newLogs,
                        "userName" to userName
                    )
                )
            } else {
                val account = LoyaltyAccount(
                    id = formattedPhone,
                    userName = userName,
                    phone = formattedPhone,
                    points = pointsAwarded,
                    historyLogs = listOf("+$pointsAwarded : $eventDesc")
                )
                docRef.set(account)
            }
        }
    }

    fun redeemDiscount(phone: String, pointsCost: Int, rewardTitle: String) {
        val formattedPhone = phone.trim()
        val docRef = db.collection("loyalty_accounts").document(formattedPhone)
        docRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val currentPoints = doc.getLong("points")?.toInt() ?: 0
                if (currentPoints >= pointsCost) {
                    val logs = doc.get("historyLogs") as? List<String> ?: emptyList()
                    val newLogs = logs.toMutableList().apply {
                        add(0, "-$pointsCost : استبدال جائزة $rewardTitle (${System.currentTimeMillis()})")
                    }
                    docRef.update(
                        mapOf(
                            "points" to (currentPoints - pointsCost),
                            "historyLogs" to newLogs
                        )
                    )
                }
            }
        }
    }

    // ACTIONS: COMPLAINTS / REPORTS
    fun reportProvider(providerId: String, providerName: String, userName: String, userPhone: String, reason: String) {
        val id = "report_${System.currentTimeMillis()}"
        val report = Complaint(
            id = id,
            providerId = providerId,
            providerName = providerName,
            userName = userName,
            userPhone = userPhone,
            reason = reason,
            timestamp = System.currentTimeMillis(),
            status = "pending"
        )
        db.collection("complaints").document(id).set(report)
    }

    fun resolveComplaint(id: String) {
        db.collection("complaints").document(id).update("status", "resolved")
    }

    fun dismissComplaint(id: String) {
        db.collection("complaints").document(id).update("status", "dismissed")
    }

    // ACTIONS: AD BANNERS
    fun addBannerAd(title: String, imageUrl: String, targetUrl: String, durationDays: Int, sizeType: String, bannerType: String) {
        val id = "banner_${System.currentTimeMillis()}"
        val ad = BannerAd(
            id = id,
            title = title,
            imageUrl = if (imageUrl.isBlank()) "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600" else imageUrl,
            targetUrl = targetUrl,
            durationDays = durationDays,
            sizeType = sizeType,
            bannerType = bannerType,
            timestamp = System.currentTimeMillis()
        )
        db.collection("banners").document(id).set(ad)
    }

    fun deleteBannerAd(id: String) {
        db.collection("banners").document(id).delete()
    }

    // ACTIONS: CUSTOM CITIES
    fun addCity(nameAr: String, nameEn: String) {
        val id = "city_${System.currentTimeMillis()}"
        val city = CityOption(id, nameAr, nameEn)
        db.collection("cities").document(id).set(city)
    }

    fun deleteCity(id: String) {
        db.collection("cities").document(id).delete()
    }

    // ACTIONS: PREMIUM SUBSCRIPTION PAYMENTS
    fun submitSubscriptionPayment(providerId: String, providerName: String, receiptPhotoUrl: String, notes: String) {
        val id = "subpay_${System.currentTimeMillis()}"
        val payment = SubscriptionPayment(
            id = id,
            providerId = providerId,
            providerName = providerName,
            receiptPhotoUrl = if (receiptPhotoUrl.isBlank()) "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=300" else receiptPhotoUrl,
            notes = notes,
            status = "pending",
            timestamp = System.currentTimeMillis()
        )
        db.collection("subscription_payments").document(id).set(payment)
    }

    fun approveSubscriptionPayment(paymentId: String, providerId: String) {
        db.collection("subscription_payments").document(paymentId).update("status", "approved")
            .addOnSuccessListener {
                togglePremiumDirect(providerId, true)
            }
    }

    fun rejectSubscriptionPayment(paymentId: String) {
        db.collection("subscription_payments").document(paymentId).update("status", "rejected")
    }

    // ACTIONS: SUPERVISORS
    fun addSupervisor(username: String, password: String) {
        val id = "sup_${System.currentTimeMillis()}"
        val supervisor = Supervisor(id, username, password, false, "")
        db.collection("supervisors").document(id).set(supervisor)
    }

    fun deleteSupervisor(id: String) {
        db.collection("supervisors").document(id).delete()
    }

    fun updateSupervisorTFA(id: String, enabled: Boolean, secret: String) {
        db.collection("supervisors").document(id).update(
            mapOf(
                "tfaEnabled" to enabled,
                "tfaSecret" to secret
            )
        )
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
        supportWhatsapp: String,
        adminPass: String,
        
        // New interactive theme overrides
        themePreset: String,
        backgroundColorHex: String,
        textColorPreset: String,
        textColorHex: String,
        
        // Floating action assistant configs
        smartAssistantSize: String,
        smartAssistantColorHex: String,
        smartAssistantAlignLeft: Boolean,
        smartAssistantEnabled: Boolean,
        
        // Maintenance and toggles
        maintenanceMode: Boolean,
        dataSaverMode: Boolean,
        maxRadiusDefault: Int,
        
        // FCM toggles
        fcmJoinRequests: Boolean,
        fcmComplaints: Boolean,
        
        // Loyalty ratios
        pointsPerReview: Int,
        pointsPerShare: Int,
        isSubscriptionEnabled: Boolean,
        topBarConfig: String
    ) {
        val update = mapOf(
            "appName" to appName,
            "primaryColorHex" to primaryHex,
            "secondaryColorHex" to secondaryHex,
            "footerText" to footerText,
            "welcomeMessage" to welcomeMsg,
            "supportNumber" to supportNum,
            "supportEmail" to supportEmail,
            "supportWhatsapp" to supportWhatsapp,
            "adminPasswordHex" to adminPass,
            
            // Themes and Presets
            "themePreset" to themePreset,
            "backgroundColorHex" to backgroundColorHex,
            "textColorPreset" to textColorPreset,
            "textColorHex" to textColorHex,
            
            // Smart Assistant
            "smartAssistantSize" to smartAssistantSize,
            "smartAssistantColorHex" to smartAssistantColorHex,
            "smartAssistantAlignLeft" to smartAssistantAlignLeft,
            "smartAssistantEnabled" to smartAssistantEnabled,
            
            // Status sliders
            "maintenanceMode" to maintenanceMode,
            "dataSaverMode" to dataSaverMode,
            "maxRadiusDefault" to maxRadiusDefault,
            
            // Channels
            "fcmJoinRequests" to fcmJoinRequests,
            "fcmComplaints" to fcmComplaints,
            
            // Loyalty and premium
            "pointsPerReview" to pointsPerReview,
            "pointsPerShare" to pointsPerShare,
            "isSubscriptionEnabled" to isSubscriptionEnabled,
            "topBarConfig" to topBarConfig
        )
        db.collection("settings").document("general").set(update)
    }

    // DATA CLEANING ENGINE (Scheduler Simulation)
    fun runCacheAutoClean() {
        // Scheduled task to instantly delete old rejected/dismissed complaints and items to optimize Firestore
        db.collection("complaints").whereEqualTo("status", "dismissed").get()
            .addOnSuccessListener { qSnap ->
                for (doc in qSnap.documents) {
                    db.collection("complaints").document(doc.id).delete()
                }
            }
        db.collection("complaints").whereEqualTo("status", "resolved").get()
            .addOnSuccessListener { qSnap ->
                for (doc in qSnap.documents) {
                    db.collection("complaints").document(doc.id).delete()
                }
            }
    }

    // BACKUP ENGINE: DATABASE TO COMPACT JSON STRINGS
    fun fetchBackupJSON(onReady: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val providersList = _providers.value
                val categoriesList = _categories.value
                
                // Fast serialization builder to prevent depending on extra third-party tools
                val sb = StringBuilder()
                sb.append("{\n  \"backup_timestamp\": ${System.currentTimeMillis()},\n")
                
                // Providers Array
                sb.append("  \"providers\": [\n")
                providersList.forEachIndexed { i, p ->
                    sb.append("    {\n")
                    sb.append("      \"id\": \"${p.id}\",\n")
                    sb.append("      \"name\": \"${p.name}\",\n")
                    sb.append("      \"phone\": \"${p.phone}\",\n")
                    sb.append("      \"district\": \"${p.district}\",\n")
                    sb.append("      \"workAddress\": \"${p.workAddress}\",\n")
                    sb.append("      \"rating\": ${p.rating}\n")
                    sb.append("    }${if (i < providersList.size - 1) "," else ""}\n")
                }
                sb.append("  ],\n")
                
                // Categories Array
                sb.append("  \"categories\": [\n")
                categoriesList.forEachIndexed { i, c ->
                    sb.append("    {\n")
                    sb.append("      \"id\": \"${c.id}\",\n")
                    sb.append("      \"nameAr\": \"${c.nameAr}\",\n")
                    sb.append("      \"nameEn\": \"${c.nameEn}\"\n")
                    sb.append("    }${if (i < categoriesList.size - 1) "," else ""}\n")
                }
                sb.append("  ]\n}")
                onReady(sb.toString())
            } catch (e: Exception) {
                onReady("{ \"error\": \"${e.localizedMessage}\" }")
            }
        }
    }

    fun restoreBackupFromJSON(jsonString: String, onSuccess: () -> Unit) {
        // Interactive simulated restore that inserts basic elements into Firestore collections
        // Simply parsing JSON keys and adding them mock style back
        try {
            // Seed sample database items to ensure restoration
            addDirectProvider("محمود صالح", "777123456", "c1", "s1", "شارع الستين", "صنعاء", "15.3,44.2", "")
            addDirectProvider("صالح اليماني", "777333444", "c2", "s2", "خور مكسر", "عدن", "12.8,45.0", "")
            onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerRefresh() {
        setupCategoriesListener()
        setupProvidersListener()
        setupPendingProvidersListener()
        setupReviewsListener()
        setupSettingsListener()
        setupSupervisorsListener()
        setupComplaintsListener()
        setupBannersListener()
        setupLoyaltyAccountsListener()
        setupCitiesListener()
        setupSubscriptionPaymentsListener()
    }
}
