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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (activeLanguageAr) settings.topBarTitleAr else settings.topBarTitleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
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
                        IconButton(onClick = { viewModel.performSyncWithFirestore() }) {
                            Icon(Icons.Default.Refresh, "Refresh database")
                        }
                    }
                    if (settings.showLanguageIcon) {
                        IconButton(onClick = { activeLanguageAr = !activeLanguageAr }) {
                            Icon(Icons.Default.Translate, "Switch language")
                        }
                    }
                    if (settings.showThemeToggleIcon) {
                        IconButton(onClick = {
                            val nextTheme = when (settings.appTheme) {
                                "Cosmic Slate" -> "Charcoal Gold"
                                "Charcoal Gold" -> "Royal Emerald"
                                else -> "Cosmic Slate"
                            }
                            viewModel.changeThemePreference(nextTheme, settings.primaryColorHex)
                        }) {
                            Icon(Icons.Default.Palette, "Toggle Theme")
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
        // Welcome Custom Header Customizable by Admin
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
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

    var customPrimaryColorField by remember { mutableStateOf(settings.primaryColorHex) }
    var bannerImgInput by remember { mutableStateOf("") }
    var bannerRedirectInput by remember { mutableStateOf("") }
    var bannerDurationSec by remember { mutableStateOf("6") }

    var backupFolderInput by remember { mutableStateOf("/storage/emulated/0/DaliliBackups/") }

    var selectedAdminOption by remember { mutableStateOf("themes") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        item {
            Text(
                text = if (isAr) "🛠️ لوحة تحكم الإدارة العليا ومراقبة السلوك" else "🛠️ High Command Administration Dashboard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Sub tabs for clean visual architecture
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val menu = listOf(
                    "themes" to (if (isAr) "السمات العامة" else "Themes"),
                    "banners" to (if (isAr) "الإعلانات" else "Banners"),
                    "security" to (if (isAr) "التوثيق" else "Verifications"),
                    "backup" to (if (isAr) "النسخ الاحتياطي" else "Backups"),
                    "chats" to (if (isAr) "مراقبة الدردشات" else "Chats Audit"),
                    "logs" to (if (isAr) "سجل العمليات" else "Action Logs")
                )
                menu.forEach { (key, label) ->
                    Box(
                        modifier = Modifier
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
            }

            "banners" -> item {
                // Manage banner ads and sliders duration
                Text(if (isAr) "توزيع وإزاحة لوحات الإعلانات الممولة" else "Promoted Advertising Slots control panel", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = bannerImgInput,
                    onValueChange = { bannerImgInput = it },
                    placeholder = { Text("Unsplash URL") },
                    label = { Text("رابط صورة اللافتة") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bannerRedirectInput,
                    onValueChange = { bannerRedirectInput = it },
                    placeholder = { Text("prov_1") },
                    label = { Text("رابط توجيه مقدم الخدمة (المعرف ID)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bannerDurationSec,
                    onValueChange = { bannerDurationSec = it },
                    placeholder = { Text("6") },
                    label = { Text("مدة العرض (بالثواني)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val secs = bannerDurationSec.toIntOrNull() ?: 5
                        if (bannerImgInput.isNotBlank()) {
                            viewModel.addNewPromotionBanner(bannerImgInput, bannerRedirectInput, secs, "Medium")
                            bannerImgInput = ""
                            bannerRedirectInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAr) "توليد وتعميم الإعلان التجاري على الأجهزة" else "Create and spread advertising slide")
                }
            }

            "security" -> {
                // User identification requests & verified status allocation review
                val pendingDocs = verifications.filter { it.status == "Pending" }
                if (pendingDocs.isEmpty()) {
                    item {
                        Text(if (isAr) "لا توجد مستندات معلقة بانتظار المراجعة القانونية." else "No pending validation document requests recorded in queue.")
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
                                Text(reqDoc.documentType, fontSize = 12.sp, color = Color.Gray)
                                Text(reqDoc.fileUrl, fontSize = 11.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { viewModel.verifyProviderStatus(reqDoc.providerId, true) }) {
                                        Text(if (isAr) "توثيق الحساب" else "Certify Verified Badge")
                                    }
                                    Button(
                                        onClick = { viewModel.verifyProviderStatus(reqDoc.providerId, false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) {
                                        Text(if (isAr) "رفض" else "Dismiss")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "backup" -> item {
                // Scheduled task folder back-up simulations
                Text(if (isAr) "تنزيل نسخة احتياطية محلية لقاعدة المعطيات" else "Create daily offline Firestore full snapshot backups", fontWeight = FontWeight.Bold)
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
