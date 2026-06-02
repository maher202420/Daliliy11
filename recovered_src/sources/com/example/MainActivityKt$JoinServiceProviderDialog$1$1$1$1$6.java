package com.example;

import android.content.Context;
import android.widget.Toast;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.example.ui.DaliliViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$JoinServiceProviderDialog$1$1$1$1$6 implements Function3<LazyItemScope, Composer, Integer, Unit> {
    final /* synthetic */ Context $context;
    final /* synthetic */ MutableState<String> $imageUrl$delegate;
    final /* synthetic */ MutableState<String> $name$delegate;
    final /* synthetic */ Function0<Unit> $onDismiss;
    final /* synthetic */ MutableState<String> $phone$delegate;
    final /* synthetic */ MutableState<String> $region$delegate;
    final /* synthetic */ MutableState<Integer> $selectedCategoryId$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$JoinServiceProviderDialog$1$1$1$1$6(Function0<Unit> function0, Context context, DaliliViewModel daliliViewModel, MutableState<String> mutableState, MutableState<String> mutableState2, MutableState<Integer> mutableState3, MutableState<String> mutableState4, MutableState<String> mutableState5) {
        this.$onDismiss = function0;
        this.$context = context;
        this.$viewModel = daliliViewModel;
        this.$name$delegate = mutableState;
        this.$phone$delegate = mutableState2;
        this.$selectedCategoryId$delegate = mutableState3;
        this.$imageUrl$delegate = mutableState4;
        this.$region$delegate = mutableState5;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0251  */
    /* JADX WARN: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void invoke(androidx.compose.foundation.lazy.LazyItemScope r53, androidx.compose.runtime.Composer r54, int r55) {
        /*
            Method dump skipped, instructions count: 597
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$JoinServiceProviderDialog$1$1$1$1$6.invoke(androidx.compose.foundation.lazy.LazyItemScope, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$3$lambda$2$lambda$1(final Context $context, DaliliViewModel $viewModel, MutableState $name$delegate, MutableState $phone$delegate, MutableState $selectedCategoryId$delegate, MutableState $imageUrl$delegate, MutableState $region$delegate, final Function0 $onDismiss) {
        String JoinServiceProviderDialog$lambda$21;
        String JoinServiceProviderDialog$lambda$24;
        Integer JoinServiceProviderDialog$lambda$33;
        String JoinServiceProviderDialog$lambda$212;
        String JoinServiceProviderDialog$lambda$242;
        Integer JoinServiceProviderDialog$lambda$332;
        String JoinServiceProviderDialog$lambda$30;
        String JoinServiceProviderDialog$lambda$27;
        JoinServiceProviderDialog$lambda$21 = MainActivityKt.JoinServiceProviderDialog$lambda$21($name$delegate);
        if (!(JoinServiceProviderDialog$lambda$21.length() == 0)) {
            JoinServiceProviderDialog$lambda$24 = MainActivityKt.JoinServiceProviderDialog$lambda$24($phone$delegate);
            if (!(JoinServiceProviderDialog$lambda$24.length() == 0)) {
                JoinServiceProviderDialog$lambda$33 = MainActivityKt.JoinServiceProviderDialog$lambda$33($selectedCategoryId$delegate);
                if (JoinServiceProviderDialog$lambda$33 != null) {
                    JoinServiceProviderDialog$lambda$212 = MainActivityKt.JoinServiceProviderDialog$lambda$21($name$delegate);
                    JoinServiceProviderDialog$lambda$242 = MainActivityKt.JoinServiceProviderDialog$lambda$24($phone$delegate);
                    JoinServiceProviderDialog$lambda$332 = MainActivityKt.JoinServiceProviderDialog$lambda$33($selectedCategoryId$delegate);
                    Intrinsics.checkNotNull(JoinServiceProviderDialog$lambda$332);
                    int intValue = JoinServiceProviderDialog$lambda$332.intValue();
                    JoinServiceProviderDialog$lambda$30 = MainActivityKt.JoinServiceProviderDialog$lambda$30($imageUrl$delegate);
                    JoinServiceProviderDialog$lambda$27 = MainActivityKt.JoinServiceProviderDialog$lambda$27($region$delegate);
                    $viewModel.addPendingProvider(JoinServiceProviderDialog$lambda$212, JoinServiceProviderDialog$lambda$242, intValue, null, JoinServiceProviderDialog$lambda$30, JoinServiceProviderDialog$lambda$27, new Function1() { // from class: com.example.MainActivityKt$JoinServiceProviderDialog$1$1$1$1$6$$ExternalSyntheticLambda0
                        public final Object invoke(Object obj) {
                            Unit invoke$lambda$3$lambda$2$lambda$1$lambda$0;
                            invoke$lambda$3$lambda$2$lambda$1$lambda$0 = MainActivityKt$JoinServiceProviderDialog$1$1$1$1$6.invoke$lambda$3$lambda$2$lambda$1$lambda$0($context, $onDismiss, ((Boolean) obj).booleanValue());
                            return invoke$lambda$3$lambda$2$lambda$1$lambda$0;
                        }
                    });
                    return Unit.INSTANCE;
                }
                Toast.makeText($context, "الرجاء تعبئة جميع الحقول المطلوبة واختيار الفئة", 0).show();
                return Unit.INSTANCE;
            }
        }
        Toast.makeText($context, "الرجاء تعبئة جميع الحقول المطلوبة واختيار الفئة", 0).show();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$3$lambda$2$lambda$1$lambda$0(Context $context, Function0 $onDismiss, boolean success) {
        if (success) {
            Toast.makeText($context, "تم تقديم طلبك بنجاح وسيتلقى الأدمن تفاصيله وصورتك للمراجعة!", 1).show();
            $onDismiss.invoke();
        } else {
            Toast.makeText($context, "فشل في تقديم الطلب، حاول مرة أخرى", 0).show();
        }
        return Unit.INSTANCE;
    }
}
