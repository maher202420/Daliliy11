package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Admin
import com.example.data.Category
import com.example.data.ServiceProvider
import com.example.ui.DaliliViewModel
import com.example.ui.theme.MyApplicationTheme

sealed class Screen {
    object Home : Screen()
    data class CategoryDetails(val category: Category) : Screen()
    object Login : Screen()
    object AdminDashboard : Screen()
}

class MainActivity : ComponentActivity() {
    private val viewModel: DaliliViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val activeTheme by viewModel.currentTheme.collectAsState()
            MyApplicationTheme(themeChoice = activeTheme) {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(viewModel: DaliliViewModel) {
    val context = LocalContext.current
    
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    
    val categories by viewModel.categories.collectAsState()
    val serviceProviders by viewModel.serviceProviders.collectAsState()
    val admins by viewModel.admins.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Handle Hardware Back Press
    BackHandler(enabled = currentScreen != Screen.Home) {
        currentScreen = when (currentScreen) {
            is Screen.CategoryDetails -> Screen.Home
            Screen.Login -> Screen.Home
            Screen.AdminDashboard -> Screen.Home
            Screen.Home -> Screen.Home // unreachable
        }
    }

    // Error toast feedback
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            PromoFooterSection()
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        slideInHorizontally { width -> if (targetState is Screen.Home) -width else width } togetherWith
                        slideOutHorizontally { width -> if (targetState is Screen.Home) width else -width }
                    },
                    label = "screen_tr"
                ) { targetScreen ->
                    when (targetScreen) {
                        is Screen.Home -> {
                            HomeScreen(
                                categories = categories,
                                serviceProviders = serviceProviders,
                                searchQuery = searchQuery,
                                currentUser = currentUser,
                                onCategoryClick = { currentScreen = Screen.CategoryDetails(it) },
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onAdminIconClick = {
                                    if (currentUser != null) {
                                        currentScreen = Screen.AdminDashboard
                                    } else {
                                        currentScreen = Screen.Login
                                    }
                                },
                                onLogoutClick = {
                                    viewModel.logout()
                                    Toast.makeText(context, "تم تسجيل الخروج", Toast.LENGTH_SHORT).show()
                                },
                                viewModel = viewModel
                            )
                        }
                        is Screen.CategoryDetails -> {
                            CategoryProvidersScreen(
                                category = targetScreen.category,
                                allProviders = serviceProviders,
                                onBackClick = { currentScreen = Screen.Home },
                                viewModel = viewModel
                            )
                        }
                        is Screen.Login -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    currentScreen = Screen.AdminDashboard
                                    Toast.makeText(context, "أهلاً بك!", Toast.LENGTH_SHORT).show()
                                },
                                onBackClick = { currentScreen = Screen.Home },
                                viewModel = viewModel
                            )
                        }
                        is Screen.AdminDashboard -> {
                            if (currentUser == null) {
                                LaunchedEffect(Unit) { currentScreen = Screen.Login }
                            } else {
                                AdminDashboardScreen(
                                    currentUser = currentUser!!,
                                    categories = categories,
                                    serviceProviders = serviceProviders,
                                    admins = admins,
                                    viewModel = viewModel,
                                    onBackClick = { currentScreen = Screen.Home }
                                )
                            }
                        }
                    }
                }

                // Loading Overlay Indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "جاري المزامنة مع قاعدة البيانات...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【1. CONFIG - PROMO FOOTER】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun PromoFooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MAW 777644670",
            color = Color.LightGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("promo_footer")
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【2. HOME SCREEN】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun HomeScreen(
    categories: List<Category>,
    serviceProviders: List<ServiceProvider>,
    searchQuery: String,
    currentUser: Admin?,
    onCategoryClick: (Category) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onAdminIconClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: DaliliViewModel
) {
    val isOnline by viewModel.isCloudConnected.collectAsState()
    val customAppNameVal by viewModel.customAppName.collectAsState()
    val customAppLogoVal by viewModel.customAppLogo.collectAsState()
    var backdoorClickCount by remember { mutableStateOf(0) }
    var lastBackdoorClickTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current
    var showDiagnosticsDialog by remember { mutableStateOf(false) }

    // Filter logic for both sections or providers
    val filteredCategories = remember(categories, searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            categories
        } else {
            categories.filter { cat ->
                cat.nameAr.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val filteredProvidersCount = remember(serviceProviders, searchQuery) {
        if (searchQuery.trim().isEmpty()) 0 else {
            serviceProviders.filter { it.name.contains(searchQuery, ignoreCase = true) }.size
        }
    }

    if (showDiagnosticsDialog) {
        val currentUrl = remember { viewModel.getSupabaseUrl() }
        val currentKey = remember { viewModel.getSupabaseKey() }
        AlertDialog(
            onDismissRequest = { showDiagnosticsDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.refreshAll()
                        showDiagnosticsDialog = false
                    }
                ) {
                    Text("تحديث وفحص مجدداً", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiagnosticsDialog = false }) {
                    Text("إغلاق")
                }
            },
            icon = {
                Icon(
                    imageVector = if (isOnline) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = "اتصال",
                    tint = if (isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = if (isOnline) "المزامنة السحابية نشطة ومتصلة" else "التشغيل في الوضع المحلي الهجين",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (isOnline) 
                            "التطبيق متصل بقاعدة البيانات السحابية (Supabase) بشكل ممتاز. أي تعديل يتم حفظه في لوحة التحكم يظهر فوراً وبشكل تلقائي لدى جميع مستخدمي التطبيق في ثوانٍ!" 
                        else 
                            "يتعذر الاتصال بالخادم السحابي حالياً. يعمل التطبيق بكفاءة كاملة في الوضع المحلي الذكي (Offline Hybrid Cache) بحيث تتصفح جميع الأقسام مسبقة التحميل وتتصل بمقدمي الخدمات بحرية وسرعة فائقة.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Right,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("معلومات الربط السحابي الحالية للربط:", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("الرابط: ${currentUrl.substringBefore("/rest/")}", fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("المفتاح: ${currentKey.take(15)}...", fontSize = 10.sp, color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentUser != null) {
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "تسجيل الخروج",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Title & logo which allows backdoor access when clicked 5 times
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val now = System.currentTimeMillis()
                        if (now - lastBackdoorClickTime < 2500) {
                            backdoorClickCount++
                            if (backdoorClickCount >= 5) {
                                backdoorClickCount = 0
                                onAdminIconClick()
                                Toast.makeText(context, "🔓 تم كشف بوابة الدخول الخلفية السرية للوحة التحكم!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            backdoorClickCount = 1
                        }
                        lastBackdoorClickTime = now
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = customAppLogoVal,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = customAppNameVal,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            // Admin Dashboard Control Panel Access Action Button
            IconButton(
                onClick = onAdminIconClick,
                modifier = Modifier.testTag("admin_dashboard_button")
            ) {
                Icon(
                    imageVector = if (currentUser != null) Icons.Default.Settings else Icons.Default.Lock,
                    contentDescription = "لوحة التحكم",
                    tint = if (currentUser != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            }
        }

        // Subtext description
        Text(
            text = "المساعد الفوري للوصول إلى الخدمات ومقدمي الخدمات المحليين بلحظة واحدة.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Connection status pill
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .clickable { showDiagnosticsDialog = true }
                .background(
                    color = if (isOnline) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFFF9800).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isOnline) "متزامن سحابياً بالإنترنت 🟢" else "تصفح أوفلاين محلي 🟠 (اضغط للفحص)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }

        // Search text field
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { 
                Text(
                    "ابحث عن الأقسام أو مقدمي الخدمات...", 
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                ) 
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "مسح")
                    }
                }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .testTag("search_bar")
        )

        // If searching a provider directly from home, show top prompt
        if (searchQuery.trim().isNotEmpty() && filteredProvidersCount > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = "معلومات", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تم العثور على $filteredProvidersCount مقدم خدمة يطابق بحثك. اضغط على أقسامهم بالأسفل لعرضهم.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Main Categories Grid
        if (filteredCategories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info, 
                        contentDescription = "تحذير",
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "عذراً، لم نجد أي قسم يطابق هذا الاسم.",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCategories, key = { it.id ?: 0 }) { item ->
                    CategoryCardItem(category = item, onClick = { onCategoryClick(item) })
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【3. CATEGORY CARD COMPOSABLE】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCardItem(category: Category, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .testTag("category_card_${category.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Check if icon is URL or Emoji symbol
            val isIconUrl = category.icon.startsWith("http://") || category.icon.startsWith("https://")
            
            if (isIconUrl) {
                AsyncImage(
                    model = category.icon,
                    contentDescription = category.nameAr,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = null // fallbacks to emoji display of first letter
                )
            } else {
                // Large emojis or letters
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (category.icon.isNotEmpty()) category.icon else "?",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = category.nameAr,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【4. SERVICE PROVIDERS SCREEN】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun CategoryProvidersScreen(
    category: Category,
    allProviders: List<ServiceProvider>,
    onBackClick: () -> Unit,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var qInput by remember { mutableStateOf("") }
    var activeProviderReviewsTarget by remember { mutableStateOf<ServiceProvider?>(null) }
    
    // Filter byCategory ID & Filter query name
    val filteredList = remember(allProviders, category.id, qInput) {
        allProviders.filter { 
            it.categoryId == category.id && 
            it.isActive &&
            (qInput.isEmpty() || it.name.contains(qInput, ignoreCase = true) || it.phone.contains(qInput, ignoreCase = true))
        }
    }

    if (activeProviderReviewsTarget != null) {
        val provider = activeProviderReviewsTarget!!
        val reviewsState by viewModel.reviews.collectAsState()
        val providerReviews = remember(reviewsState, provider.id) {
            reviewsState.filter { it.providerId == provider.id }
        }
        val currentUser by viewModel.currentUser.collectAsState()
        val isAdmin = currentUser != null

        var reviewerName by remember { mutableStateOf("") }
        var reviewComment by remember { mutableStateOf("") }
        var selectedRatingStars by remember { mutableStateOf(5.0) }
        var reviewErrorHint by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { activeProviderReviewsTarget = null },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { activeProviderReviewsTarget = null }) {
                    Text("إغلاق", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "تقييمات وآراء العملاء للخدمة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.End
                ) {
                    // Overall average card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val averageText = if (providerReviews.isEmpty()) provider.rating.toString() else {
                                val avg = providerReviews.map { it.rating }.average()
                                String.format("%.1f", avg)
                            }
                            Text(
                                text = "⭐ $averageText / 5.0",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "بناءً على ${providerReviews.size} مراجعة مسجّلة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Reviews feed list
                    Text(
                        text = "آراء العملاء المكتوبة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    if (providerReviews.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "لا توجد تعليقات بعد لهذا مقدم الخدمة. كن أول من يضيف تعليقك!",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        providerReviews.forEach { review ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Trash bin for quick deletion by Admin
                                        if (isAdmin) {
                                            IconButton(
                                                onClick = {
                                                    review.id?.let { rId ->
                                                        viewModel.deleteReview(rId) { res ->
                                                            if (res) {
                                                                Toast.makeText(context, "تم حذف تعليق العميل بنجاح", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "حذف التعليق",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.width(1.dp))
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "⭐ ${review.rating}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(end = 6.dp)
                                            )
                                            Text(
                                                text = review.userName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                    Text(
                                        text = review.comment,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                        modifier = Modifier.padding(top = 4.dp),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Comment Submission Form
                    Text(
                        text = "شارك رأيك وقيم مقدم المهنة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Clickable Interactive Yellow Star Selector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("مستوى التقييم بالنجوم:", fontSize = 11.sp, modifier = Modifier.padding(end = 8.dp))
                        repeat(5) { index ->
                            val starValue = index + 1.0
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "تقييم بالنجوم",
                                tint = if (selectedRatingStars >= starValue) MaterialTheme.colorScheme.secondary else Color.LightGray,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { selectedRatingStars = starValue }
                            )
                        }
                    }

                    // Author Name Input (Styled Outlined RTL)
                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("اسمك الكريم", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End, textDirection = TextDirection.Rtl)
                    )

                    // Comment Area Input (Styled Outlined RTL)
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("اكتب مراجعة عملك أو تعليقك هنا...", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End, textDirection = TextDirection.Rtl)
                    )

                    if (reviewErrorHint != null) {
                        Text(
                            text = reviewErrorHint!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.End
                        )
                    }

                    // Send Button
                    Button(
                        onClick = {
                            if (reviewerName.trim().isEmpty() || reviewComment.trim().isEmpty()) {
                                reviewErrorHint = "الرجاء تعبئة اسمك الكريم ومربع التعليق!"
                            } else {
                                reviewErrorHint = null
                                viewModel.addReview(
                                    providerId = provider.id ?: 0,
                                    userName = reviewerName.trim(),
                                    comment = reviewComment.trim(),
                                    rating = selectedRatingStars
                                ) { ok ->
                                    if (ok) {
                                        reviewerName = ""
                                        reviewComment = ""
                                        selectedRatingStars = 5.0
                                        Toast.makeText(context, "شكرًا لك! تم نشر مراجعتك بنجاح", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إرسال التقييم والتعليق", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(48.dp)) // symmetry spacer
            Text(
                text = category.nameAr,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("providers_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward, // RTL back arrow direction
                    contentDescription = "العودة للرئيسية"
                )
            }
        }

        // Inline Search in Providers List
        TextField(
            value = qInput,
            onValueChange = { qInput = it },
            placeholder = { 
                Text(
                    "ابحث في هذا القسم...", 
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                ) 
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Services list
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person, 
                        contentDescription = "خالي", 
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "عذراً، لم يتم العثور على مقدمي خدمات نشطين حالياً.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id ?: 0 }) { provider ->
                    ProviderCardRow(
                        provider = provider, 
                        context = context,
                        onClick = { activeProviderReviewsTarget = provider }
                    )
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【5. SERVICE PROVIDER ROW COMPOSABLE】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun ProviderCardRow(provider: ServiceProvider, context: Context, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("provider_row_${provider.id}"),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action Buttons Group (Left Aligned for convenient tapping)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Phone Intent Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("call_${provider.id}")
                ) {
                    Icon(
                        Icons.Default.Phone, 
                        contentDescription = "اتصال", 
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اتصال", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // WhatsApp API View Intent Button (Styled Vibrant Green #4CAF50)
                Button(
                    onClick = {
                        val pureDigits = provider.phone.filter { it.isDigit() }
                        val linkUrl = "https://api.whatsapp.com/send?phone=$pureDigits"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("whatsapp_${provider.id}")
                ) {
                    Text("واتساب", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details info in Middle/Right
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = provider.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = provider.phone,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // Ratings Stars (Task 3: التقييم نجوم)
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val fullStars = provider.rating.toInt()
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "تقييم",
                            tint = if (index < fullStars) MaterialTheme.colorScheme.secondary else Color.LightGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${provider.rating}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Image Thumbnail Container
            val hasImage = !provider.imageUrl.isNullOrBlank()
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (hasImage) {
                    AsyncImage(
                        model = provider.imageUrl,
                        contentDescription = provider.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "افتراضي",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【6. ADMIN LOGIN SCREEN】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: DaliliViewModel
) {
    var userVal by remember { mutableStateOf("") }
    var passVal by remember { mutableStateOf("") }
    var helperText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Character Glyph centered
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("د", fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "تسجيل دخول الإدارة",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Username
        OutlinedTextField(
            value = userVal,
            onValueChange = { userVal = it },
            label = { Text("اسم المستخدم", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("username_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = passVal,
            onValueChange = { passVal = it },
            label = { Text("كلمة المرور", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("password_field")
        )

        if (helperText != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = helperText!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                if (userVal.trim().isEmpty() || passVal.trim().isEmpty()) {
                    helperText = "يرجى تعبئة كافة الحقول"
                    return@Button
                }
                viewModel.login(userVal.trim(), passVal) { success, err ->
                    if (success) {
                        onLoginSuccess()
                    } else {
                        helperText = err ?: "فشل تسجيل الدخول"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_login"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("دخول", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackClick) {
            Text("إلغاء والعودة للرئيسية", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【7. DASHBOARD SCREEN & TABS】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun AdminDashboardScreen(
    currentUser: Admin,
    categories: List<Category>,
    serviceProviders: List<ServiceProvider>,
    admins: List<Admin>,
    viewModel: DaliliViewModel,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    // Tab list (super_admin has 4 tabs, standard admin has 2)
    val isSuperAdmin = currentUser.role == "super_admin" || currentUser.username == "admin"
    val tabs = if (isSuperAdmin) {
        listOf("المشرفون", "الأقسام", "مقدمو الخدمات", "الإعدادات")
    } else {
        listOf("الأقسام", "مقدمو الخدمات")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(48.dp))
            // Central Title
            Text(
                text = "لوحة التحكم",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowForward, contentDescription = "قائمة")
            }
        }

        Text(
            text = "المستخدم الحالي: ${currentUser.username} (${if (isSuperAdmin) "مدير نظام" else "مشرف عادي"})",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Center
        )

        // Custom Navigation Tab Row
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Render contents based on active tab
        Box(modifier = Modifier.weight(1f)) {
            if (isSuperAdmin) {
                when (selectedTab) {
                    0 -> AdminsManagementSubscreen(admins = admins, viewModel = viewModel)
                    1 -> CategoriesManagementSubscreen(categories = categories, viewModel = viewModel)
                    2 -> ProvidersManagementSubscreen(providers = serviceProviders, categories = categories, viewModel = viewModel)
                    3 -> SettingsManagementSubscreen(viewModel = viewModel)
                }
            } else {
                when (selectedTab) {
                    0 -> CategoriesManagementSubscreen(categories = categories, viewModel = viewModel)
                    1 -> ProvidersManagementSubscreen(providers = serviceProviders, categories = categories, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SettingsManagementSubscreen(viewModel: DaliliViewModel) {
    val context = LocalContext.current
    val currentThemeChoice by viewModel.currentTheme.collectAsState()
    val isOnline by viewModel.isCloudConnected.collectAsState()

    val savedAppName by viewModel.customAppName.collectAsState()
    val savedAppLogo by viewModel.customAppLogo.collectAsState()

    var appNameInput by remember(savedAppName) { mutableStateOf(savedAppName) }
    var appLogoInput by remember(savedAppLogo) { mutableStateOf(savedAppLogo) }

    var customUrl by remember { mutableStateOf(viewModel.getSupabaseUrl()) }
    var customKey by remember { mutableStateOf(viewModel.getSupabaseKey()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.End
    ) {
        // App Custom Brand Identity Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "تخصيص هوية واسم التطبيق الشاملة 🏷️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "يسمح هذا الإعداد بتحديث اسم التطبيق الرئيسي والرمز التعبيري للعلامة فوراً لدى جميع المستخدمين بشكل مباشر وسلس.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = appNameInput,
                    onValueChange = { appNameInput = it },
                    label = { Text("اسم التطبيق المخصص", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                )

                OutlinedTextField(
                    value = appLogoInput,
                    onValueChange = { appLogoInput = it },
                    label = { Text("أيقونة/رمز التطبيق التعبيري (Emoji/Symbol)", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                )

                Button(
                    onClick = {
                        if (appNameInput.trim().isEmpty() || appLogoInput.trim().isEmpty()) {
                            Toast.makeText(context, "الرجاء تعبئة الحقول المطلوبة بشكل صحيح!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updateAppNameAndLogo(appNameInput, appLogoInput)
                            Toast.makeText(context, "تم حفظ وتطبيق هوية التطبيق الجديدة فوراً بنجاح! 🎉", Toast.LENGTH_LONG).show()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Done, contentDescription = "حفظ الهوية", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حفظ وتطبيق الهوية فوراً", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
        // Theme Colors Choice Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "تخصيص هوية التطبيق والألوان 🎨",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "اختر الهوية البصرية التي تناسب ذوق عملائك. يتغير مظهر التطبيق بالكامل فوراً وبأعلى دقة.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val themeOptions = listOf(
                    Triple("red_black", "الهوية الملكية (أحمر وأسود 🔴🖤)", listOf(Color(0xFFE53935), Color(0xFF121212))),
                    Triple("slate_silver", "الحديثة الهادئة (فضي 🔵🩶)", listOf(Color(0xFF2196F3), Color(0xFFB0BEC5))),
                    Triple("emerald_green", "الواحة الخضراء (أخضر زاهي 🟢💚)", listOf(Color(0xFF2E7D32), Color(0xFFA5D6A7))),
                    Triple("royal_indigo", "الغسق الكوني (بنفسجي وأزرق 🟣💙)", listOf(Color(0xFF673AB7), Color(0xFF42A5F5)))
                )

                themeOptions.forEach { (themeId, label, swatches) ->
                    val isSelected = currentThemeChoice == themeId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.setAppTheme(themeId)
                                Toast.makeText(context, "تم تطبيق السمة الجديدة بنجاح!", Toast.LENGTH_SHORT).show()
                            }
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        // Color Swatches
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            swatches.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(color, CircleShape)
                                )
                            }
                        }

                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.weight(1f)
                        )

                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                viewModel.setAppTheme(themeId)
                                Toast.makeText(context, "تم تطبيق السمة الجديدة بنجاح!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Supabase Cloud Configuration Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "رابط التوازن السحابي (Supabase) 🌐",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "يرتبط التطبيق بقاعدتك السحابية مباشرة. يمكنك تعديل الرابط أو تبديل المشغل في أي وقت وسيربط التطبيق نفسه تلقائياً دون الحاجة إلى تحديث متجر التطبيقات!",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = customUrl,
                    onValueChange = { customUrl = it },
                    label = { Text("Supabase URL (رابط المشروع السحابي)", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Left)
                )

                OutlinedTextField(
                    value = customKey,
                    onValueChange = { customKey = it },
                    label = { Text("Anon/Publishable Key (مفتاح المشروع المشترك)", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Left)
                )

                Button(
                    onClick = {
                        if (customUrl.trim().isEmpty() || customKey.trim().isEmpty()) {
                            Toast.makeText(context, "الرجاء كتابة الرابط ومفتاح الاتصال بشكل صحيح!", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.updateSupabaseConfig(customUrl.trim(), customKey.trim()) { success ->
                                if (success) {
                                    viewModel.refreshAll()
                                }
                            }
                            Toast.makeText(context, "تم تحديث إعدادات الاتصال بنجاح! يتم الآن إعادة الربط والمزامنة السحابية الفورية...", Toast.LENGTH_LONG).show()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "حفظ وإعادة مزامنة", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حفظ وإعادة الاتصال السحابي السريع", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Connection diagnostics checklist
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = if (isOnline) 
                                "حالة الربط: خادم سحابي نشط ومتزامن بنجاح عبر الإنترنت ✅" 
                            else 
                                "حالة الربط: خادم متوقف أو معطل، يعمل محلياً في وضع الحماية ❌",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFE65100),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【8. SUB-DASHBOARD: ADMINS (Super Admin only)】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun AdminsManagementSubscreen(admins: List<Admin>, viewModel: DaliliViewModel) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var changePasswordAdminTarget by remember { mutableStateOf<Admin?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Add Button
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("add_admin_btn"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "اضافة")
            Spacer(modifier = Modifier.width(6.dp))
            Text("إضافة مشرف جديد", fontWeight = FontWeight.Bold)
        }

        if (admins.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("لا يوجد مشرفين إضافيين مسجلين حالياً.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(admins, key = { it.id ?: 0 }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Delete button
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = {
                                        item.id?.let { adminId ->
                                            viewModel.deleteAdmin(adminId) { ok ->
                                                if (ok) Toast.makeText(context, "تم حذف المشرف بنجاح", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag("delete_admin_${item.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                }

                                TextButton(onClick = { changePasswordAdminTarget = item }) {
                                    Text("تغيير كلمة المرور", fontSize = 12.sp)
                                }
                            }

                            // Username
                            Column(horizontalAlignment = Alignment.End) {
                                Text(item.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(if (item.role == "super_admin") "مدير نظام" else "مشرف عادي", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Admin Dialog
    if (showAddDialog) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorHint by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة مشرف جديد", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("اسم المستخدم", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    errorHint?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (username.trim().isEmpty() || password.trim().isEmpty()) {
                        errorHint = "الرجاء ملء الفراغات"
                        return@Button
                    }
                    viewModel.addAdmin(username.trim(), password) { ok ->
                        if (ok) {
                            showAddDialog = false
                            Toast.makeText(context, "تم إضافة المشرف بنجاح", Toast.LENGTH_SHORT).show()
                        } else {
                            errorHint = "اسم المستخدم مكرر أو حدث خطأ بالخادم"
                        }
                    }
                }) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Change Password Dialog
    changePasswordAdminTarget?.let { adminTarget ->
        var newPass by remember { mutableStateOf("") }
        var errorHint by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { changePasswordAdminTarget = null },
            title = { Text("تغيير كلمة مرور المشرف ${adminTarget.username}", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("كلمة المرور الجديدة", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    errorHint?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newPass.trim().isEmpty()) {
                        errorHint = "يرجى كتابة كلمة المرور"
                        return@Button
                    }
                    adminTarget.id?.let { adminId ->
                        viewModel.changeAdminPassword(adminId, newPass) { ok ->
                            if (ok) {
                                changePasswordAdminTarget = null
                                Toast.makeText(context, "تم تغيير كلمة المرور للمشرف ${adminTarget.username}", Toast.LENGTH_SHORT).show()
                            } else {
                                errorHint = "فشل في تحديث كلمة المرور"
                            }
                        }
                    }
                }) {
                    Text("تحديث")
                }
            },
            dismissButton = {
                TextButton(onClick = { changePasswordAdminTarget = null }) { Text("إلغاء") }
            }
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【9. SUB-DASHBOARD: CATEGORIES】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun CategoriesManagementSubscreen(categories: List<Category>, viewModel: DaliliViewModel) {
    val context = LocalContext.current
    var showUpsertCategoryDialogTarget by remember { mutableStateOf<Category?>(null) }
    var isNewCategory by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                showUpsertCategoryDialogTarget = Category(id = null, nameAr = "", icon = "📁", orderIndex = categories.size + 1)
                isNewCategory = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("add_category_btn"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "اضافة")
            Spacer(modifier = Modifier.width(6.dp))
            Text("إضافة قسم جديد", fontWeight = FontWeight.Bold)
        }

        if (categories.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("جاري استيراد الأقسام...", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories, key = { it.id ?: 0 }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Delete/Edit buttons group
                            Row {
                                IconButton(
                                    onClick = {
                                        item.id?.let { catId ->
                                            viewModel.deleteCategory(catId) { ok ->
                                                if (ok) Toast.makeText(context, "تم حذف القسم بنجاح", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag("delete_category_${item.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                }

                                IconButton(
                                    onClick = {
                                        showUpsertCategoryDialogTarget = item
                                        isNewCategory = false
                                    },
                                    modifier = Modifier.testTag("edit_category_${item.id}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.secondary)
                                }
                            }

                            // Left Text Details
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(item.nameAr, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("الترتيب: ${item.orderIndex}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                // Icon tag preview
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (item.icon.startsWith("http")) {
                                        AsyncImage(
                                            model = item.icon,
                                            contentDescription = item.nameAr,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                                        )
                                    } else {
                                        Text(item.icon, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create / Update Category Dialog
    showUpsertCategoryDialogTarget?.let { catTarget ->
        var nameArIn by remember { mutableStateOf(catTarget.nameAr) }
        var iconIn by remember { mutableStateOf(catTarget.icon) }
        var orderIn by remember { mutableStateOf(catTarget.orderIndex.toString()) }
        var errorHint by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showUpsertCategoryDialogTarget = null },
            title = {
                Text(
                    text = if (isNewCategory) "إضافة قسم جديد" else "تعديل القسم",
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameArIn,
                        onValueChange = { nameArIn = it },
                        label = { Text("اسم القسم (بالعربية)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = iconIn,
                        onValueChange = { iconIn = it },
                        label = { Text("أيقونة (رمز إيموجي أو رابط صورة)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = orderIn,
                        onValueChange = { orderIn = it },
                        label = { Text("index الترتيب التسلسلي", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    errorHint?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (nameArIn.trim().isEmpty() || iconIn.trim().isEmpty() || orderIn.trim().isEmpty()) {
                        errorHint = "يرجى تعبئة كافة الحقول"
                        return@Button
                    }
                    val currentOrder = orderIn.toIntOrNull() ?: 0

                    if (isNewCategory) {
                        viewModel.addCategory(nameArIn.trim(), iconIn.trim(), currentOrder) { ok ->
                            if (ok) {
                                showUpsertCategoryDialogTarget = null
                                Toast.makeText(context, "تمت إضافة القسم", Toast.LENGTH_SHORT).show()
                            } else {
                                errorHint = "حدث خطأ بالشبكة"
                            }
                        }
                    } else {
                        catTarget.id?.let { catId ->
                            viewModel.updateCategory(catId, nameArIn.trim(), iconIn.trim(), currentOrder) { ok ->
                                if (ok) {
                                    showUpsertCategoryDialogTarget = null
                                    Toast.makeText(context, "تم حفظ التعديلات", Toast.LENGTH_SHORT).show()
                                } else {
                                    errorHint = "فشل في تحديث البيانات"
                                }
                            }
                        }
                    }
                }) {
                    Text(if (isNewCategory) "إضافة" else "حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpsertCategoryDialogTarget = null }) { Text("إلغاء") }
            }
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 【10. SUB-DASHBOARD: SERVICE PROVIDERS】
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun ProvidersManagementSubscreen(
    providers: List<ServiceProvider>,
    categories: List<Category>,
    viewModel: DaliliViewModel
) {
    val context = LocalContext.current
    var showUpsertProviderDialogTarget by remember { mutableStateOf<ServiceProvider?>(null) }
    var isNewProvider by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                val firstCatId = categories.firstOrNull()?.id ?: 0
                showUpsertProviderDialogTarget = ServiceProvider(
                    id = null,
                    name = "",
                    phone = "",
                    categoryId = firstCatId,
                    rating = 5.0,
                    imageUrl = "",
                    isActive = true
                )
                isNewProvider = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("add_provider_btn"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "اضافة")
            Spacer(modifier = Modifier.width(6.dp))
            Text("إضافة مقدم خدمة جديد", fontWeight = FontWeight.Bold)
        }

        if (providers.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("لا يتوفر مقدمي خدمات مسجلين حالياً.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(providers, key = { it.id ?: 0 }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Buttons
                            Row {
                                IconButton(
                                    onClick = {
                                        item.id?.let { provId ->
                                            viewModel.deleteServiceProvider(provId) { ok ->
                                                if (ok) Toast.makeText(context, "تم حذف مقدم الخدمة بنجاح", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag("delete_provider_${item.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                }

                                IconButton(
                                    onClick = {
                                        showUpsertProviderDialogTarget = item
                                        isNewProvider = false
                                    },
                                    modifier = Modifier.testTag("edit_provider_${item.id}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.secondary)
                                }
                            }

                            // Details
                            val categoryName = categories.firstOrNull { it.id == item.categoryId }?.nameAr ?: "قسم مجهول"
                            Column(horizontalAlignment = Alignment.End) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${item.phone} • $categoryName", fontSize = 11.sp, color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (item.isActive) "نشط" else "متوقف", fontSize = 10.sp, color = if (item.isActive) Color(0xFF4CAF50) else Color.Red, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تقييم: ${item.rating}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for Upsert Service Provider
    showUpsertProviderDialogTarget?.let { provTarget ->
        var nameIn by remember { mutableStateOf(provTarget.name) }
        var phoneIn by remember { mutableStateOf(provTarget.phone) }
        var categoryIdIn by remember { mutableStateOf(provTarget.categoryId) }
        var ratingIn by remember { mutableStateOf(provTarget.rating.toString()) }
        var imageUrlIn by remember { mutableStateOf(provTarget.imageUrl ?: "") }
        var isActiveIn by remember { mutableStateOf(provTarget.isActive) }
        var errorHint by remember { mutableStateOf<String?>(null) }
        
        var dropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showUpsertProviderDialogTarget = null },
            title = {
                Text(
                    text = if (isNewProvider) "إضافة مقدم خدمة جديد" else "تعديل مقدم الخدمة",
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                // Wrap in local Vertical Scroll so dialogue stays within height budget
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        OutlinedTextField(
                            value = nameIn,
                            onValueChange = { nameIn = it },
                            label = { Text("الاسم الكامل", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phoneIn,
                            onValueChange = { phoneIn = it },
                            label = { Text("رقم الهاتف", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Category ID selection Dropdown Menu
                        val selectedCategory = categories.firstOrNull { it.id == categoryIdIn }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { dropdownExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Text(
                                    text = "القسم: ${selectedCategory?.nameAr ?: "اضغط للاختيار"}",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.nameAr, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                                        onClick = {
                                            cat.id?.let { categoryIdIn = it }
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = ratingIn,
                            onValueChange = { ratingIn = it },
                            label = { Text("التقييم (مثال: 4.5 أو 5)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = imageUrlIn,
                            onValueChange = { imageUrlIn = it },
                            label = { Text("رابط صورة مقدم الخدمة (اختياري)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Switch(
                                checked = isActiveIn,
                                onCheckedChange = { isActiveIn = it }
                            )
                            Text("هل مقدم الخدمة نشط ويعمل حالياً؟", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }

                        errorHint?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (nameIn.trim().isEmpty() || phoneIn.trim().isEmpty() || ratingIn.trim().isEmpty()) {
                        errorHint = "يرجى تعبئة الحقول الأساسية"
                        return@Button
                    }
                    val currentRating = ratingIn.toDoubleOrNull() ?: 5.0

                    if (isNewProvider) {
                        viewModel.addServiceProvider(
                            name = nameIn.trim(),
                            phone = phoneIn.trim(),
                            categoryId = categoryIdIn,
                            rating = currentRating,
                            imageUrl = imageUrlIn.trim(),
                            isActive = isActiveIn
                        ) { ok ->
                            if (ok) {
                                showUpsertProviderDialogTarget = null
                                Toast.makeText(context, "تم إضافة مقدم الخدمة", Toast.LENGTH_SHORT).show()
                            } else {
                                errorHint = "فشل الإرسال، تحقق من الاتصال بالخادم"
                            }
                        }
                    } else {
                        provTarget.id?.let { provId ->
                            viewModel.updateServiceProvider(
                                providerId = provId,
                                name = nameIn.trim(),
                                phone = phoneIn.trim(),
                                categoryId = categoryIdIn,
                                rating = currentRating,
                                imageUrl = imageUrlIn.trim(),
                                isActive = isActiveIn
                            ) { ok ->
                                if (ok) {
                                    showUpsertProviderDialogTarget = null
                                    Toast.makeText(context, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show()
                                } else {
                                    errorHint = "فشل في تحديث بيانات العميل"
                                }
                            }
                        }
                    }
                }) {
                    Text(if (isNewProvider) "إضافة" else "حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpsertProviderDialogTarget = null }) { Text("إلغاء") }
            }
        )
    }
}
