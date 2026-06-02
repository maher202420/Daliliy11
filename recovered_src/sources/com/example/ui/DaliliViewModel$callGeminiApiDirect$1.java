package com.example.ui;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: DaliliViewModel.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
@DebugMetadata(c = "com.example.ui.DaliliViewModel", f = "DaliliViewModel.kt", i = {}, l = {591}, m = "callGeminiApiDirect", n = {}, s = {})
/* loaded from: /tmp/dex/classes5.dex */
public final class DaliliViewModel$callGeminiApiDirect$1 extends ContinuationImpl {
    int label;
    /* synthetic */ Object result;
    final /* synthetic */ DaliliViewModel this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DaliliViewModel$callGeminiApiDirect$1(DaliliViewModel daliliViewModel, Continuation<? super DaliliViewModel$callGeminiApiDirect$1> continuation) {
        super(continuation);
        this.this$0 = daliliViewModel;
    }

    public final Object invokeSuspend(Object obj) {
        this.result = obj;
        this.label |= Integer.MIN_VALUE;
        return this.this$0.callGeminiApiDirect(null, (Continuation) this);
    }
}
