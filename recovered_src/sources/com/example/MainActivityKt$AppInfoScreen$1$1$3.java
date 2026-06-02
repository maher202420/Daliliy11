package com.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.State;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$AppInfoScreen$1$1$3 implements Function3<LazyItemScope, Composer, Integer, Unit> {
    final /* synthetic */ State<String> $appShareText$delegate;
    final /* synthetic */ State<String> $appUpdatesUrl$delegate;
    final /* synthetic */ Context $context;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$AppInfoScreen$1$1$3(State<String> state, Context context, State<String> state2) {
        this.$appUpdatesUrl$delegate = state;
        this.$context = context;
        this.$appShareText$delegate = state2;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0222  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x02b5  */
    /* JADX WARN: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x022f A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void invoke(androidx.compose.foundation.lazy.LazyItemScope r42, androidx.compose.runtime.Composer r43, int r44) {
        /*
            Method dump skipped, instructions count: 697
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AppInfoScreen$1$1$3.invoke(androidx.compose.foundation.lazy.LazyItemScope, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$5$lambda$1$lambda$0(Context $context, State $appUpdatesUrl$delegate) {
        String AppInfoScreen$lambda$86;
        AppInfoScreen$lambda$86 = MainActivityKt.AppInfoScreen$lambda$86($appUpdatesUrl$delegate);
        Intent updatesIntent = new Intent("android.intent.action.VIEW", Uri.parse(AppInfoScreen$lambda$86));
        $context.startActivity(updatesIntent);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$5$lambda$4$lambda$3(Context $context, State $appShareText$delegate) {
        String AppInfoScreen$lambda$87;
        Intent shareIntent = new Intent();
        shareIntent.setAction("android.intent.action.SEND");
        AppInfoScreen$lambda$87 = MainActivityKt.AppInfoScreen$lambda$87($appShareText$delegate);
        shareIntent.putExtra("android.intent.extra.TEXT", AppInfoScreen$lambda$87);
        shareIntent.setType("text/plain");
        $context.startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"));
        return Unit.INSTANCE;
    }
}
