package com.example;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.text.input.PlatformImeOptions;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$JoinServiceProviderDialog$1$1$1$1$2 implements Function3<LazyItemScope, Composer, Integer, Unit> {
    final /* synthetic */ MutableState<String> $phone$delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$JoinServiceProviderDialog$1$1$1$1$2(MutableState<String> mutableState) {
        this.$phone$delegate = mutableState;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
        String JoinServiceProviderDialog$lambda$24;
        Object value$iv;
        Intrinsics.checkNotNullParameter($this$item, "$this$item");
        ComposerKt.sourceInformation($composer, "C470@19642L14,468@19546L283:MainActivity.kt#to5c3");
        if (($changed & 17) != 16 || !$composer.getSkipping()) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(2041525583, $changed, -1, "com.example.JoinServiceProviderDialog.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:468)");
            }
            JoinServiceProviderDialog$lambda$24 = MainActivityKt.JoinServiceProviderDialog$lambda$24(this.$phone$delegate);
            $composer.startReplaceableGroup(-1058690596);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            final MutableState<String> mutableState = this.$phone$delegate;
            Object it$iv = $composer.rememberedValue();
            if (it$iv == Composer.Companion.getEmpty()) {
                value$iv = new Function1() { // from class: com.example.MainActivityKt$JoinServiceProviderDialog$1$1$1$1$2$$ExternalSyntheticLambda0
                    public final Object invoke(Object obj) {
                        Unit invoke$lambda$1$lambda$0;
                        invoke$lambda$1$lambda$0 = MainActivityKt$JoinServiceProviderDialog$1$1$1$1$2.invoke$lambda$1$lambda$0(mutableState, (String) obj);
                        return invoke$lambda$1$lambda$0;
                    }
                };
                $composer.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv;
            }
            $composer.endReplaceableGroup();
            MainActivityKt.CustomTextField(JoinServiceProviderDialog$lambda$24, (Function1) value$iv, "رقم الهاتف / الواتساب", null, null, new KeyboardOptions(0, false, KeyboardType.Companion.getPhone-PjHm6EE(), 0, (PlatformImeOptions) null, 27, (DefaultConstructorMarker) null), null, $composer, 197040, 88);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
                return;
            }
            return;
        }
        $composer.skipToGroupEnd();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$1$lambda$0(MutableState $phone$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $phone$delegate.setValue(it);
        return Unit.INSTANCE;
    }
}
