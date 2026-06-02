package com.example.data;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import com.yemende.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Models.kt */
@JsonClass(generateAdapter = true)
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001Bk\u0012\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0003\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0003\u0010\n\u001a\u00020\u0003\u0012\n\b\u0003\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0003\u0010\f\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\r\u0010\u000eJ\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0007HÆ\u0003J\u0010\u0010 \u001a\u0004\u0018\u00010\u0007HÆ\u0003¢\u0006\u0002\u0010\u0016J\u000b\u0010!\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0003HÆ\u0003Jr\u0010%\u001a\u00020\u00002\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u00032\b\b\u0003\u0010\u0006\u001a\u00020\u00072\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u00072\n\b\u0003\u0010\t\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\n\u001a\u00020\u00032\n\b\u0003\u0010\u000b\u001a\u0004\u0018\u00010\u00032\n\b\u0003\u0010\f\u001a\u0004\u0018\u00010\u0003HÆ\u0001¢\u0006\u0002\u0010&J\u0013\u0010'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010*\u001a\u00020\u0007HÖ\u0001J\t\u0010+\u001a\u00020\u0003HÖ\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0015\u0010\b\u001a\u0004\u0018\u00010\u0007¢\u0006\n\n\u0002\u0010\u0017\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010R\u0011\u0010\n\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0010R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0010R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0010¨\u0006,"}, d2 = {"Lcom/example/data/PendingProvider;", "", "id", "", "name", "phone", "categoryId", "", "subCategoryId", "imageUrl", "status", "region", "createdAt", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getId", "()Ljava/lang/String;", "getName", "getPhone", "getCategoryId", "()I", "getSubCategoryId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getImageUrl", "getStatus", "getRegion", "getCreatedAt", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/example/data/PendingProvider;", "equals", "", "other", "hashCode", "toString", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes4.dex */
public final /* data */ class PendingProvider {
    public static final int $stable = 0;
    private final int categoryId;
    private final String createdAt;
    private final String id;
    private final String imageUrl;
    private final String name;
    private final String phone;
    private final String region;
    private final String status;
    private final Integer subCategoryId;

    /* renamed from: component1, reason: from getter */
    public final String getId() {
        return this.id;
    }

    /* renamed from: component2, reason: from getter */
    public final String getName() {
        return this.name;
    }

    /* renamed from: component3, reason: from getter */
    public final String getPhone() {
        return this.phone;
    }

    /* renamed from: component4, reason: from getter */
    public final int getCategoryId() {
        return this.categoryId;
    }

    /* renamed from: component5, reason: from getter */
    public final Integer getSubCategoryId() {
        return this.subCategoryId;
    }

    /* renamed from: component6, reason: from getter */
    public final String getImageUrl() {
        return this.imageUrl;
    }

    /* renamed from: component7, reason: from getter */
    public final String getStatus() {
        return this.status;
    }

    /* renamed from: component8, reason: from getter */
    public final String getRegion() {
        return this.region;
    }

    /* renamed from: component9, reason: from getter */
    public final String getCreatedAt() {
        return this.createdAt;
    }

    public final PendingProvider copy(@Json(name = "id") String id, @Json(name = "name") String name, @Json(name = "phone") String phone, @Json(name = "categoryId") int categoryId, @Json(name = "subCategoryId") Integer subCategoryId, @Json(name = "imageUrl") String imageUrl, @Json(name = "status") String status, @Json(name = "region") String region, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(phone, "phone");
        Intrinsics.checkNotNullParameter(status, "status");
        return new PendingProvider(id, name, phone, categoryId, subCategoryId, imageUrl, status, region, createdAt);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PendingProvider)) {
            return false;
        }
        PendingProvider pendingProvider = (PendingProvider) other;
        return Intrinsics.areEqual(this.id, pendingProvider.id) && Intrinsics.areEqual(this.name, pendingProvider.name) && Intrinsics.areEqual(this.phone, pendingProvider.phone) && this.categoryId == pendingProvider.categoryId && Intrinsics.areEqual(this.subCategoryId, pendingProvider.subCategoryId) && Intrinsics.areEqual(this.imageUrl, pendingProvider.imageUrl) && Intrinsics.areEqual(this.status, pendingProvider.status) && Intrinsics.areEqual(this.region, pendingProvider.region) && Intrinsics.areEqual(this.createdAt, pendingProvider.createdAt);
    }

    public int hashCode() {
        return ((((((((((((((((this.id == null ? 0 : this.id.hashCode()) * 31) + this.name.hashCode()) * 31) + this.phone.hashCode()) * 31) + Integer.hashCode(this.categoryId)) * 31) + (this.subCategoryId == null ? 0 : this.subCategoryId.hashCode())) * 31) + (this.imageUrl == null ? 0 : this.imageUrl.hashCode())) * 31) + this.status.hashCode()) * 31) + (this.region == null ? 0 : this.region.hashCode())) * 31) + (this.createdAt != null ? this.createdAt.hashCode() : 0);
    }

    public String toString() {
        return "PendingProvider(id=" + this.id + ", name=" + this.name + ", phone=" + this.phone + ", categoryId=" + this.categoryId + ", subCategoryId=" + this.subCategoryId + ", imageUrl=" + this.imageUrl + ", status=" + this.status + ", region=" + this.region + ", createdAt=" + this.createdAt + ")";
    }

    public PendingProvider(@Json(name = "id") String id, @Json(name = "name") String name, @Json(name = "phone") String phone, @Json(name = "categoryId") int categoryId, @Json(name = "subCategoryId") Integer subCategoryId, @Json(name = "imageUrl") String imageUrl, @Json(name = "status") String status, @Json(name = "region") String region, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(phone, "phone");
        Intrinsics.checkNotNullParameter(status, "status");
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.imageUrl = imageUrl;
        this.status = status;
        this.region = region;
        this.createdAt = createdAt;
    }

    public /* synthetic */ PendingProvider(String str, String str2, String str3, int i, Integer num, String str4, String str5, String str6, String str7, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? null : str, str2, str3, i, (i2 & 16) != 0 ? null : num, (i2 & 32) != 0 ? null : str4, (i2 & 64) != 0 ? "pending" : str5, (i2 & 128) != 0 ? null : str6, (i2 & 256) != 0 ? null : str7);
    }

    public final String getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final String getPhone() {
        return this.phone;
    }

    public final int getCategoryId() {
        return this.categoryId;
    }

    public final Integer getSubCategoryId() {
        return this.subCategoryId;
    }

    public final String getImageUrl() {
        return this.imageUrl;
    }

    public final String getStatus() {
        return this.status;
    }

    public final String getRegion() {
        return this.region;
    }

    public final String getCreatedAt() {
        return this.createdAt;
    }
}
