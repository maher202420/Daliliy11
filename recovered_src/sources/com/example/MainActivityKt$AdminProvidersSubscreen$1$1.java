package com.example;

import android.content.Context;
import android.widget.Toast;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.State;
import com.example.data.Category;
import com.example.ui.DaliliViewModel;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$AdminProvidersSubscreen$1$1 implements Function3<ColumnScope, Composer, Integer, Unit> {
    final /* synthetic */ State<List<Category>> $categories$delegate;
    final /* synthetic */ Context $context;
    final /* synthetic */ MutableState<String> $imageUrl$delegate;
    final /* synthetic */ MutableState<Boolean> $isPinned$delegate;
    final /* synthetic */ MutableState<String> $name$delegate;
    final /* synthetic */ MutableState<String> $phone$delegate;
    final /* synthetic */ MutableState<Integer> $selectedCategoryId$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public MainActivityKt$AdminProvidersSubscreen$1$1(Context context, DaliliViewModel daliliViewModel, MutableState<String> mutableState, MutableState<String> mutableState2, MutableState<String> mutableState3, State<? extends List<Category>> state, MutableState<Integer> mutableState4, MutableState<Boolean> mutableState5) {
        this.$context = context;
        this.$viewModel = daliliViewModel;
        this.$name$delegate = mutableState;
        this.$phone$delegate = mutableState2;
        this.$imageUrl$delegate = mutableState3;
        this.$categories$delegate = state;
        this.$selectedCategoryId$delegate = mutableState4;
        this.$isPinned$delegate = mutableState5;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:100:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:102:0x07f1  */
    /* JADX WARN: Removed duplicated region for block: B:104:0x0760 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:105:0x071b  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x0359  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x028c  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x023e  */
    /* JADX WARN: Removed duplicated region for block: B:111:0x01ed  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x01dd  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0230  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x027e  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x0347  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0353  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0428  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x04f7  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0503  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x05d4  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x05e1 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0509  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0685 A[EDGE_INSN: B:76:0x0685->B:77:0x0685 BREAK  A[LOOP:0: B:43:0x0418->B:67:0x05f0], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x070b  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0717  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x074a  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x07e3  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x087a  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x08e5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void invoke(androidx.compose.foundation.layout.ColumnScope r109, androidx.compose.runtime.Composer r110, int r111) {
        /*
            Method dump skipped, instructions count: 2281
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$AdminProvidersSubscreen$1$1.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$20$lambda$1$lambda$0(MutableState $name$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $name$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$20$lambda$3$lambda$2(MutableState $phone$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $phone$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$20$lambda$5$lambda$4(MutableState $imageUrl$delegate, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $imageUrl$delegate.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$20$lambda$12$lambda$11$lambda$7$lambda$6(Category $cat, MutableState $selectedCategoryId$delegate) {
        $selectedCategoryId$delegate.setValue($cat.getId());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$20$lambda$12$lambda$11$lambda$10$lambda$9$lambda$8(Category $cat, MutableState $selectedCategoryId$delegate) {
        $selectedCategoryId$delegate.setValue($cat.getId());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$20$lambda$15$lambda$14$lambda$13(MutableState $isPinned$delegate, boolean it) {
        MainActivityKt.AdminProvidersSubscreen$lambda$188($isPinned$delegate, it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$20$lambda$19$lambda$18(final Context $context, DaliliViewModel $viewModel, final MutableState $name$delegate, final MutableState $phone$delegate, MutableState $selectedCategoryId$delegate, final MutableState $imageUrl$delegate, final MutableState $isPinned$delegate) {
        String AdminProvidersSubscreen$lambda$175;
        String AdminProvidersSubscreen$lambda$178;
        Integer AdminProvidersSubscreen$lambda$184;
        String AdminProvidersSubscreen$lambda$1752;
        String AdminProvidersSubscreen$lambda$1782;
        Integer AdminProvidersSubscreen$lambda$1842;
        String AdminProvidersSubscreen$lambda$181;
        boolean AdminProvidersSubscreen$lambda$187;
        boolean AdminProvidersSubscreen$lambda$1872;
        AdminProvidersSubscreen$lambda$175 = MainActivityKt.AdminProvidersSubscreen$lambda$175($name$delegate);
        if (!(AdminProvidersSubscreen$lambda$175.length() == 0)) {
            AdminProvidersSubscreen$lambda$178 = MainActivityKt.AdminProvidersSubscreen$lambda$178($phone$delegate);
            if (!(AdminProvidersSubscreen$lambda$178.length() == 0)) {
                AdminProvidersSubscreen$lambda$184 = MainActivityKt.AdminProvidersSubscreen$lambda$184($selectedCategoryId$delegate);
                if (AdminProvidersSubscreen$lambda$184 != null) {
                    AdminProvidersSubscreen$lambda$1752 = MainActivityKt.AdminProvidersSubscreen$lambda$175($name$delegate);
                    AdminProvidersSubscreen$lambda$1782 = MainActivityKt.AdminProvidersSubscreen$lambda$178($phone$delegate);
                    AdminProvidersSubscreen$lambda$1842 = MainActivityKt.AdminProvidersSubscreen$lambda$184($selectedCategoryId$delegate);
                    Intrinsics.checkNotNull(AdminProvidersSubscreen$lambda$1842);
                    int intValue = AdminProvidersSubscreen$lambda$1842.intValue();
                    AdminProvidersSubscreen$lambda$181 = MainActivityKt.AdminProvidersSubscreen$lambda$181($imageUrl$delegate);
                    String str = AdminProvidersSubscreen$lambda$181;
                    if (str.length() == 0) {
                        str = null;
                    }
                    AdminProvidersSubscreen$lambda$187 = MainActivityKt.AdminProvidersSubscreen$lambda$187($isPinned$delegate);
                    AdminProvidersSubscreen$lambda$1872 = MainActivityKt.AdminProvidersSubscreen$lambda$187($isPinned$delegate);
                    $viewModel.addServiceProvider(AdminProvidersSubscreen$lambda$1752, AdminProvidersSubscreen$lambda$1782, intValue, null, str, AdminProvidersSubscreen$lambda$187, AdminProvidersSubscreen$lambda$1872, "medium", "near", new Function1() { // from class: com.example.MainActivityKt$AdminProvidersSubscreen$1$1$$ExternalSyntheticLambda7
                        public final Object invoke(Object obj) {
                            Unit invoke$lambda$20$lambda$19$lambda$18$lambda$17;
                            invoke$lambda$20$lambda$19$lambda$18$lambda$17 = MainActivityKt$AdminProvidersSubscreen$1$1.invoke$lambda$20$lambda$19$lambda$18$lambda$17($context, $name$delegate, $phone$delegate, $imageUrl$delegate, $isPinned$delegate, ((Boolean) obj).booleanValue());
                            return invoke$lambda$20$lambda$19$lambda$18$lambda$17;
                        }
                    });
                    return Unit.INSTANCE;
                }
            }
        }
        Toast.makeText($context, "يرجى ملء جميع الحقول وتحديد قسم", 0).show();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$20$lambda$19$lambda$18$lambda$17(Context $context, MutableState $name$delegate, MutableState $phone$delegate, MutableState $imageUrl$delegate, MutableState $isPinned$delegate, boolean success) {
        if (success) {
            $name$delegate.setValue("");
            $phone$delegate.setValue("");
            $imageUrl$delegate.setValue("");
            MainActivityKt.AdminProvidersSubscreen$lambda$188($isPinned$delegate, false);
            Toast.makeText($context, "تمت إضافة وتفعيل مزود الخدمة ومزامنته فوراً!", 0).show();
        }
        return Unit.INSTANCE;
    }
}
