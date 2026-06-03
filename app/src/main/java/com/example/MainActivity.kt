package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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

class MainActivity : ComponentActivity() {
    private val viewModel: DaliliViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by viewModel.appSettings.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            
            // RTL-aware Arabic language state
            var isAr by remember { mutableStateOf(true) }

            DaliliTheme(settings = settings) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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

    // Helper translation map for visual fields
    val t = remember(isAr) {
        mapOf(
            "home" to if (isAr) "الرئيسية" else "Home",
            "login" to if (isAr) "تسجيل الدخول" else "Login",
            "register" to if (isAr) "انضم إلينا" else "Join Us",
            "search_placeholder" to if (isAr) "ابحث عن مزودي الخدمة أو الأقسام..." else "Search providers or categories...",
            "categories" to if (isAr) "الأقسام والخدمات الرئيسية" else "Categories & Services",
            "recommended" to if (isAr) "مقدمو خدمات موصى بهم ⭐" else "Recommended Providers ⭐",
            "no_recommended" to if (isAr) "لا يوجد مقدمو خدمات تم رجميزهم للترقية حالياً." else "No featured providers currently.",
            "pinned" to if (isAr) "مثبّت" else "Pinned",
            "contact_support" to if (isAr) "تواصل مع الدعم الفني" else "Contact Support",
            "admin_panel" to if (isAr) "لوحة الإدارة والمشرفين" else "Admin & Supervisors Panel",
            "username" to if (isAr) "اسم المستخدم" else "Username",
            "password" to if (isAr) "كلمة المرور" else "Password",
            "login_btn" to if (isAr) "تسجيل دخول آمن" else "Secure Sign In",
            "invalid_login" to if (isAr) "البيانات المدخلة خاطئة!" else "Invalid credentials!",
            "triple_name" to if (isAr) "الاسم الثلاثي الكامل" else "Full Triple Name",
            "phone" to if (isAr) "رقم الهاتف الفعال / واتساب" else "Phone / WhatsApp",
            "select_cat" to if (isAr) "اختر القسم والخدمة الرئيسية" else "Select Category / Service",
            "work_address" to if (isAr) "مكان وعنوان مكتب العمل الحالي" else "Current Work Office Address",
            "district" to if (isAr) "منطقة الدائرة السكنية / المديرية" else "Residence District",
            "gps" to if (isAr) "إحداثيات وموقع الخريطة GPS (اختياري)" else "GPS Coordinates (Optional)",
            "personal_photo" to if (isAr) "رابط الصورة الشخصية (إجباري)" else "Personal Photo Link (Required)",
            "id_card" to if (isAr) "رابط صورة بطاقة الهوية (اختياري)" else "ID Card Photo Link (Optional)",
            "submit_req" to if (isAr) "تقديم طلب الانضمام للمراجعة الفورية" else "Submit Registration Request",
            "use_demo_images" to if (isAr) "تعبئة صور تجريبية تلقائية" else "Use Standard Demo Images",
            "req_success" to if (isAr) "تم تقديم طلبك بنجاح وسيتزامن فوراً مع لوحة المدير!" else "Your request has been submitted and is real-time synced!",
            "fill_required" to if (isAr) "الرجاء تعبئة جميع الحقول الإجبارية!" else "Please fill all required fields!",
            "admin_logged" to if (isAr) "مرحباً يا مدير!" else "Welcome Admin!",
            "super_logged" to if (isAr) "مرحباً يا مشرف!" else "Welcome Supervisor!",
            "logout" to if (isAr) "تسجيل الخروج" else "Logout",
            "tabs_requests" to if (isAr) "طلبات التسجيل" else "Registration Requests",
            "tabs_cats" to if (isAr) "إدارة الأقسام" else "Categories",
            "tabs_direct_add" to if (isAr) "إضافة يدوي" else "Direct Add",
            "tabs_manage_prov" to if (isAr) "إدارة مقدمي الخدمات" else "Manage Providers",
            "tabs_sups" to if (isAr) "إدارة المشرفين" else "Supervisors",
            "backdoor_dialog_title" to if (isAr) "بوابة المالك السرية" else "Owner Secret Gateway",
            "backdoor_dialog_desc" to if (isAr) "الرجاء إدخال كلمة المرور الخلفية للوصول للإعدادات الفورية" else "Enter backdoor password to access settings instantly",
            "backdoor_enter" to if (isAr) "دخول آمن" else "Authenticate",
            "subcategories" to if (isAr) "الأقسام الفرعية" else "Subcategories",
            "add_main_cat" to if (isAr) "إضافة قسم رئيسي" else "Add Main Category",
            "add_sub_cat" to if (isAr) "إضافة قسم فرعي" else "Add Subcategory",
            "ar_name" to if (isAr) "الاسم بالعربية" else "Arabic Name",
            "en_name" to if (isAr) "الاسم بالإنجليزية" else "English Name",
            "img_url" to if (isAr) "رابط الصورة" else "Image URL",
            "sort_order" to if (isAr) "الترتيب" else "Sort Order",
            "add_btn" to if (isAr) "إضافة" else "Add",
            "direct_desc" to if (isAr) "إضافة مقدم خدمة إلى الدليل مباشرة بدون مراجعة" else "Add provider directory-wide instantly without check",
            "pin_recom_desc" to if (isAr) "تثبيت وترشيح وتوصية مزودي الخدمات في الدليل" else "Pin & Recommend Directory Providers",
            "pin_header" to if (isAr) "تثبيت بالقسم" else "Pin In Category",
            "recom_header" to if (isAr) "توصية رئيسية" else "Recommend",
            "delete" to if (isAr) "حذف" else "Delete",
            "pending_details" to if (isAr) "تفاصيل طلب التسجيل" else "Registration Details",
            "accept" to if (isAr) "قبول واعتماد" else "Accept Provider",
            "reject" to if (isAr) "رفض واستبعاد" else "Reject Request",
            "reviews_title" to if (isAr) "التقييمات وآراء العملاء" else "Customer Reviews & Ratings",
            "add_review" to if (isAr) "كتابة تقييم جديد" else "Write a Review",
            "review_user" to if (isAr) "اسمك الكامل" else "Your Full Name",
            "review_comment" to if (isAr) "اكتب تجربتك هنا..." else "Describe your experience here...",
            "review_submit" to if (isAr) "إرسال التقييم فوراً" else "Submit Review",
            "secret_settings_title" to if (isAr) "لوحة التحكم السرية للمالك" else "Owner Secret Settings",
            "app_name_setting" to if (isAr) "اسم التطبيق الرئيسي" else "Main App Name",
            "primary_color" to if (isAr) "اللون الأساسي (Hex)" else "Primary Color (Hex)",
            "secondary_color" to if (isAr) "اللون الثنائي / الفرعي (Hex)" else "Secondary Color (Hex)",
            "welcome_message" to if (isAr) "رسالة ترحيب الشاشة الرئيسية" else "Home Screen Welcome Text",
            "footer_setting" to if (isAr) "نص التذييل الدعائي" else "Sponsor Footer Text",
            "sup_phone" to if (isAr) "رقم دعم العملاء" else "Support Phone",
            "sup_email" to if (isAr) "بريد الدعم الفني" else "Support Email",
            "admin_pwd" to if (isAr) "كلمة مرور المدير الجديدة" else "New Admin Password",
            "save_all" to if (isAr) "حفظ التغييرات وتعميمها" else "Persist & Propagate Settings",
            "no_results" to if (isAr) "لا توجد نتائج مطابقة لبحثك!" else "No matching results found!"
        )
    }

