package com.example.ui;

import android.util.Log;
import com.example.data.Category;
import com.example.data.Review;
import com.example.data.ServiceProvider;
import com.example.data.SubCategory;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: DaliliViewModel.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 0, 0}, xi = 48)
@DebugMetadata(c = "com.example.ui.DaliliViewModel$seedInitialDatabase$1", f = "DaliliViewModel.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: /tmp/dex/classes5.dex */
public final class DaliliViewModel$seedInitialDatabase$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    int label;
    final /* synthetic */ DaliliViewModel this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DaliliViewModel$seedInitialDatabase$1(DaliliViewModel daliliViewModel, Continuation<? super DaliliViewModel$seedInitialDatabase$1> continuation) {
        super(2, continuation);
        this.this$0 = daliliViewModel;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new DaliliViewModel$seedInitialDatabase$1(this.this$0, continuation);
    }

    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return create(coroutineScope, continuation).invokeSuspend(Unit.INSTANCE);
    }

    public final Object invokeSuspend(Object obj) {
        Iterable defaultCategories;
        Iterable defaultSubCategories;
        Iterable defaultProviders;
        Iterable defaultReviews;
        FirebaseFirestore firebaseFirestore;
        FirebaseFirestore firebaseFirestore2;
        FirebaseFirestore firebaseFirestore3;
        FirebaseFirestore firebaseFirestore4;
        FirebaseFirestore firebaseFirestore5;
        IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                try {
                    defaultCategories = this.this$0.getDefaultCategories();
                    Iterable $this$forEach$iv = defaultCategories;
                    DaliliViewModel daliliViewModel = this.this$0;
                    for (Object element$iv : $this$forEach$iv) {
                        Category it = (Category) element$iv;
                        firebaseFirestore5 = daliliViewModel.db;
                        firebaseFirestore5.collection("categories").document(String.valueOf(it.getId())).set(it);
                    }
                    defaultSubCategories = this.this$0.getDefaultSubCategories();
                    Iterable $this$forEach$iv2 = defaultSubCategories;
                    DaliliViewModel daliliViewModel2 = this.this$0;
                    for (Object element$iv2 : $this$forEach$iv2) {
                        SubCategory it2 = (SubCategory) element$iv2;
                        firebaseFirestore4 = daliliViewModel2.db;
                        firebaseFirestore4.collection("sub_categories").document(String.valueOf(it2.getId())).set(it2);
                    }
                    defaultProviders = this.this$0.getDefaultProviders();
                    Iterable $this$forEach$iv3 = defaultProviders;
                    DaliliViewModel daliliViewModel3 = this.this$0;
                    for (Object element$iv3 : $this$forEach$iv3) {
                        ServiceProvider it3 = (ServiceProvider) element$iv3;
                        firebaseFirestore3 = daliliViewModel3.db;
                        firebaseFirestore3.collection("service_providers").document(String.valueOf(it3.getId())).set(it3);
                    }
                    defaultReviews = this.this$0.getDefaultReviews();
                    Iterable $this$forEach$iv4 = defaultReviews;
                    DaliliViewModel daliliViewModel4 = this.this$0;
                    for (Object element$iv4 : $this$forEach$iv4) {
                        Review it4 = (Review) element$iv4;
                        firebaseFirestore2 = daliliViewModel4.db;
                        firebaseFirestore2.collection("reviews").document(String.valueOf(it4.getId())).set(it4);
                    }
                    Map initialConfig = MapsKt.mapOf(new Pair[]{TuplesKt.to("theme_choice", "red_black"), TuplesKt.to("custom_app_name", "دليلي - Dalili"), TuplesKt.to("welcome_text", "دليلي - دليلك الشامل لجميع الخدمات والأجهزة الطبية والصيانة في اليمن!"), TuplesKt.to("support_phone", "777644670"), TuplesKt.to("support_email", "support@dalili.ye"), TuplesKt.to("support_whatsapp", "777644670"), TuplesKt.to("footer_text", "جميع الحقوق محفوظة © تطبيق دليلي 2026"), TuplesKt.to("show_footer", Boxing.boxBoolean(true)), TuplesKt.to("about_app_subtitle", "دليلي هو منصة الكترونية شاملة ومجانية تهدف لتسهيل الوصول لمزودي الخدمات الهندسية، الطبية والاتصالات في جميع مناطق الجمهورية."), TuplesKt.to("app_updates_url", "https://dalili.ye/updates"), TuplesKt.to("app_share_text", "حمل الآن تطبيق دليلي للأجهزة والخدمات، دليلك في جيبك!"), TuplesKt.to("assistant_welcome_text", "مرحباً بك! أنا مساعدك الذكي في تطبيق دليلي. كيف يمكنني مساعدتك في العثور على مقدمي الخدمات اليوم؟")});
                    firebaseFirestore = this.this$0.db;
                    firebaseFirestore.collection("app_config").document("global").set(initialConfig);
                } catch (Exception ex) {
                    Log.e("Seeding", "Seeding failed: " + ex.getMessage());
                }
                return Unit.INSTANCE;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}
