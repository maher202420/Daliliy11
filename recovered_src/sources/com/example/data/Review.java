package com.example.data;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import com.yemende.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Models.kt */
@JsonClass(generateAdapter = true)
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001BG\u0012\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0001\u0010\b\u001a\u00020\t\u0012\n\b\u0003\u0010\n\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\u000eJ\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0006HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0006HÆ\u0003J\t\u0010\u001c\u001a\u00020\tHÆ\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0006HÆ\u0003JN\u0010\u001e\u001a\u00020\u00002\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u00062\b\b\u0003\u0010\u0007\u001a\u00020\u00062\b\b\u0003\u0010\b\u001a\u00020\t2\n\b\u0003\u0010\n\u001a\u0004\u0018\u00010\u0006HÆ\u0001¢\u0006\u0002\u0010\u001fJ\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010#\u001a\u00020\u0003HÖ\u0001J\t\u0010$\u001a\u00020\u0006HÖ\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0013¨\u0006%"}, d2 = {"Lcom/example/data/Review;", "", "id", "", "providerId", "userName", "", "comment", "rating", "", "createdAt", "<init>", "(Ljava/lang/Integer;ILjava/lang/String;Ljava/lang/String;DLjava/lang/String;)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getProviderId", "()I", "getUserName", "()Ljava/lang/String;", "getComment", "getRating", "()D", "getCreatedAt", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Ljava/lang/Integer;ILjava/lang/String;Ljava/lang/String;DLjava/lang/String;)Lcom/example/data/Review;", "equals", "", "other", "hashCode", "toString", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes4.dex */
public final /* data */ class Review {
    public static final int $stable = 0;
    private final String comment;
    private final String createdAt;
    private final Integer id;
    private final int providerId;
    private final double rating;
    private final String userName;

    public static /* synthetic */ Review copy$default(Review review, Integer num, int i, String str, String str2, double d, String str3, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            num = review.id;
        }
        if ((i2 & 2) != 0) {
            i = review.providerId;
        }
        int i3 = i;
        if ((i2 & 4) != 0) {
            str = review.userName;
        }
        String str4 = str;
        if ((i2 & 8) != 0) {
            str2 = review.comment;
        }
        String str5 = str2;
        if ((i2 & 16) != 0) {
            d = review.rating;
        }
        double d2 = d;
        if ((i2 & 32) != 0) {
            str3 = review.createdAt;
        }
        return review.copy(num, i3, str4, str5, d2, str3);
    }

    /* renamed from: component1, reason: from getter */
    public final Integer getId() {
        return this.id;
    }

    /* renamed from: component2, reason: from getter */
    public final int getProviderId() {
        return this.providerId;
    }

    /* renamed from: component3, reason: from getter */
    public final String getUserName() {
        return this.userName;
    }

    /* renamed from: component4, reason: from getter */
    public final String getComment() {
        return this.comment;
    }

    /* renamed from: component5, reason: from getter */
    public final double getRating() {
        return this.rating;
    }

    /* renamed from: component6, reason: from getter */
    public final String getCreatedAt() {
        return this.createdAt;
    }

    public final Review copy(@Json(name = "id") Integer id, @Json(name = "providerId") int providerId, @Json(name = "userName") String userName, @Json(name = "comment") String comment, @Json(name = "rating") double rating, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(userName, "userName");
        Intrinsics.checkNotNullParameter(comment, "comment");
        return new Review(id, providerId, userName, comment, rating, createdAt);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Review)) {
            return false;
        }
        Review review = (Review) other;
        return Intrinsics.areEqual(this.id, review.id) && this.providerId == review.providerId && Intrinsics.areEqual(this.userName, review.userName) && Intrinsics.areEqual(this.comment, review.comment) && Double.compare(this.rating, review.rating) == 0 && Intrinsics.areEqual(this.createdAt, review.createdAt);
    }

    public int hashCode() {
        return ((((((((((this.id == null ? 0 : this.id.hashCode()) * 31) + Integer.hashCode(this.providerId)) * 31) + this.userName.hashCode()) * 31) + this.comment.hashCode()) * 31) + Double.hashCode(this.rating)) * 31) + (this.createdAt != null ? this.createdAt.hashCode() : 0);
    }

    public String toString() {
        return "Review(id=" + this.id + ", providerId=" + this.providerId + ", userName=" + this.userName + ", comment=" + this.comment + ", rating=" + this.rating + ", createdAt=" + this.createdAt + ")";
    }

    public Review(@Json(name = "id") Integer id, @Json(name = "providerId") int providerId, @Json(name = "userName") String userName, @Json(name = "comment") String comment, @Json(name = "rating") double rating, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(userName, "userName");
        Intrinsics.checkNotNullParameter(comment, "comment");
        this.id = id;
        this.providerId = providerId;
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ Review(java.lang.Integer r11, int r12, java.lang.String r13, java.lang.String r14, double r15, java.lang.String r17, int r18, kotlin.jvm.internal.DefaultConstructorMarker r19) {
        /*
            r10 = this;
            r0 = r18 & 1
            r1 = 0
            if (r0 == 0) goto L7
            r3 = r1
            goto L8
        L7:
            r3 = r11
        L8:
            r0 = r18 & 32
            if (r0 == 0) goto Le
            r9 = r1
            goto L10
        Le:
            r9 = r17
        L10:
            r2 = r10
            r4 = r12
            r5 = r13
            r6 = r14
            r7 = r15
            r2.<init>(r3, r4, r5, r6, r7, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.data.Review.<init>(java.lang.Integer, int, java.lang.String, java.lang.String, double, java.lang.String, int, kotlin.jvm.internal.DefaultConstructorMarker):void");
    }

    public final Integer getId() {
        return this.id;
    }

    public final int getProviderId() {
        return this.providerId;
    }

    public final String getUserName() {
        return this.userName;
    }

    public final String getComment() {
        return this.comment;
    }

    public final double getRating() {
        return this.rating;
    }

    public final String getCreatedAt() {
        return this.createdAt;
    }
}
