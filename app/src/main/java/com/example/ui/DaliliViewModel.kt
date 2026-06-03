package com.example.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

class DaliliViewModel(application: Application) : AndroidViewModel(application) {

    private val localDb = LocalDatabase.getDatabase(application)
    private val categoryDao = localDb.categoryDao()
    private val providerDao = localDb.providerDao()

    // -------------------------------------------------------------
    // App State Properties (Reactive)
    // -------------------------------------------------------------
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _syncLogs = MutableStateFlow<List<String>>(emptyList())
    val syncLogs: StateFlow<List<String>> = _syncLogs.asStateFlow()

    // Screen navigation
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _selectedProviderId = MutableStateFlow("")
    val selectedProviderId: StateFlow<String> = _selectedProviderId.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow("")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    // Authentication & User role state
    private val _currentUserEmail = MutableStateFlow("user@example.com")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentUserRole = MutableStateFlow("Guest") // Admin, Provider, Guest
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    // -------------------------------------------------------------
    // Data Sources (Simulated Firestore and Local Room)
    // -------------------------------------------------------------
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _subcategories = MutableStateFlow<List<Subcategory>>(emptyList())
    val subcategories: StateFlow<List<Subcategory>> = _subcategories.asStateFlow()

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _loyaltyPointsLog = MutableStateFlow<List<LoyaltyPoints>>(emptyList())
    val loyaltyPointsLog: StateFlow<List<LoyaltyPoints>> = _loyaltyPointsLog.asStateFlow()

    private val _userPoints = MutableStateFlow(120) // Starting coupon points
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms.asStateFlow()

    private val _currentRoomMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentRoomMessages: StateFlow<List<ChatMessage>> = _currentRoomMessages.asStateFlow()

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    private val _banners = MutableStateFlow<List<PromotionBanner>>(emptyList())
    val banners: StateFlow<List<PromotionBanner>> = _banners.asStateFlow()

    private val _verifications = MutableStateFlow<List<VerificationDocument>>(emptyList())
    val verifications: StateFlow<List<VerificationDocument>> = _verifications.asStateFlow()

    private val _moderators = MutableStateFlow<List<Moderator>>(emptyList())
    val moderators: StateFlow<List<Moderator>> = _moderators.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<AdminActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<AdminActivityLog>> = _activityLogs.asStateFlow()

    private val _sectionVisits = MutableStateFlow<List<SectionVisit>>(emptyList())
    val sectionVisits: StateFlow<List<SectionVisit>> = _sectionVisits.asStateFlow()

    private val _faqs = MutableStateFlow<List<FaqItem>>(emptyList())
    val faqs: StateFlow<List<FaqItem>> = _faqs.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // Selected Chat Room helper
    private val _activeRoomId = MutableStateFlow("")
    val activeRoomId: StateFlow<String> = _activeRoomId.asStateFlow()

    // -------------------------------------------------------------
    // Infinite Scroll & Filtering state
    // -------------------------------------------------------------
    private val _pageSize = MutableStateFlow(5) // Load chunk size customizable by Admin
    val pageSize: StateFlow<Int> = _pageSize.asStateFlow()

    private val _currentPageOffset = MutableStateFlow(1)
    val currentPageOffset: StateFlow<Int> = _currentPageOffset.asStateFlow()

    // Search advanced criteria
    val searchQuery = MutableStateFlow("")
    val searchCity = MutableStateFlow("")
    val searchNeighborhood = MutableStateFlow("")
    val searchPhone = MutableStateFlow("")
    val searchRatingMin = MutableStateFlow(0f)
    val searchRadiusInput = MutableStateFlow("") // Circular Radius Filter

    // Data-saving mode (وضع توفير البيانات)
    private val _isDataSavingMode = MutableStateFlow(false)
    val isDataSavingMode: StateFlow<Boolean> = _isDataSavingMode.asStateFlow()

    // -------------------------------------------------------------
    // Initialization with Gorgeous Demo Data
    // -------------------------------------------------------------
    init {
        loadDefaultDataset()
        observeRoomSync()
        // Automatically simulate backgrounds logs syncing
        viewModelScope.launch {
            while (true) {
                delay(30000) // check background sync state every 30s
                if (_isOnline.value && !_isDataSavingMode.value) {
                    performSyncWithFirestore()
                }
            }
        }
    }

