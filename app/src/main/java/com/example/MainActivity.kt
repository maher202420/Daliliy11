package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.DaliliViewModel
import com.example.ui.theme.MyApplicationTheme
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: DaliliViewModel = viewModel()
            val currentTheme by viewModel.getCurrentTheme().collectAsStateWithLifecycle()
            
            MyApplicationTheme(themeChoice = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(viewModel = viewModel)
                }
            }
        }
    }
}

// ----------------------------------------------------
// CUSTOM TEXT FIELD WITH BOLD PURE WHITE TEXT (HYPER-VISIBLE)
// ----------------------------------------------------
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = Color.White) },
        placeholder = { Text(text = placeholder, color = Color.White.copy(alpha = 0.8f)) },
        textStyle = TextStyle(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.End
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF1E1E1E), // Solid dark surface for clear contrast
            unfocusedContainerColor = Color(0xFF262626),
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
            cursorColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            focusedPlaceholderColor = Color.White.copy(alpha = 0.8f),
            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

// ----------------------------------------------------
// NAVIGATION TABS & SKELETON
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(viewModel: DaliliViewModel) {
    var currentTab by remember { mutableStateOf("home") }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home.INSTANCE) }
    
    val appName by viewModel.getAppName().collectAsStateWithLifecycle()
    val showFooter by viewModel.getShowFooter().collectAsStateWithLifecycle()
    val footerText by viewModel.getFooterText().collectAsStateWithLifecycle()
    val currentUser by viewModel.getCurrentUser().collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    var showJoinDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = appName.ifEmpty { "دليلي" },
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = { showJoinDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "انضم كمزود خدمة",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column {
                if (showFooter && footerText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = footerText,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == "home",
                        onClick = {
                            currentTab = "home"
                            currentScreen = Screen.Home.INSTANCE
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية", fontSize = 11.sp) }
                    )
                    
                    NavigationBarItem(
                        selected = currentTab == "assistant",
                        onClick = {
                            currentTab = "assistant"
                        },
                        icon = { Icon(Icons.Default.Email, contentDescription = "المساعد الذكي") }, // Changed to Smart Assistant Chat in footer
                        label = { Text("المساعد الذكي", fontSize = 11.sp) }
                    )
                    
                    NavigationBarItem(
                        selected = currentTab == "info",
                        onClick = {
                            currentTab = "info"
                        },
                        icon = { Icon(Icons.Default.Info, contentDescription = "عن التطبيق") },
                        label = { Text("عن التطبيق", fontSize = 11.sp) }
                    )
                    
                    NavigationBarItem(
                        selected = currentTab == "admin",
                        onClick = {
                            currentTab = "admin"
                            currentScreen = if (currentUser != null) Screen.AdminDashboard.INSTANCE else Screen.Login.INSTANCE
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "الإدارة") },
                        label = { Text("الإدارة", fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                currentTab == "assistant" -> {
                    SmartAssistantScreen(viewModel = viewModel)
                }
                currentTab == "info" -> {
                    AppInfoScreen(viewModel = viewModel)
                }
                currentTab == "admin" -> {
                    when (currentScreen) {
                        is Screen.Login -> LoginScreen(viewModel = viewModel) {
                            currentScreen = Screen.AdminDashboard.INSTANCE
                        }
                        is Screen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel) {
                            currentTab = "home"
                            currentScreen = Screen.Home.INSTANCE
                        }
                        else -> LoginScreen(viewModel = viewModel) {
                            currentScreen = Screen.AdminDashboard.INSTANCE
                        }
                    }
                }
                else -> { // "home"
                    when (val screen = currentScreen) {
                        is Screen.Home -> HomeScreen(viewModel = viewModel) { category ->
                            currentScreen = Screen.CategoryDetails(category)
                        }
                        is Screen.CategoryDetails -> CategoryProvidersScreen(
                            category = screen.category,
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Home.INSTANCE }
                        )
                        else -> HomeScreen(viewModel = viewModel) { category ->
                            currentScreen = Screen.CategoryDetails(category)
                        }
                    }
                }
            }
        }
    }
    
    if (showJoinDialog) {
        JoinServiceProviderDialog(
            viewModel = viewModel,
            onDismiss = { showJoinDialog = false }
        )
    }
}

