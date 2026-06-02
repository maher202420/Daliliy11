package com.example;

import androidx.compose.animation.AnimatedContentKt;
import androidx.compose.animation.AnimatedContentTransitionScope;
import androidx.compose.animation.ContentTransform;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.core.FiniteAnimationSpec;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.example.ui.DaliliViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$MainContent$3 implements Function3<PaddingValues, Composer, Integer, Unit> {
    final /* synthetic */ MutableState<Screen> $currentScreen$delegate;
    final /* synthetic */ MutableState<String> $currentTab$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$MainContent$3(DaliliViewModel daliliViewModel, MutableState<String> mutableState, MutableState<Screen> mutableState2) {
        this.$viewModel = daliliViewModel;
        this.$currentTab$delegate = mutableState;
        this.$currentScreen$delegate = mutableState2;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((PaddingValues) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x017b  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x023c  */
    /* JADX WARN: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0191  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void invoke(androidx.compose.foundation.layout.PaddingValues r41, androidx.compose.runtime.Composer r42, int r43) {
        /*
            Method dump skipped, instructions count: 576
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$MainContent$3.invoke(androidx.compose.foundation.layout.PaddingValues, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ContentTransform invoke$lambda$2$lambda$1$lambda$0(AnimatedContentTransitionScope $this$AnimatedContent) {
        Intrinsics.checkNotNullParameter($this$AnimatedContent, "$this$AnimatedContent");
        return AnimatedContentKt.with(EnterExitTransitionKt.fadeIn$default((FiniteAnimationSpec) null, 0.0f, 3, (Object) null), EnterExitTransitionKt.fadeOut$default((FiniteAnimationSpec) null, 0.0f, 3, (Object) null));
    }
}
