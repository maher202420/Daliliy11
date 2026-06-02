package com.example;

import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.AddKt;
import androidx.compose.material.icons.filled.ArrowBackKt;
import androidx.compose.material.icons.filled.CloudDownloadKt;
import androidx.compose.material.icons.filled.DeleteKt;
import androidx.compose.material.icons.filled.PhoneKt;
import androidx.compose.material.icons.filled.SendKt;
import androidx.compose.material.icons.filled.ShareKt;
import androidx.compose.material.icons.outlined.AdminPanelSettingsKt;
import androidx.compose.material.icons.outlined.HomeKt;
import androidx.compose.material.icons.outlined.InfoKt;
import androidx.compose.material.icons.outlined.SmartButtonKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner;
import androidx.lifecycle.viewmodel.compose.ViewModelKt;
import com.example.ui.DaliliViewModel;
import com.example.ui.theme.ThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class ComposableSingletons$MainActivityKt {
    public static final ComposableSingletons$MainActivityKt INSTANCE = new ComposableSingletons$MainActivityKt();

    /* renamed from: lambda-1, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f0lambda1 = ComposableLambdaKt.composableLambdaInstance(-601144069, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-1$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            CreationExtras extras$iv;
            ComposerKt.sourceInformation($composer, "C64@2611L11,65@2677L16,67@2707L287:MainActivity.kt#to5c3");
            if (($changed & 3) != 2 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-601144069, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-1.<anonymous> (MainActivity.kt:64)");
                }
                $composer.startReplaceableGroup(1729797275);
                ComposerKt.sourceInformation($composer, "CC(viewModel)P(3,2,1)*80@3834L7,90@4209L68:ViewModel.kt#3tja67");
                HasDefaultViewModelProviderFactory current = LocalViewModelStoreOwner.INSTANCE.getCurrent($composer, 6);
                if (current == null) {
                    throw new IllegalStateException("No ViewModelStoreOwner was provided via LocalViewModelStoreOwner".toString());
                }
                if (current instanceof HasDefaultViewModelProviderFactory) {
                    extras$iv = current.getDefaultViewModelCreationExtras();
                } else {
                    extras$iv = CreationExtras.Empty.INSTANCE;
                }
                DaliliViewModel viewModel = ViewModelKt.viewModel(DaliliViewModel.class, current, (String) null, (ViewModelProvider.Factory) null, extras$iv, $composer, ((0 << 3) & 896) | 36936, 0);
                $composer.endReplaceableGroup();
                final DaliliViewModel viewModel2 = viewModel;
                State themeChoice$delegate = SnapshotStateKt.collectAsState(viewModel2.getCurrentTheme(), (CoroutineContext) null, $composer, 0, 1);
                ThemeKt.MyApplicationTheme(invoke$lambda$0(themeChoice$delegate), ComposableLambdaKt.composableLambda($composer, -1251129304, true, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-1$1.1
                    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
                        invoke((Composer) p1, ((Number) p2).intValue());
                        return Unit.INSTANCE;
                    }

                    public final void invoke(Composer $composer2, int $changed2) {
                        ComposerKt.sourceInformation($composer2, "C70@2877L11,68@2771L209:MainActivity.kt#to5c3");
                        if (($changed2 & 3) != 2 || !$composer2.getSkipping()) {
                            if (ComposerKt.isTraceInProgress()) {
                                ComposerKt.traceEventStart(-1251129304, $changed2, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-1.<anonymous>.<anonymous> (MainActivity.kt:68)");
                            }
                            Modifier fillMaxSize$default = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0f, 1, (Object) null);
                            long j = MaterialTheme.INSTANCE.getColorScheme($composer2, MaterialTheme.$stable).getBackground-0d7_KjU();
                            final DaliliViewModel daliliViewModel = DaliliViewModel.this;
                            SurfaceKt.Surface-T9BRK9s(fillMaxSize$default, (Shape) null, j, 0L, 0.0f, 0.0f, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer2, -1928935251, true, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons.MainActivityKt.lambda-1.1.1.1
                                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
                                    invoke((Composer) p1, ((Number) p2).intValue());
                                    return Unit.INSTANCE;
                                }

                                public final void invoke(Composer $composer3, int $changed3) {
                                    ComposerKt.sourceInformation($composer3, "C72@2940L22:MainActivity.kt#to5c3");
                                    if (($changed3 & 3) == 2 && $composer3.getSkipping()) {
                                        $composer3.skipToGroupEnd();
                                        return;
                                    }
                                    if (ComposerKt.isTraceInProgress()) {
                                        ComposerKt.traceEventStart(-1928935251, $changed3, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-1.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:72)");
                                    }
                                    MainActivityKt.MainContent(DaliliViewModel.this, $composer3, 0);
                                    if (ComposerKt.isTraceInProgress()) {
                                        ComposerKt.traceEventEnd();
                                    }
                                }
                            }), $composer2, 12582918, 122);
                            if (ComposerKt.isTraceInProgress()) {
                                ComposerKt.traceEventEnd();
                                return;
                            }
                            return;
                        }
                        $composer2.skipToGroupEnd();
                    }
                }), $composer, 48);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }

        private static final String invoke$lambda$0(State<String> state) {
            Object thisObj$iv = state.getValue();
            return (String) thisObj$iv;
        }
    });

    /* renamed from: lambda-2, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f11lambda2 = ComposableLambdaKt.composableLambdaInstance(-721115301, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-2$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C161@6479L58:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-721115301, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-2.<anonymous> (MainActivity.kt:161)");
            }
            IconKt.Icon-ww6aTOc(HomeKt.getHome(Icons.Outlined.INSTANCE), "الرئيسية", (Modifier) null, 0L, $composer, 48, 12);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-3, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f22lambda3 = ComposableLambdaKt.composableLambdaInstance(-1566585736, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-3$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C162@6571L46:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1566585736, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-3.<anonymous> (MainActivity.kt:162)");
            }
            TextKt.Text--4IGK_g("الرئيسية", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-4, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f27lambda4 = ComposableLambdaKt.composableLambdaInstance(-254956782, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-4$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C175@7180L70:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-254956782, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-4.<anonymous> (MainActivity.kt:175)");
            }
            IconKt.Icon-ww6aTOc(SmartButtonKt.getSmartButton(Icons.Outlined.INSTANCE), "المساعد الذكي", (Modifier) null, 0L, $composer, 48, 12);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-5, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f28lambda5 = ComposableLambdaKt.composableLambdaInstance(-1003225873, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-5$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C176@7284L45:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1003225873, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-5.<anonymous> (MainActivity.kt:176)");
            }
            TextKt.Text--4IGK_g("المساعد", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-6, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f29lambda6 = ComposableLambdaKt.composableLambdaInstance(8740243, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-6$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C188@7810L62:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(8740243, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-6.<anonymous> (MainActivity.kt:188)");
            }
            IconKt.Icon-ww6aTOc(InfoKt.getInfo(Icons.Outlined.INSTANCE), "دليل التطبيق", (Modifier) null, 0L, $composer, 48, 12);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-7, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f30lambda7 = ComposableLambdaKt.composableLambdaInstance(-739528848, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-7$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C189@7906L48:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-739528848, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-7.<anonymous> (MainActivity.kt:189)");
            }
            TextKt.Text--4IGK_g("عن التطبيق", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-8, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f31lambda8 = ComposableLambdaKt.composableLambdaInstance(272437268, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-8$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C207@8726L75:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(272437268, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-8.<anonymous> (MainActivity.kt:207)");
            }
            IconKt.Icon-ww6aTOc(AdminPanelSettingsKt.getAdminPanelSettings(Icons.Outlined.INSTANCE), "لوحة التحكم", (Modifier) null, 0L, $composer, 48, 12);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-9, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f32lambda9 = ComposableLambdaKt.composableLambdaInstance(-475831823, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-9$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C208@8835L49:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-475831823, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-9.<anonymous> (MainActivity.kt:208)");
            }
            TextKt.Text--4IGK_g("لوحة التحكم", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-10, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f1lambda10 = ComposableLambdaKt.composableLambdaInstance(143663548, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-10$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C354@15129L83,355@15241L39,356@15309L57:MainActivity.kt#to5c3");
            if (($changed & 17) != 16 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(143663548, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-10.<anonymous> (MainActivity.kt:354)");
                }
                IconKt.Icon-ww6aTOc(AddKt.getAdd(Icons.INSTANCE.getDefault()), (String) null, SizeKt.size-3ABfNKs(Modifier.Companion, Dp.constructor-impl(18)), 0L, $composer, 432, 8);
                SpacerKt.Spacer(SizeKt.width-3ABfNKs(Modifier.Companion, Dp.constructor-impl(6)), $composer, 6);
                TextKt.Text--4IGK_g("سجل كمزود خدمة معنا", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: lambda-11, reason: not valid java name */
    public static Function3<LazyItemScope, Composer, Integer, Unit> f2lambda11 = ComposableLambdaKt.composableLambdaInstance(1363227635, false, new Function3<LazyItemScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-11$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$item, "$this$item");
            ComposerKt.sourceInformation($composer, "C367@15598L10,369@15706L11,365@15513L348:MainActivity.kt#to5c3");
            if (($changed & 17) != 16 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(1363227635, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-11.<anonymous> (MainActivity.kt:365)");
                }
                TextStyle titleMedium = MaterialTheme.INSTANCE.getTypography($composer, MaterialTheme.$stable).getTitleMedium();
                TextKt.Text--4IGK_g("تصفح حسب فئات الخدمات", PaddingKt.padding-qDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null), 0.0f, Dp.constructor-impl(8), 0.0f, 0.0f, 13, (Object) null), MaterialTheme.INSTANCE.getColorScheme($composer, MaterialTheme.$stable).getOnBackground-0d7_KjU(), 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, TextAlign.box-impl(TextAlign.Companion.getRight-e0LSkKk()), 0L, 0, false, 0, 0, (Function1) null, titleMedium, $composer, 196662, 0, 64984);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: lambda-12, reason: not valid java name */
    public static Function3<LazyItemScope, Composer, Integer, Unit> f3lambda12 = ComposableLambdaKt.composableLambdaInstance(-2064681961, false, new Function3<LazyItemScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-12$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$item, "$this$item");
            ComposerKt.sourceInformation($composer, "C451@18948L10,453@19072L11,449@18851L378:MainActivity.kt#to5c3");
            if (($changed & 17) != 16 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-2064681961, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-12.<anonymous> (MainActivity.kt:449)");
                }
                TextStyle titleMedium = MaterialTheme.INSTANCE.getTypography($composer, MaterialTheme.$stable).getTitleMedium();
                FontWeight bold = FontWeight.Companion.getBold();
                TextKt.Text--4IGK_g("طلب انضمام لدليلي", SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null), MaterialTheme.INSTANCE.getColorScheme($composer, MaterialTheme.$stable).getOnSurface-0d7_KjU(), 0L, (FontStyle) null, bold, (FontFamily) null, 0L, (TextDecoration) null, TextAlign.box-impl(TextAlign.Companion.getCenter-e0LSkKk()), 0L, 0, false, 0, 0, (Function1) null, titleMedium, $composer, 196662, 0, 64984);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: lambda-13, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f4lambda13 = ComposableLambdaKt.composableLambdaInstance(-594786188, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-13$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$TextButton, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$TextButton, "$this$TextButton");
            ComposerKt.sourceInformation($composer, "C526@22231L34:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-594786188, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-13.<anonymous> (MainActivity.kt:526)");
            }
            TextKt.Text--4IGK_g("إلغاء", (Modifier) null, Color.Companion.getWhite-0d7_KjU(), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 390, 0, 131066);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-14, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f5lambda14 = ComposableLambdaKt.composableLambdaInstance(-1776318041, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-14$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C551@23774L49:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1776318041, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-14.<anonymous> (MainActivity.kt:551)");
            }
            TextKt.Text--4IGK_g("إرسال الطلب", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-15, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f6lambda15 = ComposableLambdaKt.composableLambdaInstance(-249187827, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-15$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C591@25120L58:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-249187827, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-15.<anonymous> (MainActivity.kt:591)");
            }
            IconKt.Icon-ww6aTOc(ArrowBackKt.getArrowBack(Icons.INSTANCE.getDefault()), "Back", (Modifier) null, 0L, $composer, 48, 12);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-16, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f7lambda16 = ComposableLambdaKt.composableLambdaInstance(1031990140, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-16$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C625@26452L42:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1031990140, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-16.<anonymous> (MainActivity.kt:625)");
            }
            TextKt.Text--4IGK_g("الكل", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-17, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f8lambda17 = ComposableLambdaKt.composableLambdaInstance(-402733204, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-17$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C728@31661L74:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-402733204, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-17.<anonymous> (MainActivity.kt:728)");
            }
            IconKt.Icon-ww6aTOc(PhoneKt.getPhone(Icons.INSTANCE.getDefault()), "Call", (Modifier) null, Color.Companion.getWhite-0d7_KjU(), $composer, 3120, 4);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-18, reason: not valid java name */
    public static Function3<LazyItemScope, Composer, Integer, Unit> f9lambda18 = ComposableLambdaKt.composableLambdaInstance(-599031300, false, new Function3<LazyItemScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-18$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        /* JADX WARN: Removed duplicated region for block: B:24:0x0196  */
        /* JADX WARN: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final void invoke(androidx.compose.foundation.lazy.LazyItemScope r36, androidx.compose.runtime.Composer r37, int r38) {
            /*
                Method dump skipped, instructions count: 410
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.example.ComposableSingletons$MainActivityKt$lambda18$1.invoke(androidx.compose.foundation.lazy.LazyItemScope, androidx.compose.runtime.Composer, int):void");
        }
    });

    /* renamed from: lambda-19, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f10lambda19 = ComposableLambdaKt.composableLambdaInstance(-1308995221, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-19$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C824@35016L61:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1308995221, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-19.<anonymous> (MainActivity.kt:824)");
            }
            IconKt.Icon-ww6aTOc(SendKt.getSend(Icons.INSTANCE.getDefault()), "Send message", (Modifier) null, 0L, $composer, 48, 12);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-20, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f12lambda20 = ComposableLambdaKt.composableLambdaInstance(-871182171, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-20$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C960@40545L60,961@40626L39,962@40686L51:MainActivity.kt#to5c3");
            if (($changed & 17) != 16 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-871182171, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-20.<anonymous> (MainActivity.kt:960)");
                }
                IconKt.Icon-ww6aTOc(CloudDownloadKt.getCloudDownload(Icons.INSTANCE.getDefault()), (String) null, (Modifier) null, 0L, $composer, 48, 12);
                SpacerKt.Spacer(SizeKt.width-3ABfNKs(Modifier.Companion, Dp.constructor-impl(6)), $composer, 6);
                TextKt.Text--4IGK_g("تحديثات دليلي", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: lambda-21, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f13lambda21 = ComposableLambdaKt.composableLambdaInstance(-1099405476, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-21$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C977@41397L52,978@41470L39,979@41530L50:MainActivity.kt#to5c3");
            if (($changed & 17) != 16 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-1099405476, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-21.<anonymous> (MainActivity.kt:977)");
                }
                IconKt.Icon-ww6aTOc(ShareKt.getShare(Icons.INSTANCE.getDefault()), (String) null, (Modifier) null, 0L, $composer, 48, 12);
                SpacerKt.Spacer(SizeKt.width-3ABfNKs(Modifier.Companion, Dp.constructor-impl(6)), $composer, 6);
                TextKt.Text--4IGK_g("شارك التطبيق", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: lambda-22, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f14lambda22 = ComposableLambdaKt.composableLambdaInstance(1379656041, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-22$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C1114@47258L68:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1379656041, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-22.<anonymous> (MainActivity.kt:1114)");
            }
            TextKt.Text--4IGK_g("تسجيل الدخول", (Modifier) null, 0L, TextUnitKt.getSp(16), (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 199686, 0, 131030);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-23, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f15lambda23 = ComposableLambdaKt.composableLambdaInstance(-894100679, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-23$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C1145@48213L18:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-894100679, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-23.<anonymous> (MainActivity.kt:1145)");
            }
            TextKt.Text--4IGK_g("تسجيل خروج", (Modifier) null, 0L, 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 6, 0, 131070);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-24, reason: not valid java name */
    public static Function3<ColumnScope, Composer, Integer, Unit> f16lambda24 = ComposableLambdaKt.composableLambdaInstance(-1544838758, false, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-24$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(ColumnScope $this$Tab, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Tab, "$this$Tab");
            ComposerKt.sourceInformation($composer, "C1161@48668L101:MainActivity.kt#to5c3");
            if (($changed & 17) != 16 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-1544838758, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-24.<anonymous> (MainActivity.kt:1161)");
                }
                TextKt.Text--4IGK_g("إعدادات عامة Theme/App Logo", PaddingKt.padding-3ABfNKs(Modifier.Companion, Dp.constructor-impl(12)), 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196662, 0, 131036);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: lambda-25, reason: not valid java name */
    public static Function3<ColumnScope, Composer, Integer, Unit> f17lambda25 = ComposableLambdaKt.composableLambdaInstance(-512861309, false, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-25$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(ColumnScope $this$Tab, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Tab, "$this$Tab");
            ComposerKt.sourceInformation($composer, "C1164@48902L89:MainActivity.kt#to5c3");
            if (($changed & 17) != 16 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-512861309, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-25.<anonymous> (MainActivity.kt:1164)");
                }
                TextKt.Text--4IGK_g("الأقسام الرئيسي", PaddingKt.padding-3ABfNKs(Modifier.Companion, Dp.constructor-impl(12)), 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196662, 0, 131036);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: lambda-26, reason: not valid java name */
    public static Function3<ColumnScope, Composer, Integer, Unit> f18lambda26 = ComposableLambdaKt.composableLambdaInstance(636970978, false, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-26$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(ColumnScope $this$Tab, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Tab, "$this$Tab");
            ComposerKt.sourceInformation($composer, "C1167@49122L86:MainActivity.kt#to5c3");
            if (($changed & 17) != 16 || !$composer.getSkipping()) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(636970978, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-26.<anonymous> (MainActivity.kt:1167)");
                }
                TextKt.Text--4IGK_g("مزودي الخدمة", PaddingKt.padding-3ABfNKs(Modifier.Companion, Dp.constructor-impl(12)), 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196662, 0, 131036);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    return;
                }
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: lambda-27, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f19lambda27 = ComposableLambdaKt.composableLambdaInstance(1240983169, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-27$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C1332@56389L83:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1240983169, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-27.<anonymous> (MainActivity.kt:1332)");
            }
            TextKt.Text--4IGK_g("حفظ وتحديث كل الأجهزة فوراً", (Modifier) null, 0L, TextUnitKt.getSp(16), (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 199686, 0, 131030);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-28, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f20lambda28 = ComposableLambdaKt.composableLambdaInstance(-2119177991, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-28$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C1369@58490L25:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-2119177991, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-28.<anonymous> (MainActivity.kt:1369)");
            }
            TextKt.Text--4IGK_g("إضافة قسم ومزامنة", (Modifier) null, 0L, 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 6, 0, 131070);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-29, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f21lambda29 = ComposableLambdaKt.composableLambdaInstance(1326378542, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-29$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C1387@59391L75:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1326378542, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-29.<anonymous> (MainActivity.kt:1387)");
            }
            IconKt.Icon-ww6aTOc(DeleteKt.getDelete(Icons.INSTANCE.getDefault()), "Delete", (Modifier) null, Color.Companion.getRed-0d7_KjU(), $composer, 3120, 4);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-30, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f23lambda30 = ComposableLambdaKt.composableLambdaInstance(-190732687, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-30$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C1458@62989L31:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-190732687, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-30.<anonymous> (MainActivity.kt:1458)");
            }
            TextKt.Text--4IGK_g("إضافة مزود خدمة ومزامنة", (Modifier) null, 0L, 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 6, 0, 131070);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-31, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f24lambda31 = ComposableLambdaKt.composableLambdaInstance(915211214, false, new Function2<Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-31$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
            invoke((Composer) p1, ((Number) p2).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C1476@63881L75:MainActivity.kt#to5c3");
            if (($changed & 3) == 2 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(915211214, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-31.<anonymous> (MainActivity.kt:1476)");
            }
            IconKt.Icon-ww6aTOc(DeleteKt.getDelete(Icons.INSTANCE.getDefault()), "Delete", (Modifier) null, Color.Companion.getRed-0d7_KjU(), $composer, 3120, 4);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-32, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f25lambda32 = ComposableLambdaKt.composableLambdaInstance(1293126374, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-32$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$TextButton, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$TextButton, "$this$TextButton");
            ComposerKt.sourceInformation($composer, "C1552@68061L66:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1293126374, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-32.<anonymous> (MainActivity.kt:1552)");
            }
            TextKt.Text--4IGK_g("رفض الطلب", (Modifier) null, Color.Companion.getRed-0d7_KjU(), 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196998, 0, 131034);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-33, reason: not valid java name */
    public static Function3<RowScope, Composer, Integer, Unit> f26lambda33 = ComposableLambdaKt.composableLambdaInstance(1009000915, false, new Function3<RowScope, Composer, Integer, Unit>() { // from class: com.example.ComposableSingletons$MainActivityKt$lambda-33$1
        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
            invoke((RowScope) p1, (Composer) p2, ((Number) p3).intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter($this$Button, "$this$Button");
            ComposerKt.sourceInformation($composer, "C1566@68781L55:MainActivity.kt#to5c3");
            if (($changed & 17) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1009000915, $changed, -1, "com.example.ComposableSingletons$MainActivityKt.lambda-33.<anonymous> (MainActivity.kt:1566)");
            }
            TextKt.Text--4IGK_g("قبول وتفعيل الطلب", (Modifier) null, 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196614, 0, 131038);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: getLambda-1$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m0getLambda1$app_debug() {
        return f0lambda1;
    }

    /* renamed from: getLambda-10$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m1getLambda10$app_debug() {
        return f1lambda10;
    }

    /* renamed from: getLambda-11$app_debug, reason: not valid java name */
    public final Function3<LazyItemScope, Composer, Integer, Unit> m2getLambda11$app_debug() {
        return f2lambda11;
    }

    /* renamed from: getLambda-12$app_debug, reason: not valid java name */
    public final Function3<LazyItemScope, Composer, Integer, Unit> m3getLambda12$app_debug() {
        return f3lambda12;
    }

    /* renamed from: getLambda-13$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m4getLambda13$app_debug() {
        return f4lambda13;
    }

    /* renamed from: getLambda-14$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m5getLambda14$app_debug() {
        return f5lambda14;
    }

    /* renamed from: getLambda-15$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6getLambda15$app_debug() {
        return f6lambda15;
    }

    /* renamed from: getLambda-16$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m7getLambda16$app_debug() {
        return f7lambda16;
    }

    /* renamed from: getLambda-17$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m8getLambda17$app_debug() {
        return f8lambda17;
    }

    /* renamed from: getLambda-18$app_debug, reason: not valid java name */
    public final Function3<LazyItemScope, Composer, Integer, Unit> m9getLambda18$app_debug() {
        return f9lambda18;
    }

    /* renamed from: getLambda-19$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m10getLambda19$app_debug() {
        return f10lambda19;
    }

    /* renamed from: getLambda-2$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m11getLambda2$app_debug() {
        return f11lambda2;
    }

    /* renamed from: getLambda-20$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m12getLambda20$app_debug() {
        return f12lambda20;
    }

    /* renamed from: getLambda-21$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m13getLambda21$app_debug() {
        return f13lambda21;
    }

    /* renamed from: getLambda-22$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m14getLambda22$app_debug() {
        return f14lambda22;
    }

    /* renamed from: getLambda-23$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m15getLambda23$app_debug() {
        return f15lambda23;
    }

    /* renamed from: getLambda-24$app_debug, reason: not valid java name */
    public final Function3<ColumnScope, Composer, Integer, Unit> m16getLambda24$app_debug() {
        return f16lambda24;
    }

    /* renamed from: getLambda-25$app_debug, reason: not valid java name */
    public final Function3<ColumnScope, Composer, Integer, Unit> m17getLambda25$app_debug() {
        return f17lambda25;
    }

    /* renamed from: getLambda-26$app_debug, reason: not valid java name */
    public final Function3<ColumnScope, Composer, Integer, Unit> m18getLambda26$app_debug() {
        return f18lambda26;
    }

    /* renamed from: getLambda-27$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m19getLambda27$app_debug() {
        return f19lambda27;
    }

    /* renamed from: getLambda-28$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m20getLambda28$app_debug() {
        return f20lambda28;
    }

    /* renamed from: getLambda-29$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m21getLambda29$app_debug() {
        return f21lambda29;
    }

    /* renamed from: getLambda-3$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m22getLambda3$app_debug() {
        return f22lambda3;
    }

    /* renamed from: getLambda-30$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m23getLambda30$app_debug() {
        return f23lambda30;
    }

    /* renamed from: getLambda-31$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m24getLambda31$app_debug() {
        return f24lambda31;
    }

    /* renamed from: getLambda-32$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m25getLambda32$app_debug() {
        return f25lambda32;
    }

    /* renamed from: getLambda-33$app_debug, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m26getLambda33$app_debug() {
        return f26lambda33;
    }

    /* renamed from: getLambda-4$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m27getLambda4$app_debug() {
        return f27lambda4;
    }

    /* renamed from: getLambda-5$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m28getLambda5$app_debug() {
        return f28lambda5;
    }

    /* renamed from: getLambda-6$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m29getLambda6$app_debug() {
        return f29lambda6;
    }

    /* renamed from: getLambda-7$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m30getLambda7$app_debug() {
        return f30lambda7;
    }

    /* renamed from: getLambda-8$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m31getLambda8$app_debug() {
        return f31lambda8;
    }

    /* renamed from: getLambda-9$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m32getLambda9$app_debug() {
        return f32lambda9;
    }
}
