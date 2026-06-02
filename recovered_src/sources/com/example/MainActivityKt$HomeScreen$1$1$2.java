package com.example;

import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.foundation.lazy.grid.GridCells;
import androidx.compose.foundation.lazy.grid.LazyGridDslKt;
import androidx.compose.foundation.lazy.grid.LazyGridItemScope;
import androidx.compose.foundation.lazy.grid.LazyGridScope;
import androidx.compose.foundation.lazy.grid.LazyGridState;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.CardElevation;
import androidx.compose.material3.CardKt;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import com.example.data.Category;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$HomeScreen$1$1$2 implements Function3<LazyItemScope, Composer, Integer, Unit> {
    final /* synthetic */ State<List<Category>> $categories$delegate;
    final /* synthetic */ Function1<Category, Unit> $onCategorySelected;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public MainActivityKt$HomeScreen$1$1$2(State<? extends List<Category>> state, Function1<? super Category, Unit> function1) {
        this.$categories$delegate = state;
        this.$onCategorySelected = function1;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
        invoke((LazyItemScope) p1, (Composer) p2, ((Number) p3).intValue());
        return Unit.INSTANCE;
    }

    public final void invoke(LazyItemScope $this$item, Composer $composer, int $changed) {
        Object value$iv;
        Intrinsics.checkNotNullParameter($this$item, "$this$item");
        ComposerKt.sourceInformation($composer, "C382@16195L1521,377@15930L1786:MainActivity.kt#to5c3");
        if (($changed & 17) != 16 || !$composer.getSkipping()) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1054470766, $changed, -1, "com.example.HomeScreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:377)");
            }
            GridCells fixed = new GridCells.Fixed(2);
            Modifier modifier = SizeKt.height-3ABfNKs(Modifier.Companion, Dp.constructor-impl(380));
            Arrangement.Horizontal horizontal = Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl(12));
            GridCells gridCells = fixed;
            Arrangement.Vertical vertical = Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl(12));
            Arrangement.Horizontal horizontal2 = horizontal;
            $composer.startReplaceableGroup(119788202);
            ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
            boolean invalid$iv = $composer.changed(this.$categories$delegate) | $composer.changed(this.$onCategorySelected);
            final State<List<Category>> state = this.$categories$delegate;
            final Function1<Category, Unit> function1 = this.$onCategorySelected;
            Object it$iv = $composer.rememberedValue();
            if (invalid$iv || it$iv == Composer.Companion.getEmpty()) {
                value$iv = new Function1() { // from class: com.example.MainActivityKt$HomeScreen$1$1$2$$ExternalSyntheticLambda0
                    public final Object invoke(Object obj) {
                        Unit invoke$lambda$3$lambda$2;
                        invoke$lambda$3$lambda$2 = MainActivityKt$HomeScreen$1$1$2.invoke$lambda$3$lambda$2(state, function1, (LazyGridScope) obj);
                        return invoke$lambda$3$lambda$2;
                    }
                };
                $composer.updateRememberedValue(value$iv);
            } else {
                value$iv = it$iv;
            }
            $composer.endReplaceableGroup();
            LazyGridDslKt.LazyVerticalGrid(gridCells, modifier, (LazyGridState) null, (PaddingValues) null, false, vertical, horizontal2, (FlingBehavior) null, false, (Function1) value$iv, $composer, 1769520, 412);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
                return;
            }
            return;
        }
        $composer.skipToGroupEnd();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$3$lambda$2(State $categories$delegate, final Function1 $onCategorySelected, LazyGridScope $this$LazyVerticalGrid) {
        final List items$iv;
        Intrinsics.checkNotNullParameter($this$LazyVerticalGrid, "$this$LazyVerticalGrid");
        items$iv = MainActivityKt.HomeScreen$lambda$9($categories$delegate);
        final Function1 contentType$iv = new Function1() { // from class: com.example.MainActivityKt$HomeScreen$1$1$2$invoke$lambda$3$lambda$2$$inlined$items$default$1
            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return m49invoke((Category) p1);
            }

            /* renamed from: invoke, reason: collision with other method in class */
            public final Void m49invoke(Category category) {
                return null;
            }
        };
        $this$LazyVerticalGrid.items(items$iv.size(), (Function1) null, (Function2) null, new Function1<Integer, Object>() { // from class: com.example.MainActivityKt$HomeScreen$1$1$2$invoke$lambda$3$lambda$2$$inlined$items$default$4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                return invoke(((Number) p1).intValue());
            }

            public final Object invoke(int index) {
                return contentType$iv.invoke(items$iv.get(index));
            }
        }, ComposableLambdaKt.composableLambdaInstance(699646206, true, new Function4<LazyGridItemScope, Integer, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$HomeScreen$1$1$2$invoke$lambda$3$lambda$2$$inlined$items$default$5
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(4);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3, Object p4) {
                invoke((LazyGridItemScope) p1, ((Number) p2).intValue(), (Composer) p3, ((Number) p4).intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(LazyGridItemScope $this$items, int it, Composer $composer, int $changed) {
                Object value$iv;
                ComposerKt.sourceInformation($composer, "C461@19441L22:LazyGridDsl.kt#7791vq");
                int $dirty = $changed;
                if (($changed & 14) == 0) {
                    $dirty |= $composer.changed($this$items) ? 4 : 2;
                }
                if (($changed & 112) == 0) {
                    $dirty |= $composer.changed(it) ? 32 : 16;
                }
                if (($dirty & 731) == 146 && $composer.getSkipping()) {
                    $composer.skipToGroupEnd();
                    return;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(699646206, $dirty, -1, "androidx.compose.foundation.lazy.grid.items.<anonymous> (LazyGridDsl.kt:461)");
                }
                int $changed2 = $dirty & 14;
                final Category category = (Category) items$iv.get(it);
                $composer.startReplaceableGroup(265341730);
                ComposerKt.sourceInformation($composer, "C*387@16398L32,389@16579L11,389@16537L69,384@16265L1419:MainActivity.kt#to5c3");
                Modifier fillMaxWidth$default = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0f, 1, (Object) null);
                $composer.startReplaceableGroup(-1654005706);
                ComposerKt.sourceInformation($composer, "CC(remember):MainActivity.kt#9igjgp");
                boolean invalid$iv = (((($changed2 & 112) ^ 48) > 32 && $composer.changed(category)) || ($changed2 & 48) == 32) | $composer.changed($onCategorySelected);
                Object it$iv = $composer.rememberedValue();
                if (invalid$iv || it$iv == Composer.Companion.getEmpty()) {
                    final Function1 function1 = $onCategorySelected;
                    value$iv = new Function0<Unit>() { // from class: com.example.MainActivityKt$HomeScreen$1$1$2$1$1$1$1$1
                        public /* bridge */ /* synthetic */ Object invoke() {
                            m76invoke();
                            return Unit.INSTANCE;
                        }

                        /* renamed from: invoke, reason: collision with other method in class */
                        public final void m76invoke() {
                            function1.invoke(category);
                        }
                    };
                    $composer.updateRememberedValue(value$iv);
                } else {
                    value$iv = it$iv;
                }
                $composer.endReplaceableGroup();
                CardKt.Card(ClickableKt.clickable-XHw0xAI$default(fillMaxWidth$default, false, (String) null, (Role) null, (Function0) value$iv, 7, (Object) null), RoundedCornerShapeKt.RoundedCornerShape-0680j_4(Dp.constructor-impl(12)), CardDefaults.INSTANCE.cardColors-ro_MJ88(MaterialTheme.INSTANCE.getColorScheme($composer, MaterialTheme.$stable).getSurfaceVariant-0d7_KjU(), 0L, 0L, 0L, $composer, CardDefaults.$stable << 12, 14), (CardElevation) null, (BorderStroke) null, ComposableLambdaKt.composableLambda($composer, 2078364092, true, new Function3<ColumnScope, Composer, Integer, Unit>() { // from class: com.example.MainActivityKt$HomeScreen$1$1$2$1$1$1$2
                    public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                        invoke((ColumnScope) p1, (Composer) p2, ((Number) p3).intValue());
                        return Unit.INSTANCE;
                    }

                    /* JADX WARN: Removed duplicated region for block: B:24:0x021f  */
                    /* JADX WARN: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                        To view partially-correct add '--show-bad-code' argument
                    */
                    public final void invoke(androidx.compose.foundation.layout.ColumnScope r74, androidx.compose.runtime.Composer r75, int r76) {
                        /*
                            Method dump skipped, instructions count: 547
                            To view this dump add '--comments-level debug' option
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.example.MainActivityKt$HomeScreen$1$1$2$1$1$1$2.invoke(androidx.compose.foundation.layout.ColumnScope, androidx.compose.runtime.Composer, int):void");
                    }
                }), $composer, 196608, 24);
                $composer.endReplaceableGroup();
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            }
        }));
        return Unit.INSTANCE;
    }
}