    Scaffold(
        topBar = {
            // Replaced custom real-time adaptive Top App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                // Determine layout direction dynamically for icons
                val contentModifier = Modifier.fillMaxWidth()
                Row(
                    modifier = contentModifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Logo and Title (Acts as Backdoor Trigger when clicked 5 times)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                backdoorTaps++
                                if (backdoorTaps >= 5) {
                                    backdoorTaps = 0
                                    showBackdoorDialog = true
                                }
                            }
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
                                text = if (isAr) "دليل الخدمات الفوري" else "Live Almanac",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Top Bar Navigational Icons Row (1. Home 2. Lock 3. UserReg 4. Globe 5. Refresh)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Icon 1: HOME (🏠)
                        IconButton(
                            onClick = {
                                viewModel.navigateTo("home")
                                backdoorTaps++
                                if (backdoorTaps >= 5) {
                                    backdoorTaps = 0
                                    showBackdoorDialog = true
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (currentScreen == "home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // Icon 2: LOGIN (🔐)
                        IconButton(
                            onClick = { viewModel.navigateTo("login") }
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Login Pane",
                                tint = if (currentScreen == "login" || currentScreen == "admin") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(23.dp)
                            )
                        }

                        // Icon 3: REGISTER (👤 ID Add)
                        IconButton(
                            onClick = { viewModel.navigateTo("register") }
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = "Register",
                                tint = if (currentScreen == "register") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Icon 4: GLOBE (🌐)
                        IconButton(
                            onClick = { onLanguageToggle() }
                        ) {
                            Text(
                                text = "🌐",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(2.dp)
                            )
                        }

                        // Icon 5: REFRESH (🔄)
                        IconButton(
                            onClick = {
                                viewModel.triggerRefresh()
                                Toast.makeText(context, if (isAr) "تم تحديث البيانات الحية بنجاح!" else "Live lists updated!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Sync",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Screen Navigation Switch Page Router
                Box(modifier = Modifier.weight(1f)) {
                    when (currentScreen) {
                        "home" -> HomeScreen(viewModel, t, isAr)
                        "login" -> LoginScreen(viewModel, t, isAr)
                        "register" -> RegistrationScreen(viewModel, t, isAr)
                        "category" -> CategoryDetailsScreen(viewModel, t, isAr)
                        "detail" -> ProviderDetailsScreen(viewModel, t, isAr)
                        "admin" -> AdminPanelScreen(viewModel, t, isAr)
                        "secret" -> SecretSettingsScreen(viewModel, t, isAr)
                        else -> HomeScreen(viewModel, t, isAr)
                    }
                }

                // Brand Interactive Customizable Sponsor Footer at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.clickable {
                            // Easily dial sponsor
                            val u = settings.footerText.filter { it.isDigit() }
                            if (u.isNotBlank()) {
                                try {
                                    val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$u"))
                                    context.startActivity(i)
                                } catch (e: Exception) {
                                    // Fallback
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Sponsor",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = settings.footerText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Dial Sponsor",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Hidden Owner Backdoor Access Dialog
            if (showBackdoorDialog) {
                Dialog(onDismissRequest = { showBackdoorDialog = false }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Secret Gateway",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = t["backdoor_dialog_title"] ?: "Secret Gateway",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = t["backdoor_dialog_desc"] ?: "Enter credentials",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = backdoorPasswordInput,
                                onValueChange = { backdoorPasswordInput = it },
                                label = { Text(t["password"] ?: "Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showBackdoorDialog = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (isAr) "إلغاء" else "Cancel")
                                }
                                Button(
                                    onClick = {
                                        if (backdoorPasswordInput == "maher--736462") {
                                            showBackdoorDialog = false
                                            backdoorPasswordInput = ""
                                            viewModel.navigateTo("secret")
                                            Toast.makeText(context, if (isAr) "مرحباً يا مالك التطبيق! تم الدخول بنجاح." else "Welcome Owner! Bypassed successfully.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, if (isAr) "رمز المرور خاطئ!" else "Incorrect PIN!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1.5f)
                                ) {
                                    Text(t["backdoor_enter"] ?: "Access")
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
@Composable
fun HomeScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val categories by viewModel.categories.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    // Filter providers that are recommended
    val recommendedProviders = remember(providers) {
        providers.filter { it.isRecommended && it.status == "approved" }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Welcoming Card Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = settings.welcomeMessage,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isAr) "نشط ومزامن" else "Live Ready",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Search Input Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                placeholder = { Text(t["search_placeholder"] ?: "Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Show Search Results if Search Query is active
        if (searchQuery.isNotBlank()) {
            val filtered = providers.filter {
                (it.name.contains(searchQuery, ignoreCase = true) ||
                        it.workAddress.contains(searchQuery, ignoreCase = true) ||
                        it.district.contains(searchQuery, ignoreCase = true)) &&
                        it.status == "approved"
            }

            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = t["no_results"] ?: "No results",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(filtered) { p ->
                    ProviderCompactCard(p, categories, isAr) {
                        viewModel.navigateToProvider(p.id)
                    }
                }
            }
        } else {
            // "Recommended Providers" Section Slider
            item {
                Text(
                    text = t["recommended"] ?: "Recommended Guides",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (recommendedProviders.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = t["no_recommended"] ?: "No recommended",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        items(recommendedProviders) { p ->
                            RecommendedProviderCard(p, isAr) {
                                viewModel.navigateToProvider(p.id)
                            }
                        }
                    }
                }
            }

            // Categories Header List Grid
            item {
                Text(
                    text = t["categories"] ?: "Categories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (categories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                    }
                }
            } else {
                item {
                    // Render custom grid logic to avoid multi-scroll nested crashes in LazyColumn
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val chunks = categories.chunked(2)
                        for (chunk in chunks) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (cat in chunk) {
                                    CategoryGridCard(
                                        category = cat,
                                        isAr = isAr,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        viewModel.navigateToCategory(cat.id)
                                    }
                                }
                                if (chunk.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Margin space
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CategoryGridCard(
    category: Category,
    isAr: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = if (category.imageUrl.isBlank()) "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=300" else category.imageUrl,
                contentDescription = category.nameAr,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAr) category.nameAr else category.nameEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RecommendedProviderCard(
    provider: Provider,
    isAr: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = provider.personalPhotoUrl,
                    contentDescription = provider.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Pinned badge inside layout
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopEnd)
                        .background(Color(0xFFFFD700), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("⭐", fontSize = 9.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = provider.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = provider.district,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⭐", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${provider.rating} (${provider.reviewCount})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProviderCompactCard(
    provider: Provider,
    categories: List<Category>,
    isAr: Boolean,
    onClick: () -> Unit
) {
    val categoryName = remember(provider.categoryId, categories) {
        val c = categories.find { it.id == provider.categoryId }
        if (c != null) (if (isAr) c.nameAr else c.nameEn) else ""
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = provider.personalPhotoUrl,
                contentDescription = provider.name,
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = provider.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (provider.isPinned) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE2C412), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(if (isAr) "مثبّت" else "Pinned", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(
                    text = "$categoryName • ${provider.district}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = provider.workAddress,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${provider.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text("(${provider.reviewCount})", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 2: LOGIN SCREEN
// -------------------------------------------------------------
@Composable
fun LoginScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val role by viewModel.currentRole.collectAsState()
    val usernameState by viewModel.currentUsername.collectAsState()
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (role != "Guest") {
            // Already Logged-in view
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (role == "Admin") t["admin_logged"] ?: "" else t["super_logged"] ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${t["username"]}: $usernameState",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.navigateTo("admin") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(t["admin_panel"] ?: "Admin Panel")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text(t["logout"] ?: "Logout")
                    }
                }
            }
        } else {
            // Standard username/password input form
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = t["admin_panel"] ?: "Security Access Control",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) "تسجيل الدخول للمدير والأوصياء والمشرفين" else "Admin and supervisor portal access",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(t["username"] ?: "Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(t["password"] ?: "Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val success = viewModel.login(username, password)
                            if (success) {
                                username = ""
                                password = ""
                                Toast.makeText(context, if (isAr) "تم المصادقة بنجاح!" else "Authenticated successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, t["invalid_login"], Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(t["login_btn"] ?: "Log In")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 3: DETAILED PROFESSIONAL REGISTRATION FORM (👤)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
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
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var workAddress by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var gpsCoordinates by remember { mutableStateOf("") }
    var personalPhotoUrl by remember { mutableStateOf("") }
    var idCardPhotoUrl by remember { mutableStateOf("") }

    var expandedDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = t["register"] ?: "Professional Registrations",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isAr) "انضم إلى دليل الموثوقين الموحد. سيتم تدقيق ومراجعة طلبك فوراً من الإدارة." else "Join our service provider almanac today.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )
        }

        // Demo Autofill tool button
        item {
            FilledTonalButton(
                onClick = {
                    name = "م. ماهر محمد طاهر"
                    phone = "777644670"
                    if (categories.isNotEmpty()) {
                        selectedCategory = categories.first()
                    }
                    workAddress = "شارع حدة - برج السلام"
                    district = "مديرية السبعين"
                    gpsCoordinates = "15.3694, 44.1910"
                    personalPhotoUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=200"
                    idCardPhotoUrl = "https://images.unsplash.com/photo-1554774853-aae0a22c8aa4?w=300"
                    Toast.makeText(context, if (isAr) "تم ملء الاستمارة التجريبية بنجاح!" else "Demo values autofilled!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Star, contentDescription = "Demo generator")
                Spacer(modifier = Modifier.width(8.dp))
                Text(t["use_demo_images"] ?: "Autofill Demo File")
            }
        }

        // Form Fields
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(t["triple_name"] ?: "Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(t["phone"] ?: "Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Selector Dropdown Menu
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = if (selectedCategory != null) (if (isAr) selectedCategory!!.nameAr else selectedCategory!!.nameEn) else "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(t["select_cat"] ?: "Select Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(if (isAr) cat.nameAr else cat.nameEn) },
                                onClick = {
                                    selectedCategory = cat
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = workAddress,
                onValueChange = { workAddress = it },
                label = { Text(t["work_address"] ?: "Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text(t["district"] ?: "District") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = gpsCoordinates,
                onValueChange = { gpsCoordinates = it },
                label = { Text(t["gps"] ?: "GPS Connection") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = personalPhotoUrl,
                onValueChange = { personalPhotoUrl = it },
                label = { Text(t["personal_photo"] ?: "Photo url") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = idCardPhotoUrl,
                onValueChange = { idCardPhotoUrl = it },
                label = { Text(t["id_card"] ?: "ID url") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() || selectedCategory == null || workAddress.isBlank() || district.isBlank() || personalPhotoUrl.isBlank()) {
                        Toast.makeText(context, t["fill_required"], Toast.LENGTH_LONG).show()
                    } else {
                        viewModel.submitProfessionalRequest(
                            name = name,
                            phone = phone,
                            categoryId = selectedCategory!!.id,
                            workAddress = workAddress,
                            district = district,
                            gpsCoordinates = gpsCoordinates,
                            personalPhotoUrl = personalPhotoUrl,
                            idCardPhotoUrl = idCardPhotoUrl
                        )
                        // Clear
                        name = ""
                        phone = ""
                        selectedCategory = null
                        workAddress = ""
                        district = ""
                        gpsCoordinates = ""
                        personalPhotoUrl = ""
                        idCardPhotoUrl = ""
                        Toast.makeText(context, t["req_success"], Toast.LENGTH_LONG).show()
                        viewModel.navigateTo("home")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = t["submit_req"] ?: "Register Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 4: CATEGORY DETAILS SCREEN (LISTING WITH PIN/SORT VALUE)
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

    val currentCat = remember(categoryId, categories) {
        categories.find { it.id == categoryId }
    }

    // Filter, sort pinned first
    val catProviders = remember(categoryId, providers) {
        providers.filter { it.categoryId == categoryId && it.status == "approved" }
            .sortedByDescending { it.isPinned }
    }

    if (currentCat == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Category not found")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Header display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                IconButton(onClick = { viewModel.navigateTo("home") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (isAr) currentCat.nameAr else currentCat.nameEn,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${catProviders.size} ${if (isAr) "مقدم خدمة مسجل" else "providers registered"}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            if (catProviders.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isAr) "لا يوجد مقدمو خدمات في هذا القسم حالياً." else "No providers listed in this category yet.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(catProviders) { p ->
                        ProviderCompactCard(p, categories, isAr) {
                            viewModel.navigateToProvider(p.id)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 5: PROVIDER DETAILS SCREEN WITH REVIEWS ADDITION
// -------------------------------------------------------------
@Composable
fun ProviderDetailsScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val providerId by viewModel.selectedProviderId.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    val context = LocalContext.current

    val provider = remember(providerId, providers) {
        providers.find { it.id == providerId } ?: providers.find { it.id == providerId }
    }

    val providerReviews = remember(providerId, reviews) {
        reviews.filter { it.providerId == providerId }
    }

    // Interactive review additions inputs state
    var reviewUserName by remember { mutableStateOf("") }
    var reviewRating by remember { mutableFloatStateOf(5.0f) }
    var reviewComment by remember { mutableStateOf("") }

    // Visual image zoom overlays
    var zoomedImageUrl by remember { mutableStateOf<String?>(null) }

    if (provider == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val categoryName = remember(provider.categoryId, categories) {
            val c = categories.find { it.id == provider.categoryId }
            if (c != null) (if (isAr) c.nameAr else c.nameEn) else ""
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Page Title
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.navigateTo("home") }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "تفاصيل الحساب المهني للمزود" else "Professional Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Profile card block
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = provider.personalPhotoUrl,
                                    contentDescription = provider.name,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .clickable { zoomedImageUrl = provider.personalPhotoUrl },
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = provider.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = categoryName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("⭐", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${provider.rating} (${provider.reviewCount} ${if (isAr) "تقييم" else "reviews"})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 14.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // Contact Actions dial buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        try {
                                            val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                                            context.startActivity(i)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Cannot place dialler direct!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1.2f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isAr) "اتصال هاتف" else "Dial Provider")
                                }

                                Button(
                                    onClick = {
                                        try {
                                            // Launch WhatsApp
                                            val wpUrl = "https://api.whatsapp.com/send?phone=${provider.phone}"
                                            val i = Intent(Intent.ACTION_VIEW, Uri.parse(wpUrl))
                                            context.startActivity(i)
                                        } catch (e: Exception) {
                                            // Fallback
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "WhatsApp")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("واتساب", color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Field description listing
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // District Location details
                            Row {
                                Text(
                                    text = "${t["district"]}:  ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(text = provider.district, fontSize = 13.sp)
                            }
                            // Address details
                            Row {
                                Text(
                                    text = "${t["work_address"]}:  ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(text = provider.workAddress, fontSize = 13.sp)
                            }
                            // GPS click connection link
                            if (provider.gpsCoordinates.isNotBlank()) {
                                Row(
                                    modifier = Modifier.clickable {
                                        try {
                                            val i = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("geo:${provider.gpsCoordinates}?q=${provider.gpsCoordinates}")
                                            )
                                            context.startActivity(i)
                                        } catch (e: Exception) {
                                            // Web google maps link representation
                                            val link = "https://www.google.com/maps/search/?api=1&query=${provider.gpsCoordinates}"
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "${t["gps"]}:  ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = provider.gpsCoordinates,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // ID Card document image indicator
                if (provider.idCardPhotoUrl.isNotBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isAr) "بطاقة الهوية والضمانات المهنية" else "Guarantees & ID Documentation",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                AsyncImage(
                                    model = provider.idCardPhotoUrl,
                                    contentDescription = "ID Card doc",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { zoomedImageUrl = provider.idCardPhotoUrl },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // Reviews Add Input Section
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = t["add_review"] ?: "Submit ratings reviews",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            // Name input
                            OutlinedTextField(
                                value = reviewUserName,
                                onValueChange = { reviewUserName = it },
                                label = { Text(t["review_user"] ?: "Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            // Stars selector
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = if (isAr) "التقييم:" else "Rating: ", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                for (star in 1..5) {
                                    val checked = star <= reviewRating
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "$star stars selector",
                                        tint = if (checked) Color(0xFFFFD700) else Color.Gray,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable { reviewRating = star.toFloat() }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            // Comments input text
                            OutlinedTextField(
                                value = reviewComment,
                                onValueChange = { reviewComment = it },
                                label = { Text(t["review_comment"] ?: "Review") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    if (reviewUserName.isBlank() || reviewComment.isBlank()) {
                                        Toast.makeText(context, t["fill_required"], Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.addReview(provider.id, reviewUserName, reviewRating, reviewComment)
                                        reviewUserName = ""
                                        reviewComment = ""
                                        reviewRating = 5.0f
                                        Toast.makeText(context, if (isAr) "تم إرسال التقييم ونشره فوراً!" else "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(t["review_submit"] ?: "Submit Reviews")
                            }
                        }
                    }
                }

                // Reviews Listing
                item {
                    Text(
                        text = t["reviews_title"] ?: "Reviews",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (providerReviews.isEmpty()) {
                    item {
                        Text(
                            text = if (isAr) "لا توجد تقييمات لهذا المزود حالياً. كن أول من يكتب تقييماً!" else "No customer reviews yet. Write a review to build trust!",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                } else {
                    items(providerReviews) { r ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = r.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Row {
                                        for (st in 1..5) {
                                            val col = if (st <= r.rating) Color(0xFFFFD700) else Color.LightGray
                                            Icon(Icons.Default.Star, "star score", tint = col, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = r.comment, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // Image full screen dialog viewing zoom overlay
            if (zoomedImageUrl != null) {
                Dialog(onDismissRequest = { zoomedImageUrl = null }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = zoomedImageUrl,
                                contentDescription = "Zoomed screen image file",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                            IconButton(
                                onClick = { zoomedImageUrl = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                            ) {
                                Icon(Icons.Default.Close, "Close overview Zoom image", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 6: CONTROL ADVANCED PANELS SCREEN
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
    val supervisors by viewModel.supervisors.collectAsState()

    val context = LocalContext.current

    if (role == "Guest") {
        // Guarded Access Screen
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Icon(Icons.Default.Lock, "Shield guard", tint = Color.Red, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isAr) "عذراً، هذه اللوحة مخصصة للأفراد المعتمدين والمشرفين فقط" else "Access Unauthorized!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.navigateTo("login") }) {
                    Text(t["login"] ?: "Go to Secure Login")
                }
            }
        }
    } else {
        // High Quality Tab Manager Controls
        var activeTab by remember { mutableIntStateOf(0) }
        val tabs = remember(role, isAr) {
            val baseList = mutableListOf(
                t["tabs_requests"] ?: "Requests",
                t["tabs_cats"] ?: "Categories",
                t["tabs_direct_add"] ?: "Instant Add",
                t["tabs_manage_prov"] ?: "Listing Manage"
            )
            if (role == "Admin") {
                baseList.add(t["tabs_sups"] ?: "Supervisors")
            }
            baseList
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Panel Header Banner with Logout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (role == "Admin") t["admin_logged"] ?: "" else t["super_logged"] ?: "",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isAr) "تغيرات حية وسريعة وقابلة للمزامنة" else "Live Synchronized Directory Engine",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(t["logout"] ?: "Logout", fontSize = 11.sp)
                    }
                }
            }

            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Current Active Tab layout Router
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                when (activeTab) {
                    0 -> PendingRequestsTab(viewModel, pendingProviders, categories, isAr)
                    1 -> CategoriesAdminTab(viewModel, categories, isAr)
                    2 -> DirectAddProviderTab(viewModel, categories, isAr)
                    3 -> ManageProvidersTab(viewModel, providers, categories, role == "Admin", isAr)
                    4 -> if (role == "Admin") SupervisorsAdminTab(viewModel, supervisors, isAr) else Spacer(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

// Sub-Tab 1: Pending registration requests reviewer with image zoomable viewer
@Composable
fun PendingRequestsTab(
    viewModel: DaliliViewModel,
    requests: List<Provider>,
    categories: List<Category>,
    isAr: Boolean
) {
    var activeDetailsRequest by remember { mutableStateOf<Provider?>(null) }
    var zoomedImageUrl by remember { mutableStateOf<String?>(null) }

    if (activeDetailsRequest != null) {
        // Detailed Request Overlay view
        val r = activeDetailsRequest!!
        val catName = categories.find { it.id == r.categoryId }?.let { if (isAr) it.nameAr else it.nameEn } ?: r.categoryId

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { activeDetailsRequest = null }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "طلب: ${r.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "رقم الهاتف والواتساب: ${r.phone}", fontSize = 13.sp)
                        Text(text = "القسم المطلوب: $catName", fontSize = 13.sp)
                        Text(text = "العنوان المهني: ${r.workAddress}", fontSize = 13.sp)
                        Text(text = "مديرية الإقامة: ${r.district}", fontSize = 13.sp)
                        Text(text = "إحداثيات GPS: ${r.gpsCoordinates}", fontSize = 13.sp)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "الصورة الشخصية (تثبيت للتكبير)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        AsyncImage(
                            model = r.personalPhotoUrl,
                            contentDescription = "Personal Image Overview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { zoomedImageUrl = r.personalPhotoUrl },
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (r.idCardPhotoUrl.isNotBlank()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "صورة بطاقة الهوية (تثبيت للتكبير)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            AsyncImage(
                                model = r.idCardPhotoUrl,
                                contentDescription = "ID Card Overview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { zoomedImageUrl = r.idCardPhotoUrl },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Action Accept/reject buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.acceptPendingRequest(r.id)
                            activeDetailsRequest = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("قبول واعتماد", color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.rejectPendingRequest(r.id, "Rejected by administration review")
                            activeDetailsRequest = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("رفض واستبعاد")
                    }
                }
            }
        }

        // Expandable zoom dialog
        if (zoomedImageUrl != null) {
            Dialog(onDismissRequest = { zoomedImageUrl = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = zoomedImageUrl,
                            contentDescription = "Zoomed screen image file on Admin Area",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = { zoomedImageUrl = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                        ) {
                            Icon(Icons.Default.Close, "Close overview Zoom", tint = Color.White)
                        }
                    }
                }
            }
        }
    } else {
        // List pending requests
        if (requests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isAr) "لا توجد طلبات تسجيل معلقة بانتظار المراجعة حالياً." else "No registrations waiting review.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(requests) { req ->
                    val catName = categories.find { it.id == req.categoryId }?.let { if (isAr) it.nameAr else it.nameEn } ?: req.categoryId
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeDetailsRequest = req },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = req.personalPhotoUrl,
                                contentDescription = req.name,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(req.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("القسم: $catName • سكن: ${req.district}", fontSize = 11.sp, color = Color.Gray)
                                Text("جوال: ${req.phone}", fontSize = 11.sp)
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = "View Details spec")
                        }
                    }
                }
            }
        }
    }
}

// Sub-Tab 2: Categories hierarchy adder administration (إدارة الأقسام)
@Composable
fun CategoriesAdminTab(
    viewModel: DaliliViewModel,
    categories: List<Category>,
    isAr: Boolean
) {
    var showAddMainDialog by remember { mutableStateOf(false) }
    var selectedMainCategoryForSub by remember { mutableStateOf<Category?>(null) }

    // Input States
    var nameAr by remember { mutableStateOf("") }
    var nameEn by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf("0") }

    var subNameAr by remember { mutableStateOf("") }
    var subNameEn by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "الأقسام الرئيسية المتوفرة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Button(onClick = { showAddMainDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add categoryicon")
                Spacer(modifier = Modifier.width(4.dp))
                Text("قسم جديد")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "${cat.nameAr} (${cat.nameEn})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(onClick = { selectedMainCategoryForSub = cat }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add sub category", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.deleteCategory(cat.id) }) {
                                    Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                                }
                            }
                        }
                        
                        // Listing subcategories nested
                        if (cat.subcategories.isNotEmpty()) {
                            Text(
                                text = "الأقسام الفرعية التابعة له:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )
                            Row( // Stable horizontal scroll list
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                cat.subcategories.forEach { sub ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = if (isAr) sub.nameAr else sub.nameEn, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog adding Main Category
        if (showAddMainDialog) {
            Dialog(onDismissRequest = { showAddMainDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().padding(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "إضافة قسم رئيسي جديد", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        OutlinedTextField(value = nameAr, onValueChange = { nameAr = it }, label = { Text("الاسم بالعربية") })
                        OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text("الاسم بالإنجليزية") })
                        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("رابط صورة الغلاف") })
                        OutlinedTextField(value = sortOrder, onValueChange = { sortOrder = it }, label = { Text("الترتيب العددي") })
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { showAddMainDialog = false }) {
                                Text("إلغاء")
                            }
                            Button(onClick = {
                                if (nameAr.isBlank() || nameEn.isBlank()) {
                                    Toast.makeText(context, "الرجاء إدخال الاسمين للمطابقة!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val order = sortOrder.toIntOrNull() ?: 0
                                    viewModel.addMainCategory(nameAr, nameEn, imageUrl, order)
                                    nameAr = ""
                                    nameEn = ""
                                    imageUrl = ""
                                    sortOrder = "0"
                                    showAddMainDialog = false
                                }
                            }) {
                                Text("حفظ القسم")
                            }
                        }
                    }
                }
            }
        }

        // Dialog adding sub category
        if (selectedMainCategoryForSub != null) {
            val mainCat = selectedMainCategoryForSub!!
            Dialog(onDismissRequest = { selectedMainCategoryForSub = null }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().padding(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "إضافة قسم فرعي لـ: ${mainCat.nameAr}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        OutlinedTextField(value = subNameAr, onValueChange = { subNameAr = it }, label = { Text("الاسم الفرعي بالعربية") })
                        OutlinedTextField(value = subNameEn, onValueChange = { subNameEn = it }, label = { Text("الاسم الفرعي بالإنجليزية") })
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { selectedMainCategoryForSub = null }) {
                                Text("إلغاء")
                            }
                            Button(onClick = {
                                if (subNameAr.isBlank() || subNameEn.isBlank()) {
                                    Toast.makeText(context, "املأ جميع الحقول إجبارياً!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.addSubcategory(mainCat.id, subNameAr, subNameEn)
                                    subNameAr = ""
                                    subNameEn = ""
                                    selectedMainCategoryForSub = null
                                }
                            }) {
                                Text("إضافة الفرع")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Sub-Tab 3: Add provider directly by admin (إضافة مقدم خدمة مباشرة بدون شروط)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectAddProviderTab(
    viewModel: DaliliViewModel,
    categories: List<Category>,
    isAr: Boolean
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubcategoryId by remember { mutableStateOf("") }
    var workAddress by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var gpsCoordinates by remember { mutableStateOf("") }
    var personalPhotoUrl by remember { mutableStateOf("") }

    var expandedDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(text = "إضافة مقدم خدمة مباشر (بدون الحاجة لموافقة أو رفع هوامش)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("الاسم المهني الثلاثي") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم جوال الإتصال وواتس") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category dropdown selector
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = if (selectedCategory != null) (if (isAr) selectedCategory!!.nameAr else selectedCategory!!.nameEn) else "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("اختر القسم والخدمة") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(if (isAr) cat.nameAr else cat.nameEn) },
                                onClick = {
                                    selectedCategory = cat
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = workAddress,
                onValueChange = { workAddress = it },
                label = { Text("العنوان وتوصيف مكان العمل") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("المديرية / الدائرة") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = gpsCoordinates,
                onValueChange = { gpsCoordinates = it },
                label = { Text("إحداثيات GPS (اختياري)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = personalPhotoUrl,
                onValueChange = { personalPhotoUrl = it },
                label = { Text("رابط الصورة الشخصية (اختياري)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() || selectedCategory == null || workAddress.isBlank() || district.isBlank()) {
                        Toast.makeText(context, "الرجاء كتابة جميع الحقول الأساسية قبل الإضافة المباشرة!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.addDirectProvider(
                            name = name,
                            phone = phone,
                            categoryId = selectedCategory!!.id,
                            subcategoryId = selectedSubcategoryId,
                            workAddress = workAddress,
                            district = district,
                            gpsCoordinates = gpsCoordinates,
                            personalPhotoUrl = personalPhotoUrl
                        )
                        // Reset
                        name = ""
                        phone = ""
                        selectedCategory = null
                        workAddress = ""
                        district = ""
                        gpsCoordinates = ""
                        personalPhotoUrl = ""
                        Toast.makeText(context, "تم حفظ مقدم الخدمة ونشره فوراً في الدليل الحقيقي!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
                Text("إضافة ونشر لمقدم الخدمة مباشرة", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Sub-Tab 4: Pin & Recommend & Delete approved directory users (تثبيت وترشيح مقدمي الخدمة)
@Composable
fun ManageProvidersTab(
    viewModel: DaliliViewModel,
    providers: List<Provider>,
    categories: List<Category>,
    isAdmin: Boolean,
    isAr: Boolean
) {
    val context = LocalContext.current
    if (providers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا تملك أي مزودي خدمات مسجلين ونشطين حتى هذه اللحظة.", color = Color.Gray, fontSize = 12.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(providers) { p ->
                val catName = categories.find { it.id == p.categoryId }?.let { if (isAr) it.nameAr else it.nameEn } ?: p.categoryId
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "جوال: ${p.phone} • القسم: $catName", fontSize = 11.sp, color = Color.Gray)
                            }

                            // Pin and Recommend Toggles (Enabled only for Admins)
                            Row(
                                modifier = Modifier.weight(1.7f),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Recommend ⭐
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            if (isAdmin) {
                                                viewModel.toggleRecommend(p.id)
                                            } else {
                                                Toast.makeText(context, "ميزة الترشيح والترقية مخصصة للمدير الرئيسي WAM2026 فقط!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Recommend star flag",
                                        tint = if (p.isRecommended) Color(0xFFFFD700) else Color.LightGray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(if (p.isRecommended) "مرشّح" else "ترشيح", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Pin 📌
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            if (isAdmin) {
                                                viewModel.togglePin(p.id)
                                            } else {
                                                Toast.makeText(context, "ميزة التثبيت الرأسي مخصصة للمدير الرئيسي WAM2026 فقط!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Pin category flag",
                                        tint = if (p.isPinned) Color(0xFF1E88E5) else Color.LightGray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(if (p.isPinned) "مثبّت" else "تثبيت", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Full delete
                                IconButton(onClick = { viewModel.deleteProvider(p.id) }) {
                                    Icon(Icons.Default.Delete, "Delete provider", tint = Color.Red, modifier = Modifier.size(22.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Sub-Tab 5: Create/Delete Supervisors (إدارة المشرفين) - ONLY FOR ADMIN (WAM2026)
@Composable
fun SupervisorsAdminTab(
    viewModel: DaliliViewModel,
    supervisors: List<Supervisor>,
    isAr: Boolean
) {
    var supUsername by remember { mutableStateOf("") }
    var supPassword by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "إنشاء حساب مشرف فرعي للتدقيق والمراجعة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = supUsername,
                onValueChange = { supUsername = it },
                label = { Text("المتدرب / المشرف") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = supPassword,
                onValueChange = { supPassword = it },
                label = { Text("رمز المرور") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = {
                    if (supUsername.isBlank() || supPassword.isBlank()) {
                        Toast.makeText(context, "الرجاء تعبئة بيانات المشرف كاملة!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.addSupervisor(supUsername, supPassword)
                        supUsername = ""
                        supPassword = ""
                        Toast.makeText(context, "تم حفظ المشرف وبدء حظه في المزامنة الفورية الجارية!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.height(54.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save sup")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "المشرفون الفرعيون النشطاء حالياً", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))

        if (supervisors.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("لا يوجد مشرفين مدخرين في قاعدة البيانات الحالية.", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(supervisors) { sup ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "المشرف: ${sup.username}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "كلمة المرور المسجلة: ${sup.password}", fontSize = 11.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { viewModel.deleteSupervisor(sup.id) }) {
                                Icon(Icons.Default.Delete, "Delete sup user", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 7: GLOBAL SECRET SETTINGS SCREEN (البوابة الخلفية)
// -------------------------------------------------------------
@Composable
fun SecretSettingsScreen(
    viewModel: DaliliViewModel,
    t: Map<String, String>,
    isAr: Boolean
) {
    val settings by viewModel.appSettings.collectAsState()
    val context = LocalContext.current

    var appName by remember { mutableStateOf(settings.appName) }
    var primaryHex by remember { mutableStateOf(settings.primaryColorHex) }
    var secondaryHex by remember { mutableStateOf(settings.secondaryColorHex) }
    var welcomeMsg by remember { mutableStateOf(settings.welcomeMessage) }
    var footerText by remember { mutableStateOf(settings.footerText) }
    var supportNum by remember { mutableStateOf(settings.supportNumber) }
    var supportEmail by remember { mutableStateOf(settings.supportEmail) }
    var adminPass by remember { mutableStateOf(settings.adminPasswordHex) }

    // Synchronize local input state in case setting listener gets remote update
    LaunchedEffect(settings) {
        appName = settings.appName
        primaryHex = settings.primaryColorHex
        secondaryHex = settings.secondaryColorHex
        welcomeMsg = settings.welcomeMessage
        footerText = settings.footerText
        supportNum = settings.supportNumber
        supportEmail = settings.supportEmail
        adminPass = settings.adminPasswordHex
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("home") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = t["secret_settings_title"] ?: "Owner Secret settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isAr) "تغيرات وتعديلات فورية تؤثر مباشرة على جميع الأجهزة دون الحاجة لإعادة الكود" else "Live updates sent across all sync nodes instantly",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Form fields for Secret Owner Actions
        item {
            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text(t["app_name_setting"] ?: "App Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = primaryHex,
                    onValueChange = { primaryHex = it },
                    label = { Text(t["primary_color"] ?: "Primary (Hex)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = secondaryHex,
                    onValueChange = { secondaryHex = it },
                    label = { Text(t["secondary_color"] ?: "Secondary (Hex)") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            OutlinedTextField(
                value = welcomeMsg,
                onValueChange = { welcomeMsg = it },
                label = { Text(t["welcome_message"] ?: "Welcome Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }

        item {
            OutlinedTextField(
                value = footerText,
                onValueChange = { footerText = it },
                label = { Text(t["footer_setting"] ?: "Sponsor Footer") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = supportNum,
                onValueChange = { supportNum = it },
                label = { Text(t["sup_phone"] ?: "Support Phone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = supportEmail,
                onValueChange = { supportEmail = it },
                label = { Text(t["sup_email"] ?: "Support Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = adminPass,
                onValueChange = { adminPass = it },
                label = { Text(t["admin_pwd"] ?: "Admin Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Button(
                onClick = {
                    if (appName.isBlank() || primaryHex.isBlank() || secondaryHex.isBlank() || adminPass.isBlank()) {
                        Toast.makeText(context, "يرجى تعبئة الحقول الأساسية لضمان سلامة المصادقة المباشرة!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.updateSecretSettings(
                            appName = appName,
                            primaryHex = primaryHex,
                            secondaryHex = secondaryHex,
                            footerText = footerText,
                            welcomeMsg = welcomeMsg,
                            supportNum = supportNum,
                            supportEmail = supportEmail,
                            adminPass = adminPass
                        )
                        Toast.makeText(context, if (isAr) "تم الحفظ والتزامن الفوري مع الأجهزة!" else "Settings propagated to all clients in real-time!", Toast.LENGTH_SHORT).show()
                        viewModel.navigateTo("home")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save settings")
                Spacer(modifier = Modifier.width(8.dp))
                Text(t["save_all"] ?: "Save and Propagate Changes")
            }
        }
    }
}
