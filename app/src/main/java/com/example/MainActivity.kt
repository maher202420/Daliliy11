package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.DaliliTheme
import com.example.ui.DaliliViewModel
import com.example.ui.parseColorSafe
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.*

class MainActivity : ComponentActivity() {
    private val viewModel: DaliliViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by viewModel.appSettings.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            var isAr by remember { mutableStateOf(true) }

            DaliliTheme(settings = settings) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (settings.maintenanceMode && currentScreen != "login" && viewModel.currentRole.value == "Guest") {
                        MaintenanceScreen(settings, isAr) {
                            viewModel.navigateTo("login")
                        }
                    } else {
                        MainAppContent(
                            viewModel = viewModel,
                            settings = settings,
                            currentScreen = currentScreen,
                            isAr = isAr,
                            onLanguageToggle = { isAr = !isAr }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceScreen(settings: AppSettings, isAr: Boolean, onSecretClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = "Maintenance",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(72.dp)
                        .clickable { onSecretClick() }
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = if (isAr) "عذراً، التطبيق في وضع الصيانة" else "Application in Maintenance Mode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = settings.welcomeMessage,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    viewModel: DaliliViewModel,
    settings: AppSettings,
    currentScreen: String,
    isAr: Boolean,
    onLanguageToggle: () -> Unit
) {
    val context = LocalContext.current
    var backdoorTaps by remember { mutableStateOf(0) }
    var showBackdoorDialog by remember { mutableStateOf(false) }
    var backdoorPasswordInput by remember { mutableStateOf("") }
    val currentRole by viewModel.currentRole.collectAsState()

    val t = remember(isAr) {
        mapOf(
            "home" to if (isAr) "الرئيسية" else "Home",
            "login" to if (isAr) "تسجيل الدخول" else "Login",
            "register" to if (isAr) "انضم إلينا" else "Join Us",
            "search_placeholder" to if (isAr) "ابحث عن مزودي الخدمة بالاسم أو الهاتف..." else "Search providers by name or phone...",
            "categories" to if (isAr) "الأقسام والخدمات الرئيسية" else "Categories & Services",
            "recommended" to if (isAr) "مقدمو خدمات موصى بهم ⭐" else "Recommended Providers ⭐",
            "no_recommended" to if (isAr) "لا توجد ترقيات مميزة نشطة حالياً." else "No featured ads active.",
            "pinned" to if (isAr) "مثبّت" else "Pinned",
            "contact_support" to if (isAr) "تواصل مع الدعم الفني" else "Contact Support",
            "admin_panel" to if (isAr) "لوحة التحكم بالإدارة" else "Admin Dashboard",
            "username" to if (isAr) "اسم المستخدم" else "Username",
            "password" to if (isAr) "كلمة المرور" else "Password",
            "login_btn" to if (isAr) "دخول آمن للمشرفين" else "Admin Secure Login",
            "invalid_login" to if (isAr) "بيانات الدخول المدخلة غير صحيحة!" else "Incorrect credentials!",
            "triple_name" to if (isAr) "الاسم الثلاثي الكامل" else "Full Triple Name",
            "phone" to if (isAr) "رقم الهاتف الفعال" else "Phone Number",
            "select_cat" to if (isAr) "اختر القسم والخدمة المطلوبة" else "Select Category / Service",
            "work_address" to if (isAr) "العنوان التفصيلي ومكان العمل" else "Office Work Address",
            "district" to if (isAr) "المنطقة / المحافظة" else "District / Governorate",
            "gps" to if (isAr) "موقع الخريطة GPS (أرقام)" else "GPS Decimal Coordinates",
            "personal_photo" to if (isAr) "رابط الصورة الشخصية" else "Personal Image Photo",
            "id_card" to if (isAr) "رابط وثيقة الهوية (اختياري)" else "ID Document Image",
            "submit_req" to if (isAr) "تقديم طلب تسجيل للانضمام" else "Configure Registration Request",
            "use_demo_images" to if (isAr) "استيراد صور تجريبية تلقائية" else "Impose Demo Images",
            "req_success" to if (isAr) "تم إرسال طلب الانضمام، وهو قيد المراجعة الفورية!" else "Your application was transmitted for quick checkout!",
            "fill_required" to if (isAr) "فضلاً تعبئة جميع الحقول المطلوبة!" else "Please fill up required fields!",
            "admin_logged" to if (isAr) "مرحباً بالمدير العام!" else "Logged as Owner General!",
            "super_logged" to if (isAr) "مرحباً يا مشرف!" else "Logged as Supervisor!",
            "logout" to if (isAr) "خروج" else "Exit",
            "tabs_requests" to if (isAr) "الطلبات" else "Requests",
            "tabs_cats" to if (isAr) "الأقسام" else "Categories",
            "tabs_direct_add" to if (isAr) "إضافة مرشح" else "Direct Add",
            "tabs_manage_prov" to if (isAr) "الموفرين" else "Providers",
            "tabs_sups" to if (isAr) "المشرفين" else "Supervisors",
            "backdoor_dialog_title" to if (isAr) "بوابة المالك الفورية" else "Direct Owner Gateway",
            "backdoor_dialog_desc" to if (isAr) "أدخل الرمز السري الفوري للمالك" else "Key-in instant owner credentials",
            "backdoor_enter" to if (isAr) "مصادقة" else "Authenticate",
            "no_results" to if (isAr) "لا توجد نتائج مطابقة لبحثك!" else "No results found!"
        )
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.clickable {
                            backdoorTaps++
                            if (backdoorTaps >= 5) {
                                backdoorTaps = 0
                                showBackdoorDialog = true
                            }
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = "Logo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = settings.appName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAr) "شبكة ومزاحم الخدمات الفوري" else "Interactive Almanacs Tracker",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Top Bar Dynamic customizable tabs order
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val parsedConfigs = remember(settings.topBarConfig) {
                            settings.topBarConfig.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        }
                        parsedConfigs.forEach { iconKey ->
                            when (iconKey) {
                                "home" -> {
                                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                                        Icon(
                                            Icons.Default.Home, "Home",
                                            tint = if (currentScreen == "home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                "login" -> {
                                    IconButton(onClick = { viewModel.navigateTo("login") }) {
                                        Icon(
                                            Icons.Default.Lock, "Login",
                                            tint = if (currentScreen == "login" || currentScreen == "admin") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                "register" -> {
                                    IconButton(onClick = { viewModel.navigateTo("register") }) {
                                        Icon(
                                            Icons.Default.AddCircle, "Register",
                                            tint = if (currentScreen == "register") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }

                        // Static icons
                        IconButton(onClick = { viewModel.navigateTo("about") }) {
                            Icon(
                                Icons.Default.Info, "About",
                                tint = if (currentScreen == "about") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        IconButton(onClick = { onLanguageToggle() }) {
                            Text("🌐", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    "home" -> HomeScreen(viewModel, t, isAr)
                    "login" -> LoginScreen(viewModel, t, isAr)
                    "register" -> RegistrationScreen(viewModel, t, isAr)
                    "category" -> CategoryDetailsScreen(viewModel, t, isAr)
                    "detail" -> ProviderDetailsScreen(viewModel, t, isAr)
                    "admin" -> AdminPanelScreen(viewModel, t, isAr)
                    "secret" -> SecretSettingsScreen(viewModel, t, isAr)
                    "about" -> AboutScreen(viewModel, t, isAr)
                    "loyalty" -> LoyaltyScreen(viewModel, isAr)
                    else -> HomeScreen(viewModel, t, isAr)
                }
            }

            // Small Floating Smart Assistant Widget (Admin configured)
            if (settings.smartAssistantEnabled) {
                val sizeDp = when (settings.smartAssistantSize) {
                    "small" -> 44.dp
                    "large" -> 64.dp
                    else -> 54.dp
                }
                val alignModifier = if (settings.smartAssistantAlignLeft) Alignment.BottomStart else Alignment.BottomEnd
                val assistantColor = parseColorSafe(settings.smartAssistantColorHex, MaterialTheme.colorScheme.primary)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 72.dp, start = 16.dp, end = 16.dp),
                    contentAlignment = alignModifier
                ) {
                    Box(
                        modifier = Modifier
                            .size(sizeDp)
                            .clip(CircleShape)
                            .background(assistantColor)
                            .clickable {
                                viewModel.navigateTo("loyalty")
                                Toast.makeText(context, if (isAr) "توجيه لنظام الولاء والمساعد!" else "Navigating loyalty rewards!", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Star, "Assistant", tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("خدمات", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom sponsor footer
            if (settings.footerText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.clickable {
                            val num = settings.footerText.filter { it.isDigit() }
                            if (num.isNotBlank()) {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num")))
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, "Sponsor", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(settings.footerText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Backdoor Gateway Password Dialog
            if (showBackdoorDialog) {
                Dialog(onDismissRequest = { showBackdoorDialog = false }) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Settings, "Backdoor", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(t["backdoor_dialog_title"] ?: "Owner Panel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = backdoorPasswordInput,
                                onValueChange = { backdoorPasswordInput = it },
                                label = { Text(t["password"] ?: "Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        if (backdoorPasswordInput == "maher--736462") {
                                            showBackdoorDialog = false
                                            backdoorPasswordInput = ""
                                            viewModel.navigateTo("secret")
                                        } else {
                                            Toast.makeText(context, t["invalid_login"], Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(t["backdoor_enter"] ?: "Enter")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 1: HOME SCREEN
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val categories by viewModel.categories.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val cities by viewModel.cities.collectAsState()

    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    // Geographical filters
    var selectedCity by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("") }
    var searchPhoneOption by remember { mutableStateOf("") }
    var searchTripleName by remember { mutableStateOf("") }
    var showAdvancedFilters by remember { mutableStateOf(false) }

    // Map radius filter parameters
    var maxRadiusSlider by remember { mutableFloatStateOf(5.0f) }
    var userLat by remember { mutableDoubleStateOf(15.348) } // Sana'a default coordinates
    var userLng by remember { mutableDoubleStateOf(44.206) }

    // Speech Voice Input Handler Activity Results launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val spokenData = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spokenData.isNullOrEmpty()) {
                searchQuery = spokenData[0]
            }
        }
    )

    // Recommended providers sorting logic (Premium Subscription items show in top!)
    val sortedApprovedProviders = remember(providers, selectedCity, selectedDistrict, searchPhoneOption, searchTripleName, maxRadiusSlider, searchQuery, settings.dataSaverMode) {
        providers.filter { p ->
            p.status == "approved" &&
            (searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true) || p.phone.contains(searchQuery)) &&
            (selectedCity.isBlank() || p.workAddress.contains(selectedCity, ignoreCase = true)) &&
            (selectedDistrict.isBlank() || p.district.contains(selectedDistrict, ignoreCase = true)) &&
            (searchPhoneOption.isBlank() || p.phone.contains(searchPhoneOption)) &&
            (searchTripleName.isBlank() || p.name.contains(searchTripleName, ignoreCase = true)) &&
            // Map Circle Radius Search filter bounds using high efficiency Spherical coordinates
            calculateCoordinateDistance(userLat, userLng, p.gpsCoordinates) <= maxRadiusSlider
        }.sortedWith(compareBy({ !it.isPremium }, { !it.isRecommended }))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Welcome Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = settings.welcomeMessage, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        // Banners Carousel Ads
        if (banners.isNotEmpty()) {
            item {
                Text(
                    text = if (isAr) "العروض والخدمات الراعية" else "Sponsored Advertisements",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    items(banners) { ad ->
                        val sizeWidth = when (ad.sizeType) {
                            "small" -> 160.dp
                            "large" -> 280.dp
                            else -> 220.dp
                        }
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .width(sizeWidth)
                                .height(100.dp)
                        ) {
                            if (ad.bannerType == "text_alert") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(ad.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                }
                            } else {
                                AsyncImage(
                                    model = if (settings.dataSaverMode) "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=100" else ad.imageUrl,
                                    contentDescription = ad.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Voice query filter panel
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(t["search_placeholder"] ?: "Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Voice Mic search icon
                            IconButton(onClick = {
                                try {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, if (isAr) "تحدث الآن للبحث..." else "Speak context searching...")
                                    }
                                    speechLauncher.launch(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "تعذر تفعيل البحث الصوتي!", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("🎙️", fontSize = 18.sp)
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, "Clear")
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = { showAdvancedFilters = !showAdvancedFilters }) {
                    Icon(
                        Icons.Default.List, "Filters",
                        tint = if (showAdvancedFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Advanced filter parameters dropdowns
        if (showAdvancedFilters) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = if (isAr) "تصفية متطورة وموقع جغرافي" else "Advanced Filter Coordinates", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        
                        // Cities Dropdown selection
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Selected city
                            OutlinedTextField(
                                value = selectedCity,
                                onValueChange = { selectedCity = it },
                                label = { Text(if (isAr) "المدينة" else "City") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = selectedDistrict,
                                onValueChange = { selectedDistrict = it },
                                label = { Text(if (isAr) "الحي / المديرية" else "District") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = searchTripleName,
                                onValueChange = { searchTripleName = it },
                                label = { Text(if (isAr) "الاسم الثلاثي" else "Triple Name") },
                                modifier = Modifier.weight(1.2f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = searchPhoneOption,
                                onValueChange = { searchPhoneOption = it },
                                label = { Text(if (isAr) "الهاتف" else "Phone") },
                                modifier = Modifier.weight(0.8f),
                                singleLine = true
                            )
                        }

                        // Coordinates radius sliders bounds
                        Column {
                            Text(
                                text = if (isAr) "أقصى نطاق بحث: ${maxRadiusSlider.toInt()} كم" else "Search Radius: ${maxRadiusSlider.toInt()} km",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = maxRadiusSlider,
                                onValueChange = { maxRadiusSlider = it },
                                valueRange = 1.0f..(settings.maxRadiusDefault.toFloat().coerceAtLeast(10f))
                            )
                        }

                        // Reset filters
                        TextButton(
                            onClick = {
                                selectedCity = ""
                                selectedDistrict = ""
                                searchPhoneOption = ""
                                searchTripleName = ""
                                maxRadiusSlider = settings.maxRadiusDefault.toFloat()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(if (isAr) "إعادة التعيين" else "Reset Choices")
                        }
                    }
                }
            }
        }

        // Output listings List
        if (sortedApprovedProviders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(t["no_results"] ?: "No results matched", fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            }
        } else {
            item {
                Text(
                    text = if (isAr) "مقدمو الخدمات النشطين" else "Active Registered Handlers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            items(sortedApprovedProviders) { p ->
                ProviderItemCard(p, categories, isAr) {
                    viewModel.navigateToProvider(p.id)
                }
            }
        }

        // Categories Grid Card lists
        item {
            Text(text = t["categories"] ?: "Categories", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(vertical = 10.dp))
        }

        if (categories.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            }
        } else {
            val rows = categories.chunked(2)
            items(rows) { chunk ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (cat in chunk) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clickable { viewModel.navigateToCategory(cat.id) }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = if (settings.dataSaverMode) "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=100" else cat.imageUrl,
                                    contentDescription = cat.nameAr,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Text(
                                        text = if (isAr) cat.nameAr else cat.nameEn,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    if (chunk.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// Spherical coordinates calculation
fun calculateCoordinateDistance(lat1: Double, lng1: Double, targetGps: String): Double {
    try {
        val parts = targetGps.split(",")
        if (parts.size < 2) return 0.0
        val lat2 = parts[0].trim().toDoubleOrNull() ?: return 0.0
        val lng2 = parts[1].trim().toDoubleOrNull() ?: return 0.0
        
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    } catch (e: Exception) {
        return 0.0
    }
}

@Composable
fun ProviderItemCard(p: Provider, categories: List<Category>, isAr: Boolean, onClick: () -> Unit) {
    val catName = categories.find { it.id == p.categoryId }?.let { if (isAr) it.nameAr else it.nameEn } ?: ""
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = p.personalPhotoUrl,
                contentDescription = p.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (p.isPremium) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "مميز ⭐" else "Premium ⭐",
                            color = Color(0xFFFFD700),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.Black, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(text = "$catName • ${p.district}", fontSize = 11.sp, color = Color.Gray)
                Text(text = p.phone, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Default.ArrowForward, contentDescription = "Open")
        }
    }
}

// -------------------------------------------------------------
// SCREEN 2: LOGIN SCREEN (with 2FA checkboxes options)
// -------------------------------------------------------------
@Composable
fun LoginScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var supervisor2FAEnabled by remember { mutableStateOf(false) }
    var tfaCodeInput by remember { mutableStateOf("") }
    var rememberMeCheckbox by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.padding(24.dp), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Lock, "Login Banner", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(54.dp))
                Text(text = t["login_btn"] ?: "Admin Login", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(t["username"] ?: "Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(t["password"] ?: "Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Optional 2-Factor check for safety
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(checked = supervisor2FAEnabled, onCheckedChange = { supervisor2FAEnabled = it })
                    Text(text = if (isAr) "اختياري: تفعيل التحقق بخطوتين (2FA)" else "Dual Factor Verification (2FA)", fontSize = 12.sp)
                }

                if (supervisor2FAEnabled) {
                    OutlinedTextField(
                        value = tfaCodeInput,
                        onValueChange = { tfaCodeInput = it },
                        label = { Text(if (isAr) "أدخل رمز التحقق (6-أرقام)" else "Enter Code PIN (6-digit)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(checked = rememberMeCheckbox, onCheckedChange = { rememberMeCheckbox = it })
                    Text(text = if (isAr) "تذكر تسجيل الدخول" else "Remember me", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (supervisor2FAEnabled && tfaCodeInput.length < 4) {
                            Toast.makeText(context, if (isAr) "الرجاء كتاية رمز المصادقة الفعال!" else "Specify valid authenticate security pin!", Toast.LENGTH_SHORT).show()
                        } else {
                            val success = viewModel.login(username, password)
                            if (success) {
                                Toast.makeText(context, if (isAr) "تم الدخول والمصادقة بنجاح!" else "Security clearance authorized!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, t["invalid_login"], Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t["login_btn"] ?: "Proceed")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 3: REGISTRATION SCREEN (Supports Nominatim Address Lookup Fallback)
// -------------------------------------------------------------
@Composable
fun RegistrationScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val categories by viewModel.categories.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var workAddress by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var gpsCoordinates by remember { mutableStateOf("15.348,44.206") }
    var personalPhoto by remember { mutableStateOf("") }
    var idCardPhoto by remember { mutableStateOf("") }

    // Dropdown suggestions list from openstreetmap nominatim
    var osmSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(text = t["submit_req"] ?: "Join professional", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        item {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(t["triple_name"] ?: "Name") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(t["phone"] ?: "Phone") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(
                value = workAddress,
                onValueChange = {
                    workAddress = it
                    // Trigger dynamic OSM Nominatim autocomplete address lookup
                    if (it.length > 3) {
                        java.lang.Thread {
                            try {
                                val url = URL("https://nominatim.openstreetmap.org/search?q=${java.net.URLEncoder.encode(it, "UTF-8")}&format=json&limit=3")
                                val conn = url.openConnection() as HttpURLConnection
                                conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                                val responseText = conn.inputStream.bufferedReader().use { r -> r.readText() }
                                val regex = "\"display_name\":\"([^\"]+)\"".toRegex()
                                val matches = regex.findAll(responseText).map { m -> m.groupValues[1] }.toList()
                                osmSuggestions = matches
                            } catch (e: Exception) { e.printStackTrace() }
                        }.start()
                    }
                },
                label = { Text(t["work_address"] ?: "Work Address") },
                modifier = Modifier.fillMaxWidth()
            )

            if (osmSuggestions.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Column {
                        osmSuggestions.forEach { choice ->
                            Text(
                                text = choice,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        workAddress = choice
                                        osmSuggestions = emptyList()
                                        // Randomize offset coordinates near center of selection
                                        gpsCoordinates = "15.${(300..400).random()},44.${(100..300).random()}"
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        item {
            OutlinedTextField(value = district, onValueChange = { district = it }, label = { Text(t["district"] ?: "District") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = gpsCoordinates, onValueChange = { gpsCoordinates = it }, label = { Text(t["gps"] ?: "GPS Coordinates") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = personalPhoto, onValueChange = { personalPhoto = it }, label = { Text(t["personal_photo"] ?: "Photo url") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = idCardPhoto, onValueChange = { idCardPhoto = it }, label = { Text(t["id_card"] ?: "ID Card url") }, modifier = Modifier.fillMaxWidth())
        }

        item {
            Button(
                onClick = {
                    personalPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300"
                    idCardPhoto = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(t["use_demo_images"] ?: "Auto Dem")
            }
        }

        item {
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() || workAddress.isBlank() || district.isBlank()) {
                        Toast.makeText(context, t["fill_required"], Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.submitProfessionalRequest(name, phone, categoryId, workAddress, district, gpsCoordinates, personalPhoto, idCardPhoto)
                        Toast.makeText(context, t["req_success"], Toast.LENGTH_LONG).show()
                        viewModel.navigateTo("home")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(t["submit_req"] ?: "Register Now")
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 4: CATEGORY DETAILS LIST SCREEN
// -------------------------------------------------------------
@Composable
fun CategoryDetailsScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val categoryId by viewModel.selectedCategoryId.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val providers by viewModel.providers.collectAsState()

    val category = remember(categoryId, categories) { categories.find { it.id == categoryId } }
    val matchingProviders = remember(categoryId, providers) { providers.filter { it.categoryId == categoryId && it.status == "approved" } }

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo("home") }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text(
                text = category?.let { if (isAr) it.nameAr else it.nameEn } ?: "Category",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (matchingProviders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(t["no_results"] ?: "No providers mapped.", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(matchingProviders) { p ->
                    ProviderItemCard(p, categories, isAr) {
                        viewModel.navigateToProvider(p.id)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 5: PROVIDER DETAILS SCREEN (Reviews, Premium receipts upload & Complaints)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailsScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val providerId by viewModel.selectedProviderId.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val categoryList by viewModel.categories.collectAsState()
    val reviewsList by viewModel.reviews.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    val p = remember(providerId, providers) { providers.find { it.id == providerId } }
    val pReviews = remember(providerId, reviewsList) { reviewsList.filter { it.providerId == providerId } }

    val context = LocalContext.current
    var showAbuseReportDialog by remember { mutableStateOf(false) }
    var reporterName by remember { mutableStateOf("") }
    var reporterPhone by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("") }

    // Premium membership verify receipts
    var showReceiptSubmissionDialog by remember { mutableStateOf(false) }
    var receiptNotesInp by remember { mutableStateOf("") }

    // Review entry specs
    var rName by remember { mutableStateOf("") }
    var rComment by remember { mutableStateOf("") }
    var rRating by remember { mutableFloatStateOf(5.0f) }

    if (p == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("الموفر غير متوفر حالياً!", fontWeight = FontWeight.Bold)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("home") }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Text(p.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (p.isPremium) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مميز ⭐", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = p.personalPhotoUrl,
                    contentDescription = p.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(90.dp).clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = "العنوان: ${p.workAddress}", fontSize = 12.sp)
                    Text(text = "المديرية: ${p.district}", fontSize = 12.sp)
                    Text(text = "الهاتف: ${p.phone}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "التقييم: ${p.rating} ★ (${p.reviewCount} تقييم)", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // WhatsApp direct triggers
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${p.phone}")))
                        } catch (e: Exception) { e.printStackTrace() }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Phone, "Call")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAr) "اتصال هاتف" else "Call Now")
                }

                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${p.phone}"))
                            context.startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Send, "WhatsApp")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("واتساب")
                }
            }
        }

        // Premium Badge membership uploads config
        if (settings.isSubscriptionEnabled && !p.isPremium) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isAr) "أظهر كعضو متميز في أعلى نتائج البحث المباشرة" else "Appear on top as featured provider!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(onClick = { showReceiptSubmissionDialog = true }) {
                            Text(if (isAr) "شراء شارة عضو متميز" else "Order Premium Badge Now")
                        }
                    }
                }
            }
        }

        // Complaint Abuse panel
        item {
            OutlinedButton(
                onClick = { showAbuseReportDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Warning, "Abuse report", tint = Color.Red)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isAr) "الإبلاغ عن مقدم الخدمة" else "Report Abuses / Complaints")
            }
        }

        // Reviews mapping
        item {
            Text(text = t["reviews_title"] ?: "Reviews List", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        if (pReviews.isEmpty()) {
            item {
                Text(text = if (isAr) "لا توجد تقييمات مكتوبة لهذا الموثوق حتى الآن." else "No reviews recorded.", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            items(pReviews) { rev ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(rev.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${rev.rating} ★", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(rev.comment, fontSize = 12.sp)
                    }
                }
            }
        }

        // New review entry
        item {
            Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp) , verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = t["add_review"] ?: "Leave a review", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = rName, onValueChange = { rName = it }, label = { Text(t["review_user"] ?: "User") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rComment, onValueChange = { rComment = it }, label = { Text(t["review_comment"] ?: "Comment") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("التقييم: ", fontSize = 12.sp)
                        Slider(value = rRating, onValueChange = { rRating = it }, valueRange = 1f..5f, steps = 3, modifier = Modifier.weight(1f))
                        Text("${rRating.toInt()} ★", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            if (rName.isBlank() || rComment.isBlank()) {
                                Toast.makeText(context, t["fill_required"], Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.addReview(p.id, rName, rRating, rComment)
                                Toast.makeText(context, if (isAr) "شكرًا لتقييمك! تم إضافتها فوراً!" else "Review synced!", Toast.LENGTH_SHORT).show()
                                rName = ""
                                rComment = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(t["review_submit"] ?: "Submit Rating")
                    }
                }
            }
        }
    }

    // Abuse report Dialog
    if (showAbuseReportDialog) {
        Dialog(onDismissRequest = { showAbuseReportDialog = false }) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (isAr) "تسجيل بلاغ شكوى / إساءة" else "Log Inappropriate Violation", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = reporterName, onValueChange = { reporterName = it }, label = { Text(if (isAr) "اسمك الرباعي" else "Reporter Full Name") })
                    OutlinedTextField(value = reporterPhone, onValueChange = { reporterPhone = it }, label = { Text(if (isAr) "رقم الهاتف للتثبت" else "Security phone check") })
                    OutlinedTextField(value = reportReason, onValueChange = { reportReason = it }, label = { Text(if (isAr) "تفاصيل البلاغ والشكوى" else "What was inappropriate?") })
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                viewModel.reportProvider(p.id, p.name, reporterName, reporterPhone, reportReason)
                                Toast.makeText(context, if (isAr) "تم تسجيل البلاغ ومزامنتها مع المشرفين بنجاح!" else "Abuse recorded in supervisor files!", Toast.LENGTH_SHORT).show()
                                showAbuseReportDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isAr) "إيداع البلاغ" else "Commit Report")
                        }
                    }
                }
            }
        }
    }

    // Premium Membership Verify Dialog
    if (showReceiptSubmissionDialog) {
        Dialog(onDismissRequest = { showReceiptSubmissionDialog = false }) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = if (isAr) "تأكيد سداد قسط الاشتراك" else "Submit Monthly Verification Receipts", fontWeight = FontWeight.Bold)
                    Text(text = if (isAr) "يرجى تحويل الاشتراك لحساب الدعم (777644670) وإيداع الملاحظات والايصال تالياً" else "Transfer premium fees to general accounts and file note references.", fontSize = 11.sp, color = Color.Gray)
                    OutlinedTextField(value = receiptNotesInp, onValueChange = { receiptNotesInp = it }, label = { Text(if (isAr) "تفاصيل الحوالة أو الإشعارات" else "Receipt reference info") })
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                viewModel.submitSubscriptionPayment(p.id, p.name, "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=300", receiptNotesInp)
                                Toast.makeText(context, if (isAr) "تم إرسال الايصال للمدير للمصادقة الفورية!" else "Receipt files posted to live checking queue!", Toast.LENGTH_SHORT).show()
                                showReceiptSubmissionDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isAr) "تقديم الدفع" else "Post Payment")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 6: ADMIN PANEL SCREEN
// -------------------------------------------------------------
@Composable
fun AdminPanelScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val role by viewModel.currentRole.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val pendingProviders by viewModel.pendingProviders.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val complaints by viewModel.complaints.collectAsState()
    val subscriptionPayments by viewModel.subscriptionPayments.collectAsState()
    val cities by viewModel.cities.collectAsState()

    val context = LocalContext.current

    if (role == "Guest") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { viewModel.navigateTo("login") }) {
                Text(t["login"] ?: "Login First")
            }
        }
    } else {
        var activeTab by remember { mutableIntStateOf(0) }
        val tabs = listOf(
            if (isAr) "الطلبات" else "Requests",
            if (isAr) "الشكاوى" else "Complaints",
            if (isAr) "الاشتراكات" else "Subscriptions",
            if (isAr) "المدن والقرى" else "Cities",
            if (isAr) "البيانات والنسخ" else "Backup Utilities"
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp)
            ) {
                Text(text = if (isAr) "لوحة إدارة دليلي" else "Dalili Admin", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = { viewModel.logout() }) {
                    Text(t["logout"] ?: "Exit")
                }
            }

            ScrollableTabRow(selectedTabIndex = activeTab) {
                tabs.forEachIndexed { idx, title ->
                    Tab(selected = activeTab == idx, onClick = { activeTab = idx }, text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                when (activeTab) {
                    0 -> { // Registration pending reviews
                        if (pendingProviders.isEmpty()) {
                            Text(if (isAr) "لا توجد طلبات انضمام عالقة حالياً." else "No pending applications.")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(pendingProviders) { req ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(req.name, fontWeight = FontWeight.Bold)
                                            Text(req.workAddress)
                                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Button(onClick = { viewModel.acceptPendingRequest(req.id) }) { Text("قبول") }
                                                Button(onClick = { viewModel.rejectPendingRequest(req.id, "إداري") }) { Text("استبعاد") }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> { // Abuse reports review
                        if (complaints.isEmpty()) {
                            Text(if (isAr) "السجل فارغ من الشكاوى والبلاغات" else "Empty complaints logbook.")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(complaints) { c ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("بلاغ ضد: ${c.providerName}", fontWeight = FontWeight.Bold)
                                            Text("المبلغ: ${c.userName} (${c.userPhone})")
                                            Text("السبب: ${c.reason}", color = Color.Red)
                                            if (c.status == "pending") {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Button(onClick = { viewModel.resolveComplaint(c.id) }) { Text("حل الإشكال") }
                                                    Button(onClick = { viewModel.dismissComplaint(c.id) }) { Text("تجاهل") }
                                                }
                                            } else {
                                                Text("الحالة: ${c.status}", color = Color.Gray, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Premium payment receipt validations
                        if (subscriptionPayments.isEmpty()) {
                            Text(if (isAr) "لا توجد ايصالات دفع قيد المراجعة." else "No subscription review logs.")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(subscriptionPayments) { sp ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("طلب ترقية من: ${sp.providerName}", fontWeight = FontWeight.Bold)
                                            Text("التفاصيل المرفقة: ${sp.notes}")
                                            if (sp.status == "pending") {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Button(onClick = { viewModel.approveSubscriptionPayment(sp.id, sp.providerId) }) { Text("تفعيل العضوية") }
                                                    Button(onClick = { viewModel.rejectSubscriptionPayment(sp.id) }) { Text("رفض الإيصال") }
                                                }
                                            } else {
                                                Text("التشخيص: ${sp.status}", fontWeight = FontWeight.Bold, color = Color.Gray)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> { // Dynamic custom cities
                        var newCityAr by remember { mutableStateOf("") }
                        var newCityEn by remember { mutableStateOf("") }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = newCityAr, onValueChange = { newCityAr = it }, label = { Text("الاسم عربي") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = newCityEn, onValueChange = { newCityEn = it }, label = { Text("الاسم إنجليزي") }, modifier = Modifier.weight(1f))
                            }
                            Button(
                                onClick = {
                                    if (newCityAr.isNotBlank()) {
                                        viewModel.addCity(newCityAr, newCityEn)
                                        newCityAr = ""
                                        newCityEn = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("إدراج مدينة جديدة") }

                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(cities) { city ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${city.nameAr} (${city.nameEn})")
                                        IconButton(onClick = { viewModel.deleteCity(city.id) }) {
                                            Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    4 -> { // Database backup/recover and temporary cleaning logs
                        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            Button(
                                onClick = {
                                    viewModel.fetchBackupJSON { data ->
                                        try {
                                            val dir = context.getExternalFilesDir(null)
                                            val file = File(dir, "dalili_backup_${System.currentTimeMillis()}.json")
                                            file.writeText(data)
                                            Toast.makeText(context, "تم حفظ نسخة احتياطية في: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "فشل حفظ الملف: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("نسخ احتياطي فوري لقاعدة البيانات (JSON)")
                            }

                            Button(
                                onClick = {
                                    viewModel.restoreBackupFromJSON("") {
                                        Toast.makeText(context, "تم استرجاع وفرد قاعدة بيانات دليلي الافتراضية!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("استعادة البيانات والعودة للنسخ")
                            }

                            Button(
                                onClick = {
                                    viewModel.runCacheAutoClean()
                                    Toast.makeText(context, "تم مسح البيانات القديمة وغير الضرورية وتصفير الذاكرة التخزينية لجهاز الخادم!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                Text("تفعيل الجدولة الذكية (مسح الذاكرة المؤقتة)")
                            }

                            // Dynamic weekly activity report generator
                            Button(
                                onClick = {
                                    val reportStr = "--- تقرير دليلي لإحصائيات الأداء الأسبوعي ---\nالمقاولين المزكّين: ${providers.size}\nالطلبات العالقة: ${pendingProviders.size}\nالبلاغات النشطة: ${complaints.size}"
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, reportStr)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "مشاركة تقرير الأداء"))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                            ) {
                                Text("توليد ومشاركة التقرير الأسبوعي")
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 7: SECRET BACKDOOR SETTINGS SCREEN
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretSettingsScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val settings by viewModel.appSettings.collectAsState()
    val context = LocalContext.current

    var appName by remember { mutableStateOf(settings.appName) }
    var primaryColorHex by remember { mutableStateOf(settings.primaryColorHex) }
    var secondaryColorHex by remember { mutableStateOf(settings.secondaryColorHex) }
    var welcomeMessage by remember { mutableStateOf(settings.welcomeMessage) }
    var footerText by remember { mutableStateOf(settings.footerText) }
    var supportNumber by remember { mutableStateOf(settings.supportNumber) }
    var supportEmail by remember { mutableStateOf(settings.supportEmail) }
    var supportWhatsapp by remember { mutableStateOf(settings.supportWhatsapp) }
    var adminPasswordHex by remember { mutableStateOf(settings.adminPasswordHex) }

    // Dynamic Presets selector
    var themePreset by remember { mutableStateOf(settings.themePreset) }
    var backgroundColorHex by remember { mutableStateOf(settings.backgroundColorHex) }
    var textColorPreset by remember { mutableStateOf(settings.textColorPreset) }
    var textColorHex by remember { mutableStateOf(settings.textColorHex) }

    // Float assistant sizes
    var assistantSize by remember { mutableStateOf(settings.smartAssistantSize) }
    var assistantColorHex by remember { mutableStateOf(settings.smartAssistantColorHex) }
    var assistantAlignLeft by remember { mutableStateOf(settings.smartAssistantAlignLeft) }
    var assistantEnabled by remember { mutableStateOf(settings.smartAssistantEnabled) }

    // Check sliders
    var maintenanceMode by remember { mutableStateOf(settings.maintenanceMode) }
    var dataSaverMode by remember { mutableStateOf(settings.dataSaverMode) }
    var maxRadiusVal by remember { mutableIntStateOf(settings.maxRadiusDefault) }

    // FCM channel controls
    var fcmJoinRequests by remember { mutableStateOf(settings.fcmJoinRequests) }
    var fcmComplaints by remember { mutableStateOf(settings.fcmComplaints) }

    // Loyalty configuration
    var pointsPerReviewInp by remember { mutableIntStateOf(settings.pointsPerReview) }
    var pointsPerShareInp by remember { mutableIntStateOf(settings.pointsPerShare) }
    var subEnabled by remember { mutableStateOf(settings.isSubscriptionEnabled) }
    var topBarConfigInp by remember { mutableStateOf(settings.topBarConfig) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("home") }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Text("إعدادات التحكم المتقدمة الفورية للمالك", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        item {
            OutlinedTextField(value = appName, onValueChange = { appName = it }, label = { Text("اسم التطبيق") }, modifier = Modifier.fillMaxWidth())
        }

        // Color premium presets selector interactive!
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("اختر الهوية والسمة البصرية الجاهزة:", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(onClick = { themePreset = "cosmic_slate" }, colors = ButtonDefaults.buttonColors(containerColor = if (themePreset == "cosmic_slate") MaterialTheme.colorScheme.primary else Color.Gray)) {
                            Text("كوزميك فضّي", fontSize = 10.sp)
                        }
                        Button(onClick = { themePreset = "charcoal_gold" }, colors = ButtonDefaults.buttonColors(containerColor = if (themePreset == "charcoal_gold") MaterialTheme.colorScheme.primary else Color.Gray)) {
                            Text("ذهبي فاحم", fontSize = 10.sp)
                        }
                        Button(onClick = { themePreset = "royal_emerald" }, colors = ButtonDefaults.buttonColors(containerColor = if (themePreset == "royal_emerald") MaterialTheme.colorScheme.primary else Color.Gray)) {
                            Text("زمردي ملكي", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Custom font text color selectors
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("لون خطوط نصوص التطبيق والكتابة بالحقول:", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(onClick = { textColorPreset = "bright_white" }, colors = ButtonDefaults.buttonColors(containerColor = if (textColorPreset == "bright_white") MaterialTheme.colorScheme.primary else Color.Gray)) {
                            Text("أبيض ناصع", fontSize = 10.sp)
                        }
                        Button(onClick = { textColorPreset = "light_gold" }, colors = ButtonDefaults.buttonColors(containerColor = if (textColorPreset == "light_gold") MaterialTheme.colorScheme.primary else Color.Gray)) {
                            Text("ذهبي فاتح", fontSize = 10.sp)
                        }
                        Button(onClick = { textColorPreset = "vibrant_silver" }, colors = ButtonDefaults.buttonColors(containerColor = if (textColorPreset == "vibrant_silver") MaterialTheme.colorScheme.primary else Color.Gray)) {
                            Text("فضي متوهج", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Controls size, shape colors of floating smart assistant
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("تهيئة المساعد الذكي العائم وعلاقته:", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(onClick = { assistantSize = "small" }) { Text("صغير") }
                        Button(onClick = { assistantSize = "medium" }) { Text("متوسط") }
                        Button(onClick = { assistantSize = "large" }) { Text("كبير") }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = assistantAlignLeft, onCheckedChange = { assistantAlignLeft = it })
                        Text("محاذاة لليسار بالأسفل (Left alignment)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = assistantEnabled, onCheckedChange = { assistantEnabled = it })
                        Text("عرض وتفعيل الزر العائم للتطبيق مع خدمات")
                    }
                }
            }
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

        // FCM channels controls
        item {
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
        }

        // Multipliers loyalty
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pointsPerReviewInp.toString(), onValueChange = { pointsPerReviewInp = it.toIntOrNull() ?: 10 }, label = { Text("نقاط التقييم") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pointsPerShareInp.toString(), onValueChange = { pointsPerShareInp = it.toIntOrNull() ?: 20 }, label = { Text("نقاط المشاركة") }, modifier = Modifier.weight(1f))
            }
        }

        item {
            OutlinedTextField(value = supportNumber, onValueChange = { supportNumber = it }, label = { Text("رقم هاتف الدعم الفني") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = supportEmail, onValueChange = { supportEmail = it }, label = { Text("بريد الدعم الفني") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = supportWhatsapp, onValueChange = { supportWhatsapp = it }, label = { Text("رقم واتساب الدعم الفني") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = footerText, onValueChange = { footerText = it }, label = { Text("تذييل الصفحة (الوسط)") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = adminPasswordHex, onValueChange = { adminPasswordHex = it }, label = { Text("رمز المرور للمدير") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = topBarConfigInp, onValueChange = { topBarConfigInp = it }, label = { Text("ترتيب أيقونات الشريط العلوي") }, modifier = Modifier.fillMaxWidth())
        }

        item {
            Button(
                onClick = {
                    viewModel.updateSecretSettings(
                        appName = appName,
                        primaryHex = primaryColorHex,
                        secondaryHex = secondaryColorHex,
                        footerText = footerText,
                        welcomeMsg = welcomeMessage,
                        supportNum = supportNumber,
                        supportEmail = supportEmail,
                        supportWhatsapp = supportWhatsapp,
                        adminPass = adminPasswordHex,
                        themePreset = themePreset,
                        backgroundColorHex = backgroundColorHex,
                        textColorPreset = textColorPreset,
                        textColorHex = textColorHex,
                        smartAssistantSize = assistantSize,
                        smartAssistantColorHex = assistantColorHex,
                        smartAssistantAlignLeft = assistantAlignLeft,
                        smartAssistantEnabled = assistantEnabled,
                        maintenanceMode = maintenanceMode,
                        dataSaverMode = dataSaverMode,
                        maxRadiusDefault = maxRadiusVal,
                        fcmJoinRequests = fcmJoinRequests,
                        fcmComplaints = fcmComplaints,
                        pointsPerReview = pointsPerReviewInp,
                        pointsPerShare = pointsPerShareInp,
                        isSubscriptionEnabled = subEnabled,
                        topBarConfig = topBarConfigInp
                    )
                    Toast.makeText(context, "تم حفظ الإعدادات، ومزامنتها بنجاح مع كافة الأجهزة في نفس الوقت!", Toast.LENGTH_LONG).show()
                    viewModel.navigateTo("home")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ وتعميم التغييرات")
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 8: ABOUT SCREEN (with Support contacts info)
// -------------------------------------------------------------
@Composable
fun AboutScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val settings by viewModel.appSettings.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo("home") }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text(if (isAr) "عن التطبيق والدعم" else "About the Almanac App", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = settings.appName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "رقم الدعم الفني المباشر للتطبيق: ${settings.supportNumber}", fontSize = 13.sp)
                Text(text = "إيميل الدعم الفني: ${settings.supportEmail}", fontSize = 13.sp)
                Text(text = "الدعم عبر الواتساب: ${settings.supportWhatsapp}", fontSize = 13.sp)
            }
        }

        Text(text = if (isAr) "خيارات التواصل المباشر السريعة:" else "Direct Supported Support Paths:", fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${settings.supportNumber}")))
                    } catch (e: Exception) { e.printStackTrace() }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Phone, "Call")
                Spacer(modifier = Modifier.width(6.dp))
                Text("اتصال دعم")
            }

            Button(
                onClick = {
                    try {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${settings.supportWhatsapp}"))
                        context.startActivity(i)
                    } catch (e: Exception) { e.printStackTrace() }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Send, "WhatsApp")
                Spacer(modifier = Modifier.width(6.dp))
                Text("واتساب الدعم")
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 9: USER LOYALTY BALANCE SCREEN
// -------------------------------------------------------------
@Composable
fun LoyaltyScreen(viewModel: DaliliViewModel, isAr: Boolean) {
    val settings by viewModel.appSettings.collectAsState()
    val accounts by viewModel.loyaltyAccounts.collectAsState()
    val context = LocalContext.current

    var userPhoneCheck by remember { mutableStateOf("777644670") }
    val myAccount = remember(accounts, userPhoneCheck) { accounts.find { it.phone == userPhoneCheck } }

    Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo("home") }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text(if (isAr) "المحفظة وبرنامج ولاء المستخدمين" else "Client Loyalty & Points Wallet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Text(text = if (isAr) "تسجيل الدخول برقم جوالك لمراجعة الرصيد:" else "Insert mobile line checking balances:", fontSize = 12.sp)
        OutlinedTextField(
            value = userPhoneCheck,
            onValueChange = { userPhoneCheck = it },
            label = { Text(if (isAr) "رقم الهاتف" else "Phone line") },
            modifier = Modifier.fillMaxWidth()
        )

        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (isAr) "إجمالي نقاط الولاء الخاصة بك" else "Your Loyalty Accumulated Balance", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${myAccount?.points ?: 0} نقطة", fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Share the app simulation to earn points instantly
            Button(
                onClick = {
                    viewModel.addLoyaltyPoints(userPhoneCheck, "مستخدم دليلي", settings.pointsPerShare, "مشاركة رابط التطبيق")
                    Toast.makeText(context, if (isAr) "رائع! تم تزويد محفظتك بإنتاجية ${settings.pointsPerShare} نقطة!" else "Shared! Points added!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1.2f)
            ) {
                Icon(Icons.Default.Share, "Share rewards")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isAr) "مشاركة التطبيق وتثبيت" else "Share App for Rewards")
            }

            Button(
                onClick = {
                    if ((myAccount?.points ?: 0) >= 50) {
                        viewModel.redeemDiscount(userPhoneCheck, 50, "خصم بنسبة 20% لدى مقدم خدمة")
                        Toast.makeText(context, if (isAr) "تهانينا! تم استبدال الكود وإصدار الخصم!" else "Claimed successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, if (isAr) "عذراً، يجب تجميع 50 نقطة للادخار واستحقاق الخصومات!" else "Accumulate minimum 50 points to checkout!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(0.8f)
            ) {
                Text(if (isAr) "استبدال الخصم" else "Redeem Promo")
            }
        }

        Text(text = if (isAr) "سجل المعاملات والعمليات المنتهية:" else "Loyalty Ledger Statement Logs:", fontWeight = FontWeight.Bold)
        if (myAccount == null || myAccount.historyLogs.isEmpty()) {
            Text(if (isAr) "لا توجد حركات سابقة" else "Ledger currently empty.", color = Color.Gray, fontSize = 12.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(myAccount.historyLogs) { statement ->
                    Text(statement, fontSize = 11.sp, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}
