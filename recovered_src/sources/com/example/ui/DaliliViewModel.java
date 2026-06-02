package com.example.ui;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import com.example.data.Admin;
import com.example.data.Category;
import com.example.data.PendingProvider;
import com.example.data.Review;
import com.example.data.ServiceProvider;
import com.example.data.SubCategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.yemende.BuildConfig;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

/* compiled from: DaliliViewModel.kt */
@Metadata(d1 = {"\u0000\u0096\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u0002\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0016\n\u0002\u0010\u0006\n\u0002\b\u0015\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010`\u001a\u00020aH\u0002J\b\u0010b\u001a\u00020aH\u0002J\u0016\u0010c\u001a\u00020E2\u0006\u0010d\u001a\u00020)2\u0006\u0010e\u001a\u00020)J\u0006\u0010f\u001a\u00020aJ\u000e\u0010g\u001a\u00020a2\u0006\u0010h\u001a\u00020)J\u000e\u0010i\u001a\u00020)2\u0006\u0010j\u001a\u00020)J\u008e\u0001\u0010k\u001a\u00020a2\u0006\u0010l\u001a\u00020)2\u0006\u00100\u001a\u00020)2\u0006\u0010m\u001a\u00020)2\b\u0010n\u001a\u0004\u0018\u00010)2\b\u0010o\u001a\u0004\u0018\u00010)2\u0006\u0010p\u001a\u00020)2\u0006\u0010q\u001a\u00020)2\u0006\u0010r\u001a\u00020)2\u0006\u0010s\u001a\u00020)2\u0006\u0010t\u001a\u00020E2\u0006\u0010u\u001a\u00020)2\u0006\u0010v\u001a\u00020)2\u0006\u0010w\u001a\u00020)2\u0006\u0010X\u001a\u00020)2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJ2\u0010z\u001a\u00020a2\u0006\u0010{\u001a\u00020)2\u0006\u0010|\u001a\u00020)2\u0006\u0010}\u001a\u00020~2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJ#\u0010\u007f\u001a\u00020a2\u0007\u0010\u0080\u0001\u001a\u00020\u000f2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJ$\u0010\u0081\u0001\u001a\u00020a2\u0007\u0010\u0082\u0001\u001a\u00020~2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJy\u0010\u0083\u0001\u001a\u00020a2\u0007\u0010\u0084\u0001\u001a\u00020)2\u0006\u0010p\u001a\u00020)2\u0007\u0010\u0085\u0001\u001a\u00020~2\t\u0010\u0086\u0001\u001a\u0004\u0018\u00010~2\t\u0010\u0087\u0001\u001a\u0004\u0018\u00010)2\u0007\u0010\u0088\u0001\u001a\u00020E2\u0007\u0010\u0089\u0001\u001a\u00020E2\t\u0010\u008a\u0001\u001a\u0004\u0018\u00010)2\t\u0010\u008b\u0001\u001a\u0004\u0018\u00010)2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0y¢\u0006\u0003\u0010\u008c\u0001J$\u0010\u008d\u0001\u001a\u00020a2\u0007\u0010\u008e\u0001\u001a\u00020\u00192\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJ$\u0010\u008f\u0001\u001a\u00020a2\u0007\u0010\u0082\u0001\u001a\u00020~2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJ@\u0010\u0090\u0001\u001a\u00020a2\u0007\u0010\u0091\u0001\u001a\u00020~2\u0007\u0010\u0092\u0001\u001a\u00020)2\u0007\u0010\u0093\u0001\u001a\u00020)2\b\u0010\u0094\u0001\u001a\u00030\u0095\u00012\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJ\u0012\u0010\u0096\u0001\u001a\u00020a2\u0007\u0010\u0091\u0001\u001a\u00020~H\u0002J\\\u0010\u0097\u0001\u001a\u00020a2\u0007\u0010\u0084\u0001\u001a\u00020)2\u0006\u0010p\u001a\u00020)2\u0007\u0010\u0085\u0001\u001a\u00020~2\t\u0010\u0086\u0001\u001a\u0004\u0018\u00010~2\t\u0010\u0087\u0001\u001a\u0004\u0018\u00010)2\t\u0010\u0098\u0001\u001a\u0004\u0018\u00010)2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0y¢\u0006\u0003\u0010\u0099\u0001J$\u0010\u009a\u0001\u001a\u00020a2\u0007\u0010\u009b\u0001\u001a\u00020\u001d2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJ$\u0010\u009c\u0001\u001a\u00020a2\u0007\u0010\u009b\u0001\u001a\u00020\u001d2\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020E\u0012\u0004\u0012\u00020a0yJ\u0019\u0010\u009d\u0001\u001a\u00020a2\u0007\u0010\u009e\u0001\u001a\u00020)2\u0007\u0010\u009f\u0001\u001a\u00020EJ\u0007\u0010 \u0001\u001a\u00020aJ\u0010\u0010¡\u0001\u001a\u00020a2\u0007\u0010¢\u0001\u001a\u00020)J\u0019\u0010£\u0001\u001a\u00020)2\u0007\u0010¢\u0001\u001a\u00020)H\u0086@¢\u0006\u0003\u0010¤\u0001J\u0010\u0010¥\u0001\u001a\u00020)2\u0007\u0010¢\u0001\u001a\u00020)J\u000f\u0010¦\u0001\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0002J\u000f\u0010§\u0001\u001a\b\u0012\u0004\u0012\u00020\u00150\u000eH\u0002J\u000f\u0010¨\u0001\u001a\b\u0012\u0004\u0012\u00020\u00190\u000eH\u0002J\u000f\u0010©\u0001\u001a\b\u0012\u0004\u0012\u00020!0\u000eH\u0002R\u0018\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u000e0\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0013R\u001a\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u000e0\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0013R\u001a\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\u001e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u000e0\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0013R\u001a\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020!0\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020!0\u000e0\u0011¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0013R\u001a\u0010$\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020%0\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020%0\u000e0\u0011¢\u0006\b\n\u0000\u001a\u0004\b'\u0010\u0013R\u0014\u0010(\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010*\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0013R\u0016\u0010,\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010-\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b.\u0010\u0013R\u0014\u0010/\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u00100\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b1\u0010\u0013R\u0014\u00102\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u00103\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b4\u0010\u0013R\u0016\u00105\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u00106\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b7\u0010\u0013R\u0014\u00108\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u00109\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b:\u0010\u0013R\u0014\u0010;\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010<\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b=\u0010\u0013R\u0014\u0010>\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010?\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b@\u0010\u0013R\u0014\u0010A\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010B\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\bC\u0010\u0013R\u0014\u0010D\u001a\b\u0012\u0004\u0012\u00020E0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010F\u001a\b\u0012\u0004\u0012\u00020E0\u0011¢\u0006\b\n\u0000\u001a\u0004\bG\u0010\u0013R\u0014\u0010H\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010I\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\bJ\u0010\u0013R\u0014\u0010K\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010L\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\bM\u0010\u0013R\u0014\u0010N\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010O\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\bP\u0010\u0013R&\u0010Q\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020)\u0012\u0004\u0012\u00020E0R0\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000R)\u0010S\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020)\u0012\u0004\u0012\u00020E0R0\u000e0\u0011¢\u0006\b\n\u0000\u001a\u0004\bT\u0010\u0013R\u0014\u0010U\u001a\b\u0012\u0004\u0012\u00020E0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010V\u001a\b\u0012\u0004\u0012\u00020E0\u0011¢\u0006\b\n\u0000\u001a\u0004\bV\u0010\u0013R\u0014\u0010W\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010X\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\bY\u0010\u0013R\u0016\u0010Z\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010%0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010[\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010%0\u0011¢\u0006\b\n\u0000\u001a\u0004\b\\\u0010\u0013R\u0014\u0010]\u001a\b\u0012\u0004\u0012\u00020)0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010^\u001a\b\u0012\u0004\u0012\u00020)0\u0011¢\u0006\b\n\u0000\u001a\u0004\b_\u0010\u0013¨\u0006ª\u0001"}, d2 = {"Lcom/example/ui/DaliliViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "context", "Landroid/content/Context;", "kotlin.jvm.PlatformType", "Landroid/content/Context;", "db", "Lcom/google/firebase/firestore/FirebaseFirestore;", "_categories", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/example/data/Category;", "categories", "Lkotlinx/coroutines/flow/StateFlow;", "getCategories", "()Lkotlinx/coroutines/flow/StateFlow;", "_subCategories", "Lcom/example/data/SubCategory;", "subCategories", "getSubCategories", "_serviceProviders", "Lcom/example/data/ServiceProvider;", "serviceProviders", "getServiceProviders", "_pendingProviders", "Lcom/example/data/PendingProvider;", "pendingProviders", "getPendingProviders", "_reviews", "Lcom/example/data/Review;", "reviews", "getReviews", "_admins", "Lcom/example/data/Admin;", "admins", "getAdmins", "_currentTheme", "", "currentTheme", "getCurrentTheme", "_welcomeImage", "welcomeImage", "getWelcomeImage", "_appName", "appName", "getAppName", "_welcomeText", "welcomeText", "getWelcomeText", "_appLogo", "appLogo", "getAppLogo", "_supportPhone", "supportPhone", "getSupportPhone", "_supportEmail", "supportEmail", "getSupportEmail", "_supportWhatsapp", "supportWhatsapp", "getSupportWhatsapp", "_footerText", "footerText", "getFooterText", "_showFooter", "", "showFooter", "getShowFooter", "_aboutAppSubtitle", "aboutAppSubtitle", "getAboutAppSubtitle", "_appUpdatesUrl", "appUpdatesUrl", "getAppUpdatesUrl", "_appShareText", "appShareText", "getAppShareText", "_chatHistory", "Lkotlin/Pair;", "chatHistory", "getChatHistory", "_isAssistantLoading", "isAssistantLoading", "_assistantWelcomeText", "assistantWelcomeText", "getAssistantWelcomeText", "_currentUser", "currentUser", "getCurrentUser", "_searchQuery", "searchQuery", "getSearchQuery", "setupRealtimeSync", "", "seedInitialDatabase", "login", "username", "pwhash", "logout", "setSearchQuery", "query", "hashPasswordHelper", "password", "updateAppConfig", "themeChoice", "welcomeMsg", "welcomeImg", "appLogoUrl", "phone", "email", "whatsapp", "footer", "showF", "aboutSubtitle", "updatesUrl", "shareText", "onComplete", "Lkotlin/Function1;", "addCategory", "nameAr", "icon", "orderIndex", "", "updateCategory", "category", "deleteCategory", "id", "addServiceProvider", "name", "categoryId", "subCategoryId", "imageUrl", "isPinned", "isRecommended", "priceCategory", "distanceCategory", "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/Integer;Ljava/lang/String;ZZLjava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "updateServiceProvider", "provider", "deleteServiceProvider", "addReview", "providerId", "userName", "comment", "rating", "", "updateProviderRatingAsync", "addPendingProvider", "region", "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "approvePendingProvider", "pending", "rejectPendingProvider", "addChatMessage", "message", "isUser", "clearChatHistory", "askAssistant", "question", "callGeminiApiDirect", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getOfflineAnswer", "getDefaultCategories", "getDefaultSubCategories", "getDefaultProviders", "getDefaultReviews", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public final class DaliliViewModel extends AndroidViewModel {
    public static final int $stable = 8;
    private final MutableStateFlow<String> _aboutAppSubtitle;
    private final MutableStateFlow<List<Admin>> _admins;
    private final MutableStateFlow<String> _appLogo;
    private final MutableStateFlow<String> _appName;
    private final MutableStateFlow<String> _appShareText;
    private final MutableStateFlow<String> _appUpdatesUrl;
    private final MutableStateFlow<String> _assistantWelcomeText;
    private final MutableStateFlow<List<Category>> _categories;
    private final MutableStateFlow<List<Pair<String, Boolean>>> _chatHistory;
    private final MutableStateFlow<String> _currentTheme;
    private final MutableStateFlow<Admin> _currentUser;
    private final MutableStateFlow<String> _footerText;
    private final MutableStateFlow<Boolean> _isAssistantLoading;
    private final MutableStateFlow<List<PendingProvider>> _pendingProviders;
    private final MutableStateFlow<List<Review>> _reviews;
    private final MutableStateFlow<String> _searchQuery;
    private final MutableStateFlow<List<ServiceProvider>> _serviceProviders;
    private final MutableStateFlow<Boolean> _showFooter;
    private final MutableStateFlow<List<SubCategory>> _subCategories;
    private final MutableStateFlow<String> _supportEmail;
    private final MutableStateFlow<String> _supportPhone;
    private final MutableStateFlow<String> _supportWhatsapp;
    private final MutableStateFlow<String> _welcomeImage;
    private final MutableStateFlow<String> _welcomeText;
    private final StateFlow<String> aboutAppSubtitle;
    private final StateFlow<List<Admin>> admins;
    private final StateFlow<String> appLogo;
    private final StateFlow<String> appName;
    private final StateFlow<String> appShareText;
    private final StateFlow<String> appUpdatesUrl;
    private final StateFlow<String> assistantWelcomeText;
    private final StateFlow<List<Category>> categories;
    private final StateFlow<List<Pair<String, Boolean>>> chatHistory;
    private final Context context;
    private final StateFlow<String> currentTheme;
    private final StateFlow<Admin> currentUser;
    private FirebaseFirestore db;
    private final StateFlow<String> footerText;
    private final StateFlow<Boolean> isAssistantLoading;
    private final StateFlow<List<PendingProvider>> pendingProviders;
    private final StateFlow<List<Review>> reviews;
    private final StateFlow<String> searchQuery;
    private final StateFlow<List<ServiceProvider>> serviceProviders;
    private final StateFlow<Boolean> showFooter;
    private final StateFlow<List<SubCategory>> subCategories;
    private final StateFlow<String> supportEmail;
    private final StateFlow<String> supportPhone;
    private final StateFlow<String> supportWhatsapp;
    private final StateFlow<String> welcomeImage;
    private final StateFlow<String> welcomeText;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DaliliViewModel(Application application) {
        super(application);
        Intrinsics.checkNotNullParameter(application, "application");
        this.context = application.getApplicationContext();
        this._categories = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
        this.categories = FlowKt.asStateFlow(this._categories);
        this._subCategories = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
        this.subCategories = FlowKt.asStateFlow(this._subCategories);
        this._serviceProviders = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
        this.serviceProviders = FlowKt.asStateFlow(this._serviceProviders);
        this._pendingProviders = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
        this.pendingProviders = FlowKt.asStateFlow(this._pendingProviders);
        this._reviews = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
        this.reviews = FlowKt.asStateFlow(this._reviews);
        this._admins = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
        this.admins = FlowKt.asStateFlow(this._admins);
        this._currentTheme = StateFlowKt.MutableStateFlow("red_black");
        this.currentTheme = FlowKt.asStateFlow(this._currentTheme);
        this._welcomeImage = StateFlowKt.MutableStateFlow((Object) null);
        this.welcomeImage = FlowKt.asStateFlow(this._welcomeImage);
        this._appName = StateFlowKt.MutableStateFlow("دليلي - Dalili");
        this.appName = FlowKt.asStateFlow(this._appName);
        this._welcomeText = StateFlowKt.MutableStateFlow("دليلي - دليلك الشامل لجميع الخدمات والأجهزة الطبية والصيانة في اليمن!");
        this.welcomeText = FlowKt.asStateFlow(this._welcomeText);
        this._appLogo = StateFlowKt.MutableStateFlow((Object) null);
        this.appLogo = FlowKt.asStateFlow(this._appLogo);
        this._supportPhone = StateFlowKt.MutableStateFlow("777644670");
        this.supportPhone = FlowKt.asStateFlow(this._supportPhone);
        this._supportEmail = StateFlowKt.MutableStateFlow("support@dalili.ye");
        this.supportEmail = FlowKt.asStateFlow(this._supportEmail);
        this._supportWhatsapp = StateFlowKt.MutableStateFlow("777644670");
        this.supportWhatsapp = FlowKt.asStateFlow(this._supportWhatsapp);
        this._footerText = StateFlowKt.MutableStateFlow("جميع الحقوق محفوظة © تطبيق دليلي 2026");
        this.footerText = FlowKt.asStateFlow(this._footerText);
        this._showFooter = StateFlowKt.MutableStateFlow(true);
        this.showFooter = FlowKt.asStateFlow(this._showFooter);
        this._aboutAppSubtitle = StateFlowKt.MutableStateFlow("دليلي هو منصة الكترونية شاملة ومجانية تهدف لتسهيل الوصول لمزودي الخدمات الهندسية، الطبية والاتصالات في جميع مناطق الجمهورية.");
        this.aboutAppSubtitle = FlowKt.asStateFlow(this._aboutAppSubtitle);
        this._appUpdatesUrl = StateFlowKt.MutableStateFlow("https://dalili.ye/updates");
        this.appUpdatesUrl = FlowKt.asStateFlow(this._appUpdatesUrl);
        this._appShareText = StateFlowKt.MutableStateFlow("حمل الآن تطبيق دليلي للأجهزة والخدمات، دليلك في جيبك!");
        this.appShareText = FlowKt.asStateFlow(this._appShareText);
        this._chatHistory = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
        this.chatHistory = FlowKt.asStateFlow(this._chatHistory);
        this._isAssistantLoading = StateFlowKt.MutableStateFlow(false);
        this.isAssistantLoading = FlowKt.asStateFlow(this._isAssistantLoading);
        this._assistantWelcomeText = StateFlowKt.MutableStateFlow("مرحباً بك! أنا مساعدك الذكي في تطبيق دليلي. كيف يمكنني مساعدتك في العثور على مقدمي الخدمات اليوم؟");
        this.assistantWelcomeText = FlowKt.asStateFlow(this._assistantWelcomeText);
        this._currentUser = StateFlowKt.MutableStateFlow((Object) null);
        this.currentUser = FlowKt.asStateFlow(this._currentUser);
        this._searchQuery = StateFlowKt.MutableStateFlow("");
        this.searchQuery = FlowKt.asStateFlow(this._searchQuery);
        try {
            if (FirebaseApp.getApps(this.context).isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder().setApiKey("AIzaSyBoFpZzhWBpwhYwnlfcPehoUp5HfU4DTGc").setApplicationId("1:10499647772:android:2e17b3c6b0c7bdae9e32d9").setProjectId("yemen-da").setStorageBucket("yemen-da.firebasestorage.app").build();
                Intrinsics.checkNotNullExpressionValue(options, "build(...)");
                FirebaseApp.initializeApp(this.context, options);
            }
        } catch (Exception e) {
            Log.e("Firebase", "Failed to initialize Firebase: " + e.getMessage());
        }
        this.db = FirebaseFirestore.getInstance();
        setupRealtimeSync();
    }

    public final StateFlow<List<Category>> getCategories() {
        return this.categories;
    }

    public final StateFlow<List<SubCategory>> getSubCategories() {
        return this.subCategories;
    }

    public final StateFlow<List<ServiceProvider>> getServiceProviders() {
        return this.serviceProviders;
    }

    public final StateFlow<List<PendingProvider>> getPendingProviders() {
        return this.pendingProviders;
    }

    public final StateFlow<List<Review>> getReviews() {
        return this.reviews;
    }

    public final StateFlow<List<Admin>> getAdmins() {
        return this.admins;
    }

    public final StateFlow<String> getCurrentTheme() {
        return this.currentTheme;
    }

    public final StateFlow<String> getWelcomeImage() {
        return this.welcomeImage;
    }

    public final StateFlow<String> getAppName() {
        return this.appName;
    }

    public final StateFlow<String> getWelcomeText() {
        return this.welcomeText;
    }

    public final StateFlow<String> getAppLogo() {
        return this.appLogo;
    }

    public final StateFlow<String> getSupportPhone() {
        return this.supportPhone;
    }

    public final StateFlow<String> getSupportEmail() {
        return this.supportEmail;
    }

    public final StateFlow<String> getSupportWhatsapp() {
        return this.supportWhatsapp;
    }

    public final StateFlow<String> getFooterText() {
        return this.footerText;
    }

    public final StateFlow<Boolean> getShowFooter() {
        return this.showFooter;
    }

    public final StateFlow<String> getAboutAppSubtitle() {
        return this.aboutAppSubtitle;
    }

    public final StateFlow<String> getAppUpdatesUrl() {
        return this.appUpdatesUrl;
    }

    public final StateFlow<String> getAppShareText() {
        return this.appShareText;
    }

    public final StateFlow<List<Pair<String, Boolean>>> getChatHistory() {
        return this.chatHistory;
    }

    public final StateFlow<Boolean> isAssistantLoading() {
        return this.isAssistantLoading;
    }

    public final StateFlow<String> getAssistantWelcomeText() {
        return this.assistantWelcomeText;
    }

    public final StateFlow<Admin> getCurrentUser() {
        return this.currentUser;
    }

    public final StateFlow<String> getSearchQuery() {
        return this.searchQuery;
    }

    private final void setupRealtimeSync() {
        this.db.collection("categories").addSnapshotListener(new EventListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda14
            public final void onEvent(Object obj, FirebaseFirestoreException firebaseFirestoreException) {
                DaliliViewModel.setupRealtimeSync$lambda$3(DaliliViewModel.this, (QuerySnapshot) obj, firebaseFirestoreException);
            }
        });
        this.db.collection("sub_categories").addSnapshotListener(new EventListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda15
            public final void onEvent(Object obj, FirebaseFirestoreException firebaseFirestoreException) {
                DaliliViewModel.setupRealtimeSync$lambda$7(DaliliViewModel.this, (QuerySnapshot) obj, firebaseFirestoreException);
            }
        });
        this.db.collection("service_providers").addSnapshotListener(new EventListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda16
            public final void onEvent(Object obj, FirebaseFirestoreException firebaseFirestoreException) {
                DaliliViewModel.setupRealtimeSync$lambda$10(DaliliViewModel.this, (QuerySnapshot) obj, firebaseFirestoreException);
            }
        });
        this.db.collection("pending_providers").addSnapshotListener(new EventListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda17
            public final void onEvent(Object obj, FirebaseFirestoreException firebaseFirestoreException) {
                DaliliViewModel.setupRealtimeSync$lambda$12(DaliliViewModel.this, (QuerySnapshot) obj, firebaseFirestoreException);
            }
        });
        this.db.collection("reviews").addSnapshotListener(new EventListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda18
            public final void onEvent(Object obj, FirebaseFirestoreException firebaseFirestoreException) {
                DaliliViewModel.setupRealtimeSync$lambda$15(DaliliViewModel.this, (QuerySnapshot) obj, firebaseFirestoreException);
            }
        });
        this.db.collection("admins").addSnapshotListener(new EventListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda19
            public final void onEvent(Object obj, FirebaseFirestoreException firebaseFirestoreException) {
                DaliliViewModel.setupRealtimeSync$lambda$17(DaliliViewModel.this, (QuerySnapshot) obj, firebaseFirestoreException);
            }
        });
        this.db.collection("app_config").document("global").addSnapshotListener(new EventListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda20
            public final void onEvent(Object obj, FirebaseFirestoreException firebaseFirestoreException) {
                DaliliViewModel.setupRealtimeSync$lambda$18(DaliliViewModel.this, (DocumentSnapshot) obj, firebaseFirestoreException);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00ba  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00be A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void setupRealtimeSync$lambda$3(com.example.ui.DaliliViewModel r24, com.google.firebase.firestore.QuerySnapshot r25, com.google.firebase.firestore.FirebaseFirestoreException r26) {
        /*
            Method dump skipped, instructions count: 259
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.DaliliViewModel.setupRealtimeSync$lambda$3(com.example.ui.DaliliViewModel, com.google.firebase.firestore.QuerySnapshot, com.google.firebase.firestore.FirebaseFirestoreException):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0095  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00a2 A[Catch: Exception -> 0x00b8, TryCatch #3 {Exception -> 0x00b8, blocks: (B:21:0x005d, B:23:0x0076, B:26:0x0080, B:29:0x008d, B:32:0x009a, B:34:0x00a2, B:35:0x00ac, B:47:0x0065, B:49:0x006e), top: B:20:0x005d }] */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00c7  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00cb A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00aa  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0098  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void setupRealtimeSync$lambda$7(com.example.ui.DaliliViewModel r25, com.google.firebase.firestore.QuerySnapshot r26, com.google.firebase.firestore.FirebaseFirestoreException r27) {
        /*
            Method dump skipped, instructions count: 267
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.DaliliViewModel.setupRealtimeSync$lambda$7(com.example.ui.DaliliViewModel, com.google.firebase.firestore.QuerySnapshot, com.google.firebase.firestore.FirebaseFirestoreException):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:28:0x009b A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00be A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00cf A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x013e A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x014e A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x016c  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0171 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x011f A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0105 A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00eb A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x00c3  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x00a7 A[Catch: Exception -> 0x004a, TryCatch #0 {Exception -> 0x004a, blocks: (B:14:0x003e, B:15:0x0053, B:19:0x0062, B:22:0x006f, B:24:0x0078, B:25:0x007e, B:26:0x0093, B:28:0x009b, B:29:0x009f, B:30:0x00b6, B:32:0x00be, B:33:0x00c5, B:35:0x00cf, B:38:0x00dc, B:40:0x00e4, B:42:0x00f6, B:44:0x00fe, B:45:0x0110, B:47:0x0118, B:48:0x012a, B:50:0x013e, B:51:0x0144, B:53:0x014e, B:54:0x0154, B:61:0x011f, B:65:0x0105, B:69:0x00eb, B:75:0x00a7, B:77:0x00af, B:79:0x0082, B:81:0x008c), top: B:13:0x003e }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void setupRealtimeSync$lambda$10(com.example.ui.DaliliViewModel r35, com.google.firebase.firestore.QuerySnapshot r36, com.google.firebase.firestore.FirebaseFirestoreException r37) {
        /*
            Method dump skipped, instructions count: 415
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.DaliliViewModel.setupRealtimeSync$lambda$10(com.example.ui.DaliliViewModel, com.google.firebase.firestore.QuerySnapshot, com.google.firebase.firestore.FirebaseFirestoreException):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0075  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00a0 A[Catch: Exception -> 0x00d1, TryCatch #2 {Exception -> 0x00d1, blocks: (B:24:0x0079, B:25:0x007d, B:26:0x0098, B:28:0x00a0, B:31:0x00ad, B:34:0x00b7, B:45:0x0085, B:47:0x0091), top: B:23:0x0079 }] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00b5  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00dc  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00e1 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0085 A[Catch: Exception -> 0x00d1, TryCatch #2 {Exception -> 0x00d1, blocks: (B:24:0x0079, B:25:0x007d, B:26:0x0098, B:28:0x00a0, B:31:0x00ad, B:34:0x00b7, B:45:0x0085, B:47:0x0091), top: B:23:0x0079 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void setupRealtimeSync$lambda$12(com.example.ui.DaliliViewModel r27, com.google.firebase.firestore.QuerySnapshot r28, com.google.firebase.firestore.FirebaseFirestoreException r29) {
        /*
            Method dump skipped, instructions count: 252
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.DaliliViewModel.setupRealtimeSync$lambda$12(com.example.ui.DaliliViewModel, com.google.firebase.firestore.QuerySnapshot, com.google.firebase.firestore.FirebaseFirestoreException):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0097  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00a4 A[Catch: Exception -> 0x00ba, TryCatch #2 {Exception -> 0x00ba, blocks: (B:22:0x005f, B:23:0x0063, B:24:0x0078, B:27:0x0082, B:30:0x008f, B:33:0x009c, B:35:0x00a4, B:36:0x00ab, B:48:0x0067, B:50:0x0071), top: B:21:0x005f }] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00cc  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00d0 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00a9  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x009a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void setupRealtimeSync$lambda$15(com.example.ui.DaliliViewModel r26, com.google.firebase.firestore.QuerySnapshot r27, com.google.firebase.firestore.FirebaseFirestoreException r28) {
        /*
            Method dump skipped, instructions count: 258
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.DaliliViewModel.setupRealtimeSync$lambda$15(com.example.ui.DaliliViewModel, com.google.firebase.firestore.QuerySnapshot, com.google.firebase.firestore.FirebaseFirestoreException):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0098 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void setupRealtimeSync$lambda$17(com.example.ui.DaliliViewModel r23, com.google.firebase.firestore.QuerySnapshot r24, com.google.firebase.firestore.FirebaseFirestoreException r25) {
        /*
            Method dump skipped, instructions count: 225
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.DaliliViewModel.setupRealtimeSync$lambda$17(com.example.ui.DaliliViewModel, com.google.firebase.firestore.QuerySnapshot, com.google.firebase.firestore.FirebaseFirestoreException):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setupRealtimeSync$lambda$18(DaliliViewModel this$0, DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (snapshot != null && snapshot.exists()) {
            MutableStateFlow<String> mutableStateFlow = this$0._currentTheme;
            String string = snapshot.getString("theme_choice");
            if (string == null) {
                string = "red_black";
            }
            mutableStateFlow.setValue(string);
            this$0._welcomeImage.setValue(snapshot.getString("welcome_image"));
            MutableStateFlow<String> mutableStateFlow2 = this$0._appName;
            String string2 = snapshot.getString("custom_app_name");
            if (string2 == null) {
                string2 = "دليلي - Dalili";
            }
            mutableStateFlow2.setValue(string2);
            MutableStateFlow<String> mutableStateFlow3 = this$0._welcomeText;
            String string3 = snapshot.getString("welcome_text");
            if (string3 == null) {
                string3 = "دليلي - دليلك الشامل لجميع الخدمات والأجهزة الطبية والصيانة في اليمن!";
            }
            mutableStateFlow3.setValue(string3);
            this$0._appLogo.setValue(snapshot.getString("app_logo"));
            MutableStateFlow<String> mutableStateFlow4 = this$0._supportPhone;
            String string4 = snapshot.getString("support_phone");
            if (string4 == null) {
                string4 = "777644670";
            }
            mutableStateFlow4.setValue(string4);
            MutableStateFlow<String> mutableStateFlow5 = this$0._supportEmail;
            String string5 = snapshot.getString("support_email");
            if (string5 == null) {
                string5 = "support@dalili.ye";
            }
            mutableStateFlow5.setValue(string5);
            MutableStateFlow<String> mutableStateFlow6 = this$0._supportWhatsapp;
            String string6 = snapshot.getString("support_whatsapp");
            mutableStateFlow6.setValue(string6 != null ? string6 : "777644670");
            MutableStateFlow<String> mutableStateFlow7 = this$0._footerText;
            String string7 = snapshot.getString("footer_text");
            if (string7 == null) {
                string7 = "جميع الحقوق محفوظة © تطبيق دليلي 2026";
            }
            mutableStateFlow7.setValue(string7);
            MutableStateFlow<Boolean> mutableStateFlow8 = this$0._showFooter;
            Boolean bool = snapshot.getBoolean("show_footer");
            mutableStateFlow8.setValue(Boolean.valueOf(bool != null ? bool.booleanValue() : true));
            MutableStateFlow<String> mutableStateFlow9 = this$0._aboutAppSubtitle;
            String string8 = snapshot.getString("about_app_subtitle");
            if (string8 == null) {
                string8 = (String) this$0._aboutAppSubtitle.getValue();
            }
            mutableStateFlow9.setValue(string8);
            MutableStateFlow<String> mutableStateFlow10 = this$0._appUpdatesUrl;
            String string9 = snapshot.getString("app_updates_url");
            if (string9 == null) {
                string9 = "https://dalili.ye/updates";
            }
            mutableStateFlow10.setValue(string9);
            MutableStateFlow<String> mutableStateFlow11 = this$0._appShareText;
            String string10 = snapshot.getString("app_share_text");
            if (string10 == null) {
                string10 = "حمل الآن تطبيق دليلي للأجهزة والخدمات، دليلك في جيبك!";
            }
            mutableStateFlow11.setValue(string10);
            MutableStateFlow<String> mutableStateFlow12 = this$0._assistantWelcomeText;
            String string11 = snapshot.getString("assistant_welcome_text");
            if (string11 == null) {
                string11 = "مرحباً بك! أنا مساعدك الذكي في تطبيق دليلي. كيف يمكنني مساعدتك في العثور على مقدمي الخدمات اليوم؟";
            }
            mutableStateFlow12.setValue(string11);
        }
    }

    private final void seedInitialDatabase() {
        BuildersKt.launch$default(ViewModelKt.getViewModelScope((ViewModel) this), (CoroutineContext) null, (CoroutineStart) null, new DaliliViewModel$seedInitialDatabase$1(this, null), 3, (Object) null);
    }

    public final boolean login(String username, String pwhash) {
        Object obj;
        Intrinsics.checkNotNullParameter(username, "username");
        Intrinsics.checkNotNullParameter(pwhash, "pwhash");
        String hashValue = hashPasswordHelper(pwhash);
        Iterator it = ((Iterable) this.admins.getValue()).iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            Admin it2 = (Admin) obj;
            if (((StringsKt.equals(it2.getUsername(), username, true) && Intrinsics.areEqual(it2.getPasswordHash(), hashValue)) ? 1 : null) != null) {
                break;
            }
        }
        Admin admin = (Admin) obj;
        if (admin == null) {
            return false;
        }
        this._currentUser.setValue(admin);
        return true;
    }

    public final void logout() {
        this._currentUser.setValue((Object) null);
    }

    public final void setSearchQuery(String query) {
        Intrinsics.checkNotNullParameter(query, "query");
        this._searchQuery.setValue(query);
    }

    public final String hashPasswordHelper(String password) {
        Intrinsics.checkNotNullParameter(password, "password");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = password.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] hash = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                String format = String.format("%02x", Arrays.copyOf(new Object[]{Byte.valueOf(b)}, 1));
                Intrinsics.checkNotNullExpressionValue(format, "format(...)");
                sb.append(format);
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    public final void updateAppConfig(String themeChoice, String appName, String welcomeMsg, String welcomeImg, String appLogoUrl, String phone, String email, String whatsapp, String footer, boolean showF, String aboutSubtitle, String updatesUrl, String shareText, String assistantWelcomeText, final Function1<? super Boolean, Unit> onComplete) {
        Intrinsics.checkNotNullParameter(themeChoice, "themeChoice");
        Intrinsics.checkNotNullParameter(appName, "appName");
        Intrinsics.checkNotNullParameter(welcomeMsg, "welcomeMsg");
        Intrinsics.checkNotNullParameter(phone, "phone");
        Intrinsics.checkNotNullParameter(email, "email");
        Intrinsics.checkNotNullParameter(whatsapp, "whatsapp");
        Intrinsics.checkNotNullParameter(footer, "footer");
        Intrinsics.checkNotNullParameter(aboutSubtitle, "aboutSubtitle");
        Intrinsics.checkNotNullParameter(updatesUrl, "updatesUrl");
        Intrinsics.checkNotNullParameter(shareText, "shareText");
        Intrinsics.checkNotNullParameter(assistantWelcomeText, "assistantWelcomeText");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        HashMap data = MapsKt.hashMapOf(new Pair[]{TuplesKt.to("theme_choice", themeChoice), TuplesKt.to("custom_app_name", appName), TuplesKt.to("welcome_text", welcomeMsg), TuplesKt.to("support_phone", phone), TuplesKt.to("support_email", email), TuplesKt.to("support_whatsapp", whatsapp), TuplesKt.to("footer_text", footer), TuplesKt.to("show_footer", Boolean.valueOf(showF)), TuplesKt.to("about_app_subtitle", aboutSubtitle), TuplesKt.to("app_updates_url", updatesUrl), TuplesKt.to("app_share_text", shareText), TuplesKt.to("assistant_welcome_text", assistantWelcomeText)});
        if (welcomeImg != null) {
            data.put("welcome_image", welcomeImg);
        }
        if (appLogoUrl != null) {
            data.put("app_logo", appLogoUrl);
        }
        this.db.collection("app_config").document("global").set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda13
            public final void onComplete(Task task) {
                DaliliViewModel.updateAppConfig$lambda$22(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void updateAppConfig$lambda$22(Function1 $onComplete, Task task) {
        Intrinsics.checkNotNullParameter(task, "task");
        $onComplete.invoke(Boolean.valueOf(task.isSuccessful()));
    }

    public final void addCategory(String nameAr, String icon, int orderIndex, final Function1<? super Boolean, Unit> onComplete) {
        Integer num;
        Intrinsics.checkNotNullParameter(nameAr, "nameAr");
        Intrinsics.checkNotNullParameter(icon, "icon");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        Iterator it = ((Iterable) this.categories.getValue()).iterator();
        if (it.hasNext()) {
            Category it2 = (Category) it.next();
            Integer id = it2.getId();
            Integer valueOf = Integer.valueOf(id != null ? id.intValue() : 0);
            while (it.hasNext()) {
                Category it3 = (Category) it.next();
                Integer id2 = it3.getId();
                Integer valueOf2 = Integer.valueOf(id2 != null ? id2.intValue() : 0);
                if (valueOf.compareTo(valueOf2) < 0) {
                    valueOf = valueOf2;
                }
            }
            num = valueOf;
        } else {
            num = null;
        }
        Integer num2 = num;
        int id3 = (num2 != null ? num2.intValue() : 0) + 1;
        Category item = new Category(Integer.valueOf(id3), nameAr, icon, orderIndex, new Date().toString());
        this.db.collection("categories").document(String.valueOf(id3)).set(item).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda0
            public final void onComplete(Task task) {
                DaliliViewModel.addCategory$lambda$24(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addCategory$lambda$24(Function1 $onComplete, Task it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $onComplete.invoke(Boolean.valueOf(it.isSuccessful()));
    }

    public final void updateCategory(Category category, final Function1<? super Boolean, Unit> onComplete) {
        Intrinsics.checkNotNullParameter(category, "category");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        this.db.collection("categories").document(String.valueOf(category.getId())).set(category).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda9
            public final void onComplete(Task task) {
                DaliliViewModel.updateCategory$lambda$25(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void updateCategory$lambda$25(Function1 $onComplete, Task it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $onComplete.invoke(Boolean.valueOf(it.isSuccessful()));
    }

    public final void deleteCategory(int id, final Function1<? super Boolean, Unit> onComplete) {
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        this.db.collection("categories").document(String.valueOf(id)).delete().addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda7
            public final void onComplete(Task task) {
                DaliliViewModel.deleteCategory$lambda$26(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void deleteCategory$lambda$26(Function1 $onComplete, Task it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $onComplete.invoke(Boolean.valueOf(it.isSuccessful()));
    }

    public final void addServiceProvider(String name, String phone, int categoryId, Integer subCategoryId, String imageUrl, boolean isPinned, boolean isRecommended, String priceCategory, String distanceCategory, final Function1<? super Boolean, Unit> onComplete) {
        Integer num;
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(phone, "phone");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        Iterator it = ((Iterable) this.serviceProviders.getValue()).iterator();
        if (it.hasNext()) {
            ServiceProvider it2 = (ServiceProvider) it.next();
            Integer id = it2.getId();
            Integer valueOf = Integer.valueOf(id != null ? id.intValue() : 0);
            while (it.hasNext()) {
                ServiceProvider it3 = (ServiceProvider) it.next();
                Integer id2 = it3.getId();
                Integer valueOf2 = Integer.valueOf(id2 != null ? id2.intValue() : 0);
                if (valueOf.compareTo(valueOf2) < 0) {
                    valueOf = valueOf2;
                }
            }
            num = valueOf;
        } else {
            num = null;
        }
        Integer num2 = num;
        int id3 = (num2 != null ? num2.intValue() : 0) + 1;
        ServiceProvider item = new ServiceProvider(Integer.valueOf(id3), name, phone, categoryId, subCategoryId, 0.0d, imageUrl == null ? "" : imageUrl, true, isPinned, isRecommended, null, null, priceCategory == null ? "medium" : priceCategory, distanceCategory == null ? "near" : distanceCategory, new Date().toString(), 3104, null);
        this.db.collection("service_providers").document(String.valueOf(id3)).set(item).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda11
            public final void onComplete(Task task) {
                DaliliViewModel.addServiceProvider$lambda$28(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addServiceProvider$lambda$28(Function1 $onComplete, Task it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $onComplete.invoke(Boolean.valueOf(it.isSuccessful()));
    }

    public final void updateServiceProvider(ServiceProvider provider, final Function1<? super Boolean, Unit> onComplete) {
        Intrinsics.checkNotNullParameter(provider, "provider");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        this.db.collection("service_providers").document(String.valueOf(provider.getId())).set(provider).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda10
            public final void onComplete(Task task) {
                DaliliViewModel.updateServiceProvider$lambda$29(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void updateServiceProvider$lambda$29(Function1 $onComplete, Task it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $onComplete.invoke(Boolean.valueOf(it.isSuccessful()));
    }

    public final void deleteServiceProvider(int id, final Function1<? super Boolean, Unit> onComplete) {
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        this.db.collection("service_providers").document(String.valueOf(id)).delete().addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda5
            public final void onComplete(Task task) {
                DaliliViewModel.deleteServiceProvider$lambda$30(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void deleteServiceProvider$lambda$30(Function1 $onComplete, Task it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $onComplete.invoke(Boolean.valueOf(it.isSuccessful()));
    }

    public final void addReview(final int providerId, String userName, String comment, double rating, final Function1<? super Boolean, Unit> onComplete) {
        Integer num;
        Intrinsics.checkNotNullParameter(userName, "userName");
        Intrinsics.checkNotNullParameter(comment, "comment");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        Iterator it = ((Iterable) this.reviews.getValue()).iterator();
        if (it.hasNext()) {
            Review it2 = (Review) it.next();
            Integer id = it2.getId();
            Integer valueOf = Integer.valueOf(id != null ? id.intValue() : 0);
            while (it.hasNext()) {
                Review it3 = (Review) it.next();
                Integer id2 = it3.getId();
                Integer valueOf2 = Integer.valueOf(id2 != null ? id2.intValue() : 0);
                if (valueOf.compareTo(valueOf2) < 0) {
                    valueOf = valueOf2;
                }
            }
            num = valueOf;
        } else {
            num = null;
        }
        Integer num2 = num;
        int id3 = (num2 != null ? num2.intValue() : 0) + 1;
        Review item = new Review(Integer.valueOf(id3), providerId, userName, comment, rating, new Date().toString());
        this.db.collection("reviews").document(String.valueOf(id3)).set(item).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda8
            public final void onComplete(Task task) {
                DaliliViewModel.addReview$lambda$32(DaliliViewModel.this, providerId, onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addReview$lambda$32(DaliliViewModel this$0, int $providerId, Function1 $onComplete, Task task) {
        Intrinsics.checkNotNullParameter(task, "task");
        if (task.isSuccessful()) {
            this$0.updateProviderRatingAsync($providerId);
        }
        $onComplete.invoke(Boolean.valueOf(task.isSuccessful()));
    }

    private final void updateProviderRatingAsync(int providerId) {
        BuildersKt.launch$default(ViewModelKt.getViewModelScope((ViewModel) this), (CoroutineContext) null, (CoroutineStart) null, new DaliliViewModel$updateProviderRatingAsync$1(this, providerId, null), 3, (Object) null);
    }

    public final void addPendingProvider(String name, String phone, int categoryId, Integer subCategoryId, String imageUrl, String region, final Function1<? super Boolean, Unit> onComplete) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(phone, "phone");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        String docId = this.db.collection("pending_providers").document().getId();
        Intrinsics.checkNotNullExpressionValue(docId, "getId(...)");
        PendingProvider item = new PendingProvider(docId, name, phone, categoryId, subCategoryId, imageUrl == null ? "" : imageUrl, "pending", region == null ? "" : region, new Date().toString());
        this.db.collection("pending_providers").document(docId).set(item).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda6
            public final void onComplete(Task task) {
                DaliliViewModel.addPendingProvider$lambda$33(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addPendingProvider$lambda$33(Function1 $onComplete, Task it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $onComplete.invoke(Boolean.valueOf(it.isSuccessful()));
    }

    public final void approvePendingProvider(final PendingProvider pending, final Function1<? super Boolean, Unit> onComplete) {
        Intrinsics.checkNotNullParameter(pending, "pending");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        CollectionReference collection = this.db.collection("pending_providers");
        String id = pending.getId();
        Intrinsics.checkNotNull(id);
        collection.document(id).update("status", "approved", new Object[0]).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda4
            public final void onComplete(Task task) {
                DaliliViewModel.approvePendingProvider$lambda$35(DaliliViewModel.this, pending, onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void approvePendingProvider$lambda$35(DaliliViewModel this$0, PendingProvider $pending, final Function1 $onComplete, Task task) {
        Intrinsics.checkNotNullParameter(task, "task");
        if (task.isSuccessful()) {
            this$0.addServiceProvider($pending.getName(), $pending.getPhone(), $pending.getCategoryId(), $pending.getSubCategoryId(), $pending.getImageUrl(), false, false, "medium", "near", new Function1() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda3
                public final Object invoke(Object obj) {
                    Unit approvePendingProvider$lambda$35$lambda$34;
                    approvePendingProvider$lambda$35$lambda$34 = DaliliViewModel.approvePendingProvider$lambda$35$lambda$34($onComplete, ((Boolean) obj).booleanValue());
                    return approvePendingProvider$lambda$35$lambda$34;
                }
            });
        } else {
            $onComplete.invoke(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit approvePendingProvider$lambda$35$lambda$34(Function1 $onComplete, boolean it) {
        $onComplete.invoke(Boolean.valueOf(it));
        return Unit.INSTANCE;
    }

    public final void rejectPendingProvider(PendingProvider pending, final Function1<? super Boolean, Unit> onComplete) {
        Intrinsics.checkNotNullParameter(pending, "pending");
        Intrinsics.checkNotNullParameter(onComplete, "onComplete");
        CollectionReference collection = this.db.collection("pending_providers");
        String id = pending.getId();
        Intrinsics.checkNotNull(id);
        collection.document(id).update("status", "rejected", new Object[0]).addOnCompleteListener(new OnCompleteListener() { // from class: com.example.ui.DaliliViewModel$$ExternalSyntheticLambda12
            public final void onComplete(Task task) {
                DaliliViewModel.rejectPendingProvider$lambda$36(onComplete, task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void rejectPendingProvider$lambda$36(Function1 $onComplete, Task it) {
        Intrinsics.checkNotNullParameter(it, "it");
        $onComplete.invoke(Boolean.valueOf(it.isSuccessful()));
    }

    public final void addChatMessage(String message, boolean isUser) {
        Intrinsics.checkNotNullParameter(message, "message");
        List current = CollectionsKt.toMutableList((Collection) this._chatHistory.getValue());
        current.add(new Pair(message, Boolean.valueOf(isUser)));
        this._chatHistory.setValue(current);
    }

    public final void clearChatHistory() {
        this._chatHistory.setValue(CollectionsKt.emptyList());
    }

    public final void askAssistant(String question) {
        Intrinsics.checkNotNullParameter(question, "question");
        addChatMessage(question, true);
        this._isAssistantLoading.setValue(true);
        BuildersKt.launch$default(ViewModelKt.getViewModelScope((ViewModel) this), (CoroutineContext) null, (CoroutineStart) null, new DaliliViewModel$askAssistant$1(this, question, null), 3, (Object) null);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x002c  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0031  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object callGeminiApiDirect(java.lang.String r8, kotlin.coroutines.Continuation<? super java.lang.String> r9) {
        /*
            r7 = this;
            boolean r0 = r9 instanceof com.example.ui.DaliliViewModel$callGeminiApiDirect$1
            if (r0 == 0) goto L14
            r0 = r9
            com.example.ui.DaliliViewModel$callGeminiApiDirect$1 r0 = (com.example.ui.DaliliViewModel$callGeminiApiDirect$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r1 = r0.label
            int r1 = r1 - r2
            r0.label = r1
            goto L19
        L14:
            com.example.ui.DaliliViewModel$callGeminiApiDirect$1 r0 = new com.example.ui.DaliliViewModel$callGeminiApiDirect$1
            r0.<init>(r7, r9)
        L19:
            java.lang.Object r1 = r0.result
            java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r3 = r0.label
            switch(r3) {
                case 0: goto L31;
                case 1: goto L2c;
                default: goto L24;
            }
        L24:
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
            r8.<init>(r0)
            throw r8
        L2c:
            kotlin.ResultKt.throwOnFailure(r1)
            r8 = r1
            goto L4d
        L31:
            kotlin.ResultKt.throwOnFailure(r1)
            r3 = r7
            kotlinx.coroutines.CoroutineDispatcher r4 = kotlinx.coroutines.Dispatchers.getIO()
            kotlin.coroutines.CoroutineContext r4 = (kotlin.coroutines.CoroutineContext) r4
            com.example.ui.DaliliViewModel$callGeminiApiDirect$2 r5 = new com.example.ui.DaliliViewModel$callGeminiApiDirect$2
            r6 = 0
            r5.<init>(r8, r3, r6)
            kotlin.jvm.functions.Function2 r5 = (kotlin.jvm.functions.Function2) r5
            r6 = 1
            r0.label = r6
            java.lang.Object r8 = kotlinx.coroutines.BuildersKt.withContext(r4, r5, r0)
            if (r8 != r2) goto L4d
            return r2
        L4d:
            java.lang.String r2 = "withContext(...)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r8, r2)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.DaliliViewModel.callGeminiApiDirect(java.lang.String, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x010b  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x010e A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.String getOfflineAnswer(java.lang.String r19) {
        /*
            Method dump skipped, instructions count: 457
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.DaliliViewModel.getOfflineAnswer(java.lang.String):java.lang.String");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence getOfflineAnswer$lambda$37(Category it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return "📁 " + it.getNameAr();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence getOfflineAnswer$lambda$39(ServiceProvider it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return "✨ " + it.getName() + " - 📞 " + it.getPhone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final List<Category> getDefaultCategories() {
        return CollectionsKt.listOf(new Category[]{new Category(1001, "خدمات الاتصالات والنت", "📱", 1, new Date().toString()), new Category(1002, "الهندسة والصيانة المنزلية", "🛠️", 2, new Date().toString()), new Category(1003, "الطب والتمريض والعيادات", "🩺", 3, new Date().toString()), new Category(1004, "سيارات وسائقين وأجرة", "🚕", 4, new Date().toString()), new Category(1005, "خدمات التعليم والتدريس", "📚", 5, new Date().toString()), new Category(1006, "خدمات الطعام وتوصيل الطلبات", "🍕", 6, new Date().toString())});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final List<SubCategory> getDefaultSubCategories() {
        return CollectionsKt.listOf(new SubCategory[]{new SubCategory(5001, 1003, "عيادات العظام", "🦴", 1, new Date().toString()), new SubCategory(5002, 1003, "عيادات العيون", "👁️", 2, new Date().toString()), new SubCategory(5003, 1003, "الجراحة العامة", "✂️", 3, new Date().toString()), new SubCategory(5004, 1003, "تمريض منزلي", "🩹", 4, new Date().toString()), new SubCategory(5005, 1002, "كهرباء منزلي", "🔌", 1, new Date().toString()), new SubCategory(5006, 1002, "أعمال السباكة", "🪠", 2, new Date().toString()), new SubCategory(5007, 1002, "صيانة مكيفات", "❄️", 3, new Date().toString()), new SubCategory(5008, 1001, "تمديد شبكات", "🎛️", 1, new Date().toString()), new SubCategory(5009, 1001, "برمجة وبطاقات", "💳", 2, new Date().toString())});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final List<ServiceProvider> getDefaultProviders() {
        return CollectionsKt.listOf(new ServiceProvider[]{new ServiceProvider(2001, "مؤسسة الاتصالات والشبكات والإنترنت المتكاملة", "777644670", 1001, null, 5.0d, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c", true, true, true, Double.valueOf(15.3694d), Double.valueOf(44.191d), "low", "near", null, 16384, null), new ServiceProvider(2002, "المهندس أحمد لصيانة التكييف والأجهزة المنزلية", "711223344", 1002, 5007, 4.8d, "https://images.unsplash.com/photo-1581092160607-ee22621dd758", true, false, true, Double.valueOf(15.35d), Double.valueOf(44.2d), "medium", "medium", null, 16384, null), new ServiceProvider(2003, "أخصائي الطقس والتمريض المنزلي السريع", "770011223", 1003, 5004, 5.0d, "https://images.unsplash.com/photo-1559839734-2b71ea197ec2", true, false, false, Double.valueOf(15.36d), Double.valueOf(44.18d), "high", "far", null, 16384, null), new ServiceProvider(2004, "تاكسي المشوار السريع للتنقل والرحلات", "777644670", 1004, null, 4.9d, "https://images.unsplash.com/photo-1549417229-aa67d3263c09", true, false, false, Double.valueOf(15.37d), Double.valueOf(44.21d), "medium", "near", null, 16384, null), new ServiceProvider(2005, "أستاذ الرياضيات والفيزياء الخصوصي", "733445566", 1005, null, 4.7d, "https://images.unsplash.com/photo-1434030216411-0b793f4b4173", true, false, false, Double.valueOf(15.334d), Double.valueOf(44.201d), "low", "medium", null, 16384, null), new ServiceProvider(2006, "مطعم الطاهي اليمني للوجبات السريعة والتوصيل", "775566778", 1006, null, 4.6d, "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38", true, false, false, Double.valueOf(15.366d), Double.valueOf(44.175d), "medium", "near", null, 16384, null)});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final List<Review> getDefaultReviews() {
        return CollectionsKt.listOf(new Review[]{new Review(3001, 2001, "أبو ماجد", "خدمة ممتازة وسريعة، وتغطية شبكة جيدة جداً في كل المناطق.", 5.0d, new Date().toString()), new Review(3002, 2001, "فيصل الحربي", "الدعم الفني متعاون للغاية وسرعة في استجابة المشكلات.", 4.0d, new Date().toString()), new Review(3003, 2003, "د. علي الخالدي", "أبطال الإسعاف، استجابة سريعة جداً في وقت الطوارئ شكراً لكم.", 5.0d, new Date().toString()), new Review(3004, 2004, "سارة أحمد", "سائق محترم والسيارة نظيفة ووصلت بالوقت المحدد.", 5.0d, new Date().toString())});
    }
}
