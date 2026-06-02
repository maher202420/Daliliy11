package com.example.ui;

import com.yemende.BuildConfig;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: DaliliViewModel.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 0, 0}, xi = 48)
@DebugMetadata(c = "com.example.ui.DaliliViewModel$askAssistant$1", f = "DaliliViewModel.kt", i = {}, l = {579}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: /tmp/dex/classes5.dex */
public final class DaliliViewModel$askAssistant$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ String $question;
    int label;
    final /* synthetic */ DaliliViewModel this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DaliliViewModel$askAssistant$1(DaliliViewModel daliliViewModel, String str, Continuation<? super DaliliViewModel$askAssistant$1> continuation) {
        super(2, continuation);
        this.this$0 = daliliViewModel;
        this.$question = str;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new DaliliViewModel$askAssistant$1(this.this$0, this.$question, continuation);
    }

    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return create(coroutineScope, continuation).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v8, types: [com.example.ui.DaliliViewModel] */
    /* JADX WARN: Type inference failed for: r8v0, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v10, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v2 */
    /* JADX WARN: Type inference failed for: r8v9 */
    public final Object invokeSuspend(Object obj) {
        MutableStateFlow mutableStateFlow;
        MutableStateFlow mutableStateFlow2;
        Object obj2;
        Object obj3;
        MutableStateFlow mutableStateFlow3;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        try {
            try {
                switch (this.label) {
                    case 0:
                        ResultKt.throwOnFailure((Object) obj);
                        this.label = 1;
                        Object callGeminiApiDirect = this.this$0.callGeminiApiDirect(this.$question, (Continuation) this);
                        if (callGeminiApiDirect != coroutine_suspended) {
                            obj2 = obj;
                            obj3 = callGeminiApiDirect;
                            break;
                        } else {
                            return coroutine_suspended;
                        }
                    case BuildConfig.VERSION_CODE /* 1 */:
                        ResultKt.throwOnFailure((Object) obj);
                        obj2 = obj;
                        obj3 = obj;
                        break;
                    default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
            } catch (Exception e) {
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            obj = (String) obj3;
            this.this$0.addChatMessage(obj, false);
            mutableStateFlow3 = this.this$0._isAssistantLoading;
            mutableStateFlow3.setValue(Boxing.boxBoolean(false));
        } catch (Exception e2) {
            obj = obj2;
            this.this$0.addChatMessage(this.this$0.getOfflineAnswer(this.$question), false);
            mutableStateFlow2 = this.this$0._isAssistantLoading;
            mutableStateFlow2.setValue(Boxing.boxBoolean(false));
            return Unit.INSTANCE;
        } catch (Throwable th2) {
            th = th2;
            mutableStateFlow = this.this$0._isAssistantLoading;
            mutableStateFlow.setValue(Boxing.boxBoolean(false));
            throw th;
        }
        return Unit.INSTANCE;
    }
}
