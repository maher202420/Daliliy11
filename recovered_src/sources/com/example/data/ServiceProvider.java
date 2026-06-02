package com.example.data;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import com.yemende.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Models.kt */
@JsonClass(generateAdapter = true)
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b3\b\u0087\b\u0018\u00002\u00020\u0001B\u00ad\u0001\u0012\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0003\u0012\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0003\u0010\t\u001a\u00020\n\u0012\n\b\u0003\u0010\u000b\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0003\u0010\f\u001a\u00020\r\u0012\b\b\u0003\u0010\u000e\u001a\u00020\r\u0012\b\b\u0003\u0010\u000f\u001a\u00020\r\u0012\n\b\u0003\u0010\u0010\u001a\u0004\u0018\u00010\n\u0012\n\b\u0003\u0010\u0011\u001a\u0004\u0018\u00010\n\u0012\n\b\u0003\u0010\u0012\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0003\u0010\u0013\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0003\u0010\u0014\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0015\u0010\u0016J\u0010\u0010+\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\u0018J\t\u0010,\u001a\u00020\u0005HÆ\u0003J\t\u0010-\u001a\u00020\u0005HÆ\u0003J\t\u0010.\u001a\u00020\u0003HÆ\u0003J\u0010\u0010/\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\u0018J\t\u00100\u001a\u00020\nHÆ\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u00102\u001a\u00020\rHÆ\u0003J\t\u00103\u001a\u00020\rHÆ\u0003J\t\u00104\u001a\u00020\rHÆ\u0003J\u0010\u00105\u001a\u0004\u0018\u00010\nHÆ\u0003¢\u0006\u0002\u0010%J\u0010\u00106\u001a\u0004\u0018\u00010\nHÆ\u0003¢\u0006\u0002\u0010%J\u000b\u00107\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u00108\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u00109\u001a\u0004\u0018\u00010\u0005HÆ\u0003J´\u0001\u0010:\u001a\u00020\u00002\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u00052\b\b\u0003\u0010\u0007\u001a\u00020\u00032\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\t\u001a\u00020\n2\n\b\u0003\u0010\u000b\u001a\u0004\u0018\u00010\u00052\b\b\u0003\u0010\f\u001a\u00020\r2\b\b\u0003\u0010\u000e\u001a\u00020\r2\b\b\u0003\u0010\u000f\u001a\u00020\r2\n\b\u0003\u0010\u0010\u001a\u0004\u0018\u00010\n2\n\b\u0003\u0010\u0011\u001a\u0004\u0018\u00010\n2\n\b\u0003\u0010\u0012\u001a\u0004\u0018\u00010\u00052\n\b\u0003\u0010\u0013\u001a\u0004\u0018\u00010\u00052\n\b\u0003\u0010\u0014\u001a\u0004\u0018\u00010\u0005HÆ\u0001¢\u0006\u0002\u0010;J\u0013\u0010<\u001a\u00020\r2\b\u0010=\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010>\u001a\u00020\u0003HÖ\u0001J\t\u0010?\u001a\u00020\u0005HÖ\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0015\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u001f\u0010\u0018R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001bR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010#R\u0011\u0010\u000e\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010#R\u0011\u0010\u000f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010#R\u0015\u0010\u0010\u001a\u0004\u0018\u00010\n¢\u0006\n\n\u0002\u0010&\u001a\u0004\b$\u0010%R\u0015\u0010\u0011\u001a\u0004\u0018\u00010\n¢\u0006\n\n\u0002\u0010&\u001a\u0004\b'\u0010%R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001bR\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001bR\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001b¨\u0006@"}, d2 = {"Lcom/example/data/ServiceProvider;", "", "id", "", "name", "", "phone", "categoryId", "subCategoryId", "rating", "", "imageUrl", "isActive", "", "isPinned", "isRecommended", "lat", "lng", "priceCategory", "distanceCategory", "createdAt", "<init>", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Integer;DLjava/lang/String;ZZZLjava/lang/Double;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getName", "()Ljava/lang/String;", "getPhone", "getCategoryId", "()I", "getSubCategoryId", "getRating", "()D", "getImageUrl", "()Z", "getLat", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getLng", "getPriceCategory", "getDistanceCategory", "getCreatedAt", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "copy", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Integer;DLjava/lang/String;ZZZLjava/lang/Double;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/example/data/ServiceProvider;", "equals", "other", "hashCode", "toString", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes4.dex */
public final /* data */ class ServiceProvider {
    public static final int $stable = 0;
    private final int categoryId;
    private final String createdAt;
    private final String distanceCategory;
    private final Integer id;
    private final String imageUrl;
    private final boolean isActive;
    private final boolean isPinned;
    private final boolean isRecommended;
    private final Double lat;
    private final Double lng;
    private final String name;
    private final String phone;
    private final String priceCategory;
    private final double rating;
    private final Integer subCategoryId;

    /* renamed from: component1, reason: from getter */
    public final Integer getId() {
        return this.id;
    }

    /* renamed from: component10, reason: from getter */
    public final boolean getIsRecommended() {
        return this.isRecommended;
    }

    /* renamed from: component11, reason: from getter */
    public final Double getLat() {
        return this.lat;
    }

    /* renamed from: component12, reason: from getter */
    public final Double getLng() {
        return this.lng;
    }

    /* renamed from: component13, reason: from getter */
    public final String getPriceCategory() {
        return this.priceCategory;
    }

    /* renamed from: component14, reason: from getter */
    public final String getDistanceCategory() {
        return this.distanceCategory;
    }

    /* renamed from: component15, reason: from getter */
    public final String getCreatedAt() {
        return this.createdAt;
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
    public final double getRating() {
        return this.rating;
    }

    /* renamed from: component7, reason: from getter */
    public final String getImageUrl() {
        return this.imageUrl;
    }

    /* renamed from: component8, reason: from getter */
    public final boolean getIsActive() {
        return this.isActive;
    }

    /* renamed from: component9, reason: from getter */
    public final boolean getIsPinned() {
        return this.isPinned;
    }

    public final ServiceProvider copy(@Json(name = "id") Integer id, @Json(name = "name") String name, @Json(name = "phone") String phone, @Json(name = "categoryId") int categoryId, @Json(name = "subCategoryId") Integer subCategoryId, @Json(name = "rating") double rating, @Json(name = "imageUrl") String imageUrl, @Json(name = "isActive") boolean isActive, @Json(name = "isPinned") boolean isPinned, @Json(name = "isRecommended") boolean isRecommended, @Json(name = "lat") Double lat, @Json(name = "lng") Double lng, @Json(name = "priceCategory") String priceCategory, @Json(name = "distanceCategory") String distanceCategory, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(phone, "phone");
        return new ServiceProvider(id, name, phone, categoryId, subCategoryId, rating, imageUrl, isActive, isPinned, isRecommended, lat, lng, priceCategory, distanceCategory, createdAt);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ServiceProvider)) {
            return false;
        }
        ServiceProvider serviceProvider = (ServiceProvider) other;
        return Intrinsics.areEqual(this.id, serviceProvider.id) && Intrinsics.areEqual(this.name, serviceProvider.name) && Intrinsics.areEqual(this.phone, serviceProvider.phone) && this.categoryId == serviceProvider.categoryId && Intrinsics.areEqual(this.subCategoryId, serviceProvider.subCategoryId) && Double.compare(this.rating, serviceProvider.rating) == 0 && Intrinsics.areEqual(this.imageUrl, serviceProvider.imageUrl) && this.isActive == serviceProvider.isActive && this.isPinned == serviceProvider.isPinned && this.isRecommended == serviceProvider.isRecommended && Intrinsics.areEqual(this.lat, serviceProvider.lat) && Intrinsics.areEqual(this.lng, serviceProvider.lng) && Intrinsics.areEqual(this.priceCategory, serviceProvider.priceCategory) && Intrinsics.areEqual(this.distanceCategory, serviceProvider.distanceCategory) && Intrinsics.areEqual(this.createdAt, serviceProvider.createdAt);
    }

    public int hashCode() {
        return ((((((((((((((((((((((((((((this.id == null ? 0 : this.id.hashCode()) * 31) + this.name.hashCode()) * 31) + this.phone.hashCode()) * 31) + Integer.hashCode(this.categoryId)) * 31) + (this.subCategoryId == null ? 0 : this.subCategoryId.hashCode())) * 31) + Double.hashCode(this.rating)) * 31) + (this.imageUrl == null ? 0 : this.imageUrl.hashCode())) * 31) + Boolean.hashCode(this.isActive)) * 31) + Boolean.hashCode(this.isPinned)) * 31) + Boolean.hashCode(this.isRecommended)) * 31) + (this.lat == null ? 0 : this.lat.hashCode())) * 31) + (this.lng == null ? 0 : this.lng.hashCode())) * 31) + (this.priceCategory == null ? 0 : this.priceCategory.hashCode())) * 31) + (this.distanceCategory == null ? 0 : this.distanceCategory.hashCode())) * 31) + (this.createdAt != null ? this.createdAt.hashCode() : 0);
    }

    public String toString() {
        return "ServiceProvider(id=" + this.id + ", name=" + this.name + ", phone=" + this.phone + ", categoryId=" + this.categoryId + ", subCategoryId=" + this.subCategoryId + ", rating=" + this.rating + ", imageUrl=" + this.imageUrl + ", isActive=" + this.isActive + ", isPinned=" + this.isPinned + ", isRecommended=" + this.isRecommended + ", lat=" + this.lat + ", lng=" + this.lng + ", priceCategory=" + this.priceCategory + ", distanceCategory=" + this.distanceCategory + ", createdAt=" + this.createdAt + ")";
    }

    public ServiceProvider(@Json(name = "id") Integer id, @Json(name = "name") String name, @Json(name = "phone") String phone, @Json(name = "categoryId") int categoryId, @Json(name = "subCategoryId") Integer subCategoryId, @Json(name = "rating") double rating, @Json(name = "imageUrl") String imageUrl, @Json(name = "isActive") boolean isActive, @Json(name = "isPinned") boolean isPinned, @Json(name = "isRecommended") boolean isRecommended, @Json(name = "lat") Double lat, @Json(name = "lng") Double lng, @Json(name = "priceCategory") String priceCategory, @Json(name = "distanceCategory") String distanceCategory, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(phone, "phone");
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.isPinned = isPinned;
        this.isRecommended = isRecommended;
        this.lat = lat;
        this.lng = lng;
        this.priceCategory = priceCategory;
        this.distanceCategory = distanceCategory;
        this.createdAt = createdAt;
    }

    public /* synthetic */ ServiceProvider(Integer num, String str, String str2, int i, Integer num2, double d, String str3, boolean z, boolean z2, boolean z3, Double d2, Double d3, String str4, String str5, String str6, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? null : num, str, str2, i, (i2 & 16) != 0 ? null : num2, (i2 & 32) != 0 ? 0.0d : d, (i2 & 64) != 0 ? null : str3, (i2 & 128) != 0 ? true : z, (i2 & 256) != 0 ? false : z2, (i2 & 512) != 0 ? false : z3, (i2 & 1024) != 0 ? null : d2, (i2 & 2048) != 0 ? null : d3, (i2 & 4096) != 0 ? null : str4, (i2 & 8192) != 0 ? null : str5, (i2 & 16384) != 0 ? null : str6);
    }

    public final Integer getId() {
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

    public final double getRating() {
        return this.rating;
    }

    public final String getImageUrl() {
        return this.imageUrl;
    }

    public final boolean isActive() {
        return this.isActive;
    }

    public final boolean isPinned() {
        return this.isPinned;
    }

    public final boolean isRecommended() {
        return this.isRecommended;
    }

    public final Double getLat() {
        return this.lat;
    }

    public final Double getLng() {
        return this.lng;
    }

    public final String getPriceCategory() {
        return this.priceCategory;
    }

    public final String getDistanceCategory() {
        return this.distanceCategory;
    }

    public final String getCreatedAt() {
        return this.createdAt;
    }
}
