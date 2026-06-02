package com.example;

import androidx.compose.runtime.MutableState;
import com.example.ui.DaliliViewModel;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 0, 0}, xi = 48)
@DebugMetadata(c = "com.example.MainActivityKt$AdminConfigSubscreen$1$1", f = "MainActivity.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: /tmp/dex/classes5.dex */
public final class MainActivityKt$AdminConfigSubscreen$1$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ MutableState<String> $aboutAppSubtitle$delegate;
    final /* synthetic */ MutableState<String> $appLogo$delegate;
    final /* synthetic */ MutableState<String> $appName$delegate;
    final /* synthetic */ MutableState<String> $appShareText$delegate;
    final /* synthetic */ MutableState<String> $appUpdatesUrl$delegate;
    final /* synthetic */ MutableState<String> $assistantWelcomeText$delegate;
    final /* synthetic */ MutableState<String> $footerText$delegate;
    final /* synthetic */ MutableState<String> $selectedTheme$delegate;
    final /* synthetic */ MutableState<Boolean> $showFooter$delegate;
    final /* synthetic */ MutableState<String> $supportEmail$delegate;
    final /* synthetic */ MutableState<String> $supportPhone$delegate;
    final /* synthetic */ MutableState<String> $supportWhatsapp$delegate;
    final /* synthetic */ DaliliViewModel $viewModel;
    final /* synthetic */ MutableState<String> $welcomeImage$delegate;
    final /* synthetic */ MutableState<String> $welcomeText$delegate;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public MainActivityKt$AdminConfigSubscreen$1$1(DaliliViewModel daliliViewModel, MutableState<String> mutableState, MutableState<String> mutableState2, MutableState<String> mutableState3, MutableState<String> mutableState4, MutableState<String> mutableState5, MutableState<String> mutableState6, MutableState<String> mutableState7, MutableState<String> mutableState8, MutableState<String> mutableState9, MutableState<String> mutableState10, MutableState<String> mutableState11, MutableState<String> mutableState12, MutableState<Boolean> mutableState13, MutableState<String> mutableState14, Continuation<? super MainActivityKt$AdminConfigSubscreen$1$1> continuation) {
        super(2, continuation);
        this.$viewModel = daliliViewModel;
        this.$appName$delegate = mutableState;
        this.$welcomeText$delegate = mutableState2;
        this.$welcomeImage$delegate = mutableState3;
        this.$appLogo$delegate = mutableState4;
        this.$supportPhone$delegate = mutableState5;
        this.$supportEmail$delegate = mutableState6;
        this.$supportWhatsapp$delegate = mutableState7;
        this.$footerText$delegate = mutableState8;
        this.$aboutAppSubtitle$delegate = mutableState9;
        this.$appUpdatesUrl$delegate = mutableState10;
        this.$appShareText$delegate = mutableState11;
        this.$assistantWelcomeText$delegate = mutableState12;
        this.$showFooter$delegate = mutableState13;
        this.$selectedTheme$delegate = mutableState14;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new MainActivityKt$AdminConfigSubscreen$1$1(this.$viewModel, this.$appName$delegate, this.$welcomeText$delegate, this.$welcomeImage$delegate, this.$appLogo$delegate, this.$supportPhone$delegate, this.$supportEmail$delegate, this.$supportWhatsapp$delegate, this.$footerText$delegate, this.$aboutAppSubtitle$delegate, this.$appUpdatesUrl$delegate, this.$appShareText$delegate, this.$assistantWelcomeText$delegate, this.$showFooter$delegate, this.$selectedTheme$delegate, continuation);
    }

    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return create(coroutineScope, continuation).invokeSuspend(Unit.INSTANCE);
    }

    public final Object invokeSuspend(Object obj) {
        IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                this.$appName$delegate.setValue((String) this.$viewModel.getAppName().getValue());
                this.$welcomeText$delegate.setValue((String) this.$viewModel.getWelcomeText().getValue());
                MutableState<String> mutableState = this.$welcomeImage$delegate;
                String str = (String) this.$viewModel.getWelcomeImage().getValue();
                if (str == null) {
                    str = "";
                }
                mutableState.setValue(str);
                MutableState<String> mutableState2 = this.$appLogo$delegate;
                String str2 = (String) this.$viewModel.getAppLogo().getValue();
                mutableState2.setValue(str2 != null ? str2 : "");
                this.$supportPhone$delegate.setValue((String) this.$viewModel.getSupportPhone().getValue());
                this.$supportEmail$delegate.setValue((String) this.$viewModel.getSupportEmail().getValue());
                this.$supportWhatsapp$delegate.setValue((String) this.$viewModel.getSupportWhatsapp().getValue());
                this.$footerText$delegate.setValue((String) this.$viewModel.getFooterText().getValue());
                this.$aboutAppSubtitle$delegate.setValue((String) this.$viewModel.getAboutAppSubtitle().getValue());
                this.$appUpdatesUrl$delegate.setValue((String) this.$viewModel.getAppUpdatesUrl().getValue());
                this.$appShareText$delegate.setValue((String) this.$viewModel.getAppShareText().getValue());
                this.$assistantWelcomeText$delegate.setValue((String) this.$viewModel.getAssistantWelcomeText().getValue());
                MainActivityKt.AdminConfigSubscreen$lambda$147(this.$showFooter$delegate, ((Boolean) this.$viewModel.getShowFooter().getValue()).booleanValue());
                this.$selectedTheme$delegate.setValue((String) this.$viewModel.getCurrentTheme().getValue());
                return Unit.INSTANCE;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}
