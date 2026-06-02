package com.example.data;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import com.yemende.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Models.kt */
@JsonClass(generateAdapter = true)
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001BG\u0012\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0003\u0010\b\u001a\u00020\u0003\u0012\n\b\u0003\u0010\t\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\rJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0006HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0006HÆ\u0003JN\u0010\u001c\u001a\u00020\u00002\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u00062\b\b\u0003\u0010\u0007\u001a\u00020\u00062\b\b\u0003\u0010\b\u001a\u00020\u00032\n\b\u0003\u0010\t\u001a\u0004\u0018\u00010\u0006HÆ\u0001¢\u0006\u0002\u0010\u001dJ\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010!\u001a\u00020\u0003HÖ\u0001J\t\u0010\"\u001a\u00020\u0006HÖ\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012¨\u0006#"}, d2 = {"Lcom/example/data/SubCategory;", "", "id", "", "parentCategoryId", "nameAr", "", "icon", "orderIndex", "createdAt", "<init>", "(Ljava/lang/Integer;ILjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getParentCategoryId", "()I", "getNameAr", "()Ljava/lang/String;", "getIcon", "getOrderIndex", "getCreatedAt", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Ljava/lang/Integer;ILjava/lang/String;Ljava/lang/String;ILjava/lang/String;)Lcom/example/data/SubCategory;", "equals", "", "other", "hashCode", "toString", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes4.dex */
public final /* data */ class SubCategory {
    public static final int $stable = 0;
    private final String createdAt;
    private final String icon;
    private final Integer id;
    private final String nameAr;
    private final int orderIndex;
    private final int parentCategoryId;

    public static /* synthetic */ SubCategory copy$default(SubCategory subCategory, Integer num, int i, String str, String str2, int i2, String str3, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            num = subCategory.id;
        }
        if ((i3 & 2) != 0) {
            i = subCategory.parentCategoryId;
        }
        int i4 = i;
        if ((i3 & 4) != 0) {
            str = subCategory.nameAr;
        }
        String str4 = str;
        if ((i3 & 8) != 0) {
            str2 = subCategory.icon;
        }
        String str5 = str2;
        if ((i3 & 16) != 0) {
            i2 = subCategory.orderIndex;
        }
        int i5 = i2;
        if ((i3 & 32) != 0) {
            str3 = subCategory.createdAt;
        }
        return subCategory.copy(num, i4, str4, str5, i5, str3);
    }

    /* renamed from: component1, reason: from getter */
    public final Integer getId() {
        return this.id;
    }

    /* renamed from: component2, reason: from getter */
    public final int getParentCategoryId() {
        return this.parentCategoryId;
    }

    /* renamed from: component3, reason: from getter */
    public final String getNameAr() {
        return this.nameAr;
    }

    /* renamed from: component4, reason: from getter */
    public final String getIcon() {
        return this.icon;
    }

    /* renamed from: component5, reason: from getter */
    public final int getOrderIndex() {
        return this.orderIndex;
    }

    /* renamed from: component6, reason: from getter */
    public final String getCreatedAt() {
        return this.createdAt;
    }

    public final SubCategory copy(@Json(name = "id") Integer id, @Json(name = "parentCategoryId") int parentCategoryId, @Json(name = "nameAr") String nameAr, @Json(name = "icon") String icon, @Json(name = "orderIndex") int orderIndex, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(nameAr, "nameAr");
        Intrinsics.checkNotNullParameter(icon, "icon");
        return new SubCategory(id, parentCategoryId, nameAr, icon, orderIndex, createdAt);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SubCategory)) {
            return false;
        }
        SubCategory subCategory = (SubCategory) other;
        return Intrinsics.areEqual(this.id, subCategory.id) && this.parentCategoryId == subCategory.parentCategoryId && Intrinsics.areEqual(this.nameAr, subCategory.nameAr) && Intrinsics.areEqual(this.icon, subCategory.icon) && this.orderIndex == subCategory.orderIndex && Intrinsics.areEqual(this.createdAt, subCategory.createdAt);
    }

    public int hashCode() {
        return ((((((((((this.id == null ? 0 : this.id.hashCode()) * 31) + Integer.hashCode(this.parentCategoryId)) * 31) + this.nameAr.hashCode()) * 31) + this.icon.hashCode()) * 31) + Integer.hashCode(this.orderIndex)) * 31) + (this.createdAt != null ? this.createdAt.hashCode() : 0);
    }

    public String toString() {
        return "SubCategory(id=" + this.id + ", parentCategoryId=" + this.parentCategoryId + ", nameAr=" + this.nameAr + ", icon=" + this.icon + ", orderIndex=" + this.orderIndex + ", createdAt=" + this.createdAt + ")";
    }

    public SubCategory(@Json(name = "id") Integer id, @Json(name = "parentCategoryId") int parentCategoryId, @Json(name = "nameAr") String nameAr, @Json(name = "icon") String icon, @Json(name = "orderIndex") int orderIndex, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(nameAr, "nameAr");
        Intrinsics.checkNotNullParameter(icon, "icon");
        this.id = id;
        this.parentCategoryId = parentCategoryId;
        this.nameAr = nameAr;
        this.icon = icon;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt;
    }

    public /* synthetic */ SubCategory(Integer num, int i, String str, String str2, int i2, String str3, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this((i3 & 1) != 0 ? null : num, i, str, str2, (i3 & 16) != 0 ? 0 : i2, (i3 & 32) != 0 ? null : str3);
    }

    public final Integer getId() {
        return this.id;
    }

    public final int getParentCategoryId() {
        return this.parentCategoryId;
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
