package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.Category
import com.example.data.PendingProvider
import com.example.data.Review
import com.example.data.ServiceProvider
import com.example.ui.DaliliViewModel
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: DaliliViewModel = viewModel()
            val isDark by viewModel.isDark.collectAsState()
            
            // Central Application Theme (Forced dark/night mode with intense contrasts)
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color(0xFF121214),
                    surface = Color(0xFF1E1E22),
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                MainAppContainer(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: DaliliViewModel) {
    val context = LocalContext.current
    val currentScreenState = remember { mutableStateOf<Screen>(Screen.Home) }
    val lang by viewModel.language.collectAsState()
    
    // Config values
    val appName by viewModel.appName.collectAsState()
    val themeColorHex by viewModel.themeColorHex.collectAsState()
    
    // Parse primary theme color safely
    val primaryColor = remember(themeColorHex) {
        try {
            Color(android.graphics.Color.parseColor(themeColorHex))
        } catch (e: Exception) {
            Color(0xFF3F51B5) // Default indigo
        }
    }

    // Backdoor 5-tap state
    var backdoorTapCount by remember { mutableStateOf(0) }
    var showBackdoorDialog by remember { mutableStateOf(false) }
    var backdoorPasswordInput by remember { mutableStateOf("") }

    // AI Bottomsheet/Dialog Assist Chat state
    var showChatDialog by remember { mutableStateOf(false) }

    // Apply proper RTL context when Lang is Arabic
    val direction = if (lang == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Scaffold(
            topBar = {
                // Top App Bar formatted exactly per user layout: 
                // Right to Left: 🔄 Refresh -> 🌐 Lang -> 🌙 Night Theme -> ⚙️ Admin -> 👤 Provider Reg -> 🏠 Home (Backdoor)
                // Wrapped in strict RTL layout to preserve physical layout order
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    TopAppBar(
                        title = {
                            Text(
                                text = appName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF1A1A1E)
                        ),
                        actions = {
                            // 1. 🔄 (Circular arrow) → refresh page / sync database
                            IconButton(onClick = {
                                Toast.makeText(context, if(lang == "ar") "جاري تحديث البيانات..." else "Refreshing data...", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                            }
                            
                            // 2. 🌐 (Globe) → Switch Language
                            IconButton(onClick = {
                                viewModel.toggleLanguage()
                            }) {
                                Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.White)
                            }
                            
                            // 3. 🌙 (Moon) → Switch Dark Mode (Contrast toggle)
                            IconButton(onClick = {
                                viewModel.toggleDarkMode()
                                Toast.makeText(context, if(lang == "ar") "تم تبديل تباين الوضع الليلي" else "Night mode toggled", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.DarkMode, contentDescription = "Dark Mode", tint = Color.White)
                            }
                            
                            // 4. ⚙️ (Gear) → enter admins panel
                            IconButton(onClick = {
                                if (viewModel.currentUser.value != null) {
                                    currentScreenState.value = Screen.AdminDashboard
                                } else {
                                    currentScreenState.value = Screen.Login
                                }
                            }) {
                                Icon(Icons.Default.Settings, contentDescription = "Admin", tint = Color.White)
                            }
                            
                            // 5. 👤 (User icon) → Provider registration
                            IconButton(onClick = {
                                currentScreenState.value = Screen.ProviderRegister
                            }) {
                                Icon(Icons.Default.Person, contentDescription = "Provider Registration", tint = Color.White)
                            }
                            
                            // 6. 🏠 (Home logo) → Backdoor portal (Tapping 5 times triggers "maher--736462")
                            IconButton(onClick = {
                                currentScreenState.value = Screen.Home
                                backdoorTapCount++
                                if (backdoorTapCount >= 5) {
                                    backdoorTapCount = 0
                                    showBackdoorDialog = true
                                }
                            }) {
                                Icon(Icons.Default.Home, contentDescription = "Home App", tint = primaryColor)
                            }
                        }
                    )
                }
            },
            bottomBar = {
                // Persistent Footer including the custom text size and AI support bubble on bottom-left
                AppFooterSection(
                    viewModel = viewModel,
                    primaryColor = primaryColor,
                    lang = lang,
                    onAiClick = { showChatDialog = true }
                )
            },
            containerColor = Color(0xFF121214)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF121214))
            ) {
                when (val screen = currentScreenState.value) {
                    is Screen.Home -> HomeScreen(viewModel, primaryColor, lang) { cat ->
                        currentScreenState.value = Screen.CategoryDetails(cat)
                    }
                    is Screen.CategoryDetails -> CategoryDetailsScreen(screen.category, viewModel, primaryColor, lang) {
                        currentScreenState.value = Screen.Home
                    }
                    is Screen.Login -> LoginScreen(viewModel, primaryColor, lang) {
                        currentScreenState.value = Screen.AdminDashboard
                    }
                    is Screen.AdminDashboard -> AdminDashboardScreen(viewModel, primaryColor, lang) {
                        currentScreenState.value = Screen.Home
                    }
                    is Screen.ProviderRegister -> ProviderRegisterScreen(viewModel, primaryColor, lang) {
                        currentScreenState.value = Screen.Home
                    }
                }

                // 5-Tap Backdoor Password Dialog
                if (showBackdoorDialog) {
                    Dialog(onDismissRequest = { showBackdoorDialog = false }) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (lang == "ar") "البوابة الخلفية السرية" else "Secret Backdoor Portal",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = backdoorPasswordInput,
                                    onValueChange = { backdoorPasswordInput = it },
                                    label = { Text(if (lang == "ar") "كلمة المرور السرية" else "Secret Password", color = Color.White) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = primaryColor,
                                        unfocusedBorderColor = Color.White.copy(0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            if (backdoorPasswordInput == "maher--736462") {
                                                // Log in directly as admin
                                                viewModel.login("admin", "maher736462")
                                                currentScreenState.value = Screen.AdminDashboard
                                                showBackdoorDialog = false
                                                backdoorPasswordInput = ""
                                                Toast.makeText(context, if (lang == "ar") "تم فتح بوابة الإدارة الخلفية بنجاح!" else "Backdoor unlocked!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, if (lang == "ar") "كلمة مرور خاطئة!" else "Wrong password!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                                    ) {
                                        Text(if (lang == "ar") "دخول" else "Enter", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    TextButton(onClick = { showBackdoorDialog = false }) {
                                        Text(if (lang == "ar") "إلغاء" else "Cancel", color = Color.White.copy(0.6f))
                                    }
                                }
                            }
                        }
                    }
                }

                // AI Assistant Interactive Chat Overlay Dialog
                if (showChatDialog) {
                    ChatDialogOverlay(viewModel, primaryColor, lang) {
                        showChatDialog = false
                    }
                }
            }
        }
    }
}

