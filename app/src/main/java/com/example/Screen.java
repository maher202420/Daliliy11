package com.example;

import com.example.data.Category;
import com.yemende.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0004\u0004\u0005\u0006\u0007B\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0004\b\t\n\u000b¨\u0006\f"}, d2 = {"Lcom/example/Screen;", "", "<init>", "()V", "Home", "CategoryDetails", "Login", "AdminDashboard", "Lcom/example/Screen$AdminDashboard;", "Lcom/example/Screen$CategoryDetails;", "Lcom/example/Screen$Home;", "Lcom/example/Screen$Login;", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes5.dex */
public abstract class Screen {
    public static final int $stable = 0;

    public /* synthetic */ Screen(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/example/Screen$Home;", "Lcom/example/Screen;", "<init>", "()V", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
    /* loaded from: /tmp/dex/classes5.dex */
    public static final class Home extends Screen {
        public static final int $stable = 0;
        public static final Home INSTANCE = new Home();

        private Home() {
            super(null);
        }
    }

    private Screen() {
    }

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rHÖ\u0003J\t\u0010\u000e\u001a\u00020\u000fHÖ\u0001J\t\u0010\u0010\u001a\u00020\u0011HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0012"}, d2 = {"Lcom/example/Screen$CategoryDetails;", "Lcom/example/Screen;", "category", "Lcom/example/data/Category;", "<init>", "(Lcom/example/data/Category;)V", "getCategory", "()Lcom/example/data/Category;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
    /* loaded from: /tmp/dex/classes5.dex */
    public static final /* data */ class CategoryDetails extends Screen {
        public static final int $stable = 0;
        private final Category category;

        public static /* synthetic */ CategoryDetails copy$default(CategoryDetails categoryDetails, Category category, int i, Object obj) {
            if ((i & 1) != 0) {
                category = categoryDetails.category;
            }
            return categoryDetails.copy(category);
        }

        /* renamed from: component1, reason: from getter */
        public final Category getCategory() {
            return this.category;
        }

        public final CategoryDetails copy(Category category) {
            Intrinsics.checkNotNullParameter(category, "category");
            return new CategoryDetails(category);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof CategoryDetails) && Intrinsics.areEqual(this.category, ((CategoryDetails) other).category);
        }

        public int hashCode() {
            return this.category.hashCode();
        }

        public String toString() {
            return "CategoryDetails(category=" + this.category + ")";
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CategoryDetails(Category category) {
            super(null);
            Intrinsics.checkNotNullParameter(category, "category");
            this.category = category;
        }

        public final Category getCategory() {
            return this.category;
        }
    }

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/example/Screen$Login;", "Lcom/example/Screen;", "<init>", "()V", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
    /* loaded from: /tmp/dex/classes5.dex */
    public static final class Login extends Screen {
        public static final int $stable = 0;
        public static final Login INSTANCE = new Login();

        private Login() {
            super(null);
        }
    }

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/example/Screen$AdminDashboard;", "Lcom/example/Screen;", "<init>", "()V", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
    /* loaded from: /tmp/dex/classes5.dex */
    public static final class AdminDashboard extends Screen {
        public static final int $stable = 0;
        public static final AdminDashboard INSTANCE = new AdminDashboard();

        private AdminDashboard() {
            super(null);
        }
    }
}
