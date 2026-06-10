package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class DaliliViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private var db: FirebaseFirestore? = null

    // State flows representing the models
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _subCategories = MutableStateFlow<List<SubCategory>>(emptyList())
    val subCategories: StateFlow<List<SubCategory>> = _subCategories.asStateFlow()

    private val _providers = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val providers: StateFlow<List<ServiceProvider>> = _providers.asStateFlow()

    private val _pendingProviders = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val pendingProviders: StateFlow<List<ServiceProvider>> = _pendingProviders.asStateFlow()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chats: StateFlow<List<ChatMessage>> = _chats.asStateFlow()

    private val _whitelistedDevices = MutableStateFlow<List<WhitelistedDevice>>(emptyList())
    val whitelistedDevices: StateFlow<List<WhitelistedDevice>> = _whitelistedDevices.asStateFlow()

    private val _admins = MutableStateFlow<List<AdminUser>>(emptyList())
    val admins: StateFlow<List<AdminUser>> = _admins.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private val _serviceOrders = MutableStateFlow<List<ServiceOrder>>(emptyList())
    val serviceOrders: StateFlow<List<ServiceOrder>> = _serviceOrders.asStateFlow()

    // Firestore listener registrations for real-time synchronization
    private val listeners = mutableListOf<ListenerRegistration>()

    init {
        try {
            db = FirebaseFirestore.getInstance()
            setupRealtimeSync()
        } catch (e: Exception) {
            Log.e("DaliliViewModel", "Firebase uninitialized or failed. Initializing memory cache fallback.", e)
            loadOfflineFallbackData()
        }
    }

    private fun setupRealtimeSync() {
        val firestore = db ?: return

        // 1. AppSettings Listener
        listeners.add(
            firestore.collection("settings").document("global_config")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null && snapshot.exists()) {
                        snapshot.toObject(AppSettings::class.java)?.let {
                            _settings.value = it
                        }
                    } else {
                        // Seed default settings to Firestore
                        firestore.collection("settings").document("global_config").set(AppSettings())
                    }
                }
        )

        // 2. Categories Listener
        listeners.add(
            firestore.collection("categories").orderBy("displayOrder")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        val list = snapshot.toObjects(Category::class.java)
                        _categories.value = list
                        if (list.isEmpty()) seedDefaultCategories()
                    }
                }
        )

        // 3. SubCategories Listener
        listeners.add(
            firestore.collection("subcategories")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _subCategories.value = snapshot.toObjects(SubCategory::class.java)
                    }
                }
        )

        // 4. Service Providers Listener
        listeners.add(
            firestore.collection("service_providers")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _providers.value = snapshot.toObjects(ServiceProvider::class.java)
                    }
                }
        )

        // 5. Pending Providers Listener
        listeners.add(
            firestore.collection("pending_providers")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _pendingProviders.value = snapshot.toObjects(ServiceProvider::class.java)
                    }
                }
        )

        // 6. Banners Listener
        listeners.add(
            firestore.collection("banners").orderBy("expiryTimestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _banners.value = snapshot.toObjects(Banner::class.java)
                    }
                }
        )

        // 7. Complaints Listener
        listeners.add(
            firestore.collection("complaints").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _complaints.value = snapshot.toObjects(Complaint::class.java)
                    }
                }
        )

        // 8. Chats Listener
        listeners.add(
            firestore.collection("chats").orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _chats.value = snapshot.toObjects(ChatMessage::class.java)
                    }
                }
        )

        // 9. Whitelist Listener
        listeners.add(
            firestore.collection("whitelisted_devices")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _whitelistedDevices.value = snapshot.toObjects(WhitelistedDevice::class.java)
                    }
                }
        )

        // 10. Admin User Accounts Listener
        listeners.add(
            firestore.collection("admins")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        val list = snapshot.toObjects(AdminUser::class.java)
                        _admins.value = list
                        if (list.isEmpty()) seedDefaultAdmins()
                    }
                }
        )

        // 11. Activity Notification activityLogs
        listeners.add(
            firestore.collection("activity_logs").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _activityLogs.value = snapshot.toObjects(ActivityLog::class.java)
                    }
                }
        )

        // 12. Service orders tracking (previous requests history)
        listeners.add(
            firestore.collection("service_orders")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _serviceOrders.value = snapshot.toObjects(ServiceOrder::class.java)
                    }
                }
        )
    }

    private fun loadOfflineFallbackData() {
        _settings.value = AppSettings()
        
        val seedCats = listOf(
            Category("cat1", "تكنولوجيا وصيانة", "Tech & Support", "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=120dp", 1),
            Category("cat2", "نجارة وديكور", "Carpentry & Decor", "https://images.unsplash.com/photo-1533090161767-e6ffed986c88?w=120dp", 2),
            Category("cat3", "كهرباء وسباكة", "Electric & Plumbing", "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=120dp", 3),
            Category("cat4", "أعمال البناء والمقاولات", "Builder & Mason", "https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=120dp", 4)
        )
        _categories.value = seedCats

        _subCategories.value = listOf(
            SubCategory("sub1", "cat1", "صيانة الهواتف الذكية", "Mobile Phone Repair"),
            SubCategory("sub2", "cat1", "برمجة ويب وتطبيقات", "App & Web Coding"),
            SubCategory("sub3", "cat2", "تركيب غرف وأخشاب", "Bedroom Crafting"),
            SubCategory("sub4", "cat2", "تصاميم جبس وديكور", "Decor & Gypsum"),
            SubCategory("sub5", "cat3", "سباكة منزلية متكاملة", "Plumbing Repairs"),
            SubCategory("sub6", "cat3", "تأسيس تمديدات الكهرباء", "Home Wiring")
        )

        _providers.value = mutableListOf(
            ServiceProvider(
                id = "p1",
                name = "المهندس ماهر البدري",
                phone = "777644670",
                categoryId = "cat1",
                categoryName = "تكنولوجيا وصيانة",
                subCategoryId = "sub2",
                subCategoryName = "برمجة ويب وتطبيقات",
                region = "صنعاء",
                address = "شارع حدة - أمام مركز الكمبيوتر",
                personalPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
                isVerified = true,
                isPinned = true,
                isRecommended = true,
                isPremium = true
            ),
            ServiceProvider(
                id = "p2",
                name = "المعلم محمد اليماني",
                phone = "736462700",
                categoryId = "cat2",
                categoryName = "نجارة وديكور",
                subCategoryId = "sub3",
                subCategoryName = "تركيب غرف وأخشاب",
                region = "عدن",
                address = "المنصورة - خلف جولة كالتكس",
                personalPhoto = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100",
                isVerified = true,
                isPinned = false,
                isRecommended = true,
                isPremium = false
            )
        )

        _banners.value = listOf(
            Banner(
                id = "b1",
                textMessage = "خصم 40% على خدمات الصيانة المنزلية والبرمجة هذا الأسبوع!",
                type = "text",
                isSponsored = true,
                sizeChoice = "medium",
                expiryTimestamp = System.currentTimeMillis() + 864000000L
            )
        )

        _admins.value = listOf(
            AdminUser("adm1", "WAM2026", "maher736462", "owner")
        )
    }

    // Seeding logic for firestore when empty
    private fun seedDefaultCategories() {
        val firestore = db ?: return
        val items = listOf(
            Category("cat1", "تكنولوجيا وصيانة", "Tech & Support", "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=150", 1),
            Category("cat2", "نجارة وديكور", "Carpentry & Decor", "https://images.unsplash.com/photo-1533090161767-e6ffed986c88?w=150", 2),
            Category("cat3", "كهرباء وسباكة", "Electric & Plumbing", "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=150", 3),
            Category("cat4", "أعمال البناء والمقاولات", "Builder & Mason", "https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=150", 4)
        )
        for (item in items) {
            firestore.collection("categories").document(item.id).set(item)
        }

        val subs = listOf(
            SubCategory("sub1", "cat1", "صيانة الهواتف الذكية", "Mobile Phone Repair"),
            SubCategory("sub2", "cat1", "برمجة ويب وتطبيقات", "App & Web Coding"),
            SubCategory("sub3", "cat2", "تركيب غرف وأخشاب", "Bedroom Crafting"),
            SubCategory("sub4", "cat2", "تصاميم جبس وديكور", "Decor & Gypsum"),
            SubCategory("sub5", "cat3", "سباكة منزلية متكاملة", "Plumbing Repairs"),
            SubCategory("sub6", "cat3", "تأسيس تمديدات الكهرباء", "Home Wiring")
        )
        for (sub in subs) {
            firestore.collection("subcategories").document(sub.id).set(sub)
        }
    }

    private fun seedDefaultAdmins() {
        val firestore = db ?: return
        val superAdmin = AdminUser("adm1", "WAM2026", "maher736462", "owner")
        firestore.collection("admins").document(superAdmin.id).set(superAdmin)
    }

    // --- VIEWMODEL CONTROL API ACTIONS ---

    fun updateSettings(newSettings: AppSettings, onComplete: (Boolean) -> Unit) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("settings").document("global_config").set(newSettings)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            _settings.value = newSettings
            onComplete(true)
        }
    }

    fun submitRegistrationForm(provider: ServiceProvider, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val freshId = if (provider.id.isEmpty()) "prov_" + System.currentTimeMillis() else provider.id
        provider.id = freshId
        provider.subscriptionStatus = "none"

        // Log notification to activity logs
        val alert = ActivityLog(
            id = "log_" + System.currentTimeMillis(),
            title = "طلب تسجيل جديد",
            description = "قام المهني ${provider.name} بتقديم طلب للانضمام للأقسام.",
            category = "registration"
        )

        if (firestore != null) {
            // Write to pending
            firestore.collection("pending_providers").document(freshId).set(provider)
                .addOnSuccessListener {
                    firestore.collection("activity_logs").document(alert.id).set(alert)
                    onComplete(true)
                }
                .addOnFailureListener { onComplete(false) }
        } else {
            // Memory update
            val currentPending = _pendingProviders.value.toMutableList()
            currentPending.add(provider)
            _pendingProviders.value = currentPending

            val currentLogs = _activityLogs.value.toMutableList()
            currentLogs.add(alert)
            _activityLogs.value = currentLogs
            onComplete(true)
        }
    }

    fun acceptRegistration(pending: ServiceProvider, onComplete: (Boolean) -> Unit) {
        val firestore = db
        pending.subscriptionStatus = "none"

        val alert = ActivityLog(
            id = "log_" + System.currentTimeMillis(),
            title = "ترخيص مهني مكتمل",
            description = "تم قبول طلب انضمام ${pending.name} بنجاح.",
            category = "registration"
        )

        if (firestore != null) {
            firestore.runTransaction { transaction ->
                val pendingRef = firestore.collection("pending_providers").document(pending.id)
                val providerRef = firestore.collection("service_providers").document(pending.id)
                val logRef = firestore.collection("activity_logs").document(alert.id)

                transaction.delete(pendingRef)
                transaction.set(providerRef, pending)
                transaction.set(logRef, alert)
            }.addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            // Offline transition
            val currentPending = _pendingProviders.value.toMutableList()
            currentPending.removeAll { it.id == pending.id }
            _pendingProviders.value = currentPending

            val currentActive = _providers.value.toMutableList()
            currentActive.add(pending)
            _providers.value = currentActive

            val currentLogs = _activityLogs.value.toMutableList()
            currentLogs.add(alert)
            _activityLogs.value = currentLogs
            onComplete(true)
        }
    }

    fun rejectRegistration(pendingId: String, reason: String, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val alert = ActivityLog(
            id = "log_" + System.currentTimeMillis(),
            title = "تم رفض طلب التسجيل",
            description = "تم رفض الطلب رقم $pendingId. السبب: $reason",
            category = "registration"
        )

        if (firestore != null) {
            firestore.runTransaction { transaction ->
                val pendingRef = firestore.collection("pending_providers").document(pendingId)
                val logRef = firestore.collection("activity_logs").document(alert.id)

                transaction.delete(pendingRef)
                transaction.set(logRef, alert)
            }.addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val currentPending = _pendingProviders.value.toMutableList()
            currentPending.removeAll { it.id == pendingId }
            _pendingProviders.value = currentPending

            val currentLogs = _activityLogs.value.toMutableList()
            currentLogs.add(alert)
            _activityLogs.value = currentLogs
            onComplete(true)
        }
    }

    // Pin, Recommendation, Verification and blocking toggles
    fun togglePinProvider(providerId: String, isPinned: Boolean) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("service_providers").document(providerId).update("pinned", isPinned)
        } else {
            val list = _providers.value.toMutableList()
            list.find { it.id == providerId }?.isPinned = isPinned
            _providers.value = list
        }
    }

    fun toggleRecommendProvider(providerId: String, isRecommended: Boolean) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("service_providers").document(providerId).update("recommended", isRecommended)
        } else {
            val list = _providers.value.toMutableList()
            list.find { it.id == providerId }?.isRecommended = isRecommended
            _providers.value = list
        }
    }

    fun toggleVerifyProvider(providerId: String, isVerified: Boolean) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("service_providers").document(providerId).update("verified", isVerified)
        } else {
            val list = _providers.value.toMutableList()
            list.find { it.id == providerId }?.isVerified = isVerified
            _providers.value = list
        }
    }

    fun toggleBlockProvider(providerId: String, isBlocked: Boolean) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("service_providers").document(providerId).update("blocked", isBlocked)
        } else {
            val list = _providers.value.toMutableList()
            list.find { it.id == providerId }?.isBlocked = isBlocked
            _providers.value = list
        }
    }

    // Direct Manual Addition bypasses general approvals
    fun addProviderManually(provider: ServiceProvider, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val freshId = if (provider.id.isEmpty()) "manual_" + System.currentTimeMillis() else provider.id
        provider.id = freshId
        provider.isVerified = true // Auto true for manual additions

        if (firestore != null) {
            firestore.collection("service_providers").document(freshId).set(provider)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _providers.value.toMutableList()
            list.add(provider)
            _providers.value = list
            onComplete(true)
        }
    }

    // Monthly Subscriptions monthly premium trigger
    fun requestPremiumSubscription(providerId: String, phone: String, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val alert = ActivityLog(
            id = "log_" + System.currentTimeMillis(),
            title = "طلب اشتراك مميز شهري",
            description = "طلب مزود خدمة $providerId تفعيل اشتراك مميز شهري برقم هاتف $phone.",
            category = "subscription"
        )
        if (firestore != null) {
            firestore.runTransaction { transaction ->
                val providerRef = firestore.collection("service_providers").document(providerId)
                val logRef = firestore.collection("activity_logs").document(alert.id)

                transaction.update(providerRef, "subscriptionStatus", "pending_approval")
                transaction.set(logRef, alert)
            }.addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _providers.value.toMutableList()
            list.find { it.id == providerId }?.let {
                it.subscriptionStatus = "pending_approval"
            }
            _providers.value = list

            val currentLogs = _activityLogs.value.toMutableList()
            currentLogs.add(alert)
            _activityLogs.value = currentLogs
            onComplete(true)
        }
    }

    fun approvePremiumSubscription(providerId: String, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val expiry = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 // 30 Days
        val alert = ActivityLog(
            id = "log_" + System.currentTimeMillis(),
            title = "تفعيل الاشتراك المميز",
            description = "تمت الموافقة وتفعيل شارة التميز لمزود الخدمة $providerId للشهر الجاري.",
            category = "subscription"
        )

        if (firestore != null) {
            firestore.runTransaction { transaction ->
                val providerRef = firestore.collection("service_providers").document(providerId)
                val logRef = firestore.collection("activity_logs").document(alert.id)

                transaction.update(providerRef, "subscriptionStatus", "active")
                transaction.update(providerRef, "subscriptionExpiry", expiry)
                transaction.update(providerRef, "premium", true)
            }.addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _providers.value.toMutableList()
            list.find { it.id == providerId }?.let {
                it.subscriptionStatus = "active"
                it.subscriptionExpiry = expiry
                it.isPremium = true
            }
            _providers.value = list

            val currentLogs = _activityLogs.value.toMutableList()
            currentLogs.add(alert)
            _activityLogs.value = currentLogs
            onComplete(true)
        }
    }

    // Management of categories & subcategories from admin panel
    fun addMainCategory(category: Category, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val freshId = "cat_" + System.currentTimeMillis()
        category.id = freshId
        category.displayOrder = _categories.value.size + 1

        if (firestore != null) {
            firestore.collection("categories").document(freshId).set(category)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _categories.value.toMutableList()
            list.add(category)
            _categories.value = list
            onComplete(true)
        }
    }

    fun addSubCategory(sub: SubCategory, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val freshId = "sub_" + System.currentTimeMillis()
        sub.id = freshId

        if (firestore != null) {
            firestore.collection("subcategories").document(freshId).set(sub)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _subCategories.value.toMutableList()
            list.add(sub)
            _subCategories.value = list
            onComplete(true)
        }
    }

    fun updateMainCategory(category: Category, onComplete: (Boolean) -> Unit = {}) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("categories").document(category.id).set(category)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _categories.value.toMutableList()
            val index = list.indexOfFirst { it.id == category.id }
            if (index != -1) {
                list[index] = category
                _categories.value = list
            }
            onComplete(true)
        }
    }

    fun deleteMainCategory(categoryId: String, onComplete: (Boolean) -> Unit = {}) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("categories").document(categoryId).delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _categories.value.toMutableList()
            list.removeAll { it.id == categoryId }
            _categories.value = list
            onComplete(true)
        }
    }

    fun updateSubCategory(sub: SubCategory, onComplete: (Boolean) -> Unit = {}) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("subcategories").document(sub.id).set(sub)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _subCategories.value.toMutableList()
            val index = list.indexOfFirst { it.id == sub.id }
            if (index != -1) {
                list[index] = sub
                _subCategories.value = list
            }
            onComplete(true)
        }
    }

    fun deleteSubCategory(subId: String, onComplete: (Boolean) -> Unit = {}) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("subcategories").document(subId).delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _subCategories.value.toMutableList()
            list.removeAll { it.id == subId }
            _subCategories.value = list
            onComplete(true)
        }
    }

    // Reports/Complaints submitting engine
    fun submitReport(provider: ServiceProvider, reason: String, reporterName: String, reporterPhone: String, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val freshId = "comp_" + System.currentTimeMillis()
        val complaint = Complaint(
            id = freshId,
            providerId = provider.id,
            providerName = provider.name,
            reporterName = reporterName.ifEmpty { "زائر" },
            reporterPhone = reporterPhone,
            reasonText = reason
        )
        val alert = ActivityLog(
            id = "log_" + System.currentTimeMillis(),
            title = "بلاغ شكوى جديد",
            description = "تم تقديم بلاغ ضد المهني ${provider.name}. السبب: $reason",
            category = "reports"
        )

        if (firestore != null) {
            firestore.runTransaction { transaction ->
                val compRef = firestore.collection("complaints").document(freshId)
                val logRef = firestore.collection("activity_logs").document(alert.id)

                transaction.set(compRef, complaint)
                transaction.set(logRef, alert)
            }.addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _complaints.value.toMutableList()
            list.add(0, complaint)
            _complaints.value = list

            val currentLogs = _activityLogs.value.toMutableList()
            currentLogs.add(alert)
            _activityLogs.value = currentLogs
            onComplete(true)
        }
    }

    fun deleteComplaint(complaintId: String) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("complaints").document(complaintId).delete()
        } else {
            val list = _complaints.value.toMutableList()
            list.removeAll { it.id == complaintId }
            _complaints.value = list
        }
    }

    // Manage supervisor accounts
    fun addAdminUser(newAdmin: AdminUser, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val id = "adm_" + System.currentTimeMillis()
        newAdmin.id = id
        if (firestore != null) {
            firestore.collection("admins").document(id).set(newAdmin)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _admins.value.toMutableList()
            list.add(newAdmin)
            _admins.value = list
            onComplete(true)
        }
    }

    fun removeAdminUser(adminId: String) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("admins").document(adminId).delete()
        } else {
            val list = _admins.value.toMutableList()
            list.removeAll { it.id == adminId }
            _admins.value = list
        }
    }

    // Whitelisted devices
    fun addWhitelistedDevice(device: WhitelistedDevice, onComplete: (Boolean) -> Unit) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("whitelisted_devices").document(device.deviceId).set(device)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _whitelistedDevices.value.toMutableList()
            list.add(device)
            _whitelistedDevices.value = list
            onComplete(true)
        }
    }

    fun removeWhitelistedDevice(deviceId: String) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("whitelisted_devices").document(deviceId).delete()
        } else {
            val list = _whitelistedDevices.value.toMutableList()
            list.removeAll { it.deviceId == deviceId }
            _whitelistedDevices.value = list
        }
    }

    // Secure auth notification logger
    fun logUnauthorizedAttempt(deviceInfo: String) {
        val firestore = db
        val alert = ActivityLog(
            id = "sec_" + System.currentTimeMillis(),
            title = "محاولة دخول غير مصرح بها",
            description = "جرت محاولة دخول من جهاز غير موثق: $deviceInfo",
            category = "security"
        )
        if (firestore != null) {
            firestore.collection("activity_logs").document(alert.id).set(alert)
        } else {
            val list = _activityLogs.value.toMutableList()
            list.add(0, alert)
            _activityLogs.value = list
        }
    }

    fun readAllLogs() {
        // Clear notifications or mark read
        val firestore = db
        if (firestore != null) {
            firestore.collection("activity_logs").get().addOnSuccessListener { query ->
                for (doc in query.documents) {
                    doc.reference.update("read", true)
                }
            }
        } else {
            val list = _activityLogs.value.toMutableList()
            list.forEach { it.isRead = true }
            _activityLogs.value = list
        }
    }

    // --- MANAGE TOP BANNERS ---
    fun addBanner(banner: Banner, onComplete: (Boolean) -> Unit) {
        val firestore = db
        val id = "banner_" + System.currentTimeMillis()
        banner.id = id
        banner.expiryTimestamp = System.currentTimeMillis() + (banner.durationSeconds * 1000L * 60)

        if (firestore != null) {
            firestore.collection("banners").document(id).set(banner)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _banners.value.toMutableList()
            list.add(banner)
            _banners.value = list
            onComplete(true)
        }
    }

    fun deleteBanner(bannerId: String, onComplete: (Boolean) -> Unit) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("banners").document(bannerId).delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            val list = _banners.value.toMutableList()
            list.removeAll { it.id == bannerId }
            _banners.value = list
            onComplete(true)
        }
    }

    // Service order simulator for dashboard CUJs
    fun recordServiceRequest(provider: ServiceProvider) {
        val order = ServiceOrder(
            id = "ord_" + System.currentTimeMillis(),
            userId = "guest_user",
            providerId = provider.id,
            providerName = provider.name,
            categoryName = provider.categoryName,
            orderDate = System.currentTimeMillis(),
            status = "completed"
        )
        val firestore = db
        if (firestore != null) {
            firestore.collection("service_orders").document(order.id).set(order)
        } else {
            val list = _serviceOrders.value.toMutableList()
            list.add(0, order)
            _serviceOrders.value = list
        }
    }

    // Peer to peer chat messages
    fun sendChatMessage(msg: ChatMessage) {
        val firestore = db
        val id = "chat_" + System.currentTimeMillis()
        msg.id = id
        if (firestore != null) {
            firestore.collection("chats").document(id).set(msg)
        } else {
            val list = _chats.value.toMutableList()
            list.add(msg)
            _chats.value = list
        }
    }

    fun clearAllChats(onComplete: (Boolean) -> Unit = {}) {
        val firestore = db
        if (firestore != null) {
            firestore.collection("chats")
                .get()
                .addOnSuccessListener { snapshot ->
                    val batch = firestore.batch()
                    snapshot.documents.forEach { doc ->
                        batch.delete(doc.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            _chats.value = emptyList()
                            onComplete(true)
                        }
                        .addOnFailureListener { onComplete(false) }
                }
                .addOnFailureListener { onComplete(false) }
        } else {
            _chats.value = emptyList()
            onComplete(true)
        }
    }

    fun refreshAllData(onComplete: (Boolean) -> Unit = {}) {
        try {
            listeners.forEach {
                try { it.remove() } catch (ex: Exception) {}
            }
            listeners.clear()
            setupRealtimeSync()
            onComplete(true)
        } catch (e: Exception) {
            onComplete(false)
        }
    }

    // --- BACKUP SYSTEMS: Local Storage backup output ---
    fun triggerBackupDatabase(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val fullBackup = JSONObject()
                
                // Categories
                val catsArray = JSONArray()
                _categories.value.forEach {
                    catsArray.put(JSONObject().apply {
                        put("id", it.id)
                        put("nameAr", it.nameAr)
                        put("nameEn", it.nameEn)
                        put("imageUrl", it.imageUrl)
                        put("displayOrder", it.displayOrder)
                    })
                }
                fullBackup.put("categories", catsArray)

                // SubCategories
                val subArray = JSONArray()
                _subCategories.value.forEach {
                    subArray.put(JSONObject().apply {
                        put("id", it.id)
                        put("categoryId", it.categoryId)
                        put("nameAr", it.nameAr)
                        put("nameEn", it.nameEn)
                    })
                }
                fullBackup.put("subCategories", subArray)

                // Providers
                val provArray = JSONArray()
                _providers.value.forEach {
                    provArray.put(JSONObject().apply {
                        put("id", it.id)
                        put("name", it.name)
                        put("phone", it.phone)
                        put("categoryId", it.categoryId)
                        put("categoryName", it.categoryName)
                        put("region", it.region)
                        put("address", it.address)
                        put("isVerified", it.isVerified)
                        put("isPinned", it.isPinned)
                        put("isRecommended", it.isRecommended)
                        put("isPremium", it.isPremium)
                    })
                }
                fullBackup.put("providers", provArray)

                // Settings
                val s = _settings.value
                val settObj = JSONObject().apply {
                    put("appNameAr", s.appNameAr)
                    put("appNameEn", s.appNameEn)
                    put("promoFooterText", s.promoFooterText)
                    put("supportPhone", s.supportPhone)
                    put("supportEmail", s.supportEmail)
                    put("supportWhatsapp", s.supportWhatsapp)
                    put("primaryColor", s.primaryColor)
                    put("secondaryColor", s.secondaryColor)
                    put("themeChoice", s.themeChoice)
                    put("assistantEnabled", s.assistantEnabled)
                    put("assistantSize", s.assistantSize)
                }
                fullBackup.put("settings", settObj)

                // Save to filesDir
                val file = File(context.filesDir, "dalili_db_backup.json")
                val fos = FileOutputStream(file)
                fos.write(fullBackup.toString(4).toByteArray())
                fos.close()

                onResult("تم أخذ نسخة احتياطية محلية وتخزينها كملف JSON بنجاح!")
            } catch (e: Exception) {
                onResult("فشل نسخ البيانات احتياطياً: ${e.localizedMessage}")
            }
        }
    }

    fun restoreDatabaseFromBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val file = File(context.filesDir, "dalili_db_backup.json")
                if (!file.exists()) {
                    onResult("عذراً، لم يتم العثور على أي ملف نسخة احتياطية سابقة!")
                    return@launch
                }
                val content = file.readText()
                val root = JSONObject(content)

                // Restore locally
                if (root.has("settings")) {
                    val sObj = root.getJSONObject("settings")
                    val s = AppSettings(
                        appNameAr = sObj.optString("appNameAr", "دليلي"),
                        appNameEn = sObj.optString("appNameEn", "Dalili"),
                        promoFooterText = sObj.optString("promoFooterText", "MAW 777644670"),
                        supportPhone = sObj.optString("supportPhone", "777644670"),
                        supportEmail = sObj.optString("supportEmail", "support@dalili.com"),
                        supportWhatsapp = sObj.optString("supportWhatsapp", "777644670"),
                        primaryColor = sObj.optString("primaryColor", "#1A237E"),
                        secondaryColor = sObj.optString("secondaryColor", "#FFD700"),
                        themeChoice = sObj.optString("themeChoice", "dark"),
                        assistantEnabled = sObj.optBoolean("assistantEnabled", true),
                        assistantSize = sObj.optString("assistantSize", "medium")
                    )
                    updateSettings(s) {}
                }

                val firestore = db
                if (firestore != null) {
                    // Update Firestore if online
                    if (root.has("categories")) {
                        val cats = root.getJSONArray("categories")
                        for (i in 0 until cats.length()) {
                            val cObj = cats.getJSONObject(i)
                            val cat = Category(
                                id = cObj.getString("id"),
                                nameAr = cObj.getString("nameAr"),
                                nameEn = cObj.getString("nameEn"),
                                imageUrl = cObj.getString("imageUrl"),
                                displayOrder = cObj.getInt("displayOrder")
                            )
                            firestore.collection("categories").document(cat.id).set(cat)
                        }
                    }
                }
                
                onResult("تمت استعادة البيانات والإعدادات من النسخة الإحتياطية وتحديثها فوراً!")
            } catch (e: Exception) {
                onResult("فشل استعادة النسخة الاحتياطية: ${e.localizedMessage}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listeners.forEach { it.remove() }
    }
}