    private fun loadDefaultDataset() {
        // Init Categories
        val defaultCats = listOf(
            Category("cat_1", "صيانة الأجهزة", "Appliance Maintenance", "build", true, 1),
            Category("cat_2", "خدمات طبية صيدلانية", "Medical Services", "medical_services", true, 2),
            Category("cat_3", "التعليم والتدريس الخصوصي", "Teaching & Education", "school", false, 3),
            Category("cat_4", "أعمال حرفية ومهنية", "Crafts & Labor", "handyman", true, 4),
            Category("cat_5", "خدمات قانونية وعقارية", "Legal & Real Estate", "balance", false, 5)
        )
        _categories.value = defaultCats

        val defaultSubs = listOf(
            Subcategory("sub_1", "cat_1", "إصلاح غسالات", "Washing Machine Repair"),
            Subcategory("sub_2", "cat_1", "صيانة مكيفات الهواء", "AC Maintenance"),
            Subcategory("sub_3", "cat_2", "عيادة عائلية", "Family Clinic"),
            Subcategory("sub_4", "cat_2", "طبيب أسنان", "Dentist clinic"),
            Subcategory("sub_5", "cat_3", "مدرس رياضيات", "Math Tutor"),
            Subcategory("sub_6", "cat_4", "سباكة وتركيب صحي", "Plumbing Services"),
            Subcategory("sub_7", "cat_4", "نجارة وتصليح أثاث", "Carpentry Services")
        )
        _subcategories.value = defaultSubs

        // Init Providers
        val defaultProvs = listOf(
            Provider(
                id = "prov_1",
                name = "المهندس أحمد مصطفى",
                phone = "0791234567",
                categoryId = "cat_1",
                subcategoryId = "sub_2",
                personalPhotoUrl = "https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=150",
                workspacePhotoUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=300",
                city = "عمان",
                neighborhood = "الجبيهة",
                latitude = 32.015,
                longitude = 35.862,
                isVerified = true,
                isPremium = true,
                rating = 4.8f,
                reviewCount = 24,
                isPinned = true,
                viewsCount = 1350,
                responseTimeMs = 120000L // 2 mins
            ),
            Provider(
                id = "prov_2",
                name = "الدكتور رامي العبدالله",
                phone = "0771122334",
                categoryId = "cat_2",
                subcategoryId = "sub_3",
                personalPhotoUrl = "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=150",
                workspacePhotoUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=300",
                city = "إربد",
                neighborhood = "رونق",
                latitude = 32.551,
                longitude = 35.851,
                isVerified = true,
                isPremium = false,
                rating = 4.9f,
                reviewCount = 56,
                isPinned = false,
                viewsCount = 980,
                responseTimeMs = 60000L // 1 min
            ),
            Provider(
                id = "prov_3",
                name = "الأستاذة رانيا العمري",
                phone = "0788899221",
                categoryId = "cat_3",
                subcategoryId = "sub_5",
                personalPhotoUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150",
                workspacePhotoUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=300",
                city = "الزرقاء",
                neighborhood = "الوسط التجاري",
                latitude = 32.062,
                longitude = 36.088,
                isVerified = false,
                isPremium = false,
                rating = 4.2f,
                reviewCount = 8,
                isPinned = false,
                viewsCount = 230,
                responseTimeMs = 1800000L // 30 mins
            ),
            Provider(
                id = "prov_4",
                name = "المعلم ناجي السباك",
                phone = "0795551234",
                categoryId = "cat_4",
                subcategoryId = "sub_6",
                personalPhotoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                workspacePhotoUrl = "https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=300",
                city = "عمان",
                neighborhood = "خلدا",
                latitude = 31.985,
                longitude = 35.837,
                isVerified = true,
                isPremium = true,
                rating = 4.6f,
                reviewCount = 19,
                isPinned = true,
                viewsCount = 870,
                responseTimeMs = 300000L // 5 mins
            ),
            Provider(
                id = "prov_5",
                name = "أبو طارق لأعمال النجارة والموبليا",
                phone = "0798887711",
                categoryId = "cat_4",
                subcategoryId = "sub_7",
                personalPhotoUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=150",
                workspacePhotoUrl = "https://images.unsplash.com/photo-1534080391025-a17c0af14a7f?w=300",
                city = "العقبة",
                neighborhood = "البلد",
                latitude = 29.532,
                longitude = 35.006,
                isVerified = false,
                isPremium = false,
                rating = 4.0f,
                reviewCount = 3,
                isPinned = false,
                viewsCount = 110,
                responseTimeMs = 2400000L // 40 mins
            )
        )
        _providers.value = defaultProvs

        // Banners
        _banners.value = listOf(
            PromotionBanner("b_1", "https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=600", "prov_1", 3, "Medium", "Special", true),
            PromotionBanner("b_2", "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=600", "prov_3", 5, "Large", "Standard", true)
        )

        // Reviews
        _reviews.value = listOf(
            Review("rev_1", "prov_1", "user1", "أبو يوسف", 5f, "صيانة سريعة وممتازة والتزام بالموعد الخبير المهندس بامتياز!"),
            Review("rev_2", "prov_1", "user2", "ريهام علي", 4.5f, "جيد جداً دقة عالية"),
            Review("rev_3", "prov_2", "user3", "كمال سليم", 5f, "أفضل طبيب أسنان وعيادة نظيفة جداً ومريحة للأطفال")
        )

        // FAQ manual search list
        _faqs.value = listOf(
            FaqItem("faq_1", "كيف أقوم بتوثيق حسابي كمزود خدمة؟", "توجه لملف التعريف الخاص بك ثم اختر 'تسجيل الهوية وتوثيق السجل'. ارفع شهادة السجل وسيراجع الآدمن طلبك.", "How do I verify my account?", "Go to your profile -> Upload Identity, upload documentation of commercial registration. Admin will audit."),
            FaqItem("faq_2", "ما هي الميزات التي تقدمها الاشتراكات الشهرية؟", "الاشتراك يمنحك شارة مميز تظهر فورا بجانب اسمك في التطبيق وتثبيت الملف في الصدارة ومقدمة البحث.", "What are subscription benefits?", "Gives premium badge and stays on top of search results in category."),
            FaqItem("faq_3", "كيف أستخدم نظام نقاط الولاء؟", "عند التقييم ومشاركة التطبيق تحصل على نقاط، يمكنك استبدالها بخصومات من صفحة المكافآت.", "How to use Loyalty Points?", "Rating and sharing earns points. Redeem them for certified coupons in the loyalty center.")
        )

        // Moderators
        _moderators.value = listOf(
            Moderator("mod_1", "admin@dalili.com", "admin123", false),
            Moderator("mod_2", "moderator@dalili.com", "mod123", false)
        )

        // Activity Logs
        _activityLogs.value = listOf(
            AdminActivityLog("al_1", "admin@dalili.com", "تم تهيئة دليل المساعدة ونطاقات البحث الافتراضية", "Setup directory help manuals and default search structures"),
            AdminActivityLog("al_2", "admin@dalili.com", "تثبيت مقدم خدمة متميز 'المهندس أحمد مصطفى'", "Pinned premium provider احمد مصطفى")
        )

        // Loyalty LOG
        _loyaltyPointsLog.value = listOf(
            LoyaltyPoints("l_1", "user@example.com", 20, "مشاركة التطبيق مع الأصدقاء", "Share application with friends"),
            LoyaltyPoints("l_2", "user@example.com", 15, "إضافة تقييم ذكي ومفصل لمزود الخدمة أحمد مصطفى", "Detailed review and rating submission")
        )

        // Appointments logs
        _appointments.value = listOf(
            Appointment("ap_1", "prov_1", "المهندس أحمد مصطفى", "user@example.com", "غداً الساعة 10:00 صباحاً", "Tomorrow at 10:00 AM", System.currentTimeMillis() + 86400000L)
        )
    }

