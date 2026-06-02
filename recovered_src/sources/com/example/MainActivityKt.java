package com.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.foundation.text.KeyboardActions;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.foundation.text.selection.TextSelectionColors;
import androidx.compose.material3.AppBarKt;
import androidx.compose.material3.CardColors;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.CardElevation;
import androidx.compose.material3.CardKt;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.material3.NavigationBarKt;
import androidx.compose.material3.OutlinedTextFieldDefaults;
import androidx.compose.material3.OutlinedTextFieldKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.material3.TextKt;
import androidx.compose.material3.TopAppBarDefaults;
import androidx.compose.material3.TopAppBarScrollBehavior;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SkippableUpdater;
import androidx.compose.runtime.SnapshotMutationPolicy;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shadow;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.drawscope.DrawStyle;
import androidx.compose.ui.layout.LayoutKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.PlatformTextStyle;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontSynthesis;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.VisualTransformation;
import androidx.compose.ui.text.intl.LocaleList;
import androidx.compose.ui.text.style.BaselineShift;
import androidx.compose.ui.text.style.LineHeightStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.text.style.TextGeometricTransform;
import androidx.compose.ui.text.style.TextIndent;
import androidx.compose.ui.text.style.TextMotion;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import androidx.compose.ui.window.AndroidDialog_androidKt;
import androidx.compose.ui.window.DialogProperties;
import com.example.Screen;
import com.example.data.Admin;
import com.example.data.Category;
import com.example.data.PendingProvider;
import com.example.data.ServiceProvider;
import com.example.data.SubCategory;
import com.example.ui.DaliliViewModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000\u0088\u0001\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0007\u001aY\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u0007¢\u0006\u0002\u0010\u000e\u001a\u0015\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\u0010\u0012\u001a)\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\u0005H\u0007¢\u0006\u0002\u0010\u0016\u001a#\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u0019H\u0007¢\u0006\u0002\u0010\u001a\u001a+\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00010\u0019H\u0007¢\u0006\u0002\u0010\u001e\u001a\u0015\u0010\u001f\u001a\u00020\u00012\u0006\u0010 \u001a\u00020!H\u0007¢\u0006\u0002\u0010\"\u001a\u0015\u0010#\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\u0010\u0012\u001a\u001d\u0010$\u001a\u00020\u00012\u0006\u0010%\u001a\u00020\u00032\u0006\u0010&\u001a\u00020'H\u0007¢\u0006\u0002\u0010(\u001a\u0015\u0010)\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\u0010\u0012\u001a#\u0010*\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00010\u0019H\u0007¢\u0006\u0002\u0010\u001a\u001a#\u0010,\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00010\u0019H\u0007¢\u0006\u0002\u0010\u001a\u001a\u0015\u0010.\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\u0010\u0012\u001a\u0015\u0010/\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\u0010\u0012\u001a\u0015\u00100\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\u0010\u0012\u001a\u0015\u00101\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\u0010\u0012¨\u00062²\u0006\n\u00103\u001a\u000204X\u008a\u008e\u0002²\u0006\n\u00105\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u00106\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\u0010\u00107\u001a\b\u0012\u0004\u0012\u00020\u001508X\u008a\u0084\u0002²\u0006\n\u00109\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\f\u0010:\u001a\u0004\u0018\u00010\u0003X\u008a\u0084\u0002²\u0006\n\u0010;\u001a\u00020'X\u008a\u008e\u0002²\u0006\n\u0010<\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010=\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010>\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010?\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\f\u0010@\u001a\u0004\u0018\u00010AX\u008a\u008e\u0002²\u0006\u0010\u00107\u001a\b\u0012\u0004\u0012\u00020\u001508X\u008a\u0084\u0002²\u0006\u0010\u0010B\u001a\b\u0012\u0004\u0012\u00020C08X\u008a\u0084\u0002²\u0006\u0010\u0010D\u001a\b\u0012\u0004\u0012\u00020E08X\u008a\u0084\u0002²\u0006\n\u0010F\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\f\u0010G\u001a\u0004\u0018\u00010AX\u008a\u008e\u0002²\u0006\u001c\u0010H\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020'0I08X\u008a\u0084\u0002²\u0006\n\u0010J\u001a\u00020'X\u008a\u0084\u0002²\u0006\n\u0010K\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\n\u0010L\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010M\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\n\u0010N\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\n\u0010O\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\n\u0010P\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\n\u0010Q\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\n\u0010R\u001a\u00020\u0003X\u008a\u0084\u0002²\u0006\f\u0010S\u001a\u0004\u0018\u00010\u0003X\u008a\u0084\u0002²\u0006\n\u0010T\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010U\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010V\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\u0010\u0010W\u001a\b\u0012\u0004\u0012\u00020X08X\u008a\u0084\u0002²\u0006\n\u00106\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u00109\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010:\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010S\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010M\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010N\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010O\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010Y\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010P\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010Q\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010R\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010K\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010Z\u001a\u00020'X\u008a\u008e\u0002²\u0006\n\u0010[\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\u0010\u00107\u001a\b\u0012\u0004\u0012\u00020\u001508X\u008a\u0084\u0002²\u0006\n\u0010\\\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010]\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010^\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\u0010\u0010B\u001a\b\u0012\u0004\u0012\u00020C08X\u008a\u0084\u0002²\u0006\u0010\u00107\u001a\b\u0012\u0004\u0012\u00020\u001508X\u008a\u0084\u0002²\u0006\n\u0010<\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010=\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010?\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\f\u0010@\u001a\u0004\u0018\u00010AX\u008a\u008e\u0002²\u0006\n\u0010_\u001a\u00020'X\u008a\u008e\u0002²\u0006\u0010\u0010W\u001a\b\u0012\u0004\u0012\u00020X08X\u008a\u0084\u0002²\u0006\u0010\u00107\u001a\b\u0012\u0004\u0012\u00020\u001508X\u008a\u0084\u0002"}, d2 = {"CustomTextField", "", "value", "", "onValueChange", "Lkotlin/Function1;", "label", "modifier", "Landroidx/compose/ui/Modifier;", "placeholder", "keyboardOptions", "Landroidx/compose/foundation/text/KeyboardOptions;", "visualTransformation", "Landroidx/compose/ui/text/input/VisualTransformation;", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;Ljava/lang/String;Landroidx/compose/ui/Modifier;Ljava/lang/String;Landroidx/compose/foundation/text/KeyboardOptions;Landroidx/compose/ui/text/input/VisualTransformation;Landroidx/compose/runtime/Composer;II)V", "MainContent", "viewModel", "Lcom/example/ui/DaliliViewModel;", "(Lcom/example/ui/DaliliViewModel;Landroidx/compose/runtime/Composer;I)V", "HomeScreen", "onCategorySelected", "Lcom/example/data/Category;", "(Lcom/example/ui/DaliliViewModel;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V", "JoinServiceProviderDialog", "onDismiss", "Lkotlin/Function0;", "(Lcom/example/ui/DaliliViewModel;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V", "CategoryProvidersScreen", "category", "onBack", "(Lcom/example/data/Category;Lcom/example/ui/DaliliViewModel;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V", "RatingBar", "rating", "", "(DLandroidx/compose/runtime/Composer;I)V", "SmartAssistantScreen", "ChatBubble", "message", "isUser", "", "(Ljava/lang/String;ZLandroidx/compose/runtime/Composer;I)V", "AppInfoScreen", "LoginScreen", "onLoginSuccess", "AdminDashboardScreen", "onLogout", "AdminConfigSubscreen", "AdminCategoriesSubscreen", "AdminProvidersSubscreen", "AdminApplicationsSubscreen", "app_debug", "currentScreen", "Lcom/example/Screen;", "currentTab", "appName", "categories", "", "welcomeText", "welcomeImage", "showJoinDialog", "name", "phone", "region", "imageUrl", "selectedCategoryId", "", "providers", "Lcom/example/data/ServiceProvider;", "subCategories", "Lcom/example/data/SubCategory;", "searchQuery", "selectedSubCategoryId", "chatHistory", "Lkotlin/Pair;", "isAssistantLoading", "assistantWelcomeText", "inputQuery", "supportPhone", "supportEmail", "supportWhatsapp", "aboutAppSubtitle", "appUpdatesUrl", "appShareText", "appLogo", "username", "password", "subscreenTab", "pendingProviders", "Lcom/example/data/PendingProvider;", "footerText", "showFooter", "selectedTheme", "nameAr", "icon", "orderIndex", "isPinned"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt {
    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminApplicationsSubscreen$lambda$204(DaliliViewModel daliliViewModel, int i, Composer composer, int i2) {
        AdminApplicationsSubscreen(daliliViewModel, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminCategoriesSubscreen$lambda$171(DaliliViewModel daliliViewModel, int i, Composer composer, int i2) {
        AdminCategoriesSubscreen(daliliViewModel, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminConfigSubscreen$lambda$154(DaliliViewModel daliliViewModel, int i, Composer composer, int i2) {
        AdminConfigSubscreen(daliliViewModel, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminDashboardScreen$lambda$108(DaliliViewModel daliliViewModel, Function0 function0, int i, Composer composer, int i2) {
        AdminDashboardScreen(daliliViewModel, function0, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminProvidersSubscreen$lambda$195(DaliliViewModel daliliViewModel, int i, Composer composer, int i2) {
        AdminProvidersSubscreen(daliliViewModel, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AppInfoScreen$lambda$91(DaliliViewModel daliliViewModel, int i, Composer composer, int i2) {
        AppInfoScreen(daliliViewModel, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit CategoryProvidersScreen$lambda$60(Category category, DaliliViewModel daliliViewModel, Function0 function0, int i, Composer composer, int i2) {
        CategoryProvidersScreen(category, daliliViewModel, function0, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ChatBubble$lambda$81(String str, boolean z, int i, Composer composer, int i2) {
        ChatBubble(str, z, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit CustomTextField$lambda$0(String str, Function1 function1, String str2, Modifier modifier, String str3, KeyboardOptions keyboardOptions, VisualTransformation visualTransformation, int i, int i2, Composer composer, int i3) {
        CustomTextField(str, function1, str2, modifier, str3, keyboardOptions, visualTransformation, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1), i2);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HomeScreen$lambda$19(DaliliViewModel daliliViewModel, Function1 function1, int i, Composer composer, int i2) {
        HomeScreen(daliliViewModel, function1, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit JoinServiceProviderDialog$lambda$36(DaliliViewModel daliliViewModel, Function0 function0, int i, Composer composer, int i2) {
        JoinServiceProviderDialog(daliliViewModel, function0, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit LoginScreen$lambda$99(DaliliViewModel daliliViewModel, Function0 function0, int i, Composer composer, int i2) {
        LoginScreen(daliliViewModel, function0, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainContent$lambda$8(DaliliViewModel daliliViewModel, int i, Composer composer, int i2) {
        MainContent(daliliViewModel, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit RatingBar$lambda$62(double d, int i, Composer composer, int i2) {
        RatingBar(d, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit SmartAssistantScreen$lambda$79(DaliliViewModel daliliViewModel, int i, Composer composer, int i2) {
        SmartAssistantScreen(daliliViewModel, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    public static final void CustomTextField(final String value, final Function1<? super String, Unit> function1, final String label, Modifier modifier, String placeholder, KeyboardOptions keyboardOptions, VisualTransformation visualTransformation, Composer $composer, final int $changed, final int i) {
        Modifier modifier2;
        final String placeholder2;
        KeyboardOptions keyboardOptions2;
        VisualTransformation visualTransformation2;
        Modifier modifier3;
        String placeholder3;
        Composer $composer2;
        Intrinsics.checkNotNullParameter(value, "value");
        Intrinsics.checkNotNullParameter(function1, "onValueChange");
        Intrinsics.checkNotNullParameter(label, "label");
        Composer $composer3 = $composer.startRestartGroup(-1313121154);
        ComposerKt.sourceInformation($composer3, "C(CustomTextField)P(5,3,1,2,4)103@4075L397,90@3507L1014:MainActivity.kt#to5c3");
        int $dirty = $changed;
        if ((i & 1) != 0) {
            $dirty |= 6;
        } else if (($changed & 6) == 0) {
            $dirty |= $composer3.changed(value) ? 4 : 2;
        }
        if ((i & 2) != 0) {
            $dirty |= 48;
        } else if (($changed & 48) == 0) {
            $dirty |= $composer3.changedInstance(function1) ? 32 : 16;
        }
        if ((i & 4) != 0) {
            $dirty |= 384;
        } else if (($changed & 384) == 0) {
            $dirty |= $composer3.changed(label) ? 256 : 128;
        }
        int i2 = i & 8;
        if (i2 != 0) {
            $dirty |= 3072;
            modifier2 = modifier;
        } else if (($changed & 3072) == 0) {
            modifier2 = modifier;
            $dirty |= $composer3.changed(modifier2) ? 2048 : 1024;
        } else {
            modifier2 = modifier;
        }
        int i3 = i & 16;
        if (i3 != 0) {
            $dirty |= 24576;
            placeholder2 = placeholder;
        } else if (($changed & 24576) == 0) {
            placeholder2 = placeholder;
            $dirty |= $composer3.changed(placeholder2) ? 16384 : 8192;
        } else {
            placeholder2 = placeholder;
        }
        int i4 = i & 32;
        if (i4 != 0) {
            $dirty |= 196608;
            keyboardOptions2 = keyboardOptions;
        } else if ((196608 & $changed) == 0) {
            keyboardOptions2 = keyboardOptions;
            $dirty |= $composer3.changed(keyboardOptions2) ? 131072 : 65536;
        } else {
            keyboardOptions2 = keyboardOptions;
        }
        int i5 = i & 64;
        if (i5 != 0) {
            $dirty |= 1572864;
            visualTransformation2 = visualTransformation;
        } else if ((1572864 & $changed) == 0) {
            visualTransformation2 = visualTransformation;
            $dirty |= $composer3.changed(visualTransformation2) ? 1048576 : 524288;
        } else {
            visualTransformation2 = visualTransformation;
        }
        if (($dirty & 599187) == 599186 && $composer3.getSkipping()) {
            $composer3.skipToGroupEnd();
            modifier3 = modifier2;
            placeholder3 = placeholder2;
            $composer2 = $composer3;
        } else {
            Modifier modifier4 = i2 != 0 ? (Modifier) Modifier.Companion : modifier2;
            if (i3 != 0) {
                placeholder2 = "";
            }
            KeyboardOptions keyboardOptions3 = i4 != 0 ? KeyboardOptions.Companion.getDefault() : keyboardOptions2;
            VisualTransformation visualTransformation3 = i5 != 0 ? VisualTransformation.Companion.getNone() : visualTransformation2;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1313121154, $dirty, -1, "com.example.CustomTextField (MainActivity.kt:89)");
            }
            modifier3 = modifier4;
            placeholder3 = placeholder2;
            $composer2 = $composer3;
            OutlinedTextFieldKt.OutlinedTextField(value, function1, SizeKt.fillMaxWidth$default(modifier4, 0.0f, 1, (Object) null), false, false, new TextStyle(Color.Companion.getWhite-0d7_KjU(), TextUnitKt.getSp(16), FontWeight.Companion.getBold(), (FontStyle) null, (FontSynthesis) null, (FontFamily) null, (String) null, 0L, (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (DrawStyle) null, 0, 0, 0L, (TextIndent) null, (PlatformTextStyle) null, (LineHeightStyle) null, 0, 0, (TextMotion) null, 16777208, (DefaultConstructorMarker) null), ComposableLambdaKt.composableLambda($composer3, 1015890340, true, new Function2<Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$CustomTextField$1
                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
                    invoke((Composer) p1, ((Number) p2).intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(Composer $composer4, int $changed2) {
                    ComposerKt.sourceInformation($composer4, "C94@3650L51:MainActivity.kt#to5c3");
                    if (($changed2 & 3) == 2 && $composer4.getSkipping()) {
                        $composer4.skipToGroupEnd();
                        return;
                    }
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart(1015890340, $changed2, -1, "com.example.CustomTextField.<anonymous> (MainActivity.kt:94)");
                    }
                    TextKt.Text--4IGK_g(label, (Modifier) null, Color.copy-wmQWz5c$default(Color.Companion.getWhite-0d7_KjU(), 0.8f, 0.0f, 0.0f, 0.0f, 14, (Object) null), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer4, 384, 0, 131066);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                    }
                }
            }), ComposableLambdaKt.composableLambda($composer3, 1265168741, true, new Function2<Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$CustomTextField$2
                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
                    invoke((Composer) p1, ((Number) p2).intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(Composer $composer4, int $changed2) {
                    ComposerKt.sourceInformation($composer4, "C95@3729L57:MainActivity.kt#to5c3");
                    if (($changed2 & 3) == 2 && $composer4.getSkipping()) {
                        $composer4.skipToGroupEnd();
                        return;
                    }
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart(1265168741, $changed2, -1, "com.example.CustomTextField.<anonymous> (MainActivity.kt:95)");
                    }
                    TextKt.Text--4IGK_g(placeholder2, (Modifier) null, Color.copy-wmQWz5c$default(Color.Companion.getWhite-0d7_KjU(), 0.5f, 0.0f, 0.0f, 0.0f, 14, (Object) null), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer4, 384, 0, 131066);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                    }
                }
            }), (Function2) null, (Function2) null, (Function2) null, (Function2) null, (Function2) null, false, visualTransformation3, keyboardOptions3, (KeyboardActions) null, false, 0, 0, (MutableInteractionSource) null, RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(12)), OutlinedTextFieldDefaults.INSTANCE.colors-0hiis_0(Color.Companion.getWhite-0d7_KjU(), Color.Companion.getWhite-0d7_KjU(), 0L, 0L, Color.copy-wmQWz5c$default(Color.Companion.getBlack-0d7_KjU(), 0.3f, 0.0f, 0.0f, 0.0f, 14, (Object) null), Color.copy-wmQWz5c$default(Color.Companion.getBlack-0d7_KjU(), 0.15f, 0.0f, 0.0f, 0.0f, 14, (Object) null), 0L, 0L, Color.Companion.getWhite-0d7_KjU(), 0L, (TextSelectionColors) null, Color.Companion.getWhite-0d7_KjU(), Color.copy-wmQWz5c$default(Color.Companion.getWhite-0d7_KjU(), 0.6f, 0.0f, 0.0f, 0.0f, 14, (Object) null), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, $composer3, 100884534, 432, 0, 0, 3072, 2147477196, 4095), $composer2, ($dirty & 14) | 14155776 | ($dirty & 112), (57344 & ($dirty >> 6)) | (458752 & $dirty), 0, 2047768);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
            keyboardOptions2 = keyboardOptions3;
            visualTransformation2 = visualTransformation3;
        }
        ScopeUpdateScope endRestartGroup = $composer2.endRestartGroup();
        if (endRestartGroup != null) {
            final Modifier modifier5 = modifier3;
            final String str = placeholder3;
            final KeyboardOptions keyboardOptions4 = keyboardOptions2;
            final VisualTransformation visualTransformation4 = visualTransformation2;
            endRestartGroup.updateScope(new Function2() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda22
                public final Object invoke(Object obj, Object obj2) {
                    Unit CustomTextField$lambda$0;
                    CustomTextField$lambda$0 = MainActivityKt.CustomTextField$lambda$0(value, function1, label, modifier5, str, keyboardOptions4, visualTransformation4, $changed, i, (Composer) obj, ((Integer) obj2).intValue());
                    return CustomTextField$lambda$0;
                }
            });
        }
    }

    public static final void MainContent(final DaliliViewModel viewModel, Composer $composer, final int $changed) {
        Object value$iv;
        Object value$iv2;
        Composer $composer2;
        Intrinsics.checkNotNullParameter(viewModel, "viewModel");
        Composer $composer3 = $composer.startRestartGroup(1118328202);
        ComposerKt.sourceInformation($composer3, "C(MainContent)119@4648L48,120@4719L35,121@4821L7,123@4867L16,125@4889L6773:MainActivity.kt#to5c3");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer3.changedInstance(viewModel) ? 4 : 2;
        }
        int $dirty2 = $dirty;
        if (($dirty2 & 3) != 2 || !$composer3.getSkipping()) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1118328202, $dirty2, -1, "com.example.MainContent (MainActivity.kt:118)");
            }
            $composer3.startReplaceableGroup(-538118305);
            ComposerKt.sourceInformation($composer3, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv = $composer3.rememberedValue();
            if (it$iv == Composer.Companion.getEmpty()) {
                value$iv = SnapshotStateKt.mutableStateOf$default(Screen.Home.INSTANCE, (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer3.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv;
            }
            final MutableState currentScreen$delegate = (MutableState) value$iv;
            $composer3.endReplaceableGroup();
            $composer3.startReplaceableGroup(-538116046);
            ComposerKt.sourceInformation($composer3, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv2 = $composer3.rememberedValue();
            if (it$iv2 == Composer.Companion.getEmpty()) {
                value$iv2 = SnapshotStateKt.mutableStateOf$default("home", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer3.updateRememberedValue(value$iv2);
            } else {
                value$iv2 = it$iv2;
            }
            final MutableState currentTab$delegate = (MutableState) value$iv2;
            $composer3.endReplaceableGroup();
            CompositionLocal this_$iv = AndroidCompositionLocals_androidKt.getLocalContext();
            ComposerKt.sourceInformationMarkerStart($composer3, 2023513938, "CC:CompositionLocal.kt#9igjgp");
            Object consume = $composer3.consume(this_$iv);
            ComposerKt.sourceInformationMarkerEnd($composer3);
            final State appName$delegate = SnapshotStateKt.collectAsState(viewModel.getAppName(), (CoroutineContext) null, $composer3, 0, 1);
            $composer2 = $composer3;
            ScaffoldKt.Scaffold-TvnljyQ((Modifier) null, ComposableLambdaKt.composableLambda($composer3, -1114913202, true, new Function2<Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$MainContent$1
                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
                    invoke((Composer) p1, ((Number) p2).intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(Composer $composer4, int $changed2) {
                    ComposerKt.sourceInformation($composer4, "C143@5700L11,144@5775L11,142@5632L182,127@4930L898:MainActivity.kt#to5c3");
                    if (($changed2 & 3) != 2 || !$composer4.getSkipping()) {
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventStart(-1114913202, $changed2, -1, "com.example.MainContent.<anonymous> (MainActivity.kt:127)");
                        }
                        final State<String> state = appName$delegate;
                        AppBarKt.TopAppBar(ComposableLambdaKt.composableLambda($composer4, -1018570742, true, new Function2<Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$MainContent$1.1
                            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
                                invoke((Composer) p1, ((Number) p2).intValue());
                                return Unit.INSTANCE;
                            }

                            /* JADX WARN: Removed duplicated region for block: B:24:0x01c4  */
                            /* JADX WARN: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
                            /*
                                Code decompiled incorrectly, please refer to instructions dump.
                                To view partially-correct add '--show-bad-code' argument
                            */
                            public final void invoke(androidx.compose.runtime.Composer r49, int r50) {
                                /*
                                    Method dump skipped, instructions count: 456
                                    To view this dump add '--comments-level debug' option
                                */
                                throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$MainContent$1.AnonymousClass1.invoke(androidx.compose.runtime.Composer, int):void");
                            }
                        }), (Modifier) null, (Function2) null, (Function3) null, (WindowInsets) null, TopAppBarDefaults.INSTANCE.topAppBarColors-zjMxDiM(MaterialTheme.INSTANCE.getColorScheme($composer4, MaterialTheme.$stable).getSurface-0d7_KjU(), 0L, 0L, MaterialTheme.INSTANCE.getColorScheme($composer4, MaterialTheme.$stable).getOnSurface-0d7_KjU(), 0L, $composer4, TopAppBarDefaults.$stable << 15, 22), (TopAppBarScrollBehavior) null, $composer4, 6, 94);
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventEnd();
                            return;
                        }
                        return;
                    }
                    $composer4.skipToGroupEnd();
                }
            }), ComposableLambdaKt.composableLambda($composer3, -1500279985, true, new Function2<Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$MainContent$2
                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
                    invoke((Composer) p1, ((Number) p2).intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(Composer $composer4, int $changed2) {
                    ComposerKt.sourceInformation($composer4, "C151@6043L11,150@5981L3180:MainActivity.kt#to5c3");
                    if (($changed2 & 3) != 2 || !$composer4.getSkipping()) {
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventStart(-1500279985, $changed2, -1, "com.example.MainContent.<anonymous> (MainActivity.kt:150)");
                        }
                        NavigationBarKt.NavigationBar-HsRjFd4(WindowInsetsPadding_androidKt.navigationBarsPadding(Modifier.Companion), MaterialTheme.INSTANCE.getColorScheme($composer4, MaterialTheme.$stable).getSurface-0d7_KjU(), 0L, Dp.constructor-impl(8), (WindowInsets) null, ComposableLambdaKt.composableLambda($composer4, 1495036982, true, new AnonymousClass1(DaliliViewModel.this, currentTab$delegate, currentScreen$delegate)), $composer4, 199680, 20);
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventEnd();
                            return;
                        }
                        return;
                    }
                    $composer4.skipToGroupEnd();
                }

                /* JADX INFO: Access modifiers changed from: package-private */
                /* compiled from: MainActivity.kt */
                @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
                /* renamed from: com.example.MainActivityKt$MainContent$2$1, reason: invalid class name */
                /* loaded from: /tmp/dex/classes5.dex */
                public static final class AnonymousClass1 implements Function3<RowScope, Composer, Integer, Unit> {
                    final /* synthetic */ MutableState<Screen> $currentScreen$delegate;
                    final /* synthetic */ MutableState<String> $currentTab$delegate;
                    final /* synthetic */ DaliliViewModel $viewModel;

                    AnonymousClass1(DaliliViewModel daliliViewModel, MutableState<String> mutableState, MutableState<Screen> mutableState2) {
                        this.$viewModel = daliliViewModel;
                        this.$currentTab$delegate = mutableState;
                        this.$currentScreen$delegate = mutableState2;
                    }

                    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                        invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
                        return Unit.INSTANCE;
                    }

                    /* JADX WARN: Removed duplicated region for block: B:25:0x0084  */
                    /* JADX WARN: Removed duplicated region for block: B:28:0x011d  */
                    /* JADX WARN: Removed duplicated region for block: B:31:0x01b1  */
                    /* JADX WARN: Removed duplicated region for block: B:39:0x02bf  */
                    /* JADX WARN: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
                    /* JADX WARN: Removed duplicated region for block: B:43:0x01bd  */
                    /* JADX WARN: Removed duplicated region for block: B:44:0x0129  */
                    /* JADX WARN: Removed duplicated region for block: B:45:0x0090  */
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                        To view partially-correct add '--show-bad-code' argument
                    */
                    public final void invoke(androidx.compose.foundation.layout.RowScope r39, androidx.compose.runtime.Composer r40, int r41) {
                        /*
                            Method dump skipped, instructions count: 707
                            To view this dump add '--comments-level debug' option
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$MainContent$2.AnonymousClass1.invoke(androidx.compose.foundation.layout.RowScope, androidx.compose.runtime.Composer, int):void");
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public static final Unit invoke$lambda$1$lambda$0(MutableState $currentTab$delegate, MutableState $currentScreen$delegate) {
                        $currentTab$delegate.setValue("home");
                        $currentScreen$delegate.setValue(Screen.Home.INSTANCE);
                        return Unit.INSTANCE;
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public static final Unit invoke$lambda$3$lambda$2(MutableState $currentTab$delegate) {
                        $currentTab$delegate.setValue("chat");
                        return Unit.INSTANCE;
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public static final Unit invoke$lambda$5$lambda$4(MutableState $currentTab$delegate) {
                        $currentTab$delegate.setValue("info");
                        return Unit.INSTANCE;
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public static final Unit invoke$lambda$7$lambda$6(DaliliViewModel $viewModel, MutableState $currentTab$delegate, MutableState $currentScreen$delegate) {
                        $currentTab$delegate.setValue("admin");
                        Admin user = (Admin) $viewModel.getCurrentUser().getValue();
                        if (user != null) {
                            $currentScreen$delegate.setValue(Screen.AdminDashboard.INSTANCE);
                        } else {
                            $currentScreen$delegate.setValue(Screen.Login.INSTANCE);
                        }
                        return Unit.INSTANCE;
                    }
                }
            }), (Function2) null, (Function2) null, 0, 0L, 0L, (WindowInsets) null, ComposableLambdaKt.composableLambda($composer3, -1625259495, true, new MainActivityKt$MainContent$3(viewModel, currentTab$delegate, currentScreen$delegate)), $composer3, 805306800, 505);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer3.skipToGroupEnd();
            $composer2 = $composer3;
        }
        ScopeUpdateScope endRestartGroup = $composer2.endRestartGroup();
        if (endRestartGroup != null) {
            endRestartGroup.updateScope(new Function2() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda13
                public final Object invoke(Object obj, Object obj2) {
                    Unit MainContent$lambda$8;
                    MainContent$lambda$8 = MainActivityKt.MainContent$lambda$8(DaliliViewModel.this, $changed, (Composer) obj, ((Integer) obj2).intValue());
                    return MainContent$lambda$8;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Screen MainContent$lambda$2(MutableState<Screen> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (Screen) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String MainContent$lambda$5(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String MainContent$lambda$7(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    public static final void HomeScreen(final DaliliViewModel viewModel, final Function1<? super Category, Unit> function1, Composer $composer, final int $changed) {
        Object value$iv;
        MutableState showJoinDialog$delegate;
        Composer $composer2;
        Object value$iv2;
        Intrinsics.checkNotNullParameter(viewModel, "viewModel");
        Intrinsics.checkNotNullParameter(function1, "onCategorySelected");
        Composer $composer3 = $composer.startRestartGroup(-2051737634);
        ComposerKt.sourceInformation($composer3, "C(HomeScreen)P(1)284@11816L16,285@11878L16,286@11942L16,287@11985L34,292@12159L5573,289@12025L5707,420@17854L26,418@17768L122:MainActivity.kt#to5c3");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer3.changedInstance(viewModel) ? 4 : 2;
        }
        if (($changed & 48) == 0) {
            $dirty |= $composer3.changedInstance(function1) ? 32 : 16;
        }
        int $dirty2 = $dirty;
        if (($dirty2 & 19) != 18 || !$composer3.getSkipping()) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-2051737634, $dirty2, -1, "com.example.HomeScreen (MainActivity.kt:283)");
            }
            final State categories$delegate = SnapshotStateKt.collectAsState(viewModel.getCategories(), (CoroutineContext) null, $composer3, 0, 1);
            final State welcomeText$delegate = SnapshotStateKt.collectAsState(viewModel.getWelcomeText(), (CoroutineContext) null, $composer3, 0, 1);
            final State welcomeImage$delegate = SnapshotStateKt.collectAsState(viewModel.getWelcomeImage(), (CoroutineContext) null, $composer3, 0, 1);
            $composer3.startReplaceableGroup(-2147218914);
            ComposerKt.sourceInformation($composer3, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv = $composer3.rememberedValue();
            if (it$iv == Composer.Companion.getEmpty()) {
                value$iv = SnapshotStateKt.mutableStateOf$default(false, (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer3.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv;
            }
            final MutableState showJoinDialog$delegate2 = (MutableState) value$iv;
            $composer3.endReplaceableGroup();
            Modifier modifier = PaddingKt.padding-3ABfNKs(SizeKt.fillMaxSize$default(Modifier.Companion, 0.0f, 1, (Object) null), Dp.constructor-impl(16));
            Arrangement.Vertical vertical = Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl(16));
            $composer3.startReplaceableGroup(-2147207807);
            ComposerKt.sourceInformation($composer3, "CC(remember):MainActivity.kt#9igjgp");
            boolean invalid$iv = $composer3.changed(welcomeImage$delegate) | $composer3.changed(welcomeText$delegate) | $composer3.changed(categories$delegate) | (($dirty2 & 112) == 32);
            Object value$iv3 = $composer3.rememberedValue();
            if (invalid$iv || value$iv3 == Composer.Companion.getEmpty()) {
                showJoinDialog$delegate = showJoinDialog$delegate2;
                value$iv3 = new Function1() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda26
                    public final Object invoke(Object obj) {
                        Unit HomeScreen$lambda$16$lambda$15;
                        HomeScreen$lambda$16$lambda$15 = MainActivityKt.HomeScreen$lambda$16$lambda$15(welcomeImage$delegate, welcomeText$delegate, showJoinDialog$delegate2, categories$delegate, function1, (LazyListScope) obj);
                        return HomeScreen$lambda$16$lambda$15;
                    }
                };
                $composer3.updateRememberedValue(value$iv3);
            } else {
                showJoinDialog$delegate = showJoinDialog$delegate2;
            }
            $composer3.endReplaceableGroup();
            $composer2 = $composer3;
            LazyDslKt.LazyColumn(modifier, (LazyListState) null, (PaddingValues) null, false, vertical, (Alignment.Horizontal) null, (FlingBehavior) null, false, (Function1) value$iv3, $composer2, 24582, 238);
            if (HomeScreen$lambda$13(showJoinDialog$delegate)) {
                $composer2.startReplaceableGroup(-2147031114);
                ComposerKt.sourceInformation($composer2, "CC(remember):MainActivity.kt#9igjgp");
                Object it$iv2 = $composer2.rememberedValue();
                if (it$iv2 == Composer.Companion.getEmpty()) {
                    final MutableState showJoinDialog$delegate3 = showJoinDialog$delegate;
                    value$iv2 = new Function0() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda27
                        public final Object invoke() {
                            Unit HomeScreen$lambda$18$lambda$17;
                            HomeScreen$lambda$18$lambda$17 = MainActivityKt.HomeScreen$lambda$18$lambda$17(showJoinDialog$delegate3);
                            return HomeScreen$lambda$18$lambda$17;
                        }
                    };
                    $composer2.updateRememberedValue(value$iv2);
                } else {
                    value$iv2 = it$iv2;
                }
                $composer2.endReplaceableGroup();
                JoinServiceProviderDialog(viewModel, (Function0) value$iv2, $composer2, ($dirty2 & 14) | 48);
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer3.skipToGroupEnd();
            $composer2 = $composer3;
        }
        ScopeUpdateScope endRestartGroup = $composer2.endRestartGroup();
        if (endRestartGroup != null) {
            endRestartGroup.updateScope(new Function2() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda28
                public final Object invoke(Object obj, Object obj2) {
                    Unit HomeScreen$lambda$19;
                    HomeScreen$lambda$19 = MainActivityKt.HomeScreen$lambda$19(DaliliViewModel.this, function1, $changed, (Composer) obj, ((Integer) obj2).intValue());
                    return HomeScreen$lambda$19;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List<Category> HomeScreen$lambda$9(State<? extends List<Category>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String HomeScreen$lambda$10(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String HomeScreen$lambda$11(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    private static final boolean HomeScreen$lambda$13(MutableState<Boolean> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return ((Boolean) $this$getValue$iv.getValue()).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void HomeScreen$lambda$14(MutableState<Boolean> mutableState, boolean z) {
        Object value$iv = Boolean.valueOf(z);
        mutableState.setValue(value$iv);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HomeScreen$lambda$16$lambda$15(final State $welcomeImage$delegate, final State $welcomeText$delegate, final MutableState $showJoinDialog$delegate, State $categories$delegate, Function1 $onCategorySelected, LazyListScope $this$LazyColumn) {
        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(1878207946, true, new Function3<LazyItemScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$HomeScreen$1$1$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
                Intrinsics.checkNotNullParameter($this$item, "$this$item");
                ComposerKt.sourceInformation($composer, "C298@12446L11,298@12404L71,295@12257L3189:MainActivity.kt#to5c3");
                if (($changed & 17) != 16 || !$composer.getSkipping()) {
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart(1878207946, $changed, -1, "com.example.HomeScreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:295)");
                    }
                    CardKt.Card(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null), RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(16)), CardDefaults.INSTANCE.cardColors-ro_MJ88(MaterialTheme.INSTANCE.getColorScheme($composer, MaterialTheme.$stable).getPrimaryContainer-0d7_KjU(), 0L, 0L, 0L, $composer, CardDefaults.$stable << 12, 14), (CardElevation) null, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer, -1262268904, true, new AnonymousClass1($welcomeImage$delegate, $welcomeText$delegate, $showJoinDialog$delegate)), $composer, 196614, 24);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                        return;
                    }
                    return;
                }
                $composer.skipToGroupEnd();
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            /* compiled from: MainActivity.kt */
            @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
            /* renamed from: com.example.MainActivityKt$HomeScreen$1$1$1$1, reason: invalid class name */
            /* loaded from: /tmp/dex/classes5.dex */
            public static final class AnonymousClass1 implements Function3<ColumnScope, Composer, Integer, Unit> {
                final /* synthetic */ MutableState<Boolean> $showJoinDialog$delegate;
                final /* synthetic */ State<String> $welcomeImage$delegate;
                final /* synthetic */ State<String> $welcomeText$delegate;

                AnonymousClass1(State<String> state, State<String> state2, MutableState<Boolean> mutableState) {
                    this.$welcomeImage$delegate = state;
                    this.$welcomeText$delegate = state2;
                    this.$showJoinDialog$delegate = mutableState;
                }

                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                    invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                    return Unit.INSTANCE;
                }

                /* JADX WARN: Removed duplicated region for block: B:28:0x016d  */
                /* JADX WARN: Removed duplicated region for block: B:31:0x0459  */
                /* JADX WARN: Removed duplicated region for block: B:34:0x0465  */
                /* JADX WARN: Removed duplicated region for block: B:42:0x05a0  */
                /* JADX WARN: Removed duplicated region for block: B:45:0x063a  */
                /* JADX WARN: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
                /* JADX WARN: Removed duplicated region for block: B:48:0x05ae  */
                /* JADX WARN: Removed duplicated region for block: B:51:0x046b  */
                /* JADX WARN: Removed duplicated region for block: B:52:0x01f3  */
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public final void invoke(androidx.compose.foundation.layout.ColumnScope r81, androidx.compose.runtime.Composer r82, int r83) {
                    /*
                        Method dump skipped, instructions count: 1598
                        To view this dump add '--comments-level debug' option
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$HomeScreen$1$1$1.AnonymousClass1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
                }

                /* JADX INFO: Access modifiers changed from: private */
                public static final Unit invoke$lambda$4$lambda$3$lambda$2$lambda$1(MutableState $showJoinDialog$delegate) {
                    MainActivityKt.HomeScreen$lambda$14($showJoinDialog$delegate, true);
                    return Unit.INSTANCE;
                }
            }
        }), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableSingletons$MainActivityKt.INSTANCE.m2getLambda11$app_debug(), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-1054470766, true, new MainActivityKt$HomeScreen$1$1$2($categories$delegate, $onCategorySelected)), 3, (Object) null);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HomeScreen$lambda$18$lambda$17(MutableState $showJoinDialog$delegate) {
        HomeScreen$lambda$14($showJoinDialog$delegate, false);
        return Unit.INSTANCE;
    }

    public static final void JoinServiceProviderDialog(final DaliliViewModel viewModel, final Function0<Unit> function0, Composer $composer, final int $changed) {
        Object value$iv;
        Object value$iv2;
        Object value$iv3;
        Object value$iv4;
        Object value$iv5;
        Intrinsics.checkNotNullParameter(viewModel, "viewModel");
        Intrinsics.checkNotNullParameter(function0, "onDismiss");
        Composer $composer2 = $composer.startRestartGroup(-1086572038);
        ComposerKt.sourceInformation($composer2, "C(JoinServiceProviderDialog)P(1)430@18021L31,431@18070L31,432@18120L31,433@18172L31,434@18234L39,435@18317L16,436@18365L7,438@18378L5541:MainActivity.kt#to5c3");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer2.changedInstance(viewModel) ? 4 : 2;
        }
        if (($changed & 48) == 0) {
            $dirty |= $composer2.changedInstance(function0) ? 32 : 16;
        }
        int $dirty2 = $dirty;
        if (($dirty2 & 19) != 18 || !$composer2.getSkipping()) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1086572038, $dirty2, -1, "com.example.JoinServiceProviderDialog (MainActivity.kt:429)");
            }
            $composer2.startReplaceableGroup(-1192334126);
            ComposerKt.sourceInformation($composer2, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv = $composer2.rememberedValue();
            if (it$iv == Composer.Companion.getEmpty()) {
                value$iv = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer2.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv;
            }
            final MutableState name$delegate = (MutableState) value$iv;
            $composer2.endReplaceableGroup();
            $composer2.startReplaceableGroup(-1192332558);
            ComposerKt.sourceInformation($composer2, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv2 = $composer2.rememberedValue();
            if (it$iv2 == Composer.Companion.getEmpty()) {
                value$iv2 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer2.updateRememberedValue(value$iv2);
            } else {
                value$iv2 = it$iv2;
            }
            final MutableState phone$delegate = (MutableState) value$iv2;
            $composer2.endReplaceableGroup();
            $composer2.startReplaceableGroup(-1192330958);
            ComposerKt.sourceInformation($composer2, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv3 = $composer2.rememberedValue();
            if (it$iv3 == Composer.Companion.getEmpty()) {
                value$iv3 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer2.updateRememberedValue(value$iv3);
            } else {
                value$iv3 = it$iv3;
            }
            final MutableState region$delegate = (MutableState) value$iv3;
            $composer2.endReplaceableGroup();
            $composer2.startReplaceableGroup(-1192329294);
            ComposerKt.sourceInformation($composer2, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv4 = $composer2.rememberedValue();
            if (it$iv4 == Composer.Companion.getEmpty()) {
                value$iv4 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer2.updateRememberedValue(value$iv4);
            } else {
                value$iv4 = it$iv4;
            }
            final MutableState imageUrl$delegate = (MutableState) value$iv4;
            $composer2.endReplaceableGroup();
            $composer2.startReplaceableGroup(-1192327302);
            ComposerKt.sourceInformation($composer2, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv5 = $composer2.rememberedValue();
            if (it$iv5 == Composer.Companion.getEmpty()) {
                value$iv5 = SnapshotStateKt.mutableStateOf$default((Object) null, (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer2.updateRememberedValue(value$iv5);
            } else {
                value$iv5 = it$iv5;
            }
            final MutableState selectedCategoryId$delegate = (MutableState) value$iv5;
            $composer2.endReplaceableGroup();
            final State categories$delegate = SnapshotStateKt.collectAsState(viewModel.getCategories(), (CoroutineContext) null, $composer2, 0, 1);
            CompositionLocal this_$iv = AndroidCompositionLocals_androidKt.getLocalContext();
            ComposerKt.sourceInformationMarkerStart($composer2, 2023513938, "CC:CompositionLocal.kt#9igjgp");
            Object consume = $composer2.consume(this_$iv);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            final Context context = (Context) consume;
            AndroidDialog_androidKt.Dialog(function0, (DialogProperties) null, ComposableLambdaKt.composableLambda($composer2, 1625213201, true, new Function2<Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$JoinServiceProviderDialog$1
                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
                    invoke((Composer) p1, ((Number) p2).intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(Composer $composer3, int $changed2) {
                    ComposerKt.sourceInformation($composer3, "C442@18617L11,442@18575L62,439@18425L5488:MainActivity.kt#to5c3");
                    if (($changed2 & 3) != 2 || !$composer3.getSkipping()) {
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventStart(1625213201, $changed2, -1, "com.example.JoinServiceProviderDialog.<anonymous> (MainActivity.kt:439)");
                        }
                        CardKt.Card(PaddingKt.padding-3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null), Dp.constructor-impl(16)), RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(16)), CardDefaults.INSTANCE.cardColors-ro_MJ88(MaterialTheme.INSTANCE.getColorScheme($composer3, MaterialTheme.$stable).getSurface-0d7_KjU(), 0L, 0L, 0L, $composer3, CardDefaults.$stable << 12, 14), (CardElevation) null, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer3, -33050493, true, new AnonymousClass1(categories$delegate, function0, context, viewModel, name$delegate, phone$delegate, region$delegate, imageUrl$delegate, selectedCategoryId$delegate)), $composer3, 196614, 24);
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventEnd();
                            return;
                        }
                        return;
                    }
                    $composer3.skipToGroupEnd();
                }

                /* JADX INFO: Access modifiers changed from: package-private */
                /* compiled from: MainActivity.kt */
                @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
                /* renamed from: com.example.MainActivityKt$JoinServiceProviderDialog$1$1, reason: invalid class name */
                /* loaded from: /tmp/dex/classes5.dex */
                public static final class AnonymousClass1 implements Function3<ColumnScope, Composer, Integer, Unit> {
                    final /* synthetic */ State<List<Category>> $categories$delegate;
                    final /* synthetic */ Context $context;
                    final /* synthetic */ MutableState<String> $imageUrl$delegate;
                    final /* synthetic */ MutableState<String> $name$delegate;
                    final /* synthetic */ Function0<Unit> $onDismiss;
                    final /* synthetic */ MutableState<String> $phone$delegate;
                    final /* synthetic */ MutableState<String> $region$delegate;
                    final /* synthetic */ MutableState<Integer> $selectedCategoryId$delegate;
                    final /* synthetic */ DaliliViewModel $viewModel;

                    /* JADX WARN: Multi-variable type inference failed */
                    AnonymousClass1(State<? extends List<Category>> state, Function0<Unit> function0, Context context, DaliliViewModel daliliViewModel, MutableState<String> mutableState, MutableState<String> mutableState2, MutableState<String> mutableState3, MutableState<String> mutableState4, MutableState<Integer> mutableState5) {
                        this.$categories$delegate = state;
                        this.$onDismiss = function0;
                        this.$context = context;
                        this.$viewModel = daliliViewModel;
                        this.$name$delegate = mutableState;
                        this.$phone$delegate = mutableState2;
                        this.$region$delegate = mutableState3;
                        this.$imageUrl$delegate = mutableState4;
                        this.$selectedCategoryId$delegate = mutableState5;
                    }

                    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                        invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                        return Unit.INSTANCE;
                    }

                    /* JADX WARN: Removed duplicated region for block: B:18:0x00e8  */
                    /* JADX WARN: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                        To view partially-correct add '--show-bad-code' argument
                    */
                    public final void invoke(androidx.compose.foundation.layout.ColumnScope r32, androidx.compose.runtime.Composer r33, int r34) {
                        /*
                            Method dump skipped, instructions count: 236
                            To view this dump add '--comments-level debug' option
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$JoinServiceProviderDialog$1.AnonymousClass1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public static final Unit invoke$lambda$1$lambda$0(MutableState $name$delegate, MutableState $phone$delegate, MutableState $region$delegate, MutableState $imageUrl$delegate, State $categories$delegate, MutableState $selectedCategoryId$delegate, Function0 $onDismiss, Context $context, DaliliViewModel $viewModel, LazyListScope $this$LazyColumn) {
                        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
                        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableSingletons$MainActivityKt.INSTANCE.m3getLambda12$app_debug(), 3, (Object) null);
                        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(1983543310, true, new MainActivityKt$JoinServiceProviderDialog$1$1$1$1$1($name$delegate)), 3, (Object) null);
                        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(2041525583, true, new MainActivityKt$JoinServiceProviderDialog$1$1$1$1$2($phone$delegate)), 3, (Object) null);
                        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(2099507856, true, new MainActivityKt$JoinServiceProviderDialog$1$1$1$1$3($region$delegate)), 3, (Object) null);
                        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-2137477167, true, new MainActivityKt$JoinServiceProviderDialog$1$1$1$1$4($imageUrl$delegate)), 3, (Object) null);
                        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-2079494894, true, new MainActivityKt$JoinServiceProviderDialog$1$1$1$1$5($categories$delegate, $selectedCategoryId$delegate)), 3, (Object) null);
                        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-2021512621, true, new MainActivityKt$JoinServiceProviderDialog$1$1$1$1$6($onDismiss, $context, $viewModel, $name$delegate, $phone$delegate, $selectedCategoryId$delegate, $imageUrl$delegate, $region$delegate)), 3, (Object) null);
                        return Unit.INSTANCE;
                    }
                }
            }), $composer2, (($dirty2 >> 3) & 14) | 384, 2);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer2.skipToGroupEnd();
        }
        ScopeUpdateScope endRestartGroup = $composer2.endRestartGroup();
        if (endRestartGroup != null) {
            endRestartGroup.updateScope(new Function2() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda14
                public final Object invoke(Object obj, Object obj2) {
                    Unit JoinServiceProviderDialog$lambda$36;
                    JoinServiceProviderDialog$lambda$36 = MainActivityKt.JoinServiceProviderDialog$lambda$36(DaliliViewModel.this, function0, $changed, (Composer) obj, ((Integer) obj2).intValue());
                    return JoinServiceProviderDialog$lambda$36;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String JoinServiceProviderDialog$lambda$21(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String JoinServiceProviderDialog$lambda$24(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String JoinServiceProviderDialog$lambda$27(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String JoinServiceProviderDialog$lambda$30(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Integer JoinServiceProviderDialog$lambda$33(MutableState<Integer> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (Integer) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List<Category> JoinServiceProviderDialog$lambda$35(State<? extends List<Category>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    /* JADX WARN: Code restructure failed: missing block: B:58:0x0100, code lost:
    
        if (kotlin.text.StringsKt.contains$default(r20.getPhone(), CategoryProvidersScreen$lambda$39(r18), false, 2, (java.lang.Object) null) != false) goto L51;
     */
    /* JADX WARN: Removed duplicated region for block: B:101:0x03a2  */
    /* JADX WARN: Removed duplicated region for block: B:104:0x03d9  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x0572  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x05d0  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0710  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0735  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x0791  */
    /* JADX WARN: Removed duplicated region for block: B:148:0x0869  */
    /* JADX WARN: Removed duplicated region for block: B:162:0x0aac  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0a10  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0747  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0713  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x0848  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x03ef  */
    /* JADX WARN: Removed duplicated region for block: B:186:0x03a8  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x010e  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0111 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0396  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void CategoryProvidersScreen(com.example.data.Category r95, final com.example.ui.DaliliViewModel r96, final kotlin.jvm.functions.Function0<kotlin.Unit> r97, androidx.compose.runtime.Composer r98, final int r99) {
        /*
            Method dump skipped, instructions count: 2759
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.CategoryProvidersScreen(com.example.data.Category, com.example.ui.DaliliViewModel, kotlin.jvm.functions.Function0, androidx.compose.runtime.Composer, int):void");
    }

    private static final List<ServiceProvider> CategoryProvidersScreen$lambda$37(State<? extends List<ServiceProvider>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    private static final List<SubCategory> CategoryProvidersScreen$lambda$38(State<? extends List<SubCategory>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    private static final String CategoryProvidersScreen$lambda$39(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    private static final Integer CategoryProvidersScreen$lambda$43(MutableState<Integer> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (Integer) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit CategoryProvidersScreen$lambda$59$lambda$48$lambda$47(DaliliViewModel $viewModel, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $viewModel.setSearchQuery(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit CategoryProvidersScreen$lambda$59$lambda$54$lambda$50$lambda$49(MutableState $selectedSubCategoryId$delegate) {
        $selectedSubCategoryId$delegate.setValue(null);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit CategoryProvidersScreen$lambda$59$lambda$54$lambda$53$lambda$52$lambda$51(SubCategory $sub, MutableState $selectedSubCategoryId$delegate) {
        $selectedSubCategoryId$delegate.setValue($sub.getId());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit CategoryProvidersScreen$lambda$59$lambda$58$lambda$57(final List $displayedProviders, final Category $category, LazyListScope $this$LazyColumn) {
        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
        final Function1 contentType$iv = new Function1() { // from class: com.example.MainActivityKt$CategoryProvidersScreen$lambda$59$lambda$58$lambda$57$$inlined$items$default$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return m48invoke((ServiceProvider) p1);
            }

            /* renamed from: invoke, reason: collision with other method in class */
            public final Void m48invoke(ServiceProvider serviceProvider) {
                return null;
            }
        };
        $this$LazyColumn.items($displayedProviders.size(), (Function1) null, new Function1<Integer, Object>() { // from class: com.example.MainActivityKt$CategoryProvidersScreen$lambda$59$lambda$58$lambda$57$$inlined$items$default$3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return invoke(((Number) p1).intValue());
            }

            public final Object invoke(int index) {
                return contentType$iv.invoke($displayedProviders.get(index));
            }
        }, ComposableLambdaKt.composableLambdaInstance(-632812321, true, new Function4<LazyItemScope, Integer, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$CategoryProvidersScreen$lambda$59$lambda$58$lambda$57$$inlined$items$default$4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(4);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3, Object p4) {
                invoke((LazyItemScope) p1, ((Number) p2).intValue(), (Composer) p3, ((Number) p4).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyItemScope $this$items, int it, Composer $composer, int $changed) {
                ComposerKt.sourceInformation($composer, "C148@6730L22:LazyDsl.kt#428nma");
                int $dirty = $changed;
                if (($changed & 14) == 0) {
                    $dirty |= $composer.changed($this$items) ? 4 : 2;
                }
                if (($changed & 112) == 0) {
                    $dirty |= $composer.changed(it) ? 32 : 16;
                }
                if (($dirty & 731) == 146 && $composer.getSkipping()) {
                    $composer.skipToGroupEnd();
                    return;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-632812321, $dirty, -1, "androidx.compose.foundation.lazy.items.<anonymous> (LazyDsl.kt:148)");
                }
                int i = $dirty & 14;
                final ServiceProvider provider = (ServiceProvider) $displayedProviders.get(it);
                $composer.startReplaceableGroup(1283613947);
                ComposerKt.sourceInformation($composer, "C*661@27917L11,661@27875L69,658@27704L4109:MainActivity.kt#to5c3");
                Modifier fillMaxWidth$default = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null);
                Shape shape = RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(12));
                CardColors cardColors = CardDefaults.INSTANCE.cardColors-ro_MJ88(MaterialTheme.INSTANCE.getColorScheme($composer, MaterialTheme.$stable).getSurfaceVariant-0d7_KjU(), 0L, 0L, 0L, $composer, CardDefaults.$stable << 12, 14);
                final Category category = $category;
                CardKt.Card(fillMaxWidth$default, shape, cardColors, (CardElevation) null, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer, -1107142963, true, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$CategoryProvidersScreen$1$5$1$1$1
                    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                        invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                        return Unit.INSTANCE;
                    }

                    /* JADX WARN: Removed duplicated region for block: B:29:0x0185  */
                    /* JADX WARN: Removed duplicated region for block: B:32:0x046f  */
                    /* JADX WARN: Removed duplicated region for block: B:35:0x047b  */
                    /* JADX WARN: Removed duplicated region for block: B:43:0x059e  */
                    /* JADX WARN: Removed duplicated region for block: B:46:0x05aa  */
                    /* JADX WARN: Removed duplicated region for block: B:49:0x05e3  */
                    /* JADX WARN: Removed duplicated region for block: B:54:0x06b9  */
                    /* JADX WARN: Removed duplicated region for block: B:57:0x07dc  */
                    /* JADX WARN: Removed duplicated region for block: B:62:0x0849  */
                    /* JADX WARN: Removed duplicated region for block: B:64:? A[RETURN, SYNTHETIC] */
                    /* JADX WARN: Removed duplicated region for block: B:66:0x0703  */
                    /* JADX WARN: Removed duplicated region for block: B:68:0x05f9 A[ADDED_TO_REGION] */
                    /* JADX WARN: Removed duplicated region for block: B:69:0x05b0  */
                    /* JADX WARN: Removed duplicated region for block: B:72:0x0481  */
                    /* JADX WARN: Removed duplicated region for block: B:73:0x0213  */
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                        To view partially-correct add '--show-bad-code' argument
                    */
                    public final void invoke(androidx.compose.foundation.layout.ColumnScope r111, androidx.compose.runtime.Composer r112, int r113) {
                        /*
                            Method dump skipped, instructions count: 2125
                            To view this dump add '--comments-level debug' option
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$CategoryProvidersScreen$1$5$1$1$1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
                    }
                }), $composer, 196614, 24);
                $composer.endReplaceableGroup();
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            }
        }));
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0173  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x01cb A[EDGE_INSN: B:46:0x01cb->B:47:0x01cb BREAK  A[LOOP:0: B:34:0x016e->B:42:0x019a], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x01ef  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void RatingBar(final double r38, androidx.compose.runtime.Composer r40, final int r41) {
        /*
            Method dump skipped, instructions count: 513
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.RatingBar(double, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x02c4  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x02d0  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x03c8  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x04a7  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x04b3  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x04ec  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0585  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x05de  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0661  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x05eb A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0595  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0502 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x04b9  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x03d5 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x02d6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void SmartAssistantScreen(final com.example.ui.DaliliViewModel r78, androidx.compose.runtime.Composer r79, final int r80) {
        /*
            Method dump skipped, instructions count: 1656
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.SmartAssistantScreen(com.example.ui.DaliliViewModel, androidx.compose.runtime.Composer, int):void");
    }

    private static final List<Pair<String, Boolean>> SmartAssistantScreen$lambda$63(State<? extends List<Pair<String, Boolean>>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    private static final boolean SmartAssistantScreen$lambda$64(State<Boolean> state) {
        Object thisObj$iv = state.getValue();
        return ((Boolean) thisObj$iv).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String SmartAssistantScreen$lambda$65(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    private static final String SmartAssistantScreen$lambda$67(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit SmartAssistantScreen$lambda$78$lambda$72$lambda$71$lambda$70(final State $assistantWelcomeText$delegate, State $chatHistory$delegate, State $isAssistantLoading$delegate, LazyListScope $this$LazyColumn) {
        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(17978551, true, new Function3<LazyItemScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$SmartAssistantScreen$1$1$1$1$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
                String SmartAssistantScreen$lambda$65;
                Intrinsics.checkNotNullParameter($this$item, "$this$item");
                ComposerKt.sourceInformation($composer, "C778@33377L58:MainActivity.kt#to5c3");
                if (($changed & 17) != 16 || !$composer.getSkipping()) {
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart(17978551, $changed, -1, "com.example.SmartAssistantScreen.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:778)");
                    }
                    SmartAssistantScreen$lambda$65 = MainActivityKt.SmartAssistantScreen$lambda$65($assistantWelcomeText$delegate);
                    MainActivityKt.ChatBubble(SmartAssistantScreen$lambda$65, false, $composer, 48);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                        return;
                    }
                    return;
                }
                $composer.skipToGroupEnd();
            }
        }), 3, (Object) null);
        final List items$iv = SmartAssistantScreen$lambda$63($chatHistory$delegate);
        final Function1 contentType$iv = new Function1() { // from class: com.example.MainActivityKt$SmartAssistantScreen$lambda$78$lambda$72$lambda$71$lambda$70$$inlined$items$default$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return m51invoke((Pair<? extends String, ? extends Boolean>) p1);
            }

            /* renamed from: invoke, reason: collision with other method in class */
            public final Void m51invoke(Pair<? extends String, ? extends Boolean> pair) {
                return null;
            }
        };
        $this$LazyColumn.items(items$iv.size(), (Function1) null, new Function1<Integer, Object>() { // from class: com.example.MainActivityKt$SmartAssistantScreen$lambda$78$lambda$72$lambda$71$lambda$70$$inlined$items$default$3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return invoke(((Number) p1).intValue());
            }

            public final Object invoke(int index) {
                return contentType$iv.invoke(items$iv.get(index));
            }
        }, ComposableLambdaKt.composableLambdaInstance(-632812321, true, new Function4<LazyItemScope, Integer, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$SmartAssistantScreen$lambda$78$lambda$72$lambda$71$lambda$70$$inlined$items$default$4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(4);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3, Object p4) {
                invoke((LazyItemScope) p1, ((Number) p2).intValue(), (Composer) p3, ((Number) p4).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyItemScope $this$items, int it, Composer $composer, int $changed) {
                ComposerKt.sourceInformation($composer, "C148@6730L22:LazyDsl.kt#428nma");
                int $dirty = $changed;
                if (($changed & 14) == 0) {
                    $dirty |= $composer.changed($this$items) ? 4 : 2;
                }
                if (($changed & 112) == 0) {
                    $dirty |= $composer.changed(it) ? 32 : 16;
                }
                if (($dirty & 731) == 146 && $composer.getSkipping()) {
                    $composer.skipToGroupEnd();
                    return;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-632812321, $dirty, -1, "androidx.compose.foundation.lazy.items.<anonymous> (LazyDsl.kt:148)");
                }
                int i = $dirty & 14;
                Pair messagePair = (Pair) items$iv.get(it);
                $composer.startReplaceableGroup(-2086370926);
                ComposerKt.sourceInformation($composer, "C*783@33564L68:MainActivity.kt#to5c3");
                MainActivityKt.ChatBubble((String) messagePair.getFirst(), ((Boolean) messagePair.getSecond()).booleanValue(), $composer, 0);
                $composer.endReplaceableGroup();
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            }
        }));
        if (SmartAssistantScreen$lambda$64($isAssistantLoading$delegate)) {
            LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableSingletons$MainActivityKt.INSTANCE.m9getLambda18$app_debug(), 3, (Object) null);
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit SmartAssistantScreen$lambda$78$lambda$77$lambda$74$lambda$73(MutableState $inputQuery$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $inputQuery$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit SmartAssistantScreen$lambda$78$lambda$77$lambda$76$lambda$75(DaliliViewModel $viewModel, MutableState $inputQuery$delegate) {
        if (SmartAssistantScreen$lambda$67($inputQuery$delegate).length() > 0) {
            $viewModel.askAssistant(SmartAssistantScreen$lambda$67($inputQuery$delegate));
            $inputQuery$delegate.setValue("");
        }
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x01c0  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x01d8  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x01ff  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0285  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0217  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x01e3  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x01cb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void ChatBubble(final java.lang.String r44, final boolean r45, androidx.compose.runtime.Composer r46, final int r47) {
        /*
            Method dump skipped, instructions count: 663
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.ChatBubble(java.lang.String, boolean, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0176  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void AppInfoScreen(final com.example.ui.DaliliViewModel r30, androidx.compose.runtime.Composer r31, final int r32) {
        /*
            Method dump skipped, instructions count: 392
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.AppInfoScreen(com.example.ui.DaliliViewModel, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AppInfoScreen$lambda$82(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AppInfoScreen$lambda$83(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AppInfoScreen$lambda$84(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AppInfoScreen$lambda$85(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AppInfoScreen$lambda$86(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AppInfoScreen$lambda$87(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AppInfoScreen$lambda$88(State<String> state) {
        Object thisObj$iv = state.getValue();
        return (String) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AppInfoScreen$lambda$90$lambda$89(final State $appLogo$delegate, final State $aboutAppSubtitle$delegate, State $appUpdatesUrl$delegate, final Context $context, State $appShareText$delegate, final State $supportPhone$delegate, final State $supportWhatsapp$delegate, final State $supportEmail$delegate, LazyListScope $this$LazyColumn) {
        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-1711568871, true, new Function3<LazyItemScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AppInfoScreen$1$1$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
                Intrinsics.checkNotNullParameter($this$item, "$this$item");
                ComposerKt.sourceInformation($composer, "C878@36991L1856:MainActivity.kt#to5c3");
                if (($changed & 17) != 16 || !$composer.getSkipping()) {
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart(-1711568871, $changed, -1, "com.example.AppInfoScreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:878)");
                    }
                    Modifier fillMaxWidth$default = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null);
                    Shape shape = RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(16));
                    final State<String> state = $appLogo$delegate;
                    CardKt.Card(fillMaxWidth$default, shape, (CardColors) null, (CardElevation) null, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer, 1012990859, true, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AppInfoScreen$1$1$1.1
                        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                            invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                            return Unit.INSTANCE;
                        }

                        /* JADX WARN: Removed duplicated region for block: B:28:0x0173  */
                        /* JADX WARN: Removed duplicated region for block: B:31:0x043c  */
                        /* JADX WARN: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
                        /* JADX WARN: Removed duplicated region for block: B:34:0x0201  */
                        /*
                            Code decompiled incorrectly, please refer to instructions dump.
                            To view partially-correct add '--show-bad-code' argument
                        */
                        public final void invoke(androidx.compose.foundation.layout.ColumnScope r60, androidx.compose.runtime.Composer r61, int r62) {
                            /*
                                Method dump skipped, instructions count: 1088
                                To view this dump add '--comments-level debug' option
                            */
                            throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AppInfoScreen$1$1$1.AnonymousClass1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
                        }
                    }), $composer, 196614, 28);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                        return;
                    }
                    return;
                }
                $composer.skipToGroupEnd();
            }
        }), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-1730400752, true, new Function3<LazyItemScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AppInfoScreen$1$1$2
            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
                Intrinsics.checkNotNullParameter($this$item, "$this$item");
                ComposerKt.sourceInformation($composer, "C922@38886L956:MainActivity.kt#to5c3");
                if (($changed & 17) != 16 || !$composer.getSkipping()) {
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart(-1730400752, $changed, -1, "com.example.AppInfoScreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:922)");
                    }
                    Modifier fillMaxWidth$default = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null);
                    Shape shape = RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(12));
                    final State<String> state = $aboutAppSubtitle$delegate;
                    CardKt.Card(fillMaxWidth$default, shape, (CardColors) null, (CardElevation) null, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer, 936416514, true, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AppInfoScreen$1$1$2.1
                        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                            invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                            return Unit.INSTANCE;
                        }

                        /* JADX WARN: Removed duplicated region for block: B:24:0x023b  */
                        /* JADX WARN: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
                        /*
                            Code decompiled incorrectly, please refer to instructions dump.
                            To view partially-correct add '--show-bad-code' argument
                        */
                        public final void invoke(androidx.compose.foundation.layout.ColumnScope r79, androidx.compose.runtime.Composer r80, int r81) {
                            /*
                                Method dump skipped, instructions count: 575
                                To view this dump add '--comments-level debug' option
                            */
                            throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AppInfoScreen$1$1$2.AnonymousClass1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
                        }
                    }), $composer, 196614, 28);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                        return;
                    }
                    return;
                }
                $composer.skipToGroupEnd();
            }
        }), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(1116559697, true, new MainActivityKt$AppInfoScreen$1$1$3($appUpdatesUrl$delegate, $context, $appShareText$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-331447150, true, new Function3<LazyItemScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AppInfoScreen$1$1$4
            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
                Intrinsics.checkNotNullParameter($this$item, "$this$item");
                ComposerKt.sourceInformation($composer, "C985@41651L3045:MainActivity.kt#to5c3");
                if (($changed & 17) != 16 || !$composer.getSkipping()) {
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart(-331447150, $changed, -1, "com.example.AppInfoScreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:985)");
                    }
                    CardKt.Card(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null), RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(12)), (CardColors) null, (CardElevation) null, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer, -1959597180, true, new AnonymousClass1($supportPhone$delegate, $context, $supportWhatsapp$delegate, $supportEmail$delegate)), $composer, 196614, 28);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                        return;
                    }
                    return;
                }
                $composer.skipToGroupEnd();
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            /* compiled from: MainActivity.kt */
            @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
            /* renamed from: com.example.MainActivityKt$AppInfoScreen$1$1$4$1, reason: invalid class name */
            /* loaded from: /tmp/dex/classes5.dex */
            public static final class AnonymousClass1 implements Function3<ColumnScope, Composer, Integer, Unit> {
                final /* synthetic */ Context $context;
                final /* synthetic */ State<String> $supportEmail$delegate;
                final /* synthetic */ State<String> $supportPhone$delegate;
                final /* synthetic */ State<String> $supportWhatsapp$delegate;

                AnonymousClass1(State<String> state, Context context, State<String> state2, State<String> state3) {
                    this.$supportPhone$delegate = state;
                    this.$context = context;
                    this.$supportWhatsapp$delegate = state2;
                    this.$supportEmail$delegate = state3;
                }

                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                    invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                    return Unit.INSTANCE;
                }

                /* JADX WARN: Removed duplicated region for block: B:29:0x02a1  */
                /* JADX WARN: Removed duplicated region for block: B:32:0x02ad  */
                /* JADX WARN: Removed duplicated region for block: B:40:0x041e  */
                /* JADX WARN: Removed duplicated region for block: B:45:0x04b7  */
                /* JADX WARN: Removed duplicated region for block: B:48:0x04c3  */
                /* JADX WARN: Removed duplicated region for block: B:51:0x04fc  */
                /* JADX WARN: Removed duplicated region for block: B:56:0x062f  */
                /* JADX WARN: Removed duplicated region for block: B:61:0x06bd  */
                /* JADX WARN: Removed duplicated region for block: B:64:0x06c9  */
                /* JADX WARN: Removed duplicated region for block: B:67:0x06fc  */
                /* JADX WARN: Removed duplicated region for block: B:72:0x0829  */
                /* JADX WARN: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
                /* JADX WARN: Removed duplicated region for block: B:76:0x0712 A[ADDED_TO_REGION] */
                /* JADX WARN: Removed duplicated region for block: B:77:0x06cd  */
                /* JADX WARN: Removed duplicated region for block: B:80:0x0512 A[ADDED_TO_REGION] */
                /* JADX WARN: Removed duplicated region for block: B:81:0x04c9  */
                /* JADX WARN: Removed duplicated region for block: B:85:0x02b3  */
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public final void invoke(androidx.compose.foundation.layout.ColumnScope r96, androidx.compose.runtime.Composer r97, int r98) {
                    /*
                        Method dump skipped, instructions count: 2093
                        To view this dump add '--comments-level debug' option
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AppInfoScreen$1$1$4.AnonymousClass1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
                }

                /* JADX INFO: Access modifiers changed from: private */
                public static final Unit invoke$lambda$9$lambda$1$lambda$0(Context $context, State $supportPhone$delegate) {
                    String AppInfoScreen$lambda$82;
                    AppInfoScreen$lambda$82 = MainActivityKt.AppInfoScreen$lambda$82($supportPhone$delegate);
                    Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + AppInfoScreen$lambda$82));
                    $context.startActivity(intent);
                    return Unit.INSTANCE;
                }

                /* JADX INFO: Access modifiers changed from: private */
                public static final Unit invoke$lambda$9$lambda$4$lambda$3(Context $context, State $supportWhatsapp$delegate) {
                    String AppInfoScreen$lambda$84;
                    AppInfoScreen$lambda$84 = MainActivityKt.AppInfoScreen$lambda$84($supportWhatsapp$delegate);
                    String url = "https://wa.me/" + AppInfoScreen$lambda$84;
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                    $context.startActivity(intent);
                    return Unit.INSTANCE;
                }

                /* JADX INFO: Access modifiers changed from: private */
                public static final Unit invoke$lambda$9$lambda$7$lambda$6(Context $context, State $supportEmail$delegate) {
                    String AppInfoScreen$lambda$83;
                    AppInfoScreen$lambda$83 = MainActivityKt.AppInfoScreen$lambda$83($supportEmail$delegate);
                    Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:" + AppInfoScreen$lambda$83));
                    $context.startActivity(intent);
                    return Unit.INSTANCE;
                }
            }
        }), 3, (Object) null);
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x0284  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void LoginScreen(final com.example.ui.DaliliViewModel r44, final kotlin.jvm.functions.Function0<kotlin.Unit> r45, androidx.compose.runtime.Composer r46, final int r47) {
        /*
            Method dump skipped, instructions count: 662
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.LoginScreen(com.example.ui.DaliliViewModel, kotlin.jvm.functions.Function0, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String LoginScreen$lambda$93(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String LoginScreen$lambda$96(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:100:0x03f1  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x03fb  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x0405  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x0375 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:109:0x035a  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x024f  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x023d  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0249  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0358  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0368  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x03e7  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x04bd  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x04c9  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0502  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x058b  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0640  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x05a8  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x05c7  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x05e4  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0601  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0518 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:99:0x04cf  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void AdminDashboardScreen(final com.example.ui.DaliliViewModel r83, final kotlin.jvm.functions.Function0<kotlin.Unit> r84, androidx.compose.runtime.Composer r85, final int r86) {
        /*
            Method dump skipped, instructions count: 1656
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.AdminDashboardScreen(com.example.ui.DaliliViewModel, kotlin.jvm.functions.Function0, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminDashboardScreen$lambda$101(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminDashboardScreen$lambda$107$lambda$105$lambda$104$lambda$103(DaliliViewModel $viewModel, Function0 $onLogout) {
        $viewModel.logout();
        $onLogout.invoke();
        return Unit.INSTANCE;
    }

    public static final void AdminConfigSubscreen(final DaliliViewModel viewModel, Composer $composer, final int $changed) {
        Object value$iv;
        Object value$iv2;
        Object value$iv3;
        Object value$iv4;
        Object value$iv5;
        Object value$iv6;
        Object value$iv7;
        Object value$iv8;
        Object value$iv9;
        Object value$iv10;
        Object value$iv11;
        Object value$iv12;
        Object value$iv13;
        Object value$iv14;
        Unit unit;
        Context context;
        String str;
        Composer $composer2;
        Composer $composer3;
        Intrinsics.checkNotNullParameter(viewModel, "viewModel");
        Composer $composer4 = $composer.startRestartGroup(366082977);
        ComposerKt.sourceInformation($composer4, "C(AdminConfigSubscreen)1191@50174L31,1192@50229L31,1193@50285L31,1194@50336L31,1195@50392L31,1196@50448L31,1197@50507L31,1198@50561L31,1199@50621L31,1200@50678L31,1201@50734L31,1202@50798L31,1203@50852L33,1204@50911L40,1206@50984L7,1209@51062L750,1209@51041L771,1229@51937L4565,1226@51818L4684:MainActivity.kt#to5c3");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer4.changedInstance(viewModel) ? 4 : 2;
        }
        int $dirty2 = $dirty;
        if (($dirty2 & 3) != 2 || !$composer4.getSkipping()) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(366082977, $dirty2, -1, "com.example.AdminConfigSubscreen (MainActivity.kt:1190)");
            }
            $composer4.startReplaceableGroup(464606859);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv = $composer4.rememberedValue();
            if (it$iv == Composer.Companion.getEmpty()) {
                value$iv = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv;
            }
            final MutableState appName$delegate = (MutableState) value$iv;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464608619);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv2 = $composer4.rememberedValue();
            if (it$iv2 == Composer.Companion.getEmpty()) {
                value$iv2 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv2);
            } else {
                value$iv2 = it$iv2;
            }
            final MutableState welcomeText$delegate = (MutableState) value$iv2;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464610411);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv3 = $composer4.rememberedValue();
            if (it$iv3 == Composer.Companion.getEmpty()) {
                value$iv3 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv3);
            } else {
                value$iv3 = it$iv3;
            }
            final MutableState welcomeImage$delegate = (MutableState) value$iv3;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464612043);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv4 = $composer4.rememberedValue();
            if (it$iv4 == Composer.Companion.getEmpty()) {
                value$iv4 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv4);
            } else {
                value$iv4 = it$iv4;
            }
            final MutableState appLogo$delegate = (MutableState) value$iv4;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464613835);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv5 = $composer4.rememberedValue();
            if (it$iv5 == Composer.Companion.getEmpty()) {
                value$iv5 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv5);
            } else {
                value$iv5 = it$iv5;
            }
            final MutableState supportPhone$delegate = (MutableState) value$iv5;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464615627);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv6 = $composer4.rememberedValue();
            if (it$iv6 == Composer.Companion.getEmpty()) {
                value$iv6 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv6);
            } else {
                value$iv6 = it$iv6;
            }
            final MutableState supportEmail$delegate = (MutableState) value$iv6;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464617515);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv7 = $composer4.rememberedValue();
            if (it$iv7 == Composer.Companion.getEmpty()) {
                value$iv7 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv7);
            } else {
                value$iv7 = it$iv7;
            }
            final MutableState supportWhatsapp$delegate = (MutableState) value$iv7;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464619243);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv8 = $composer4.rememberedValue();
            if (it$iv8 == Composer.Companion.getEmpty()) {
                value$iv8 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv8);
            } else {
                value$iv8 = it$iv8;
            }
            final MutableState footerText$delegate = (MutableState) value$iv8;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464621163);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv9 = $composer4.rememberedValue();
            if (it$iv9 == Composer.Companion.getEmpty()) {
                value$iv9 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv9);
            } else {
                value$iv9 = it$iv9;
            }
            final MutableState aboutAppSubtitle$delegate = (MutableState) value$iv9;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464622987);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv10 = $composer4.rememberedValue();
            if (it$iv10 == Composer.Companion.getEmpty()) {
                value$iv10 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv10);
            } else {
                value$iv10 = it$iv10;
            }
            final MutableState appUpdatesUrl$delegate = (MutableState) value$iv10;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464624779);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv11 = $composer4.rememberedValue();
            if (it$iv11 == Composer.Companion.getEmpty()) {
                value$iv11 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv11);
            } else {
                value$iv11 = it$iv11;
            }
            final MutableState appShareText$delegate = (MutableState) value$iv11;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464626827);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv12 = $composer4.rememberedValue();
            if (it$iv12 == Composer.Companion.getEmpty()) {
                value$iv12 = SnapshotStateKt.mutableStateOf$default("", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv12);
            } else {
                value$iv12 = it$iv12;
            }
            final MutableState assistantWelcomeText$delegate = (MutableState) value$iv12;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464628557);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv13 = $composer4.rememberedValue();
            if (it$iv13 == Composer.Companion.getEmpty()) {
                value$iv13 = SnapshotStateKt.mutableStateOf$default(true, (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv13);
            } else {
                value$iv13 = it$iv13;
            }
            final MutableState showFooter$delegate = (MutableState) value$iv13;
            $composer4.endReplaceableGroup();
            $composer4.startReplaceableGroup(464630452);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            Object it$iv14 = $composer4.rememberedValue();
            if (it$iv14 == Composer.Companion.getEmpty()) {
                value$iv14 = SnapshotStateKt.mutableStateOf$default("red_black", (SnapshotMutationPolicy) null, 2, (Object) null);
                $composer4.updateRememberedValue(value$iv14);
            } else {
                value$iv14 = it$iv14;
            }
            final MutableState selectedTheme$delegate = (MutableState) value$iv14;
            $composer4.endReplaceableGroup();
            CompositionLocal this_$iv = AndroidCompositionLocals_androidKt.getLocalContext();
            ComposerKt.sourceInformationMarkerStart($composer4, 2023513938, "CC:CompositionLocal.kt#9igjgp");
            Object consume = $composer4.consume(this_$iv);
            ComposerKt.sourceInformationMarkerEnd($composer4);
            Context context2 = (Context) consume;
            Unit unit2 = Unit.INSTANCE;
            $composer4.startReplaceableGroup(464635994);
            ComposerKt.sourceInformation($composer4, "CC(remember):MainActivity.kt#9igjgp");
            boolean invalid$iv = $composer4.changedInstance(viewModel);
            Object value$iv15 = $composer4.rememberedValue();
            if (invalid$iv || value$iv15 == Composer.Companion.getEmpty()) {
                unit = unit2;
                context = context2;
                str = "CC(remember):MainActivity.kt#9igjgp";
                $composer2 = $composer4;
                value$iv15 = new MainActivityKt$AdminConfigSubscreen$1$1(viewModel, appName$delegate, welcomeText$delegate, welcomeImage$delegate, appLogo$delegate, supportPhone$delegate, supportEmail$delegate, supportWhatsapp$delegate, footerText$delegate, aboutAppSubtitle$delegate, appUpdatesUrl$delegate, appShareText$delegate, assistantWelcomeText$delegate, showFooter$delegate, selectedTheme$delegate, null);
                $composer4.updateRememberedValue(value$iv15);
            } else {
                unit = unit2;
                context = context2;
                str = "CC(remember):MainActivity.kt#9igjgp";
                $composer2 = $composer4;
            }
            $composer2.endReplaceableGroup();
            Composer $composer5 = $composer2;
            EffectsKt.LaunchedEffect(unit, (Function2) value$iv15, $composer5, 6);
            Modifier fillMaxSize$default = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0f, 1, (Object) null);
            Arrangement.Vertical vertical = Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl(12));
            $composer5.startReplaceableGroup(464667809);
            ComposerKt.sourceInformation($composer5, str);
            final Context context3 = context;
            boolean invalid$iv2 = $composer5.changedInstance(viewModel) | $composer5.changedInstance(context3);
            Object value$iv16 = $composer5.rememberedValue();
            if (invalid$iv2 || value$iv16 == Composer.Companion.getEmpty()) {
                $composer3 = $composer5;
                value$iv16 = new Function1() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda0
                    public final Object invoke(Object obj) {
                        Unit AdminConfigSubscreen$lambda$153$lambda$152;
                        AdminConfigSubscreen$lambda$153$lambda$152 = MainActivityKt.AdminConfigSubscreen$lambda$153$lambda$152(selectedTheme$delegate, appName$delegate, welcomeText$delegate, welcomeImage$delegate, appLogo$delegate, aboutAppSubtitle$delegate, appUpdatesUrl$delegate, appShareText$delegate, assistantWelcomeText$delegate, supportPhone$delegate, supportWhatsapp$delegate, supportEmail$delegate, footerText$delegate, viewModel, context3, showFooter$delegate, (LazyListScope) obj);
                        return AdminConfigSubscreen$lambda$153$lambda$152;
                    }
                };
                $composer5.updateRememberedValue(value$iv16);
            } else {
                $composer3 = $composer5;
            }
            $composer3.endReplaceableGroup();
            LazyDslKt.LazyColumn(fillMaxSize$default, (LazyListState) null, (PaddingValues) null, false, vertical, (Alignment.Horizontal) null, (FlingBehavior) null, false, (Function1) value$iv16, $composer3, 24582, 238);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer4.skipToGroupEnd();
            $composer3 = $composer4;
        }
        ScopeUpdateScope endRestartGroup = $composer3.endRestartGroup();
        if (endRestartGroup != null) {
            endRestartGroup.updateScope(new Function2() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda11
                public final Object invoke(Object obj, Object obj2) {
                    Unit AdminConfigSubscreen$lambda$154;
                    AdminConfigSubscreen$lambda$154 = MainActivityKt.AdminConfigSubscreen$lambda$154(DaliliViewModel.this, $changed, (Composer) obj, ((Integer) obj2).intValue());
                    return AdminConfigSubscreen$lambda$154;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$110(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$113(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$116(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$119(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$122(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$125(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$128(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$131(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$134(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$137(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$140(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$143(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean AdminConfigSubscreen$lambda$146(MutableState<Boolean> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return ((Boolean) $this$getValue$iv.getValue()).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void AdminConfigSubscreen$lambda$147(MutableState<Boolean> mutableState, boolean z) {
        Object value$iv = Boolean.valueOf(z);
        mutableState.setValue(value$iv);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminConfigSubscreen$lambda$149(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminConfigSubscreen$lambda$153$lambda$152(MutableState $selectedTheme$delegate, MutableState $appName$delegate, MutableState $welcomeText$delegate, MutableState $welcomeImage$delegate, MutableState $appLogo$delegate, MutableState $aboutAppSubtitle$delegate, MutableState $appUpdatesUrl$delegate, MutableState $appShareText$delegate, MutableState $assistantWelcomeText$delegate, MutableState $supportPhone$delegate, MutableState $supportWhatsapp$delegate, MutableState $supportEmail$delegate, MutableState $footerText$delegate, DaliliViewModel $viewModel, Context $context, MutableState $showFooter$delegate, LazyListScope $this$LazyColumn) {
        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(1061261, true, new MainActivityKt$AdminConfigSubscreen$2$1$1($selectedTheme$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-513919050, true, new MainActivityKt$AdminConfigSubscreen$2$1$2($appName$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(1363349845, true, new MainActivityKt$AdminConfigSubscreen$2$1$3($welcomeText$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-1054348556, true, new MainActivityKt$AdminConfigSubscreen$2$1$4($welcomeImage$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(822920339, true, new MainActivityKt$AdminConfigSubscreen$2$1$5($appLogo$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-1594778062, true, new MainActivityKt$AdminConfigSubscreen$2$1$6($aboutAppSubtitle$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(282490833, true, new MainActivityKt$AdminConfigSubscreen$2$1$7($appUpdatesUrl$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-2135207568, true, new MainActivityKt$AdminConfigSubscreen$2$1$8($appShareText$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-257938673, true, new MainActivityKt$AdminConfigSubscreen$2$1$9($assistantWelcomeText$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(1619330222, true, new MainActivityKt$AdminConfigSubscreen$2$1$10($supportPhone$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-535478860, true, new MainActivityKt$AdminConfigSubscreen$2$1$11($supportWhatsapp$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(1341790035, true, new MainActivityKt$AdminConfigSubscreen$2$1$12($supportEmail$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(-1075908366, true, new MainActivityKt$AdminConfigSubscreen$2$1$13($footerText$delegate)), 3, (Object) null);
        LazyListScope.item$default($this$LazyColumn, (Object) null, (Object) null, ComposableLambdaKt.composableLambdaInstance(801360529, true, new MainActivityKt$AdminConfigSubscreen$2$1$14($viewModel, $context, $selectedTheme$delegate, $appName$delegate, $welcomeText$delegate, $welcomeImage$delegate, $appLogo$delegate, $supportPhone$delegate, $supportEmail$delegate, $supportWhatsapp$delegate, $footerText$delegate, $showFooter$delegate, $aboutAppSubtitle$delegate, $appUpdatesUrl$delegate, $appShareText$delegate, $assistantWelcomeText$delegate)), 3, (Object) null);
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x0301  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void AdminCategoriesSubscreen(final com.example.ui.DaliliViewModel r50, androidx.compose.runtime.Composer r51, final int r52) {
        /*
            Method dump skipped, instructions count: 792
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.AdminCategoriesSubscreen(com.example.ui.DaliliViewModel, androidx.compose.runtime.Composer, int):void");
    }

    private static final List<Category> AdminCategoriesSubscreen$lambda$155(State<? extends List<Category>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminCategoriesSubscreen$lambda$157(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminCategoriesSubscreen$lambda$160(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminCategoriesSubscreen$lambda$163(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminCategoriesSubscreen$lambda$170$lambda$169$lambda$168(State $categories$delegate, final DaliliViewModel $viewModel, final Context $context, LazyListScope $this$LazyColumn) {
        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
        final List items$iv = AdminCategoriesSubscreen$lambda$155($categories$delegate);
        final Function1 contentType$iv = new Function1() { // from class: com.example.MainActivityKt$AdminCategoriesSubscreen$lambda$170$lambda$169$lambda$168$$inlined$items$default$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return m46invoke((Category) p1);
            }

            /* renamed from: invoke, reason: collision with other method in class */
            public final Void m46invoke(Category category) {
                return null;
            }
        };
        $this$LazyColumn.items(items$iv.size(), (Function1) null, new Function1<Integer, Object>() { // from class: com.example.MainActivityKt$AdminCategoriesSubscreen$lambda$170$lambda$169$lambda$168$$inlined$items$default$3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return invoke(((Number) p1).intValue());
            }

            public final Object invoke(int index) {
                return contentType$iv.invoke(items$iv.get(index));
            }
        }, ComposableLambdaKt.composableLambdaInstance(-632812321, true, new Function4<LazyItemScope, Integer, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AdminCategoriesSubscreen$lambda$170$lambda$169$lambda$168$$inlined$items$default$4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(4);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3, Object p4) {
                invoke((LazyItemScope) p1, ((Number) p2).intValue(), (Composer) p3, ((Number) p4).intValue());
                return Unit.INSTANCE;
            }

            /* JADX WARN: Removed duplicated region for block: B:47:0x02da  */
            /* JADX WARN: Removed duplicated region for block: B:49:? A[RETURN, SYNTHETIC] */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public final void invoke(androidx.compose.foundation.lazy.LazyItemScope r61, int r62, androidx.compose.runtime.Composer r63, int r64) {
                /*
                    Method dump skipped, instructions count: 734
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AdminCategoriesSubscreen$lambda$170$lambda$169$lambda$168$$inlined$items$default$4.invoke(androidx.compose.foundation.lazy.LazyItemScope, int, androidx.compose.runtime.Composer, int):void");
            }
        }));
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x038c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void AdminProvidersSubscreen(final com.example.ui.DaliliViewModel r56, androidx.compose.runtime.Composer r57, final int r58) {
        /*
            Method dump skipped, instructions count: 931
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt.AdminProvidersSubscreen(com.example.ui.DaliliViewModel, androidx.compose.runtime.Composer, int):void");
    }

    private static final List<ServiceProvider> AdminProvidersSubscreen$lambda$172(State<? extends List<ServiceProvider>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List<Category> AdminProvidersSubscreen$lambda$173(State<? extends List<Category>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminProvidersSubscreen$lambda$175(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminProvidersSubscreen$lambda$178(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String AdminProvidersSubscreen$lambda$181(MutableState<String> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (String) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Integer AdminProvidersSubscreen$lambda$184(MutableState<Integer> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return (Integer) $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean AdminProvidersSubscreen$lambda$187(MutableState<Boolean> mutableState) {
        State $this$getValue$iv = (State) mutableState;
        return ((Boolean) $this$getValue$iv.getValue()).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void AdminProvidersSubscreen$lambda$188(MutableState<Boolean> mutableState, boolean z) {
        Object value$iv = Boolean.valueOf(z);
        mutableState.setValue(value$iv);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminProvidersSubscreen$lambda$194$lambda$193$lambda$192(State $providers$delegate, final DaliliViewModel $viewModel, final Context $context, LazyListScope $this$LazyColumn) {
        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
        final List items$iv = AdminProvidersSubscreen$lambda$172($providers$delegate);
        final Function1 contentType$iv = new Function1() { // from class: com.example.MainActivityKt$AdminProvidersSubscreen$lambda$194$lambda$193$lambda$192$$inlined$items$default$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return m47invoke((ServiceProvider) p1);
            }

            /* renamed from: invoke, reason: collision with other method in class */
            public final Void m47invoke(ServiceProvider serviceProvider) {
                return null;
            }
        };
        $this$LazyColumn.items(items$iv.size(), (Function1) null, new Function1<Integer, Object>() { // from class: com.example.MainActivityKt$AdminProvidersSubscreen$lambda$194$lambda$193$lambda$192$$inlined$items$default$3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return invoke(((Number) p1).intValue());
            }

            public final Object invoke(int index) {
                return contentType$iv.invoke(items$iv.get(index));
            }
        }, ComposableLambdaKt.composableLambdaInstance(-632812321, true, new Function4<LazyItemScope, Integer, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AdminProvidersSubscreen$lambda$194$lambda$193$lambda$192$$inlined$items$default$4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(4);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3, Object p4) {
                invoke((LazyItemScope) p1, ((Number) p2).intValue(), (Composer) p3, ((Number) p4).intValue());
                return Unit.INSTANCE;
            }

            /* JADX WARN: Removed duplicated region for block: B:47:0x02cc  */
            /* JADX WARN: Removed duplicated region for block: B:49:? A[RETURN, SYNTHETIC] */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public final void invoke(androidx.compose.foundation.lazy.LazyItemScope r60, int r61, androidx.compose.runtime.Composer r62, int r63) {
                /*
                    Method dump skipped, instructions count: 720
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AdminProvidersSubscreen$lambda$194$lambda$193$lambda$192$$inlined$items$default$4.invoke(androidx.compose.foundation.lazy.LazyItemScope, int, androidx.compose.runtime.Composer, int):void");
            }
        }));
        return Unit.INSTANCE;
    }

    public static final void AdminApplicationsSubscreen(final DaliliViewModel viewModel, Composer $composer, final int $changed) {
        Object value$iv;
        Function0 factory$iv$iv$iv;
        Intrinsics.checkNotNullParameter(viewModel, "viewModel");
        Composer $composer2 = $composer.startRestartGroup(-991783200);
        ComposerKt.sourceInformation($composer2, "C(AdminApplicationsSubscreen)1486@64158L16,1487@64206L7,1488@64257L16:MainActivity.kt#to5c3");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer2.changedInstance(viewModel) ? 4 : 2;
        }
        int $dirty2 = $dirty;
        if (($dirty2 & 3) == 2 && $composer2.getSkipping()) {
            $composer2.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-991783200, $dirty2, -1, "com.example.AdminApplicationsSubscreen (MainActivity.kt:1485)");
            }
            State pendingProviders$delegate = SnapshotStateKt.collectAsState(viewModel.getPendingProviders(), (CoroutineContext) null, $composer2, 0, 1);
            CompositionLocal this_$iv = AndroidCompositionLocals_androidKt.getLocalContext();
            ComposerKt.sourceInformationMarkerStart($composer2, 2023513938, "CC:CompositionLocal.kt#9igjgp");
            Object consume = $composer2.consume(this_$iv);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            final Context context = (Context) consume;
            final State categories$delegate = SnapshotStateKt.collectAsState(viewModel.getCategories(), (CoroutineContext) null, $composer2, 0, 1);
            Iterable $this$filter$iv = AdminApplicationsSubscreen$lambda$196(pendingProviders$delegate);
            Collection destination$iv$iv = new ArrayList();
            for (Object element$iv$iv : $this$filter$iv) {
                PendingProvider it = (PendingProvider) element$iv$iv;
                if (Intrinsics.areEqual(it.getStatus(), "pending")) {
                    destination$iv$iv.add(element$iv$iv);
                }
            }
            final List pFiltered = (List) destination$iv$iv;
            if (pFiltered.isEmpty()) {
                $composer2.startReplaceableGroup(-706513736);
                ComposerKt.sourceInformation($composer2, "1493@64386L207");
                Modifier modifier$iv = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0f, 1, (Object) null);
                Alignment contentAlignment$iv = Alignment.Companion.getCenter();
                $composer2.startReplaceableGroup(733328855);
                ComposerKt.sourceInformation($composer2, "CC(Box)P(2,1,3)71@3309L67,72@3381L130:Box.kt#2w3rfo");
                MeasurePolicy measurePolicy$iv = BoxKt.rememberBoxMeasurePolicy(contentAlignment$iv, false, $composer2, ((54 >> 3) & 14) | ((54 >> 3) & 112));
                int $changed$iv$iv = (54 << 3) & 112;
                $composer2.startReplaceableGroup(-1323940314);
                ComposerKt.sourceInformation($composer2, "CC(Layout)P(!1,2)78@3182L23,80@3272L420:Layout.kt#80mrfh");
                int compositeKeyHash$iv$iv = ComposablesKt.getCurrentCompositeKeyHash($composer2, 0);
                CompositionLocalMap localMap$iv$iv = $composer2.getCurrentCompositionLocalMap();
                Function0 factory$iv$iv$iv2 = ComposeUiNode.Companion.getConstructor();
                Function3 skippableUpdate$iv$iv$iv = LayoutKt.modifierMaterializerOf(modifier$iv);
                int $changed$iv$iv$iv = (($changed$iv$iv << 9) & 7168) | 6;
                if (!($composer2.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer2.startReusableNode();
                if ($composer2.getInserting()) {
                    factory$iv$iv$iv = factory$iv$iv$iv2;
                    $composer2.createNode(factory$iv$iv$iv);
                } else {
                    factory$iv$iv$iv = factory$iv$iv$iv2;
                    $composer2.useNode();
                }
                Composer $this$Layout_u24lambda_u240$iv$iv = Updater.constructor-impl($composer2);
                Updater.set-impl($this$Layout_u24lambda_u240$iv$iv, measurePolicy$iv, ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl($this$Layout_u24lambda_u240$iv$iv, localMap$iv$iv, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 block$iv$iv$iv = ComposeUiNode.Companion.getSetCompositeKeyHash();
                if (!$this$Layout_u24lambda_u240$iv$iv.getInserting() && Intrinsics.areEqual($this$Layout_u24lambda_u240$iv$iv.rememberedValue(), Integer.valueOf(compositeKeyHash$iv$iv))) {
                    skippableUpdate$iv$iv$iv.invoke(SkippableUpdater.box-impl(SkippableUpdater.constructor-impl($composer2)), $composer2, Integer.valueOf(($changed$iv$iv$iv >> 3) & 112));
                    $composer2.startReplaceableGroup(2058660585);
                    int i = ($changed$iv$iv$iv >> 9) & 14;
                    ComposerKt.sourceInformationMarkerStart($composer2, -1253629263, "C73@3426L9:Box.kt#2w3rfo");
                    BoxScope boxScope = BoxScopeInstance.INSTANCE;
                    int i2 = ((54 >> 6) & 112) | 6;
                    ComposerKt.sourceInformationMarkerStart($composer2, 262897806, "C1494@64562L10,1494@64476L107:MainActivity.kt#to5c3");
                    TextKt.Text--4IGK_g("لا توجد طلبات انضمام معلقة حالياً.", (Modifier) null, Color.Companion.getWhite-0d7_KjU(), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, MaterialTheme.INSTANCE.getTypography($composer2, MaterialTheme.$stable).getBodyLarge(), $composer2, 390, 0, 65530);
                    ComposerKt.sourceInformationMarkerEnd($composer2);
                    ComposerKt.sourceInformationMarkerEnd($composer2);
                    $composer2.endReplaceableGroup();
                    $composer2.endNode();
                    $composer2.endReplaceableGroup();
                    $composer2.endReplaceableGroup();
                    $composer2.endReplaceableGroup();
                }
                $this$Layout_u24lambda_u240$iv$iv.updateRememberedValue(Integer.valueOf(compositeKeyHash$iv$iv));
                $this$Layout_u24lambda_u240$iv$iv.apply(Integer.valueOf(compositeKeyHash$iv$iv), block$iv$iv$iv);
                skippableUpdate$iv$iv$iv.invoke(SkippableUpdater.box-impl(SkippableUpdater.constructor-impl($composer2)), $composer2, Integer.valueOf(($changed$iv$iv$iv >> 3) & 112));
                $composer2.startReplaceableGroup(2058660585);
                int i3 = ($changed$iv$iv$iv >> 9) & 14;
                ComposerKt.sourceInformationMarkerStart($composer2, -1253629263, "C73@3426L9:Box.kt#2w3rfo");
                BoxScope boxScope2 = BoxScopeInstance.INSTANCE;
                int i22 = ((54 >> 6) & 112) | 6;
                ComposerKt.sourceInformationMarkerStart($composer2, 262897806, "C1494@64562L10,1494@64476L107:MainActivity.kt#to5c3");
                TextKt.Text--4IGK_g("لا توجد طلبات انضمام معلقة حالياً.", (Modifier) null, Color.Companion.getWhite-0d7_KjU(), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, MaterialTheme.INSTANCE.getTypography($composer2, MaterialTheme.$stable).getBodyLarge(), $composer2, 390, 0, 65530);
                ComposerKt.sourceInformationMarkerEnd($composer2);
                ComposerKt.sourceInformationMarkerEnd($composer2);
                $composer2.endReplaceableGroup();
                $composer2.endNode();
                $composer2.endReplaceableGroup();
                $composer2.endReplaceableGroup();
                $composer2.endReplaceableGroup();
            } else {
                $composer2.startReplaceableGroup(-706158414);
                ComposerKt.sourceInformation($composer2, "1497@64677L4279,1497@64615L4341");
                Arrangement.Vertical vertical = Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl(16));
                $composer2.startReplaceableGroup(-299871742);
                ComposerKt.sourceInformation($composer2, "CC(remember):MainActivity.kt#9igjgp");
                boolean invalid$iv = $composer2.changedInstance(pFiltered) | $composer2.changed(categories$delegate) | $composer2.changedInstance(viewModel) | $composer2.changedInstance(context);
                Object it$iv = $composer2.rememberedValue();
                if (invalid$iv || it$iv == Composer.Companion.getEmpty()) {
                    value$iv = new Function1() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda2
                        public final Object invoke(Object obj) {
                            Unit AdminApplicationsSubscreen$lambda$203$lambda$202;
                            AdminApplicationsSubscreen$lambda$203$lambda$202 = MainActivityKt.AdminApplicationsSubscreen$lambda$203$lambda$202(pFiltered, categories$delegate, viewModel, context, (LazyListScope) obj);
                            return AdminApplicationsSubscreen$lambda$203$lambda$202;
                        }
                    };
                    $composer2.updateRememberedValue(value$iv);
                } else {
                    value$iv = it$iv;
                }
                $composer2.endReplaceableGroup();
                LazyDslKt.LazyColumn((Modifier) null, (LazyListState) null, (PaddingValues) null, false, vertical, (Alignment.Horizontal) null, (FlingBehavior) null, false, (Function1) value$iv, $composer2, 24576, 239);
                $composer2.endReplaceableGroup();
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope endRestartGroup = $composer2.endRestartGroup();
        if (endRestartGroup != null) {
            endRestartGroup.updateScope(new Function2() { // from class: com.example.MainActivityKt$$ExternalSyntheticLambda3
                public final Object invoke(Object obj, Object obj2) {
                    Unit AdminApplicationsSubscreen$lambda$204;
                    AdminApplicationsSubscreen$lambda$204 = MainActivityKt.AdminApplicationsSubscreen$lambda$204(DaliliViewModel.this, $changed, (Composer) obj, ((Integer) obj2).intValue());
                    return AdminApplicationsSubscreen$lambda$204;
                }
            });
        }
    }

    private static final List<PendingProvider> AdminApplicationsSubscreen$lambda$196(State<? extends List<PendingProvider>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List<Category> AdminApplicationsSubscreen$lambda$197(State<? extends List<Category>> state) {
        Object thisObj$iv = state.getValue();
        return (List) thisObj$iv;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit AdminApplicationsSubscreen$lambda$203$lambda$202(final List $pFiltered, final State $categories$delegate, final DaliliViewModel $viewModel, final Context $context, LazyListScope $this$LazyColumn) {
        Intrinsics.checkNotNullParameter($this$LazyColumn, "$this$LazyColumn");
        final Function1 contentType$iv = new Function1() { // from class: com.example.MainActivityKt$AdminApplicationsSubscreen$lambda$203$lambda$202$$inlined$items$default$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return m45invoke((PendingProvider) p1);
            }

            /* renamed from: invoke, reason: collision with other method in class */
            public final Void m45invoke(PendingProvider pendingProvider) {
                return null;
            }
        };
        $this$LazyColumn.items($pFiltered.size(), (Function1) null, new Function1<Integer, Object>() { // from class: com.example.MainActivityKt$AdminApplicationsSubscreen$lambda$203$lambda$202$$inlined$items$default$3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return invoke(((Number) p1).intValue());
            }

            public final Object invoke(int index) {
                return contentType$iv.invoke($pFiltered.get(index));
            }
        }, ComposableLambdaKt.composableLambdaInstance(-632812321, true, new Function4<LazyItemScope, Integer, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AdminApplicationsSubscreen$lambda$203$lambda$202$$inlined$items$default$4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(4);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3, Object p4) {
                invoke((LazyItemScope) p1, ((Number) p2).intValue(), (Composer) p3, ((Number) p4).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyItemScope $this$items, int it, Composer $composer, int $changed) {
                List AdminApplicationsSubscreen$lambda$197;
                Object obj;
                ComposerKt.sourceInformation($composer, "C148@6730L22:LazyDsl.kt#428nma");
                int $dirty = $changed;
                if (($changed & 14) == 0) {
                    $dirty |= $composer.changed($this$items) ? 4 : 2;
                }
                if (($changed & 112) == 0) {
                    $dirty |= $composer.changed(it) ? 32 : 16;
                }
                if (($dirty & 731) == 146 && $composer.getSkipping()) {
                    $composer.skipToGroupEnd();
                    return;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-632812321, $dirty, -1, "androidx.compose.foundation.lazy.items.<anonymous> (LazyDsl.kt:148)");
                }
                int i = $dirty & 14;
                final PendingProvider pending = (PendingProvider) $pFiltered.get(it);
                $composer.startReplaceableGroup(555627320);
                ComposerKt.sourceInformation($composer, "C*1500@64817L4115:MainActivity.kt#to5c3");
                AdminApplicationsSubscreen$lambda$197 = MainActivityKt.AdminApplicationsSubscreen$lambda$197($categories$delegate);
                Iterator it2 = AdminApplicationsSubscreen$lambda$197.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        obj = null;
                        break;
                    }
                    obj = it2.next();
                    Integer id = ((Category) obj).getId();
                    if (id != null && id.intValue() == pending.getCategoryId()) {
                        break;
                    }
                }
                final Category parentCat = (Category) obj;
                Modifier fillMaxWidth$default = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null);
                Shape shape = RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(12));
                final DaliliViewModel daliliViewModel = $viewModel;
                final Context context = $context;
                CardKt.Card(fillMaxWidth$default, shape, (CardColors) null, (CardElevation) null, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer, 780214516, true, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AdminApplicationsSubscreen$2$1$1$1
                    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                        invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                        return Unit.INSTANCE;
                    }

                    /* JADX WARN: Removed duplicated region for block: B:114:0x01ff  */
                    /* JADX WARN: Removed duplicated region for block: B:24:0x01ed  */
                    /* JADX WARN: Removed duplicated region for block: B:27:0x01f9  */
                    /* JADX WARN: Removed duplicated region for block: B:35:0x02b6  */
                    /* JADX WARN: Removed duplicated region for block: B:39:0x02c2  */
                    /* JADX WARN: Removed duplicated region for block: B:42:0x0597  */
                    /* JADX WARN: Removed duplicated region for block: B:45:0x05a3  */
                    /* JADX WARN: Removed duplicated region for block: B:48:0x05dc  */
                    /* JADX WARN: Removed duplicated region for block: B:53:0x06f3  */
                    /* JADX WARN: Removed duplicated region for block: B:56:0x074a  */
                    /* JADX WARN: Removed duplicated region for block: B:61:0x083c  */
                    /* JADX WARN: Removed duplicated region for block: B:64:0x0848  */
                    /* JADX WARN: Removed duplicated region for block: B:67:0x087b  */
                    /* JADX WARN: Removed duplicated region for block: B:72:0x091e  */
                    /* JADX WARN: Removed duplicated region for block: B:77:0x09a8  */
                    /* JADX WARN: Removed duplicated region for block: B:82:0x0a22  */
                    /* JADX WARN: Removed duplicated region for block: B:84:? A[RETURN, SYNTHETIC] */
                    /* JADX WARN: Removed duplicated region for block: B:86:0x09b5 A[ADDED_TO_REGION] */
                    /* JADX WARN: Removed duplicated region for block: B:88:0x092f A[ADDED_TO_REGION] */
                    /* JADX WARN: Removed duplicated region for block: B:90:0x0891 A[ADDED_TO_REGION] */
                    /* JADX WARN: Removed duplicated region for block: B:91:0x084c  */
                    /* JADX WARN: Removed duplicated region for block: B:94:0x05f2 A[ADDED_TO_REGION] */
                    /* JADX WARN: Removed duplicated region for block: B:95:0x05a9  */
                    /* JADX WARN: Removed duplicated region for block: B:96:0x0352  */
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                        To view partially-correct add '--show-bad-code' argument
                    */
                    public final void invoke(androidx.compose.foundation.layout.ColumnScope r133, androidx.compose.runtime.Composer r134, int r135) {
                        /*
                            Method dump skipped, instructions count: 2598
                            To view this dump add '--comments-level debug' option
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AdminApplicationsSubscreen$2$1$1$1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
                    }
                }), $composer, 196614, 28);
                $composer.endReplaceableGroup();
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            }
        }));
        return Unit.INSTANCE;
    }
}
