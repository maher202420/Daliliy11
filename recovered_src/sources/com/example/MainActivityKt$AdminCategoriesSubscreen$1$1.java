package com.example;

import android.content.Context;
import android.widget.Toast;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.example.ui.DaliliViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$AdminCategoriesSubscreen$1$1 implements Function3<ColumnScope, Composer, Integer, Unit> {
    final /* synthetic */ Context $context;
    final /* synthetic */ MutableState<String> $icon$delegate;
    final /* synthetic */ MutableState<String> $nameAr$delegate;
    final /* synthetic */ MutableState<String> $orderIndex$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MainActivityKt$AdminCategoriesSubscreen$1$1(Context context, DaliliViewModel daliliViewModel, MutableState<String> mutableState, MutableState<String> mutableState2, MutableState<String> mutableState3) {
        this.$context = context;
        this.$viewModel = daliliViewModel;
        this.$nameAr$delegate = mutableState;
        this.$icon$delegate = mutableState2;
        this.$orderIndex$delegate = mutableState3;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0229  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0235  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x030b  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x036e  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x03de  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0440  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x049d  */
    /* JADX WARN: Removed duplicated region for block: B:51:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x03ec  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x037c  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x031b  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x023b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void invoke(androidx.compose.foundation.layout.ColumnScope r69, androidx.compose.runtime.Composer r70, int r71) {
        /*
            Method dump skipped, instructions count: 1185
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AdminCategoriesSubscreen$1$1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$10$lambda$4$lambda$1$lambda$0(MutableState $nameAr$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $nameAr$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$10$lambda$4$lambda$3$lambda$2(MutableState $icon$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $icon$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$10$lambda$6$lambda$5(MutableState $orderIndex$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $orderIndex$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$10$lambda$9$lambda$8(final Context $context, DaliliViewModel $viewModel, final MutableState $nameAr$delegate, final MutableState $icon$delegate, final MutableState $orderIndex$delegate) {
        String AdminCategoriesSubscreen$lambda$157;
        String AdminCategoriesSubscreen$lambda$160;
        String AdminCategoriesSubscreen$lambda$1572;
        String AdminCategoriesSubscreen$lambda$1602;
        String AdminCategoriesSubscreen$lambda$163;
        AdminCategoriesSubscreen$lambda$157 = MainActivityKt.AdminCategoriesSubscreen$lambda$157($nameAr$delegate);
        if (!(AdminCategoriesSubscreen$lambda$157.length() == 0)) {
            AdminCategoriesSubscreen$lambda$160 = MainActivityKt.AdminCategoriesSubscreen$lambda$160($icon$delegate);
            if (!(AdminCategoriesSubscreen$lambda$160.length() == 0)) {
                AdminCategoriesSubscreen$lambda$1572 = MainActivityKt.AdminCategoriesSubscreen$lambda$157($nameAr$delegate);
                AdminCategoriesSubscreen$lambda$1602 = MainActivityKt.AdminCategoriesSubscreen$lambda$160($icon$delegate);
                AdminCategoriesSubscreen$lambda$163 = MainActivityKt.AdminCategoriesSubscreen$lambda$163($orderIndex$delegate);
                Integer intOrNull = StringsKt.toIntOrNull(AdminCategoriesSubscreen$lambda$163);
                $viewModel.addCategory(AdminCategoriesSubscreen$lambda$1572, AdminCategoriesSubscreen$lambda$1602, intOrNull != null ? intOrNull.intValue() : 1, new Function1() { // from class: com.example.MainActivityKt$AdminCategoriesSubscreen$1$1$$ExternalSyntheticLambda0
                    public final Object invoke(Object obj) {
                        Unit invoke$lambda$10$lambda$9$lambda$8$lambda$7;
                        invoke$lambda$10$lambda$9$lambda$8$lambda$7 = MainActivityKt$AdminCategoriesSubscreen$1$1.invoke$lambda$10$lambda$9$lambda$8$lambda$7($context, $nameAr$delegate, $icon$delegate, $orderIndex$delegate, ((Boolean) obj).booleanValue());
                        return invoke$lambda$10$lambda$9$lambda$8$lambda$7;
                    }
                });
                return Unit.INSTANCE;
            }
        }
        Toast.makeText($context, "يرجى ملء جميع الحقول", 0).show();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$10$lambda$9$lambda$8$lambda$7(Context $context, MutableState $nameAr$delegate, MutableState $icon$delegate, MutableState $orderIndex$delegate, boolean success) {
        if (success) {
            $nameAr$delegate.setValue("");
            $icon$delegate.setValue("");
            $orderIndex$delegate.setValue("1");
            Toast.makeText($context, "تمت إضافة القسم الرئيسي ومزامنته بجميع الأجهزة!", 0).show();
        }
        return Unit.INSTANCE;
    }
}
