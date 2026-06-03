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
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.draw.scale
import com.example.data.*
import com.example.ui.DaliliTheme
import com.example.ui.DaliliViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: DaliliViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by viewModel.settings.collectAsState()
            DaliliTheme(themeName = settings.appTheme, customPrimaryColorHex = settings.primaryColorHex) {
                MainAppHost(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppHost(viewModel: DaliliViewModel) {
    val settings by viewModel.settings.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentRole by viewModel.currentUserRole.collectAsState()
    val userEmail by viewModel.currentUserEmail.collectAsState()
    val userPoints by viewModel.userPoints.collectAsState()

    var activeLanguageAr by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (activeLanguageAr) settings.topBarTitleAr else settings.topBarTitleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = if (activeLanguageAr) "دليل الموثوقين الموحد" else "Unified Services Almanac",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        if (settings.showRefreshIcon) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.performSyncWithFirestore() }
                                    .padding(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.Refresh, "Refresh database", modifier = Modifier.size(20.dp))
                                Text(
                                    text = if (activeLanguageAr) settings.refreshIconTitleAr else settings.refreshIconTitleEn,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (settings.showLanguageIcon) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { activeLanguageAr = !activeLanguageAr }
                                    .padding(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.Translate, "Switch language", modifier = Modifier.size(20.dp))
                                Text(
                                    text = if (activeLanguageAr) settings.languageIconTitleAr else settings.languageIconTitleEn,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (settings.showThemeToggleIcon) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        val nextTheme = when (settings.appTheme) {
                                            "Cosmic Slate" -> "Charcoal Gold"
                                            "Charcoal Gold" -> "Royal Emerald"
                                            else -> "Cosmic Slate"
                                        }
                                        viewModel.changeThemePreference(nextTheme, settings.primaryColorHex)
                                    }
                                    .padding(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.Palette, "Toggle Theme", modifier = Modifier.size(20.dp))
                                Text(
                                    text = if (activeLanguageAr) "السمة" else "Theme",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    val items = listOf(
                        Triple("home", Icons.Default.Home, if (activeLanguageAr) "الرئيسية" else "Home"),
                        Triple("providers_list", Icons.Default.Search, if (activeLanguageAr) "الخدمات" else "Providers"),
                        Triple("smart_bot", Icons.Default.SupportAgent, if (activeLanguageAr) "المساعد" else "Bot"),
                        Triple("invoices", Icons.Default.ReceiptLong, if (activeLanguageAr) "الفواتير" else "Invoices"),
                        Triple("admin_panel", Icons.Default.AdminPanelSettings, if (activeLanguageAr) "الإدارة" else "Admin")
                    )
                    items.forEach { (route, icon, label) ->
                        // Hide favorites from bottom list as it has been omitted as per specs: "واخفاء ايقونه المفضله من الشريط السفلي"
                        NavigationBarItem(
                            selected = currentScreen == route,
                            onClick = { viewModel.navigateTo(route) },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 11.sp, maxLines = 1) }
                        )
                    }
                }
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Connection State Indicator
                    SyncStateHeader(isOnline = isOnline, isAr = activeLanguageAr, viewModel = viewModel)

                    // Navigation Routing switch
                    Box(modifier = Modifier.weight(1f)) {
                        when (currentScreen) {
                            "home" -> HomeScreen(viewModel, activeLanguageAr)
                            "providers_list" -> ProvidersListScreen(viewModel, activeLanguageAr)
                            "provider_detail" -> ProviderDetailScreen(viewModel, activeLanguageAr)
                            "chat_screen" -> ChatScreen(viewModel, activeLanguageAr)
                            "invoices" -> InvoicesScreen(viewModel, activeLanguageAr)
                            "admin_panel" -> AdminPanelScreen(viewModel, activeLanguageAr)
                            "smart_bot" -> SmartBotScreen(viewModel, activeLanguageAr)
                            else -> HomeScreen(viewModel, activeLanguageAr)
                        }
                    }
                }
            }
        )

        // Custom Smart AI Floating Assistant overlay
        if (settings.showAssistant) {
            val assistantIcon = when (settings.assistantIconName) {
                "Chat" -> Icons.Default.Chat
                "Help" -> Icons.Default.Help
                "Support" -> Icons.Default.Support
                else -> Icons.Default.SupportAgent
            }
            
            val alignment = when (settings.assistantPosition) {
                "BottomLeft" -> Alignment.BottomStart
                "TopRight" -> Alignment.TopEnd
                "TopLeft" -> Alignment.TopStart
                else -> Alignment.BottomEnd // BottomRight
            }
            
            // Padding with logical layout clearances
            val offsetModifier = when (settings.assistantPosition) {
                "BottomLeft" -> Modifier.align(alignment).padding(start = 20.dp, bottom = 100.dp)
                "TopRight" -> Modifier.align(alignment).padding(end = 20.dp, top = 90.dp)
                "TopLeft" -> Modifier.align(alignment).padding(start = 20.dp, top = 90.dp)
                else -> Modifier.align(alignment).padding(end = 20.dp, bottom = 100.dp)
            }
            
            Box(
                modifier = offsetModifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { viewModel.navigateTo("smart_bot") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    assistantIcon,
                    contentDescription = "AI Floating Assistant",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Customizable WAM777 Backdoor Gateway Control Footer
        if (settings.showFooter) {
            val footerAlign = if (settings.footerPosition == "Top") Alignment.TopCenter else Alignment.BottomCenter
            val footerPadding = if (settings.footerPosition == "Top") PaddingValues(top = 80.dp) else PaddingValues(bottom = 85.dp)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(footerPadding)
                    .align(footerAlign),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = settings.footerText,
                    fontSize = settings.footerSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f), shape = RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo("admin_panel") } // Direct Backdoor interface action shortcut
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Shared Layout Headers & Widgets
// -------------------------------------------------------------
@Composable
fun SyncStateHeader(isOnline: Boolean, isAr: Boolean, viewModel: DaliliViewModel) {
    val isSaving by viewModel.isDataSavingMode.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isOnline) Color(0xFF16A34A) else Color(0xFFDC2626))
            .padding(vertical = 4.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isOnline) {
                    if (isAr) "متصل ومزامن بالكامل مع Firestore" else "Online & completely synced with Firestore"
                } else {
                    if (isAr) "وضع الأوفلاين - القراءة من كاش الهاتف" else "Offline - Reading from local Room Cache"
                },
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isAr) "توفير البيانات" else "Data Save",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
            Switch(
                checked = isSaving,
                onCheckedChange = { viewModel.toggleDataSavingMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.DarkGray
                ),
                modifier = Modifier.scale(0.6f)
            )
            IconButton(
                onClick = { viewModel.setOnlineStatus(!isOnline) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = "Simulate Offline Toggle",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Helper image loader matching data-save parameters
@Composable
fun NiceImage(url: String, contentDescription: String, modifier: Modifier, dataSaving: Boolean) {
    val req = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(true)
        .let { if (dataSaving) it.size(80) else it }
        .build()

    AsyncImage(
        model = req,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

// -------------------------------------------------------------
// 1. HOME SCREEN (With Slideshow, Custom Text Panel, Recommended Category)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(viewModel: DaliliViewModel, isAr: Boolean) {
    val settings by viewModel.settings.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val isSaving by viewModel.isDataSavingMode.collectAsState()

    val suggestedProviders = viewModel.getSuggestedBasedOnActivity()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Welcome Custom Header Customizable by Admin (Tapping 5 times invokes secure backdoor)
        item {
            val context = LocalContext.current
            var tapCount by remember { mutableStateOf(0) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        tapCount++
                        if (tapCount >= 5) {
                            tapCount = 0
                            viewModel.attemptLogin("WAM2026", "maher736462", true)
                            android.widget.Toast.makeText(context, if (isAr) "عبر القناة السرية للآدمن WAM2026 تم الدخول بنجاح!" else "Bypassed securely to WAM2026 Admin Panel View!", android.widget.Toast.LENGTH_LONG).show()
                            viewModel.navigateTo("admin_panel")
                        }
                    }
            ) {
                if (settings.welcomeBgUrl.isNotBlank()) {
                    NiceImage(
                        url = settings.welcomeBgUrl,
                        contentDescription = "Welcome background",
                        modifier = Modifier.fillMaxSize(),
                        dataSaving = isSaving
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAr) settings.welcomeText else settings.welcomeTextEn,
                        fontSize = settings.welcomeTextSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = try {
                            Color(android.graphics.Color.parseColor(settings.welcomeTextColorHex))
                        } catch (_: Exception) {
                            Color.White
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Action Banners carousel rotating via seconds specification
        if (banners.isNotEmpty()) {
            item {
                Text(
                    text = if (isAr) "العروض والاعلانات المتميزة" else "Premium Ads & Announcements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                var bIndex by remember { mutableStateOf(0) }
                val currentAd = banners[bIndex % banners.size]

                LaunchedEffect(bIndex, currentAd) {
                    delay(currentAd.durationSeconds * 1000L)
                    bIndex++
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable { viewModel.selectProvider(currentAd.redirectLink) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        NiceImage(
                            url = currentAd.imageUrl,
                            contentDescription = "Slide banner advertisement",
                            modifier = Modifier.fillMaxSize(),
                            dataSaving = isSaving
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (currentAd.size == "Large") "رعاية ذهبية" else "عرض ترويجي",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Categories original horizontal/grid Display
        item {
            Text(
                text = if (isAr) "تصفح حسب الفئات والمهن" else "Browse Category & Professions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            val pinnedFirst = categories.sortedByDescending { it.isPinned }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pinnedFirst.forEach { category ->
                    val isPinned = category.isPinned
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isPinned) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.selectCategory(category.id) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPinned) Icons.Default.PushPin else Icons.Default.Category,
                                contentDescription = "Category item",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) category.nameAr else category.nameEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Smart Category Recommendation "مقترح لك" according to visits counts
        item {
            Text(
                text = if (isAr) "🎯 مقترح خصيصاً لك (بناءً على نشاطك)" else "🎯 Personalized for You (Based on activity)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        if (suggestedProviders.isEmpty()) {
            item {
                Text(
                    text = if (isAr) "تصفح المهن لتوليد اقتراحات ذكية وفورية" else "Browse professions list to generate tailored recommendations.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(suggestedProviders) { provider ->
                MiniProviderCard(provider, viewModel, isAr, isSaving)
            }
        }

        // Sharing action loyalty reward
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Share, "Share application", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = if (isAr) "شارك التطبيق لربح 20 نقطة" else "Share App for 20 Coins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isAr) "انشر دليل دليلي مع أصدقائك للحصول على مكافآت فورية." else "Gift rewards by sharing local directory with peers.",
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { viewModel.shareAppAction() },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isAr) "مشاركة" else "Share", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MiniProviderCard(provider: Provider, viewModel: DaliliViewModel, isAr: Boolean, isSaving: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { viewModel.selectProvider(provider.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box {
                NiceImage(
                    url = provider.personalPhotoUrl,
                    contentDescription = "Suggested expert pic",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    dataSaving = isSaving
                )
                if (provider.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(2.dp, 2.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3B82F6))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Verified badge icon",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp).align(Alignment.Center)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = "${provider.city} • ${provider.neighborhood}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, "Rating star", tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                Text(text = " ${provider.rating}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// 2. PROVIDERS LIST SCREEN (With Circles Radius, Infinite Scroll)
// -------------------------------------------------------------
@Composable
fun ProvidersListScreen(viewModel: DaliliViewModel, isAr: Boolean) {
    val settings by viewModel.settings.collectAsState()
    val isSaving by viewModel.isDataSavingMode.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val city by viewModel.searchCity.collectAsState()
    val neighborhood by viewModel.searchNeighborhood.collectAsState()
    val phone by viewModel.searchPhone.collectAsState()
    val minRating by viewModel.searchRatingMin.collectAsState()
    val radiusInput by viewModel.searchRadiusInput.collectAsState()
    val pageSize by viewModel.pageSize.collectAsState()
    val pageOffset by viewModel.currentPageOffset.collectAsState()

    val filteredList = viewModel.getFilteredProviders()
    val hasMore = viewModel.hasMoreFilteredData()

    var showFiltersPanel by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // High density Advanced Search Controls
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text(if (isAr) "ابحث باسم مقدم الخدمة أو الخدمة..." else "Search by name or keyword...") },
            leadingIcon = { Icon(Icons.Default.Search, "Query Search") },
            trailingIcon = {
                IconButton(onClick = { showFiltersPanel = !showFiltersPanel }) {
                    Icon(Icons.Default.FilterList, "Advanced Filters Tab")
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        AnimatedVisibility(visible = showFiltersPanel) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { viewModel.searchCity.value = it },
                            placeholder = { Text(if (isAr) "المدينة" else "City") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = neighborhood,
                            onValueChange = { viewModel.searchNeighborhood.value = it },
                            placeholder = { Text(if (isAr) "الحي" else "Neigh") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { viewModel.searchPhone.value = it },
                            placeholder = { Text(if (isAr) "رقم الهاتف" else "Phone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = radiusInput,
                            onValueChange = { viewModel.searchRadiusInput.value = it },
                            placeholder = { Text(if (isAr) "دائرة البحث (كم) 📍" else "Radius Km 📍") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr) "الحد الأدنى للتقييم: ${minRating}★" else "Min Rating: ${minRating}★",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = minRating,
                            onValueChange = { viewModel.searchRatingMin.value = it },
                            valueRange = 0f..5f,
                            steps = 4,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Infinite Scroll providers list view
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList) { provider ->
                FullProviderCard(provider, viewModel, isAr, isSaving)
            }

            // Infinite Scrolling triggering button at bottom
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (hasMore) {
                        Button(
                            onClick = { viewModel.loadNextPage() },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isAr) "عرض المزيد من مقدمي الخدمات ➔" else "Load Next page page size ➔")
                        }
                    } else {
                        Text(
                            text = if (isAr) "تم تحميل جميع البيانات المتوفرة" else "All available records loaded",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FullProviderCard(provider: Provider, viewModel: DaliliViewModel, isAr: Boolean, isSaving: Boolean) {
    val context = LocalContext.current
    val isPremium = provider.isPremium

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.selectProvider(provider.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (isPremium) BorderStroke(2.dp, Color(0xFFD4AF37)) else null // Gold border for Premium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    NiceImage(
                        url = provider.personalPhotoUrl,
                        contentDescription = "Expert Profile image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        dataSaving = isSaving
                    )
                    if (provider.isVerified) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(4.dp, 4.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B82F6))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified badge icon",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp).align(Alignment.Center)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = provider.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isPremium) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFD4AF37))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (isAr) "ذهبي" else "Premium",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Text(
                        text = "📱 ${provider.phone} • ${provider.city}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                        Text(
                            text = " ${provider.rating} (${provider.reviewCount} ${if (isAr) "تقييم" else "reviews"})",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Phone, "Call icon", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isAr) "اتصل الآن" else "Call Now", fontSize = 11.sp)
                }

                Button(
                    onClick = { viewModel.openChatWith(provider.id) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Chat, "Chat icon", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isAr) "محادثة فورية" else "Live Chat", fontSize = 11.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. PROVIDER DETAIL SCREEN (Form to Review, Redeem Coins, Appointments)
// -------------------------------------------------------------
@Composable
fun ProviderDetailScreen(viewModel: DaliliViewModel, isAr: Boolean) {
    val isSaving by viewModel.isDataSavingMode.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val targetId by viewModel.selectedProviderId.collectAsState()
    val myPoints by viewModel.userPoints.collectAsState()

    val provider = providers.find { it.id == targetId } ?: return

    var clientName by remember { mutableStateOf("") }
    var ratingChosen by remember { mutableStateOf(5f) }
    var reviewComment by remember { mutableStateOf("") }

    var apptChosenDate by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        item {
            // Profile & Workspace Showcase banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)
            ) {
                NiceImage(
                    url = provider.workspacePhotoUrl,
                    contentDescription = "Workspace catalog display",
                    modifier = Modifier.fillMaxSize(),
                    dataSaving = isSaving
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Professional description & Verification labels
            Row(verticalAlignment = Alignment.CenterVertically) {
                NiceImage(
                    url = provider.personalPhotoUrl,
                    contentDescription = "Expert avatar thumbnail",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    dataSaving = isSaving
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (provider.isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Verified, "Verified Badge symbol", tint = Color(0xFF3B82F6))
                        }
                    }
                    Text(
                        text = "📍 ${provider.city} • ${provider.neighborhood}",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // LOYALTY POINTS REDEEM CORNER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isAr) "🎁 مركز استبدال نقاط الولاء" else "🎁 Loyalty Coins Redeem Center",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) {
                            "نقاطك الحالية: $myPoints نقطة المتبقية. يتطلب الخصم ${provider.pointsRedeemOption} نقطة."
                        } else {
                            "Your current score is: $myPoints coins inside ledger. Requires ${provider.pointsRedeemOption} coins for discount."
                        },
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.redeemGiftPoints(provider.id) },
                        enabled = myPoints >= provider.pointsRedeemOption,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isAr) "استبدال كوبون خصم 15%" else "Redeem discount Coupon (15%)")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // BOOK APPOINTMENTS FORM
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isAr) "📅 جدولة حية وحجز موعد مباشر" else "📅 Book Direct Expert Appointment",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = apptChosenDate,
                        onValueChange = { apptChosenDate = it },
                        placeholder = { Text(if (isAr) "أدخل تاريخ ووقت الموعد (مثلاً غداً 4م)" else "Input meeting slot (Ex: Sat 4pm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (apptChosenDate.isNotBlank()) {
                                viewModel.bookAppointment(provider.id, apptChosenDate, "Scheduled Appointment")
                                apptChosenDate = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isAr) "تأكيد حجز الموعد الفوري " else "Confirm Smart Booking Slot")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // SUBMIT DETAILED REVIEW OR FEEDBACK (Loyalty mechanism triggers points!)
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isAr) "✍️ شارك تقييمك لربح 15 نقطة!" else "✍️ Rate & Comment for 15 Coins!",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        placeholder = { Text(if (isAr) "اسمك الكامل" else "Your Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isAr) "التقييم بالنجوم: " else "Stars choice: ")
                        Slider(
                            value = ratingChosen,
                            onValueChange = { ratingChosen = it },
                            valueRange = 1f..5f,
                            steps = 3,
                            modifier = Modifier.weight(1f)
                        )
                        Text(" ${ratingChosen.toInt()}★", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        placeholder = { Text(if (isAr) "اكتب تعليقاً ووصفاً لتجربتك الفنية الحقيقية..." else "Describe product craftsmanship or technical support experience...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (clientName.isNotBlank() && reviewComment.isNotBlank()) {
                                viewModel.submitReview(provider.id, clientName, ratingChosen, reviewComment)
                                clientName = ""
                                reviewComment = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isAr) "إرسال التقييم واستباق المكافآت" else "Submit review rating")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // REVIEWS & FEEDBACK COMPENDIUM
        item {
            Text(
                text = if (isAr) "سجل آراء وتقييمات العملاء" else "Historical Reviews directory",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        val targetReviews = reviews.filter { it.providerId == provider.id }
        if (targetReviews.isEmpty()) {
            item {
                Text(
                    text = if (isAr) "لا يوجد تعليقات سابقة حتى الآن لهذا الحساب." else "No reviews recorded yet for this provider.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(targetReviews) { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("★ ${review.rating}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(review.comment, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. CHAT SYSTEM SCREEN (Supervised by Admin)
// -------------------------------------------------------------
@Composable
fun ChatScreen(viewModel: DaliliViewModel, isAr: Boolean) {
    val currentRoom by viewModel.activeRoomId.collectAsState()
    val chatRooms by viewModel.chatRooms.collectAsState()
    val messagesList = viewModel.getActiveRoomMessages()

    val roomInfo = chatRooms.find { it.id == currentRoom } ?: return

    var chatTextInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Chat Header with status indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.Default.Person,
                        "Avatar of provider",
                        modifier = Modifier.align(Alignment.Center),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(roomInfo.providerName, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (roomInfo.isMuted) "تم كتمك بواسطة الإدارة ⚠️" else "نشط الآن",
                        color = if (roomInfo.isMuted) Color.Red else Color.Green,
                        fontSize = 11.sp
                    )
                }
            }

            Row {
                IconButton(onClick = { viewModel.navigateTo("providers_list") }) {
                    Icon(Icons.Default.ArrowBack, "Back to service providers")
                }
            }
        }

        // Messages list space
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
        ) {
            items(messagesList) { msg ->
                val isSelf = msg.senderId == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelf) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.widthIn(max = 260.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = msg.senderName,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(msg.message, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Interactive chat text controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chatTextInput,
                onValueChange = { chatTextInput = it },
                placeholder = { Text(if (roomInfo.isMuted) "تم إيقاف المراسلة كإجراء تأديب مؤقت" else "اكتب تفاصيل استفسارك الفني...") },
                enabled = !roomInfo.isMuted,
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (chatTextInput.isNotBlank()) {
                        viewModel.sendChatMessage(chatTextInput)
                        chatTextInput = ""
                    }
                },
                enabled = !roomInfo.isMuted && chatTextInput.isNotBlank()
            ) {
                Icon(Icons.Default.Send, "Send chat dialog")
            }
        }
    }
}

// -------------------------------------------------------------
// 5. INVOICES SCREEN (Check Pending bills & Pay option)
// -------------------------------------------------------------
@Composable
fun InvoicesScreen(viewModel: DaliliViewModel, isAr: Boolean) {
    val invoicesList by viewModel.invoices.collectAsState()
    val mailAddress by viewModel.currentUserEmail.collectAsState()

    var userMailInput by remember { mutableStateOf(mailAddress) }
    var serviceSumDigit by remember { mutableStateOf("") }
    var serviceNotesInput by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        item {
            Text(
                text = if (isAr) "📝 الفواتير والذمم المالية الصادرة" else "📝 Issued Digital Bills",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) "إدارة فواتير ومعاملات الصيانة" else "Track financial logs of service providers transactions",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // PROVIDER ACTION CARD TO CREATE INVOICE
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isAr) "➕ إصدار فاتورة فنية جديدة للعميل" else "➕ Issue New Digital Bill to user",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = userMailInput,
                        onValueChange = { userMailInput = it },
                        placeholder = { Text(if (isAr) "عنوان بريد العميل" else "User Email address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = serviceSumDigit,
                            onValueChange = { serviceSumDigit = it },
                            placeholder = { Text(if (isAr) "القيمة مالي (دينار)" else "Amount (JOD)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = serviceNotesInput,
                            onValueChange = { serviceNotesInput = it },
                            placeholder = { Text(if (isAr) "أعمال فنية منجزة" else "Service items done") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val sum = serviceSumDigit.toDoubleOrNull() ?: 10.0
                            if (userMailInput.isNotBlank() && serviceNotesInput.isNotBlank()) {
                                viewModel.providerCreateInvoice(userMailInput, sum, serviceNotesInput)
                                serviceSumDigit = ""
                                serviceNotesInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isAr) "إصدار وحفظ الفاتورة الرقمية الحية" else "Issue and record bill invoice")
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        val myInvoices = invoicesList.filter { it.userEmail.equals(mailAddress, ignoreCase = true) }
        if (myInvoices.isEmpty()) {
            item {
                Text(
                    text = if (isAr) "لا يوجد أي فواتير ذمم مستحقة الدفع لك حالياً." else "You do not have any pending bills or historical invoices.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(myInvoices) { bill ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(bill.providerName, fontWeight = FontWeight.Bold)
                            Text(bill.serviceDetails, fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "المبلغ: ${bill.amount} JOD • الحالة: ${bill.status}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bill.status == "Paid") Color(0xFF16A34A) else Color(0xFFF59E0B)
                            )
                        }

                        if (bill.status == "Pending") {
                            Button(
                                onClick = { viewModel.payInvoice(bill.id) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isAr) "سداد الآن" else "Pay Now", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. SMART AI BOT SCREEN (Works Online/Offline, Pre-saved FAQs)
// -------------------------------------------------------------
@Composable
fun SmartBotScreen(viewModel: DaliliViewModel, isAr: Boolean) {
    val messages by viewModel.smartAssistantMessages.collectAsState()
    var userPromptText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = if (isAr) "🤖 المساعد الفني لخدمات دليلي" else "🤖 Dalili Directory Smart Bot",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isAr) "يجيبك في ثوانٍ! يعمل أونلاين لمطابقة أسئلة Firestore ومحلياً لمساعدتك." else "Gives instant replies using verified Firestore manuals offline and online",
                    fontSize = 11.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
        ) {
            items(messages) { msg ->
                val isBot = msg.senderId == "assistant"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isBot) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.message,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userPromptText,
                onValueChange = { userPromptText = it },
                placeholder = { Text(if (isAr) "مثال: كيف أوثق السجل التجاري الحرفي؟" else "Ask: How to verify account details?") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (userPromptText.isNotBlank()) {
                        viewModel.sendSmartAssistantMessage(userPromptText)
                        userPromptText = ""
                    }
                },
                enabled = userPromptText.isNotBlank()
            ) {
                Icon(Icons.Default.Send, "Submit prompt")
            }
        }
    }
}

// -------------------------------------------------------------
// 7. ADMIN PANEL PANEL (Themes, Verifications, Banners, Backups)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminPanelScreen(viewModel: DaliliViewModel, isAr: Boolean) {
    val settings by viewModel.settings.collectAsState()
    val providersList by viewModel.providers.collectAsState()
    val verifications by viewModel.verifications.collectAsState()
    val chatRooms by viewModel.chatRooms.collectAsState()
    val auditLogs by viewModel.activityLogs.collectAsState()
    val currentRole by viewModel.currentUserRole.collectAsState()

    var customPrimaryColorField by remember { mutableStateOf(settings.primaryColorHex) }
    var bannerImgInput by remember { mutableStateOf("") }
    var bannerRedirectInput by remember { mutableStateOf("") }
    var bannerDurationSec by remember { mutableStateOf("6") }

    var backupFolderInput by remember { mutableStateOf("/storage/emulated/0/DaliliBackups/") }

    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var rememberMeState by remember { mutableStateOf(false) }
    var loginErrorState by remember { mutableStateOf("") }

    if (currentRole == "Guest") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Admin Area Guard",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isAr) "دخول الإدارة واللوحة الرقابية" else "Admin & Supervisor Authentication",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text(if (isAr) "اسم المستخدم" else "Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text(if (isAr) "كلمة المرور" else "Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    
                    if (loginErrorState.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loginErrorState,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMeState,
                            onCheckedChange = { rememberMeState = it }
                        )
                        Text(
                            text = if (isAr) "حفظ تسجيل الدخول (سنتذكر الجلسة)" else "Remember Login State",
                            fontSize = 13.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            val success = viewModel.attemptLogin(usernameInput, passwordInput, rememberMeState)
                            if (success) {
                                loginErrorState = ""
                            } else {
                                loginErrorState = if (isAr) "اسم المستخدم أو كلمة المرور غير صحيحة!" else "Incorrect credentials entered!"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isAr) "تسجيل الدخول الآمن" else "Secure Admin Login")
                    }
                }
            }
        }
        return
    }

    // Default to security if Supervisor, otherwise themes
    var selectedAdminOption by remember { mutableStateOf(if (currentRole == "Supervisor") "security" else "themes") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "🛠️ لوحة تحكم الإدارة (${if (currentRole == "Supervisor") "مشرف" else "مدير"})" else "🛠️ Control Center (${currentRole})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isAr) "خروج" else "Logout", fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Sub tabs for clean visual architecture
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val menu = if (currentRole == "Supervisor") {
                    listOf(
                        "security" to (if (isAr) "التوثيق والقبول" else "Approvals"),
                        "add_provider" to (if (isAr) "إضافة مقدم خدمة" else "Add Provider")
                    )
                } else {
                    listOf(
                        "themes" to (if (isAr) "الواجهة والسمات" else "System Interface"),
                        "banners" to (if (isAr) "الإعلانات" else "Banners Slider"),
                        "security" to (if (isAr) "التوثيق والقبول" else "Verifications"),
                        "supervisors" to (if (isAr) "أعضاء الإشراف" else "Supervisors"),
                        "backup" to (if (isAr) "النسخ الاحتياطي" else "Backups"),
                        "chats" to (if (isAr) "مراقبة الدردشات" else "Chats Audit"),
                        "logs" to (if (isAr) "سجل العمليات" else "Action Logs")
                    )
                }
                menu.forEach { (key, label) ->
                    Box(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedAdminOption == key) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedAdminOption = key }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Sub control sections
        when (selectedAdminOption) {
            "themes" -> item {
                // Application central themes customizable options
                Text(if (isAr) "تخصيص ألوان وسمة الأجهزة فورياً" else "Instant Color synchronicity preferences", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                val themes = listOf("Cosmic Slate", "Charcoal Gold", "Royal Emerald")
                themes.forEach { t ->
                    Button(
                        onClick = { viewModel.changeThemePreference(t, customPrimaryColorField) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (settings.appTheme == t) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(t)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customPrimaryColorField,
                    onValueChange = { customPrimaryColorField = it },
                    placeholder = { Text("مثال: Hex #FF5500") },
                    label = { Text(if (isAr) "الرمز السداسي للون الأساسي للأجهزة" else "Primary color hex") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Greeting text modifier
                var welcomeArText by remember { mutableStateOf(settings.welcomeText) }
                var welcomeEnText by remember { mutableStateOf(settings.welcomeTextEn) }
                var welcomeSize by remember { mutableStateOf(settings.welcomeTextSize.toString()) }
                var welcomeColor by remember { mutableStateOf(settings.welcomeTextColorHex) }
                var welcomeBgUrl by remember { mutableStateOf(settings.welcomeBgUrl) }
                
                Text(if (isAr) "تعديل الشعار الترحيبي العائم" else "Modify Welcome Banner", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = welcomeArText,
                    onValueChange = { welcomeArText = it },
                    label = { Text("نص الترحيب (عربي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = welcomeEnText,
                    onValueChange = { welcomeEnText = it },
                    label = { Text("نص الترحيب (إنجليزي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = welcomeSize,
                    onValueChange = { welcomeSize = it },
                    label = { Text("حجم الخط (sp)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = welcomeColor,
                    onValueChange = { welcomeColor = it },
                    label = { Text("لون الخط (Hex)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = welcomeBgUrl,
                    onValueChange = { welcomeBgUrl = it },
                    label = { Text("رابط الصورة الخلفية من الهاتف/الإنترنت") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val sizeSp = welcomeSize.toFloatOrNull() ?: 22f
                        viewModel.updateGreetingBanner(welcomeArText, welcomeEnText, sizeSp, welcomeColor, welcomeBgUrl)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "حفظ الترحيب وبثه فورياً" else "Save & live stream Welcome Banner")
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Top-Bar Icons and Titles Customizer
                var topArTitle by remember { mutableStateOf(settings.topBarTitleAr) }
                var topEnTitle by remember { mutableStateOf(settings.topBarTitleEn) }
                var topRefLblAr by remember { mutableStateOf(settings.refreshIconTitleAr) }
                var topRefLblEn by remember { mutableStateOf(settings.refreshIconTitleEn) }
                var topLangLblAr by remember { mutableStateOf(settings.languageIconTitleAr) }
                var topLangLblEn by remember { mutableStateOf(settings.languageIconTitleEn) }
                var topShowRef by remember { mutableStateOf(settings.showRefreshIcon) }
                var topShowLang by remember { mutableStateOf(settings.showLanguageIcon) }

                Text(if (isAr) "تخصيص أيقونات وأزرار الشريط العلوي" else "Customize Top-Bar Icons & Labels", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = topArTitle,
                    onValueChange = { topArTitle = it },
                    label = { Text("عنوان التطبيق العلوي (عربي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = topEnTitle,
                    onValueChange = { topEnTitle = it },
                    label = { Text("عنوان التطبيق العلوي (إنجليزي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = topRefLblAr,
                    onValueChange = { topRefLblAr = it },
                    label = { Text("نص تحت زر التحديث (عربي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = topRefLblEn,
                    onValueChange = { topRefLblEn = it },
                    label = { Text("نص تحت زر التحديث (إنجليزي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = topLangLblAr,
                    onValueChange = { topLangLblAr = it },
                    label = { Text("نص تحت زر اللغة (عربي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = topLangLblEn,
                    onValueChange = { topLangLblEn = it },
                    label = { Text("نص تحت زر اللغة (إنجليزي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = topShowRef, onCheckedChange = { topShowRef = it })
                    Text("عرض زر التحديث")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = topShowLang, onCheckedChange = { topShowLang = it })
                    Text("عرض زر تبديل اللغة")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.updateTopBarOptions(
                            topShowRef, topShowLang, settings.showThemeToggleIcon,
                            topArTitle, topEnTitle,
                            topRefLblAr, topRefLblEn,
                            topLangLblAr, topLangLblEn
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "توزيع وتسمية أزرار الشريط العلوي" else "Apply Top-Bar Changes & Labels")
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Floating Smart Assistant configuration and placement
                var assistShow by remember { mutableStateOf(settings.showAssistant) }
                var assistIconName by remember { mutableStateOf(settings.assistantIconName) }
                var assistPos by remember { mutableStateOf(settings.assistantPosition) }

                Text(if (isAr) "التحكم بالمساعد الذكي العائم" else "Smart Assistant Button Settings", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = assistShow, onCheckedChange = { assistShow = it })
                    Text("إظهار المساعد العائم في الشاشات")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("شكل الأيقونة:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                val iconsLst = listOf("SupportAgent", "Chat", "Help", "Support")
                iconsLst.forEach { ic ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { assistIconName = ic }.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = assistIconName == ic, onClick = { assistIconName = ic })
                        Text(ic, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("موقع الأيقونة على الشاشة:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                val positionsLst = listOf("BottomRight", "BottomLeft", "TopRight", "TopLeft")
                positionsLst.forEach { pos ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { assistPos = pos }.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = assistPos == pos, onClick = { assistPos = pos })
                        Text(pos, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.updateAssistantConfig(assistShow, assistIconName, assistPos) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "حفظ موقع وشكل المساعد الذكي" else "Save Smart Assistant Settings")
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // WAM777 Footer controls
                var footerShow by remember { mutableStateOf(settings.showFooter) }
                var footerTxt by remember { mutableStateOf(settings.footerText) }
                var footerSzText by remember { mutableStateOf(settings.footerSize.toString()) }
                var footerPositionState by remember { mutableStateOf(settings.footerPosition) }

                Text(if (isAr) "التحكم في تذييل WAM777 وتحجيمه ومكانه" else "WAM777 Footer Configuration Panel", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = footerShow, onCheckedChange = { footerShow = it })
                    Text("إظهار التذييل في الشاشة")
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = footerTxt,
                    onValueChange = { footerTxt = it },
                    label = { Text("النص المكتوب بالتذييل") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = footerSzText,
                    onValueChange = { footerSzText = it },
                    label = { Text("حجم خط التذييل (sp)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("موقع التذييل:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                val footerPositions = listOf("Bottom", "Top")
                footerPositions.forEach { pos ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { footerPositionState = pos }.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = footerPositionState == pos, onClick = { footerPositionState = pos })
                        Text(pos, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val sz = footerSzText.toFloatOrNull() ?: 14f
                        viewModel.updateFooterConfig(footerShow, footerTxt, sz, footerPositionState)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "تخصيص وحفظ تذييل الجهاز" else "Save Footer Settings")
                }
            }

            "banners" -> item {
                // Manage banner ads and sliders duration
                Text(if (isAr) "إدارة اللافتات الإعلانية (Banners) في أعلى الصفحة" else "Advertising Banners Control Panel", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = bannerImgInput,
                    onValueChange = { bannerImgInput = it },
                    placeholder = { Text("مثال: https://images.unsplash.com/...") },
                    label = { Text("رابط صورة اللافتة") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bannerRedirectInput,
                    onValueChange = { bannerRedirectInput = it },
                    placeholder = { Text("مثال: ID لمزود الخدمة للتوجيه") },
                    label = { Text("رابط توجيه المستخدم عند الضغط (معرف مقدم الخدمة)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bannerDurationSec,
                    onValueChange = { bannerDurationSec = it },
                    placeholder = { Text("6") },
                    label = { Text("مدة العرض التلقائي (بالثواني)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val secs = bannerDurationSec.toIntOrNull() ?: 6
                        if (bannerImgInput.isNotBlank()) {
                            viewModel.addNewPromotionBanner(bannerImgInput, bannerRedirectInput, secs, "Medium")
                            bannerImgInput = ""
                            bannerRedirectInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "توليد ونشر الإعلان فورياً" else "Create and spread advertising slide")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(if (isAr) "اللافتات المنشورة الحالية:" else "Active Banners:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                val currentBanners by viewModel.banners.collectAsState()
                if (currentBanners.isEmpty()) {
                    Text(if (isAr) "لا توجد لافتات إعلانية نشطة حالياً." else "No active banner slides loaded.", color = Color.Gray, fontSize = 12.sp)
                } else {
                    currentBanners.forEach { ban ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("الموجه للمعرف: ${ban.redirectLink}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("المدة: ${ban.durationSeconds} ثواني • الصنف: ${ban.size}", fontSize = 11.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { viewModel.removePromotionBanner(ban.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(if (isAr) "حذف الإعلان" else "Remove", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            "security" -> {
                // User identification requests & verified status allocation review
                val pendingDocs = verifications.filter { it.status == "Pending" }
                if (pendingDocs.isEmpty()) {
                    item {
                        Text(if (isAr) "لا توجد مستندات توثيق معلقة متبقية." else "No pending validation document requests registered.")
                    }
                } else {
                    items(pendingDocs) { reqDoc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(reqDoc.providerName, fontWeight = FontWeight.Bold)
                                Text("نوع المستند: " + reqDoc.documentType, fontSize = 12.sp, color = Color.Gray)
                                Text("الموقع المرفق: " + reqDoc.fileUrl, fontSize = 11.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { viewModel.verifyProviderStatus(reqDoc.providerId, true) }) {
                                        Text(if (isAr) "قبول وتوثيق علامة الحساب ✔" else "Certify & Badge Account ✔")
                                    }
                                    Button(
                                        onClick = { viewModel.verifyProviderStatus(reqDoc.providerId, false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) {
                                        Text(if (isAr) "رفض المستند" else "Dismiss")
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Active Providers Table for pinning & premium controls (Only for non-Supervisor roles)
                if (currentRole != "Supervisor") {
                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(if (isAr) "توطين وتثبيت وتوصية مقدمي الخدمة:" else "Pin & Subscribe Provider Management:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(providersList) { prov ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(prov.name, fontWeight = FontWeight.Bold)
                                            if (prov.isVerified) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Verified Badge",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp).padding(start = 2.dp)
                                                )
                                            }
                                        }
                                        Text(prov.city + " • " + prov.neighborhood, fontSize = 11.sp, color = Color.Gray)
                                    }
                                    // Pinned & premium state chips
                                    Row {
                                        if (prov.isPinned) {
                                            Box(modifier = Modifier.background(Color.Blue).padding(horizontal = 4.dp, vertical = 2.dp).clip(RoundedCornerShape(4.dp))) {
                                                Text("Pinned", color = Color.White, fontSize = 9.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        if (prov.isPremium) {
                                            Box(modifier = Modifier.background(Color(0xFFF59E0B)).padding(horizontal = 4.dp, vertical = 2.dp).clip(RoundedCornerShape(4.dp))) {
                                                Text("Premium ⭐", color = Color.White, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Button(
                                        onClick = { viewModel.togglePinProvider(prov.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (prov.isPinned) Color.DarkGray else MaterialTheme.colorScheme.primary),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (prov.isPinned) (if (isAr) "إلغاء تثبيت" else "Unpin") else (if (isAr) "تثبيت بالصدارة 📌" else "Pin at Peak"), fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.toggleProviderPremium(prov.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (prov.isPremium) Color.Red else Color(0xFF16A34A)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (prov.isPremium) (if (isAr) "وقف الاشتراك المميز" else "Disable Subscription") else (if (isAr) "منح شارة مميز ⭐" else "Grant Premium Badge"), fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.deleteProvider(prov.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (isAr) "حذف المزود" else "Delete Provider", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "add_provider" -> item {
                var syncNewProvName by remember { mutableStateOf("") }
                var syncNewProvPhone by remember { mutableStateOf("") }
                var syncNewProvCity by remember { mutableStateOf("") }
                var syncNewProvNeigh by remember { mutableStateOf("") }
                var syncSelectedCatId by remember { mutableStateOf("") }
                
                val categories by viewModel.categories.collectAsState()
                
                Text(if (isAr) "إضافة مقدم خدمة جديد موثق ومثبت" else "Add raw service provider to directory", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                
                OutlinedTextField(
                    value = syncNewProvName,
                    onValueChange = { syncNewProvName = it },
                    label = { Text(if (isAr) "اسم مقدم الخدمة" else "Provider Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = syncNewProvPhone,
                    onValueChange = { syncNewProvPhone = it },
                    label = { Text(if (isAr) "رقم الهاتف" else "Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = syncNewProvCity,
                    onValueChange = { syncNewProvCity = it },
                    label = { Text(if (isAr) "المدينة" else "City") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = syncNewProvNeigh,
                    onValueChange = { syncNewProvNeigh = it },
                    label = { Text(if (isAr) "الحي" else "Neighborhood") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                Text(if (isAr) "اختر القسم الوظيفي:" else "Select Category:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                categories.forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { syncSelectedCatId = cat.id }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = syncSelectedCatId == cat.id, onClick = { syncSelectedCatId = cat.id })
                        Text(if (isAr) cat.nameAr else cat.nameEn, fontSize = 13.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (syncNewProvName.isNotBlank() && syncNewProvPhone.isNotBlank() && syncSelectedCatId.isNotBlank()) {
                            viewModel.addNewApprovedProvider(
                                name = syncNewProvName,
                                phone = syncNewProvPhone,
                                catId = syncSelectedCatId,
                                city = syncNewProvCity.ifBlank { "عمان" },
                                neigh = syncNewProvNeigh.ifBlank { "الجبيهة" }
                            )
                            syncNewProvName = ""
                            syncNewProvPhone = ""
                            syncNewProvCity = ""
                            syncNewProvNeigh = ""
                            // Switch view to security
                            selectedAdminOption = "security"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "حفظ وتوثيق المزود فورياً" else "Add Approved Provider")
                }
            }

            "supervisors" -> item {
                var syncNewModEmail by remember { mutableStateOf("") }
                var syncNewModPass by remember { mutableStateOf("") }
                val moderators by viewModel.moderators.collectAsState()
                
                Text(if (isAr) "إدارة وتعيين حسابات المشرفين المزامنة" else "Manage & appoint synchronized supervisors", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                
                OutlinedTextField(
                    value = syncNewModEmail,
                    onValueChange = { syncNewModEmail = it },
                    label = { Text(if (isAr) "البريد الإلكتروني / اسم مستخدم المشرف" else "Supervisor Username / Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = syncNewModPass,
                    onValueChange = { syncNewModPass = it },
                    label = { Text(if (isAr) "كلمة مرور المشرف" else "Supervisor Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (syncNewModEmail.isNotBlank() && syncNewModPass.isNotBlank()) {
                            viewModel.addModeratorUser(syncNewModEmail, syncNewModPass)
                            syncNewModEmail = ""
                            syncNewModPass = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "تعيين وإضافة المشرف الجديد" else "Register & Appoint Supervisor")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(if (isAr) "قائمة المشرفين المعتمدين الحالية:" else "Current active supervisors in registry:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (moderators.isEmpty()) {
                    Text(if (isAr) "لا توجد مشرفين نشطين مضافين." else "No active supervisors declared.", color = Color.Gray, fontSize = 12.sp)
                } else {
                    moderators.forEach { mod ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(mod.email, fontWeight = FontWeight.Bold)
                                    Text("كلمة المرور: ${mod.passwordPlain}", fontSize = 12.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { viewModel.deleteModeratorUser(mod.email) }) {
                                    Icon(Icons.Default.Delete, "Delete Supervisor", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            "backup" -> item {
                // Scheduled task folder back-up simulations
                Text(if (isAr) "تنزيل نسخة احتياق لقاعدة المعطيات" else "Create daily offline Firestore full snapshot backups", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = backupFolderInput,
                    onValueChange = { backupFolderInput = it },
                    label = { Text("مجلد حفظ النسخ في التخزين") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewModel.triggerFirestoreBackup(backupFolderInput)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "أخذ لقطة حفظ احتياطية فورية 📥" else "Execute system Firestore backup raw snapshot 📥")
                }
            }

            "chats" -> {
                // Supervisors audit screen to mute chat rooms
                if (chatRooms.isEmpty()) {
                    item {
                        Text(if (isAr) "لا توجد غرف دردشة نشطة للمشتري والمزود للمراقبة حالياً." else "No active direct chat lines open in registry to supervise.")
                    }
                } else {
                    items(chatRooms) { r ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "مزود الخدمة: ${r.providerName}", fontWeight = FontWeight.Bold)
                                    Text(text = "العميل: ${r.userEmail}", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { viewModel.toggleChatMute(r.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (r.isMuted) Color.Green else Color.DarkGray
                                    )
                                ) {
                                    Text(
                                        if (r.isMuted) {
                                            if (isAr) "إلغاء كتم" else "Unmute"
                                        } else {
                                            if (isAr) "كتم الغرفة 🤐" else "Mute Participant"
                                        },
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "logs" -> {
                // Access admin logging records
                if (auditLogs.isEmpty()) {
                    item {
                        Text(if (isAr) "لا يوجد أي سجل فني للعمليات حالياً." else "No historical admin actions logged.")
                    }
                } else {
                    items(auditLogs) { log ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (isAr) log.actionAr else log.actionEn,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "مدير المداومة: ${log.modEmail}",
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
