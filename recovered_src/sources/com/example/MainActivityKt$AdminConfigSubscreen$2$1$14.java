package com.example;

import android.content.Context;
import android.widget.Toast;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.example.ui.DaliliViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$AdminConfigSubscreen$2$1$14 implements Function3<LazyItemScope, Composer, Integer, Unit> {
    final /* synthetic */ MutableState<String> $aboutAppSubtitle$delegate;
    final /* synthetic */ MutableState<String> $appLogo$delegate;
    final /* synthetic */ MutableState<String> $appName$delegate;
    final /* synthetic */ MutableState<String> $appShareText$delegate;
    final /* synthetic */ MutableState<String> $appUpdatesUrl$delegate;
    final /* synthetic */ MutableState<String> $assistantWelcomeText$delegate;
    final /* synthetic */ Context $context;
    final /* synthetic */ MutableState<String> $footerText$delegate;
    final /* synthetic */ MutableState<String> $selectedTheme$delegate;
    final /* synthetic */ MutableState<Boolean> $showFooter$delegate;
    final /* synthetic */ MutableState<String> $supportEmail$delegate;
    final /* synthetic */ MutableState<String> $supportPhone$delegate;
    final /* synthetic */ MutableState<String> $supportWhatsapp$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;
    final /* synthetic */ MutableState<String> $welcomeImage$delegate;
    final /* synthetic */ MutableState<String> $welcomeText$delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$AdminConfigSubscreen$2$1$14(DaliliViewModel daliliViewModel, Context context, MutableState<String> mutableState, MutableState<String> mutableState2, MutableState<String> mutableState3, MutableState<String> mutableState4, MutableState<String> mutableState5, MutableState<String> mutableState6, MutableState<String> mutableState7, MutableState<String> mutableState8, MutableState<String> mutableState9, MutableState<Boolean> mutableState10, MutableState<String> mutableState11, MutableState<String> mutableState12, MutableState<String> mutableState13, MutableState<String> mutableState14) {
        this.$viewModel = daliliViewModel;
        this.$context = context;
        this.$selectedTheme$delegate = mutableState;
        this.$appName$delegate = mutableState2;
        this.$welcomeText$delegate = mutableState3;
        this.$welcomeImage$delegate = mutableState4;
        this.$appLogo$delegate = mutableState5;
        this.$supportPhone$delegate = mutableState6;
        this.$supportEmail$delegate = mutableState7;
        this.$supportWhatsapp$delegate = mutableState8;
        this.$footerText$delegate = mutableState9;
        this.$showFooter$delegate = mutableState10;
        this.$aboutAppSubtitle$delegate = mutableState11;
        this.$appUpdatesUrl$delegate = mutableState12;
        this.$appShareText$delegate = mutableState13;
        this.$assistantWelcomeText$delegate = mutableState14;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x00f3  */
    /* JADX WARN: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void invoke(androidx.compose.foundation.lazy.LazyItemScope r39, androidx.compose.runtime.Composer r40, int r41) {
        /*
            Method dump skipped, instructions count: 247
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AdminConfigSubscreen$2$1$14.invoke(androidx.compose.foundation.lazy.LazyItemScope, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$4$lambda$3(DaliliViewModel $viewModel, MutableState $selectedTheme$delegate, MutableState $appName$delegate, MutableState $welcomeText$delegate, MutableState $welcomeImage$delegate, MutableState $appLogo$delegate, MutableState $supportPhone$delegate, MutableState $supportEmail$delegate, MutableState $supportWhatsapp$delegate, MutableState $footerText$delegate, MutableState $showFooter$delegate, MutableState $aboutAppSubtitle$delegate, MutableState $appUpdatesUrl$delegate, MutableState $appShareText$delegate, MutableState $assistantWelcomeText$delegate, final Context $context) {
        String AdminConfigSubscreen$lambda$149;
        String AdminConfigSubscreen$lambda$110;
        String AdminConfigSubscreen$lambda$113;
        String AdminConfigSubscreen$lambda$116;
        String AdminConfigSubscreen$lambda$119;
        String AdminConfigSubscreen$lambda$122;
        String AdminConfigSubscreen$lambda$125;
        String AdminConfigSubscreen$lambda$128;
        String AdminConfigSubscreen$lambda$131;
        boolean AdminConfigSubscreen$lambda$146;
        String AdminConfigSubscreen$lambda$134;
        String AdminConfigSubscreen$lambda$137;
        String AdminConfigSubscreen$lambda$140;
        String AdminConfigSubscreen$lambda$143;
        AdminConfigSubscreen$lambda$149 = MainActivityKt.AdminConfigSubscreen$lambda$149($selectedTheme$delegate);
        AdminConfigSubscreen$lambda$110 = MainActivityKt.AdminConfigSubscreen$lambda$110($appName$delegate);
        AdminConfigSubscreen$lambda$113 = MainActivityKt.AdminConfigSubscreen$lambda$113($welcomeText$delegate);
        AdminConfigSubscreen$lambda$116 = MainActivityKt.AdminConfigSubscreen$lambda$116($welcomeImage$delegate);
        String str = AdminConfigSubscreen$lambda$116;
        String str2 = null;
        if (str.length() == 0) {
            str = null;
        }
        String str3 = str;
        AdminConfigSubscreen$lambda$119 = MainActivityKt.AdminConfigSubscreen$lambda$119($appLogo$delegate);
        String str4 = AdminConfigSubscreen$lambda$119;
        if (!(str4.length() == 0)) {
            str2 = str4;
        }
        String str5 = str2;
        AdminConfigSubscreen$lambda$122 = MainActivityKt.AdminConfigSubscreen$lambda$122($supportPhone$delegate);
        AdminConfigSubscreen$lambda$125 = MainActivityKt.AdminConfigSubscreen$lambda$125($supportEmail$delegate);
        AdminConfigSubscreen$lambda$128 = MainActivityKt.AdminConfigSubscreen$lambda$128($supportWhatsapp$delegate);
        AdminConfigSubscreen$lambda$131 = MainActivityKt.AdminConfigSubscreen$lambda$131($footerText$delegate);
        AdminConfigSubscreen$lambda$146 = MainActivityKt.AdminConfigSubscreen$lambda$146($showFooter$delegate);
        AdminConfigSubscreen$lambda$134 = MainActivityKt.AdminConfigSubscreen$lambda$134($aboutAppSubtitle$delegate);
        AdminConfigSubscreen$lambda$137 = MainActivityKt.AdminConfigSubscreen$lambda$137($appUpdatesUrl$delegate);
        AdminConfigSubscreen$lambda$140 = MainActivityKt.AdminConfigSubscreen$lambda$140($appShareText$delegate);
        AdminConfigSubscreen$lambda$143 = MainActivityKt.AdminConfigSubscreen$lambda$143($assistantWelcomeText$delegate);
        $viewModel.updateAppConfig(AdminConfigSubscreen$lambda$149, AdminConfigSubscreen$lambda$110, AdminConfigSubscreen$lambda$113, str3, str5, AdminConfigSubscreen$lambda$122, AdminConfigSubscreen$lambda$125, AdminConfigSubscreen$lambda$128, AdminConfigSubscreen$lambda$131, AdminConfigSubscreen$lambda$146, AdminConfigSubscreen$lambda$134, AdminConfigSubscreen$lambda$137, AdminConfigSubscreen$lambda$140, AdminConfigSubscreen$lambda$143, new Function1() { // from class: com.example.MainActivityKt$AdminConfigSubscreen$2$1$14$$ExternalSyntheticLambda0
            public final Object invoke(Object obj) {
                Unit invoke$lambda$4$lambda$3$lambda$2;
                invoke$lambda$4$lambda$3$lambda$2 = MainActivityKt$AdminConfigSubscreen$2$1$14.invoke$lambda$4$lambda$3$lambda$2($context, ((Boolean) obj).booleanValue());
                return invoke$lambda$4$lambda$3$lambda$2;
            }
        });
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$4$lambda$3$lambda$2(Context $context, boolean success) {
        if (success) {
            Toast.makeText($context, "تم حفظ ومزامنة كل التحديثات والسمة مع جميع الأجهزة فوراً وحفظها بنجاح!", 1).show();
        } else {
            Toast.makeText($context, "فشل حفظ التحديثات", 0).show();
        }
        return Unit.INSTANCE;
    }
}
