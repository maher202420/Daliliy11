package com.example;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.State;
import com.example.data.Category;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$JoinServiceProviderDialog$1$1$1$1$5 implements Function3<LazyItemScope, Composer, Integer, Unit> {
    final /* synthetic */ State<List<Category>> $categories$delegate;
    final /* synthetic */ MutableState<Integer> $selectedCategoryId$delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public MainActivityKt$JoinServiceProviderDialog$1$1$1$1$5(State<? extends List<Category>> state, MutableState<Integer> mutableState) {
        this.$categories$delegate = state;
        this.$selectedCategoryId$delegate = mutableState;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x01b9  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x02f1  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x02fd  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x03d7  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x03e4 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0303  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x04cf  */
    /* JADX WARN: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void invoke(androidx.compose.foundation.lazy.LazyItemScope r84, androidx.compose.runtime.Composer r85, int r86) {
        /*
            Method dump skipped, instructions count: 1235
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$JoinServiceProviderDialog$1$1$1$1$5.invoke(androidx.compose.foundation.lazy.LazyItemScope, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$6$lambda$5$lambda$1$lambda$0(Category $cat, MutableState $selectedCategoryId$delegate) {
        $selectedCategoryId$delegate.setValue($cat.getId());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$6$lambda$5$lambda$4$lambda$3$lambda$2(Category $cat, MutableState $selectedCategoryId$delegate) {
        $selectedCategoryId$delegate.setValue($cat.getId());
        return Unit.INSTANCE;
    }
}
