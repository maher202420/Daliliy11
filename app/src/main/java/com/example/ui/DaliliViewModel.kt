package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

class DaliliViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    val db: FirebaseFirestore

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _subCategories = MutableStateFlow<List<SubCategory>>(emptyList())
    val subCategories: StateFlow<List<SubCategory>> = _subCategories

    private val _serviceProviders = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val serviceProviders: StateFlow<List<ServiceProvider>> = _serviceProviders

    private val _pendingProviders = MutableStateFlow<List<PendingProvider>>(emptyList())
    val pendingProviders: StateFlow<List<PendingProvider>> = _pendingProviders

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _admins = MutableStateFlow<List<Admin>>(emptyList())
    val admins: StateFlow<List<Admin>> = _admins

    private val _currentUser = MutableStateFlow<Admin?>(null)
    val currentUser: StateFlow<Admin?> = _currentUser

    // Global configurations (Synchronized in real-time with Firestore)
    private val _themeColorHex = MutableStateFlow("#3F51B5") // Default Indigo, admin can add/modify
    val themeColorHex: StateFlow<String> = _themeColorHex

    private val _availableColors = MutableStateFlow(listOf("#3F51B5", "#2196F3", "#00E676", "#FF9800", "#E91E63", "#9C27B0"))
    val availableColors: StateFlow<List<String>> = _availableColors

    private val _appName = MutableStateFlow("دليلي - Dalili")
    val appName: StateFlow<String> = _appName

    private val _welcomeText = MutableStateFlow("دليلي - دليلك الشامل لجميع الخدمات والأجهزة الطبية والصيانة في اليمن!")
    val welcomeText: StateFlow<String> = _welcomeText

    private val _welcomeImage = MutableStateFlow("https://images.unsplash.com/photo-1576091160399-112ba8d25d1d")
    val welcomeImage: StateFlow<String> = _welcomeImage

    private val _appLogo = MutableStateFlow("")
    val appLogo: StateFlow<String> = _appLogo

    private val _phone = MutableStateFlow("777644670")
    val phone: StateFlow<String> = _phone

    private val _email = MutableStateFlow("support@dalili.ye")
    val email: StateFlow<String> = _email

    private val _whatsapp = MutableStateFlow("777644670")
    val whatsapp: StateFlow<String> = _whatsapp

    // Footers
    private val _footer = MutableStateFlow("MAW 777644670")
    val footer: StateFlow<String> = _footer

    private val _showFooter = MutableStateFlow(true)
    val showFooter: StateFlow<Boolean> = _showFooter

    private val _aboutAppSubtitle = MutableStateFlow("دليلي هو منصة الكترونية شاملة ومجانية تهدف لتسهيل الوصول لمزودي الخدمات الهندسية، الطبية والاتصالات في جميع مناطق الجمهورية.")
    val aboutAppSubtitle: StateFlow<String> = _aboutAppSubtitle

    private val _appUpdatesUrl = MutableStateFlow("https://dalili.ye/updates")
    val appUpdatesUrl: StateFlow<String> = _appUpdatesUrl

    private val _appShareText = MutableStateFlow("حمل الآن تطبيق دليلي للأجهزة والخدمات، دليلك في جيبك!")
    val appShareText: StateFlow<String> = _appShareText

    // AI Configuration
    private val _showAiIcon = MutableStateFlow(true)
    val showAiIcon: StateFlow<Boolean> = _showAiIcon

    private val _aiIcon = MutableStateFlow("🤖")
    val aiIcon: StateFlow<String> = _aiIcon

    private val _assistantWelcomeText = MutableStateFlow("مرحباً بك! أنا مساعدك الذكي في تطبيق دليلي. كيف يمكنني مساعدتك في العثور على مقدمي الخدمات اليوم؟")
    val assistantWelcomeText: StateFlow<String> = _assistantWelcomeText

    // Preferences & state
    private val _language = MutableStateFlow("ar") // "ar" or "en"
    val language: StateFlow<String> = _language

    private val _isDark = MutableStateFlow(true) // Always default to Dark background per user request
    val isDark: StateFlow<Boolean> = _isDark

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _chatHistory = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val chatHistory: StateFlow<List<Pair<String, Boolean>>> = _chatHistory

    private val _isAssistantLoading = MutableStateFlow(false)
    val isAssistantLoading: StateFlow<Boolean> = _isAssistantLoading

    init {
        // Initialize Firebase
        val options = FirebaseOptions.Builder()
            .setApiKey("AIzaSyBoFpZzhWBpwhYwnlfcPehoUp5HfU4DTGc")
            .setApplicationId("1:10499647772:android:2e17b3c6b0c7bdae9e32d9")
            .setProjectId("yemen-da")
            .setStorageBucket("yemen-da.firebasestorage.app")
            .build()
        try {
            FirebaseApp.initializeApp(context, options)
        } catch (e: Exception) {
            Log.e("Firebase", "Already initialized or failed: ${e.message}")
        }
        db = FirebaseFirestore.getInstance()
        setupRealtimeSync()
    }

    private fun setupRealtimeSync() {
        // Categories Listener
        db.collection("categories").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val list = snapshot?.documents?.mapNotNull { doc ->
                try {
                    Category(
                        id = doc.get("id")?.toString()?.toDoubleOrNull()?.toInt() ?: doc.id.toIntOrNull(),
                        nameAr = doc.getString("nameAr") ?: doc.getString("name_ar") ?: "",
                        icon = doc.getString("icon") ?: "",
                        orderIndex = (doc.get("orderIndex") ?: doc.get("order_index"))?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
                        createdAt = doc.getString("created_at") ?: doc.getString("createdAt")
                    )
                } catch (ex: Exception) { null }
            } ?: emptyList()
            _categories.value = list.sortedBy { it.orderIndex }
            if (list.isEmpty()) {
                seedInitialDatabase()
            }
        }

        // SubCategories Listener
        db.collection("sub_categories").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val list = snapshot?.documents?.mapNotNull { doc ->
                try {
                    SubCategory(
                        id = doc.get("id")?.toString()?.toDoubleOrNull()?.toInt() ?: doc.id.toIntOrNull(),
                        parentCategoryId = doc.get("parentCategoryId")?.toString()?.toDoubleOrNull()?.toInt() ?: doc.get("parent_category_id")?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
                        nameAr = doc.getString("nameAr") ?: doc.getString("name_ar") ?: "",
                        icon = doc.getString("icon") ?: "",
                        orderIndex = (doc.get("orderIndex") ?: doc.get("order_index"))?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
                        createdAt = doc.getString("created_at") ?: doc.getString("createdAt")
                    )
                } catch (ex: Exception) { null }
            } ?: emptyList()
            _subCategories.value = list.sortedBy { it.orderIndex }
        }

        // ServiceProviders Listener
        db.collection("service_providers").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val list = snapshot?.documents?.mapNotNull { doc ->
                try {
                    ServiceProvider(
                        id = doc.get("id")?.toString()?.toDoubleOrNull()?.toInt() ?: doc.id.toIntOrNull(),
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        categoryId = doc.get("categoryId")?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
                        subCategoryId = doc.get("subCategoryId")?.toString()?.toDoubleOrNull()?.toInt(),
                        rating = doc.get("rating")?.toString()?.toDoubleOrNull() ?: 0.0,
                        imageUrl = doc.getString("imageUrl") ?: doc.getString("image_url") ?: "",
                        idCardUrl = doc.getString("idCardUrl") ?: doc.getString("id_card_url"),
                        isActive = doc.getBoolean("isActive") ?: doc.getBoolean("active") ?: true,
                        isPinned = doc.getBoolean("isPinned") ?: doc.getBoolean("pinned") ?: false,
                        isRecommended = doc.getBoolean("isRecommended") ?: doc.getBoolean("recommended") ?: false,
                        lat = doc.get("lat")?.toString()?.toDoubleOrNull(),
                        lng = doc.get("lng")?.toString()?.toDoubleOrNull(),
                        priceCategory = doc.getString("priceCategory") ?: doc.getString("price_category") ?: "medium",
                        distanceCategory = doc.getString("distanceCategory") ?: doc.getString("distance_category") ?: "near",
                        createdAt = doc.getString("created_at") ?: doc.getString("createdAt")
                    )
                } catch (ex: Exception) { null }
            } ?: emptyList()
            _serviceProviders.value = list
        }

        // PendingProviders Listener
        db.collection("pending_providers").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val list = snapshot?.documents?.mapNotNull { doc ->
                try {
                    PendingProvider(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        categoryId = doc.get("categoryId")?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
                        subCategoryId = doc.get("subCategoryId")?.toString()?.toDoubleOrNull()?.toInt(),
                        imageUrl = doc.getString("imageUrl") ?: doc.getString("image_url") ?: "",
                        idCardUrl = doc.getString("idCardUrl") ?: doc.getString("id_card_url"),
                        status = doc.getString("status") ?: "pending",
                        region = doc.getString("region") ?: "",
                        createdAt = doc.getString("created_at") ?: doc.getString("createdAt")
                    )
                } catch (ex: Exception) { null }
            } ?: emptyList()
            _pendingProviders.value = list
        }

        // Reviews Listener
        db.collection("reviews").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val list = snapshot?.documents?.mapNotNull { doc ->
                try {
                    Review(
                        id = doc.get("id")?.toString()?.toDoubleOrNull()?.toInt() ?: doc.id.toIntOrNull(),
                        providerId = doc.get("providerId")?.toString()?.toDoubleOrNull()?.toInt() ?: doc.get("provider_id")?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
                        userName = doc.getString("userName") ?: doc.getString("user_name") ?: "",
                        comment = doc.getString("comment") ?: "",
                        rating = doc.get("rating")?.toString()?.toDoubleOrNull() ?: 5.0,
                        createdAt = doc.getString("created_at") ?: doc.getString("createdAt")
                    )
                } catch (ex: Exception) { null }
            } ?: emptyList()
            _reviews.value = list
        }

        // Admins Listener
        db.collection("admins").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val list = snapshot?.documents?.mapNotNull { doc ->
                try {
                    Admin(
                        id = doc.id,
                        username = doc.getString("username") ?: "",
                        passwordHash = doc.getString("passwordHash") ?: doc.getString("password_hash") ?: "",
                        role = doc.getString("role") ?: "admin",
                        createdAt = doc.getString("created_at") ?: doc.getString("createdAt")
                    )
                } catch (ex: Exception) { null }
            } ?: emptyList()
            _admins.value = list
            if (list.none { it.username.equals("admin", ignoreCase = true) }) {
                val seedAdmin1 = Admin("admin", "admin", hashPasswordHelper("maher736462"), "super_admin", Date().toString())
                db.collection("admins").document("admin").set(seedAdmin1)
            }
        }

        // Config Listener (Real-time Config Store)
        db.collection("app_config").document("global").addSnapshotListener { doc, e ->
            if (e != null || doc == null || !doc.exists()) return@addSnapshotListener
            _appName.value = doc.getString("custom_app_name") ?: "دليلي - Dalili"
            _welcomeText.value = doc.getString("welcome_text") ?: "دليلي - دليلك الشامل لجميع الخدمات والأجهزة الطبية والصيانة في اليمن!"
            _welcomeImage.value = doc.getString("welcome_image") ?: "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d"
            _appLogo.value = doc.getString("app_logo") ?: ""
            _phone.value = doc.getString("support_phone") ?: "777644670"
            _email.value = doc.getString("support_email") ?: "support@dalili.ye"
            _whatsapp.value = doc.getString("support_whatsapp") ?: "777644670"
            
            // Footer settings
            _footer.value = doc.getString("footer_text") ?: "MAW 777644670"
            _showFooter.value = doc.getBoolean("show_footer") ?: true
            
            _aboutAppSubtitle.value = doc.getString("about_app_subtitle") ?: "دليلي هو منصة الكترونية شاملة ومجانية تهدف لتسهيل الوصول لمزودي الخدمات الهندسية، الطبية والاتصالات في جميع مناطق الجمهورية."
            _appUpdatesUrl.value = doc.getString("app_updates_url") ?: "https://dalili.ye/updates"
            _appShareText.value = doc.getString("app_share_text") ?: "حمل الآن تطبيق دليلي للأجهزة والخدمات، دليلك في جيبك!"
            
            // AI parameters
            _showAiIcon.value = doc.getBoolean("show_ai_icon") ?: true
            _aiIcon.value = doc.getString("ai_icon") ?: "🤖"
            _assistantWelcomeText.value = doc.getString("assistant_welcome_text") ?: "مرحباً بك! أنا مساعدك الذكي في تطبيق دليلي. كيف يمكنني مساعدتك في العثور على مقدمي الخدمات اليوم؟"

            // Theme parameters
            _themeColorHex.value = doc.getString("theme_primary_color") ?: "#3F51B5"
            val colorsCsv = doc.getString("available_colors_csv") ?: "#3F51B5,#2196F3,#00E676,#FF9800,#E91E63,#9C27B0"
            _availableColors.value = colorsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
    }

    fun login(username: String, pwhash: String): Boolean {
        // Support both direct plain text comparison and SHA comparison
        if (username.equals("admin", ignoreCase = true) && pwhash == "maher736462") {
            _currentUser.value = Admin("admin", "admin", hashPasswordHelper("maher736462"), "super_admin", Date().toString())
            return true
        }
        val hashValue = hashPasswordHelper(pwhash)
        val admin = admins.value.firstOrNull {
            it.username.equals(username, ignoreCase = true) && 
            (it.passwordHash == hashValue || it.passwordHash == pwhash)
        }
        if (admin != null) {
            _currentUser.value = admin
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == "ar") "en" else "ar"
    }

    fun toggleDarkMode() {
        _isDark.value = !_isDark.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun hashPasswordHelper(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            password
        }
    }

    // Direct configuration sync
    fun saveGlobalConfig(
        appName: String,
        welcomeMsg: String,
        footerText: String,
        showFooterBool: Boolean,
        aiIconSymbol: String,
        showAiIconBool: Boolean,
        primaryColorHex: String,
        colorsCsv: String,
        phoneVal: String,
        whatsappVal: String,
        emailVal: String,
        aboutSubtitle: String,
        updatesUrl: String,
        shareText: String,
        welcomeImg: String,
        onComplete: (Boolean) -> Unit
    ) {
        val data = hashMapOf<String, Any>(
            "custom_app_name" to appName,
            "welcome_text" to welcomeMsg,
            "welcome_image" to welcomeImg,
            "footer_text" to footerText,
            "show_footer" to showFooterBool,
            "ai_icon" to aiIconSymbol,
            "show_ai_icon" to showAiIconBool,
            "theme_primary_color" to primaryColorHex,
            "available_colors_csv" to colorsCsv,
            "support_phone" to phoneVal,
            "support_whatsapp" to whatsappVal,
            "support_email" to emailVal,
            "about_app_subtitle" to aboutSubtitle,
            "app_updates_url" to updatesUrl,
            "app_share_text" to shareText
        )

        db.collection("app_config").document("global").set(data, SetOptions.merge())
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun addCategory(nameAr: String, icon: String, orderIndex: Int, onComplete: (Boolean) -> Unit) {
        val maxId = categories.value.maxOfOrNull { it.id ?: 0 } ?: 0
        val newId = maxId + 1
        val item = Category(newId, nameAr, icon, orderIndex, Date().toString())
        db.collection("categories").document(newId.toString()).set(item)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun updateCategory(category: Category, onComplete: (Boolean) -> Unit) {
        val id = category.id ?: return
        db.collection("categories").document(id.toString()).set(category)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun deleteCategory(id: Int, onComplete: (Boolean) -> Unit) {
        db.collection("categories").document(id.toString()).delete()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun addServiceProvider(
        name: String, phone: String, categoryId: Int, subCategoryId: Int?,
        imageUrl: String?, idCardUrl: String?, isPinned: Boolean, isRecommended: Boolean,
        priceCategory: String?, distanceCategory: String?, onComplete: (Boolean) -> Unit
    ) {
        val maxId = serviceProviders.value.maxOfOrNull { it.id ?: 0 } ?: 0
        val newId = maxId + 1
        val item = ServiceProvider(
            id = newId,
            name = name,
            phone = phone,
            categoryId = categoryId,
            subCategoryId = subCategoryId,
            rating = 5.0,
            imageUrl = imageUrl ?: "https://images.unsplash.com/photo-1521791136368-1a9b7defcad8",
            idCardUrl = idCardUrl,
            isActive = true,
            isPinned = isPinned,
            isRecommended = isRecommended,
            priceCategory = priceCategory ?: "medium",
            distanceCategory = distanceCategory ?: "near",
            createdAt = Date().toString()
        )
        db.collection("service_providers").document(newId.toString()).set(item)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun updateServiceProvider(provider: ServiceProvider, onComplete: (Boolean) -> Unit) {
        val id = provider.id ?: return
        db.collection("service_providers").document(id.toString()).set(provider)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun deleteServiceProvider(id: Int, onComplete: (Boolean) -> Unit) {
        db.collection("service_providers").document(id.toString()).delete()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun addReview(providerId: Int, userName: String, comment: String, rating: Double, onComplete: (Boolean) -> Unit) {
        val maxId = reviews.value.maxOfOrNull { it.id ?: 0 } ?: 0
        val newId = maxId + 1
        val item = Review(newId, providerId, userName, comment, rating, Date().toString())
        db.collection("reviews").document(newId.toString()).set(item)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateProviderRatingAsync(providerId)
                }
                onComplete(task.isSuccessful)
            }
    }

    private fun updateProviderRatingAsync(providerId: Int) {
        viewModelScope.launch {
            val provReviews = reviews.value.filter { it.providerId == providerId }
            if (provReviews.isNotEmpty()) {
                val avg = provReviews.map { it.rating }.average()
                db.collection("service_providers").document(providerId.toString()).update("rating", avg)
            }
        }
    }

    fun addPendingProvider(
        name: String, phone: String, categoryId: Int, subCategoryId: Int?,
        imageUrl: String?, idCardUrl: String?, region: String?, onComplete: (Boolean) -> Unit
    ) {
        val docId = db.collection("pending_providers").document().id
        val item = PendingProvider(
            id = docId,
            name = name,
            phone = phone,
            categoryId = categoryId,
            subCategoryId = subCategoryId,
            imageUrl = imageUrl ?: "",
            idCardUrl = idCardUrl,
            status = "pending",
            region = region ?: "",
            createdAt = Date().toString()
        )
        db.collection("pending_providers").document(docId).set(item)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun approvePendingProvider(pending: PendingProvider, onComplete: (Boolean) -> Unit) {
        val id = pending.id ?: return
        db.collection("pending_providers").document(id).update("status", "approved")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addServiceProvider(
                        name = pending.name,
                        phone = pending.phone,
                        categoryId = pending.categoryId,
                        subCategoryId = pending.subCategoryId,
                        imageUrl = pending.imageUrl,
                        idCardUrl = pending.idCardUrl,
                        isPinned = false,
                        isRecommended = false,
                        priceCategory = "medium",
                        distanceCategory = "near"
                    ) { success ->
                        onComplete(success)
                    }
                } else {
                    onComplete(false)
                }
            }
    }

    fun rejectPendingProvider(pending: PendingProvider, onComplete: (Boolean) -> Unit) {
        val id = pending.id ?: return
        db.collection("pending_providers").document(id).update("status", "rejected")
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun addChatMessage(message: String, isUser: Boolean) {
        val current = _chatHistory.value.toMutableList()
        current.add(Pair(message, isUser))
        _chatHistory.value = current
    }

    fun clearChatHistory() {
        _chatHistory.value = emptyList()
    }

    fun askAssistant(question: String) {
        addChatMessage(question, true)
        _isAssistantLoading.value = true
        viewModelScope.launch {
            try {
                val answer = callGeminiApiDirect(question)
                addChatMessage(answer, false)
            } catch (e: Exception) {
                Log.e("Gemini", "Error direct call: ${e.message}")
                addChatMessage(getOfflineAnswer(question), false)
            } finally {
                _isAssistantLoading.value = false
            }
        }
    }

    private suspend fun callGeminiApiDirect(question: String): String = withContext(Dispatchers.IO) {
        // Applet default API key
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=AIzaSyBoFpZzhWBpwhYwnlfcPehoUp5HfU4DTGc"
        
        // Structured prompt system instructing Gemini about Yemen Dalili services so it acts as an intelligent assistant
        val sysPrompt = "You are the smart assistant for Yemen Dalili (دليلي اليمن). Talk in Arabic. Assist users in finding handymen, medical clinics, taxis etc. " +
                "Here are current categories: ${categories.value.map { it.nameAr }.joinToString(", ")}, " +
                "and available providers: ${serviceProviders.value.map { "${it.name} (${it.phone})" }.joinToString(", ")}. " +
                "Help the user immediately and give exact names and phones when asked!"
        
        val combinedInput = "$sysPrompt\n\nUser Question: $question"
        val escapedQuestion = combinedInput.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
        val jsonRequest = """
            {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": "$escapedQuestion"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val requestBody = jsonRequest.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val root = JSONObject(body)
                val candidates = root.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.getJSONObject("content")
                val parts = contentObj.getJSONArray("parts")
                parts.getJSONObject(0).getString("text")
            } else {
                throw Exception("API Error: code = ${response.code}")
            }
        }
    }

    fun getOfflineAnswer(question: String): String {
        val q = question.lowercase()
        val sb = java.lang.StringBuilder()
        sb.append("مرحباً بك! أنا مساعد دليلي الذكي وبسبب وضع عدم الاتصال، رصدت متطلباتك وسأقترح عليك الأنسب فوراً:\n\n")

        val matchingCats = categories.value.filter {
            it.nameAr.contains(q, ignoreCase = true)
        }

        val matchingProv = serviceProviders.value.filter {
            it.name.contains(q, ignoreCase = true) ||
            it.phone.contains(q) ||
            (it.priceCategory ?: "").contains(q, ignoreCase = true)
        }

        if (matchingCats.isNotEmpty()) {
            sb.append("🗂️ الأقسام المطابقة لطلبك:\n")
            matchingCats.forEach { sb.append("📁 ${it.nameAr}\n") }
            sb.append("\n")
        }

        if (matchingProv.isNotEmpty()) {
            sb.append("✨ مقدمي الخدمات المتطابقين:\n")
            matchingProv.forEach { sb.append("👤 ${it.name} - 📞 ${it.phone}\n") }
        } else {
            sb.append("المجموعات والأقسام المتاحة حالياً:\n")
            categories.value.take(4).forEach { sb.append("📁 ${it.nameAr}\n") }
            sb.append("\n💡 مرشحون مقترحون للتواصل المباشر:\n")
            serviceProviders.value.filter { it.isRecommended || it.isPinned }.take(4).forEach {
                sb.append("✨ ${it.name} - 📞 ${it.phone}\n")
            }
        }
        return sb.toString()
    }

    private fun seedInitialDatabase() {
        viewModelScope.launch {
            try {
                getDefaultCategories().forEach {
                    db.collection("categories").document(it.id.toString()).set(it)
                }
                getDefaultSubCategories().forEach {
                    db.collection("sub_categories").document(it.id.toString()).set(it)
                }
                getDefaultProviders().forEach {
                    db.collection("service_providers").document(it.id.toString()).set(it)
                }
                getDefaultReviews().forEach {
                    db.collection("reviews").document(it.id.toString()).set(it)
                }

                val initialConfig = hashMapOf<String, Any>(
                    "custom_app_name" to "دليلي - Dalili",
                    "welcome_text" to "دليلي - دليلك الشامل لجميع الخدمات والأجهزة الطبية والصيانة في اليمن!",
                    "welcome_image" to "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d",
                    "support_phone" to "777644670",
                    "support_email" to "support@dalili.ye",
                    "support_whatsapp" to "777644670",
                    "footer_text" to "MAW 777644670",
                    "show_footer" to true,
                    "about_app_subtitle" to "دليلي هو منصة الكترونية شاملة ومجانية تهدف لتسهيل الوصول لمزودي الخدمات الهندسية، الطبية والاتصالات في جميع مناطق الجمهورية.",
                    "app_updates_url" to "https://dalili.ye/updates",
                    "app_share_text" to "حمل الآن تطبيق دليلي للأجهزة والخدمات، دليلك في جيبك!",
                    "ai_icon" to "🤖",
                    "show_ai_icon" to true,
                    "assistant_welcome_text" to "مرحباً بك! أنا مساعدك الذكي في تطبيق دليلي. كيف يمكنني مساعدتك في العثور على مقدمي الخدمات اليوم؟",
                    "theme_primary_color" to "#3F51B5",
                    "available_colors_csv" to "#3F51B5,#2196F3,#00E676,#FF9800,#E91E63,#9C27B0"
                )
                db.collection("app_config").document("global").set(initialConfig)

                // Seed structural admin accounts
                val seedAdmin = Admin("admin", "admin", hashPasswordHelper("maher736462"), "super_admin", Date().toString())
                db.collection("admins").document("admin").set(seedAdmin)
            } catch (ex: Exception) {
                Log.e("Seeding", "Seeding failed: ${ex.message}")
            }
        }
    }

    fun getDefaultCategories() = listOf(
        Category(1001, "خدمات الاتصالات والنت", "📱", 1, Date().toString()),
        Category(1002, "الهندسة والصيانة المنزلية", "🛠️", 2, Date().toString()),
        Category(1003, "الطب والتمريض والعيادات", "🩺", 3, Date().toString()),
        Category(1004, "سيارات وسائقين وأجرة", "🚕", 4, Date().toString()),
        Category(1005, "خدمات التعليم والتدريس", "📚", 5, Date().toString()),
        Category(1006, "خدمات الطعام وتوصيل الطلبات", "🍕", 6, Date().toString())
    )

    fun getDefaultSubCategories() = listOf(
        SubCategory(5001, 1003, "عيادات العظام", "Bone", 1, Date().toString()),
        SubCategory(5002, 1003, "عيادات العيون", "Eye", 2, Date().toString()),
        SubCategory(5003, 1003, "الجراحة العامة", "Surgery", 3, Date().toString()),
        SubCategory(5004, 1003, "تمريض منزلي", "Nurse", 4, Date().toString()),
        SubCategory(5005, 1002, "كهرباء منزلي", "Electricity", 1, Date().toString()),
        SubCategory(5006, 1002, "أعمال السباكة", "Plumber", 2, Date().toString()),
        SubCategory(5007, 1002, "صيانة مكيفات", "AC", 3, Date().toString()),
        SubCategory(5008, 1001, "تمديد شبكات", "Networks", 1, Date().toString()),
        SubCategory(5009, 1001, "برمجة وبطاقات", "Programming", 2, Date().toString())
    )

    fun getDefaultProviders() = listOf(
        ServiceProvider(2001, "مؤسسة الاتصالات والشبكات والإنترنت المتكاملة", "777644670", 1001, null, 5.0, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c", null, true, true, true, 15.3694, 44.191, "low", "near", Date().toString()),
        ServiceProvider(2002, "المهندس أحمد لصيانة التكييف والأجهزة المنزلية", "711223344", 1002, 5007, 4.8, "https://images.unsplash.com/photo-1581092160607-ee22621dd758", null, true, false, true, 15.35, 44.2, "medium", "medium", Date().toString()),
        ServiceProvider(2003, "أخصائي الطقس والتمريض المنزلي السريع", "770011223", 1003, 5004, 5.0, "https://images.unsplash.com/photo-1559839734-2b71ea197ec2", null, true, false, false, 15.36, 44.18, "high", "far", Date().toString()),
        ServiceProvider(2004, "تاكسي المشوار السريع للتنقل والرحلات", "777644670", 1004, null, 4.9, "https://images.unsplash.com/photo-1549417229-aa67d3263c09", null, true, false, false, 15.37, 44.21, "medium", "near", Date().toString()),
        ServiceProvider(2005, "أستاذ الرياضيات والفيزياء الخصوصي", "733445566", 1005, null, 4.7, "https://images.unsplash.com/photo-1434030216411-0b793f4b4173", null, true, false, false, 15.334, 44.201, "low", "medium", Date().toString()),
        ServiceProvider(2006, "مطعم الطاهي اليمني للوجبات السريعة والتوصيل", "775566778", 1006, null, 4.6, "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38", null, true, false, false, 15.366, 44.175, "medium", "near", Date().toString())
    )

    fun getDefaultReviews() = listOf(
        Review(3001, 2001, "أبو ماجد", "خدمة ممتازة وسريعة، وتغطية شبكة جيدة جداً في كل المناطق.", 5.0, Date().toString()),
        Review(3002, 2001, "فيصل الحربي", "الدعم الفني متعاون للغاية وسرعة في استجابة المشكلات.", 4.0, Date().toString()),
        Review(3003, 2003, "د. علي الخالدي", "أبطال الإسعاف، استجابة سريعة جداً في وقت الطوارئ شكراً لكم.", 5.0, Date().toString()),
        Review(3004, 2004, "سارة أحمد", "سائق محترم والسيارة نظيفة ووصلت بالوقت المحدد.", 5.0, Date().toString())
    )
}
