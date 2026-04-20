package com.hjq.permissions.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;

/**
 * Android version helper methods.
 */
@SuppressLint("AnnotateVersionCheck")
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public final class PermissionVersion {

    public static final int ANDROID_16 = Build.VERSION_CODES.BAKLAVA;
    public static final int ANDROID_15 = Build.VERSION_CODES.VANILLA_ICE_CREAM;
    public static final int ANDROID_14 = Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    public static final int ANDROID_13 = Build.VERSION_CODES.TIRAMISU;
    public static final int ANDROID_12_L = Build.VERSION_CODES.S_V2;
    public static final int ANDROID_12 = Build.VERSION_CODES.S;
    public static final int ANDROID_11 = Build.VERSION_CODES.R;
    public static final int ANDROID_10 = Build.VERSION_CODES.Q;
    public static final int ANDROID_9 = Build.VERSION_CODES.P;
    public static final int ANDROID_8_1 = Build.VERSION_CODES.O_MR1;
    public static final int ANDROID_8 = Build.VERSION_CODES.O;
    public static final int ANDROID_7_1 = Build.VERSION_CODES.N_MR1;
    public static final int ANDROID_7 = Build.VERSION_CODES.N;
    public static final int ANDROID_6 = Build.VERSION_CODES.M;
    public static final int ANDROID_5_1 = Build.VERSION_CODES.LOLLIPOP_MR1;
    public static final int ANDROID_5 = Build.VERSION_CODES.LOLLIPOP;
    public static final int ANDROID_4_4 = Build.VERSION_CODES.KITKAT;
    public static final int ANDROID_4_3 = Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final int ANDROID_4_2 = Build.VERSION_CODES.JELLY_BEAN_MR1;
    public static final int ANDROID_4_1 = Build.VERSION_CODES.JELLY_BEAN;
    public static final int ANDROID_4_0 = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static final int ANDROID_3_2 = Build.VERSION_CODES.HONEYCOMB_MR2;
    public static final int ANDROID_3_1 = Build.VERSION_CODES.HONEYCOMB_MR1;
    public static final int ANDROID_3_0 = Build.VERSION_CODES.HONEYCOMB;
    public static final int ANDROID_2_3_3 = Build.VERSION_CODES.GINGERBREAD_MR1;
    public static final int ANDROID_2_3 = Build.VERSION_CODES.GINGERBREAD;
    public static final int ANDROID_2_2 = Build.VERSION_CODES.FROYO;
    public static final int ANDROID_2_1 = Build.VERSION_CODES.ECLAIR_MR1;
    public static final int ANDROID_2_0_1 = Build.VERSION_CODES.ECLAIR_0_1;
    public static final int ANDROID_2_0 = Build.VERSION_CODES.ECLAIR;

    /** Returns the current SDK version. */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /** Returns the app target SDK version. */
    public static int getTargetSdkVersion(@NonNull Context context) {
        return context.getApplicationInfo().targetSdkVersion;
    }

    /** Returns whether the device is running Android 16 or later. */
    public static boolean isAndroid16() {
        return Build.VERSION.SDK_INT >= ANDROID_16;
    }

    /** Returns whether the device is running Android 15 or later. */
    public static boolean isAndroid15() {
        return Build.VERSION.SDK_INT >= ANDROID_15;
    }

    /** Returns whether the device is running Android 14 or later. */
    public static boolean isAndroid14() {
        return Build.VERSION.SDK_INT >= ANDROID_14;
    }

    /** Returns whether the device is running Android 13 or later. */
    public static boolean isAndroid13() {
        return Build.VERSION.SDK_INT >= ANDROID_13;
    }

    /** Returns whether the device is running Android 12 or later. */
    public static boolean isAndroid12() {
        return Build.VERSION.SDK_INT >= ANDROID_12;
    }

    /** Returns whether the device is running Android 11 or later. */
    public static boolean isAndroid11() {
        return Build.VERSION.SDK_INT >= ANDROID_11;
    }

    /** Returns whether the device is running Android 10 or later. */
    public static boolean isAndroid10() {
        return Build.VERSION.SDK_INT >= ANDROID_10;
    }

    /** Returns whether the device is running Android 9 or later. */
    public static boolean isAndroid9() {
        return Build.VERSION.SDK_INT >= ANDROID_9;
    }

    /** Returns whether the device is running Android 8.1 or later. */
    public static boolean isAndroid8_1() {
        return Build.VERSION.SDK_INT >= ANDROID_8_1;
    }

    /** Returns whether the device is running Android 8 or later. */
    public static boolean isAndroid8() {
        return Build.VERSION.SDK_INT >= ANDROID_8;
    }

    /** Returns whether the device is running Android 7.1 or later. */
    public static boolean isAndroid7_1() {
        return Build.VERSION.SDK_INT >= ANDROID_7_1;
    }

    /** Returns whether the device is running Android 7 or later. */
    public static boolean isAndroid7() {
        return Build.VERSION.SDK_INT >= ANDROID_7;
    }

    /** Returns whether the device is running Android 6 or later. */
    public static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= ANDROID_6;
    }

    /** Returns whether the device is running Android 5.1 or later. */
    public static boolean isAndroid5_1() {
        return Build.VERSION.SDK_INT >= ANDROID_5_1;
    }

    /** Returns whether the device is running Android 5 or later. */
    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= ANDROID_5;
    }

    /** Returns whether the device is running Android 4.4 or later. */
    public static boolean isAndroid4_4() {
        return Build.VERSION.SDK_INT >= ANDROID_4_4;
    }

    /** Returns whether the device is running Android 4.3 or later. */
    public static boolean isAndroid4_3() {
        return Build.VERSION.SDK_INT >= ANDROID_4_3;
    }
}
