package com.example.ui;

import com.example.data.Review;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
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
@DebugMetadata(c = "com.example.ui.DaliliViewModel$updateProviderRatingAsync$1", f = "DaliliViewModel.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: /tmp/dex/classes5.dex */
public final class DaliliViewModel$updateProviderRatingAsync$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ int $providerId;
    int label;
    final /* synthetic */ DaliliViewModel this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DaliliViewModel$updateProviderRatingAsync$1(DaliliViewModel daliliViewModel, int i, Continuation<? super DaliliViewModel$updateProviderRatingAsync$1> continuation) {
        super(2, continuation);
        this.this$0 = daliliViewModel;
        this.$providerId = i;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new DaliliViewModel$updateProviderRatingAsync$1(this.this$0, this.$providerId, continuation);
    }

    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return create(coroutineScope, continuation).invokeSuspend(Unit.INSTANCE);
    }

    public final Object invokeSuspend(Object obj) {
        FirebaseFirestore firebaseFirestore;
        IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                Iterable $this$filterTo$iv$iv = (Iterable) this.this$0.getReviews().getValue();
                int i = this.$providerId;
                Collection destination$iv$iv = new ArrayList();
                Iterator it = $this$filterTo$iv$iv.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        Iterable provReviews = (List) destination$iv$iv;
                        if (!((Collection) provReviews).isEmpty()) {
                            Iterable $this$map$iv = provReviews;
                            Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                            for (Object item$iv$iv : $this$map$iv) {
                                Review it2 = (Review) item$iv$iv;
                                destination$iv$iv2.add(Boxing.boxDouble(it2.getRating()));
                            }
                            double avg = CollectionsKt.averageOfDouble((List) destination$iv$iv2);
                            firebaseFirestore = this.this$0.db;
                            firebaseFirestore.collection("service_providers").document(String.valueOf(this.$providerId)).update("rating", Boxing.boxDouble(avg), new Object[0]);
                        }
                        return Unit.INSTANCE;
                    }
                    Object element$iv$iv = it.next();
                    Review it3 = (Review) element$iv$iv;
                    if (it3.getProviderId() == i) {
                        destination$iv$iv.add(element$iv$iv);
                    }
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}
