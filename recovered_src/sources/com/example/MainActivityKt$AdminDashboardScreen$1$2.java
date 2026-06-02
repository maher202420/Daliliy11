package com.example;

import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.TabKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.unit.Dp;
import com.example.data.PendingProvider;
import com.example.ui.DaliliViewModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$AdminDashboardScreen$1$2 implements Function2<Composer, Integer, Unit> {
    final /* synthetic */ MutableState<String> $subscreenTab$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$AdminDashboardScreen$1$2(MutableState<String> mutableState, DaliliViewModel daliliViewModel) {
        this.$subscreenTab$delegate = mutableState;
        this.$viewModel = daliliViewModel;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) {
        invoke((Composer) p1, ((Number) p2).intValue());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$1$lambda$0(MutableState $subscreenTab$delegate) {
        $subscreenTab$delegate.setValue("config");
        return Unit.INSTANCE;
    }

    public final void invoke(Composer $composer, int $changed) {
        String AdminDashboardScreen$lambda$101;
        Object value$iv;
        String AdminDashboardScreen$lambda$1012;
        Object value$iv2;
        String AdminDashboardScreen$lambda$1013;
        Object value$iv3;
        String AdminDashboardScreen$lambda$1014;
        Object value$iv4;
        ComposerKt.sourceInformation($composer, "C1160@48621L27,1160@48570L213,1163@48851L31,1163@48796L209,1166@49072L30,1166@49018L204,1169@49292L33,1169@49235L396:MainActivity.kt#to5c3");
        if (($changed & 3) == 2 && $composer.getSkipping()) {
            $composer.skipToGroupEnd();
            return;
        }
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1293458643, $changed, -1, "com.example.AdminDashboardScreen.<anonymous>.<anonymous> (MainActivity.kt:1160)");
        }
        AdminDashboardScreen$lambda$101 = MainActivityKt.AdminDashboardScreen$lambda$101(this.$subscreenTab$delegate);
        boolean areEqual = Intrinsics.areEqual(AdminDashboardScreen$lambda$101, "config");
        $composer.startReplaceableGroup(-2094300933);
        ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
        final MutableState<String> mutableState = this.$subscreenTab$delegate;
        Object it$iv = $composer.rememberedValue();
        if (it$iv == Composer.Companion.getEmpty()) {
            value$iv = new Function0() { // from class: com.example.MainActivityKt$AdminDashboardScreen$1$2$$ExternalSyntheticLambda0
                public final Object invoke() {
                    Unit invoke$lambda$1$lambda$0;
                    invoke$lambda$1$lambda$0 = MainActivityKt$AdminDashboardScreen$1$2.invoke$lambda$1$lambda$0(mutableState);
                    return invoke$lambda$1$lambda$0;
                }
            };
            $composer.updateRememberedValue(value$iv);
        } else {
            value$iv = it$iv;
        }
        $composer.endReplaceableGroup();
        TabKt.Tab-bogVsAg(areEqual, (Function0) value$iv, (Modifier) null, false, 0L, 0L, (MutableInteractionSource) null, ComposableSingletons$MainActivityKt.INSTANCE.m16getLambda24$app_debug(), $composer, 12582960, 124);
        AdminDashboardScreen$lambda$1012 = MainActivityKt.AdminDashboardScreen$lambda$101(this.$subscreenTab$delegate);
        boolean areEqual2 = Intrinsics.areEqual(AdminDashboardScreen$lambda$1012, "categories");
        $composer.startReplaceableGroup(-2094293569);
        ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
        final MutableState<String> mutableState2 = this.$subscreenTab$delegate;
        Object it$iv2 = $composer.rememberedValue();
        if (it$iv2 == Composer.Companion.getEmpty()) {
            value$iv2 = new Function0() { // from class: com.example.MainActivityKt$AdminDashboardScreen$1$2$$ExternalSyntheticLambda1
                public final Object invoke() {
                    Unit invoke$lambda$3$lambda$2;
                    invoke$lambda$3$lambda$2 = MainActivityKt$AdminDashboardScreen$1$2.invoke$lambda$3$lambda$2(mutableState2);
                    return invoke$lambda$3$lambda$2;
                }
            };
            $composer.updateRememberedValue(value$iv2);
        } else {
            value$iv2 = it$iv2;
        }
        $composer.endReplaceableGroup();
        TabKt.Tab-bogVsAg(areEqual2, (Function0) value$iv2, (Modifier) null, false, 0L, 0L, (MutableInteractionSource) null, ComposableSingletons$MainActivityKt.INSTANCE.m17getLambda25$app_debug(), $composer, 12582960, 124);
        AdminDashboardScreen$lambda$1013 = MainActivityKt.AdminDashboardScreen$lambda$101(this.$subscreenTab$delegate);
        boolean areEqual3 = Intrinsics.areEqual(AdminDashboardScreen$lambda$1013, "providers");
        $composer.startReplaceableGroup(-2094286498);
        ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
        final MutableState<String> mutableState3 = this.$subscreenTab$delegate;
        Object it$iv3 = $composer.rememberedValue();
        if (it$iv3 == Composer.Companion.getEmpty()) {
            value$iv3 = new Function0() { // from class: com.example.MainActivityKt$AdminDashboardScreen$1$2$$ExternalSyntheticLambda2
                public final Object invoke() {
                    Unit invoke$lambda$5$lambda$4;
                    invoke$lambda$5$lambda$4 = MainActivityKt$AdminDashboardScreen$1$2.invoke$lambda$5$lambda$4(mutableState3);
                    return invoke$lambda$5$lambda$4;
                }
            };
            $composer.updateRememberedValue(value$iv3);
        } else {
            value$iv3 = it$iv3;
        }
        $composer.endReplaceableGroup();
        TabKt.Tab-bogVsAg(areEqual3, (Function0) value$iv3, (Modifier) null, false, 0L, 0L, (MutableInteractionSource) null, ComposableSingletons$MainActivityKt.INSTANCE.m18getLambda26$app_debug(), $composer, 12582960, 124);
        AdminDashboardScreen$lambda$1014 = MainActivityKt.AdminDashboardScreen$lambda$101(this.$subscreenTab$delegate);
        boolean areEqual4 = Intrinsics.areEqual(AdminDashboardScreen$lambda$1014, "applications");
        $composer.startReplaceableGroup(-2094279455);
        ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
        final MutableState<String> mutableState4 = this.$subscreenTab$delegate;
        Object it$iv4 = $composer.rememberedValue();
        if (it$iv4 == Composer.Companion.getEmpty()) {
            value$iv4 = new Function0() { // from class: com.example.MainActivityKt$AdminDashboardScreen$1$2$$ExternalSyntheticLambda3
                public final Object invoke() {
                    Unit invoke$lambda$7$lambda$6;
                    invoke$lambda$7$lambda$6 = MainActivityKt$AdminDashboardScreen$1$2.invoke$lambda$7$lambda$6(mutableState4);
                    return invoke$lambda$7$lambda$6;
                }
            };
            $composer.updateRememberedValue(value$iv4);
        } else {
            value$iv4 = it$iv4;
        }
        $composer.endReplaceableGroup();
        final DaliliViewModel daliliViewModel = this.$viewModel;
        TabKt.Tab-bogVsAg(areEqual4, (Function0) value$iv4, (Modifier) null, false, 0L, 0L, (MutableInteractionSource) null, ComposableLambdaKt.composableLambda($composer, 1786803265, true, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$AdminDashboardScreen$1$2.5
            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                return Unit.INSTANCE;
            }

            private static final List<PendingProvider> invoke$lambda$0(State<? extends List<PendingProvider>> state) {
                Object thisObj$iv = state.getValue();
                return (List) thisObj$iv;
            }

            public final void invoke(ColumnScope $this$Tab, Composer $composer2, int $changed2) {
                Intrinsics.checkNotNullParameter($this$Tab, "$this$Tab");
                ComposerKt.sourceInformation($composer2, "C1170@49396L16,1172@49520L97:MainActivity.kt#to5c3");
                if (($changed2 & 17) != 16 || !$composer2.getSkipping()) {
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart(1786803265, $changed2, -1, "com.example.AdminDashboardScreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:1170)");
                    }
                    State pendingProviders$delegate = SnapshotStateKt.collectAsState(DaliliViewModel.this.getPendingProviders(), (CoroutineContext) null, $composer2, 0, 1);
                    Iterable $this$filter$iv = invoke$lambda$0(pendingProviders$delegate);
                    Collection destination$iv$iv = new ArrayList();
                    for (Object element$iv$iv : $this$filter$iv) {
                        PendingProvider it = (PendingProvider) element$iv$iv;
                        if (Intrinsics.areEqual(it.getStatus(), "pending")) {
                            destination$iv$iv.add(element$iv$iv);
                        }
                    }
                    int pendingCount = ((List) destination$iv$iv).size();
                    TextKt.Text--4IGK_g("الطلبات (" + pendingCount + ")", PaddingKt.padding-3ABfNKs(Modifier.Companion, Dp.constructor-impl(12)), 0L, 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer2, 196656, 0, 131036);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                        return;
                    }
                    return;
                }
                $composer2.skipToGroupEnd();
            }
        }), $composer, 12582960, 124);
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$3$lambda$2(MutableState $subscreenTab$delegate) {
        $subscreenTab$delegate.setValue("categories");
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$5$lambda$4(MutableState $subscreenTab$delegate) {
        $subscreenTab$delegate.setValue("providers");
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$7$lambda$6(MutableState $subscreenTab$delegate) {
        $subscreenTab$delegate.setValue("applications");
        return Unit.INSTANCE;
    }
}
