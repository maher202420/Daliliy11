package com.example.data;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import com.yemende.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Models.kt */
@JsonClass(generateAdapter = true)
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B=\u0012\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0003\u0010\u0007\u001a\u00020\u0003\u0012\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\fJ\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0005HÆ\u0003JD\u0010\u0019\u001a\u00020\u00002\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u00052\b\b\u0003\u0010\u0007\u001a\u00020\u00032\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u0005HÆ\u0001¢\u0006\u0002\u0010\u001aJ\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001e\u001a\u00020\u0003HÖ\u0001J\t\u0010\u001f\u001a\u00020\u0005HÖ\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000f¨\u0006 "}, d2 = {"Lcom/example/data/Category;", "", "id", "", "nameAr", "", "icon", "orderIndex", "createdAt", "<init>", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getNameAr", "()Ljava/lang/String;", "getIcon", "getOrderIndex", "()I", "getCreatedAt", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Lcom/example/data/Category;", "equals", "", "other", "hashCode", "toString", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes4.dex */
public final /* data */ class Category {
    public static final int $stable = 0;
    private final String createdAt;
    private final String icon;
    private final Integer id;
    private final String nameAr;
    private final int orderIndex;

    public static /* synthetic */ Category copy$default(Category category, Integer num, String str, String str2, int i, String str3, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            num = category.id;
        }
        if ((i2 & 2) != 0) {
            str = category.nameAr;
        }
        String str4 = str;
        if ((i2 & 4) != 0) {
            str2 = category.icon;
        }
        String str5 = str2;
        if ((i2 & 8) != 0) {
            i = category.orderIndex;
        }
        int i3 = i;
        if ((i2 & 16) != 0) {
            str3 = category.createdAt;
        }
        return category.copy(num, str4, str5, i3, str3);
    }

    /* renamed from: component1, reason: from getter */
    public final Integer getId() {
        return this.id;
    }

    /* renamed from: component2, reason: from getter */
    public final String getNameAr() {
        return this.nameAr;
    }

    /* renamed from: component3, reason: from getter */
    public final String getIcon() {
        return this.icon;
    }

    /* renamed from: component4, reason: from getter */
    public final int getOrderIndex() {
        return this.orderIndex;
    }

    /* renamed from: component5, reason: from getter */
    public final String getCreatedAt() {
        return this.createdAt;
    }

    public final Category copy(@Json(name = "id") Integer id, @Json(name = "nameAr") String nameAr, @Json(name = "icon") String icon, @Json(name = "orderIndex") int orderIndex, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(nameAr, "nameAr");
        Intrinsics.checkNotNullParameter(icon, "icon");
        return new Category(id, nameAr, icon, orderIndex, createdAt);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Category)) {
            return false;
        }
        Category category = (Category) other;
        return Intrinsics.areEqual(this.id, category.id) && Intrinsics.areEqual(this.nameAr, category.nameAr) && Intrinsics.areEqual(this.icon, category.icon) && this.orderIndex == category.orderIndex && Intrinsics.areEqual(this.createdAt, category.createdAt);
    }

    public int hashCode() {
        return ((((((((this.id == null ? 0 : this.id.hashCode()) * 31) + this.nameAr.hashCode()) * 31) + this.icon.hashCode()) * 31) + Integer.hashCode(this.orderIndex)) * 31) + (this.createdAt != null ? this.createdAt.hashCode() : 0);
    }

    public String toString() {
        return "Category(id=" + this.id + ", nameAr=" + this.nameAr + ", icon=" + this.icon + ", orderIndex=" + this.orderIndex + ", createdAt=" + this.createdAt + ")";
    }

    public Category(@Json(name = "id") Integer id, @Json(name = "nameAr") String nameAr, @Json(name = "icon") String icon, @Json(name = "orderIndex") int orderIndex, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(nameAr, "nameAr");
        Intrinsics.checkNotNullParameter(icon, "icon");
        this.id = id;
        this.nameAr = nameAr;
        this.icon = icon;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt;
    }

    public /* synthetic */ Category(Integer num, String str, String str2, int i, String str3, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? null : num, str, str2, (i2 & 8) != 0 ? 0 : i, (i2 & 16) != 0 ? null : str3);
    }

    public final Integer getId() {
        return this.id;
    }

    public final String getNameAr() {
        return this.nameAr;
    }

    public final String getIcon() {
        return this.icon;
    }

    public final int getOrderIndex() {
        return this.orderIndex;
    }

    public final String getCreatedAt() {
        return this.createdAt;
    }
}