// ----------------------------------------------------
// MAIN SCREENS
// ----------------------------------------------------

@Composable
fun HomeScreen(viewModel: DaliliViewModel, onCategoryClick: (Category) -> Unit) {
    val categories by viewModel.getCategories().collectAsStateWithLifecycle()
    val welcomeText by viewModel.getWelcomeText().collectAsStateWithLifecycle()
    val welcomeImage by viewModel.getWelcomeImage().collectAsStateWithLifecycle()
    val searchQuery by viewModel.getSearchQuery().collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Welcome Greeting Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.height(180.dp)) {
                    if (welcomeImage.isNotEmpty()) {
                        AsyncImage(
                            model = welcomeImage,
                            contentDescription = "Welcome Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Text(
                            text = welcomeText.ifEmpty { "مرحباً بك في تطبيق دليلي لتقديم الخدمات في اليمن" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
            
            // SEARCH FIELD (Hyper-visible clean font check)
            CustomTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = "البحث عن الخدمات",
                placeholder = "اكتب اسم الخدمة..."
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "الأقسام الرئيسية",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.End
            )
        }
        
        // Dynamic Filter categories based on search
        val filteredCats = categories.filter {
            it.nameAr.contains(searchQuery, ignoreCase = true)
        }.sortedBy { it.orderIndex }
        
        items(filteredCats) { category ->
            Card(
                onClick = { onCategoryClick(category) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("category_card_${category.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "تفاصيل",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.nameAr,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        AsyncImage(
                            model = category.icon,
                            contentDescription = category.nameAr,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryProvidersScreen(category: Category, viewModel: DaliliViewModel, onBack: () -> Unit) {
    val providers by viewModel.getServiceProviders().collectAsStateWithLifecycle()
    val subCategories by viewModel.getSubCategories().collectAsStateWithLifecycle()
    
    var selectedSubCatId by remember { mutableStateOf<Int?>(null) }
    
    val catProviders = providers.filter {
        it.categoryId == category.id && (selectedSubCatId == null || it.subCategoryId == selectedSubCatId)
    }
    
    val categorySubs = subCategories.filter { it.parentCategoryId == category.id }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
            }
            Text(
                text = category.nameAr,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        
        // Subcategories Chips
        if (categorySubs.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                ElevatedFilterChip(
                    selected = selectedSubCatId == null,
                    onClick = { selectedSubCatId = null },
                    label = { Text("الكل") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                categorySubs.forEach { sub ->
                    ElevatedFilterChip(
                        selected = selectedSubCatId == sub.id,
                        onClick = { selectedSubCatId = sub.id },
                        label = { Text(sub.nameAr) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
        
        if (catProviders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "لا يوجد مقدمي خدمة متاحين في هذا القسم حالياً", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(catProviders) { provider ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = provider.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "الهاتف: ${provider.phone}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                AsyncImage(
                                    model = provider.imageUrl,
                                    contentDescription = provider.name,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Rating Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val context = LocalContext.current
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "اتصال")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("اتصل الآن")
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${provider.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Icon(Icons.Default.Star, contentDescription = "تقييم", tint = Color(0xFFFDD835))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SMART ASSISTANT SCREEN
// ----------------------------------------------------
@Composable
fun SmartAssistantScreen(viewModel: DaliliViewModel) {
    val chatHistory by viewModel.getChatHistory().collectAsStateWithLifecycle()
    val isAssistantLoading by viewModel.isAssistantLoading().collectAsStateWithLifecycle()
    val assistantWelcomeText by viewModel.getAssistantWelcomeText().collectAsStateWithLifecycle()
    
    var inputQuery by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "مساعدك الذكي",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Chat Window
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true
            ) {
                // Show Assistant Response Loader
                if (isAssistantLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterStart))
                        }
                    }
                }
                
                // Render converse
                val chatToRender = chatHistory.reversed()
                items(chatToRender) { msg ->
                    val isUser = msg.second
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = msg.first,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(10.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                // Show Welcome message
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = assistantWelcomeText.ifEmpty { "مرحباً بك! أنا مساعدك الذكي للتوجيه والاستدلال عن الخدمات في اليمن، كيف يمكنني مساعدتك اليوم؟" },
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(10.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Send Message controls with White Text box
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (inputQuery.trim().isNotEmpty()) {
                        viewModel.askAssistant(inputQuery)
                        inputQuery = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "إرسال", tint = MaterialTheme.colorScheme.onPrimary)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            CustomTextField(
                value = inputQuery,
                onValueChange = { inputQuery = it },
                label = "",
                placeholder = "اسأل عن فني، مزود خدمة، إلخ...",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ----------------------------------------------------
// APP INFO SCREEN
// ----------------------------------------------------
@Composable
fun AppInfoScreen(viewModel: DaliliViewModel) {
    val appLogo by viewModel.getAppLogo().collectAsStateWithLifecycle()
    val appSubtitle by viewModel.getAboutAppSubtitle().collectAsStateWithLifecycle()
    val supportPhone by viewModel.getSupportPhone().collectAsStateWithLifecycle()
    val supportEmail by viewModel.getSupportEmail().collectAsStateWithLifecycle()
    val supportWhatsapp by viewModel.getSupportWhatsapp().collectAsStateWithLifecycle()
    val updatesUrl by viewModel.getAppUpdatesUrl().collectAsStateWithLifecycle()
    val shareText by viewModel.getAppShareText().collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // App Logo
            AsyncImage(
                model = appLogo.ifEmpty { "https://cdn-icons-png.flaticon.com/512/3135/3135715.png" },
                contentDescription = "شعار التطبيقات",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .padding(bottom = 12.dp)
            )
            
            Text(
                text = "دليلي للخدمات الشامل",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = appSubtitle.ifEmpty { "هذا التطبيق وسيطك ومستشارك الموثوق للتوصيل بمختلف الكوادر المهنية والخدمية في اليمن." },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Interaction Action buttons
            Column(modifier = Modifier.fillMaxWidth()) {
                // Button 1: Search for updates
                CardButton(
                    title = "البحث عن تحديثات التطبيق",
                    icon = Icons.Default.Refresh,
                    onClick = {
                        val targetUrl = updatesUrl.ifEmpty { "https://play.google.com/store" }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
                        context.startActivity(intent)
                    }
                )
                
                // Button 2: Share application
                CardButton(
                    title = "مشاركة التطبيق مع الأصدقاء",
                    icon = Icons.Default.Share,
                    onClick = {
                        val targetMsg = shareText.ifEmpty { "حمل تطبيق دليلي اليمني للخدمات المهنية الأكثر ثقة!" }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, targetMsg)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة"))
                    }
                )
                
                // Whatsapp Support
                if (supportWhatsapp.isNotEmpty()) {
                    CardButton(
                        title = "تواصل معنا - واتساب",
                        icon = Icons.Default.Phone,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$supportWhatsapp"))
                            context.startActivity(intent)
                        }
                    )
                }
                
                // Phone Support
                if (supportPhone.isNotEmpty()) {
                    CardButton(
                        title = "تواصل معنا - مكالمة هاتفية",
                        icon = Icons.Default.Call,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$supportPhone"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CardButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// ----------------------------------------------------
// LOGIN SCREEN
// ----------------------------------------------------
@Composable
fun LoginScreen(viewModel: DaliliViewModel, onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "دخول لوحة التحكم",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                CustomTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "اسم المستخدم"
                )
                
                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "كلمة المرور",
                    isPassword = true
                )
                
                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val isSuccess = viewModel.login(username, viewModel.hashPasswordHelper(password))
                        if (isSuccess) {
                            onLoginSuccess()
                        } else {
                            error = "خطأ في اسم المستخدم أو كلمة المرور"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تسجيل الدخول")
                }
            }
        }
    }
}

// ----------------------------------------------------
// ADMIN DASHBOARD
// ----------------------------------------------------
@Composable
fun AdminDashboardScreen(viewModel: DaliliViewModel, onLogout: () -> Unit) {
    var adminTab by remember { mutableStateOf(0) }
    
    val pending by viewModel.getPendingProviders().collectAsStateWithLifecycle()
    val categories by viewModel.getCategories().collectAsStateWithLifecycle()
    val providers by viewModel.getServiceProviders().collectAsStateWithLifecycle()
    
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = adminTab) {
            Tab(selected = adminTab == 0, onClick = { adminTab = 0 }) {
                Text("الإعدادات والسمات", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
            }
            Tab(selected = adminTab == 1, onClick = { adminTab = 1 }) {
                Text("الأقسام", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
            }
            Tab(selected = adminTab == 2, onClick = { adminTab = 2 }) {
                Text("مقدمي الخدمة", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
            }
            Tab(selected = adminTab == 3, onClick = { adminTab = 3 }) {
                BadgedBox(badge = {
                    if (pending.isNotEmpty()) {
                        Badge { Text("${pending.size}") }
                    }
                }) {
                    Text("الطلبات", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                }
            }
        }
        
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (adminTab) {
                0 -> AdminConfigSubscreen(viewModel, onLogout)
                1 -> AdminCategoriesSubscreen(viewModel)
                2 -> AdminProvidersSubscreen(viewModel, providers)
                3 -> AdminApplicationsSubscreen(viewModel, pending)
            }
        }
    }
}

// ----------------------------------------------------
// ADMIN DASHBOARD SUBSCREENS
// ----------------------------------------------------

@Composable
fun AdminConfigSubscreen(viewModel: DaliliViewModel, onLogout: () -> Unit) {
    val themeChoice by viewModel.getCurrentTheme().collectAsStateWithLifecycle()
    val appName by viewModel.getAppName().collectAsStateWithLifecycle()
    val welcomeText by viewModel.getWelcomeText().collectAsStateWithLifecycle()
    val welcomeImage by viewModel.getWelcomeImage().collectAsStateWithLifecycle()
    val appLogo by viewModel.getAppLogo().collectAsStateWithLifecycle()
    val phone by viewModel.getSupportPhone().collectAsStateWithLifecycle()
    val email by viewModel.getSupportEmail().collectAsStateWithLifecycle()
    val whatsapp by viewModel.getSupportWhatsapp().collectAsStateWithLifecycle()
    val footer by viewModel.getFooterText().collectAsStateWithLifecycle()
    val showF by viewModel.getShowFooter().collectAsStateWithLifecycle()
    val aboutAppSubtitle by viewModel.getAboutAppSubtitle().collectAsStateWithLifecycle()
    val updatesUrl by viewModel.getAppUpdatesUrl().collectAsStateWithLifecycle()
    val shareText by viewModel.getAppShareText().collectAsStateWithLifecycle()
    val assistantWelcomeText by viewModel.getAssistantWelcomeText().collectAsStateWithLifecycle()

    var stateThemeChoice by remember(themeChoice) { mutableStateOf(themeChoice) }
    var stateAppName by remember(appName) { mutableStateOf(appName) }
    var stateWelcomeText by remember(welcomeText) { mutableStateOf(welcomeText) }
    var stateWelcomeImage by remember(welcomeImage) { mutableStateOf(welcomeImage) }
    var stateAppLogo by remember(appLogo) { mutableStateOf(appLogo) }
    var statePhone by remember(phone) { mutableStateOf(phone) }
    var stateEmail by remember(email) { mutableStateOf(email) }
    var stateWhatsapp by remember(whatsapp) { mutableStateOf(whatsapp) }
    var stateFooter by remember(footer) { mutableStateOf(footer) }
    var stateShowF by remember(showF) { mutableStateOf(showF) }
    var stateAboutSub by remember(aboutAppSubtitle) { mutableStateOf(aboutAppSubtitle) }
    var stateUpdatesUrl by remember(updatesUrl) { mutableStateOf(updatesUrl) }
    var stateShareText by remember(shareText) { mutableStateOf(shareText) }
    var stateAssistantText by remember(assistantWelcomeText) { mutableStateOf(assistantWelcomeText) }

    val context = LocalContext.current

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("تعديل سمات وهوية التطبيق", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Theme selection list with 9 available themes
            Text("اختر مظهر التطبيق (9 مظاهر متوفرة):", fontSize = 14.sp)
            val themeOptions = listOf(
                "slate" to "الفضي الكلاسيكي (Slate)",
                "red" to "الأحمر والأسود (Red Black)",
                "indigo" to "الأزرق الملكي (Indigo)",
                "emerald" to "الأخضر الجذاب (Emerald)",
                "teal" to "البحري الرائع (Ocean Teal)",
                "beige" to "البيج الكريمي الدافئ (Beige)",
                "gold" to "الذهبي الملكي الفاخر (Royal Gold)",
                "sage" to "سحر الغابات الهادئ (Sage Forest)",
                "lavender" to "اللافندر الساحر الرائع (Lavender)"
            )
            
            themeOptions.forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { stateThemeChoice = opt.first }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(opt.second, color = if (stateThemeChoice == opt.first) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    RadioButton(selected = stateThemeChoice == opt.first, onClick = { stateThemeChoice = opt.first })
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            CustomTextField(value = stateAppName, onValueChange = { stateAppName = it }, label = "اسم التطبيق")
            CustomTextField(value = stateWelcomeText, onValueChange = { stateWelcomeText = it }, label = "نص رسالة الترحيب في البانر")
            CustomTextField(value = stateWelcomeImage, onValueChange = { stateWelcomeImage = it }, label = "رابط صورة رسالة الترحيب")
            CustomTextField(value = stateAppLogo, onValueChange = { stateAppLogo = it }, label = "رابط صورة شعار التطبيقات")
            CustomTextField(value = stateAboutSub, onValueChange = { stateAboutSub = it }, label = "وصف/نبذة عن التطبيق")
            CustomTextField(value = stateUpdatesUrl, onValueChange = { stateUpdatesUrl = it }, label = "رابط البحث عن تحديثات")
            CustomTextField(value = stateShareText, onValueChange = { stateShareText = it }, label = "نص رسالة مشاركة التطبيقات")
            CustomTextField(value = stateAssistantText, onValueChange = { stateAssistantText = it }, label = "نص ترحيب المساعد الذكي")
            CustomTextField(value = statePhone, onValueChange = { statePhone = it }, label = "رقم هاتف الدعم")
            CustomTextField(value = stateEmail, onValueChange = { stateEmail = it }, label = "بريد الدعم")
            CustomTextField(value = stateWhatsapp, onValueChange = { stateWhatsapp = it }, label = "رقم واتساب الدعم")
            CustomTextField(value = stateFooter, onValueChange = { stateFooter = it }, label = "نص تذييل الصفحة")
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(checked = stateShowF, onCheckedChange = { stateShowF = it })
                Text("إظهار شريط التذييل")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    viewModel.updateAppConfig(
                        stateThemeChoice,
                        stateAppName,
                        stateWelcomeText,
                        stateWelcomeImage,
                        stateAppLogo,
                        statePhone,
                        stateEmail,
                        stateWhatsapp,
                        stateFooter,
                        stateShowF,
                        stateAboutSub,
                        stateUpdatesUrl,
                        stateShareText,
                        stateAssistantText
                    ) { success ->
                        val text = if (success) "تم الحفظ ومزامنة بقية الأجهزة فورياً بنجاح!" else "فشل حفظ التعديلات"
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ التعديلات ومزامنة الأجهزة")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تسجيل الخروج من لوحة الإشراف")
            }
        }
    }
}

@Composable
fun AdminCategoriesSubscreen(viewModel: DaliliViewModel) {
    val categories by viewModel.getCategories().collectAsStateWithLifecycle()
    var catName by remember { mutableStateOf("") }
    var catIcon by remember { mutableStateOf("") }
    
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("إضافة قسم رئيسي جديد", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        CustomTextField(value = catName, onValueChange = { catName = it }, label = "اسم القسم باللغة العربية")
        CustomTextField(value = catIcon, onValueChange = { catIcon = it }, label = "رابط أيقونة/شعار القسم")
        
        Button(
            onClick = {
                if (catName.isNotEmpty() && catIcon.isNotEmpty()) {
                    var maxIdx = categories.maxOfOrNull { it.orderIndex } ?: 0
                    viewModel.addCategory(catName, catIcon, maxIdx + 1) { success ->
                        if (success) {
                            catName = ""
                            catIcon = ""
                            Toast.makeText(context, "تمت إضافة القسم الرئيسي بنجاح!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("إضافة القسم")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Text("الأقسام الحالية:", fontWeight = FontWeight.Bold)
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(categories) { cat ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.deleteCategory(cat.id ?: 0) {
                                Toast.makeText(context, "تم حذف القسم", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(cat.nameAr)
                            Spacer(modifier = Modifier.width(12.dp))
                            AsyncImage(
                                model = cat.icon,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProvidersSubscreen(viewModel: DaliliViewModel, providers: List<ServiceProvider>) {
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("إدارة مقدمي الخدمة الحاليين", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(providers) { pr ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.deleteServiceProvider(pr.id ?: 0) {
                                Toast.makeText(context, "تم حذف مقدم الخدمة ومزامنة الأجهزة", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(pr.name, fontWeight = FontWeight.Bold)
                                Text(pr.phone, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            AsyncImage(
                                model = pr.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    
                    // Display ID Card of active provider if it exists
                    if (!pr.idCardUrl.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("بطاقة الهوية والتوثيق المرفقة:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                        AsyncImage(
                            model = pr.idCardUrl,
                            contentDescription = "قسيمة الهوية والتحقق",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminApplicationsSubscreen(viewModel: DaliliViewModel, pending: List<PendingProvider>) {
    val context = LocalContext.current
    
    if (pending.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا تتوفر طلبات انضمام جديدة ومعلقة حالياً", textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("بوابة طلبات الانضمام والموافقة الفورية", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(pending) { request ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "طلب معلق لتوفير الموثوقية",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = request.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "الهاتف: ${request.phone}", fontSize = 13.sp)
                                    request.region?.let {
                                        Text(text = "المنطقة: $it", fontSize = 13.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                AsyncImage(
                                    model = request.imageUrl,
                                    contentDescription = "صورة مقدم الطلب قبل الموافقة",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        
                        // ID CARD IMAGE (Hyper-priority request: واظهار صورة بطاقة الهوية الشخصية قبل موافقته او رفضه)
                        if (!request.idCardUrl.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "بطاقة الهوية والتحقق الشخصي المرفقة:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AsyncImage(
                                model = request.idCardUrl,
                                contentDescription = "بطاقة الهوية",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.getDpOrPx().dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        // Open image in full size via intent helper
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(request.idCardUrl))
                                        context.startActivity(intent)
                                    },
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "لا توجد بطاقة هوية مرفقة (اختياري)",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Accept/Reject action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    viewModel.approvePendingProvider(request) { success ->
                                        if (success) {
                                            Toast.makeText(context, "تمت الموافقة وتم قبول مزود الخدمة بنجاح وفوراً!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Text("قبول الطلب وتفعيل المزود")
                            }
                            
                            Button(
                                onClick = {
                                    viewModel.rejectPendingProvider(request) { success ->
                                        if (success) {
                                            Toast.makeText(context, "تم رفض ومسح طلب الانضمام المعلق", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                            ) {
                                Text("رفض الطلب")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// JOIN REQUEST DIALOG (REGISTRATION & APPLICANTS IMAGE PICKER WORKFLOWS)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinServiceProviderDialog(viewModel: DaliliViewModel, onDismiss: () -> Unit) {
    val categories by viewModel.getCategories().collectAsStateWithLifecycle()
    val subCategories by viewModel.getSubCategories().collectAsStateWithLifecycle()
    
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var idCardUrl by remember { mutableStateOf("") }
    
    var selectedCatId by remember { mutableStateOf<Int?>(categories.firstOrNull()?.id) }
    var selectedSubCatId by remember { mutableStateOf<Int?>(null) }
    
    val catSubs = subCategories.filter { it.parentCategoryId == selectedCatId }
    val context = LocalContext.current
    
    // Pickers using standard Uri result callbacks (Using direct HTTP uploads safely from prompt mock upload, as Android emulator doesn't run local file system inside the server side builder)
    val applicantImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Since there are no native file uploads, we simulate/convert local picked Uri or notify applicant
            imageUrl = it.toString()
            Toast.makeText(context, "تم اختيار وصورة مقدم الطلب بنجاح", Toast.LENGTH_SHORT).show()
        }
    }
    
    val idCardImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            idCardUrl = it.toString()
            Toast.makeText(context, "تم تحميل بطاقة الهوية بنجاح", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "طلب الانضمام كمزود خدمة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                CustomTextField(value = name, onValueChange = { name = it }, label = "الاسم الكامل للجناح/المتقدم")
                CustomTextField(value = phone, onValueChange = { phone = it }, label = "رقم هاتف التواصل", keyboardType = KeyboardType.Phone)
                CustomTextField(value = region, onValueChange = { region = it }, label = "المنطقة / المحافظة في اليمن")
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Dropdown to select core categories
                Text("اختر القسم المهني المناسب للتقديم:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                var expandedCat by remember { mutableStateOf(false) }
                val currentSelectedCatName = categories.find { it.id == selectedCatId }?.nameAr ?: "اختر الفئة..."
                
                ExposedDropdownMenuBox(
                    expanded = expandedCat,
                    onExpandedChange = { expandedCat = !expandedCat }
                ) {
                    OutlinedButton(
                        onClick = { expandedCat = true },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(currentSelectedCatName)
                    }
                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.nameAr) },
                                onClick = {
                                    selectedCatId = cat.id
                                    selectedSubCatId = null
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // IMAGE SELECTIONS (Support both direct HTTPS string URL or Uri File selector)
                Text("الموقع الشخصي وصور إثبات الهوية (اختياري وسهل):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                Spacer(modifier = Modifier.height(4.dp))
                
                // Choice A: Personal Photo (صورة مقدم الخدمة الشخصية)
                CustomTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = "رابط صورتك الشخصية (أو ارفع عبر الزر بالأسفل)",
                    placeholder = "https://example.com/your-image.jpg"
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(
                        onClick = { applicantImagePicker.launch("image/*") },
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("رفع صورتك")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Choice B: ID Card Photo (صورة لبطاقة الهوية الشخصية)
                CustomTextField(
                    value = idCardUrl,
                    onValueChange = { idCardUrl = it },
                    label = "رابط بطاقة الهوية الشخصية (اختياري)",
                    placeholder = "https://example.com/id-card.jpg"
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { idCardImagePicker.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("رفع بطاقة الهوية")
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && phone.isNotEmpty() && selectedCatId != null) {
                                viewModel.addPendingProvider(
                                    name,
                                    phone,
                                    selectedCatId!!,
                                    selectedSubCatId,
                                    imageUrl.ifEmpty { "https://cdn-icons-png.flaticon.com/512/3135/3135715.png" }, // Default avatar fallback
                                    idCardUrl,
                                    region,
                                    onComplete = { success ->
                                        if (success) {
                                            Toast.makeText(context, "تم إرسال طلبك للإدارة، وسيرحب بك الإدمن قريباً!", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "فشل تقديم الطلب.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            } else {
                                Toast.makeText(context, "الرجاء كتابة الاسم الكامل ورقم هاتف التواصل", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إرسال طلب الانضمام")
                    }
                    
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إلغاء الأمر")
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// GENERAL LAYOUT UTILITIES
// ----------------------------------------------------
private fun Int.getDpOrPx(): Int {
    // Avoid foldable configuration height scaling issues
    return this
}