@Composable
fun AppFooterSection(
    viewModel: DaliliViewModel,
    primaryColor: Color,
    lang: String,
    onAiClick: () -> Unit
) {
    val footerText by viewModel.footer.collectAsState()
    val showFooter by viewModel.showFooter.collectAsState()
    val showAiIcon by viewModel.showAiIcon.collectAsState()
    val aiIconSymbol by viewModel.aiIcon.collectAsState()

    if (showFooter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1E))
                .padding(vertical = 4.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Assistant Icon (Bottom Left of Footer)
                if (showAiIcon) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = primaryColor.copy(0.2f)),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onAiClick() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = aiIconSymbol.ifEmpty { "🤖" },
                                fontSize = 18.sp
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(36.dp))
                }

                // Footnote "MAW 777644670" size strictly 50% smaller (normal 14sp -> 7sp or 8sp for crisp readability)
                Text(
                    text = footerText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: DaliliViewModel,
    primaryColor: Color,
    lang: String,
    onCategoryClick: (Category) -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val serviceProviders by viewModel.serviceProviders.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val welcomeText by viewModel.welcomeText.collectAsState()
    val welcomeImage by viewModel.welcomeImage.collectAsState()

    val filteredProviders = remember(searchQuery, serviceProviders) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            serviceProviders.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero / Welcome Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    AsyncImage(
                        model = welcomeImage,
                        contentDescription = "Welcome",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                    Text(
                        text = welcomeText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Professional Search list
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { 
                    Text(
                        text = if (lang == "ar") "بحث عن مقدم خدمة أو مهنة..." else "Search provider or profession...", 
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.White.copy(0.3f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(0.7f)
                ),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Render Search results if active, with bright white and bold typography
        if (searchQuery.isNotBlank()) {
            if (filteredProviders.isEmpty()) {
                item {
                    Text(
                        text = if (lang == "ar") "لا توجد نتائج مطابقة لبحثك" else "No matching results found",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                items(filteredProviders) { provider ->
                    ServiceProviderRowListItem(provider, viewModel, primaryColor, lang)
                }
            }
        }

        // Category heading
        item {
            Text(
                text = if (lang == "ar") "تصفح الفئات الرئيسية" else "Browse main categories",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Vertical Grid layout for categories
        item {
            Box(modifier = Modifier.heightIn(max = 400.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategoryClick(cat) }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = cat.icon.ifEmpty { "📁" },
                                    fontSize = 32.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = cat.nameAr,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDetailsScreen(
    category: Category,
    viewModel: DaliliViewModel,
    primaryColor: Color,
    lang: String,
    onBack: () -> Unit
) {
    val subCategories by viewModel.subCategories.collectAsState()
    val serviceProviders by viewModel.serviceProviders.collectAsState()
    
    val catSubs = remember(category, subCategories) {
        subCategories.filter { it.parentCategoryId == category.id }
    }
    
    val catProviders = remember(category, serviceProviders) {
        serviceProviders.filter { it.categoryId == category.id }
    }

    var selectedSubId by remember { mutableStateOf<Int?>(null) }
    
    val displayedProviders = remember(selectedSubId, catProviders) {
        if (selectedSubId == null) {
            catProviders
        } else {
            catProviders.filter { it.subCategoryId == selectedSubId }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${category.icon} ${category.nameAr}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        // Subcategories filter chips
        if (catSubs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "All" chip
                    InputChip(
                        selected = selectedSubId == null,
                        onClick = { selectedSubId = null },
                        label = { Text(if (lang == "ar") "الكل" else "All", fontWeight = FontWeight.Bold, color = Color.White) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = primaryColor,
                            containerColor = Color(0xFF1E1E22)
                        )
                    )
                    
                    catSubs.forEach { sub ->
                        InputChip(
                            selected = selectedSubId == sub.id,
                            onClick = { selectedSubId = sub.id },
                            label = { Text(sub.nameAr, fontWeight = FontWeight.Bold, color = Color.White) },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = primaryColor,
                                containerColor = Color(0xFF1E1E22)
                            )
                        )
                    }
                }
            }
        }

        // Providers header
        item {
            Text(
                text = if (lang == "ar") "مقدمو الخدمات المتاحون (${displayedProviders.size})" else "Available Providers (${displayedProviders.size})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        // Providers list
        if (displayedProviders.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (lang == "ar") "لا يوجد موفروا خدمة مسجلين في هذا القسم حالياً." else "No registered operators in this section yet.",
                            color = Color.White.copy(0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            items(displayedProviders) { provider ->
                ServiceProviderRowListItem(provider, viewModel, primaryColor, lang)
            }
        }
    }
}

@Composable
fun ServiceProviderRowListItem(
    provider: ServiceProvider,
    viewModel: DaliliViewModel,
    primaryColor: Color,
    lang: String
) {
    val context = LocalContext.current
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewerName by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }
    var reviewStars by remember { mutableStateOf(5.0) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = provider.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1521791136368-1a9b7defcad8" },
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = provider.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (provider.isPinned) {
                            Text(
                                text = "📌 Sp",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📞 ${provider.phone}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.White.copy(0.1f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(Locale.US, "%.1f", provider.rating),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Call Button
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open dialer", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (lang == "ar") "اتصال 📞" else "Call", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Add review
                    OutlinedButton(
                        onClick = { showReviewDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (lang == "ar") "تقييم ✍️" else "Rate", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showReviewDialog) {
        Dialog(onDismissRequest = { showReviewDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (lang == "ar") "تقييم مقدم الخدمة" else "Review Provider",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text(if (lang == "ar") "اسمك" else "Your Name", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.White.copy(0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text(if (lang == "ar") "التعليق والتقييم" else "Comment", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.White.copy(0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(if (lang == "ar") "النجوم:" else "Stars:", color = Color.White, fontWeight = FontWeight.Bold)
                        (1..5).forEach { star ->
                            IconButton(
                                onClick = { reviewStars = star.toDouble() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star star",
                                    tint = if (star <= reviewStars) Color(0xFFFFD700) else Color.White.copy(0.3f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (reviewerName.trim().isEmpty() || reviewComment.trim().isEmpty()) {
                                    Toast.makeText(context, "الرجاء تعبئة البيانات كاملة", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.addReview(provider.id ?: 0, reviewerName, reviewComment, reviewStars) { success ->
                                        if (success) {
                                            Toast.makeText(context, "شكراً لتقييمك!", Toast.LENGTH_SHORT).show()
                                            showReviewDialog = false
                                        } else {
                                            Toast.makeText(context, "عذراً حاول مجدداً", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Text(if (lang == "ar") "حفظ" else "Save", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        TextButton(onClick = { showReviewDialog = false }) {
                            Text(if (lang == "ar") "إلغاء" else "Cancel", color = Color.White.copy(0.6f))
                        }
                    }
                }
            }
        }
    }
}

// 4. Admin Credentials Screen (admin / maher736462)
@Composable
fun LoginScreen(
    viewModel: DaliliViewModel,
    primaryColor: Color,
    lang: String,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var uname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Lock", tint = primaryColor, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (lang == "ar") "دخول المشرفين والملاك" else "Admin Login",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uname,
                    onValueChange = { uname = it },
                    label = { Text(if (lang == "ar") "اسم المستخدم" else "Username", color = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (lang == "ar") "كلمة المرور" else "Password", color = Color.White) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        val success = viewModel.login(uname, password)
                        if (success) {
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, if (lang == "ar") "خطأ في اسم المستخدم أو كلمة المرور!" else "Incorrect credentials!", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (lang == "ar") "تسجيل الدخول" else "Login", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 5. Admin Dashboard (App color configuration, pending approvals, add themes)
@Composable
fun AdminDashboardScreen(
    viewModel: DaliliViewModel,
    primaryColor: Color,
    lang: String,
    onBack: () -> Unit
) {
    val pendingList by viewModel.pendingProviders.collectAsState()
    val availableColors by viewModel.availableColors.collectAsState()
    
    // Remote states
    val remoteAppName by viewModel.appName.collectAsState()
    val remoteWelcomeText by viewModel.welcomeText.collectAsState()
    val remoteFooterText by viewModel.footer.collectAsState()
    val remoteShowFooter by viewModel.showFooter.collectAsState()
    val remoteAiIcon by viewModel.aiIcon.collectAsState()
    val remoteShowAi by viewModel.showAiIcon.collectAsState()
    val themeColorHex by viewModel.themeColorHex.collectAsState()
    
    val phoneVal by viewModel.phone.collectAsState()
    val whatsappVal by viewModel.whatsapp.collectAsState()
    val emailVal by viewModel.email.collectAsState()
    val welcomeImg by viewModel.welcomeImage.collectAsState()
    val aboutSub by viewModel.aboutAppSubtitle.collectAsState()
    val updatesUrl by viewModel.appUpdatesUrl.collectAsState()
    val shareText by viewModel.appShareText.collectAsState()

    // Form states
    var appNameInput by remember { mutableStateOf(remoteAppName) }
    var welcomeInput by remember { mutableStateOf(remoteWelcomeText) }
    var footerInput by remember { mutableStateOf(remoteFooterText) }
    var showFooterInput by remember { mutableStateOf(remoteShowFooter) }
    var aiIconInput by remember { mutableStateOf(remoteAiIcon) }
    var showAiIconInput by remember { mutableStateOf(remoteShowAi) }
    var customHexInput by remember { mutableStateOf("") }
    
    var phoneInput by remember { mutableStateOf(phoneVal) }
    var whatsappInput by remember { mutableStateOf(whatsappVal) }
    var emailInput by remember { mutableStateOf(emailVal) }
    var welcomeImgInput by remember { mutableStateOf(welcomeImg) }
    var aboutSubInput by remember { mutableStateOf(aboutSub) }
    var updatesUrlInput by remember { mutableStateOf(updatesUrl) }
    var shareTextInput by remember { mutableStateOf(shareText) }

    LaunchedEffect(remoteAppName, remoteWelcomeText, remoteFooterText, remoteShowFooter, remoteAiIcon, remoteShowAi) {
        appNameInput = remoteAppName
        welcomeInput = remoteWelcomeText
        footerInput = remoteFooterText
        showFooterInput = remoteShowFooter
        aiIconInput = remoteAiIcon
        showAiIconInput = remoteShowAi
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == "ar") "لوحة تحكم المدير الموحدة 👑" else "Admin Config Panel 👑",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Button(onClick = { viewModel.logout(); onBack() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text(if (lang == "ar") "خروج" else "Logout", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 1: Dynamic configurations synced instantly per instruction
        item {
            Text(
                text = if (lang == "ar") "تخصيص الهوية والألوان والتذييل (يزامن فورياً)" else "Sync Brand & Footer customization",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // App Name
                    OutlinedTextField(
                        value = appNameInput,
                        onValueChange = { appNameInput = it },
                        label = { Text("App Name", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Welcome Text
                    OutlinedTextField(
                        value = welcomeInput,
                        onValueChange = { welcomeInput = it },
                        label = { Text("Welcome Banner text", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Footer Text Control (Can edit "MAW 777644670")
                    OutlinedTextField(
                        value = footerInput,
                        onValueChange = { footerInput = it },
                        label = { Text("Footer Text", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Toggle Footer
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = showFooterInput, onCheckedChange = { showFooterInput = it })
                        Text("Show Footer on Screen", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // AI Assistant Icon
                    OutlinedTextField(
                        value = aiIconInput,
                        onValueChange = { aiIconInput = it },
                        label = { Text("AI Assistant Custom Icon", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Toggle AI Assistant Icon
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = showAiIconInput, onCheckedChange = { showAiIconInput = it })
                        Text("Show AI Assistant Launcher Icon", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Selections of app colors
                    Text("Select Primary Theme Accent:", color = Color.White, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableColors.forEach { colStr ->
                            val col = remember(colStr) { Color(android.graphics.Color.parseColor(colStr)) }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(col)
                                    .clickable {
                                        viewModel.saveGlobalConfig(
                                            appNameInput, welcomeInput, footerInput, showFooterInput,
                                            aiIconInput, showAiIconInput, colStr, availableColors.joinToString(","),
                                            phoneInput, whatsappInput, emailInput, aboutSubInput, updatesUrlInput,
                                            shareTextInput, welcomeImgInput
                                        ) {}
                                    }
                            )
                        }
                    }

                    // Add Custom Colors ("اضافات الوان للتطبيق يستطيع الادمن تغييرها")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = customHexInput,
                            onValueChange = { customHexInput = it },
                            placeholder = { Text("#E91E63", color = Color.White.copy(0.4f)) },
                            label = { Text("Add Hex Color", color = Color.White) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (customHexInput.startsWith("#") && customHexInput.length == 7) {
                                val updatedList = availableColors.toMutableList()
                                if (!updatedList.contains(customHexInput)) {
                                    updatedList.add(customHexInput)
                                    viewModel.saveGlobalConfig(
                                        appNameInput, welcomeInput, footerInput, showFooterInput,
                                        aiIconInput, showAiIconInput, customHexInput, updatedList.joinToString(","),
                                        phoneInput, whatsappInput, emailInput, aboutSubInput, updatesUrlInput,
                                        shareTextInput, welcomeImgInput
                                    ) {}
                                    customHexInput = ""
                                    Toast.makeText(viewModel.getApplication(), "New Color preset added!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(viewModel.getApplication(), "Insert valid #RRGGBB", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text("+")
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.saveGlobalConfig(
                                appNameInput, welcomeInput, footerInput, showFooterInput,
                                aiIconInput, showAiIconInput, themeColorHex, availableColors.joinToString(","),
                                phoneInput, whatsappInput, emailInput, aboutSubInput, updatesUrlInput,
                                shareTextInput, welcomeImgInput
                            ) { success ->
                                if (success) {
                                    Toast.makeText(viewModel.getApplication(), "Config Saved & Synced successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(viewModel.getApplication(), "Error syncing config", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (lang == "ar") "حفظ ومزامنة الهوية" else "Save & Sync Configuration", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 2: Manage Pending Professionals registrations
        item {
            Text(
                text = if (lang == "ar") "طلبات تسجيل أصحاب المهن المعلقة (${pendingList.size})" else "Pending Professionals registrations (${pendingList.size})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (pendingList.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No pending register requests.", color = Color.White.copy(0.6f), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(pendingList) { pending ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Name: ${pending.name}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Phone: ${pending.phone}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Region/Region: ${pending.region}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Status: ${pending.status}", color = Color.Yellow, fontWeight = FontWeight.Bold)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.approvePendingProvider(pending) {} },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                            ) {
                                Text("Approve ✅", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.rejectPendingProvider(pending) {} },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Reject ❌", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 👤 Service provider registration view (Owners of professions sign-up)
@Composable
fun ProviderRegisterScreen(
    viewModel: DaliliViewModel,
    primaryColor: Color,
    lang: String,
    onBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val subCategories by viewModel.subCategories.collectAsState()
    val context = LocalContext.current

    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf<Category?>(null) }
    var regionInput by remember { mutableStateOf("") }

    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && selectedCat == null) {
            selectedCat = categories.firstOrNull()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = if (lang == "ar") "تسجيل أصحاب المهن والمهندسين" else "Service Provider Registration",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(if (lang == "ar") "الاسم بالكامل (أو اسم المحل/المؤسسة)" else "Full Name / Enterprise Name", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text(if (lang == "ar") "رقم الهاتف للعملاء 📞" else "Active Customer Phone No", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = regionInput,
                        onValueChange = { regionInput = it },
                        label = { Text(if (lang == "ar") "المنطقة / المحافظة" else "Governorate / City / Region", color = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = if (lang == "ar") "اختر التصنيف الأساسي لمهنتك:" else "Select Primary Category for your trade:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    categories.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCat = cat }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedCat?.id == cat.id,
                                onClick = { selectedCat = cat }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat.nameAr, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (nameInput.isBlank() || phoneInput.isBlank() || selectedCat == null) {
                                Toast.makeText(context, "الرجاء اكمال الحقول المطلوبة", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.addPendingProvider(
                                    name = nameInput,
                                    phone = phoneInput,
                                    categoryId = selectedCat?.id ?: 0,
                                    subCategoryId = null,
                                    imageUrl = "",
                                    idCardUrl = "",
                                    region = regionInput
                                ) { success ->
                                    if (success) {
                                        Toast.makeText(context, "تم إرسال طلبك للمشرفين بنجاح! سيتم مراجعته وتفعيله.", Toast.LENGTH_LONG).show()
                                        onBack()
                                    } else {
                                        Toast.makeText(context, "عذراً حدث خطأ، يرجى المحاولة لاحقاً.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (lang == "ar") "تقديم طلب التسجيل 📤" else "Submit Registration", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog/Overlay Chatbot window
@Composable
fun ChatDialogOverlay(
    viewModel: DaliliViewModel,
    primaryColor: Color,
    lang: String,
    onDismiss: () -> Unit
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isAssistantLoading by viewModel.isAssistantLoading.collectAsState()
    val aiIconSymbol by viewModel.aiIcon.collectAsState()
    val welcomeText by viewModel.assistantWelcomeText.collectAsState()

    var userMessageInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(440.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Header of AI assistant
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = aiIconSymbol.ifEmpty { "🤖" }, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == "ar") "مساعد دليلي الذكي" else "Dalili AI Assistant",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                HorizontalDivider(color = Color.White.copy(0.1f), modifier = Modifier.padding(vertical = 4.dp))

                // Log representation
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = false
                ) {
                    item {
                        // Welcome text from AI
                        ChatBubbleField(msg = welcomeText, isUser = false, primaryColor = primaryColor)
                    }

                    items(chatHistory) { message ->
                        ChatBubbleField(msg = message.first, isUser = message.second, primaryColor = primaryColor)
                    }

                    if (isAssistantLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                CircularProgressIndicator(color = primaryColor, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(0.1f), modifier = Modifier.padding(vertical = 4.dp))

                // Message Text Field Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userMessageInput,
                        onValueChange = { userMessageInput = it },
                        placeholder = { Text(if (lang == "ar") "اسأل المساعد الذكي عن مهندس أو طبيب..." else "Ask AI helper...", color = Color.White.copy(0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.White.copy(0.3f)
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (userMessageInput.isNotBlank()) {
                                viewModel.askAssistant(userMessageInput)
                                userMessageInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send Message", tint = primaryColor)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleField(msg: String, isUser: Boolean, primaryColor: Color) {
    val align = if (isUser) Alignment.End else Alignment.Start
    val bg = if (isUser) primaryColor else Color.White.copy(0.1f)
    val textCol = Color.White

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 220.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 12.dp
                    )
                )
                .background(bg)
                .padding(10.dp)
        ) {
            Text(
                text = msg,
                color = textCol,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
