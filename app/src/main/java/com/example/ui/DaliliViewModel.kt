package com.example.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestoreSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

class DaliliViewModel : ViewModel() {

    private val db: FirebaseFirestore? by lazy {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val settings = firestoreSettings {
                isPersistenceEnabled = true
            }
            firestore.firestoreSettings = settings
            firestore
        } catch (e: Exception) {
            Log.e("Firebase", "Firestore init error, offline backup activated: ${e.message}")
            null
        }
    }

    // Listener reference tracking for cleaning up
    private val registrations = mutableListOf<ListenerRegistration>()

    // Core States
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _providers = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val providers: StateFlow<List<ServiceProvider>> = _providers.asStateFlow()

    private val _pendingProviders = MutableStateFlow<List<PendingProvider>>(emptyList())
    val pendingProviders: StateFlow<List<PendingProvider>> = _pendingProviders.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chats: StateFlow<List<ChatMessage>> = _chats.asStateFlow()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    private val _serviceOrders = MutableStateFlow<List<ServiceOrder>>(emptyList())
    val serviceOrders: StateFlow<List<ServiceOrder>> = _serviceOrders.asStateFlow()

    // Offline seeding backup list
    private var isUsingOfflineMock = false

    init {
        setupSnapshotListeners()
    }

    private fun setupSnapshotListeners() {
        val firestore = db
        if (firestore == null) {
            activateBackupStaticData()
            return
        }

        try {
            // 1. Settings Listener
            val settingsRef = firestore.collection("settings").document("global")
            val lSettings = settingsRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firebase", "Settings error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val rawObj = snapshot.toObject(AppSettings::class.java)
                    if (rawObj != null) {
                        _settings.value = rawObj
                    }
                } else {
                    // Seed initial setting document
                    saveSettings(AppSettings())
                }
            }
            registrations.add(lSettings)

            // 2. Categories Listener
            val lCategories = firestore.collection("categories")
                .orderBy("order")
                .addSnapshotListener { snapshots, error ->
                    if (snapshots != null) {
                        val list = snapshots.toObjects(Category::class.java)
                        _categories.value = list
                        if (list.isEmpty()) {
                            seedDefaultCategories()
                        }
                    }
                }
            registrations.add(lCategories)

            // 3. Service Providers Listener
            val lProviders = firestore.collection("service_providers")
                .addSnapshotListener { snapshots, error ->
                    if (snapshots != null) {
                        val list = snapshots.toObjects(ServiceProvider::class.java)
                        _providers.value = list
                        if (list.isEmpty()) {
                            seedDefaultProviders()
                        }
                    }
                }
            registrations.add(lProviders)

            // 4. Pending Providers Listener
            val lPending = firestore.collection("pending_providers")
                .addSnapshotListener { snapshots, error ->
                    if (snapshots != null) {
                        _pendingProviders.value = snapshots.toObjects(PendingProvider::class.java)
                    }
                }
            registrations.add(lPending)

            // 5. Reviews Listener
            val lReviews = firestore.collection("reviews")
                .addSnapshotListener { snapshots, error ->
                    if (snapshots != null) {
                        _reviews.value = snapshots.toObjects(Review::class.java)
                    }
                }
            registrations.add(lReviews)

            // 6. Chats Listener
            val lChats = firestore.collection("chats")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, error ->
                    if (snapshots != null) {
                        _chats.value = snapshots.toObjects(ChatMessage::class.java)
                    }
                }
            registrations.add(lChats)

            // 7. Banners Listener
            val lBanners = firestore.collection("banners")
                .addSnapshotListener { snapshots, error ->
                    if (snapshots != null) {
                        _banners.value = snapshots.toObjects(Banner::class.java)
                        if (snapshots.isEmpty) {
                            seedDefaultBanners()
                        }
                    }
                }
            registrations.add(lBanners)

            // 8. Complaints Listener
            val lComplaints = firestore.collection("complaints")
                .addSnapshotListener { snapshots, error ->
                    if (snapshots != null) {
                        _complaints.value = snapshots.toObjects(Complaint::class.java)
                    }
                }
            registrations.add(lComplaints)

            // 9. Service Orders List Listener
            val lOrders = firestore.collection("service_orders")
                .addSnapshotListener { snapshots, error ->
                    if (snapshots != null) {
                        _serviceOrders.value = snapshots.toObjects(ServiceOrder::class.java)
                    }
                }
            registrations.add(lOrders)

        } catch (e: Exception) {
            Log.e("Firebase", "Error subscribing to SnapshotListeners: ${e.message}")
            activateBackupStaticData()
        }
    }

    private fun activateBackupStaticData() {
        isUsingOfflineMock = true
        _settings.value = AppSettings()
        
        // Mock Categories
        _categories.value = listOf(
            Category("c1", "كهربائي محترف", "Professional Electrician", "https://picsum.photos/300/200?random=1", 1),
            Category("c2", "سباك وصيانة صحية", "Plumber & Sanitary Specialist", "https://picsum.photos/300/200?random=2", 2),
            Category("c3", "صيانة أنظمة تكييف", "HVAC Maintenance Specialist", "https://picsum.photos/300/200?random=3", 3),
            Category("c4", "نجارة وهندسة ديكور", "Carpentry & Decor Craft", "https://picsum.photos/300/200?random=4", 4),
            Category("c5", "صيانة أجهزة وهواتف", "Mobiles & Tech Care", "https://picsum.photos/300/200?random=5", 5),
            Category("c6", "تعليم وتدريس خصوصي", "Tutoring & Education Helper", "https://picsum.photos/300/200?random=6", 6)
        )

        // Mock Providers
        _providers.value = listOf(
            ServiceProvider(
                id = "p1",
                name = "ماهر محمد طاهر",
                phone = "777644670",
                categoryId = "c1",
                categoryName = "كهربائي محترف",
                address = "شارع حده، أمام برج كنعان",
                region = "صنعاء",
                gpsLat = 15.3188,
                gpsLng = 44.2012,
                personalPhoto = "https://picsum.photos/150/150?random=11",
                isPinned = true,
                isRecommended = true,
                isVerified = true,
                rating = 4.9f,
                ratingCount = 12,
                loyaltyPoints = 250,
                isPremium = true
            ),
            ServiceProvider(
                id = "p2",
                name = "فارس أحمد الجبري",
                phone = "771122334",
                categoryId = "c2",
                categoryName = "سباك وصيانة صحية",
                address = "شارع المعلا الرئيسي",
                region = "عدن",
                gpsLat = 12.7855,
                gpsLng = 44.9752,
                personalPhoto = "https://picsum.photos/150/150?random=12",
                isPinned = false,
                isRecommended = true,
                isVerified = false,
                rating = 4.5f,
                ratingCount = 8,
                loyaltyPoints = 90,
                isPremium = false
            ),
            ServiceProvider(
                id = "p3",
                name = "عبدالرحمن الشميري",
                phone = "733889922",
                categoryId = "c1",
                categoryName = "كهربائي محترف",
                address = "شارع جمال، جوار البريد المالي",
                region = "تعز",
                gpsLat = 13.5786,
                gpsLng = 44.0135,
                personalPhoto = "https://picsum.photos/150/150?random=13",
                isPinned = true,
                isRecommended = false,
                isVerified = true,
                rating = 4.8f,
                ratingCount = 15,
                loyaltyPoints = 310,
                isPremium = true
            ),
            ServiceProvider(
                id = "p4",
                name = "مأمون نجيب غانم",
                phone = "775443322",
                categoryId = "c5",
                categoryName = "صيانة أجهزة وهواتف",
                address = "جوار جامعة العلوم والتكنولوجيا",
                region = "صنعاء",
                gpsLat = 15.3523,
                gpsLng = 44.1725,
                personalPhoto = "https://picsum.photos/150/150?random=14",
                isPinned = false,
                isRecommended = false,
                isVerified = false,
                rating = 4.2f,
                ratingCount = 5,
                loyaltyPoints = 40,
                isPremium = false
            )
        )

        _pendingProviders.value = listOf(
            PendingProvider(
                id = "p_pending1",
                name = "علي مصلح القاضي",
                phone = "773445566",
                categoryId = "c4",
                categoryName = "نجارة وهندسة ديكور",
                address = "شارع الستين الشرقي",
                region = "صنعاء",
                personalPhoto = "https://picsum.photos/100/100?random=20",
                submittedAt = System.currentTimeMillis()
            )
        )

        _banners.value = listOf(
            Banner("b1", "خصومات المهنيين المعتمدين!", "text", "", "احصل على أفضل الخدمات المضمونة اليوم بنسبة خصم تصل لـ 20% بضمان أسبوع كامل!", 6, "", isSponsored = false),
            Banner("b2", "مقدم الخدمة المتميز الأسبوعي", "image", "https://picsum.photos/600/250?random=30", "ماهر محمد طاهر - أخصائي تمديدات منزلية وصناعية معتمدة بقيمة اقتصادية ومصداقية كاملة.", 8, "777644670", isSponsored = true, "p1")
        )

        _chats.value = listOf(
            ChatMessage("msg1", "p1", "visitor123", "زائر 123", "guest", "مرحباً، هل تقدمون خدمات الصيانة في منطقة الروضة؟", System.currentTimeMillis() - 120000),
            ChatMessage("msg2", "p1", "visitor123", "ماهر محمد طاهر", "provider", "نعم أهلاً بك، أصل لأي مكان بصنعاء، متى تريد الفحص؟", System.currentTimeMillis() - 60000)
        )
    }

    // Seeding logic for Firebase collections when starting empty
    private fun seedDefaultCategories() {
        val firestore = db ?: return
        val list = listOf(
            Category("c1", "كهربائي محترف", "Professional Electrician", "https://picsum.photos/300/200?random=1", 1),
            Category("c2", "سباك وصيانة صحية", "Plumber & Sanitary Specialist", "https://picsum.photos/300/200?random=2", 2),
            Category("c3", "صيانة أنظمة تكييف", "HVAC Maintenance Specialist", "https://picsum.photos/300/200?random=3", 3),
            Category("c4", "نجارة وهندسة ديكور", "Carpentry & Decor Craft", "https://picsum.photos/300/200?random=4", 4),
            Category("c5", "صيانة أجهزة وهواتف", "Mobiles & Tech Care", "https://picsum.photos/300/200?random=5", 5),
            Category("c6", "تعليم وتدريس خصوصي", "Tutoring & Education Helper", "https://picsum.photos/300/200?random=6", 6)
        )
        for (item in list) {
            firestore.collection("categories").document(item.id).set(item)
        }
    }

    private fun seedDefaultProviders() {
        val firestore = db ?: return
        val list = listOf(
            ServiceProvider(
                id = "p1",
                name = "ماهر محمد طاهر",
                phone = "777644670",
                categoryId = "c1",
                categoryName = "كهربائي محترف",
                address = "شارع حده، أمام برج كنعان",
                region = "صنعاء",
                gpsLat = 15.3188,
                gpsLng = 44.2012,
                personalPhoto = "https://picsum.photos/150/150?random=11",
                isPinned = true,
                isRecommended = true,
                isVerified = true,
                rating = 4.9f,
                ratingCount = 12,
                loyaltyPoints = 250,
                isPremium = true,
                premiumApproved = true,
                registeredAt = System.currentTimeMillis()
            )
        )
        for (item in list) {
            firestore.collection("service_providers").document(item.id).set(item)
        }
    }

    private fun seedDefaultBanners() {
        val firestore = db ?: return
        val list = listOf(
            Banner("b1", "خصومات المهنيين المعتمدين!", "text", "", "احصل على أفضل الخدمات المضمونة اليوم بنسبة خصم تصل لـ 20% بضمان أسبوع كامل!", 6, "", isSponsored = false),
            Banner("b2", "مقدم الخدمة المتميز الأسبوعي", "image", "https://picsum.photos/600/250?random=30", "ماهر محمد طاهر - أخصائي تمديدات منزلية وصناعية معتمدة بقيمة اقتصادية ومصداقية كاملة.", 8, "777644670", isSponsored = true, "p1")
        )
        for (item in list) {
            firestore.collection("banners").document(item.id).set(item)
        }
    }

    // Settings actions (Backdoor and general Admin access)
    fun saveSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        db?.collection("settings")?.document("global")?.set(newSettings)
    }

    // Submit a provider for review
    fun submitPendingProvider(
        name: String,
        phone: String,
        categoryId: String,
        categoryName: String,
        address: String,
        region: String,
        gpsLat: Double,
        gpsLng: Double,
        personalPhoto: String,
        idCard: String,
        onComplete: (Boolean) -> Unit
    ) {
        val newId = "pending_${System.currentTimeMillis()}"
        val obj = PendingProvider(
            id = newId,
            name = name,
            phone = phone,
            categoryId = categoryId,
            categoryName = categoryName,
            address = address,
            region = region,
            gpsLat = gpsLat,
            gpsLng = gpsLng,
            personalPhoto = personalPhoto.ifEmpty { "https://picsum.photos/150/150?random=${System.currentTimeMillis() % 100}" },
            idCard = idCard,
            submittedAt = System.currentTimeMillis()
        )

        if (isUsingOfflineMock) {
            _pendingProviders.value = _pendingProviders.value + obj
            onComplete(true)
            return
        }

        db?.collection("pending_providers")?.document(newId)?.set(obj)
            ?.addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            } ?: run {
                _pendingProviders.value = _pendingProviders.value + obj
                onComplete(true)
            }
    }

    // Approve Provider -> move to service_providers
    fun approveProvider(pending: PendingProvider, onComplete: (Boolean) -> Unit) {
        val approved = ServiceProvider(
            id = "prov_${System.currentTimeMillis()}",
            name = pending.name,
            phone = pending.phone,
            categoryId = pending.categoryId,
            categoryName = pending.categoryName,
            address = pending.address,
            region = pending.region,
            gpsLat = pending.gpsLat,
            gpsLng = pending.gpsLng,
            personalPhoto = pending.personalPhoto,
            idCard = pending.idCard,
            registeredAt = System.currentTimeMillis()
        )

        if (isUsingOfflineMock) {
            _providers.value = _providers.value + approved
            _pendingProviders.value = _pendingProviders.value.filter { it.id != pending.id }
            onComplete(true)
            return
        }

        val firestore = db ?: run {
            onComplete(false)
            return
        }
        firestore.runTransaction { transaction ->
            val pendingRef = firestore.collection("pending_providers").document(pending.id)
            val providerRef = firestore.collection("service_providers").document(approved.id)
            transaction.delete(pendingRef)
            transaction.set(providerRef, approved)
        }.addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    // Direct registration (Direct add - bypass review, owner/admin level)
    fun addProviderDirectly(
        name: String,
        phone: String,
        categoryId: String,
        categoryName: String,
        address: String,
        region: String,
        gpsLat: Double,
        gpsLng: Double,
        personalPhoto: String,
        onComplete: (Boolean) -> Unit
    ) {
        val newId = "prov_dir_${System.currentTimeMillis()}"
        val obj = ServiceProvider(
            id = newId,
            name = name,
            phone = phone,
            categoryId = categoryId,
            categoryName = categoryName,
            address = address,
            region = region,
            gpsLat = gpsLat,
            gpsLng = gpsLng,
            personalPhoto = personalPhoto.ifEmpty { "https://picsum.photos/150/150?random=${System.currentTimeMillis() % 100}" },
            registeredAt = System.currentTimeMillis()
        )
        if (isUsingOfflineMock) {
            _providers.value = _providers.value + obj
            onComplete(true)
            return
        }
        db?.collection("service_providers")?.document(newId)?.set(obj)
            ?.addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            } ?: run {
                _providers.value = _providers.value + obj
                onComplete(true)
            }
    }

    // Reject pending provider with reason
    fun rejectProvider(pendingId: String, reason: String, onComplete: (Boolean) -> Unit) {
        if (isUsingOfflineMock) {
            _pendingProviders.value = _pendingProviders.value.filter { it.id != pendingId }
            onComplete(true)
            return
        }
        db?.collection("pending_providers")?.document(pendingId)?.update("rejectReason", reason)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Optionally delete application from pending after rejection setting
                    db?.collection("pending_providers")?.document(pendingId)?.delete()
                }
                onComplete(task.isSuccessful)
            } ?: run {
                _pendingProviders.value = _pendingProviders.value.filter { it.id != pendingId }
                onComplete(true)
            }
    }

    // Pin provider
    fun togglePinProvider(providerId: String, isPinned: Boolean) {
        if (isUsingOfflineMock) {
            _providers.value = _providers.value.map {
                if (it.id == providerId) it.copy(isPinned = isPinned) else it
            }
            return
        }
        db?.collection("service_providers")?.document(providerId)?.update("isPinned", isPinned)
    }

    // Recommend provider
    fun toggleRecommendProvider(providerId: String, isRecommended: Boolean) {
        if (isUsingOfflineMock) {
            _providers.value = _providers.value.map {
                if (it.id == providerId) it.copy(isRecommended = isRecommended) else it
            }
            return
        }
        db?.collection("service_providers")?.document(providerId)?.update("isRecommended", isRecommended)
    }

    // Verify provider (Blue badge)
    fun toggleVerifyProvider(providerId: String, isVerified: Boolean) {
        if (isUsingOfflineMock) {
            _providers.value = _providers.value.map {
                if (it.id == providerId) it.copy(isVerified = isVerified) else it
            }
            return
        }
        db?.collection("service_providers")?.document(providerId)?.update("isVerified", isVerified)
    }

    // Block/Unblock provider
    fun toggleBlockProvider(providerId: String, isBlocked: Boolean) {
        if (isUsingOfflineMock) {
            _providers.value = _providers.value.map {
                if (it.id == providerId) it.copy(isBlocked = isBlocked) else it
            }
            return
        }
        db?.collection("service_providers")?.document(providerId)?.update("isBlocked", isBlocked)
    }

    // Add / Update / Delete Categories
    fun addOrUpdateCategory(cat: Category) {
        val catId = cat.id.ifEmpty { "c_${System.currentTimeMillis()}" }
        val finalCat = cat.copy(id = catId)
        if (isUsingOfflineMock) {
            _categories.value = _categories.value.filter { it.id != catId } + finalCat
            return
        }
        db?.collection("categories")?.document(catId)?.set(finalCat)
    }

    fun deleteCategory(catId: String) {
        if (isUsingOfflineMock) {
            _categories.value = _categories.value.filter { it.id != catId }
            return
        }
        db?.collection("categories")?.document(catId)?.delete()
    }

    // Submit user review for provider with optional rating points
    fun submitReview(providerId: String, userName: String, rating: Int, comment: String) {
        val reviewId = "rev_${System.currentTimeMillis()}"
        val obj = Review(reviewId, providerId, userName, rating, comment, System.currentTimeMillis())
        
        // Award loyalty points to reviewer if tracked, and update rating logic
        if (isUsingOfflineMock) {
            _reviews.value = _reviews.value + obj
            updateLocalProviderRating(providerId, rating)
            return
        }

        db?.collection("reviews")?.document(reviewId)?.set(obj)?.addOnSuccessListener {
            // Update average rating on target provider
            val prov = _providers.value.find { it.id == providerId }
            if (prov != null) {
                val newCount = prov.ratingCount + 1
                val newRating = ((prov.rating * prov.ratingCount) + rating) / newCount
                val newPoints = prov.loyaltyPoints + 15 // award 15 loyalty points on review!
                db?.collection("service_providers")?.document(providerId)?.update(
                    mapOf(
                        "rating" to newRating,
                        "ratingCount" to newCount,
                        "loyaltyPoints" to newPoints
                    )
                )
            }
        }
    }

    private fun updateLocalProviderRating(providerId: String, rating: Int) {
        _providers.value = _providers.value.map { prov ->
            if (prov.id == providerId) {
                val newCount = prov.ratingCount + 1
                val newRating = ((prov.rating * prov.ratingCount) + rating) / newCount
                val newPoints = prov.loyaltyPoints + 15
                prov.copy(rating = newRating, ratingCount = newCount, loyaltyPoints = newPoints)
            } else prov
        }
    }

    // Redeem points logic
    fun redeemLoyaltyPoints(providerId: String, pointsToDeduct: Int, onResult: (Boolean) -> Unit) {
        val prov = _providers.value.find { it.id == providerId } ?: return onResult(false)
        if (prov.loyaltyPoints < pointsToDeduct) {
            onResult(false)
            return
        }
        val nextPoints = prov.loyaltyPoints - pointsToDeduct
        if (isUsingOfflineMock) {
            _providers.value = _providers.value.map {
                if (it.id == providerId) it.copy(loyaltyPoints = nextPoints) else it
            }
            onResult(true)
            return
        }
        db?.collection("service_providers")?.document(providerId)?.update("loyaltyPoints", nextPoints)
            ?.addOnCompleteListener { onResult(it.isSuccessful) }
    }

    // Premium status toggles
    fun requestPremiumSubscription(providerId: String, onComplete: (Boolean) -> Unit) {
        // Mock intermediate approval state
        if (isUsingOfflineMock) {
            _providers.value = _providers.value.map {
                if (it.id == providerId) it.copy(isPremium = true, premiumApproved = false) else it
            }
            onComplete(true)
            return
        }
        db?.collection("service_providers")?.document(providerId)
            ?.update(mapOf("isPremium" to true, "premiumApproved" to false))
            ?.addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun approvePremium(providerId: String, approved: Boolean) {
        if (isUsingOfflineMock) {
            _providers.value = _providers.value.map {
                if (it.id == providerId) it.copy(premiumApproved = approved, isPremium = approved) else it
            }
            return
        }
        db?.collection("service_providers")?.document(providerId)
            ?.update(mapOf("premiumApproved" to approved, "isPremium" to approved))
    }

    // Real-time Chat
    fun sendChatMessage(providerId: String, userId: String, senderName: String, senderType: String, messageText: String) {
        val msgId = "msg_${System.currentTimeMillis()}"
        val obj = ChatMessage(msgId, providerId, userId, senderName, senderType, messageText, System.currentTimeMillis())
        if (isUsingOfflineMock) {
            _chats.value = _chats.value + obj
            return
        }
        db?.collection("chats")?.document(msgId)?.set(obj)
    }

    // Send admin complaint
    fun submitComplaint(providerId: String, providerName: String, userName: String, userPhone: String, text: String) {
        val cid = "comp_${System.currentTimeMillis()}"
        val obj = Complaint(cid, providerId, providerName, userName, userPhone, text, System.currentTimeMillis())
        if (isUsingOfflineMock) {
            _complaints.value = _complaints.value + obj
            return
        }
        db?.collection("complaints")?.document(cid)?.set(obj)
    }

    // Direct add service order
    fun addServiceOrder(provider: ServiceProvider, userName: String, userPhone: String, details: String) {
        val oid = "ord_${System.currentTimeMillis()}"
        val obj = ServiceOrder(oid, provider.id, provider.name, provider.phone, userName, userPhone, details, "completed", System.currentTimeMillis())
        if (isUsingOfflineMock) {
            _serviceOrders.value = _serviceOrders.value + obj
            return
        }
        db?.collection("service_orders")?.document(oid)?.set(obj)
    }

    // Backup actions
    fun performBackup(context: Context, mode: String, folderPath: String): String {
        // Convert local databases state to readable JSON output
        try {
            val json = """
                {
                  "appName": "${settings.value.appNameAr}",
                  "categories_count": ${categories.value.size},
                  "providers_count": ${providers.value.size},
                  "reviews_count": ${reviews.value.size}
                }
            """.trimIndent()
            val file = File(context.cacheDir, "dalili_backup_${System.currentTimeMillis()}.json")
            val writer = FileWriter(file)
            writer.write(json)
            writer.flush()
            writer.close()
            return file.absolutePath
        } catch (e: Exception) {
            return "Failed: ${e.message}"
        }
    }

    // Restore Backup Mock
    fun restoreBackupMock(dataJson: String): Boolean {
        // Restores some configurations or settings from formatted string
        return dataJson.contains("dalili")
    }

    // Banners manager
    fun addOrUpdateBanner(banner: Banner) {
        val bid = banner.id.ifEmpty { "b_${System.currentTimeMillis()}" }
        val finalBanner = banner.copy(id = bid)
        if (isUsingOfflineMock) {
            _banners.value = _banners.value.filter { it.id != bid } + finalBanner
            return
        }
        db?.collection("banners")?.document(bid)?.set(finalBanner)
    }

    fun deleteBanner(bid: String) {
        if (isUsingOfflineMock) {
            _banners.value = _banners.value.filter { it.id != bid }
            return
        }
        db?.collection("banners")?.document(bid)?.delete()
    }

    // Helper update function for testing
    fun forceSync() {
        Log.i("Dalili", "Data refresh requested manually.")
        setupSnapshotListeners()
    }

    override fun onCleared() {
        super.onCleared()
        // Safely detach all snapshot listeners to avoid memory leaks
        registrations.forEach { it.remove() }
        registrations.clear()
    }
}
