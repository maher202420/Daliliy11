package com.example.ui.theme;

import androidx.compose.material3.ColorScheme;
import androidx.compose.material3.ColorSchemeKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.RecomposeScopeImplKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/* compiled from: Theme.kt */
@Metadata(d1 = {"\u0000\"\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a(\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0011\u0010\u000b\u001a\r\u0012\u0004\u0012\u00020\b0\f¢\u0006\u0002\b\rH\u0007¢\u0006\u0002\u0010\u000e\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"RedBlackScheme", "Landroidx/compose/material3/ColorScheme;", "RoyalIndigoScheme", "EmeraldGreenScheme", "SlateSilverScheme", "OceanTealScheme", "BeigeCreamScheme", "MyApplicationTheme", "", "themeChoice", "", "content", "Lkotlin/Function0;", "Landroidx/compose/runtime/Composable;", "(Ljava/lang/String;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;I)V", "app_debug"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/dex/classes3.dex */
public final class ThemeKt {
    private static final ColorScheme BeigeCreamScheme;
    private static final ColorScheme EmeraldGreenScheme;
    private static final ColorScheme OceanTealScheme;
    private static final ColorScheme RedBlackScheme;
    private static final ColorScheme RoyalIndigoScheme;
    private static final ColorScheme SlateSilverScheme;

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MyApplicationTheme$lambda$0(String str, Function2 function2, int i, Composer composer, int i2) {
        MyApplicationTheme(str, function2, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    static {
        long redPrimary = ColorKt.getRedPrimary();
        long redPrimaryContainer = ColorKt.getRedPrimaryContainer();
        long redOnPrimary = ColorKt.getRedOnPrimary();
        long redBackground = ColorKt.getRedBackground();
        long redSurface = ColorKt.getRedSurface();
        RedBlackScheme = ColorSchemeKt.darkColorScheme-C-Xl9yA$default(redPrimary, redOnPrimary, redPrimaryContainer, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4294939904L), 0L, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4294953984L), 0L, 0L, 0L, redBackground, ColorKt.getBrightWhite(), redSurface, ColorKt.getRedOnSurface(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -123432, 15, (Object) null);
        long indigoPrimary = ColorKt.getIndigoPrimary();
        long indigoPrimaryContainer = ColorKt.getIndigoPrimaryContainer();
        long indigoOnPrimary = ColorKt.getIndigoOnPrimary();
        long indigoBackground = ColorKt.getIndigoBackground();
        long indigoSurface = ColorKt.getIndigoSurface();
        RoyalIndigoScheme = ColorSchemeKt.darkColorScheme-C-Xl9yA$default(indigoPrimary, indigoOnPrimary, indigoPrimaryContainer, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4278243839L), 0L, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4289028079L), 0L, 0L, 0L, indigoBackground, ColorKt.getBrightWhite(), indigoSurface, ColorKt.getIndigoOnSurface(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -123432, 15, (Object) null);
        long emeraldPrimary = ColorKt.getEmeraldPrimary();
        long emeraldPrimaryContainer = ColorKt.getEmeraldPrimaryContainer();
        long emeraldOnPrimary = ColorKt.getEmeraldOnPrimary();
        long emeraldBackground = ColorKt.getEmeraldBackground();
        long emeraldSurface = ColorKt.getEmeraldSurface();
        EmeraldGreenScheme = ColorSchemeKt.darkColorScheme-C-Xl9yA$default(emeraldPrimary, emeraldOnPrimary, emeraldPrimaryContainer, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4294953984L), 0L, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4284139770L), 0L, 0L, 0L, emeraldBackground, ColorKt.getBrightWhite(), emeraldSurface, ColorKt.getEmeraldOnSurface(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -123432, 15, (Object) null);
        long slatePrimary = ColorKt.getSlatePrimary();
        long slatePrimaryContainer = ColorKt.getSlatePrimaryContainer();
        long slateOnPrimary = ColorKt.getSlateOnPrimary();
        long slateBackground = ColorKt.getSlateBackground();
        long slateSurface = ColorKt.getSlateSurface();
        SlateSilverScheme = ColorSchemeKt.darkColorScheme-C-Xl9yA$default(slatePrimary, slateOnPrimary, slatePrimaryContainer, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4293256682L), 0L, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4291940822L), 0L, 0L, 0L, slateBackground, ColorKt.getBrightWhite(), slateSurface, ColorKt.getSlateOnSurface(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -123432, 15, (Object) null);
        long tealPrimary = ColorKt.getTealPrimary();
        long tealPrimaryContainer = ColorKt.getTealPrimaryContainer();
        long tealOnPrimary = ColorKt.getTealOnPrimary();
        long tealBackground = ColorKt.getTealBackground();
        long tealSurface = ColorKt.getTealSurface();
        OceanTealScheme = ColorSchemeKt.darkColorScheme-C-Xl9yA$default(tealPrimary, tealOnPrimary, tealPrimaryContainer, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4281648985L), 0L, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4294939904L), 0L, 0L, 0L, tealBackground, ColorKt.getBrightWhite(), tealSurface, ColorKt.getTealOnSurface(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -123432, 15, (Object) null);
        long beigePrimary = ColorKt.getBeigePrimary();
        long beigePrimaryContainer = ColorKt.getBeigePrimaryContainer();
        long beigeOnPrimary = ColorKt.getBeigeOnPrimary();
        long beigeBackground = ColorKt.getBeigeBackground();
        long beigeSurface = ColorKt.getBeigeSurface();
        BeigeCreamScheme = ColorSchemeKt.lightColorScheme-C-Xl9yA$default(beigePrimary, beigeOnPrimary, beigePrimaryContainer, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4291659071L), 0L, 0L, 0L, androidx.compose.ui.graphics.ColorKt.Color(4287317267L), 0L, 0L, 0L, beigeBackground, ColorKt.getBeigeOnSurface(), beigeSurface, ColorKt.getBeigeOnSurface(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -123432, 15, (Object) null);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0063, code lost:
    
        if (r9.equals("slate_silver") == false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a8, code lost:
    
        r1 = com.example.ui.theme.ThemeKt.SlateSilverScheme;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x006c, code lost:
    
        if (r9.equals("distinctive_white") == false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00b5, code lost:
    
        r1 = com.example.ui.theme.ThemeKt.BeigeCreamScheme;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0082, code lost:
    
        if (r9.equals("pure_silver") == false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00a5, code lost:
    
        if (r9.equals("silver_metallic") == false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00b2, code lost:
    
        if (r9.equals("beige_cream") == false) goto L57;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void MyApplicationTheme(final java.lang.String r9, final kotlin.jvm.functions.Function2<? super androidx.compose.runtime.Composer, ? super java.lang.Integer, kotlin.Unit> r10, androidx.compose.runtime.Composer r11, final int r12) {
        /*
            Method dump skipped, instructions count: 266
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.ui.theme.ThemeKt.MyApplicationTheme(java.lang.String, kotlin.jvm.functions.Function2, androidx.compose.runtime.Composer, int):void");
    }
}
