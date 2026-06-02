package com.example.ui.theme;

import androidx.compose.material3.Typography;
import androidx.compose.ui.graphics.Shadow;
import androidx.compose.ui.graphics.drawscope.DrawStyle;
import androidx.compose.ui.text.PlatformTextStyle;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontSynthesis;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.intl.LocaleList;
import androidx.compose.ui.text.style.BaselineShift;
import androidx.compose.ui.text.style.LineHeightStyle;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.text.style.TextGeometricTransform;
import androidx.compose.ui.text.style.TextIndent;
import androidx.compose.ui.text.style.TextMotion;
import androidx.compose.ui.unit.TextUnitKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* compiled from: Type.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\"\u0011\u0010\u0000\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Typography", "Landroidx/compose/material3/Typography;", "getTypography", "()Landroidx/compose/material3/Typography;", "app_debug"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes3.dex */
public final class TypeKt {
    private static final Typography Typography;

    static {
        FontFamily fontFamily = FontFamily.Companion.getDefault();
        FontWeight normal = FontWeight.Companion.getNormal();
        FontFamily fontFamily2 = fontFamily;
        TextStyle textStyle = new TextStyle(0L, TextUnitKt.getSp(16), normal, (FontStyle) null, (FontSynthesis) null, fontFamily2, (String) null, TextUnitKt.getSp(0.5d), (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (DrawStyle) null, 0, 0, TextUnitKt.getSp(24), (TextIndent) null, (PlatformTextStyle) null, (LineHeightStyle) null, 0, 0, (TextMotion) null, 16645977, (DefaultConstructorMarker) null);
        FontFamily fontFamily3 = FontFamily.Companion.getDefault();
        FontWeight bold = FontWeight.Companion.getBold();
        FontFamily fontFamily4 = fontFamily3;
        TextStyle textStyle2 = new TextStyle(0L, TextUnitKt.getSp(22), bold, (FontStyle) null, (FontSynthesis) null, fontFamily4, (String) null, TextUnitKt.getSp(0), (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (DrawStyle) null, 0, 0, TextUnitKt.getSp(28), (TextIndent) null, (PlatformTextStyle) null, (LineHeightStyle) null, 0, 0, (TextMotion) null, 16645977, (DefaultConstructorMarker) null);
        FontFamily fontFamily5 = FontFamily.Companion.getDefault();
        FontWeight medium = FontWeight.Companion.getMedium();
        FontFamily fontFamily6 = fontFamily5;
        Typography = new Typography((TextStyle) null, (TextStyle) null, (TextStyle) null, (TextStyle) null, (TextStyle) null, (TextStyle) null, textStyle2, (TextStyle) null, (TextStyle) null, textStyle, (TextStyle) null, (TextStyle) null, (TextStyle) null, (TextStyle) null, new TextStyle(0L, TextUnitKt.getSp(11), medium, (FontStyle) null, (FontSynthesis) null, fontFamily6, (String) null, TextUnitKt.getSp(0.5d), (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (DrawStyle) null, 0, 0, TextUnitKt.getSp(16), (TextIndent) null, (PlatformTextStyle) null, (LineHeightStyle) null, 0, 0, (TextMotion) null, 16645977, (DefaultConstructorMarker) null), 15807, (DefaultConstructorMarker) null);
    }

    public static final Typography getTypography() {
        return Typography;
    }
}
