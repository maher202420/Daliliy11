package com.example;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.material3.RadioButtonColors;
import androidx.compose.material3.RadioButtonKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SkippableUpdater;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.LayoutKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.unit.Dp;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$AdminConfigSubscreen$2$1$1 implements Function3<LazyItemScope, Composer, Integer, Unit> {
    final /* synthetic */ MutableState<String> $selectedTheme$delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$AdminConfigSubscreen$2$1$1(MutableState<String> mutableState) {
        this.$selectedTheme$delegate = mutableState;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
        Object value$iv;
        Function0 factory$iv$iv$iv;
        String AdminConfigSubscreen$lambda$149;
        boolean invalid$iv;
        Object it$iv;
        Object value$iv2;
        Intrinsics.checkNotNullParameter($this$item, "$this$item");
        ComposerKt.sourceInformation($composer, "C1231@51966L81,*1244@52718L32,1241@52598L545:MainActivity.kt#to5c3");
        if (($changed & 17) == 16 && $composer.getSkipping()) {
            $composer.skipToGroupEnd();
            return;
        }
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1061261, $changed, -1, "com.example.AdminConfigSubscreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:1231)");
        }
        TextKt.Text--4IGK_g("التحكم بالمظهر والسمة:", (Modifier) null, Color.Companion.getWhite-0d7_KjU(), 0L, (FontStyle) null, FontWeight.Companion.getBold(), (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 196998, 0, 131034);
        int i = 1;
        List themes = CollectionsKt.listOf(new Pair[]{new Pair("الأحمر والأسود الافتراضي (Red Black)", "red_black"), new Pair("الأزرق الإمبراطوري (Midnight Royal)", "royal_indigo"), new Pair("الأخضر الزمردي الراقي (Emerald)", "emerald_green"), new Pair("الفضي المعدني والمودرن (Slate Silver)", "slate_silver"), new Pair("المحيط الهادئ فيروزي (Ocean Teal)", "ocean_teal"), new Pair("البيج الكلاسيكي والدافئ (Cream)", "beige_cream")});
        List $this$forEach$iv = themes;
        final MutableState<String> mutableState = this.$selectedTheme$delegate;
        for (Object element$iv : $this$forEach$iv) {
            final Pair theme = (Pair) element$iv;
            Modifier fillMaxWidth$default = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, i, (Object) null);
            $composer.startReplaceableGroup(1396305288);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            boolean invalid$iv2 = $composer.changed(theme);
            Object it$iv2 = $composer.rememberedValue();
            if (invalid$iv2 || it$iv2 == Composer.Companion.getEmpty()) {
                value$iv = new Function0() { // from class: com.example.MainActivityKt$AdminConfigSubscreen$2$1$1$$ExternalSyntheticLambda0
                    public final Object invoke() {
                        Unit invoke$lambda$5$lambda$1$lambda$0;
                        invoke$lambda$5$lambda$1$lambda$0 = MainActivityKt$AdminConfigSubscreen$2$1$1.invoke$lambda$5$lambda$1$lambda$0(theme, mutableState);
                        return invoke$lambda$5$lambda$1$lambda$0;
                    }
                };
                $composer.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv2;
            }
            $composer.endReplaceableGroup();
            Modifier modifier$iv = PaddingKt.padding-VpY3zN4$default(ClickableKt.clickable-XHw0xAI$default(fillMaxWidth$default, false, (String) null, (Role) null, (Function0) value$iv, 7, (Object) null), 0.0f, Dp.constructor-impl(4), 1, (Object) null);
            Alignment.Vertical verticalAlignment$iv = Alignment.Companion.getCenterVertically();
            $composer.startReplaceableGroup(693286680);
            ComposerKt.sourceInformation($composer, "CC(Row)P(2,1,3)90@4553L58,91@4616L130:Row.kt#2w3rfo");
            Arrangement.Horizontal horizontalArrangement$iv = Arrangement.INSTANCE.getStart();
            List themes2 = themes;
            MeasurePolicy measurePolicy$iv = RowKt.rowMeasurePolicy(horizontalArrangement$iv, verticalAlignment$iv, $composer, ((384 >> 3) & 14) | ((384 >> 3) & 112));
            int $changed$iv$iv = (384 << 3) & 112;
            Iterable $this$forEach$iv2 = $this$forEach$iv;
            $composer.startReplaceableGroup(-1323940314);
            ComposerKt.sourceInformation($composer, "CC(Layout)P(!1,2)78@3182L23,80@3272L420:Layout.kt#80mrfh");
            int compositeKeyHash$iv$iv = ComposablesKt.getCurrentCompositeKeyHash($composer, 0);
            CompositionLocalMap localMap$iv$iv = $composer.getCurrentCompositionLocalMap();
            Function0 factory$iv$iv$iv2 = ComposeUiNode.Companion.getConstructor();
            Function3 skippableUpdate$iv$iv$iv = LayoutKt.modifierMaterializerOf(modifier$iv);
            int $changed$iv$iv$iv = (($changed$iv$iv << 9) & 7168) | 6;
            if (!($composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer.startReusableNode();
            if ($composer.getInserting()) {
                factory$iv$iv$iv = factory$iv$iv$iv2;
                $composer.createNode(factory$iv$iv$iv);
            } else {
                factory$iv$iv$iv = factory$iv$iv$iv2;
                $composer.useNode();
            }
            Composer $this$Layout_u24lambda_u240$iv$iv = Updater.constructor-impl($composer);
            Updater.set-impl($this$Layout_u24lambda_u240$iv$iv, measurePolicy$iv, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl($this$Layout_u24lambda_u240$iv$iv, localMap$iv$iv, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 block$iv$iv$iv = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (!$this$Layout_u24lambda_u240$iv$iv.getInserting() && Intrinsics.areEqual($this$Layout_u24lambda_u240$iv$iv.rememberedValue(), Integer.valueOf(compositeKeyHash$iv$iv))) {
                skippableUpdate$iv$iv$iv.invoke(SkippableUpdater.box-impl(SkippableUpdater.constructor-impl($composer)), $composer, Integer.valueOf(($changed$iv$iv$iv >> 3) & 112));
                $composer.startReplaceableGroup(2058660585);
                int i2 = ($changed$iv$iv$iv >> 9) & 14;
                ComposerKt.sourceInformationMarkerStart($composer, -326681643, "C92@4661L9:Row.kt#2w3rfo");
                RowScope rowScope = RowScopeInstance.INSTANCE;
                int i3 = ((384 >> 6) & 112) | 6;
                ComposerKt.sourceInformationMarkerStart($composer, 214988740, "C1248@52973L32,1248@52909L97,1249@53027L39,1250@53087L38:MainActivity.kt#to5c3");
                AdminConfigSubscreen$lambda$149 = MainActivityKt.AdminConfigSubscreen$lambda$149(mutableState);
                boolean areEqual = Intrinsics.areEqual(AdminConfigSubscreen$lambda$149, theme.getSecond());
                $composer.startReplaceableGroup(-1655631001);
                ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
                invalid$iv = $composer.changed(theme);
                it$iv = $composer.rememberedValue();
                if (!invalid$iv && it$iv != Composer.Companion.getEmpty()) {
                    value$iv2 = it$iv;
                    $composer.endReplaceableGroup();
                    RadioButtonKt.RadioButton(areEqual, (Function0) value$iv2, (Modifier) null, false, (RadioButtonColors) null, (MutableInteractionSource) null, $composer, 0, 60);
                    SpacerKt.Spacer(SizeKt.width-3ABfNKs(Modifier.Companion, Dp.constructor-impl(8)), $composer, 6);
                    TextKt.Text--4IGK_g((String) theme.getFirst(), (Modifier) null, Color.Companion.getWhite-0d7_KjU(), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 384, 0, 131066);
                    ComposerKt.sourceInformationMarkerEnd($composer);
                    ComposerKt.sourceInformationMarkerEnd($composer);
                    $composer.endReplaceableGroup();
                    $composer.endNode();
                    $composer.endReplaceableGroup();
                    $composer.endReplaceableGroup();
                    themes = themes2;
                    $this$forEach$iv = $this$forEach$iv2;
                    i = 1;
                }
                value$iv2 = new Function0() { // from class: com.example.MainActivityKt$AdminConfigSubscreen$2$1$1$$ExternalSyntheticLambda1
                    public final Object invoke() {
                        Unit invoke$lambda$5$lambda$4$lambda$3$lambda$2;
                        invoke$lambda$5$lambda$4$lambda$3$lambda$2 = MainActivityKt$AdminConfigSubscreen$2$1$1.invoke$lambda$5$lambda$4$lambda$3$lambda$2(theme, mutableState);
                        return invoke$lambda$5$lambda$4$lambda$3$lambda$2;
                    }
                };
                $composer.updateRememberedValue(value$iv2);
                $composer.endReplaceableGroup();
                RadioButtonKt.RadioButton(areEqual, (Function0) value$iv2, (Modifier) null, false, (RadioButtonColors) null, (MutableInteractionSource) null, $composer, 0, 60);
                SpacerKt.Spacer(SizeKt.width-3ABfNKs(Modifier.Companion, Dp.constructor-impl(8)), $composer, 6);
                TextKt.Text--4IGK_g((String) theme.getFirst(), (Modifier) null, Color.Companion.getWhite-0d7_KjU(), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 384, 0, 131066);
                ComposerKt.sourceInformationMarkerEnd($composer);
                ComposerKt.sourceInformationMarkerEnd($composer);
                $composer.endReplaceableGroup();
                $composer.endNode();
                $composer.endReplaceableGroup();
                $composer.endReplaceableGroup();
                themes = themes2;
                $this$forEach$iv = $this$forEach$iv2;
                i = 1;
            }
            $this$Layout_u24lambda_u240$iv$iv.updateRememberedValue(Integer.valueOf(compositeKeyHash$iv$iv));
            $this$Layout_u24lambda_u240$iv$iv.apply(Integer.valueOf(compositeKeyHash$iv$iv), block$iv$iv$iv);
            skippableUpdate$iv$iv$iv.invoke(SkippableUpdater.box-impl(SkippableUpdater.constructor-impl($composer)), $composer, Integer.valueOf(($changed$iv$iv$iv >> 3) & 112));
            $composer.startReplaceableGroup(2058660585);
            int i22 = ($changed$iv$iv$iv >> 9) & 14;
            ComposerKt.sourceInformationMarkerStart($composer, -326681643, "C92@4661L9:Row.kt#2w3rfo");
            RowScope rowScope2 = RowScopeInstance.INSTANCE;
            int i32 = ((384 >> 6) & 112) | 6;
            ComposerKt.sourceInformationMarkerStart($composer, 214988740, "C1248@52973L32,1248@52909L97,1249@53027L39,1250@53087L38:MainActivity.kt#to5c3");
            AdminConfigSubscreen$lambda$149 = MainActivityKt.AdminConfigSubscreen$lambda$149(mutableState);
            boolean areEqual2 = Intrinsics.areEqual(AdminConfigSubscreen$lambda$149, theme.getSecond());
            $composer.startReplaceableGroup(-1655631001);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            invalid$iv = $composer.changed(theme);
            it$iv = $composer.rememberedValue();
            if (!invalid$iv) {
                value$iv2 = it$iv;
                $composer.endReplaceableGroup();
                RadioButtonKt.RadioButton(areEqual2, (Function0) value$iv2, (Modifier) null, false, (RadioButtonColors) null, (MutableInteractionSource) null, $composer, 0, 60);
                SpacerKt.Spacer(SizeKt.width-3ABfNKs(Modifier.Companion, Dp.constructor-impl(8)), $composer, 6);
                TextKt.Text--4IGK_g((String) theme.getFirst(), (Modifier) null, Color.Companion.getWhite-0d7_KjU(), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 384, 0, 131066);
                ComposerKt.sourceInformationMarkerEnd($composer);
                ComposerKt.sourceInformationMarkerEnd($composer);
                $composer.endReplaceableGroup();
                $composer.endNode();
                $composer.endReplaceableGroup();
                $composer.endReplaceableGroup();
                themes = themes2;
                $this$forEach$iv = $this$forEach$iv2;
                i = 1;
            }
            value$iv2 = new Function0() { // from class: com.example.MainActivityKt$AdminConfigSubscreen$2$1$1$$ExternalSyntheticLambda1
                public final Object invoke() {
                    Unit invoke$lambda$5$lambda$4$lambda$3$lambda$2;
                    invoke$lambda$5$lambda$4$lambda$3$lambda$2 = MainActivityKt$AdminConfigSubscreen$2$1$1.invoke$lambda$5$lambda$4$lambda$3$lambda$2(theme, mutableState);
                    return invoke$lambda$5$lambda$4$lambda$3$lambda$2;
                }
            };
            $composer.updateRememberedValue(value$iv2);
            $composer.endReplaceableGroup();
            RadioButtonKt.RadioButton(areEqual2, (Function0) value$iv2, (Modifier) null, false, (RadioButtonColors) null, (MutableInteractionSource) null, $composer, 0, 60);
            SpacerKt.Spacer(SizeKt.width-3ABfNKs(Modifier.Companion, Dp.constructor-impl(8)), $composer, 6);
            TextKt.Text--4IGK_g((String) theme.getFirst(), (Modifier) null, Color.Companion.getWhite-0d7_KjU(), 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1) null, (TextStyle) null, $composer, 384, 0, 131066);
            ComposerKt.sourceInformationMarkerEnd($composer);
            ComposerKt.sourceInformationMarkerEnd($composer);
            $composer.endReplaceableGroup();
            $composer.endNode();
            $composer.endReplaceableGroup();
            $composer.endReplaceableGroup();
            themes = themes2;
            $this$forEach$iv = $this$forEach$iv2;
            i = 1;
        }
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$5$lambda$1$lambda$0(Pair $theme, MutableState $selectedTheme$delegate) {
        $selectedTheme$delegate.setValue((String) $theme.getSecond());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$5$lambda$4$lambda$3$lambda$2(Pair $theme, MutableState $selectedTheme$delegate) {
        $selectedTheme$delegate.setValue((String) $theme.getSecond());
        return Unit.INSTANCE;
    }
}
