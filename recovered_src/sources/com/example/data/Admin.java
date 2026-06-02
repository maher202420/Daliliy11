package com.example.data;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import com.yemende.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Models.kt */
@JsonClass(generateAdapter = true)
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B=\u0012\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0003\u0010\u0007\u001a\u00020\u0005\u0012\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\fJ\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0005HÆ\u0003JD\u0010\u0018\u001a\u00020\u00002\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u00052\b\b\u0003\u0010\u0007\u001a\u00020\u00052\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u0005HÆ\u0001¢\u0006\u0002\u0010\u0019J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001d\u001a\u00020\u0003HÖ\u0001J\t\u0010\u001e\u001a\u00020\u0005HÖ\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000f¨\u0006\u001f"}, d2 = {"Lcom/example/data/Admin;", "", "id", "", "username", "", "passwordHash", "role", "createdAt", "<init>", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getUsername", "()Ljava/lang/String;", "getPasswordHash", "getRole", "getCreatedAt", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/example/data/Admin;", "equals", "", "other", "hashCode", "toString", "app_debug"}, k = BuildConfig.VERSION_CODE, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes4.dex */
public final /* data */ class Admin {
    public static final int $stable = 0;
    private final String createdAt;
    private final Integer id;
    private final String passwordHash;
    private final String role;
    private final String username;

    public static /* synthetic */ Admin copy$default(Admin admin, Integer num, String str, String str2, String str3, String str4, int i, Object obj) {
        if ((i & 1) != 0) {
            num = admin.id;
        }
        if ((i & 2) != 0) {
            str = admin.username;
        }
        String str5 = str;
        if ((i & 4) != 0) {
            str2 = admin.passwordHash;
        }
        String str6 = str2;
        if ((i & 8) != 0) {
            str3 = admin.role;
        }
        String str7 = str3;
        if ((i & 16) != 0) {
            str4 = admin.createdAt;
        }
        return admin.copy(num, str5, str6, str7, str4);
    }

    /* renamed from: component1, reason: from getter */
    public final Integer getId() {
        return this.id;
    }

    /* renamed from: component2, reason: from getter */
    public final String getUsername() {
        return this.username;
    }

    /* renamed from: component3, reason: from getter */
    public final String getPasswordHash() {
        return this.passwordHash;
    }

    /* renamed from: component4, reason: from getter */
    public final String getRole() {
        return this.role;
    }

    /* renamed from: component5, reason: from getter */
    public final String getCreatedAt() {
        return this.createdAt;
    }

    public final Admin copy(@Json(name = "id") Integer id, @Json(name = "username") String username, @Json(name = "password_hash") String passwordHash, @Json(name = "role") String role, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(username, "username");
        Intrinsics.checkNotNullParameter(passwordHash, "passwordHash");
        Intrinsics.checkNotNullParameter(role, "role");
        return new Admin(id, username, passwordHash, role, createdAt);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Admin)) {
            return false;
        }
        Admin admin = (Admin) other;
        return Intrinsics.areEqual(this.id, admin.id) && Intrinsics.areEqual(this.username, admin.username) && Intrinsics.areEqual(this.passwordHash, admin.passwordHash) && Intrinsics.areEqual(this.role, admin.role) && Intrinsics.areEqual(this.createdAt, admin.createdAt);
    }

    public int hashCode() {
        return ((((((((this.id == null ? 0 : this.id.hashCode()) * 31) + this.username.hashCode()) * 31) + this.passwordHash.hashCode()) * 31) + this.role.hashCode()) * 31) + (this.createdAt != null ? this.createdAt.hashCode() : 0);
    }

    public String toString() {
        return "Admin(id=" + this.id + ", username=" + this.username + ", passwordHash=" + this.passwordHash + ", role=" + this.role + ", createdAt=" + this.createdAt + ")";
    }

    public Admin(@Json(name = "id") Integer id, @Json(name = "username") String username, @Json(name = "password_hash") String passwordHash, @Json(name = "role") String role, @Json(name = "created_at") String createdAt) {
        Intrinsics.checkNotNullParameter(username, "username");
        Intrinsics.checkNotNullParameter(passwordHash, "passwordHash");
        Intrinsics.checkNotNullParameter(role, "role");
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public /* synthetic */ Admin(Integer num, String str, String str2, String str3, String str4, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : num, str, str2, (i & 8) != 0 ? "admin" : str3, (i & 16) != 0 ? null : str4);
    }

    public final Integer getId() {
        return this.id;
    }

    public final String getUsername() {
        return this.username;
    }

    public final String getPasswordHash() {
        return this.passwordHash;
    }

    public final String getRole() {
        return this.role;
    }

    public final String getCreatedAt() {
        return this.createdAt;
    }
}
