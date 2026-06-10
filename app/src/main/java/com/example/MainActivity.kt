package com.example

import android.app.Activity
import android.content.Context
import android.content.ClipboardManager
import android.content.ClipData
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
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

// Color Hex parser helper
fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        if (hex.startsWith("#")) {
            Color(android.graphics.Color.parseColor(hex))
        } else {
            Color(android.graphics.Color.parseColor("#$hex"))
        }
    } catch (e: Exception) {
        fallback
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
    var isArabic by remember { mutableStateOf(true) }
    var loggedInProvider by remember { mutableStateOf<ServiceProvider?>(null) }
    var backdoorClicks by remember { mutableStateOf(0) }
    var backdoorLastClickTime by remember { mutableStateOf(0L) }
    
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
    var showChatDisabledAlert by remember { mutableStateOf(false) }
    
    val chatCustomerId = remember {
        val stored = sharedPref.getString("chat_customer_id", "") ?: ""
        if (stored.isNotEmpty()) {
            stored
        } else {
            val generated = "customer_" + (100000..999999).random()
            sharedPref.edit().putString("chat_customer_id", generated).apply()
            generated
        }
    }
    
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
                            text = if (isArabic) settings.appNameAr else settings.appNameEn,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.clickable {
                                val now = System.currentTimeMillis()
                                if (now - backdoorLastClickTime < 1500) {
                                    backdoorClicks++
                                } else {
                                    backdoorClicks = 1
                                }
                                backdoorLastClickTime = now
                                if (backdoorClicks >= 5) {
                                    backdoorClicks = 0
                                    currentScreen = "secret_backdoor"
                                    Toast.makeText(context, "🔓 تم كشف البوابة الخلفية السرية للدعم الفني الخاص بالمالك!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    actions = {
                        val iconKeys = settings.topBarIconOrder.split(",").map { it.trim().lowercase() }
                        iconKeys.forEach { key ->
                            when (key) {
                                "home" -> {
                                    IconButton(onClick = {
                                        val now = System.currentTimeMillis()
                                        if (now - backdoorLastClickTime < 1500) {
                                            backdoorClicks++
                                        } else {
                                            backdoorClicks = 1
                                        }
                                        backdoorLastClickTime = now
                                        
                                        if (backdoorClicks >= 5) {
                                            backdoorClicks = 0
                                            currentScreen = "secret_backdoor"
                                            Toast.makeText(context, "🔓 تم كشف البوابة الخلفية السرية للدعم الفني الخاص بالمالك!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            currentScreen = "home"
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "الرئيسية",
                                            tint = if (currentScreen == "home") MaterialTheme.colorScheme.primary else Color.Gray
                                        )
                                    }
                                }
                                "login" -> {
                                    IconButton(onClick = {
                                        if (loggedInAdminUser != null) {
                                            currentScreen = "admin_panel"
                                        } else if (loggedInProvider != null) {
                                            currentScreen = "provider_dash"
                                        } else {
                                            currentScreen = "admin_gate"
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "الدخول وبوابة التحكم",
                                            tint = if (currentScreen == "admin_gate" || currentScreen == "admin_panel" || currentScreen == "provider_dash") MaterialTheme.colorScheme.primary else Color.Gray
                                        )
                                    }
                                }
                                "register" -> {
                                    IconButton(onClick = { currentScreen = "registration" }) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "تسجيل مهني الجديد",
                                            tint = if (currentScreen == "registration") MaterialTheme.colorScheme.primary else Color.Gray
                                        )
                                    }
                                }
                                "orders" -> {
                                    IconButton(onClick = { currentScreen = "user_dashboard" }) {
                                        Icon(
                                            imageVector = Icons.Default.Dashboard,
                                            contentDescription = "طلباتي وسجل الخدمات",
                                            tint = if (currentScreen == "user_dashboard") MaterialTheme.colorScheme.primary else Color.Gray
                                        )
                                    }
                                }
                                "about" -> {
                                    IconButton(onClick = { currentScreen = "about" }) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "عن طاقم دليلي",
                                            tint = if (currentScreen == "about") MaterialTheme.colorScheme.primary else Color.Gray
                                        )
                                    }
                                }
                                "lang" -> {
                                    IconButton(onClick = {
                                        isArabic = !isArabic
                                        Toast.makeText(context, if (isArabic) "تم تحويل لغة العرض للعربية" else "English presentation active", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = "اللغة",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                                "refresh" -> {
                                    IconButton(onClick = {
                                        viewModel.refreshAllData { ok ->
                                            if (ok) {
                                                Toast.makeText(context, "تم مزامنة وتحديث كافة البيانات بنجاح!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "تحديث",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {}
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
                            chats = chats,
                            chatCustomerId = chatCustomerId,
                            settings = settings,
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
                        providersList = providers,
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
                        onProviderLoginSuccess = { provObj ->
                            loggedInProvider = provObj
                            currentScreen = "provider_dash"
                            Toast.makeText(context, "أهلاً بك يا ${provObj.name} لحسابك الفني 🛠️", Toast.LENGTH_SHORT).show()
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
                            chats = chats,
                            onLogout = {
                                loggedInAdminUser = null
                                sharedPref.edit().putBoolean("remember_login", false).apply()
                                currentScreen = "home"
                                Toast.makeText(context, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    "provider_dash" -> loggedInProvider?.let { prov ->
                        ProviderDashboardScreen(
                            provider = prov,
                            chats = chats,
                            viewModel = viewModel,
                            onLogout = {
                                loggedInProvider = null
                                currentScreen = "home"
                                Toast.makeText(context, "تم خروج من حساب الفني بنجاح", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    "secret_backdoor" -> SecretBackdoorScreen(
                        settings = settings,
                        viewModel = viewModel,
                        onBack = { currentScreen = "home" }
                    )
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
                if (settings.chatVisibility == "visible") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, bottom = 100.dp)
                            .size(settings.chatIconSize.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                parseHexColor(settings.chatIconColor, Color(0xFF0288D1)),
                                CircleShape
                            )
                            .clickable {
                                if (settings.isChatEnabled) {
                                    isInstantChatOpen = !isInstantChatOpen
                                } else {
                                    showChatDisabledAlert = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "الدعم المباشر ومحادثة الزوار",
                            tint = Color.White,
                            modifier = Modifier.size((settings.chatIconSize * 0.55f).dp)
                        )
                    }
                }

                if (showChatDisabledAlert) {
                    AlertDialog(
                        onDismissRequest = { showChatDisabledAlert = false },
                        title = { Text("⚠️ إشعار من الإدارة") },
                        text = { Text(settings.chatDisabledMessage) },
                        confirmButton = {
                            Button(onClick = { showChatDisabledAlert = false }) {
                                Text("موافق")
                            }
                        }
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
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<Category?>(null) }
    var selectedSubCategoryFilter by remember { mutableStateOf<SubCategory?>(null) }
    
    // Radius coordinates for user search
    var radiusKm by remember { mutableStateOf(50f) }
    var filterByRadius by remember { mutableStateOf(false) }

    // Phonetic normalization for Arabic spelling/pronunciation variations
    val arabicNormalizer = remember {
        { text: String ->
            text.replace("[أإآ]".toRegex(), "ا")
                .replace("ة\\b".toRegex(), "ه")
                .replace("ى\\b".toRegex(), "ي")
                .replace("[َُِّّْ]", "") // remove accents/diacritics
                .replace("گ", "ك")
                .replace("ؤ", "و")
                .replace("ئ", "ي")
                .trim()
                .lowercase()
        }
    }

    // Voice recognition launcher
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: ""
            searchQuery = spoken
            Toast.makeText(context, "🎤 تم التعرف على النطق: $spoken", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Filter providers
    val filteredProviders = providers.filter { prov ->
        val matchesCat = selectedCategoryFilter == null || prov.categoryId == selectedCategoryFilter?.id
        val matchesSubCat = selectedSubCategoryFilter == null || prov.subCategoryId == selectedSubCategoryFilter?.id
        
        val qNorm = arabicNormalizer(searchQuery)
        val matchesSearch = if (qNorm.isEmpty()) true else {
            arabicNormalizer(prov.name).contains(qNorm) ||
            arabicNormalizer(prov.address).contains(qNorm) ||
            arabicNormalizer(prov.region).contains(qNorm) ||
            arabicNormalizer(prov.categoryName).contains(qNorm) ||
            arabicNormalizer(prov.subCategoryName).contains(qNorm) ||
            prov.inspectionCost.contains(searchQuery) ||
            prov.inspectionCost.contains(qNorm)
        }
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
            placeholder = { Text("ابحث صوتياً أو كتابياً في دليلي...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            trailingIcon = {
                IconButton(onClick = {
                    try {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-YE")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "انطق اسم الحرفة أو المهني لتبحث فوراً... 🎙️")
                        }
                        voiceLauncher.launch(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "ميزة الإدخال الصوتي غير مفعلة بجهازك", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(Icons.Default.Mic, contentDescription = "البحث الصوتي", tint = MaterialTheme.colorScheme.primary)
                }
            },
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
    chats: List<ChatMessage>,
    chatCustomerId: String,
    settings: AppSettings,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    var showReportDialog by remember { mutableStateOf(false) }
    var reporterName by remember { mutableStateOf("") }
    var reporterPhone by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("") }

    var showPremiumRequestDialog by remember { mutableStateOf(false) }
    var premiumPhoneConfig by remember { mutableStateOf("") }

    var isDirectChatActive by remember { mutableStateOf(false) }
    var chatMsgText by remember { mutableStateOf("") }

    if (isDirectChatActive) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { isDirectChatActive = false }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "تراجع")
                }
                Spacer(Modifier.width(8.dp))
                Text("💬 محادثة مباشرة: ${provider.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            if (!settings.isChatEnabled) {
                // If chat is disabled by admin
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = settings.chatDisabledMessage,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            } else {
                // Chat conversation is active
                val roomMessages = chats.filter {
                    (it.senderId == chatCustomerId && it.receiverId == provider.id) ||
                    (it.senderId == provider.id && it.receiverId == chatCustomerId)
                }

                if (roomMessages.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("أرسل رسالة لبدء دردشة فورية مع هذا المهني بخصوص الخدمة 🤝", color = Color.Gray, textAlign = TextAlign.Center, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ) {
                        val reversed = roomMessages.reversed()
                        items(reversed.size) { index ->
                            val msg = reversed[index]
                            val isMe = msg.senderId == chatCustomerId
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Surface(
                                    color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(msg.text, fontSize = 13.sp)
                                        Text(
                                            text = if (isMe) "أنت" else provider.name,
                                            fontSize = 9.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = chatMsgText,
                        onValueChange = { chatMsgText = it },
                        placeholder = { Text("اكتب استفسارك هنا للفني...") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if (chatMsgText.isNotEmpty()) {
                                val msg = ChatMessage(
                                    senderId = chatCustomerId,
                                    senderName = "عميل دليلي",
                                    receiverId = provider.id,
                                    receiverName = provider.name,
                                    text = chatMsgText,
                                    timestamp = System.currentTimeMillis()
                                )
                                viewModel.sendChatMessage(msg)
                                chatMsgText = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "إرسال", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    } else {
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

                    // Direct chat initiation button
                    Button(
                        onClick = { isDirectChatActive = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💬 بدء محادثة فورية مباشرة مع المهني")
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
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        
        // App Custom Logo / Icon / Custom Cover
        if (settings.aboutCoverType == "image" && settings.aboutCoverUrl.isNotEmpty()) {
            AsyncImage(
                model = settings.aboutCoverUrl,
                contentDescription = "شعار وغلاف التطبيق المخصص",
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )
        } else if (settings.aboutCoverType == "text" && settings.aboutCoverText.isNotEmpty()) {
            Surface(
                modifier = Modifier.size(110.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                    Text(settings.aboutCoverText, fontSize = if (settings.aboutCoverText.length > 3) 22.sp else 42.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        } else if (settings.logoUrl.isNotEmpty()) {
            AsyncImage(
                model = settings.logoUrl,
                contentDescription = "شعار التطبيق",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🏢", fontSize = 52.sp)
                }
            }
        }
        
        Text(
            text = settings.appNameAr.ifEmpty { "دليلي" },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = settings.appNameEn.ifEmpty { "Dalili" },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        
        Text(
            text = settings.welcomeMessage.ifEmpty { 
                "دليلي لتنظيم وحوسبة الخدمات الطبية والمقاولاتية والخدمية والتقنية هو الواجهة الأمثل لخدمتكم وإتاحة سبل تواصل ذكية للتحقق."
            },
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp),
            lineHeight = 20.sp
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        
        Text(
            text = "قنوات الدعم والتواصل المباشر مع المالك",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.End)
        )
        
        // Direct Action Contact Cards
        // Phone Card
        Card(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${settings.supportPhone}"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "لم نتمكن من فتح طلب الاتصال", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "اتصال مباشر",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("الاتصال الهاتفي الساخن", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(settings.supportPhone.ifEmpty { "غير محدد" }, fontSize = 12.sp, color = Color.Gray)
                }
                Text("اتصل الآن 📞", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
        
        // Whatsapp Card
        Card(
            onClick = {
                try {
                    val wpNo = settings.supportWhatsapp.replace("+", "").replace(" ", "")
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://wa.me/$wpNo"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "فشل فتح تطبيق واتساب", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "مراسلة واتساب",
                    tint = Color(0xFF25D366),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("محادثة واتساب الفنية المباشرة", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(settings.supportWhatsapp.ifEmpty { "غير محدد" }, fontSize = 12.sp, color = Color.Gray)
                }
                Text("إرسال رسالة 💬", fontSize = 11.sp, color = Color(0xFF25D366), fontWeight = FontWeight.SemiBold)
            }
        }
        
        // Email Card
        Card(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO, android.net.Uri.parse("mailto:${settings.supportEmail}"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val clipManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipManager.setPrimaryClip(ClipData.newPlainText("email", settings.supportEmail))
                    Toast.makeText(context, "تم نسخ البريد الإلكتروني لوحة الحافظة", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "البريد الالكتروني",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("مراسلة الدعم التقني عبر البريد", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(settings.supportEmail.ifEmpty { "غير محدد" }, fontSize = 12.sp, color = Color.Gray)
                }
                Text("نسخ / مراسلة 📧", fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.SemiBold)
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        // Share App Action Button
        Button(
            onClick = {
                try {
                    val shareText = "تطبيق ${settings.appNameAr}: دليلك الشامل لجميع المهن المقاولاتية والخدمية والتقنية في اليمن!\nالدعم والاتصال المباشر: ${settings.supportPhone}\nرابط تحميل ومشاركة التطبيق الرسمي: ${settings.appSharingLink}"
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "مشاركة تطبيق دليلي اليمني")
                    context.startActivity(shareIntent)
                } catch (e: java.lang.Exception) {
                    Toast.makeText(context, "تعذر معالجة مشاركة التطبيق", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Share, contentDescription = "مشاركة")
            Spacer(Modifier.width(8.dp))
            Text("مشاركة تطبيق ${settings.appNameAr} مع الأصدقاء 🔗", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Animated promotional footer
        Text(
            text = settings.promoFooterText.ifEmpty { "MAW 777644670" },
            fontSize = 11.sp,
            color = Color.LightGray.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(20.dp))
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
    providersList: List<ServiceProvider>,
    onLoginSuccess: (AdminUser, Boolean, Boolean) -> Unit,
    onProviderLoginSuccess: (ServiceProvider) -> Unit,
    onBack: () -> Unit,
    onUnauthorizedAttempt: (String) -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf("admin") } // admin or provider


    // Admin states
    var usernameinput by remember { mutableStateOf(savedUser) }
    var passwordinput by remember { mutableStateOf(savedPass) }
    var rememberLogin by remember { mutableStateOf(isRememberChecked) }
    var savePassword by remember { mutableStateOf(isSavePwChecked) }

    // Provider states
    var providerPhoneInput by remember { mutableStateOf("") }
    var providerNameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔑", fontSize = 72.sp)
        Spacer(Modifier.height(12.dp))
        Text("بوابة تسجيل الدخول دليلي", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("يرجى اختيار نوع الحساب لإتمام المصادقة والتحقق", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(20.dp))

        // TAB SELECTOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = activeTab == "admin",
                onClick = { activeTab = "admin" },
                label = { Text("🛡️ مدير النظام") }
            )
            Spacer(Modifier.width(16.dp))
            FilterChip(
                selected = activeTab == "provider",
                onClick = { activeTab = "provider" },
                label = { Text("🛠️ مقدم خدمة / فني") }
            )
        }

        if (activeTab == "admin") {
            // ADMIN LOGIN FORM
            OutlinedTextField(
                value = usernameinput,
                onValueChange = { usernameinput = it },
                label = { Text("اسم المستخدم للمسؤول") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = passwordinput,
                onValueChange = { passwordinput = it },
                label = { Text("كلمة المرور") },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
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
                    val deviceModel = android.os.Build.MODEL ?: "Unknown Device Type"
                    val matchedDev = whitelistedDevices.find { it.deviceId == deviceModel }
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
                        onUnauthorizedAttempt("فشل البوابة | Model: $deviceModel | User: $usernameinput")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تسجيل دخول المشرف 🛡️")
            }
        } else {
            // PROVIDER LOGIN FORM
            Text("دخول سريع لمزودي الخدمات ومتابعة المحادثات والرسائل", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = providerPhoneInput,
                onValueChange = { providerPhoneInput = it },
                label = { Text("رقم الهاتف المسجل (المطابق للملف)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = providerNameInput,
                onValueChange = { providerNameInput = it },
                label = { Text("الاسم المهني (اختياري للتحقق السريع)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (providerPhoneInput.isEmpty()) {
                        Toast.makeText(context, "يرجى كتابة رقم الهاتف المسجل لتسجيل الدخول!", Toast.LENGTH_SHORT).show()
                    } else {
                        val trimmedPhone = providerPhoneInput.trim()
                        val matched = providersList.find { it.phone.trim() == trimmedPhone || it.phone.trim().contains(trimmedPhone) }
                        if (matched != null) {
                            onProviderLoginSuccess(matched)
                        } else {
                            Toast.makeText(context, "عذراً! لم نجد أي فني أو مزود خدمة معتمد بالرقم $providerPhoneInput", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("الدخول للوحة التحكم الفنية 🛠️")
            }
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text("🔙 إلغاء والعودة للرئيسية")
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
    chats: List<ChatMessage>,
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
                "settings" to "⚙️ إعدادات هوية التطبيق",
                "chats" to "💬 إدارة المحادثات"
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
                "chats" -> AdminChatsManagementView(
                    chats = chats,
                    providers = providers,
                    viewModel = viewModel,
                    settings = settings
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

    // Dialog editing states
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var editingSubCategory by remember { mutableStateOf<SubCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main categories add block & management list
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📁 إدارة الأقسام والخدمات الرئيسية", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("أضف قسماً رئيسياً جديداً لمقدمي الخدمات لتسهيل العثور عليهم من قبل العملاء.", fontSize = 11.sp, color = Color.Gray)
                
                OutlinedTextField(
                    value = categoryNameAr,
                    onValueChange = { categoryNameAr = it },
                    label = { Text("اسم القسم بالعربية") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoryNameEn,
                    onValueChange = { categoryNameEn = it },
                    label = { Text("اسم القسم بالإنجليزية") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoryImgUrl,
                    onValueChange = { categoryImgUrl = it },
                    label = { Text("رابط صورة أيقونة القسم") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (categoryNameAr.isNotEmpty()) {
                            viewModel.addMainCategory(
                                Category(nameAr = categoryNameAr, nameEn = categoryNameEn, imageUrl = categoryImgUrl)
                            ) { success ->
                                if (success) {
                                    Toast.makeText(context, "تمت إضافة التخصص والمهنة الرئيسية بنجاح!", Toast.LENGTH_SHORT).show()
                                    categoryNameAr = ""
                                    categoryNameEn = ""
                                    categoryImgUrl = ""
                                } else {
                                    Toast.makeText(context, "فشل إضافة القسم الرئسي", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("إضافة قسم رئيسي ➕")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("🗂️ الأقسام الرئيسية الحالية (${categories.size}):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                categories.forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color.Gray.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text("📁 ", fontSize = 16.sp)
                            Column {
                                Text(cat.nameAr, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                if (cat.nameEn.isNotEmpty()) {
                                    Text(cat.nameEn, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                        Row {
                            IconButton(onClick = { editingCategory = cat }) {
                                Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                            IconButton(
                                onClick = {
                                    viewModel.deleteMainCategory(cat.id) { success ->
                                        if (success) {
                                            Toast.makeText(context, "تم حذف القسم الرئيسي بنجاح", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // Sub categories add block & management list
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("➕ إضافة تخصص وقسم فرعي مخصص للمهنيين", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                var isMenuOpen by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { isMenuOpen = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(targetCatForSub?.nameAr ?: "اختر القسم الرئيسي التابع له 📍")
                    }
                    DropdownMenu(expanded = isMenuOpen, onDismissRequest = { isMenuOpen = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.nameAr) },
                                onClick = {
                                    targetCatForSub = cat
                                    isMenuOpen = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = subNameAr,
                    onValueChange = { subNameAr = it },
                    label = { Text("اسم القسم الفرعي بالعربية") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subNameEn,
                    onValueChange = { subNameEn = it },
                    label = { Text("اسم القسم الفرعي بالإنجليزية") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (subNameAr.isNotEmpty() && targetCatForSub != null) {
                            viewModel.addSubCategory(
                                SubCategory(categoryId = targetCatForSub!!.id, nameAr = subNameAr, nameEn = subNameEn)
                            ) { success ->
                                if (success) {
                                    Toast.makeText(context, "تم ربط الجزء وتنزيل القسم الفرعي المخصص!", Toast.LENGTH_SHORT).show()
                                    subNameAr = ""
                                    subNameEn = ""
                                }
                            }
                        } else if (targetCatForSub == null) {
                            Toast.makeText(context, "يرجى اختيار القسم الرئيسي أولاً!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("إضافة قسم فرعي 🔗")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("📍 الخدمات الفرعية الحالية (${subCategories.size}):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                subCategories.forEach { sub ->
                    val parentCat = categories.find { it.id == sub.categoryId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color.Gray.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text("📍 ", fontSize = 16.sp)
                            Column {
                                Text(sub.nameAr, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text("تابع لـ: ${parentCat?.nameAr ?: "قسم مجهول"}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Row {
                            IconButton(onClick = { editingSubCategory = sub }) {
                                Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                            IconButton(
                                onClick = {
                                    viewModel.deleteSubCategory(sub.id) { success ->
                                        if (success) {
                                            Toast.makeText(context, "تم حذف القسم الفرعي بنجاح", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog for category editing
    editingCategory?.let { cat ->
        var editNameAr by remember { mutableStateOf(cat.nameAr) }
        var editNameEn by remember { mutableStateOf(cat.nameEn) }
        var editImgUrl by remember { mutableStateOf(cat.imageUrl) }

        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("📝 تعديل بيانات القسم الرئيسي") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = editNameAr, onValueChange = { editNameAr = it }, label = { Text("الاسم بالعربية") })
                    OutlinedTextField(value = editNameEn, onValueChange = { editNameEn = it }, label = { Text("الاسم بالإنجليزية") })
                    OutlinedTextField(value = editImgUrl, onValueChange = { editImgUrl = it }, label = { Text("رابط الصورة الفيرستورية") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editNameAr.isNotEmpty()) {
                        val updated = cat.copy(nameAr = editNameAr, nameEn = editNameEn, imageUrl = editImgUrl)
                        viewModel.updateMainCategory(updated) { success ->
                            if (success) {
                                Toast.makeText(context, "تم تحديث القسم في Firestore ونشره فورياً!", Toast.LENGTH_SHORT).show()
                                editingCategory = null
                            }
                        }
                    }
                }) {
                    Text("حفظ التعديلات")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Modal dialog for subcategory editing
    editingSubCategory?.let { sub ->
        var editSubNameAr by remember { mutableStateOf(sub.nameAr) }
        var editSubNameEn by remember { mutableStateOf(sub.nameEn) }
        var selectedParentId by remember { mutableStateOf(sub.categoryId) }
        var isEditMenuOpen by remember { mutableStateOf(false) }

        val activeParent = categories.find { it.id == selectedParentId }

        AlertDialog(
            onDismissRequest = { editingSubCategory = null },
            title = { Text("📝 تعديل القسم الفرعي") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { isEditMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("التابع لـ: ${activeParent?.nameAr ?: "اختر..."}")
                        }
                        DropdownMenu(expanded = isEditMenuOpen, onDismissRequest = { isEditMenuOpen = false }) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.nameAr) },
                                    onClick = {
                                        selectedParentId = cat.id
                                        isEditMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(value = editSubNameAr, onValueChange = { editSubNameAr = it }, label = { Text("الاسم بالعربية") })
                    OutlinedTextField(value = editSubNameEn, onValueChange = { editSubNameEn = it }, label = { Text("الاسم بالإنجليزية") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editSubNameAr.isNotEmpty()) {
                        val updatedSub = sub.copy(categoryId = selectedParentId, nameAr = editSubNameAr, nameEn = editSubNameEn)
                        viewModel.updateSubCategory(updatedSub) { success ->
                            if (success) {
                                Toast.makeText(context, "تم التعديل ومزامنة التغييرات الفرعية!", Toast.LENGTH_SHORT).show()
                                editingSubCategory = null
                            }
                        }
                    }
                }) {
                    Text("حفظ التعديلات")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSubCategory = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun AdminManualAddView(
    categories: List<Category>,
    subCategories: List<SubCategory>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    val cities by viewModel.cities.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubCat by remember { mutableStateOf<SubCategory?>(null) }
    var region by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var inspectionCost by remember { mutableStateOf("") }
    var personalPhotoUrl by remember { mutableStateOf("") }
    var grantVipImmediately by remember { mutableStateOf(false) }

    var isCatMenuOpen by remember { mutableStateOf(false) }
    var isSubCatMenuOpen by remember { mutableStateOf(false) }
    var isCityMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("➕ إضافة فني ومزود خدمة يدوياً (القسم الثاني)", fontWeight = FontWeight.Bold)
        Text("إضافة فني جديد إلى الدليل مباشرة دون الحاجة لموافقة مسبقة. الحقول التالية اختيارية لتسهيل الإدراك السريع للأدمن.", fontSize = 11.sp, color = Color.Gray)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("الاسم الكامل (اختياري)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            placeholder = { Text("رقم الهاتف (اختياري)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Inspection Cost
        OutlinedTextField(
            value = inspectionCost,
            onValueChange = { inspectionCost = it },
            placeholder = { Text("سعر المعاينة بالريال (اختياري)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Geo Location / Cities Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { isCityMenuOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (region.isEmpty()) "حدد مدينة / منطقة الفني 📍" else "المنطقة المحددة: $region")
            }
            DropdownMenu(expanded = isCityMenuOpen, onDismissRequest = { isCityMenuOpen = false }) {
                cities.forEach { city ->
                    DropdownMenuItem(text = { Text("${city.nameAr} (${city.nameEn})") }, onClick = {
                        region = city.nameAr
                        isCityMenuOpen = false
                    })
                }
                DropdownMenuItem(text = { Text("أخرى / إدخال يدوي") }, onClick = {
                    region = ""
                    isCityMenuOpen = false
                })
            }
        }

        if (region.isEmpty()) {
            OutlinedTextField(
                value = region,
                onValueChange = { region = it },
                placeholder = { Text("أو اكتب المنطقة / المدينة المخصصة يدوياً") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            placeholder = { Text("الحي أو السكن المفصل (اختياري)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Category Selection
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { isCatMenuOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedCategory?.nameAr ?: "حدد قسم وتخصص الفني الرئيسي 📂 *")
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

        // Subcategory Selection
        selectedCategory?.let { cat ->
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { isSubCatMenuOpen = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedSubCat?.nameAr ?: "اختر تفريعة المهارة الفرعية للمهني")
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

        OutlinedTextField(
            value = personalPhotoUrl,
            onValueChange = { personalPhotoUrl = it },
            placeholder = { Text("رابط الصورة الشخصية أو السلفي (اختياري)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Grant VIP Elite checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = grantVipImmediately, onCheckedChange = { grantVipImmediately = it })
            Text("منح شارة نخبة VIP مباشرة وموثقة فورياً 🌟", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = {
                val finalName = name.ifEmpty { "مهني في تخصص " + (selectedCategory?.nameAr ?: "العام") }
                val finalPhone = phone.ifEmpty { "بلا هاتف" }
                val manualProv = ServiceProvider(
                    name = finalName,
                    phone = finalPhone,
                    categoryId = selectedCategory?.id ?: "cat1",
                    categoryName = selectedCategory?.nameAr ?: "عام",
                    subCategoryId = selectedSubCat?.id ?: "",
                    subCategoryName = selectedSubCat?.nameAr ?: "",
                    region = region.ifEmpty { "صنعاء" },
                    address = address,
                    personalPhoto = personalPhotoUrl.ifEmpty { "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100" },
                    isVerified = true,
                    isRecommended = grantVipImmediately,
                    isPremium = grantVipImmediately,
                    inspectionCost = inspectionCost.ifEmpty { "مجاني" }
                )
                viewModel.addProviderManually(manualProv) {
                    Toast.makeText(context, "تم إدراك الفني وإضافته مباشر للدليل العام بنجاح! 🛠️", Toast.LENGTH_SHORT).show()
                    name = ""
                    phone = ""
                    region = ""
                    address = ""
                    inspectionCost = ""
                    personalPhotoUrl = ""
                    grantVipImmediately = false
                    selectedCategory = null
                    selectedSubCat = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("إضافة مباشر للدليل 🚀")
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
    var editingAdminId by remember { mutableStateOf<String?>(null) }

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
                    Text(if (editingAdminId != null) "📝 تعديل بيانات المشرف الحالية" else "➕ تعيين مشرف وباب دخول خلفي جديد", fontWeight = FontWeight.Bold)
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

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                if (username.isNotEmpty() && password.isNotEmpty()) {
                                    val finalId = editingAdminId ?: ("adm_" + System.currentTimeMillis())
                                    val userToUpdate = AdminUser(id = finalId, username = username.trim(), password = password.trim(), role = adminRole)
                                    viewModel.updateAdminUser(userToUpdate) { success ->
                                        if (success) {
                                            if (editingAdminId != null) {
                                                Toast.makeText(context, "تم تعديل وحفظ بيانات المشرف سحابياً! 💾", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "تم إضافة وترقية المشرف الجديد بنجاح! 🎉", Toast.LENGTH_SHORT).show()
                                            }
                                            username = ""
                                            password = ""
                                            editingAdminId = null
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "يرجى ملء جميع الحقول أولاً!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (editingAdminId != null) "تحديث وحفظ التعديلات 💾" else "إضافة مشرف لخدمتكم ➕")
                        }
                        
                        if (editingAdminId != null) {
                            Button(
                                onClick = {
                                    username = ""
                                    password = ""
                                    editingAdminId = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("إلغاء ❌")
                            }
                        }
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
                        Text("الدور وصلاحيات المشرف: ${adm.role}", fontSize = 11.sp, color = Color.Gray)
                        Text("رمز المرور: ${adm.password}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    if (role == "owner") {
                        Row {
                            IconButton(onClick = {
                                editingAdminId = adm.id
                                username = adm.username
                                password = adm.password
                                adminRole = adm.role
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "تعديل بيانات المشرف", tint = MaterialTheme.colorScheme.secondary)
                            }
                            IconButton(onClick = { viewModel.removeAdminUser(adm.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف مشرف", tint = Color.Red)
                            }
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
    var appNameEn by remember { mutableStateOf(settings.appNameEn) }
    var footerText by remember { mutableStateOf(settings.promoFooterText) }
    var supportPhone by remember { mutableStateOf(settings.supportPhone) }
    var supportEmail by remember { mutableStateOf(settings.supportEmail) }
    var supportWhatsapp by remember { mutableStateOf(settings.supportWhatsapp) }
    var primaryColor by remember { mutableStateOf(settings.primaryColor) }
    var secondaryColor by remember { mutableStateOf(settings.secondaryColor) }
    
    // About Page Customize states
    var appSharingLink by remember { mutableStateOf(settings.appSharingLink) }
    var aboutCoverUrl by remember { mutableStateOf(settings.aboutCoverUrl) }
    var aboutCoverText by remember { mutableStateOf(settings.aboutCoverText) }
    var aboutCoverType by remember { mutableStateOf(settings.aboutCoverType) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            aboutCoverUrl = uri.toString()
            aboutCoverType = "image"
        }
    }
    
    var themeChoice by remember { mutableStateOf(settings.themeChoice) }
    var isMaintenanceMode by remember { mutableStateOf(settings.isMaintenanceMode) }
    var maintenanceMessage by remember { mutableStateOf(settings.maintenanceMessage) }

    // Assistant settings
    var assistantEnabled by remember { mutableStateOf(settings.assistantEnabled) }
    var assistantSize by remember { mutableStateOf(settings.assistantSize) }
    var assistantIconUrl by remember { mutableStateOf(settings.assistantIconUrl) }

    // Chat and Global Styling configurations
    var topBarIconOrder by remember { mutableStateOf(settings.topBarIconOrder) }
    var isChatEnabled by remember { mutableStateOf(settings.isChatEnabled) }
    var chatIconSize by remember { mutableStateOf(settings.chatIconSize) }
    var chatIconColor by remember { mutableStateOf(settings.chatIconColor) }
    var chatVisibility by remember { mutableStateOf(settings.chatVisibility) }
    var chatDisabledMessage by remember { mutableStateOf(settings.chatDisabledMessage) }
    var globalTextSize by remember { mutableStateOf(settings.globalTextSize) }
    var globalTextColor by remember { mutableStateOf(settings.globalTextColor) }
    var globalFontFamily by remember { mutableStateOf(settings.globalFontFamily) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🎨 إعدادات مظهر وهوية دليلي العام", fontWeight = FontWeight.Bold)
                
                OutlinedTextField(value = appNameAr, onValueChange = { appNameAr = it }, label = { Text("اسم التطبيق الرئيسي (عربي)") })
                OutlinedTextField(value = appNameEn, onValueChange = { appNameEn = it }, label = { Text("اسم التطبيق الفرعي (English)") })
                OutlinedTextField(value = footerText, onValueChange = { footerText = it }, label = { Text("تذييل وترويج الصفحات (Footer Text)") })
                OutlinedTextField(value = topBarIconOrder, onValueChange = { topBarIconOrder = it }, label = { Text("ترتيب أيقونات الشريط العلوي (مفصولة بفاصلة: home,login,register,lang,refresh)") })

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
                OutlinedTextField(value = appSharingLink, onValueChange = { appSharingLink = it }, label = { Text("رابط مشاركة وتحميل التطبيق") })
                
                Text("تخصيص صورة وغلاف صفحة (عن دليلي):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("image" to "عرض صورة 🖼️", "text" to "عرض نص/إيموجي ✍️").forEach { (type, label) ->
                        FilterChip(
                            selected = aboutCoverType == type,
                            onClick = { aboutCoverType = type },
                            label = { Text(label) }
                        )
                    }
                }
                
                if (aboutCoverType == "image") {
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("📁 اختر صورة الغلاف من الذاكرة (الاستوديو)")
                    }
                    if (aboutCoverUrl.startsWith("content://")) {
                        Text("تم اختيار صورة محلية بنجاح! ✔️", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    OutlinedTextField(
                        value = aboutCoverUrl,
                        onValueChange = { aboutCoverUrl = it },
                        label = { Text("أو الصق رابط صورة الغلاف مباشر من النت") }
                    )
                } else {
                    OutlinedTextField(
                        value = aboutCoverText,
                        onValueChange = { aboutCoverText = it },
                        label = { Text("اكتب نص أو إيموجي الغلاف بدلاً من الصورة") }
                    )
                }

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

                Text("💬 تخصيص نظام المحادثة الفورية (Real-time Chat)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isChatEnabled, onCheckedChange = { isChatEnabled = it })
                    Text("تشغيل نظام المحادثة للجميع", fontSize = 12.sp)
                }
                OutlinedTextField(value = chatDisabledMessage, onValueChange = { chatDisabledMessage = it }, label = { Text("الرسالة المخصصة عند تعطيل الدردشة") })
                OutlinedTextField(value = chatIconColor, onValueChange = { chatIconColor = it }, label = { Text("لون أيقونة الدردشة (Hex مثل 1E88E5#)") })
                OutlinedTextField(value = chatIconSize.toString(), onValueChange = { chatIconSize = it.toIntOrNull() ?: 28 }, label = { Text("حجم أيقونة الدردشة (الافتراضي 28)") })
                
                Text("ظهور أيقونة الدردشة مؤقتاً:")
                Row {
                    listOf("visible" to "مرئية للجميع 👁️", "hidden" to "مخفية مؤقتاً 🙈", "deleted" to "محذوفة نهائياً ❌").forEach { (v, label) ->
                        FilterChip(
                            selected = chatVisibility == v,
                            onClick = { chatVisibility = v },
                            label = { Text(label) }
                        )
                    }
                }

                Divider()
                
                Text("✍️ خيارات الخطوط والنصوص العامة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                OutlinedTextField(value = globalTextSize.toString(), onValueChange = { globalTextSize = it.toIntOrNull() ?: 16 }, label = { Text("حجم الخط العام") })
                OutlinedTextField(value = globalTextColor, onValueChange = { globalTextColor = it }, label = { Text("لون النصوص العامة (Hex)") })
                OutlinedTextField(value = globalFontFamily, onValueChange = { globalFontFamily = it }, label = { Text("نوع الخط العام") })

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
                        appNameEn = appNameEn,
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
                        assistantIconUrl = assistantIconUrl,
                        topBarIconOrder = topBarIconOrder,
                        isChatEnabled = isChatEnabled,
                        chatIconSize = chatIconSize,
                        chatIconColor = chatIconColor,
                        chatVisibility = chatVisibility,
                        chatDisabledMessage = chatDisabledMessage,
                        globalTextSize = globalTextSize,
                        globalTextColor = globalTextColor,
                        globalFontFamily = globalFontFamily,
                        appSharingLink = appSharingLink,
                        aboutCoverUrl = aboutCoverUrl,
                        aboutCoverText = aboutCoverText,
                        aboutCoverType = aboutCoverType
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

// ---------------- SECRET BACKDOOR SCREEN ----------------

@Composable
fun SecretBackdoorScreen(
    settings: AppSettings,
    viewModel: DaliliViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("dalili_prefs", Context.MODE_PRIVATE)
    
    // Check if user saved backdoor login before
    var saveBackdoorLogin by remember { mutableStateOf(sharedPref.getBoolean("save_backdoor_login", false)) }
    var inputPasscode by remember { mutableStateOf(if (saveBackdoorLogin) "maher--736462" else "") }
    var isUnlocked by remember { mutableStateOf(saveBackdoorLogin) }

    // Backup local parameters
    var appNameAr by remember { mutableStateOf(settings.appNameAr) }
    var appNameEn by remember { mutableStateOf(settings.appNameEn) }
    var pColor by remember { mutableStateOf(settings.primaryColor) }
    var sColor by remember { mutableStateOf(settings.secondaryColor) }
    var logoUrl by remember { mutableStateOf(settings.logoUrl) }
    var footerText by remember { mutableStateOf(settings.promoFooterText) }
    var welcomeMsg by remember { mutableStateOf(settings.welcomeMessage) }
    var globalTextSize by remember { mutableStateOf(settings.globalTextSize) }
    var supportPhone by remember { mutableStateOf(settings.supportPhone) }
    var supportEmail by remember { mutableStateOf(settings.supportEmail) }
    var supportWhatsapp by remember { mutableStateOf(settings.supportWhatsapp) }
    
    // Additional parameters
    var voiceChoice by remember { mutableStateOf(settings.voiceSearchEnabled) }
    var isDataSavingChoice by remember { mutableStateOf(settings.isDataSavingMode) }
    var radiusChoice by remember { mutableFloatStateOf(settings.maxSearchRadius) }
    var isChatEnabledChoice by remember { mutableStateOf(settings.isChatEnabled) }
    var chatSizeChoice by remember { mutableStateOf(settings.chatIconSize) }
    var chatColorChoice by remember { mutableStateOf(settings.chatIconColor) }
    var chatVisChoice by remember { mutableStateOf(settings.chatVisibility) }
    var chatDisabledMsg by remember { mutableStateOf(settings.chatDisabledMessage) }
    
    // Owner Security Password Change
    var newOwnerPasswordInput by remember { mutableStateOf("") }
    
    // Collecting states from viewmodel
    val categories by viewModel.categories.collectAsState()
    val subCategories by viewModel.subCategories.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val complaints by viewModel.complaints.collectAsState()
    val adminsList by viewModel.admins.collectAsState()
    val cities by viewModel.cities.collectAsState()

    // Backdoor Navigation tab switcher
    var backdoorActiveTab by remember { mutableStateOf("config") } // config, category_city, providers, supervisors, logs
    
    // Cities/Governorates tab parameters
    var newCityAr by remember { mutableStateOf("") }
    var newCityEn by remember { mutableStateOf("") }
    
    // Categories tab parameters
    var newCatAr by remember { mutableStateOf("") }
    var newCatEn by remember { mutableStateOf("") }
    var newCatIcon by remember { mutableStateOf("📁") }
    
    // Subcategories parameters
    var selectedParentCatId by remember { mutableStateOf("") }
    var newSubAr by remember { mutableStateOf("") }
    var newSubEn by remember { mutableStateOf("") }
    
    // Supervisors parameters
    var newSuperUsername by remember { mutableStateOf("") }
    var newSuperPassword by remember { mutableStateOf("") }
    var p1 by remember { mutableStateOf(true) } // manage_categories
    var p2 by remember { mutableStateOf(true) } // manage_providers
    var p3 by remember { mutableStateOf(true) } // registration_requests
    var p4 by remember { mutableStateOf(true) } // backup_restore
    var p5 by remember { mutableStateOf(true) } // general_config
    var editingBackdoorSuperId by remember { mutableStateOf<String?>(null) }
    
    // Clean-up params
    var cleanDaysOption by remember { mutableStateOf(30) } // 7, 30, 90 days

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (!isUnlocked) {
            Text("🔒", fontSize = 72.sp)
            Spacer(Modifier.height(12.dp))
            Text("البوابة الملكية الخلفية للتحكم بالبنية", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("صلاحية مطلقة مالك التطبيق الرئيسي لترميم المنظومة", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
            
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = inputPasscode,
                onValueChange = { inputPasscode = it },
                label = { Text("أدخل رمز المرور السري للمالك") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(10.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = saveBackdoorLogin,
                    onCheckedChange = { 
                        saveBackdoorLogin = it 
                        sharedPref.edit().putBoolean("save_backdoor_login", it).apply()
                    }
                )
                Text("حفظ تسجيل الدخول للمالك على هذا الجهاز", fontSize = 12.sp)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (inputPasscode == "maher--736462") {
                        isUnlocked = true
                        sharedPref.edit().putBoolean("save_backdoor_login", saveBackdoorLogin).apply()
                    } else {
                        Toast.makeText(context, "رمز المرور خاطئ! يرجى الاستعانة بمالك المنصة الرئيسي.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("المصادقة وفك الحجب 🔓")
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onBack) {
                Text("رجوع للرئيسية")
            }
        } else {
            // UNLOCKED BACKDOOR CONTROLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👑 نظام الإدارة اللامتناهية للمالك", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = {
                    sharedPref.edit().putBoolean("save_backdoor_login", false).apply()
                    saveBackdoorLogin = false
                    inputPasscode = ""
                    isUnlocked = false
                    Toast.makeText(context, "تم قفل البوابة وخروج المالك بنجاح", Toast.LENGTH_SHORT).show()
                }) {
                    Text("🔐 خروج المالك", color = Color.Red, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(10.dp))

            // BACKDOOR COMPONENT TABS SELECTOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    Triple("config", "⚙️ الإعدادات", "العامة للحوسبة"),
                    Triple("category_city", "📁 الفئات والمدن", "الأقسام والمناطق"),
                    Triple("providers", "🛠️ المقاولين", "توصية وتثبيت وجرد"),
                    Triple("supervisors", "🛡️ المشرفين", "بلاغات وتحوطات"),
                    Triple("logs", "📊 الأنماط والحوسبة", "بينات وسجلات")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(tabs.size) { i ->
                        val t = tabs[i]
                        val isSelected = backdoorActiveTab == t.first
                        FilterChip(
                            selected = isSelected,
                            onClick = { backdoorActiveTab = t.first },
                            label = { Text(t.second, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            when (backdoorActiveTab) {
                "config" -> {
                    // CONFIG AND GENERAL STYLES TAB
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("🛠️ الهوية والبيانات الفنية السحابية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            
                            OutlinedTextField(value = appNameAr, onValueChange = { appNameAr = it }, label = { Text("تعديل اسم التطبيق (عربي)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = appNameEn, onValueChange = { appNameEn = it }, label = { Text("تعديل اسم التطبيق (إنجليزي)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = logoUrl, onValueChange = { logoUrl = it }, label = { Text("رابط الشعار / الأيقونة المباشر (Launcher Icon Link)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = pColor, onValueChange = { pColor = it }, label = { Text("اللون الأساسي للهوية السحابية (#Hex)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = sColor, onValueChange = { sColor = it }, label = { Text("اللون الثانوي للهوية السحابية (#Hex)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = footerText, onValueChange = { footerText = it }, label = { Text("التذييل الدعائي المتحرك والترويجي") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = welcomeMsg, onValueChange = { welcomeMsg = it }, label = { Text("رسالة الترحيب الخاصة بالواجهات") }, modifier = Modifier.fillMaxWidth())
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            
                            Text("📞 أرقام وعناوين التواصل والدعم الفني للمنظومة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            OutlinedTextField(value = supportPhone, onValueChange = { supportPhone = it }, label = { Text("هاتف دعم المالك الرئيسي") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = supportWhatsapp, onValueChange = { supportWhatsapp = it }, label = { Text("رقم واتساب الدعم الفني المباشر") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = supportEmail, onValueChange = { supportEmail = it }, label = { Text("البريد الإلكتروني المعتمد للدعم التقني") })

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            
                            Text("🔒 أمن الحساب الرئيسي (WAM2026 Admin Bypass)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            OutlinedTextField(
                                value = newOwnerPasswordInput,
                                onValueChange = { newOwnerPasswordInput = it },
                                label = { Text("اكتب كلمة مرور المدير WAM2026 الجديدة") },
                                placeholder = { Text("تغيير كلمة المرور الافتراضية maher736462") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            
                            // Feature control Switches
                            Text("🎙️ وضع الصوت والبيانات المفتوحة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Switch(checked = voiceChoice, onCheckedChange = { voiceChoice = it })
                                Spacer(Modifier.width(10.dp))
                                Text("تمكين محرك البحث الصوتي الذكي (ar-YE)", fontSize = 12.sp)
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Switch(checked = isDataSavingChoice, onCheckedChange = { isDataSavingChoice = it })
                                Spacer(Modifier.width(10.dp))
                                Text("تفعيل وضع توفير البيانات القاسي (تعطيل تحميل سلفي الفنيين تلقائياً)", fontSize = 12.sp)
                            }
                            
                            Column {
                                Text("🌐 حد خريطة البحث الجغرافي المسافي: ${radiusChoice.toInt()} كم", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Slider(
                                    value = radiusChoice,
                                    onValueChange = { radiusChoice = it },
                                    valueRange = 5f..500f,
                                    steps = 10
                                )
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            
                            // Chat icons adjustments
                            Text("💬 تعديل وتخصيص واجهة الدعم والدردشة النشطة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Switch(checked = isChatEnabledChoice, onCheckedChange = { isChatEnabledChoice = it })
                                Spacer(Modifier.width(10.dp))
                                Text("تفعيل نظام الدردشة المباشرة (شاغر الدعم الفني)", fontSize = 12.sp)
                            }
                            
                            if (!isChatEnabledChoice) {
                                OutlinedTextField(
                                    value = chatDisabledMsg,
                                    onValueChange = { chatDisabledMsg = it },
                                    label = { Text("نص الإشعار التلقائي للمستخدمين لتوقف المحادثة") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            OutlinedTextField(
                                value = chatColorChoice,
                                onValueChange = { chatColorChoice = it },
                                label = { Text("لون أيقونة الدردشة (#Hex)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Column {
                                Text("حجم أيقونة الدردشة العائمة: ${chatSizeChoice} dp", fontSize = 11.sp)
                                Slider(
                                    value = chatSizeChoice.toFloat(),
                                    onValueChange = { chatSizeChoice = it.toInt() },
                                    valueRange = 15f..60f
                                )
                            }
                            
                            Text("رؤية وحالة أيقونة الدعم والمحادثة:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val visOpts = listOf("visible" to "👀 إظهار تلقائي", "hidden" to "🙈 إخفاء مؤقت", "deleted" to "❌ حذف الزر بالكامل")
                                visOpts.forEach { opt ->
                                    FilterChip(
                                        selected = chatVisChoice == opt.first,
                                        onClick = { chatVisChoice = opt.first },
                                        label = { Text(opt.second, fontSize = 10.sp) }
                                    )
                                }
                            }
                            
                            Spacer(Modifier.height(10.dp))
                            
                            Button(
                                onClick = {
                                    val finalOwnerPw = newOwnerPasswordInput.trim().ifEmpty { "maher736462" }
                                    val nextS = settings.copy(
                                        appNameAr = appNameAr,
                                        appNameEn = appNameEn,
                                        primaryColor = pColor,
                                        secondaryColor = sColor,
                                        logoUrl = logoUrl,
                                        promoFooterText = footerText,
                                        welcomeMessage = welcomeMsg,
                                        globalTextSize = globalTextSize,
                                        supportPhone = supportPhone,
                                        supportWhatsapp = supportWhatsapp,
                                        supportEmail = supportEmail,
                                        voiceSearchEnabled = voiceChoice,
                                        isDataSavingMode = isDataSavingChoice,
                                        maxSearchRadius = radiusChoice,
                                        isChatEnabled = isChatEnabledChoice,
                                        chatIconSize = chatSizeChoice,
                                        chatIconColor = chatColorChoice,
                                        chatVisibility = chatVisChoice,
                                        chatDisabledMessage = chatDisabledMsg
                                    )
                                    viewModel.updateSettings(nextS) { ok ->
                                        if (ok) {
                                            if (newOwnerPasswordInput.trim().isNotEmpty()) {
                                                viewModel.changeOwnerPassword(finalOwnerPw) { pwOk ->
                                                    Toast.makeText(context, "تم حفظ الإعدادات السحابية وتغيير كلمة مرور المالك WAM2026 بنجاح! ⚡", Toast.LENGTH_LONG).show()
                                                    newOwnerPasswordInput = ""
                                                }
                                            } else {
                                                Toast.makeText(context, "تم ترحيل وحفظ التعديلات السرية بنجاح!", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "فشل ترحيل الإعدادات. يرجى مراجعة حالة الاتصال بالانترنت.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("تحديث ومزامنة البيانات السحابية بالكامل ⚡")
                            }
                        }
                    }
                }
                
                "category_city" -> {
                    // CATEGORIES AND COVERAGE CITIES MANAGEMENT TAB
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("📍 إدارة مدن ومحافظات التغطية الجغرافية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("قم بإضافة أو حذف مدن اليمن المعتمدة التي تظهر لمزودي الخدمة أثناء التسجيل لتسهيل الفرز.", fontSize = 11.sp, color = Color.Gray)
                            
                            OutlinedTextField(value = newCityAr, onValueChange = { newCityAr = it }, label = { Text("المحافظة / المدينة (عربي)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = newCityEn, onValueChange = { newCityEn = it }, label = { Text("City / Governorate (English)") }, modifier = Modifier.fillMaxWidth())
                            
                            Button(
                                onClick = {
                                    if (newCityAr.trim().isNotEmpty() && newCityEn.trim().isNotEmpty()) {
                                        val newCity = City(id = "city_" + System.currentTimeMillis(), nameAr = newCityAr.trim(), nameEn = newCityEn.trim())
                                        viewModel.addCity(newCity) { ok ->
                                            if (ok) {
                                                Toast.makeText(context, "تم إضافة منطقة التغطية المحددة سحابياً!", Toast.LENGTH_SHORT).show()
                                                newCityAr = ""
                                                newCityEn = ""
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "يرجى ملء اسم المدينة بالعربي والإنجليزي!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("إضافة مدينة تغطية جديدة 🗺️")
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            Text("المدن المسجلة حالياً وتغطيتها:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            
                            cities.forEach { city ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${city.nameAr} - ${city.nameEn}", fontSize = 12.sp)
                                    IconButton(onClick = {
                                        viewModel.deleteCity(city.id) {
                                            Toast.makeText(context, "تم حذف مدينة التغطية من السحابة", Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف فرعي", tint = Color.Red)
                                    }
                                }
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            Text("📁 إضافة قسم وتخصص رئيسي جديد", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            OutlinedTextField(value = newCatAr, onValueChange = { newCatAr = it }, label = { Text("اسم القسم رئيسي (عربي)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = newCatEn, onValueChange = { newCatEn = it }, label = { Text("Category Name (En)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = newCatIcon, onValueChange = { newCatIcon = it }, label = { Text("أيقونة أو رمز تعبيري (Emoji)") }, modifier = Modifier.fillMaxWidth())
                            
                            Button(
                                onClick = {
                                    if (newCatAr.isNotEmpty()) {
                                        val newId = "cat_" + System.currentTimeMillis()
                                        val c = Category(newId, newCatAr, newCatEn.ifEmpty { newCatAr }, newCatIcon)
                                        viewModel.addMainCategory(c) {
                                            Toast.makeText(context, "تم ترحيل الفئة الرئيسية بنجاح!", Toast.LENGTH_SHORT).show()
                                            newCatAr = ""
                                            newCatEn = ""
                                            newCatIcon = "📁"
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("حفظ القسم الرئيسي المضافة 📂")
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            Text("🌿 إضافة وتفريع مهنة فرعية (SubCategory)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("يجب اختيار القسم الأب أولاً لإلحاق المهارات تحتها:", fontSize = 11.sp, color = Color.Gray)
                            
                            Box(modifier = Modifier.fillMaxWidth()) {
                                var catDropOpen by remember { mutableStateOf(false) }
                                val selectedCatLabel = categories.find { it.id == selectedParentCatId }?.nameAr ?: "اختر الفئة الأب 📂"
                                OutlinedButton(onClick = { catDropOpen = true }, modifier = Modifier.fillMaxWidth()) {
                                    Text(selectedCatLabel)
                                }
                                DropdownMenu(expanded = catDropOpen, onDismissRequest = { catDropOpen = false }) {
                                    categories.forEach { ct ->
                                        DropdownMenuItem(text = { Text(ct.nameAr) }, onClick = {
                                            selectedParentCatId = ct.id
                                            catDropOpen = false
                                        })
                                    }
                                }
                            }
                            
                            OutlinedTextField(value = newSubAr, onValueChange = { newSubAr = it }, label = { Text("اسم المهنة الفرعية (عربي)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = newSubEn, onValueChange = { newSubEn = it }, label = { Text("SubCategory (English)") }, modifier = Modifier.fillMaxWidth())
                            
                            Button(
                                onClick = {
                                    if (selectedParentCatId.isNotEmpty() && newSubAr.isNotEmpty()) {
                                        val subId = "sub_" + System.currentTimeMillis()
                                        val s = SubCategory(subId, selectedParentCatId, newSubAr, newSubEn.ifEmpty { newSubAr })
                                        viewModel.addSubCategory(s) {
                                            Toast.makeText(context, "تم ترحيل المهنة والتفريعة بنجاح!", Toast.LENGTH_SHORT).show()
                                            newSubAr = ""
                                            newSubEn = ""
                                        }
                                    } else {
                                        Toast.makeText(context, "يرجى تحديد الفئة وتعبئة البيانات الفرعية!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text("إدراج المهنة الفرعية الملحقة 💾")
                            }
                        }
                    }
                }
                
                "providers" -> {
                    // SERVICE PROVIDERS SPECIAL PINNING & VALIDATION TAB
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("⭐ التوصية والترقية لمزودي الخدمات", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("تثبيت مقدمي الخدمات في صدارة البحث أو تمييزهم بشارة كبار المهنيين (Elite VIP / Recommended) لجبر الدخل.", fontSize = 11.sp, color = Color.Gray)
                            
                            if (providers.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                    Text("دليل الفنيين فارغ حالياً.", color = Color.Gray)
                                }
                            } else {
                                providers.forEach { prov ->
                                    val isPinned = prov.isPinned
                                    val isRecommended = prov.isRecommended
                                    val isPremium = prov.isPremium
                                    val isBlocked = prov.isBlocked
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(prov.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                if (isPremium || isRecommended) {
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("⭐ نخبة", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Text("الهاتف: ${prov.phone} | ${prov.categoryName}", fontSize = 11.sp, color = Color.Gray)
                                            Text("المعاينة: ${prov.inspectionCost.ifEmpty { "غير محدد" }} | المدينة: ${prov.region}", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        
                                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        val updated = prov.copy(isRecommended = !isRecommended, isPremium = !isRecommended)
                                                        viewModel.addProviderManually(updated) { // updates if ID exists or re-saves
                                                            Toast.makeText(context, "تم تغيير حالة النخبة والتمييز للفني!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = "تميز",
                                                        tint = if (isRecommended) Color(0xFFFFD700) else Color.Gray
                                                    )
                                                }
                                                
                                                IconButton(
                                                    onClick = {
                                                        val updated = prov.copy(isPinned = !isPinned)
                                                        viewModel.addProviderManually(updated) {
                                                            Toast.makeText(context, "تم تعديل حالة التثبيت بالصدارة!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Favorite,
                                                        contentDescription = "Pinned",
                                                        tint = if (isPinned) Color.Red else Color.Gray
                                                    )
                                                }
                                            }
                                            
                                            Button(
                                                onClick = {
                                                    val updated = prov.copy(isBlocked = !isBlocked)
                                                    viewModel.addProviderManually(updated) {
                                                        val state = if (!isBlocked) "متبقي الحظر" else "نشط"
                                                        Toast.makeText(context, "تم تعديل حساب الفني لـ: $state", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isBlocked) Color.Green else Color.Red
                                                ),
                                                modifier = Modifier.height(24.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(if (isBlocked) "فك الحظر ✅" else "حظر الفني ❌", fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                "supervisors" -> {
                    // SUPERVISOR AND REGISTERED COMPLAINTS LOGS TAB
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(if (editingBackdoorSuperId != null) "📝 تعديل بيانات وصلاحيات المشرف المساعد" else "👮 إدارة وتعيين المشرفين والمساعدين مع الصلاحيات الخمس", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("تسمح هذه البوابة المخصصة للمالك فقط بصياغة وضبط أدمن النظام المساعدين مع تخصيص المربعات الخمس للصلاحيات العريضة بالمنظومة.", fontSize = 11.sp, color = Color.Gray)
                            
                            OutlinedTextField(value = newSuperUsername, onValueChange = { newSuperUsername = it }, label = { Text("اسم مستخدم المشرف") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = newSuperPassword, onValueChange = { newSuperPassword = it }, label = { Text("رمز المرور السري") }, modifier = Modifier.fillMaxWidth())
                            
                            Text("مربعات تخصيص الصلاحيات الممنوحة للمشرف:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = p1, onCheckedChange = { p1 = it })
                                Text("إدارة وحذف وإضافة الأقسام والتصنيفات", fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = p2, onCheckedChange = { p2 = it })
                                Text("إدارة مزودي الخدمات وفصلهم وحظرهم وتوثيقهم", fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = p3, onCheckedChange = { p3 = it })
                                Text("مراجعة وقبول طلبات التسجيل للفنيين ومطالعة السيلفي", fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = p4, onCheckedChange = { p4 = it })
                                Text("التحوط بنسخة احتياطية ومعاينة جرد الإحصائيات", fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = p5, onCheckedChange = { p5 = it })
                                Text("إدارة الضوابط العامة والإعدادات وشكل التطبيق", fontSize = 11.sp)
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        if (newSuperUsername.isNotEmpty() && newSuperPassword.isNotEmpty()) {
                                            val roleString = buildString {
                                                if (p1) append("manage_categories,")
                                                if (p2) append("manage_providers,")
                                                if (p3) append("registration_requests,")
                                                if (p4) append("backup_restore,")
                                                if (p5) append("general_config,")
                                            }.removeSuffix(",")
                                            
                                            val targetId = editingBackdoorSuperId ?: ("sup_" + System.currentTimeMillis())
                                            val updatedSupervisor = AdminUser(
                                                id = targetId,
                                                username = newSuperUsername.trim(),
                                                password = newSuperPassword.trim(),
                                                role = roleString.ifEmpty { "view_only" }
                                            )
                                            viewModel.updateAdminUser(updatedSupervisor) { ok ->
                                                if (ok) {
                                                    if (editingBackdoorSuperId != null) {
                                                        Toast.makeText(context, "تم تحديث وحفظ تعديلات المشرف بنجاح! 💾", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "تم تعيين المشرف وصلاحياته الخمس سحابياً فوراً! 🎉", Toast.LENGTH_SHORT).show()
                                                    }
                                                    newSuperUsername = ""
                                                    newSuperPassword = ""
                                                    p1 = true; p2 = true; p3 = true; p4 = true; p5 = true
                                                    editingBackdoorSuperId = null
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "الرجاء تعبئة اسم المشرف ورمز المرور!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (editingBackdoorSuperId != null) "حفظ التعديلات الحالية 💾" else "تسجيل المشرف بالصلاحيات المحددة 🛡️")
                                }
                                
                                if (editingBackdoorSuperId != null) {
                                    Button(
                                        onClick = {
                                            newSuperUsername = ""
                                            newSuperPassword = ""
                                            p1 = true; p2 = true; p3 = true; p4 = true; p5 = true
                                            editingBackdoorSuperId = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("إلغاء ❌")
                                    }
                                }
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            Text("👤 المشرفين والمساعدين المسجلين حالياً:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            adminsList.forEach { adm ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("أدمن: ${adm.username}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("اصطلاحاً وصلاحية: ${adm.role}", fontSize = 10.sp, color = Color.Gray)
                                        Text("رمز المرور السري: ${adm.password}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    if (adm.username != "WAM2026") {
                                        Row {
                                            IconButton(onClick = {
                                                editingBackdoorSuperId = adm.id
                                                newSuperUsername = adm.username
                                                newSuperPassword = adm.password
                                                p1 = adm.role.contains("manage_categories")
                                                p2 = adm.role.contains("manage_providers")
                                                p3 = adm.role.contains("registration_requests")
                                                p4 = adm.role.contains("backup_restore")
                                                p5 = adm.role.contains("general_config")
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit supervisor", tint = MaterialTheme.colorScheme.secondary)
                                            }
                                            IconButton(onClick = {
                                                viewModel.removeAdminUser(adm.id)
                                                Toast.makeText(context, "تم إلغاء تسجيل وتطهير المشرف سحابياً بنجاح!", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Unregister supervisor", tint = Color.Red)
                                            }
                                        }
                                    }
                                }
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            Text("📨 شكاوى وبلاغات المستخدمين والزوار (${complaints.size})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            if (complaints.isEmpty()) {
                                Text("لا تشوب المنظومة أي شكاوى مسجلة حالياً.", fontSize = 11.sp, color = Color.Gray)
                            } else {
                                complaints.forEach { comp ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("الشاكي: ${comp.reporterName} (${comp.reporterPhone})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("الفني المشتكى عليه: ${comp.providerName}", fontSize = 11.sp)
                                            Text("الشكوى: ${comp.reasonText}", fontSize = 12.sp, style = MaterialTheme.typography.bodyMedium)
                                            Spacer(Modifier.height(4.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.deleteComplaint(comp.id)
                                                    Toast.makeText(context, "تم معاينة الشكوى وحذفها من الأرشفة السحابية 🗑️", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.align(Alignment.End),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                            ) {
                                                Text("أرشفة وحذف الشكوى 🗑️", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                "logs" -> {
                    // BACKUP, STATISTICS AND TEMPORARY DATA CLEANUP TAB
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("📊 الإحصاءات العامة للمنظومة", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("• عدد الأقسام الرئيسية: ${categories.size}", fontSize = 12.sp)
                            Text("• عدد المهن التفصيلية: ${subCategories.size}", fontSize = 12.sp)
                            Text("• إجمالي الفنيين ومزودي الخدمة: ${providers.size}", fontSize = 12.sp)
                            Text("• المدن والمناطق المغطاة بالتنسيق: ${cities.size}", fontSize = 12.sp)
                            Text("• شكاوى الفنيين النشطة: ${complaints.size}", fontSize = 12.sp)
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            Text("🔋 نظام دعم أرشفة وحفظ قواعد البيانات (Backup)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("زر النسخ الاحتياطي السحابي لحفظ ومزامنة وحماية بيانات المشرفين ومزودي الخدمات من الضياع والتلف في حال تعطلت الأجهزة.", fontSize = 11.sp, color = Color.Gray)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.triggerBackupDatabase { resultMsg ->
                                            Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("أخذ نسخة احتياطية 🗄️", fontSize = 11.sp)
                                }
                                
                                Button(
                                    onClick = {
                                        viewModel.restoreDatabaseFromBackup { resultMsg ->
                                            Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text("استعادة النسخة المئوية 🔄", fontSize = 11.sp)
                                }
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            Text("🧹 تصفية تلقائية وتنظيف البيانات المؤقتة", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("تعمل هذه الأداة على تسريع خوادم دليلي بتطهير السجلات وبلاغات المحادثة القديمة نهائياً وضغط قاعدة البيانات تلقائياً.", fontSize = 11.sp, color = Color.Gray)
                            
                            Text("اختر فترة الاحتفاظ بسجلات وبلاغات الأثر القديمة:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(7 to "7 أيام", 30 to "شخصي (30 يوم)", 90 to "90 يوم كاملة").forEach { days ->
                                    FilterChip(
                                        selected = cleanDaysOption == days.first,
                                        onClick = { cleanDaysOption = days.first },
                                        label = { Text(days.second, fontSize = 10.sp) }
                                    )
                                }
                            }
                            
                            Button(
                                onClick = {
                                    viewModel.cleanOldDataAndLogs(cleanDaysOption) {
                                        Toast.makeText(context, "تم تطهير وتصفية السجلات القديمة المنتهية الصلاحية ($cleanDaysOption يوم) والملفات العالقة!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("تنظيف البيانات والتطهير الفوري السريع 🧹")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("إغلاق والعودة للرئيسية")
            }
        }
    }
}

// ---------------- PROVIDER WORKSPACE DASHBOARD SCREEN ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    provider: ServiceProvider,
    chats: List<ChatMessage>,
    viewModel: DaliliViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var replyText by remember { mutableStateOf("") }
    
    // Group chats by unique client identifier (sender name + phone)
    val groupedChats = chats.filter { 
        it.receiverId == provider.id || it.senderId == provider.id
    }.groupBy { 
        if (it.senderId == provider.id) it.receiverId else it.senderId 
    }

    var selectedRoomId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("لوحة التحكم الفنية والخدمية 🛠️", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("مرحباً بك: ${provider.name}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }
            Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("خروج 🚪", fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        if (selectedRoomId == null) {
            // LIST ACTIVE ROOMS
            Text("💬 المحادثات النشطة الواردة من الزبائن:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))

            if (groupedChats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد أي رسائل أو محادثات نشطة حالياً مع هذا الرقم المهني.", color = Color.Gray, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val entries = groupedChats.entries.toList()
                    items(entries.size) { index ->
                        val entry = entries[index]
                        val senderKey = entry.key
                        val messagesList = entry.value
                        val lastMsg = messagesList.lastOrNull()
                        Card(
                            onClick = { selectedRoomId = senderKey },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("عميل طالب خدمات 👤", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${messagesList.size} رسائل", fontSize = 11.sp, color = Color.Gray)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = lastMsg?.text ?: "لا توجد رسالة",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // INTERMEDIARY ROOM DETAILED CHAT VIEW
            val roomMessages = groupedChats[selectedRoomId] ?: emptyList()
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { selectedRoomId = null }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Text("محادثة الزبون السريعة المباشرة", fontWeight = FontWeight.Bold)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // reverse list to read starting top latest bottom
                val reversedList = roomMessages.reversed()
                items(reversedList.size) { index ->
                    val msg = reversedList[index]
                    val isMe = msg.senderId == provider.id
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(msg.text, fontSize = 13.sp)
                                Text(
                                    text = if (isMe) "أنت" else "الزبون",
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // RECOGNIZED WRITING FIELD
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("اكتب ردك للزبون المباشر...") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        if (replyText.isNotEmpty()) {
                            val newMsg = ChatMessage(
                                senderId = provider.id,
                                senderName = provider.name,
                                receiverId = selectedRoomId ?: "",
                                receiverName = "الزبون",
                                text = replyText,
                                timestamp = System.currentTimeMillis()
                            )
                            viewModel.sendChatMessage(newMsg)
                            replyText = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "إرسال الرد", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// ---------------- ADMIN CENTRAL CHATS MANAGEMENT SCREEN ----------------

@Composable
fun AdminChatsManagementView(
    chats: List<ChatMessage>,
    providers: List<ServiceProvider>,
    viewModel: DaliliViewModel,
    settings: AppSettings
) {
    val context = LocalContext.current
    var selectedChatRoom by remember { mutableStateOf<String?>(null) }
    var superAdminReply by remember { mutableStateOf("") }

    // Grouping chats by roomId
    val chatRooms = chats.groupBy { 
        if (it.senderId.startsWith("customer_") || it.receiverId.startsWith("customer_")) {
            // customers
            if (it.senderId.startsWith("customer_")) it.senderId else it.receiverId
        } else {
            it.senderId
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💬 مراقبة وإدارة محادثات العملاء والفنيين", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // CSV export
                Button(
                    onClick = {
                        val csv = StringBuilder("ID,Sender,Receiver,Message,Timestamp\n")
                        chats.forEach {
                            csv.append("\"${it.id}\",\"${it.senderName}\",\"${it.receiverName}\",\"${it.text}\",\"${it.timestamp}\"\n")
                        }
                        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("Chats CSV", csv.toString()))
                        Toast.makeText(context, "تم توليد وتصدير ملف CSV ونسخه للحافظة بنجاح!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("تصدير CSV 📋", fontSize = 10.sp)
                }

                // Delete system logs
                Button(
                    onClick = {
                        viewModel.clearAllChats { ok ->
                            if (ok) {
                                Toast.makeText(context, "تم مسح كافة سجلات الدردشة نهائياً من Firestore ⚠️", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("مسح السجل 🗑️", fontSize = 10.sp)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        if (selectedChatRoom == null) {
            // List active rooms
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val roomsKeys = chatRooms.keys.toList()
                items(roomsKeys.size) { i ->
                    val roomKey = roomsKeys[i]
                    val msgs = chatRooms[roomKey] ?: emptyList()
                    val lastMsg = msgs.lastOrNull()
                    Card(
                        onClick = { selectedChatRoom = roomKey },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("محادثة العميل: ${lastMsg?.senderName ?: "زائر"}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${msgs.size} رسائل", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text("معرّف المرسل: ${lastMsg?.senderId ?: "فارغ"}", fontSize = 11.sp, color = Color.Gray)
                            Spacer(Modifier.height(4.dp))
                            Text("آخر رسالة: ${lastMsg?.text ?: "لا رسائل"}", fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }
            }
        } else {
            // Room Detailed view inside control
            val msgs = chatRooms[selectedChatRoom] ?: emptyList()
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { selectedChatRoom = null }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "تراجع")
                }
                Text("تفاصيل غرفة: ${selectedChatRoom}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(msgs.size) { index ->
                    val msg = msgs[index]
                    val isMeAdmin = msg.senderId == "super_admin_maher"
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMeAdmin) Alignment.CenterEnd else Alignment.CenterStart) {
                        Surface(
                            color = if (isMeAdmin) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(msg.text, fontSize = 12.sp)
                                Text("مرسل: ${msg.senderName} (ID: ${msg.senderId})", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Administrator reply channel
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = superAdminReply,
                    onValueChange = { superAdminReply = it },
                    placeholder = { Text("الرد كمشرف رئيسي (Super Admin Reply)...") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        if (superAdminReply.isNotEmpty()) {
                            val msg = ChatMessage(
                                senderId = "super_admin_maher",
                                senderName = "الأدمن الرئيسي ماهر",
                                receiverId = selectedChatRoom ?: "",
                                receiverName = "العميل",
                                text = superAdminReply,
                                timestamp = System.currentTimeMillis()
                            )
                            viewModel.sendChatMessage(msg)
                            superAdminReply = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "ارسل كمشرف", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

