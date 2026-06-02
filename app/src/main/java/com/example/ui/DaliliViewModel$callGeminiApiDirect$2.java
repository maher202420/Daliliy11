package com.example.ui;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function2;
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: DaliliViewModel.kt */
@Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001*\u00020\u0003H\n"}, d2 = {"<anonymous>", "", "kotlin.jvm.PlatformType", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 0, 0}, xi = 48)
@DebugMetadata(c = "com.example.ui.DaliliViewModel$callGeminiApiDirect$2", f = "DaliliViewModel.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: /tmp/dex/classes5.dex */
public final class DaliliViewModel$callGeminiApiDirect$2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super String>, Object> {
    final /* synthetic */ String $question;
    int label;
    final /* synthetic */ DaliliViewModel this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DaliliViewModel$callGeminiApiDirect$2(String str, DaliliViewModel daliliViewModel, Continuation<? super DaliliViewModel$callGeminiApiDirect$2> continuation) {
        super(2, continuation);
        this.$question = str;
        this.this$0 = daliliViewModel;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new DaliliViewModel$callGeminiApiDirect$2(this.$question, this.this$0, continuation);
    }

    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super String> continuation) {
        return create(coroutineScope, continuation).invokeSuspend(Unit.INSTANCE);
    }

    public final Object invokeSuspend(Object obj) {
        String body;
        IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                String apiKey = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=AIzaSyBoFpZzhWBpwhYwnlfcPehoUp5HfU4DTGc";
                String escapedQuestion = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(this.$question, "\\", "\\\\", false, 4, (Object) null), "\"", "\\\"", false, 4, (Object) null), "\n", "\\n", false, 4, (Object) null);
                String jsonRequest = StringsKt.trimIndent("\n            {\n                \"contents\": [\n                    {\n                        \"parts\": [\n                            {\n                                \"text\": \"" + escapedQuestion + "\"\n                            }\n                        ]\n                    }\n                ]\n            }\n        ");
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).build();
                RequestBody requestBody = RequestBody.Companion.create(jsonRequest, MediaType.Companion.get("application/json"));
                Request request = new Request.Builder().url(apiKey).post(requestBody).build();
                try {
                    Response response = (Closeable) client.newCall(request).execute();
                    try {
                        Response response2 = response;
                        if (response2.isSuccessful()) {
                            ResponseBody body2 = response2.body();
                            if (body2 == null || (body = body2.string()) == null) {
                                body = "";
                            }
                            JSONObject root = new JSONObject(body);
                            JSONArray candidates = root.getJSONArray("candidates");
                            JSONObject firstCandidate = candidates.getJSONObject(0);
                            JSONObject contentObj = firstCandidate.getJSONObject("content");
                            JSONArray parts = contentObj.getJSONArray("parts");
                            String string = parts.getJSONObject(0).getString("text");
                            CloseableKt.closeFinally(response, (Throwable) null);
                            return string;
                        }
                        throw new Exception("API Error: code = " + response2.code());
                    } finally {
                    }
                } catch (Exception e) {
                    return this.this$0.getOfflineAnswer(this.$question);
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}
