package com.example;

import android.content.Context;
import android.widget.Toast;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.example.ui.DaliliViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$LoginScreen$1$1 implements Function3<ColumnScope, Composer, Integer, Unit> {
    final /* synthetic */ Context $context;
    final /* synthetic */ Function0<Unit> $onLoginSuccess;
    final /* synthetic */ MutableState<String> $password$delegate;
    final /* synthetic */ MutableState<String> $username$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$LoginScreen$1$1(Context context, DaliliViewModel daliliViewModel, Function0<Unit> function0, MutableState<String> mutableState, MutableState<String> mutableState2) {
        this.$context = context;
        this.$viewModel = daliliViewModel;
        this.$onLoginSuccess = function0;
        this.$username$delegate = mutableState;
        this.$password$delegate = mutableState2;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x020a  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x025a  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x032c  */
    /* JADX WARN: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0268  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0218  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void invoke(androidx.compose.foundation.layout.ColumnScope r64, androidx.compose.runtime.Composer r65, int r66) {
        /*
            Method dump skipped, instructions count: 816
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$LoginScreen$1$1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$6$lambda$1$lambda$0(MutableState $username$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $username$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$6$lambda$3$lambda$2(MutableState $password$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $password$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$6$lambda$5$lambda$4(Context $context, DaliliViewModel $viewModel, Function0 $onLoginSuccess, MutableState $username$delegate, MutableState $password$delegate) {
        String LoginScreen$lambda$93;
        String LoginScreen$lambda$96;
        String LoginScreen$lambda$932;
        String LoginScreen$lambda$962;
        LoginScreen$lambda$93 = MainActivityKt.LoginScreen$lambda$93($username$delegate);
        if (!(LoginScreen$lambda$93.length() == 0)) {
            LoginScreen$lambda$96 = MainActivityKt.LoginScreen$lambda$96($password$delegate);
            if (!(LoginScreen$lambda$96.length() == 0)) {
                LoginScreen$lambda$932 = MainActivityKt.LoginScreen$lambda$93($username$delegate);
                LoginScreen$lambda$962 = MainActivityKt.LoginScreen$lambda$96($password$delegate);
                boolean success = $viewModel.login(LoginScreen$lambda$932, LoginScreen$lambda$962);
                if (success) {
                    Toast.makeText($context, "تم تسجيل الدخول بنجاح", 0).show();
                    $onLoginSuccess.invoke();
                } else {
                    Toast.makeText($context, "اسم المستخدم أو كلمة المرور خاطئة!", 1).show();
                }
                return Unit.INSTANCE;
            }
        }
        Toast.makeText($context, "الرجاء تعبئة اسم المستخدم وكلمة المرور", 0).show();
        return Unit.INSTANCE;
    }
}
