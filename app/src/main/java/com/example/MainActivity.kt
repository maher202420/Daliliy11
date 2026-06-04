package com.example

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.DaliliTheme
import com.example.ui.DaliliViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: DaliliViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()
            
            DaliliTheme(
                themeChoice = settings.themeChoice,
                customPrimaryStr = settings.primaryColor,
                customSecondaryStr = settings.secondaryColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigationHost(viewModel = viewModel)
                }
            }
        }
    }
}

// Haversine formulas to measure radius search coordinates physically
fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Earth radius in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationHost(viewModel: DaliliViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Config files
    val settings by viewModel.settings.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val subCategories by viewModel.subCategories.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val pendingProviders by viewModel.pendingProviders.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val complaints by viewModel.complaints.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val whitelist by viewModel.whitelistedDevices.collectAsState()
    val admins by viewModel.admins.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()
    val serviceOrders by viewModel.serviceOrders.collectAsState()

    // Navigation Screens
    var currentScreen by remember { mutableStateOf("home") }
    var selectedProviderForDetails by remember { mutableStateOf<ServiceProvider?>(null) }
    
    // Login system persistence SharedPreferences
    val sharedPref = remember { context.getSharedPreferences("dalili_prefs", Context.MODE_PRIVATE) }
    var isRememberLoginChecked by remember { mutableStateOf(sharedPref.getBoolean("remember_login", false)) }
    var isSavePasswordChecked by remember { mutableStateOf(sharedPref.getBoolean("save_pw", false)) }
    
    var savedUsername by remember { mutableStateOf(sharedPref.getString("saved_user", "") ?: "") }
    var savedPassword by remember { mutableStateOf(sharedPref.getString("saved_pass", "") ?: "") }
    
    var loggedInAdminUser by remember { mutableStateOf<AdminUser?>(
        if (isRememberLoginChecked) AdminUser("persist_adm", "الأدمن الدائم", "", "owner") else null
    ) }

    // Double tab exit state logic
    var backPressedOnce by remember { mutableStateOf(false) }
    BackHandler {
        if (currentScreen != "home") {
            currentScreen = "home"
            backPressedOnce = false
        } else {
            if (backPressedOnce) {
                (context as? Activity)?.finish()
            } else {
                backPressedOnce = true
                Toast.makeText(context, "اضغط مرة أخرى للخروج من التطبيق 🚪", Toast.LENGTH_SHORT).show()
                scope.launch {
                    delay(2000)
                    backPressedOnce = false
                }
            }
        }
    }

    // Floating Interactive widgets states
    var isAssistantOpen by remember { mutableStateOf(false) }
    var isInstantChatOpen by remember { mutableStateOf(false) }
    
    // Assistant dynamic controls
    var assistantXOffset by remember { mutableFloatStateOf(0f) }
    var assistantYOffset by remember { mutableFloatStateOf(0f) }

    // Main App Navigation Root Wrapper
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != "admin_panel") {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = settings.appNameAr,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    actions = {
                        IconButton(onClick = {
                            if (loggedInAdminUser != null) {
                                currentScreen = "admin_panel"
                            } else {
                                currentScreen = "admin_gate"
                            }
                        }) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = "بوابة المشرف")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentScreen != "admin_panel") {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "registration",
                        onClick = { currentScreen = "registration" },
                        icon = { Icon(Icons.Default.AppRegistration, contentDescription = "تسجيل مهني") },
                        label = { Text("تسجيل مهني") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "user_dashboard",
                        onClick = { currentScreen = "user_dashboard" },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "طلباتی") },
                        label = { Text("طلباتي") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "about",
                        onClick = { currentScreen = "about" },
                        icon = { Icon(Icons.Default.Info, contentDescription = "عن التطبيق") },
                        label = { Text("عن التطبيق") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Check Maintenance Mode
            if (settings.isMaintenanceMode && loggedInAdminUser == null) {
                MaintenanceModeView(settings.maintenanceMessage)
            } else {
                when (currentScreen) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        banners = banners,
                        categories = categories,
                        subCategories = subCategories,
                        providers = providers,
                        settings = settings,
                        onProviderSelected = {
                            selectedProviderForDetails = it
                            currentScreen = "provider_details"
                        }
                    )
                    "provider_details" -> selectedProviderForDetails?.let { prov ->
                        ProviderDetailsScreen(
                            provider = prov,
                            viewModel = viewModel,
                            onBackToHome = { currentScreen = "home" }
                        )
                    }
                    "about" -> AboutScreen(
                        settings = settings
                    )
                    "registration" -> RegistrationScreen(
                        categories = categories,
                        subCategories = subCategories,
                        onSubmitRequest = { requestForm ->
                            viewModel.submitRegistrationForm(requestForm) { success ->
                                if (success) {
                                    Toast.makeText(context, "تم إرسال طلبك للآدمن والمراجعة وسيتم الرد فوراً!", Toast.LENGTH_LONG).show()
                                    currentScreen = "home"
                                } else {
                                    Toast.makeText(context, "حدث خطأ ما يرجى المحاولة لاحقاً", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                    "user_dashboard" -> UserOrdersDashboardScreen(
                        serviceOrders = serviceOrders
                    )
                    "admin_gate" -> AdminGateScreen(
                        adminsList = admins,
                        whitelistedDevices = whitelist,
                        savedUser = savedUsername,
                        savedPass = savedPassword,
                        isRememberChecked = isRememberLoginChecked,
                        isSavePwChecked = isSavePasswordChecked,
                        onLoginSuccess = { adminObj, rem, savePw ->
                            loggedInAdminUser = adminObj
                            isRememberLoginChecked = rem
                            isSavePasswordChecked = savePw
                            sharedPref.edit().apply {
                                putBoolean("remember_login", rem)
                                putBoolean("save_pw", savePw)
                                if (savePw) {
                                    putString("saved_user", adminObj.username)
                                    putString("saved_pass", adminObj.password)
                                } else {
                                    remove("saved_user")
                                    remove("saved_pass")
                                }
                                apply()
                            }
                            currentScreen = "admin_panel"
                            Toast.makeText(context, "مرحباً بك مجدداً ${adminObj.username} 🛡️", Toast.LENGTH_SHORT).show()
                        },
                        onBack = { currentScreen = "home" },
                        onUnauthorizedAttempt = { devInfo ->
                            viewModel.logUnauthorizedAttempt(devInfo)
                        }
                    )
                    "admin_panel" -> loggedInAdminUser?.let { admin ->
                        AdminDashboardParent(
                            viewModel = viewModel,
                            admin = admin,
                            categories = categories,
                            subCategories = subCategories,
                            providers = providers,
                            pendingProviders = pendingProviders,
                            banners = banners,
                            complaints = complaints,
                            whitelist = whitelist,
                            adminsList = admins,
                            settings = settings,
                            logs = activityLogs,
                            onLogout = {
                                loggedInAdminUser = null
                                sharedPref.edit().putBoolean("remember_login", false).apply()
                                currentScreen = "home"
                                Toast.makeText(context, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // floating Widgets Layer (Footer Promo, Assistant AI Box & Floating Chat Widget)
            if (currentScreen != "admin_panel") {
                // FOOTER: MAW Promo Line
                if (settings.promoFooterText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 60.dp)
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = settings.promoFooterText,
                            fontSize = 8.sp, // 50% scale
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI CIRCULAR POPUP ASSISTANT (works online or offline)
                if (settings.assistantEnabled) {
                    val sizeDp = when (settings.assistantSize.lowercase()) {
                        "small" -> 44.dp
                        "large" -> 72.dp
                        else -> 56.dp
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 100.dp)
                            .offset(assistantXOffset.dp, assistantYOffset.dp)
                            .size(sizeDp)
                            .shadow(8.dp, CircleShape)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { isAssistantOpen = !isAssistantOpen },
                        contentAlignment = Alignment.Center
                    ) {
                        if (settings.assistantIconUrl.isNotEmpty()) {
                            AsyncImage(
                                model = settings.assistantIconUrl,
                                contentDescription = "المساعد الذكي",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("🤖", fontSize = 24.sp)
                        }
                    }
                }

                // DEDICATED CHAT PANEL POPUP
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 100.dp)
                        .size(56.dp)
                        .shadow(8.dp, CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                        .clickable { isInstantChatOpen = !isInstantChatOpen },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "الدعم المباشر ومحادثة الزوار",
                        tint = Color.White
                    )
                }
            }

            // Chat dialog overlay popup
            if (isInstantChatOpen) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .fillMaxWidth()
                        .height(440.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "المحادثة الفورية مع مكاتب دليلي 🛡️",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = { isInstantChatOpen = false }) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق")
                            }
                        }
                        Divider()
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            item {
                                MessageBubble("مرحباً بك في دليلي للخدمات والمهن! كيف يمكننا مساعدتك اليوم؟", isSender = false)
                            }
                            items(chats) { chat ->
                                MessageBubble(chat.text, isSender = true)
                            }
                        }
                        var inputMessage by remember { mutableStateOf("") }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = inputMessage,
                                onValueChange = { inputMessage = it },
                                placeholder = { Text("أكتب رسالتك للمشرف...") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(onClick = {
                                if (inputMessage.isNotEmpty()) {
                                    viewModel.sendChatMessage(
                                        ChatMessage(
                                            senderId = "guest_usr",
                                            senderName = "زائر دليلي",
                                            text = inputMessage
                                        )
                                    )
                                    inputMessage = ""
                                }
                            }) {
                                Icon(Icons.Default.Send, contentDescription = "أرسل")
                            }
                        }
                    }
                }
            }

            // AI Smart offline Chat helper popup
            if (isAssistantOpen) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .fillMaxWidth()
                        .height(380.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ذكاء دليلي الاصطناعي (أوفلاين) 🤖", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            IconButton(onClick = { isAssistantOpen = false }) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق")
                            }
                        }
                        Divider()
                        var query by remember { mutableStateOf("") }
                        var responseText by remember { mutableStateOf("مرحباً! أنا دليلي المساعد الذكي. اسألني عن تصنيف أو ابحث عن سباك، نجار، أو مبرمج لمساعدتك فوراً وبدون إنترنت.") }
                        
                        Text(
                            text = responseText,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 12.dp)
                        )
                        Row {
                            OutlinedTextField(
                                value = query,
                                onValueChange = { query = it },
                                placeholder = { Text("مثلاً: كيف أسجل كـ سباك؟") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(6.dp))
                            Button(onClick = {
                                if (query.isNotEmpty()) {
                                    responseText = when {
                                        query.contains("رقم") || query.contains("دعم") -> "رقم الدعم الفني الخاص بدليلي المعتمد هو ${settings.supportPhone}."
                                        query.contains("سجل") || query.contains("تقديم") -> "لتسجيل حسابك كمزود خدمة، اذهب إلى تبويب 'تسجيل مهني' بالأسفل واملأ بياناتك والمهارات وسيتم تفعيل حسابك مباشرة."
                                        query.contains("سباك") || query.contains("كهربائي") -> "لدينا قوائم وتصنيفات عديدة للكهربائيين والسباكين المهرة في صنعاء وعدن ومختلف المحافظات بطلب مباشر!"
                                        else -> "طلبك مقبول! سيقوم المساعد الذكي بالبحث في قاعدة بيانات دليلي للقسم المعرّف وإعطائك النتيجة الأقرب."
                                    }
                                    query = ""
                                }
                            }) {
                                Text("اسأل")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Maintenance View Screen
@Composable
fun MaintenanceModeView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚙️", fontSize = 80.sp)
        Spacer(Modifier.height(16.dp))
        Text("عذراً، التطبيق تحت الصيانة الدورية", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(message, textAlign = TextAlign.Center, color = Color.Gray)
    }
}

// ---------------- USER HOME SCREEN ----------------

@Composable
fun HomeScreen(
    viewModel: DaliliViewModel,
    banners: List<Banner>,
    categories: List<Category>,
    subCategories: List<SubCategory>,
    providers: List<ServiceProvider>,
    settings: AppSettings,
    onProviderSelected: (ServiceProvider) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<Category?>(null) }
    var selectedSubCategoryFilter by remember { mutableStateOf<SubCategory?>(null) }
    
    // Radius coordinates for user search
    var radiusKm by remember { mutableStateOf(50f) }
    var filterByRadius by remember { mutableStateOf(false) }

    // Filter providers
    val filteredProviders = providers.filter { prov ->
        val matchesCat = selectedCategoryFilter == null || prov.categoryId == selectedCategoryFilter?.id
        val matchesSubCat = selectedSubCategoryFilter == null || prov.subCategoryId == selectedSubCategoryFilter?.id
        val matchesSearch = prov.name.contains(searchQuery, true) ||
                prov.address.contains(searchQuery, true) ||
                prov.categoryName.contains(searchQuery, true) ||
                prov.subCategoryName.contains(searchQuery, true)
        val notBlocked = !prov.isBlocked
        
        // Calculate distance logic relative to standard default coords
        val distance = calculateDistanceKm(15.3694, 44.1910, prov.latitude, prov.longitude)
        val matchesRadius = !filterByRadius || distance <= radiusKm
        
        matchesCat && matchesSubCat && matchesSearch && notBlocked && matchesRadius
    }.sortedWith(
        compareByDescending<ServiceProvider> { it.isPremium } // Premium monthly subscribers at the top
            .thenByDescending { it.isPinned }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Dynamic Sliding banners section (duration & size customizable)
        if (banners.isNotEmpty()) {
            Text("💡 إعلانات ومستجدات المقاولين", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
            HorizontalBannerRow(banners = banners)
            Spacer(Modifier.height(16.dp))
        }

        // Search options with detailed Radius slider
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث عن مقدم خدمة، تقني، سباك، نجار...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Radius Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("البحث بالنطاق الجغرافي (Radius Search)📍", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Switch(
                        checked = filterByRadius,
                        onCheckedChange = { filterByRadius = it },
                        modifier = Modifier.scale(0.8f)
                    )
                }
                if (filterByRadius) {
                    Text("المسافة القصوى: ${radiusKm.toInt()} كم", fontSize = 11.sp)
                    Slider(
                        value = radiusKm,
                        onValueChange = { radiusKm = it },
                        valueRange = 1f..150f
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Categories section
        Text("🗂️ التخصصات والمهن الرئيسية", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = {
                        selectedCategoryFilter = null
                        selectedSubCategoryFilter = null
                    },
                    label = { Text("جميع الأقسام 🌐") }
                )
            }
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategoryFilter?.id == cat.id,
                    onClick = {
                        selectedCategoryFilter = cat
                        selectedSubCategoryFilter = null
                    },
                    label = { Text(cat.nameAr) }
                )
            }
        }

        // Subcategories row if category is chosen
        selectedCategoryFilter?.let { cat ->
            val subs = subCategories.filter { it.categoryId == cat.id }
            if (subs.isNotEmpty()) {
                Text("🔖 التفريعات الفرعية لـ ${cat.nameAr}:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubCategoryFilter == null,
                            onClick = { selectedSubCategoryFilter = null },
                            label = { Text("الكل") }
                        )
                    }
                    items(subs) { sub ->
                        FilterChip(
                            selected = selectedSubCategoryFilter?.id == sub.id,
                            onClick = { selectedSubCategoryFilter = sub },
                            label = { Text(sub.nameAr) }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Recommended / Recommended Section Carousel
        val recommendedProviders = filteredProviders.filter { it.isRecommended }
        if (recommendedProviders.isNotEmpty()) {
            Text("⭐ الخدميين الموصى بهم من إدارة دليلي", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(recommendedProviders) { prov ->
                    RecommendedCard(provider = prov, onClick = { onProviderSelected(prov) })
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Main Result Providers List
        Text("🔗 قائمة الفنيين ومزودي الخدمات (${filteredProviders.size})", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
        if (filteredProviders.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("لا يوجد مزود خدمة مطابق لمعايير البحث حالياً في نطاقك.", textAlign = TextAlign.Center)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredProviders.forEach { prov ->
                    ProviderCard(provider = prov, onClick = { onProviderSelected(prov) })
                }
            }
        }
    }
}

@Composable
fun HorizontalBannerRow(banners: List<Banner>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(banners) { ban ->
            val heightDp = when (ban.sizeChoice.lowercase()) {
                "small" -> 70.dp
                "large" -> 140.dp
                else -> 100.dp
            }
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(heightDp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (ban.isSponsored) {
                            Text("اعلان ممول ✨", fontSize = 10.sp, color = Color.White, modifier = Modifier.background(Color.Red).padding(horizontal = 4.dp))
                        }
                        Text(ban.textMessage, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedCard(provider: ServiceProvider, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        border = BorderStroke(1.5.dp, Color(0xFFFFD700)) // Golden glow border
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(70.dp)) {
                if (provider.personalPhoto.isNotEmpty()) {
                    AsyncImage(
                        model = provider.personalPhoto,
                        contentDescription = provider.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 24.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color(0xFFFFD700), CircleShape)
                        .padding(3.dp)
                ) {
                    Text("⭐", fontSize = 8.sp)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(provider.name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 12.sp)
            Text(provider.categoryName, color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
fun ProviderCard(provider: ServiceProvider, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(60.dp)) {
                if (provider.personalPhoto.isNotEmpty()) {
                    AsyncImage(
                        model = provider.personalPhoto,
                        contentDescription = provider.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤")
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (provider.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        Text("✔️", color = Color(0xFF1E88E5), fontSize = 12.sp, fontWeight = FontWeight.Bold) // verified blue tick
                    }
                    if (provider.isPremium) {
                        Spacer(Modifier.width(4.dp))
                        Text("👑 مميز", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("${provider.categoryName} - ${provider.subCategoryName}", color = Color.Gray, fontSize = 11.sp)
                Text("📍 ${provider.region}، ${provider.address}", fontSize = 11.sp)
            }
            Text("⭐ ${provider.rating}", fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
        }
    }
}

// ---------------- PROVIDER DETAILS PAGE ----------------

@Composable
fun ProviderDetailsScreen(
    provider: ServiceProvider,
    viewModel: DaliliViewModel,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    var showReportDialog by remember { mutableStateOf(false) }
    var reporterName by remember { mutableStateOf("") }
    var reporterPhone by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("") }

    var showPremiumRequestDialog by remember { mutableStateOf(false) }
    var premiumPhoneConfig by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Button(onClick = onBackToHome, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Text("🔙 العودة للقائمة")
        }
        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Avatar
                if (provider.personalPhoto.isNotEmpty()) {
                    AsyncImage(
                        model = provider.personalPhoto,
                        contentDescription = provider.name,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 48.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(provider.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    if (provider.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        Text("✔️", color = Color(0xFF1E88E5))
                    }
                }
                Text("${provider.categoryName} | ${provider.subCategoryName}", color = Color.LightGray)

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                Text("التواصل المباشر 📞", fontWeight = FontWeight.Bold)
                Text("رقم الهاتف المهني: ${provider.phone}", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("العنوان والمنطقة: ${provider.region} - ${provider.address}")

                Spacer(Modifier.height(24.dp))

                // Premium subscription trigger
                if (!provider.isPremium) {
                    Button(
                        onClick = { showPremiumRequestDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Text("🥇 طلب شارة 'مميز' وترقية للبحث الأول", color = Color.Black)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFEAA611).copy(alpha = 0.2f))
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text("👑 هذا الحساب مميز ومرخص حتى تاريخ نهاية الشهر الجاري", color = Color(0xFFFFD700))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action controls
                Button(
                    onClick = {
                        viewModel.recordServiceRequest(provider)
                        Toast.makeText(context, "تم إبرام طلب الخدمة وتوثيق التواصل بنجاح!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💡 إبرام وبدء طلب خدمة جديد")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { showReportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚠️ الإبلاغ عن المحتوى أو هذا المقدّم")
                }
            }
        }
    }

    // Report dialog
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("تقديم شكوى / بلاغ معجل") },
            text = {
                Column {
                    OutlinedTextField(
                        value = reporterName,
                        onValueChange = { reporterName = it },
                        placeholder = { Text("الاسم الكريم (اختياري)") }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reporterPhone,
                        onValueChange = { reporterPhone = it },
                        placeholder = { Text("رقم هاتفك") }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        placeholder = { Text("سبب البلاغ أو الشكوى بالتفصيل...") },
                        modifier = Modifier.height(120.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (reportReason.isNotEmpty()) {
                        viewModel.submitReport(provider, reportReason, reporterName, reporterPhone) {
                            Toast.makeText(context, "تم رفع البلاغ للآدمن وسيتم التحقق الفوري والاتخاذ والإجراء اللازم.", Toast.LENGTH_LONG).show()
                            showReportDialog = false
                        }
                    }
                }) {
                    Text("تقديم البلاغ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Premium request dialog
    if (showPremiumRequestDialog) {
        AlertDialog(
            onDismissRequest = { showPremiumRequestDialog = false },
            title = { Text("الاشتراك في باقة دليلي المميزة") },
            text = {
                Column {
                    Text("تمنحك الباقة المميزة شهرياً:\n- شارة مميز تاج ذهبي 👑 بجوار اسمك والملف العام.\n- تظهر نتائجك دائماً في صدارة وتصدر نتائج البحث.\n- تواصل مجاني مع دعم التطبيق الفني.", fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = premiumPhoneConfig,
                        onValueChange = { premiumPhoneConfig = it },
                        placeholder = { Text("رقم هاتفك لتحويل الاشتراك") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (premiumPhoneConfig.isNotEmpty()) {
                        viewModel.requestPremiumSubscription(provider.id, premiumPhoneConfig) {
                            Toast.makeText(context, "تم إرسال طلب تفعيل المميز. يرجى إتمام تحويل الرسوم وسيوافق الأدمن فوراً!", Toast.LENGTH_LONG).show()
                            showPremiumRequestDialog = false
                        }
                    }
                }) {
                    Text("تأكيد وإبرام الدفع")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPremiumRequestDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

// Message bubble styling
@Composable
fun MessageBubble(text: String, isSender: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .shadow(2.dp, RoundedCornerShape(8.dp))
                .background(
                    if (isSender) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                )
                .padding(10.dp)
        ) {
            Text(text, fontSize = 12.sp)
        }
    }
}

// ---------------- ABOUT GAME SCREEN ----------------

@Composable
fun AboutScreen(settings: AppSettings) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏢", fontSize = 80.sp)
        Spacer(Modifier.height(16.dp))
        Text("عن تطبيق ${settings.appNameAr}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "دليلي لتنظيم المهن المقاولاتية والخدمية والتقنية هو الواجهة الأمثل لخدمتكم وإتاحة سبل تواصل ذكية للتحقق.",
            textAlign = TextAlign.Center,
            color = Color.LightGray
        )
        Spacer(Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("قنوات الدعم الفني المعتمدة للجمهور:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("📞 هاتف الدعم: ${settings.supportPhone}")
                Spacer(Modifier.height(6.dp))
                Text("💬 واتساب متاح: ${settings.supportWhatsapp}")
                Spacer(Modifier.height(6.dp))
                Text("📧 البريد الإلكتروني: ${settings.supportEmail}")
            }
        }
    }
}

// ---------------- REGISTRATION FORM SCREEN ----------------

@Composable
fun RegistrationScreen(
    categories: List<Category>,
    subCategories: List<SubCategory>,
    onSubmitRequest: (ServiceProvider) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubCat by remember { mutableStateOf<SubCategory?>(null) }
    var region by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    // Pictures choosing simulator
    var personalImgUrl by remember { mutableStateOf("") }
    var identityImgUrl by remember { mutableStateOf("") }

    var isCatMenuOpen by remember { mutableStateOf(false) }
    var isSubCatMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("📝 استمارة الانضمام لكوادر دليلي", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("يرجى ملء الحقول التالية بدقة لتتم مراجعة طلبك وإصدار الشارة الرسمية لك لحسابك الجاري.", fontSize = 12.sp, color = Color.Gray)
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("الاسم الكامل") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("رقم هاتف التواصل والواتساب") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        // Category chooser button
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { isCatMenuOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedCategory?.nameAr ?: "اختر القسم والتخصص الرئيسي 📂")
            }
            DropdownMenu(expanded = isCatMenuOpen, onDismissRequest = { isCatMenuOpen = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.nameAr) },
                        onClick = {
                            selectedCategory = cat
                            selectedSubCat = null
                            isCatMenuOpen = false
                        }
                    )
                }
            }
        }

        // Subcategory chooser button
        selectedCategory?.let { cat ->
            val subs = subCategories.filter { it.categoryId == cat.id }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { isSubCatMenuOpen = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedSubCat?.nameAr ?: "اختر التخصص الفرعي المناسب 🏷️")
                }
                DropdownMenu(expanded = isSubCatMenuOpen, onDismissRequest = { isSubCatMenuOpen = false }) {
                    subs.forEach { sub ->
                        DropdownMenuItem(
                            text = { Text(sub.nameAr) },
                            onClick = {
                                selectedSubCat = sub
                                isSubCatMenuOpen = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = region,
            onValueChange = { region = it },
            label = { Text("المحافظة / المدينة (مثال: صنعاء، عدن...)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("العنوان بالتفصيل والحي السكني") },
            modifier = Modifier.fillMaxWidth()
        )

        // Photo uploads simulators
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📸 الصورة الشخصية للهوية المهنية (خلفية سادة)", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { personalImgUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100" }) {
                        Text("التقاط صورة مباشرة 🤳")
                    }
                    Button(onClick = { personalImgUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100" }) {
                        Text("اختيار من الاستوديو 🖼️")
                    }
                }
                if (personalImgUrl.isNotEmpty()) {
                    Text("تم تحميل الصورة بنجاح! ✔️", color = Color.Green, fontSize = 12.sp)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🪪 صورة البطاقة الشخصية أو جواز السفر", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { identityImgUrl = "https://images.unsplash.com/photo-1554774853-aae0a22c8aa4?w=100" }) {
                        Text("التقاط صورة للبطاقة 📷")
                    }
                    Button(onClick = { identityImgUrl = "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=100" }) {
                        Text("تحميل من الاستوديو 🗃️")
                    }
                }
                if (identityImgUrl.isNotEmpty()) {
                    Text("تم تحميل البطاقة بنجاح! ✔️", color = Color.Green, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (name.isNotEmpty() && phone.isNotEmpty() && selectedCategory != null) {
                    val freshProvider = ServiceProvider(
                        name = name,
                        phone = phone,
                        categoryId = selectedCategory!!.id,
                        categoryName = selectedCategory!!.nameAr,
                        subCategoryId = selectedSubCat?.id ?: "",
                        subCategoryName = selectedSubCat?.nameAr ?: "",
                        region = region,
                        address = address,
                        personalPhoto = personalImgUrl,
                        identityPhoto = identityImgUrl
                    )
                    onSubmitRequest(freshProvider)
                } else {
                    Toast.makeText(context, "يرجى تعبئة الاسم ورقم الهاتف واختيار التخصص الرئيسي والفرعي أولاً!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("إرسال طلب الترخيص الآن")
        }
    }
}

// ---------------- USER DASHBOARD TAB SCREEN ----------------

@Composable
fun UserOrdersDashboardScreen(serviceOrders: List<ServiceOrder>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("📊 لوحة تحكم المستخدم (تتبع تواصلك)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("استعرض حالة وسجل المهنيين الذين تم التعاون والاتفاق معهم لحسابك الحالي.", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(16.dp))

        if (serviceOrders.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📉", fontSize = 44.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("لم تقم بأي طلبات خدمة حتى الآن. تصفح القائمة الرئيسية وتواصل مع الفنيين للمباشرة!")
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(serviceOrders) { ord ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(ord.providerName, fontWeight = FontWeight.Bold)
                                Text("التخصص: ${ord.categoryName}", fontSize = 12.sp, color = Color.LightGray)
                            }
                            Text(
                                "مكتمل ✔️", 
                                color = Color.Green, 
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.background(Color.Green.copy(0.1f)).padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- ADMIN BACKDOOR LOGIN SCREEN ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminGateScreen(
    adminsList: List<AdminUser>,
    whitelistedDevices: List<WhitelistedDevice>,
    savedUser: String,
    savedPass: String,
    isRememberChecked: Boolean,
    isSavePwChecked: Boolean,
    onLoginSuccess: (AdminUser, Boolean, Boolean) -> Unit,
    onBack: () -> Unit,
    onUnauthorizedAttempt: (String) -> Unit
) {
    val context = LocalContext.current
    var usernameinput by remember { mutableStateOf(savedUser) }
    var passwordinput by remember { mutableStateOf(savedPass) }
    var rememberLogin by remember { mutableStateOf(isRememberChecked) }
    var savePassword by remember { mutableStateOf(isSavePwChecked) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🛡️", fontSize = 72.sp)
        Spacer(Modifier.height(12.dp))
        Text("بوابة تسجيل الدخول المشرف", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("الولوج الآمن والمحمي للوحة التحكم والمشرفين", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = usernameinput,
            onValueChange = { usernameinput = it },
            label = { Text("اسم المستخدم") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = passwordinput,
            onValueChange = { passwordinput = it },
            label = { Text("كلمة المرور") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        // Saving controls checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberLogin, onCheckedChange = { rememberLogin = it })
                Text("حفظ تسجيل الدخول", fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = savePassword, onCheckedChange = { savePassword = it })
                Text("حفظ كلمة المرور", fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                // Device model validation whitelist checks
                val deviceModel = android.os.Build.MODEL ?: "Unknown Device Type"
                val matchedDev = whitelistedDevices.find { it.deviceId == deviceModel }
                
                // Super bypass owner fallback
                val ownerBypass = usernameinput == "WAM2026" && passwordinput == "maher736462"
                
                val adminMatched = adminsList.find { it.username == usernameinput && it.password == passwordinput }
                
                if (ownerBypass || adminMatched != null) {
                    if (whitelistedDevices.isNotEmpty() && matchedDev == null && !ownerBypass) {
                        Toast.makeText(context, "الوصول مرفوض! لم يتم ترخيص هذا الجهاز ($deviceModel) في whitelist.", Toast.LENGTH_LONG).show()
                        onUnauthorizedAttempt("Model: $deviceModel | User: $usernameinput")
                    } else {
                        val adm = adminMatched ?: AdminUser("adm1", "WAM2026", "maher736462", "owner")
                        onLoginSuccess(adm, rememberLogin, savePassword)
                    }
                } else {
                    Toast.makeText(context, "عذراً! اسم المستخدم أو كلمة المرور غير صحيحة", Toast.LENGTH_SHORT).show()
                    onUnauthorizedAttempt("فشل فك البوابة | Model: $deviceModel | User: $usernameinput")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("تسجيل الدخول الآمن")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text("🔏 إلغاء والعودة للرئيسية")
        }
    }
}

// ---------------- ADMIN PANEL CONTROL WORLD ----------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardParent(
    viewModel: DaliliViewModel,
    admin: AdminUser,
    categories: List<Category>,
    subCategories: List<SubCategory>,
    providers: List<ServiceProvider>,
    pendingProviders: List<ServiceProvider>,
    banners: List<Banner>,
    complaints: List<Complaint>,
    whitelist: List<WhitelistedDevice>,
    adminsList: List<AdminUser>,
    settings: AppSettings,
    logs: List<ActivityLog>,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var adminActiveTab by remember { mutableStateOf("analytics") }

    Column(modifier = Modifier.fillMaxSize()) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("لوحة التحكم والمشرفين ⚙️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("المستخدم الحالي: ${admin.username} (${admin.role})", fontSize = 11.sp, color = Color.LightGray)
            }
            Row {
                Button(
                    onClick = { viewModel.readAllLogs() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("كل المقروء ✔️", fontSize = 10.sp)
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("خروج 🚪", fontSize = 10.sp)
                }
            }
        }

        // Horizontal tabs flow wrap
        FlowRow(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                "analytics" to "📊 الإحصائيات وبلاغات المالك",
                "pending" to "📥 طلبات التسجيل (${pendingProviders.size})",
                "categories" to "📁 إدارة الأقسام",
                "manual_add" to "➕ إضافة مزود مهني",
                "providers" to "👥 إدارة الخدميين",
                "banners" to "🖼️ إدارة اللافتات والاعلانات",
                "admins_mgmt" to "🛡️ المشرفين",
                "complaints" to "⚠️ الشكاوى والبلاغات",
                "whitelist" to "🔒 الأجهزة الموثوقة",
                "settings" to "⚙️ إعدادات هوية التطبيق"
            ).forEach { (tabId, label) ->
                FilterChip(
                    selected = adminActiveTab == tabId,
                    onClick = { adminActiveTab = tabId },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        Divider()

        Box(modifier = Modifier.weight(1f).padding(16.dp)) {
            when (adminActiveTab) {
                "analytics" -> AdminAnalyticsView(
                    viewModel = viewModel,
                    providers = providers,
                    pending = pendingProviders,
                    logs = logs,
                    complaints = complaints
                )
                "pending" -> AdminPendingRequestsView(
                    pendingProviders = pendingProviders,
                    viewModel = viewModel
                )
                "categories" -> AdminCategoriesManagementView(
                    categories = categories,
                    subCategories = subCategories,
                    viewModel = viewModel
                )
                "manual_add" -> AdminManualAddView(
                    categories = categories,
                    subCategories = subCategories,
                    viewModel = viewModel
                )
                "providers" -> AdminProvidersManagementView(
                    providers = providers,
                    viewModel = viewModel
                )
                "banners" -> AdminBannersManagementView(
                    banners = banners,
                    viewModel = viewModel
                )
                "admins_mgmt" -> AdminSupervisorsManagementView(
                    adminsList = adminsList,
                    viewModel = viewModel,
                    role = admin.role
                )
                "complaints" -> AdminComplaintsView(
                    complaints = complaints,
                    viewModel = viewModel
                )
                "whitelist" -> AdminWhitelistView(
                    whitelist = whitelist,
                    viewModel = viewModel
                )
                "settings" -> AdminSettingsConfigView(
                    settings = settings,
                    viewModel = viewModel
                )
            }
        }
    }
}

// ---------------- TABS INTERNAL COMPOSE UI VIEWS ----------------

@Composable
fun AdminAnalyticsView(
    viewModel: DaliliViewModel,
    providers: List<ServiceProvider>,
    pending: List<ServiceProvider>,
    logs: List<ActivityLog>,
    complaints: List<Complaint>
) {
    val context = LocalContext.current
    var backupStatusText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Analytics statistics counters
        Text("لوحة تحليل النشاط والعمليات الفورية ⚡", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("المهنيين المتاحين", fontSize = 10.sp)
                    Text("${providers.size}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Green)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الطلبات المعلقة", fontSize = 10.sp)
                    Text("${pending.size}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الشكاوى النشطة", fontSize = 10.sp)
                    Text("${complaints.size}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                }
            }
        }

        // Backup manager card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📁 نظام النسخ الاحتياطي لقاعدة البيانات", fontWeight = FontWeight.Bold)
                Text("يمكنك أخذ نسخة احتياطية من كافة الحقول لمقدمي الخدمات والأقسام وتخزينها محلياً على بطاقة الذاكرة أو ذاكرة الهاتف المحمول، أو استعادتها فوراً.", fontSize = 11.sp, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        viewModel.triggerBackupDatabase { result ->
                            backupStatusText = result
                        }
                    }) {
                        Text("حفظ نسخة JSON 💾", fontSize = 11.sp)
                    }
                    Button(
                        onClick = {
                            viewModel.restoreDatabaseFromBackup { result ->
                                backupStatusText = result
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("استعادة النسخة الآن ⚡", fontSize = 11.sp)
                    }
                }
                if (backupStatusText.isNotEmpty()) {
                    Text(backupStatusText, fontSize = 11.sp, color = Color.Green, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Activity Logs (Instant security & registration notifications queue)
        Text("🔔 إشعارات المالك الفورية ونظام التحذير بالأمان", fontWeight = FontWeight.Bold)
        if (logs.isEmpty()) {
            Text("لا يوجد إشعارات أو نشاط يتطلب اهتمام المشرف حالياً.", fontSize = 12.sp, color = Color.Gray)
        } else {
            logs.take(15).forEach { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (log.category) {
                            "security" -> MaterialTheme.colorScheme.errorContainer
                            "subscription" -> MaterialTheme.colorScheme.primaryContainer
                            "reports" -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(log.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(log.description, fontSize = 11.sp)
                        }
                        if (!log.isRead) {
                            Text("جديد 🆕", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPendingRequestsView(
    pendingProviders: List<ServiceProvider>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var rejectingProvId by remember { mutableStateOf("") }
    var rejectReason by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("📥 طلبات تراخيص الانضمام للمقاولين (${pendingProviders.size})", fontWeight = FontWeight.Bold)
        if (pendingProviders.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("لا يوجد أي طلبات تسجيل معلقة حالياً!")
                }
            }
        } else {
            pendingProviders.forEach { pending ->
                var isExpanded by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(pending.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("رقم الهاتف: ${pending.phone} | التخصص الرئيسي: ${pending.categoryName}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Button(onClick = { isExpanded = !isExpanded }) {
                                Text(if (isExpanded) "إخفاء التفاصيل" else "توسيع الطلب والتحقق")
                            }
                        }

                        if (isExpanded) {
                            Spacer(Modifier.height(10.dp))
                            Divider()
                            Spacer(Modifier.height(10.dp))
                            Text("العنوان وخط العرض: ${pending.region} - ${pending.address} (Coords: Lat ${pending.latitude}, Lng ${pending.longitude})", fontSize = 12.sp)
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Selfie Zoom View
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("الصورة السيلفي الشخصية", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    if (pending.personalPhoto.isNotEmpty()) {
                                        AsyncImage(
                                            model = pending.personalPhoto,
                                            contentDescription = "سيلفي مقدم الخدمة",
                                            modifier = Modifier
                                                .size(140.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(modifier = Modifier.size(100.dp).background(Color.Gray), contentAlignment = Alignment.Center) {
                                            Text("غير مرفقة")
                                        }
                                    }
                                }

                                // Id card Zoom View
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("صورة بطاقة الهوية الرسمية", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    if (pending.identityPhoto.isNotEmpty()) {
                                        AsyncImage(
                                            model = pending.identityPhoto,
                                            contentDescription = "بطاقة هوية مقدم الخدمة",
                                            modifier = Modifier
                                                .size(140.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(modifier = Modifier.size(100.dp).background(Color.Gray), contentAlignment = Alignment.Center) {
                                            Text("غير مرفقة")
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.acceptRegistration(pending) {
                                            Toast.makeText(context, "تم ترخيص وموافقة المهني بنجاح فوراً على جميع الأجهزة!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("قبول الطلب والترخيص ✔️", color = Color.Black)
                                }
                                Button(
                                    onClick = { rejectingProvId = pending.id },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("رفض الطلب ❌")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Reason details
    if (rejectingProvId.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { rejectingProvId = "" },
            title = { Text("سبب الرفض") },
            text = {
                OutlinedTextField(
                    value = rejectReason,
                    onValueChange = { rejectReason = it },
                    placeholder = { Text("مثال: بطاقة الهوية غير واضحة") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.rejectRegistration(rejectingProvId, rejectReason) {
                        Toast.makeText(context, "تم رفض الطلب وحذفه وإعطاء إشعار للمالك.", Toast.LENGTH_SHORT).show()
                        rejectingProvId = ""
                        rejectReason = ""
                    }
                }) {
                    Text("تأكيد الرفض")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingProvId = "" }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun AdminCategoriesManagementView(
    categories: List<Category>,
    subCategories: List<SubCategory>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var categoryNameAr by remember { mutableStateOf("") }
    var categoryNameEn by remember { mutableStateOf("") }
    var categoryImgUrl by remember { mutableStateOf("") }

    var subNameAr by remember { mutableStateOf("") }
    var subNameEn by remember { mutableStateOf("") }
    var targetCatForSub by remember { mutableStateOf<Category?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main categories add block
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("➕ إضافة قسم رئيسي جديد", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = categoryNameAr,
                    onValueChange = { categoryNameAr = it },
                    placeholder = { Text("اسم القسم بالعربية") }
                )
                OutlinedTextField(
                    value = categoryNameEn,
                    onValueChange = { categoryNameEn = it },
                    placeholder = { Text("اسم القسم بالإنجليزية") }
                )
                OutlinedTextField(
                    value = categoryImgUrl,
                    onValueChange = { categoryImgUrl = it },
                    placeholder = { Text("رابط صورة أيقونة القسم") }
                )
                Button(onClick = {
                    if (categoryNameAr.isNotEmpty()) {
                        viewModel.addMainCategory(
                            Category(nameAr = categoryNameAr, nameEn = categoryNameEn, imageUrl = categoryImgUrl)
                        ) {
                            Toast.makeText(context, "تمت إضافة التخصص والمهنة الرئيسية بنجاح!", Toast.LENGTH_SHORT).show()
                            categoryNameAr = ""
                            categoryNameEn = ""
                            categoryImgUrl = ""
                        }
                    }
                }) {
                    Text("إضافة قسم رئيسي")
                }
            }
        }

        // Sub categories manage
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("➕ إضافة تخصص وقسم فرعي مخصص للمهنيين", fontWeight = FontWeight.Bold)
                
                var isMenuOpen by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { isMenuOpen = true }) {
                        Text(targetCatForSub?.nameAr ?: "اختر القسم الرئيسي التابع له 📍")
                    }
                    DropdownMenu(expanded = isMenuOpen, onDismissRequest = { isMenuOpen = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat.nameAr) }, onClick = {
                                targetCatForSub = cat
                                isMenuOpen = false
                            })
                        }
                    }
                }

                OutlinedTextField(
                    value = subNameAr,
                    onValueChange = { subNameAr = it },
                    placeholder = { Text("اسم القسم الفرعي بالعربية") }
                )
                OutlinedTextField(
                    value = subNameEn,
                    onValueChange = { subNameEn = it },
                    placeholder = { Text("الأجنبي") }
                )

                Button(onClick = {
                    if (subNameAr.isNotEmpty() && targetCatForSub != null) {
                        viewModel.addSubCategory(
                            SubCategory(categoryId = targetCatForSub!!.id, nameAr = subNameAr, nameEn = subNameEn)
                        ) {
                            Toast.makeText(context, "تم ربط الجزء وتنزيل القسم الفرعي المخصص!", Toast.LENGTH_SHORT).show()
                            subNameAr = ""
                            subNameEn = ""
                        }
                    }
                }) {
                    Text("إضافة قسم فرعي")
                }
            }
        }
    }
}

@Composable
fun AdminManualAddView(
    categories: List<Category>,
    subCategories: List<SubCategory>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubCat by remember { mutableStateOf<SubCategory?>(null) }
    var region by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var personalPhotoUrl by remember { mutableStateOf("") }

    var isCatMenuOpen by remember { mutableStateOf(false) }
    var isSubCatMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("➕ إضافة مقدم خدمة مباشرة (بدون شروط وموافقات)", fontWeight = FontWeight.Bold)
        Text("تسمح هذه الواجهة للأدمن بتسجيل المهني يدوياً بدون انتظار مراجعات أو تطلب للبطاقة وهوية الترخيص الشخصية.", fontSize = 11.sp, color = Color.Gray)

        OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("اسم مقدم الخدمة") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, placeholder = { Text("رقم الاتصال المباشر والتحويل") })

        // Category
        Box {
            OutlinedButton(onClick = { isCatMenuOpen = true }) {
                Text(selectedCategory?.nameAr ?: "اختر القسم والتخصص الرئيسي مسبقاً 📂")
            }
            DropdownMenu(expanded = isCatMenuOpen, onDismissRequest = { isCatMenuOpen = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat.nameAr) }, onClick = {
                        selectedCategory = cat
                        selectedSubCat = null
                        isCatMenuOpen = false
                    })
                }
            }
        }

        // Subcategory
        selectedCategory?.let { cat ->
            Box {
                OutlinedButton(onClick = { isSubCatMenuOpen = true }) {
                    Text(selectedSubCat?.nameAr ?: "اختر تفريعة المهارة المهنية")
                }
                DropdownMenu(expanded = isSubCatMenuOpen, onDismissRequest = { isSubCatMenuOpen = false }) {
                    subCategories.filter { it.categoryId == cat.id }.forEach { sub ->
                        DropdownMenuItem(text = { Text(sub.nameAr) }, onClick = {
                            selectedSubCat = sub
                            isSubCatMenuOpen = false
                        })
                    }
                }
            }
        }

        OutlinedTextField(value = region, onValueChange = { region = it }, placeholder = { Text("المنطقة / المحافظة") })
        OutlinedTextField(value = address, onValueChange = { address = it }, placeholder = { Text("الحي أو العنوان") })
        OutlinedTextField(value = personalPhotoUrl, onValueChange = { personalPhotoUrl = it }, placeholder = { Text("رابط الصورة الشخصية (اختياري)") })

        Button(onClick = {
            if (name.isNotEmpty() && phone.isNotEmpty() && selectedCategory != null) {
                val manualProv = ServiceProvider(
                    name = name,
                    phone = phone,
                    categoryId = selectedCategory!!.id,
                    categoryName = selectedCategory!!.nameAr,
                    subCategoryId = selectedSubCat?.id ?: "",
                    subCategoryName = selectedSubCat?.nameAr ?: "",
                    region = region,
                    address = address,
                    personalPhoto = personalPhotoUrl,
                    isVerified = true
                )
                viewModel.addProviderManually(manualProv) {
                    Toast.makeText(context, "تم تفعيل حساب المقاول وإضافته لـ لمزودي دليلي العام!", Toast.LENGTH_SHORT).show()
                    name = ""
                    phone = ""
                    region = ""
                    address = ""
                    personalPhotoUrl = ""
                }
            }
        }) {
            Text("إضافة وحفظ مباشرة")
        }
    }
}

@Composable
fun AdminProvidersManagementView(
    providers: List<ServiceProvider>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var showSubscriptionConfirmId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("👥 إدارة وضوابط الفنيين الناشطين (${providers.size})", fontWeight = FontWeight.Bold)
        providers.forEach { prov ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(prov.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${prov.categoryName} - ${prov.subCategoryName}", color = Color.Gray, fontSize = 11.sp)
                        }
                        if (prov.subscriptionStatus == "pending_approval") {
                            Button(
                                onClick = { showSubscriptionConfirmId = prov.id },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                            ) {
                                Text("موافقة الاشتراك المميز 👑", color = Color.Black, fontSize = 10.sp)
                            }
                        }
                    }

                    // Toggles block
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Recommend (gold banner header list)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = prov.isRecommended, onCheckedChange = { viewModel.toggleRecommendProvider(prov.id, it) })
                            Text("موصى به ⭐", fontSize = 10.sp)
                        }

                        // Pinned (at first of lists)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = prov.isPinned, onCheckedChange = { viewModel.togglePinProvider(prov.id, it) })
                            Text("تثبيت بالصدارة 📌", fontSize = 10.sp)
                        }

                        // Verified badge toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = prov.isVerified, onCheckedChange = { viewModel.toggleVerifyProvider(prov.id, it) })
                            Text("موثق ✔️", fontSize = 10.sp)
                        }

                        // Block account
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = prov.isBlocked, onCheckedChange = { viewModel.toggleBlockProvider(prov.id, it) })
                            Text("حظر 🚫", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    if (showSubscriptionConfirmId.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showSubscriptionConfirmId = "" },
            title = { Text("الموافقة على الدفع والاشتراك") },
            text = { Text("هل تم التحقق من استلام الرسوم المالية للشهر وتفعيل تاج شارة التثبيت والتميز للخدمي؟") },
            confirmButton = {
                Button(onClick = {
                    viewModel.approvePremiumSubscription(showSubscriptionConfirmId) {
                        Toast.makeText(context, "تم استكمال تفعيل الشارة الذهبية وظهور مزود الخدمة بالقمة!", Toast.LENGTH_SHORT).show()
                        showSubscriptionConfirmId = ""
                    }
                }) {
                    Text("نعم، تفعيل التميز")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubscriptionConfirmId = "" }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun AdminBannersManagementView(
    banners: List<Banner>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var bannerText by remember { mutableStateOf("") }
    var sizeChoice by remember { mutableStateOf("medium") }
    var bannerDurationMinutes by remember { mutableStateOf(10) }
    var isSponsored by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🎨 لوحة إنشاء وترويج لافتات مستجدات التطبيق Banner", fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = bannerText,
                    onValueChange = { bannerText = it },
                    placeholder = { Text("نص الإعلان أو التوجيه الرئيسي...") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Size
                Text("حجم ومساحة اللافتة بالمشهد:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("small" to "صغير 🧱", "medium" to "متوسط 🖼️", "large" to "عريض وكبير 👑").forEach { (sz, label) ->
                        FilterChip(
                            selected = sizeChoice == sz,
                            onClick = { sizeChoice = sz },
                            label = { Text(label) }
                        )
                    }
                }

                // Duration slider
                Text("مدة العرض بالدقائق: $bannerDurationMinutes دقيقة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = bannerDurationMinutes.toFloat(),
                    onValueChange = { bannerDurationMinutes = it.toInt() },
                    valueRange = 1f..120f
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isSponsored, onCheckedChange = { isSponsored = it })
                    Text("شارة اعلان ممول ترويجي ✨", fontSize = 12.sp)
                }

                Button(onClick = {
                    if (bannerText.isNotEmpty()) {
                        viewModel.addBanner(
                            Banner(
                                textMessage = bannerText,
                                sizeChoice = sizeChoice,
                                durationSeconds = bannerDurationMinutes,
                                isSponsored = isSponsored
                            )
                        ) {
                            Toast.makeText(context, "تم نشر اللافتة الترويجية وتفعيل جدولتها فوراً!", Toast.LENGTH_SHORT).show()
                            bannerText = ""
                        }
                    }
                }) {
                    Text("نشر وتثبيت الإعلان ⚡")
                }
            }
        }

        // Active banners listing
        Text("📌 اللافتات والشرائح المفعلة حالياً ومجهوداتها", fontWeight = FontWeight.Bold)
        banners.forEach { ban ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ban.textMessage, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("الحجم: ${ban.sizeChoice} | نوع ترويجي: ${if (ban.isSponsored) "ممول" else "عام"}", fontSize = 11.sp, color = Color.Gray)
                    }
                    IconButton(onClick = {
                        viewModel.deleteBanner(ban.id) {
                            Toast.makeText(context, "تم إزالة وحذف اللافتة بنجاح.", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف الإعلان", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSupervisorsManagementView(
    adminsList: List<AdminUser>,
    viewModel: DaliliViewModel,
    role: String
) {
    val context = LocalContext.current
    var isNewUserMenuOpen by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var adminRole by remember { mutableStateOf("admin") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (role != "owner") {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text("عذراً، تقتصر صلاحية تعيين وزيادة مشرفين فقط لمالك التطبيق الرئيسي المبرمج (Owner) 🛡️", color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("➕ تعيين مشرف وباب دخول خلفي جديد", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = username, onValueChange = { username = it }, placeholder = { Text("اسم مستخدم المشرف") })
                    OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("رمز الدخول السري") })
                    
                    Text("الصلاحية المعطاة للمشرف:")
                    Row {
                        listOf("admin" to "مشرف عام 💼", "manager" to "مدير مراجعات 🧑‍⚖️").forEach { (rl, label) ->
                            FilterChip(
                                selected = adminRole == rl,
                                onClick = { adminRole = rl },
                                label = { Text(label) }
                            )
                        }
                    }

                    Button(onClick = {
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            viewModel.addAdminUser(AdminUser(username = username, password = password, role = adminRole)) {
                                Toast.makeText(context, "تم ترقية المستخدم للائحة المشرفين الدائمين!", Toast.LENGTH_SHORT).show()
                                username = ""
                                password = ""
                            }
                        }
                    }) {
                        Text("إضافة مشرف لخدمتكم")
                    }
                }
            }
        }

        Text("👥 الكادر والمسؤولين المفوضين بالولوج", fontWeight = FontWeight.Bold)
        adminsList.forEach { adm ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(adm.username, fontWeight = FontWeight.Bold)
                        Text("الدور: ${adm.role}", fontSize = 11.sp, color = Color.Gray)
                    }
                    if (role == "owner") {
                        IconButton(onClick = { viewModel.removeAdminUser(adm.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف مشرف", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminComplaintsView(
    complaints: List<Complaint>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("⚠️ البلاغات والشكاوى المسجلة وتراجعها (${complaints.size})", fontWeight = FontWeight.Bold)
        if (complaints.isEmpty()) {
            Text("قائمة البلاغات نظيفة تماماً ولا يوجد أي نزاع مسجل بالوقت الراهن! 🙌", color = Color.Green)
        } else {
            complaints.forEach { comp ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("بلاغ موجه ضد: ${comp.providerName} (ID: ${comp.providerId})", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("تفاصيل المشكلة والتحوير: ${comp.reasonText}")
                        Divider()
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("المبلغ: ${comp.reporterName} | هاتف: ${comp.reporterPhone}", fontSize = 11.sp, color = Color.Gray)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { 
                                        viewModel.toggleBlockProvider(comp.providerId, true) 
                                        Toast.makeText(context, "تم اتخاذ الإجراء السريع وحظر حساب مقدم الخدمة 🚫", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("حظر وإخفاء الفني 🚫", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { viewModel.deleteComplaint(comp.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Text("أرشفة البلاغ ✔️", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminWhitelistView(
    whitelist: List<WhitelistedDevice>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var deviceModel by remember { mutableStateOf("") }
    var deviceLabel by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🔒 ترخيص أجهزة المشرفين (Device Whitelist)", fontWeight = FontWeight.Bold)
                Text("لترقية الأمان، سيتم حظر أي محاولة دخول خلفية للوحة التحكم لمنع المتسللين سوى الأجهزة المدونة في القائمة البيضاء التالية.", fontSize = 11.sp, color = Color.Gray)
                
                OutlinedTextField(value = deviceModel, onValueChange = { deviceModel = it }, placeholder = { Text("موديل الجهاز كما يظهر بالنظام (مثال: Pixel 6)") })
                OutlinedTextField(value = deviceLabel, onValueChange = { deviceLabel = it }, placeholder = { Text("جهة ترخيص أو اسم المشرف المعني (مثال: مصنع ماهر)") })

                Button(onClick = {
                    if (deviceModel.isNotEmpty()) {
                        viewModel.addWhitelistedDevice(WhitelistedDevice(deviceModel, deviceLabel, "SystemOwner")) {
                            Toast.makeText(context, "تم حفظ وترخيص معلمات الجهاز بالخلفية!", Toast.LENGTH_SHORT).show()
                            deviceModel = ""
                            deviceLabel = ""
                        }
                    }
                }) {
                    Text("ترخيص وحفظ الجهاز")
                }
            }
        }

        Text("📋 قائمة الأجهزة المصرح لها بالولوج الدائم", fontWeight = FontWeight.Bold)
        whitelist.forEach { dev ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(dev.deviceId, fontWeight = FontWeight.Bold)
                        Text("المالك: ${dev.deviceLabel} | مرخص ✔️", fontSize = 11.sp, color = Color.Green)
                    }
                    IconButton(onClick = { viewModel.removeWhitelistedDevice(dev.deviceId) }) {
                        Icon(Icons.Default.Delete, contentDescription = "مسح وحظر", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsConfigView(
    settings: AppSettings,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var appNameAr by remember { mutableStateOf(settings.appNameAr) }
    var footerText by remember { mutableStateOf(settings.promoFooterText) }
    var supportPhone by remember { mutableStateOf(settings.supportPhone) }
    var supportEmail by remember { mutableStateOf(settings.supportEmail) }
    var supportWhatsapp by remember { mutableStateOf(settings.supportWhatsapp) }
    var primaryColor by remember { mutableStateOf(settings.primaryColor) }
    var secondaryColor by remember { mutableStateOf(settings.secondaryColor) }
    
    var themeChoice by remember { mutableStateOf(settings.themeChoice) }
    var isMaintenanceMode by remember { mutableStateOf(settings.isMaintenanceMode) }
    var maintenanceMessage by remember { mutableStateOf(settings.maintenanceMessage) }

    // Assistant settings
    var assistantEnabled by remember { mutableStateOf(settings.assistantEnabled) }
    var assistantSize by remember { mutableStateOf(settings.assistantSize) }
    var assistantIconUrl by remember { mutableStateOf(settings.assistantIconUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🎨 إعدادات مظهر وهوية دليلي العام", fontWeight = FontWeight.Bold)
                
                OutlinedTextField(value = appNameAr, onValueChange = { appNameAr = it }, label = { Text("اسم التطبيق الرئيسي") })
                OutlinedTextField(value = footerText, onValueChange = { footerText = it }, label = { Text("تذييل وترويج الصفحات (Footer Text)") })

                Divider()

                Text("ألوان الطابع البصري الأساسية للجمهور:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                OutlinedTextField(value = primaryColor, onValueChange = { primaryColor = it }, label = { Text("اللون الأساسي (مثال: 1A237E#)") })
                OutlinedTextField(value = secondaryColor, onValueChange = { secondaryColor = it }, label = { Text("اللون الترويجي الفرعي (مثال: FFD700#)") })
                
                Text("نمط المظهر المفضل:")
                Row {
                    listOf("dark" to "ليلي عاتم 🌒", "light" to "نهاري فاقع ☀️", "cosmic" to "كوزميك فضائي 🌌").forEach { (th, label) ->
                        FilterChip(
                            selected = themeChoice == th,
                            onClick = { themeChoice = th },
                            label = { Text(label) }
                        )
                    }
                }

                Divider()

                Text("عن التطبيق والدعم والتواصل (تظهر للمستخدم):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                OutlinedTextField(value = supportPhone, onValueChange = { supportPhone = it }, label = { Text("هاتف الدعم الفني") })
                OutlinedTextField(value = supportWhatsapp, onValueChange = { supportWhatsapp = it }, label = { Text("رقم واتساب المعتمد") })
                OutlinedTextField(value = supportEmail, onValueChange = { supportEmail = it }, label = { Text("إيميل مراسلات المطور") })

                Divider()

                Text("🤖 المساعد الذكي العائم والبوب-أب للزوار", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = assistantEnabled, onCheckedChange = { assistantEnabled = it })
                    Text("إظهار المساعد الذكي بجوار التذييل للجميع", fontSize = 12.sp)
                }
                if (assistantEnabled) {
                    OutlinedTextField(value = assistantIconUrl, onValueChange = { assistantIconUrl = it }, label = { Text("رابط أيقونة مخصصة للمساعد (فارغ يعني الافتراضي)") })
                    Text("حجم أيقونة المساعد:")
                    Row {
                        listOf("small" to "صغير 🧱", "medium" to "متوسط 🖼️", "large" to "عريض وكبير 👑").forEach { (sz, label) ->
                            FilterChip(
                                selected = assistantSize == sz,
                                onClick = { assistantSize = sz },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                Divider()

                // Maintenance Switch
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚠️ تفعيل وضع الصيانة للتطبيق كامل", fontWeight = FontWeight.Bold)
                    Switch(checked = isMaintenanceMode, onCheckedChange = { isMaintenanceMode = it })
                }
                if (isMaintenanceMode) {
                    OutlinedTextField(
                        value = maintenanceMessage,
                        onValueChange = { maintenanceMessage = it },
                        label = { Text("رسالة ترحيبية وتوضيح صيانة للعملاء") }
                    )
                }

                Spacer(Modifier.height(8.dp))

                Button(onClick = {
                    val freshS = AppSettings(
                        appNameAr = appNameAr,
                        promoFooterText = footerText,
                        supportPhone = supportPhone,
                        supportEmail = supportEmail,
                        supportWhatsapp = supportWhatsapp,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        themeChoice = themeChoice,
                        isMaintenanceMode = isMaintenanceMode,
                        maintenanceMessage = maintenanceMessage,
                        assistantEnabled = assistantEnabled,
                        assistantSize = assistantSize,
                        assistantIconUrl = assistantIconUrl
                    )
                    viewModel.updateSettings(freshS) { success ->
                        if (success) {
                            Toast.makeText(context, "تم حفظ الإعدادات وقفل مزامنتها فوراً على جميع الأجهزة!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("حفظ كافة التعديلات والمزامنة الفورية ⚡")
                }
            }
        }
    }
}
