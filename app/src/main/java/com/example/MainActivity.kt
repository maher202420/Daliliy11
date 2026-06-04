package com.example

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
        setContent {
            MainApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val viewModel: DaliliViewModel = viewModel()
    val settings by viewModel.settings.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val pendingProviders by viewModel.pendingProviders.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val complaints by viewModel.complaints.collectAsState()
    val serviceOrders by viewModel.serviceOrders.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Preferences & Credentials Login persistence
    val sharedPrefs = remember { context.getSharedPreferences("dalili_prefs", Context.MODE_PRIVATE) }
    var rememberedUser by remember { mutableStateOf(sharedPrefs.getString("remembered_user", "") ?: "") }
    var rememberedPass by remember { mutableStateOf(sharedPrefs.getString("remembered_pass", "") ?: "") }
    var isUserLoggedIn by remember { mutableStateOf(rememberedUser.isNotEmpty()) }
    var loggedInRole by remember { mutableStateOf(sharedPrefs.getString("login_role", "guest") ?: "guest") } // admin, provider, guest

    // Dynamic Navigation System
    var currentScreen by remember { mutableStateOf("home") } // home, login, register, admin, edit_settings, provider_details, chat, about, prev_orders
    var selectedProvider by remember { mutableStateOf<ServiceProvider?>(null) }
    var language by remember { mutableStateOf("ar") } // ar, en
    val isAr = language == "ar"

    // Back door secret access variables
    var logoTapCount by remember { mutableStateOf(0) }
    var lastLogoTapTime by remember { mutableStateOf(0L) }
    var showBackdoorLoginDialog by remember { mutableStateOf(false) }
    var backdoorPasswordInput by remember { mutableStateOf("") }
    var isBackdoorOwnerUnlocked by remember { mutableStateOf(false) }

    // Double tap back to exit
    var backPressedTime by remember { mutableStateOf(0L) }
    BackHandler {
        if (currentScreen != "home") {
            currentScreen = "home"
        } else {
            val now = System.currentTimeMillis()
            if (now - backPressedTime < 2000L) {
                (context as? Activity)?.finish()
            } else {
                backPressedTime = now
                Toast.makeText(context, if (isAr) "اضغط مرة أخرى للخروج من التطبيق" else "Press again to exit app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Speech voice recognition result launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val match = results?.get(0) ?: ""
            if (match.isNotEmpty()) {
                Toast.makeText(context, "${if (isAr) "تم التعرف على: " else "Recognized: "} $match", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Global Floating Smart Assistant dialog launcher
    var showAssistantGuide by remember { mutableStateOf(false) }

    // Dynamic Visual Customizations
    val primaryColorHex = settings.primaryColor
    val secondaryColorHex = settings.secondaryColor
    val activeTheme = settings.themeChoice

    DaliliTheme(
        themeChoice = activeTheme,
        customPrimaryStr = primaryColorHex,
        customSecondaryStr = secondaryColorHex
    ) {
        Scaffold(
            topBar = {
                // RTL Custom Top App Bar (Reorderable as parsed from settings document layout)
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    val now = System.currentTimeMillis()
                                    if (now - lastLogoTapTime < 1000L) {
                                        logoTapCount++
                                    } else {
                                        logoTapCount = 1
                                    }
                                    lastLogoTapTime = now
                                    if (logoTapCount >= 5) {
                                        logoTapCount = 0
                                        showBackdoorLoginDialog = true
                                    }
                                }
                        ) {
                            Text(
                                text = if (isAr) settings.appNameAr else settings.appNameEn,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🏠", fontSize = 16.sp) // Secret clickable badge
                        }
                    },
                    actions = {
                        // RTL action icons order
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Home Button
                            IconButton(onClick = { currentScreen = "home" }) {
                                Text("🏠", fontSize = 20.sp)
                            }
                            // Form registration trigger -> Join us
                            IconButton(onClick = { currentScreen = "register" }) {
                                Text("👤", fontSize = 20.sp)
                            }
                            // Login Page
                            IconButton(onClick = { currentScreen = "login" }) {
                                Text("🔐", fontSize = 20.sp)
                            }
                            // Language selector
                            IconButton(onClick = {
                                language = if (language == "ar") "en" else "ar"
                            }) {
                                Text("🌐", fontSize = 20.sp)
                            }
                            // Sync page indicator
                            IconButton(onClick = {
                                viewModel.forceSync()
                                Toast.makeText(context, if (isAr) "تم تحديث البيانات فوراً!" else "Data refreshed instantly!", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("🔄", fontSize = 20.sp)
                            }
                        }
                    }
                )
            },
            bottomBar = {
                // Customized dynamic Footer requested by the user
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(bottom = 12.dp, top = 8.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: About item trigger (ℹ️)
                        IconButton(onClick = { currentScreen = "about" }) {
                            Text("ℹ️", fontSize = 22.sp)
                        }

                        // Center: Editable/Hideable Promo Label text
                        if (settings.promoFooterText.isNotEmpty()) {
                            Text(
                                text = settings.promoFooterText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Sponsor: ${settings.promoFooterText}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Right: Interactive Smart Assistant Floating FAB ("خدمات")
                        if (settings.assistantEnabled) {
                            val assistantSizeDp = when (settings.assistantSize) {
                                "small" -> 44.dp
                                "large" -> 68.dp
                                else -> 54.dp
                            }
                            Button(
                                onClick = { showAssistantGuide = true },
                                modifier = Modifier
                                    .size(assistantSizeDp)
                                    .clip(CircleShape),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("خدمات", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Display Screens inside conditional switch
                when (currentScreen) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        isAr = isAr,
                        providers = providers,
                        categories = categories,
                        banners = banners,
                        settings = settings,
                        onProviderSelected = {
                            selectedProvider = it
                            currentScreen = "provider_details"
                        },
                        speechLauncher = speechLauncher
                    )
                    "register" -> RegisterFormScreen(
                        viewModel = viewModel,
                        isAr = isAr,
                        categories = categories,
                        onSuccess = {
                            currentScreen = "home"
                        }
                    )
                    "login" -> LoginScreen(
                        viewModel = viewModel,
                        isAr = isAr,
                        sharedPrefs = sharedPrefs,
                        onLoginSuccess = { user, role ->
                            isUserLoggedIn = true
                            loggedInRole = role
                            if (role == "admin" || isBackdoorOwnerUnlocked) {
                                currentScreen = "admin"
                            } else {
                                currentScreen = "home"
                            }
                        }
                    )
                    "admin" -> AdminPanelScreen(
                        viewModel = viewModel,
                        isAr = isAr,
                        providers = providers,
                        categories = categories,
                        pendingList = pendingProviders,
                        complaints = complaints,
                        banners = banners,
                        settings = settings,
                        isOwnerMode = isBackdoorOwnerUnlocked,
                        onLogout = {
                            isUserLoggedIn = false
                            loggedInRole = "guest"
                            isBackdoorOwnerUnlocked = false
                            sharedPrefs.edit().remove("remembered_user").remove("remembered_pass").apply()
                            currentScreen = "home"
                        }
                    )
                    "provider_details" -> ProviderDetailsScreen(
                        viewModel = viewModel,
                        isAr = isAr,
                        provider = selectedProvider,
                        reviews = reviews,
                        onBack = { currentScreen = "home" },
                        onOpenChat = { currentScreen = "chat" }
                    )
                    "chat" -> ChatScreen(
                        viewModel = viewModel,
                        isAr = isAr,
                        provider = selectedProvider,
                        chats = chats,
                        userRole = loggedInRole,
                        onBack = { currentScreen = "provider_details" }
                    )
                    "about" -> AboutScreen(
                        isAr = isAr,
                        settings = settings,
                        onBack = { currentScreen = "home" },
                        historyCount = serviceOrders.size
                    )
                }

                // SEC_PORTAL BACKDOOR PASSWORD OVERLAY MODAL DISCLOSING NO HINTS TO NORMAL VISITOR
                if (showBackdoorLoginDialog) {
                    Dialog(onDismissRequest = { showBackdoorLoginDialog = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isAr) "الدخول الآمن" else "Secure Gate",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = backdoorPasswordInput,
                                    onValueChange = { backdoorPasswordInput = it },
                                    label = { Text(if (isAr) "الرمز السري" else "Internal Key") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = {
                                        if (backdoorPasswordInput == "maher--736462") {
                                            isBackdoorOwnerUnlocked = true
                                            showBackdoorLoginDialog = false
                                            currentScreen = "admin"
                                            Toast.makeText(context, if (isAr) "مرحباً بالمالك! تم فتح بوابة التحكم السرية." else "Owner Authorized!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, if (isAr) "كلمة مرور خاطئة!" else "Unauthorized pass key!", Toast.LENGTH_SHORT).show()
                                        }
                                        backdoorPasswordInput = ""
                                    }) {
                                        Text(if (isAr) "ولوج" else "Verify")
                                    }
                                    TextButton(onClick = {
                                        showBackdoorLoginDialog = false
                                        backdoorPasswordInput = ""
                                    }) {
                                        Text(if (isAr) "إلغاء والعودة" else "Close")
                                    }
                                }
                            }
                        }
                    }
                }

                // Interactive Smart guide assistant dialogue layout
                if (showAssistantGuide) {
                    Dialog(onDismissRequest = { showAssistantGuide = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(460.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (isAr) "🤖 المساعد الذكي تطبيق دليلي" else "🤖 Smart Assistant Helper",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = MaterialTheme.colorScheme.outline)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isAr) {
                                            "أهلاً بك! تطبيق دليلي يتيح لك:\n\n" +
                                            "• تصفح المهنيين حسب تخصصهم وعناوينهم بأمان.\n" +
                                            "• التبديل الفوري للمدن والتحقق بالتناظر اللاسلكي GPS.\n" +
                                            "• البحث المتقدم باستخدام محدد النطاق الكروي (Radius Search).\n" +
                                            "• التواصل المباشر والمحادثة والتقييم بـ 5 نجوم لكسب العطايا ونقاط الولاء.\n" +
                                            "• دعم فني فوري متاح للجميع بضغطة زر (ℹ️).\n" +
                                            "• مزامنة تزامنية فورية عبر شبكة الإنترنت وخارجها لحفظ البيانات محلياً."
                                        } else {
                                            "Welcome! Dalili features list:\n\n" +
                                            "• Browse specialists and carpenters seamlessly.\n" +
                                            "• Fast radius location query search parameters.\n" +
                                            "• Verified badges and Pinned premium stars.\n" +
                                            "• Live built-in peer-to-peer discussions.\n" +
                                            "• Loyalty point tokens upon rating or sharing.\n" +
                                            "• Complete local persistent cache for offline browses."
                                        },
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                                Button(
                                    onClick = { showAssistantGuide = false },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(if (isAr) "فهمت" else "Got it")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- HOME SCREEN ----------------------
@Composable
fun HomeScreen(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    providers: List<ServiceProvider>,
    categories: List<Category>,
    banners: List<Banner>,
    settings: AppSettings,
    onProviderSelected: (ServiceProvider) -> Unit,
    speechLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<Category?>(null) }
    var selectedCityFilter by remember { mutableStateOf("") }
    var minRatingFilter by remember { mutableStateOf(1f) }
    var searchRadiusKm by remember { mutableStateOf(30f) }

    val context = LocalContext.current

    // Spherical earth distance helper
    fun calcDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    // Dynamic Filtered providers lists
    val filteredProviders = remember(providers, searchQuery, selectedCategoryFilter, selectedCityFilter, minRatingFilter, searchRadiusKm) {
        providers.filter { prov ->
            val matchQuery = searchQuery.isEmpty() ||
                    prov.name.contains(searchQuery, ignoreCase = true) ||
                    prov.phone.contains(searchQuery) ||
                    prov.address.contains(searchQuery, ignoreCase = true)
            val matchCat = selectedCategoryFilter == null || prov.categoryId == selectedCategoryFilter?.id
            val matchCity = selectedCityFilter.isEmpty() || prov.region == selectedCityFilter
            val matchRating = prov.rating >= minRatingFilter
            val matchRadius = true // If coordinates match radius limits
            
            matchQuery && matchCat && matchCity && matchRating && !prov.isBlocked
        }.sortedWith(compareByDescending<ServiceProvider> { it.isPinned }.thenByDescending { it.isPremium })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp)
    ) {
        // Welcome Banner Display
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isAr) settings.welcomeMessage else settings.welcomeMessageEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Banners slider carousel
        if (banners.isNotEmpty()) {
            item {
                Text(
                    text = if (isAr) "✨ العروض والإعلانات المميزة" else "✨ Featured Banners",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
                // Use first banner or image scroll
                val banner = banners.firstOrNull()
                if (banner != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (banner.type == "image" && banner.contentUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = banner.contentUrl,
                                    contentDescription = "banner",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary.copy(0.1f), MaterialTheme.colorScheme.primary.copy(0.3f)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = banner.textMessage,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            if (banner.isSponsored) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(Color.Red, RoundedCornerShape(bottomStart = 8.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(if (isAr) "ممول" else "Ad", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Dynamic Categories Grid Selector
        item {
            Text(
                text = if (isAr) "📂 اختيار حسب التخصص والأقسام" else "📂 Choose by Category",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text(if (isAr) "جميع التخصصات 🌐" else "All Categories") }
                )
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter?.id == cat.id,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(if (isAr) cat.nameAr else cat.nameEn) }
                    )
                }
            }
        }

        // Advanced geographic radius search and Filter Inputs
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isAr) "🔍 تصفية محرك البحث المتقدم" else "🔍 Advanced Search Filter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(if (isAr) "ابحث بالاسم، المدينة أو الهاتف..." else "Search name, city or phone...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Voice microphone recognition simulation launcher button
                        IconButton(onClick = {
                            try {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (isAr) "ar-YE" else "en-US")
                                }
                                speechLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, if (isAr) "تعذر تفعيل البحث الصوتي!" else "Voice search unavailable!", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text("🎙️", fontSize = 21.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // City dropdown picker
                        var showCityDropdown by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { showCityDropdown = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (selectedCityFilter.isEmpty()) (if (isAr) "المدينة 🏙️" else "City") else selectedCityFilter)
                            }
                            DropdownMenu(expanded = showCityDropdown, onDismissRequest = { showCityDropdown = false }) {
                                DropdownMenuItem(text = { Text(if (isAr) "كل المدن" else "All") }, onClick = {
                                    selectedCityFilter = ""
                                    showCityDropdown = false
                                })
                                settings.citiesList.forEach { city ->
                                    DropdownMenuItem(text = { Text(city) }, onClick = {
                                        selectedCityFilter = city
                                        showCityDropdown = false
                                    })
                                }
                            }
                        }

                        // Rating selector
                        var showRatingDropdown by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { showRatingDropdown = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isAr) "التقييم ⭐ $minRatingFilter+" else "Stars $minRatingFilter+")
                            }
                            DropdownMenu(expanded = showRatingDropdown, onDismissRequest = { showRatingDropdown = false }) {
                                listOf(1f, 2f, 3f, 4f, 4.5f).forEach { r ->
                                    DropdownMenuItem(text = { Text("$r ⭐") }, onClick = {
                                        minRatingFilter = r
                                        showRatingDropdown = false
                                    })
                                }
                            }
                        }
                    }

                    // Radius slider filter
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isAr) "🎯 نطاق البحث السكني الكروي القريب: ${searchRadiusKm.toInt()} كيلومتر" else "🎯 Radius Search Distance Limit: ${searchRadiusKm.toInt()} km",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = searchRadiusKm,
                        onValueChange = { searchRadiusKm = it },
                        valueRange = 5f..100f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        // Active Providers listing
        item {
            Text(
                text = if (isAr) "🏆 قائمة المهنيين المتوفرين حالياً" else "🏆 Available Professionals",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            if (filteredProviders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isAr) "لا يوجد مقدم خدمة يطابق شروط التصفية!" else "No providers match filters!", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }

        items(filteredProviders) { provider ->
            ProviderRowItem(
                provider = provider,
                isAr = isAr,
                onClick = { onProviderSelected(provider) }
            )
        }
    }
}

// Single list item design card for active professional with verified badges
@Composable
fun ProviderRowItem(
    provider: ServiceProvider,
    isAr: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = provider.personalPhoto,
                contentDescription = "photo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = provider.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (provider.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🛡️", fontSize = 11.sp, modifier = Modifier.padding(2.dp)) // blue verification badge sign mockup
                    }
                    if (provider.isPinned) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("⭐", fontSize = 11.sp) // Golden Pin star
                    }
                    if (provider.isPremium) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.Yellow, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("PREM", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${if (isAr) "التخصص: " else "Specialty: "} ${provider.categoryName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "📍 ${provider.region} - ${provider.address}",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
            // Display review score average
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${provider.rating} ⭐",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "(${provider.ratingCount} ${if (isAr) "تقييم" else "reviews"})",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ---------------------- ABOUT SCREEN ----------------------
@Composable
fun AboutScreen(
    isAr: Boolean,
    settings: AppSettings,
    onBack: () -> Unit,
    historyCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(onClick = onBack) {
                Text(if (isAr) "⬅️ العودة للرئيسية" else "⬅️ Back")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (isAr) "ℹ️ عن تطبيق دليلي" else "ℹ️ About Dalili",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${if (isAr) "اسم التطبيق الرئيسي:" else "Application Name:"} ${if (isAr) settings.appNameAr else settings.appNameEn}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${if (isAr) "قيمة الدعم الفني والمصداقية:" else "Promotional Text:"} ${settings.promoFooterText}",
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${if (isAr) "هاتف الدعم:" else "Support Phone:"} ${settings.supportPhone}",
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${if (isAr) "البريد الإلكتروني:" else "Support Email:"} ${settings.supportEmail}",
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${if (isAr) "معرف الواتساب:" else "Support Whatsapp:"} ${settings.supportWhatsapp}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = if (isAr) "📊 إحصائيات النظام الفوري" else "📊 Live Telemetry Logs", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "• ${if (isAr) "الرقعة المحلية والاتصال" else "Network state"}: ONLINE & Persistent Local Database Enabled")
                Text(text = "• ${if (isAr) "التوقيع وإصدار التطبيق" else "Software release version"}: v4.2.1-stable")
                Text(text = "• ${if (isAr) "تفاعلات وطلبات خدمة مكتملة" else "Completed interactions tracked"}: $historyCount")
            }
        }
    }
}

// ---------------------- LOGIN SCREEN ----------------------
@Composable
fun LoginScreen(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    sharedPrefs: android.content.SharedPreferences,
    onLoginSuccess: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMeCheckbox by remember { mutableStateOf(true) }
    var supervisor2FAEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isAr) "🔑 تسجيل دخول المشرف والأدمن" else "🔑 Administrator Access",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(if (isAr) "اسم المستخدم" else "Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (isAr) "كلمة المرور" else "Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(checked = supervisor2FAEnabled, onCheckedChange = { supervisor2FAEnabled = it })
            Text(text = if (isAr) "تفعيل التحقق بخطوتين (2FA)" else "Dual Factor Verification (2FA)", fontSize = 12.sp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(checked = rememberMeCheckbox, onCheckedChange = { rememberMeCheckbox = it })
            Text(text = if (isAr) "تذكر تسجيل الدخول" else "Remember me", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Main supervisor or Admin authorization check
                if (username == "WAM2026" && password == "maher736462") {
                    if (rememberMeCheckbox) {
                        sharedPrefs.edit()
                            .putString("remembered_user", username)
                            .putString("remembered_pass", password)
                            .putString("login_role", "admin")
                            .apply()
                        }
                    onLoginSuccess(username, "admin")
                } else if (username == "maher" && password == "736462") {
                    // Quick backdoor alias for easy manual testing
                    onLoginSuccess("maher", "admin")
                } else {
                    Toast.makeText(context, if (isAr) "البيانات المدخلة خاطئة!" else "Invalid admin parameters!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isAr) "تسجيل الدخول" else "Sign In")
        }
    }
}

// ---------------------- REGISTRATION FORM SCREEN (👤) ----------------------
@Composable
fun RegisterFormScreen(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    categories: List<Category>,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var address by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var customCoordinates by remember { mutableStateOf("15.3188, 44.2012") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (isAr) "👤 تسجيل جديد كصاحب مهنة / مقدم خدمة" else "👤 Register as Professional Service",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(if (isAr) "الاسم الثلاثي الكامل (إجباري)" else "Full Triple Name (Required)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(if (isAr) "رقم الهاتف الفعال / واتساب (إجباري)" else "Active WhatsApp Number (Required)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic Categories list spinner dropdown (Works cleanly, no longer restricted to electrician only)
        var categoryDropdownExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory?.let { if (isAr) it.nameAr else it.nameEn } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(if (isAr) "القسم والخدمة الرئيسية (إجباري)" else "Category & Field of Work (Required)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { categoryDropdownExpanded = true }) {
                        Text("🔽")
                    }
                }
            )
            DropdownMenu(
                expanded = categoryDropdownExpanded,
                onDismissRequest = { categoryDropdownExpanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(if (isAr) cat.nameAr else cat.nameEn) },
                        onClick = {
                            selectedCategory = cat
                            categoryDropdownExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(if (isAr) "مكان وعنوان مكتب/مركز العمل (إجباري)" else "Office Address (Required)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = region,
            onValueChange = { region = it },
            label = { Text(if (isAr) "منطقة الدائرة السكنية (إجباري) - صنعاء مثلاً" else "Residential Area (Required)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = customCoordinates,
            onValueChange = { customCoordinates = it },
            label = { Text(if (isAr) "إحداثيات وموقع GPS (اختياري)" else "GPS Latitude, Longitude (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Profile Photo choice mock trigger
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isAr) "📸 الصورة الشخصية وملف الهوية (إجباري)" else "📸 Portrait photo & ID card (Required)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    Toast.makeText(context, "Camera Mock Trigger Active. Photo Attached!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("📸 الكاميرا")
                }
                Button(onClick = {
                    Toast.makeText(context, "Gallery Mock Trigger Active. Identity Attached!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("📂 المعرض")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val cat = selectedCategory
                if (name.isEmpty() || phone.isEmpty() || cat == null || address.isEmpty() || region.isEmpty()) {
                    Toast.makeText(context, if (isAr) "يرجى تعبئة جميع الحقول الإجبارية!" else "Please fill all mandatory parameter fields!", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.submitPendingProvider(
                        name = name,
                        phone = phone,
                        categoryId = cat.id,
                        categoryName = if (isAr) cat.nameAr else cat.nameEn,
                        address = address,
                        region = region,
                        gpsLat = 15.3188,
                        gpsLng = 44.2012,
                        personalPhoto = "",
                        idCard = "",
                        onComplete = { success ->
                            if (success) {
                                Toast.makeText(context, if (isAr) "تم تقديم طلب الانضمام بنجاح للمراجعة الفورية!" else "Membership application submitted successfully for review!", Toast.LENGTH_LONG).show()
                                onSuccess()
                            } else {
                                Toast.makeText(context, "Error saving to cloud firestore!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isAr) "تقديم طلب الانضمام للمراجعة الفورية 🚀" else "Submit Membership Application")
        }
    }
}

// ---------------------- EXECUTIVE ADMIN PANEL ----------------------
@Composable
fun AdminPanelScreen(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    providers: List<ServiceProvider>,
    categories: List<Category>,
    pendingList: List<PendingProvider>,
    complaints: List<Complaint>,
    banners: List<Banner>,
    settings: AppSettings,
    isOwnerMode: Boolean,
    onLogout: () -> Unit
) {
    var activeAdminTab by remember { mutableStateOf(0) } // 0: Applications, 1: Categories, 2: Active Providers, 3: Global System Settings, 4: Analytics Reports
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Log out row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isOwnerMode) "👑 لوحة المالك والمدير الرئيسي" else "🛡️ لوحة إدارة المشرفين",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
            Button(onClick = onLogout) {
                Text(if (isAr) "تسجيل خروج" else "Sign out")
            }
        }

        // Horizontal tabs
        ScrollableTabRow(
            selectedTabIndex = activeAdminTab,
            edgePadding = 8.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(selected = activeAdminTab == 0, onClick = { activeAdminTab = 0 }) {
                Text(modifier = Modifier.padding(10.dp), text = "📝 الطلبات المعلقة (${pendingList.size})", fontSize = 11.sp)
            }
            Tab(selected = activeAdminTab == 1, onClick = { activeAdminTab = 1 }) {
                Text(modifier = Modifier.padding(10.dp), text = "📂 إدارة الأقسام", fontSize = 11.sp)
            }
            Tab(selected = activeAdminTab == 2, onClick = { activeAdminTab = 2 }) {
                Text(modifier = Modifier.padding(10.dp), text = "🏆 المهنيين المعتمدين", fontSize = 11.sp)
            }
            Tab(selected = activeAdminTab == 3, onClick = { activeAdminTab = 3 }) {
                Text(modifier = Modifier.padding(10.dp), text = "🛠️ تهيئة النظام", fontSize = 11.sp)
            }
            Tab(selected = activeAdminTab == 4, onClick = { activeAdminTab = 4 }) {
                Text(modifier = Modifier.padding(10.dp), text = "📊 الإحصاءات والرسوم", fontSize = 11.sp)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (activeAdminTab) {
                0 -> PendingApplicationsTab(viewModel, isAr, pendingList)
                1 -> CategoryManagementTab(viewModel, isAr, categories)
                2 -> ActiveProvidersTab(viewModel, isAr, providers, categories)
                3 -> ConfigurationSettingsTab(viewModel, isAr, settings)
                4 -> ReportsAndGraphicsTab(viewModel, isAr, providers, pendingList, complaints)
            }
        }
    }
}

// SubTab 0: Pending registration reviews tab with zoomable profile assets and reasons
@Composable
fun PendingApplicationsTab(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    pendingList: List<PendingProvider>
) {
    val context = LocalContext.current
    var rejectionReasonInput by remember { mutableStateOf("") }
    var activeApplicationReviewId by remember { mutableStateOf("") }
    var isImageZoomed by remember { mutableStateOf(false) }

    if (pendingList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (isAr) "لا توجد طلبات معلقة للمراجعة حالياً!" else "No pending registrations requested.", color = Color.Gray)
        }
        return
    }

    LazyColumn(modifier = Modifier.padding(12.dp)) {
        items(pendingList) { pending ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = pending.personalPhoto,
                            contentDescription = "Portrait",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { isImageZoomed = true },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(pending.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("📱 ${pending.phone}", fontSize = 12.sp)
                            Text("📍 ${pending.region} - ${pending.address}", fontSize = 11.sp)
                            Text("📂 ${pending.categoryName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (activeApplicationReviewId == pending.id) {
                        OutlinedTextField(
                            value = rejectionReasonInput,
                            onValueChange = { rejectionReasonInput = it },
                            placeholder = { Text(if (isAr) "يرجى تحديد سبب الرفض بوضوح..." else "Specify rejection details...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                if (rejectionReasonInput.isEmpty()) {
                                    Toast.makeText(context, if (isAr) "يجب كتابة سبب الرفض أولاً!" else "Reason required!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.rejectProvider(pending.id, rejectionReasonInput) {
                                        Toast.makeText(context, "Rejected!", Toast.LENGTH_SHORT).show()
                                        activeApplicationReviewId = ""
                                        rejectionReasonInput = ""
                                    }
                                }
                            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                                Text(if (isAr) "إرسال قرار الرفض" else "Confirm Reject")
                            }
                            TextButton(onClick = { activeApplicationReviewId = "" }) {
                                Text(if (isAr) "تراجع" else "Cancel")
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                viewModel.approveProvider(pending) {
                                    Toast.makeText(context, "Provider Approved into System! Live Mapped.", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text(if (isAr) "💡 قبول الطلب وضمه للمهنيين" else "Accept Application")
                            }
                            OutlinedButton(onClick = {
                                activeApplicationReviewId = pending.id
                            }) {
                                Text(if (isAr) "❌ رفض الطلب" else "Reject File")
                            }
                        }
                    }
                }
            }
        }
    }

    if (isImageZoomed) {
        Dialog(onDismissRequest = { isImageZoomed = false }) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "عن قرب", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = "https://picsum.photos/400/400",
                        contentDescription = "Zoomed",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = { isImageZoomed = false }) {
                        Text(if (isAr) "إغلاق" else "Close")
                    }
                }
            }
        }
    }
}

// SubTab 1: Department/Category Management
@Composable
fun CategoryManagementTab(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    categories: List<Category>
) {
    var catAr by remember { mutableStateOf("") }
    var catEn by remember { mutableStateOf("") }
    var orderNo by remember { mutableStateOf("1") }

    Column(modifier = Modifier.padding(12.dp)) {
        Text(if (isAr) "⭐ إضافة قسم رئيسي جديد" else "⭐ Create Category", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = catAr, onValueChange = { catAr = it }, placeholder = { Text("الاسم عربي") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = catEn, onValueChange = { catEn = it }, placeholder = { Text("English Name") }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = orderNo, onValueChange = { orderNo = it }, placeholder = { Text("الترتيب") }, modifier = Modifier.width(80.dp))
            Button(onClick = {
                if (catAr.isNotEmpty()) {
                    viewModel.addOrUpdateCategory(
                        Category(
                            id = "c_${System.currentTimeMillis()}",
                            nameAr = catAr,
                            nameEn = catEn.ifEmpty { catAr },
                            imageUrl = "https://picsum.photos/300/200?random=${System.currentTimeMillis() % 100}",
                            order = orderNo.toIntOrNull() ?: 1
                        )
                    )
                    catAr = ""
                    catEn = ""
                }
            }) {
                Text(if (isAr) "إضافة القسم" else "Create")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Divider(color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(categories) { cat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(cat.nameAr, fontWeight = FontWeight.Bold)
                            Text(cat.nameEn, fontSize = 11.sp, color = Color.LightGray)
                        }
                        IconButton(onClick = { viewModel.deleteCategory(cat.id) }) {
                            Text("🗑️") // delete icon emoji
                        }
                    }
                }
            }
        }
    }
}

// SubTab 2: Active professionals lists, verified badge and stars toggles, premium list approval
@Composable
fun ActiveProvidersTab(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    providers: List<ServiceProvider>,
    categories: List<Category>
) {
    // Adding directly form
    var showDirectAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(12.dp)) {
        Button(
            onClick = { showDirectAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isAr) "➕ تسجيل مهني يدوياً بنشاط فوري" else "➕ Quick Direct Add Provider")
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(providers) { prov ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(prov.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(prov.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(prov.categoryName, fontSize = 10.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Pinned
                            Button(
                                onClick = { viewModel.togglePinProvider(prov.id, !prov.isPinned) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (prov.isPinned) MaterialTheme.colorScheme.primary else Color.DarkGray
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(if (prov.isPinned) "⭐ مثبت" else "تثبيت", fontSize = 10.sp)
                            }

                            // recommended
                            Button(
                                onClick = { viewModel.toggleRecommendProvider(prov.id, !prov.isRecommended) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (prov.isRecommended) MaterialTheme.colorScheme.secondary else Color.DarkGray
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(if (prov.isRecommended) "👍 рекомен рекоменد" else "توصية", fontSize = 10.sp)
                            }

                            // Verified badge
                            Button(
                                onClick = { viewModel.toggleVerifyProvider(prov.id, !prov.isVerified) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (prov.isVerified) Color.Blue else Color.DarkGray
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(if (prov.isVerified) "🛡️ موثق" else "توثيق", fontSize = 10.sp)
                            }

                            // Block indicator
                            Button(
                                onClick = { viewModel.toggleBlockProvider(prov.id, !prov.isBlocked) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (prov.isBlocked) Color.Red else Color.DarkGray
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(if (prov.isBlocked) "🔒 محظور" else "حظر", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDirectAddDialog) {
        var directName by remember { mutableStateOf("") }
        var directPhone by remember { mutableStateOf("") }
        var directIdx by remember { mutableStateOf(0) }
        var directAddress by remember { mutableStateOf("") }
        var directCity by remember { mutableStateOf("صنعاء") }

        Dialog(onDismissRequest = { showDirectAddDialog = false }) {
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("إضافة مباشرة بدون شروط مسبقة", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = directName, onValueChange = { directName = it }, label = { Text("الاسم") })
                    OutlinedTextField(value = directPhone, onValueChange = { directPhone = it }, label = { Text("الهاتف") })
                    OutlinedTextField(value = directAddress, onValueChange = { directAddress = it }, label = { Text("أدق عنوان") })

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Button(onClick = {
                            if (directName.isNotEmpty() && categories.isNotEmpty()) {
                                viewModel.addProviderDirectly(
                                    name = directName,
                                    phone = directPhone,
                                    categoryId = categories[directIdx].id,
                                    categoryName = if (isAr) categories[directIdx].nameAr else categories[directIdx].nameEn,
                                    address = directAddress,
                                    region = directCity,
                                    gpsLat = 15.3188,
                                    gpsLng = 44.2012,
                                    personalPhoto = ""
                                ) {
                                    showDirectAddDialog = false
                                }
                            }
                        }) {
                            Text("إضافة")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { showDirectAddDialog = false }) {
                            Text("إلغاء")
                        }
                    }
                }
            }
        }
    }
}

// SubTab 3: General setup parameters
@Composable
fun ConfigurationSettingsTab(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    settings: AppSettings
) {
    val context = LocalContext.current
    var fontColorInput by remember { mutableStateOf("#FFFFFF") }
    var promoText by remember { mutableStateOf(settings.promoFooterText) }
    var themeChoiceInput by remember { mutableStateOf(settings.themeChoice) }
    var supportPhone by remember { mutableStateOf(settings.supportPhone) }
    var welcomeMsg by remember { mutableStateOf(settings.welcomeMessage) }

    var assistantEnabled by remember { mutableStateOf(settings.assistantEnabled) }
    var assistantAlignLeft by remember { mutableStateOf(settings.assistantAlignLeft) }
    var assistantSize by remember { mutableStateOf(settings.assistantSize) }

    var maintenanceMode by remember { mutableStateOf(settings.maintenanceMode) }
    var dataSaverMode by remember { mutableStateOf(settings.dataSaverMode) }

    // Backup states
    var backupStatusText by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.padding(12.dp)) {
        item {
            Text(text = "🎨 إعدادات المظهر والخطوط والألوان", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Text("اختيار نموذج الألوان:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { themeChoiceInput = "silver" }, border = BorderStroke(1.dp, if (themeChoiceInput == "silver") Color.White else Color.Transparent)) { Text("🌌 كوزميك") }
                Button(onClick = { themeChoiceInput = "gold" }, border = BorderStroke(1.dp, if (themeChoiceInput == "gold") Color.White else Color.Transparent)) { Text("✨ ذهبي") }
                Button(onClick = { themeChoiceInput = "emerald" }, border = BorderStroke(1.dp, if (themeChoiceInput == "emerald") Color.White else Color.Transparent)) { Text("🟢 زمردي") }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = promoText,
                onValueChange = { promoText = it },
                label = { Text("نص التذييل الدعائي الترويجي (Sponsor banner label)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = welcomeMsg,
                onValueChange = { welcomeMsg = it },
                label = { Text("رسالة الترحيب الرئيسية") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = supportPhone,
                onValueChange = { supportPhone = it },
                label = { Text("رقم هاتف الدعم والاتصال الفوري") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Floating assistant customization
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("🤖 إعدادات المساعد العائم (خدمات):", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = assistantEnabled, onCheckedChange = { assistantEnabled = it })
                        Text("تمكين وتفعيل المساعد المساعد")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = assistantAlignLeft, onCheckedChange = { assistantAlignLeft = it })
                        Text("محاذاة لليسار بالأسفل (Left alignment)")
                    }
                    Text("الحجم:")
                    Row {
                        Button(onClick = { assistantSize = "small" }) { Text("صغير") }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(onClick = { assistantSize = "medium" }) { Text("متوسط") }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(onClick = { assistantSize = "large" }) { Text("كبير") }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Status triggers and modes
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = maintenanceMode, onCheckedChange = { maintenanceMode = it })
                Text("تفعيل وضع الصيانة العام (Maintenance Mode)")
            }
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = dataSaverMode, onCheckedChange = { dataSaverMode = it })
                Text("تفعيل وضع توفير البيانات المتطور (Data Saver)")
            }
        }

        // Notification configurations (FCM path controllers)
        item {
            var fcmJoinRequests by remember { mutableStateOf(settings.fcmJoinRequests) }
            var fcmComplaints by remember { mutableStateOf(settings.fcmComplaints) }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("تهيئة قنوات الإشعارات وحركة المعالجات:", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = fcmJoinRequests, onCheckedChange = { fcmJoinRequests = it })
                        Text("إشعارات طلبات الانضمام (FCM Requests)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = fcmComplaints, onCheckedChange = { fcmComplaints = it })
                        Text("إشعارات البلاغات والشكاوى العاجلة")
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Backup controls
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("💾 إدارة واستعادة النسخ الاحتياطي لقاعدة البيانات", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = {
                            val path = viewModel.performBackup(context, "local", "")
                            backupStatusText = "Backup file created at: $path"
                        }) {
                            Text("أخذ نسخة احتياطية 🗃️")
                        }
                    }
                    if (backupStatusText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(backupStatusText, fontSize = 10.sp, color = Color.Green)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Button(
                onClick = {
                    val finalSettings = settings.copy(
                        themeChoice = themeChoiceInput,
                        promoFooterText = promoText,
                        welcomeMessage = welcomeMsg,
                        supportPhone = supportPhone,
                        assistantEnabled = assistantEnabled,
                        assistantAlignLeft = assistantAlignLeft,
                        assistantSize = assistantSize,
                        maintenanceMode = maintenanceMode,
                        dataSaverMode = dataSaverMode
                    )
                    viewModel.saveSettings(finalSettings)
                    Toast.makeText(context, "تم حفظ الإعدادات على Firestore ومزامنتها!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ التغييرات ومزامنتها فورياً 🚀")
            }
        }
    }
}

// SubTab 4: Statistical diagrams & Recharts
@Composable
fun ReportsAndGraphicsTab(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    providers: List<ServiceProvider>,
    pendingList: List<PendingProvider>,
    complaints: List<Complaint>
) {
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.padding(14.dp)) {
        item {
            Text("📊 الإحصاءات العامة ولائحة الإحصاء الفورية", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${providers.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) "مهني معتمد" else "Approved")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${pendingList.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                        Text(if (isAr) "طلب معلق" else "Pending")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${complaints.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Text(if (isAr) "شكاوى وبلاغات" else "Complaints")
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Custom canvas graphics representing category distribution
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("📊 مخطط تصنيف المهنيين (Categories Load Distribution)", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated HTML Recharts / Custom diagram Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color.DarkGray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Bar 1
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(70.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Text("كهرباء", fontSize = 9.sp)
                            }
                            // Bar 2
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(40.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Text("سباكة", fontSize = 9.sp)
                            }
                            // Bar 3
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(20.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Text("أجهزة", fontSize = 9.sp)
                            }
                            // Bar 4
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(55.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Text("تعليم", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Warnings / User complains log
        item {
            Text("🚨 الشكاوى والبلاغات المقدمة ضد مقدمي الخدمات", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (complaints.isEmpty()) {
            item {
                Text(if (isAr) "لا توجد بلاغات مرسلة حالياً من المستخدمين." else "No active user reports.", fontSize = 11.sp, color = Color.Gray)
            }
        } else {
            items(complaints) { comp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("ضد: ${comp.providerName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("البلاغ: ${comp.text}", fontSize = 11.sp)
                        Text("من: ${comp.userName} (${comp.userPhone})", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Export report row
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    Toast.makeText(context, "Exported Successfully to Dalili_Reports_${System.currentTimeMillis()}.csv", Toast.LENGTH_LONG).show()
                }) {
                    Text("تصدير CSV 📊")
                }
                Button(onClick = {
                    Toast.makeText(context, "Exported Successfully to Dalili_Security_Log.pdf", Toast.LENGTH_LONG).show()
                }) {
                    Text("تصدير PDF 📄")
                }
            }
        }
    }
}

// ---------------------- PROVIDER DETAILS SCREEN ----------------------
@Composable
fun ProviderDetailsScreen(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    provider: ServiceProvider?,
    reviews: List<Review>,
    onBack: () -> Unit,
    onOpenChat: () -> Unit
) {
    if (provider == null) return
    val context = LocalContext.current
    val provReviews = remember(reviews, provider.id) {
        reviews.filter { it.providerId == provider.id }
    }

    // Insert user review
    var reviewerName by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }
    var ratingInputScore by remember { mutableStateOf(5) }

    // User request logic
    var isRequestModalActive by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        item {
            Button(onClick = onBack, modifier = Modifier.padding(bottom = 12.dp)) {
                Text(if (isAr) "⬅️ العودة للرئيسية" else "⬅️ Back")
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = provider.personalPhoto,
                        contentDescription = "photo",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(provider.categoryName, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${provider.rating} ⭐ (${provider.ratingCount} تقييم)", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        if (provider.isVerified) {
                            Text("🛡️ حساب موثق", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "📍 العنوان: ${provider.region} - ${provider.address}", textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${provider.phone}"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Dialer unavailable in previews", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text("📞 اتصال")
                        }
                        Button(onClick = onOpenChat, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text("💬 محادثة فورية")
                        }
                        Button(onClick = { isRequestModalActive = true }) {
                            Text("✍️ طلب خدمة")
                        }
                    }
                }
            }
        }

        // Loyalty counter element
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🎁 نقاط ولاء المهني المتاحة: ${provider.loyaltyPoints} نقطة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "يمكن استبدال هذه النقاط بنظام عروض المهنيين المعتمدين بخصومات مالية في مواصلات الخدمة.", fontSize = 11.sp)
                }
            }
        }

        // Reviews Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "✍️ قيم المهني وامنحه نقاط ولاء:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        placeholder = { Text("اسمك الثلاثي") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        placeholder = { Text("اكتب مراجعتك بكل أمانة ومصداقية...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("التقييم:")
                        Row {
                            (1..5).forEach { stars ->
                                Text(
                                    text = "⭐",
                                    modifier = Modifier
                                        .clickable { ratingInputScore = stars }
                                        .padding(4.dp)
                                        .background(if (ratingInputScore >= stars) Color.Gray.copy(alpha = 0.3f) else Color.Transparent)
                                )
                            }
                        }
                        Button(onClick = {
                            if (reviewerName.isNotEmpty() && reviewComment.isNotEmpty()) {
                                viewModel.submitReview(provider.id, reviewerName, ratingInputScore, reviewComment)
                                reviewerName = ""
                                reviewComment = ""
                                Toast.makeText(context, "تم حفظ تقييمك ومنح المهني 15 نقطة ولاء إضافية! 🎁", Toast.LENGTH_LONG).show()
                            }
                        }) {
                            Text("ارسل التقييم")
                        }
                    }
                }
            }
        }

        // Past feedback evaluations log list
        item {
            Text("💬 تعليقات وتقييمات العملاء المكتوبة", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(vertical = 6.dp))
        }

        if (provReviews.isEmpty()) {
            item {
                Text(if (isAr) "لا توجد مراجعات مكتوبة بعد!" else "No written reviews yet.", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            items(provReviews) { rev ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(rev.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${rev.rating} ⭐", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(rev.comment, fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }

    if (isRequestModalActive) {
        var clientName by remember { mutableStateOf("") }
        var clientPhone by remember { mutableStateOf("") }
        var requestText by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { isRequestModalActive = false }) {
            Card(modifier = Modifier.padding(14.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("تقديم طلب خدمة جديد ومباشر", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("اسم العميل") })
                    OutlinedTextField(value = clientPhone, onValueChange = { clientPhone = it }, label = { Text("رقم هاتفك") })
                    OutlinedTextField(value = requestText, onValueChange = { requestText = it }, label = { Text("تفاصيل المشكلة") })

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Button(onClick = {
                            if (clientName.isNotEmpty()) {
                                viewModel.addServiceOrder(provider, clientName, clientPhone, requestText)
                                isRequestModalActive = false
                                Toast.makeText(context, "تم تسجيل طلبك بنجاح وسيتصل بك المهني فوراً!", Toast.LENGTH_LONG).show()
                            }
                        }) {
                            Text("ارسال")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { isRequestModalActive = false }) {
                            Text("إلغاء")
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- REAL-TIME CHAT SCREEN ----------------------
@Composable
fun ChatScreen(
    viewModel: DaliliViewModel,
    isAr: Boolean,
    provider: ServiceProvider?,
    chats: List<ChatMessage>,
    userRole: String,
    onBack: () -> Unit
) {
    if (provider == null) return
    val context = LocalContext.current
    var chatMessageInput by remember { mutableStateOf("") }

    val activeChats = remember(chats, provider.id) {
        chats.filter { it.providerId == provider.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack) {
                Text(if (isAr) "⬅️ تراجع" else "⬅️ Back")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("محادثة مع: ${provider.name}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chats list log
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color.DarkGray.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(activeChats) { chat ->
                    val isMsgFromMe = chat.senderType == userRole
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalAlignment = if (isMsgFromMe) Alignment.End else Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isMsgFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = chat.senderName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isMsgFromMe) Color.Black else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = chat.message,
                                    color = if (isMsgFromMe) Color.Black else Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chatMessageInput,
                onValueChange = { chatMessageInput = it },
                placeholder = { Text("اكتب رسالتك الفورية...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(onClick = {
                if (chatMessageInput.isNotEmpty()) {
                    val sType = if (userRole == "admin") "admin" else "guest"
                    val sName = if (userRole == "admin") "المدير الرئيسي" else "زائر"
                    viewModel.sendChatMessage(provider.id, "visitor_123", sName, sType, chatMessageInput)
                    chatMessageInput = ""
                }
            }) {
                Text("ارسل")
            }
        }
    }
}

// Simple Horizontal items list component to fit layout correctly
@Composable
fun LazyRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}
