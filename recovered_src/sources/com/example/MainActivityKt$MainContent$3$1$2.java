package com.example;

import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import com.example.Screen;
import com.example.data.Category;
import com.example.ui.DaliliViewModel;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
final class MainActivityKt$MainContent$3$1$2 implements Function4<AnimatedContentScope, Screen, Composer, Integer, Unit> {
    final /* synthetic */ MutableState<Screen> $currentScreen$delegate;
    final /* synthetic */ MutableState<String> $currentTab$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$MainContent$3$1$2(DaliliViewModel daliliViewModel, MutableState<Screen> mutableState, MutableState<String> mutableState2) {
        this.$viewModel = daliliViewModel;
        this.$currentScreen$delegate = mutableState;
        this.$currentTab$delegate = mutableState2;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3, Object p4) {
        invoke((AnimatedContentScope) p1, (Screen) p2, (Composer) p3, ((Number) p4).intValue());
        return Unit.INSTANCE;
    }

    public final void invoke(AnimatedContentScope $this$AnimatedContent, Screen screen, Composer $composer, int $changed) {
        Object value$iv;
        Object value$iv2;
        Object value$iv3;
        Object value$iv4;
        Intrinsics.checkNotNullParameter($this$AnimatedContent, "$this$AnimatedContent");
        Intrinsics.checkNotNullParameter(screen, "screen");
        ComposerKt.sourceInformation($composer, "C:MainActivity.kt#to5c3");
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2018607731, $changed, -1, "com.example.MainContent.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:236)");
        }
        if (screen instanceof Screen.Home) {
            $composer.startReplaceableGroup(-1042527640);
            ComposerKt.sourceInformation($composer, "240@10068L140,238@9940L302");
            DaliliViewModel daliliViewModel = this.$viewModel;
            $composer.startReplaceableGroup(2044585016);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            final MutableState<Screen> mutableState = this.$currentScreen$delegate;
            Object it$iv = $composer.rememberedValue();
            if (it$iv == Composer.Companion.getEmpty()) {
                value$iv4 = new Function1() { // from class: com.example.MainActivityKt$MainContent$3$1$2$$ExternalSyntheticLambda0
                    public final Object invoke(Object obj) {
                        Unit invoke$lambda$1$lambda$0;
                        invoke$lambda$1$lambda$0 = MainActivityKt$MainContent$3$1$2.invoke$lambda$1$lambda$0(mutableState, (Category) obj);
                        return invoke$lambda$1$lambda$0;
                    }
                };
                $composer.updateRememberedValue(value$iv4);
            } else {
                value$iv4 = it$iv;
            }
            $composer.endReplaceableGroup();
            MainActivityKt.HomeScreen(daliliViewModel, (Function1) value$iv4, $composer, 48);
            $composer.endReplaceableGroup();
        } else if (screen instanceof Screen.CategoryDetails) {
            $composer.startReplaceableGroup(-1042106040);
            ComposerKt.sourceInformation($composer, "249@10557L107,246@10364L334");
            Category category = ((Screen.CategoryDetails) screen).getCategory();
            DaliliViewModel daliliViewModel2 = this.$viewModel;
            $composer.startReplaceableGroup(2044600631);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            final MutableState<Screen> mutableState2 = this.$currentScreen$delegate;
            Object it$iv2 = $composer.rememberedValue();
            if (it$iv2 == Composer.Companion.getEmpty()) {
                value$iv3 = new Function0() { // from class: com.example.MainActivityKt$MainContent$3$1$2$$ExternalSyntheticLambda1
                    public final Object invoke() {
                        Unit invoke$lambda$3$lambda$2;
                        invoke$lambda$3$lambda$2 = MainActivityKt$MainContent$3$1$2.invoke$lambda$3$lambda$2(mutableState2);
                        return invoke$lambda$3$lambda$2;
                    }
                };
                $composer.updateRememberedValue(value$iv3);
            } else {
                value$iv3 = it$iv2;
            }
            $composer.endReplaceableGroup();
            MainActivityKt.CategoryProvidersScreen(category, daliliViewModel2, (Function0) value$iv3, $composer, 384);
            $composer.endReplaceableGroup();
        } else if (screen instanceof Screen.Login) {
            $composer.startReplaceableGroup(-1041665406);
            ComposerKt.sourceInformation($composer, "257@10935L117,255@10810L276");
            DaliliViewModel daliliViewModel3 = this.$viewModel;
            $composer.startReplaceableGroup(2044612737);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            final MutableState<Screen> mutableState3 = this.$currentScreen$delegate;
            Object it$iv3 = $composer.rememberedValue();
            if (it$iv3 == Composer.Companion.getEmpty()) {
                value$iv2 = new Function0() { // from class: com.example.MainActivityKt$MainContent$3$1$2$$ExternalSyntheticLambda2
                    public final Object invoke() {
                        Unit invoke$lambda$5$lambda$4;
                        invoke$lambda$5$lambda$4 = MainActivityKt$MainContent$3$1$2.invoke$lambda$5$lambda$4(mutableState3);
                        return invoke$lambda$5$lambda$4;
                    }
                };
                $composer.updateRememberedValue(value$iv2);
            } else {
                value$iv2 = it$iv3;
            }
            $composer.endReplaceableGroup();
            MainActivityKt.LoginScreen(daliliViewModel3, (Function0) value$iv2, $composer, 48);
            $composer.endReplaceableGroup();
        } else {
            if (!(screen instanceof Screen.AdminDashboard)) {
                $composer.startReplaceableGroup(2044579456);
                $composer.endReplaceableGroup();
                throw new NoWhenBranchMatchedException();
            }
            $composer.startReplaceableGroup(-1041269939);
            ComposerKt.sourceInformation($composer, "265@11335L167,263@11207L329");
            DaliliViewModel daliliViewModel4 = this.$viewModel;
            $composer.startReplaceableGroup(2044625587);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            final MutableState<Screen> mutableState4 = this.$currentScreen$delegate;
            final MutableState<String> mutableState5 = this.$currentTab$delegate;
            Object it$iv4 = $composer.rememberedValue();
            if (it$iv4 == Composer.Companion.getEmpty()) {
                value$iv = new Function0() { // from class: com.example.MainActivityKt$MainContent$3$1$2$$ExternalSyntheticLambda3
                    public final Object invoke() {
                        Unit invoke$lambda$7$lambda$6;
                        invoke$lambda$7$lambda$6 = MainActivityKt$MainContent$3$1$2.invoke$lambda$7$lambda$6(mutableState4, mutableState5);
                        return invoke$lambda$7$lambda$6;
                    }
                };
                $composer.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv4;
            }
            $composer.endReplaceableGroup();
            MainActivityKt.AdminDashboardScreen(daliliViewModel4, (Function0) value$iv, $composer, 48);
            $composer.endReplaceableGroup();
        }
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$1$lambda$0(MutableState $currentScreen$delegate, Category category) {
        Intrinsics.checkNotNullParameter(category, "category");
        $currentScreen$delegate.setValue(new Screen.CategoryDetails(category));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$3$lambda$2(MutableState $currentScreen$delegate) {
        $currentScreen$delegate.setValue(Screen.Home.INSTANCE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$5$lambda$4(MutableState $currentScreen$delegate) {
        $currentScreen$delegate.setValue(Screen.AdminDashboard.INSTANCE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$7$lambda$6(MutableState $currentScreen$delegate, MutableState $currentTab$delegate) {
        $currentScreen$delegate.setValue(Screen.Home.INSTANCE);
        $currentTab$delegate.setValue("home");
        return Unit.INSTANCE;
    }
}