    private fun observeRoomSync() {
        // Feed in room updates for categories and providers
        viewModelScope.launch {
            categoryDao.getAllCategories().collect { cached ->
                if (cached.isNotEmpty() && !_isOnline.value) {
                    _categories.value = cached.map {
                        Category(it.id, it.nameAr, it.nameEn, it.iconName, it.isPinned, it.order)
                    }
                }
            }
        }
        viewModelScope.launch {
            providerDao.getAllProviders().collect { cached ->
                if (cached.isNotEmpty() && !_isOnline.value) {
                    _providers.value = cached.map {
                        Provider(
                            it.id, it.name, it.phone, it.categoryId, it.subcategoryId,
                            it.personalPhotoUrl, it.workspacePhotoUrl, it.city, it.neighborhood,
                            it.latitude, it.longitude, it.isVerified, it.isPremium,
                            it.rating, it.reviewCount, it.isPinned, it.viewsCount,
                            it.responseTimeMs, "file://dummy_doc", it.pointsRedeemOption
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Core Functions / Actions
    // -------------------------------------------------------------

    fun setOnlineStatus(online: Boolean) {
        _isOnline.value = online
        val action = if (online) "تم استعادة الاتصال - مزامنة حية" else "التشغيل في وضع عدم الاتصال"
        val timeLog = "Time: ${System.currentTimeMillis()} | $action"
        _syncLogs.value = listOf(timeLog) + _syncLogs.value

        if (online) {
            performSyncWithFirestore()
        }
    }

    fun toggleDataSavingMode() {
        _isDataSavingMode.value = !_isDataSavingMode.value
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectProvider(id: String) {
        _selectedProviderId.value = id
        // Track sections visit
        val provider = _providers.value.find { it.id == id }
        if (provider != null) {
            logSectionVisit(provider.categoryId)
            _providers.value = _providers.value.map {
                if (it.id == id) it.copy(viewsCount = it.viewsCount + 1) else it
            }
        }
        navigateTo("provider_detail")
    }

    fun selectCategory(catId: String) {
        _selectedCategoryId.value = catId
        logSectionVisit(catId)
        navigateTo("providers_list")
    }

    private fun logSectionVisit(catId: String) {
        val newVisit = SectionVisit(
            id = "sv_${System.currentTimeMillis()}",
            userEmail = _currentUserEmail.value,
            categoryId = catId,
            timestamp = System.currentTimeMillis()
        )
        _sectionVisits.value = _sectionVisits.value + newVisit
    }

    // Suggest similar providers in the category visited MOST often
    fun getSuggestedBasedOnActivity(): List<Provider> {
        val mostVisitedCatId = _sectionVisits.value
            .filter { it.userEmail == _currentUserEmail.value }
            .groupBy { it.categoryId }
            .maxByOrNull { it.value.size }
            ?.key ?: return _providers.value.filter { it.isPremium || it.isPinned }

        return _providers.value.filter { it.categoryId == mostVisitedCatId }
    }

    // Advanced search algorithm incorporating:
    // Radius, Word Query, City, Neighborhood, Rating, Page size (infinite scrolling)
    fun getFilteredProviders(): List<Provider> {
        var list = _providers.value

        // Standard Advanced Filters
        val query = searchQuery.value.trim().lowercase()
        if (query.isNotEmpty()) {
            list = list.filter {
                it.name.lowercase().contains(query) ||
                        it.phone.contains(query) ||
                        it.city.lowercase().contains(query) ||
                        it.neighborhood.lowercase().contains(query)
            }
        }

        val cityFilter = searchCity.value.trim()
        if (cityFilter.isNotEmpty()) {
            list = list.filter { it.city.equals(cityFilter, ignoreCase = true) }
        }

        val neighFilter = searchNeighborhood.value.trim()
        if (neighFilter.isNotEmpty()) {
            list = list.filter { it.neighborhood.contains(neighFilter, ignoreCase = true) }
        }

        val phoneFilter = searchPhone.value.trim()
        if (phoneFilter.isNotEmpty()) {
            list = list.filter { it.phone.contains(phoneFilter) }
        }

        if (searchRatingMin.value > 0f) {
            list = list.filter { it.rating >= searchRatingMin.value }
        }

        // Circular Radius search filters (GPS Distance calculation using Haversine)
        // Amman central point used as mockup user location (31.95, 35.91)
        val rInput = searchRadiusInput.value.toDoubleOrNull()
        if (rInput != null && rInput > 0) {
            val userLat = 31.95
            val userLng = 35.91
            val maxLimit = _settings.value.maxSearchRadiusKm
            val effectiveRadius = if (rInput > maxLimit) maxLimit else rInput

            list = list.filter {
                val distance = calculateDistanceInKm(userLat, userLng, it.latitude, it.longitude)
                distance <= effectiveRadius
            }
        }

        // Sorting: Subscribing Featured providers ALWAYS show at very top
        list = list.sortedWith(compareByDescending<Provider> { it.isPremium }.thenByDescending { it.isPinned }.thenByDescending { it.rating })

        // Paginate using infinite scrolling pages count
        val totalToTake = _currentPageOffset.value * _pageSize.value
        return list.take(totalToTake)
    }

    fun hasMoreFilteredData(): Boolean {
        // Check if there are indeed more elements than we are currently reading
        // Reset or verify sizes
        val query = searchQuery.value.trim().lowercase()
        var baseList = _providers.value
        if (query.isNotEmpty()) {
            baseList = baseList.filter { it.name.lowercase().contains(query) }
        }
        return baseList.size > (_currentPageOffset.value * _pageSize.value)
    }

    fun loadNextPage() {
        if (hasMoreFilteredData()) {
            _currentPageOffset.value += 1
        }
    }

    private fun calculateDistanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in KM
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    // -------------------------------------------------------------
    // Sync to Room Database (Offline Mirroring)
    // -------------------------------------------------------------
    fun performSyncWithFirestore() {
        viewModelScope.launch {
            try {
                // Mimic network fetch
                delay(1000)

                // Populate Room DB with current Live data to preserve offline consistency
                categoryDao.clearAll()
                categoryDao.insertCategories(_categories.value.map {
                    CachedCategory(it.id, it.nameAr, it.nameEn, it.iconName, it.isPinned, it.order)
                })

                providerDao.clearAll()
                providerDao.insertProviders(_providers.value.map {
                    CachedProvider(
                        it.id, it.name, it.phone, it.categoryId, it.subcategoryId,
                        it.personalPhotoUrl, it.workspacePhotoUrl, it.city, it.neighborhood,
                        it.latitude, it.longitude, it.isVerified, it.isPremium,
                        it.rating, it.reviewCount, it.isPinned, it.viewsCount,
                        it.responseTimeMs, it.pointsRedeemOption
                    )
                })

                val timeLog = "Time: ${System.currentTimeMillis()} | مزامنة ناجحة لقاعدة البيانات مع Firestore"
                _syncLogs.value = listOf(timeLog) + _syncLogs.value
            } catch (e: Exception) {
                val errorLog = "Error: Mismatched SQLite: ${e.localizedMessage}"
                _syncLogs.value = listOf(errorLog) + _syncLogs.value
            }
        }
    }

    // -------------------------------------------------------------
    // Smart AI assistant engine (Manual Online/Offline with FAQ)
    // -------------------------------------------------------------
    private val _smartAssistantMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("welcome", "assistant_room", "assistant", "دليلي", "مرحباً بك! أنا مساعد دليلي الذكي. كيف يمكنني مساعدتك اليوم؟ يمكنك البحث عن أي سؤال عقاري، طبي، صحي، أو كيفية تفعيل مميزات الآدمن."))
    )
    val smartAssistantMessages: StateFlow<List<ChatMessage>> = _smartAssistantMessages.asStateFlow()

    fun sendSmartAssistantMessage(text: String) {
        if (text.trim().isEmpty()) return

        val userMsg = ChatMessage(
            id = "sam_${System.currentTimeMillis()}",
            roomId = "assistant_room",
            senderId = "user",
            senderName = "مستخدم",
            message = text,
            timestamp = System.currentTimeMillis()
        )
        _smartAssistantMessages.value = _smartAssistantMessages.value + userMsg

        viewModelScope.launch {
            delay(800) // response latency
            val ans = findFaqOrGptAnswer(text)
            val assistantResponse = ChatMessage(
                id = "sam_ans_${System.currentTimeMillis()}",
                roomId = "assistant_room",
                senderId = "assistant",
                senderName = "دليلي",
                message = ans,
                timestamp = System.currentTimeMillis()
            )
            _smartAssistantMessages.value = _smartAssistantMessages.value + assistantResponse
        }
    }

    private fun findFaqOrGptAnswer(query: String): String {
        val q = query.trim().lowercase()

        // Match against database FAQ elements
        val match = _faqs.value.find {
            it.questionAr.contains(q, ignoreCase = true) ||
                    it.questionEn.contains(q, ignoreCase = true) ||
                    it.answerAr.contains(q, ignoreCase = true) ||
                    it.answerEn.contains(q, ignoreCase = true)
        }
        if (match != null) {
            return "بناءً على دليل المساعدة المعتمد بـ Firestore:\n\n${match.answerAr}\n\nEnglish:\n${match.answerEn}"
        }

        // Fallback intelligent answers
        return when {
            q.contains("نقاط") || q.contains("loyalty") || q.contains("point") ->
                "نظام نقاط الولاء يمنحك نقاطاً تلقائية عند تزويد المطورين بالتقييمات أو مشاركة التطبيق مع الآخرين، وتستبدل بخصومات فورية!"
            q.contains("اشتراك") || q.contains("premium") ->
                "مرحباً بك، اشتراك التثبيت المميز يتيح لمقدم الخدمة تصدر نتائج الفرز والبحث طوال مدة صلاحية الاشتراك، بإمكان الآدمن التعديل عليها من لوحة الإدارة."
            q.contains("تواصل") || q.contains("دعم") ->
                "بإمكانك التواصل مع الدعم الفني مباشرة عبر واتساب: 009627900000 أو الهاتف الخاص بنا المتواجد في صفحة 'عن التطبيق'."
            else -> "عذراً، لم أجد إجابة مطابقة تماماً في ذاكرتي المحلية. بإمكانك كتابة كلمات مفتاحية مثل: 'نقاط اللقاء'، 'توثيق الحساب'، 'الاشتراكات المميزة' أو مراسلة المشرفين!"
        }
    }

    // -------------------------------------------------------------
    // Review and Rating Actions with LOYALTY POINTS trigger
    // -------------------------------------------------------------
    fun submitReview(providerId: String, name: String, rating: Float, comment: String) {
        val id = "rev_${System.currentTimeMillis()}"
        val newReview = Review(id, providerId, "user_" + System.currentTimeMillis(), name, rating, comment)
        _reviews.value = _reviews.value + newReview

        // Recalculate provider overall ratings
        _providers.value = _providers.value.map {
            if (it.id == providerId) {
                val count = it.reviewCount + 1
                val totalStars = (it.rating * it.reviewCount) + rating
                it.copy(reviewCount = count, rating = totalStars / count)
            } else {
                it
            }
        }

        // Reward loyalty points
        val pointsReward = 15
        _userPoints.value += pointsReward
        val pointsEvent = LoyaltyPoints(
            id = "ly_${System.currentTimeMillis()}",
            userId = _currentUserEmail.value,
            points = pointsReward,
            reasonAr = "تقييم مزود الخدمة: $name",
            reasonEn = "Reviewed provider: $name"
        )
        _loyaltyPointsLog.value = listOf(pointsEvent) + _loyaltyPointsLog.value
    }

    fun shareAppAction() {
        val pointsReward = 20
        _userPoints.value += pointsReward
        val pointsEvent = LoyaltyPoints(
            id = "ly_${System.currentTimeMillis()}",
            userId = _currentUserEmail.value,
            points = pointsReward,
            reasonAr = "مشاركة كود التطبيق المباشر مع الأصدقاء",
            reasonEn = "Shared Dalili App application code"
        )
        _loyaltyPointsLog.value = listOf(pointsEvent) + _loyaltyPointsLog.value
    }

    fun redeemGiftPoints(providerId: String) {
        val provider = _providers.value.find { it.id == providerId } ?: return
        val cost = provider.pointsRedeemOption
        if (_userPoints.value >= cost) {
            _userPoints.value -= cost
            val pointsEvent = LoyaltyPoints(
                id = "ly_${System.currentTimeMillis()}",
                userId = _currentUserEmail.value,
                points = -cost,
                reasonAr = "استبدال خصم مالي 15% لدى ${provider.name}",
                reasonEn = "Redeemed 15% discount for provider: ${provider.name}"
            )
            _loyaltyPointsLog.value = listOf(pointsEvent) + _loyaltyPointsLog.value
        }
    }

    // -------------------------------------------------------------
    // Chat System between Users & Providers (Supervised by Admin)
    // -------------------------------------------------------------
    fun openChatWith(provId: String) {
        val provider = _providers.value.find { it.id == provId } ?: return
        val existingRoom = _chatRooms.value.find {
            it.providerId == provId && it.userEmail == _currentUserEmail.value
        }
        if (existingRoom != null) {
            _activeRoomId.value = existingRoom.id
        } else {
            val newRoom = ChatRoom(
                id = "room_${System.currentTimeMillis()}",
                userEmail = _currentUserEmail.value,
                providerId = provId,
                providerName = provider.name,
                isMuted = false
            )
            _chatRooms.value = _chatRooms.value + newRoom
            _activeRoomId.value = newRoom.id

            // Prepopulate some initial context chat
            val initialMessage = ChatMessage(
                id = "msg_init_${System.currentTimeMillis()}",
                roomId = newRoom.id,
                senderId = "provider",
                senderName = provider.name,
                message = "مرحباً بك! يسعدني خدمتك والبدء بالنقاش، تفضل بطرح تفاصيل طلبك.",
                timestamp = System.currentTimeMillis() - 5000L
            )
            _currentRoomMessages.value = listOf(initialMessage)
        }
        navigateTo("chat_screen")
    }

    fun getActiveRoomMessages(): List<ChatMessage> {
        val activeRoom = _activeRoomId.value
        return _currentRoomMessages.value.filter { it.roomId == activeRoom }
    }

    fun sendChatMessage(msgText: String) {
        if (msgText.trim().isEmpty() || _activeRoomId.value.isEmpty()) return

        val activeRoom = _chatRooms.value.find { it.id == _activeRoomId.value }
        if (activeRoom != null && activeRoom.isMuted) {
            // Muted by Admin
            return
        }

        val idMsg = "msg_${System.currentTimeMillis()}"
        val senderId = if (_currentUserRole.value == "Provider") "provider" else "user"
        val senderNm = if (_currentUserRole.value == "Provider") "مزود الخدمة" else "مستخدم دليلي"

        val newMsg = ChatMessage(idMsg, _activeRoomId.value, senderId, senderNm, msgText, System.currentTimeMillis())
        _currentRoomMessages.value = _currentRoomMessages.value + newMsg

        // If user sent it, simulate Provider smart replies automatically
        if (senderId == "user") {
            viewModelScope.launch {
                delay(1500)
                val responseMsg = ChatMessage(
                    id = "msg_resp_${System.currentTimeMillis()}",
                    roomId = _activeRoomId.value,
                    senderId = "provider",
                    senderName = activeRoom?.providerName ?: "مزود الخدمة",
                    message = "تم استلام رسالتك بنجاح! سأتصل بك هاتفياً في غضون دقائق قليلة لمتابعة الموعد والاتفاق.",
                    timestamp = System.currentTimeMillis()
                )
                _currentRoomMessages.value = _currentRoomMessages.value + responseMsg
            }
        }
    }

    // Admin commands for Chat Supervision
    fun toggleChatMute(roomId: String) {
        _chatRooms.value = _chatRooms.value.map {
            if (it.id == roomId) {
                logAdminAction("تعديل حالة كتم الغرفة ${it.id}", "Altered chat muting parameter for room ${it.id}")
                it.copy(isMuted = !it.isMuted)
            } else it
        }
    }

    // -------------------------------------------------------------
    // Provider specific Actions (Invoices, Registrations, Billing)
    // -------------------------------------------------------------
    fun submitVerificationRequest(name: String, phone: String, city: String, neigh: String, catId: String, businessId: String, docUrl: String) {
        val id = "prov_${System.currentTimeMillis()}"
        val newRequest = Provider(
            id = id,
            name = name,
            phone = phone,
            categoryId = catId,
            subcategoryId = "",
            personalPhotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            workspacePhotoUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=300",
            city = city,
            neighborhood = neigh,
            latitude = 31.95 + (Math.random() - 0.5) * 0.1,
            longitude = 35.91 + (Math.random() - 0.5) * 0.1,
            isVerified = false,
            isPremium = false,
            rating = 5.0f,
            reviewCount = 0,
            isPinned = false,
            viewsCount = 1,
            responseTimeMs = 200000L
        )

        _providers.value = _providers.value + newRequest

        // Create Official Auditing Document Request
        val requestDoc = VerificationDocument(
            id = "doc_${System.currentTimeMillis()}",
            providerId = id,
            providerName = name,
            documentType = "السجل التجاري وقوائم النقابة - $businessId",
            fileUrl = docUrl.ifBlank { "مرفق_مستند_رسمي.png" },
            status = "Pending"
        )
        _verifications.value = _verifications.value + requestDoc
    }

    fun optInSubscription() {
        // Find current authenticated provider
        val email = _currentUserEmail.value
        // For simplicity associate subscription option withاحمد مصطفى or similar or let user opt-in
        _providers.value = _providers.value.map {
            if (it.phone == "0791234567") {
                it.copy(isPremium = true)
            } else it
        }
    }

    fun providerCreateInvoice(userMail: String, sum: Double, service: String) {
        val activeProviderName = "شركة خدمات الدليل الموحدة"
        val invoice = Invoice(
            id = "inv_${System.currentTimeMillis()}",
            providerId = _selectedProviderId.value.ifBlank { "prov_1" },
            providerName = activeProviderName,
            userEmail = userMail,
            amount = sum,
            serviceDetails = service,
            status = "Pending"
        )
        _invoices.value = _invoices.value + invoice
    }

    fun payInvoice(invoiceId: String) {
        _invoices.value = _invoices.value.map {
            if (it.id == invoiceId) it.copy(status = "Paid") else it
        }
    }

    // Book appointment directly with real notification simulated
    fun bookAppointment(providerId: String, schedAr: String, schedEn: String) {
        val provider = _providers.value.find { it.id == providerId } ?: return
        val appointment = Appointment(
            id = "ap_${System.currentTimeMillis()}",
            providerId = providerId,
            providerName = provider.name,
            userEmail = _currentUserEmail.value,
            dateTimeAr = schedAr,
            dateTimeEn = schedEn,
            timestamp = System.currentTimeMillis() + 86400000L, // Tomorrow default
            status = "Scheduled",
            reminderSent = false
        )
        _appointments.value = _appointments.value + appointment
    }

    // -------------------------------------------------------------
    // Admin Authority Actions
    // -------------------------------------------------------------

    private fun logAdminAction(ar: String, en: String) {
        val log = AdminActivityLog(
            id = "al_${System.currentTimeMillis()}",
            modEmail = _currentUserEmail.value,
            actionAr = ar,
            actionEn = en,
            timestamp = System.currentTimeMillis()
        )
        _activityLogs.value = listOf(log) + _activityLogs.value
    }

    // Top Bar Customization Sync
    fun updateTopBarOptions(showRf: Boolean, showLng: Boolean, showThm: Boolean, arTitle: String, enTitle: String) {
        val updated = _settings.value.copy(
            showRefreshIcon = showRf,
            showLanguageIcon = showLng,
            showThemeToggleIcon = showThm,
            topBarTitleAr = arTitle,
            topBarTitleEn = enTitle
        )
        _settings.value = updated
        logAdminAction("تعديل أزرار وأيقونات الشريط العلوي والمزامنة الفورية", "Modified Top-Bar icons settings and synchronized live")
    }

    // Color/Themes Control
    fun changeThemePreference(themeName: String, customAccentHex: String) {
        val updated = _settings.value.copy(
            appTheme = themeName,
            primaryColorHex = customAccentHex.ifBlank { "#3B82F6" }
        )
        _settings.value = updated
        logAdminAction("تعديل سمة التطبيق الأساسية إلى $themeName", "Modified app system theme to $themeName")
    }

    // Greeting Banner Customization
    fun updateGreetingBanner(textAr: String, textEn: String, sizeSp: Float, textColor: String, bgUrl: String) {
        val updated = _settings.value.copy(
            welcomeText = textAr,
            welcomeTextEn = textEn,
            welcomeTextSize = sizeSp,
            welcomeTextColorHex = textColor,
            welcomeBgUrl = bgUrl
        )
        _settings.value = updated
        logAdminAction("تعديل نص الترحيب في الصفحة الرئيسية", "Updated home welcome message configuration and background image")
    }

    // Banner Advertisements Administration
    fun addNewPromotionBanner(imgUrl: String, redirect: String, seconds: Int, sizeStr: String) {
        val newBanner = PromotionBanner(
            id = "b_${System.currentTimeMillis()}",
            imageUrl = imgUrl,
            redirectLink = redirect,
            durationSeconds = seconds,
            size = sizeStr,
            type = "Standard",
            isActive = true
        )
        _banners.value = _banners.value + newBanner
        logAdminAction("إنشاء لافتة إعلانية جديدة للاستهداف بنطاق $seconds ثواني", "Created new advertising banner redirecting to $redirect for $seconds seconds")
    }

    fun removePromotionBanner(id: String) {
        _banners.value = _banners.value.filter { it.id != id }
        logAdminAction("حذف لافتة إعلانية ممولة", "Deleted advertising banner $id")
    }

    // Verified badge allocation (المستندات)
    fun verifyProviderStatus(providerId: String, approve: Boolean) {
        // Modify verification document status
        _verifications.value = _verifications.value.map {
            if (it.providerId == providerId) it.copy(status = if (approve) "Approved" else "Rejected") else it
        }

        // Apply verifiable badge to the provider in Firestore
        _providers.value = _providers.value.map {
            if (it.id == providerId) {
                it.copy(isVerified = approve)
            } else it
        }

        val provName = _providers.value.find { it.id == providerId }?.name ?: providerId
        logAdminAction("اعتماد وتوثيق حالة مقدم الخدمة: $provName", "Approved and certified verification details for $provName")
    }

    // Disable / Edit subscription parameters
    fun toggleProviderPremium(providerId: String) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) {
                val nextState = !it.isPremium
                val statWord = if (nextState) "تفعيل" else "إيقاف"
                logAdminAction("$statWord ميزة الاشتراك وحالة بريميوم لـ ${it.name}", "Altered subscription status for ${it.name}")
                it.copy(isPremium = nextState)
            } else it
        }
    }

    // Pin provider/category in searches manually
    fun togglePinProvider(providerId: String) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) {
                val nextPin = !it.isPinned
                val statWord = if (nextPin) "تثبيت" else "إلغاء تثبيت"
                logAdminAction("$statWord مقدم الخدمة ${it.name} في صدارة البحث", "Altered pinned status for ${it.name}")
                it.copy(isPinned = nextPin)
            } else it
        }
    }

    fun togglePinCategory(catId: String) {
        _categories.value = _categories.value.map {
            if (it.id == catId) {
                val nextPin = !it.isPinned
                val statWord = if (nextPin) "تثبيت" else "إلغاء تثبيت"
                logAdminAction("$statWord القسم الأصلي في الصدارة", "Altered category pinned status in homepage")
                it.copy(isPinned = nextPin)
            } else it
        }
    }

    // Add Section Main Category / Subcategory
    fun addNewCategory(nameAr: String, nameEn: String, iconStr: String) {
        val newCat = Category(
            id = "cat_${System.currentTimeMillis()}",
            nameAr = nameAr,
            nameEn = nameEn,
            iconName = iconStr,
            isPinned = false,
            order = _categories.value.size + 1
        )
        _categories.value = _categories.value + newCat
        logAdminAction("إضافة قسم رئيسي جديد: $nameAr", "Added new main category index: $nameAr")
        performSyncWithFirestore()
    }

    fun addNewSubcategory(catId: String, nameAr: String, nameEn: String) {
        val newSub = Subcategory(
            id = "sub_${System.currentTimeMillis()}",
            categoryId = catId,
            nameAr = nameAr,
            nameEn = nameEn
        )
        _subcategories.value = _subcategories.value + newSub
        logAdminAction("إضافة قسم فرعي جديد تحت الرمز $catId", "Added new subcategory under reference code $catId")
    }

    // Moderator Administration
    fun addModeratorUser(email: String, passes: String) {
        val m = Moderator(
            id = "mod_${System.currentTimeMillis()}",
            email = email,
            passwordPlain = passes,
            isBlocked = false
        )
        _moderators.value = _moderators.value + m
        logAdminAction("إضافة مشرف لوحة تحكم جديد: $email", "Registered new system moderator user: $email")
    }

    fun changeModeratorPassword(email: String, newPass: String) {
        _moderators.value = _moderators.value.map {
            if (it.email == email) {
                logAdminAction("تعديل كلمة مرور حساب المشرف $email", "Updated plain registry password for moderator $email")
                it.copy(passwordPlain = newPass)
            } else it
        }
    }

    // FAQs administration (Firestore synchronized)
    fun addFaqItem(qAr: String, aAr: String, qEn: String, aEn: String) {
        val item = FaqItem(
            id = "faq_${System.currentTimeMillis()}",
            questionAr = qAr,
            answerAr = aAr,
            questionEn = qEn,
            answerEn = aEn,
            order = _faqs.value.size + 1
        )
        _faqs.value = _faqs.value + item
        logAdminAction("إضافة دليل إجابة سؤال شائع بـ Firestore", "Created new Firestore sync FAQ schema")
    }

    fun deleteFaqItem(id: String) {
        _faqs.value = _faqs.value.filter { it.id != id }
        logAdminAction("حذف سؤال معتمد بقاعدة البيانات", "Removed FAQ structure $id from Firestore")
    }

    // Radius control settings
    fun updateMaxSearchRadius(maxLim: Double) {
        _settings.value = _settings.value.copy(maxSearchRadiusKm = maxLim)
        logAdminAction("تعديل الحد الأقصى الافتراضي لنطاق البحث إلى $maxLim كم", "Updated max permitted circle radius search limit to $maxLim KM")
    }

    // -------------------------------------------------------------
    // Scheduled Firestore Backup Routine (To internal folders / logs)
    // -------------------------------------------------------------
    fun triggerFirestoreBackup(backupPath: String) {
        viewModelScope.launch {
            logAdminAction("تم استدعاء نسخة احتياطية محلية لـ Firestore", "Triggered automated daily raw backup structure generation")
            // Simulate saving JSON structures representing full Firestore to download/storage path
            val backupData = """
                {"categories": ${_categories.value.size}, "providers": ${_providers.value.size}, "appointments": ${_appointments.value.size}}
            """.trimIndent()
            // Log confirmation sync
            val logMessage = "Backup Saved successfully at path: $backupPath"
            _syncLogs.value = listOf("Backup Success at $backupPath - $backupData") + _syncLogs.value
        }
    }
}
