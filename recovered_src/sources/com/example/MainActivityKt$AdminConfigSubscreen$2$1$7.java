package com.example;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$AdminConfigSubscreen$2$1$7 implements Function3<LazyItemScope, Composer, Integer, Unit> {
    final /* synthetic */ MutableState<String> $appUpdatesUrl$delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$AdminConfigSubscreen$2$1$7(MutableState<String> mutableState) {
        this.$appUpdatesUrl$delegate = mutableState;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$1$lambda$0(MutableState $appUpdatesUrl$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $appUpdatesUrl$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
        String AdminConfigSubscreen$lambda$137;
        Object value$iv;
        Intrinsics.checkNotNullParameter($this$item, "$this$item");
        ComposerKt.sourceInformation($composer, "C1277@54041L22,1277@53986L119:MainActivity.kt#to5c3");
        if (($changed & 17) != 16 || !$composer.getSkipping()) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(282490833, $changed, -1, "com.example.AdminConfigSubscreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:1277)");
            }
            AdminConfigSubscreen$lambda$137 = MainActivityKt.AdminConfigSubscreen$lambda$137(this.$appUpdatesUrl$delegate);
            $composer.startReplaceableGroup(693225055);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            final MutableState<String> mutableState = this.$appUpdatesUrl$delegate;
            Object it$iv = $composer.rememberedValue();
            if (it$iv == Composer.Companion.getEmpty()) {
                value$iv = new Function1() { // from class: com.example.MainActivityKt$AdminConfigSubscreen$2$1$7$$ExternalSyntheticLambda0
                    public final Object invoke(Object obj) {
                        Unit invoke$lambda$1$lambda$0;
                        invoke$lambda$1$lambda$0 = MainActivityKt$AdminConfigSubscreen$2$1$7.invoke$lambda$1$lambda$0(mutableState, (String) obj);
                        return invoke$lambda$1$lambda$0;
                    }
                };
                $composer.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv;
            }
            $composer.endReplaceableGroup();
            MainActivityKt.CustomTextField(AdminConfigSubscreen$lambda$137, (Function1) value$iv, "رابط التحديثات للبحث عن تحديث", null, null, null, null, $composer, 432, 120);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
                return;
            }
            return;
        }
        $composer.skipToGroupEnd();
    }
}
