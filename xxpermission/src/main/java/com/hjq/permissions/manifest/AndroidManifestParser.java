package com.hjq.permissions.manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.node.ActivityManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.BroadcastReceiverManifestInfo;
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.MetaDataManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.manifest.node.UsesSdkManifestInfo;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Parser for {@code AndroidManifest.xml}.
 *
 * <p>This utility provides methods to read and parse manifest information
 * directly from an APK, including activities, services, receivers,
 * application configuration, permissions, intent filters, and metadata.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/11/11
 */
public final class AndroidManifestParser {

    /** The file name of the manifest */
    private static final String ANDROID_MANIFEST_FILE_NAME = "AndroidManifest.xml";

    /** Android's XML namespace */
    private static final String ANDROID_NAMESPACE_URI = "http://schemas.android.com/apk/res/android";

    // --- Tag constants ---
    private static final String TAG_MANIFEST = "manifest";

    private static final String TAG_USES_SDK = "uses-sdk";
    private static final String TAG_USES_PERMISSION = "uses-permission";
    private static final String TAG_USES_PERMISSION_SDK_23 = "uses-permission-sdk-23";
    private static final String TAG_USES_PERMISSION_SDK_M = "uses-permission-sdk-m";

    private static final String TAG_QUERIES = "queries";

    private static final String TAG_APPLICATION = "application";
    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_ACTIVITY_ALIAS = "activity-alias";
    private static final String TAG_SERVICE = "service";
    private static final String TAG_RECEIVER = "receiver";

    private static final String TAG_INTENT_FILTER = "intent-filter";
    private static final String TAG_ACTION = "action";
    private static final String TAG_CATEGORY = "category";

    private static final String TAG_META_DATA = "meta-data";

    // --- Attribute constants ---
    private static final String ATTR_PACKAGE = "package";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_RESOURCE = "resource";
    private static final String ATTR_MAX_SDK_VERSION = "maxSdkVersion";
    private static final String ATTR_MIN_SDK_VERSION = "minSdkVersion";
    private static final String ATTR_USES_PERMISSION_FLAGS = "usesPermissionFlags";
    private static final String ATTR_REQUEST_LEGACY_EXTERNAL_STORAGE = "requestLegacyExternalStorage";
    private static final String ATTR_SUPPORTS_PICTURE_IN_PICTURE = "supportsPictureInPicture";
    private static final String ATTR_PERMISSION = "permission";

    /** Private constructor to prevent instantiation */
    private AndroidManifestParser() {
        // default implementation ignored
    }

    /**
     * Retrieve the manifest information for the current application.
     *
     * @param context the application context
     * @return {@link AndroidManifestInfo} if successfully parsed,
     *         or {@code null} if parsing fails
     */
    @Nullable
    public static AndroidManifestInfo getAndroidManifestInfo(Context context) {
        int apkPathCookie = AndroidManifestParser.findApkPathCookie(context, context.getApplicationInfo().sourceDir);
        // If cookie == 0, retrieval failed
        if (apkPathCookie == 0) {
            return null;
        }

        AndroidManifestInfo manifestInfo = null;
        try {
            manifestInfo = AndroidManifestParser.parseAndroidManifest(context, apkPathCookie);
            // If the package name does not match the current app's package, ignore it.
            // Example issue: https://github.com/getActivity/XXPermissions/issues/102
            if (!PermissionUtils.reverseEqualsString(context.getPackageName(), manifestInfo.packageName)) {
                return null;
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return manifestInfo;
    }

    /**
     * Find the APK cookie path in the {@link AssetManager}.
     * Returns {@code 0} if retrieval fails.
     *
     * <p>Why return {@code int} instead of {@link Integer}?
     * Because {@code AssetManager.findCookieForPath} also returns 0 when failed.</p>
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("PrivateApi")
    public static int findApkPathCookie(@NonNull Context context, @NonNull String apkPath) {
        AssetManager assets = context.getAssets();
        Integer cookie;

        try {
            if (PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_9 &&
                    PermissionVersion.getCurrentVersion() >= PermissionVersion.ANDROID_9 &&
                    PermissionVersion.getCurrentVersion() < PermissionVersion.ANDROID_11) {

                // Reflection trick: Works only on Android 9 and 10. Not effective on Android 11.
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod(
                        "getDeclaredMethod", String.class, Class[].class);
                metaGetDeclaredMethod.setAccessible(true);

                // Note: AssetManager.findCookieForPath was added in API 28 (Android 9).
                // Android 9 typically uses AssetManager.addAssetPath to get the cookie.
                Method findCookieForPathMethod = (Method) metaGetDeclaredMethod.invoke(AssetManager.class,
                        "findCookieForPath", new Class[]{String.class});
                if (findCookieForPathMethod != null) {
                    findCookieForPathMethod.setAccessible(true);
                    cookie = (Integer) findCookieForPathMethod.invoke(context.getAssets(), apkPath);
                    if (cookie != null) {
                        return cookie;
                    }
                }
            }

            Method addAssetPathMethod = assets.getClass().getDeclaredMethod("addAssetPath", String.class);
            cookie = (Integer) addAssetPathMethod.invoke(assets, apkPath);
            if (cookie != null) {
                return cookie;
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Parse the manifest file from the APK.
     *
     * @param context   application context
     * @param apkCookie cookie value representing the APK in AssetManager
     * @return parsed {@link AndroidManifestInfo}
     */
    @NonNull
    public static AndroidManifestInfo parseAndroidManifest(@NonNull Context context, int apkCookie)
            throws IOException, XmlPullParserException {
        AndroidManifestInfo manifestInfo = new AndroidManifestInfo();

        try (XmlResourceParser parser = context.getAssets()
                .openXmlResourceParser(apkCookie, ANDROID_MANIFEST_FILE_NAME)) {

            do {
                if (parser.getEventType() != XmlResourceParser.START_TAG) {
                    continue;
                }

                String tagName = parser.getName();

                if (PermissionUtils.equalsString(TAG_MANIFEST, tagName)) {
                    manifestInfo.packageName = parsePackageFromXml(parser);
                }

                if (PermissionUtils.equalsString(TAG_USES_SDK, tagName)) {
                    manifestInfo.usesSdkInfo = parseUsesSdkFromXml(parser);
                }

                if (PermissionUtils.equalsString(TAG_USES_PERMISSION, tagName) ||
                        PermissionUtils.equalsString(TAG_USES_PERMISSION_SDK_23, tagName) ||
                        PermissionUtils.equalsString(TAG_USES_PERMISSION_SDK_M, tagName)) {
                    manifestInfo.permissionInfoList.add(parsePermissionFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_QUERIES, tagName)) {
                    manifestInfo.queriesPackageList.add(parsePackageFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_APPLICATION, tagName)) {
                    manifestInfo.applicationInfo = parseApplicationFromXml(parser);
                }

                if (PermissionUtils.equalsString(TAG_ACTIVITY, tagName) ||
                        PermissionUtils.equalsString(TAG_ACTIVITY_ALIAS, tagName)) {
                    manifestInfo.activityInfoList.add(parseActivityFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_SERVICE, tagName)) {
                    manifestInfo.serviceInfoList.add(parseServerFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_RECEIVER, tagName)) {
                    manifestInfo.receiverInfoList.add(parseBroadcastReceiverFromXml(parser));
                }

                if (PermissionUtils.equalsString(TAG_META_DATA, tagName) && manifestInfo.applicationInfo != null) {
                    if (manifestInfo.applicationInfo.metaDataInfoList == null) {
                        manifestInfo.applicationInfo.metaDataInfoList = new ArrayList<>();
                    }
                    manifestInfo.applicationInfo.metaDataInfoList.add(parseMetaDataFromXml(parser));
                }

            } while (parser.next() != XmlResourceParser.END_DOCUMENT);
        }

        return manifestInfo;
    }

    // --- XML parsing helpers ---

    @NonNull
    private static String parsePackageFromXml(@NonNull XmlResourceParser parser) {
        String packageName = parser.getAttributeValue(null, ATTR_PACKAGE);
        return packageName != null ? packageName : "";
    }

    @NonNull
    private static UsesSdkManifestInfo parseUsesSdkFromXml(@NonNull XmlResourceParser parser) {
        UsesSdkManifestInfo usesSdkInfo = new UsesSdkManifestInfo();
        usesSdkInfo.minSdkVersion = parser.getAttributeIntValue(
                ANDROID_NAMESPACE_URI, ATTR_MIN_SDK_VERSION, 0);
        return usesSdkInfo;
    }

    @NonNull
    private static PermissionManifestInfo parsePermissionFromXml(@NonNull XmlResourceParser parser) {
        PermissionManifestInfo permissionInfo = new PermissionManifestInfo();
        permissionInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        permissionInfo.maxSdkVersion = parser.getAttributeIntValue(
                ANDROID_NAMESPACE_URI, ATTR_MAX_SDK_VERSION, PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION);
        permissionInfo.usesPermissionFlags = parser.getAttributeIntValue(
                ANDROID_NAMESPACE_URI, ATTR_USES_PERMISSION_FLAGS, 0);
        return permissionInfo;
    }

    @NonNull
    private static ApplicationManifestInfo parseApplicationFromXml(@NonNull XmlResourceParser parser) {
        ApplicationManifestInfo applicationInfo = new ApplicationManifestInfo();
        String applicationClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        applicationInfo.name = applicationClassName != null ? applicationClassName : "";
        applicationInfo.requestLegacyExternalStorage = parser.getAttributeBooleanValue(
                ANDROID_NAMESPACE_URI, ATTR_REQUEST_LEGACY_EXTERNAL_STORAGE, false);
        return applicationInfo;
    }

    @NonNull
    private static ActivityManifestInfo parseActivityFromXml(@NonNull XmlResourceParser parser)
            throws IOException, XmlPullParserException {
        ActivityManifestInfo activityInfo = new ActivityManifestInfo();
        String activityClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        activityInfo.name = activityClassName != null ? activityClassName : "";
        activityInfo.supportsPictureInPicture = parser.getAttributeBooleanValue(
                ANDROID_NAMESPACE_URI, ATTR_SUPPORTS_PICTURE_IN_PICTURE, false);

        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG &&
                    (PermissionUtils.equalsString(TAG_ACTIVITY, tagName) ||
                            PermissionUtils.equalsString(TAG_ACTIVITY_ALIAS, tagName))) {
                break;
            }

            if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                if (activityInfo.intentFilterInfoList == null) {
                    activityInfo.intentFilterInfoList = new ArrayList<>();
                }
                activityInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            } else if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_META_DATA, tagName)) {
                if (activityInfo.metaDataInfoList == null) {
                    activityInfo.metaDataInfoList = new ArrayList<>();
                }
                activityInfo.metaDataInfoList.add(parseMetaDataFromXml(parser));
            }
        }

        return activityInfo;
    }

    @NonNull
    private static ServiceManifestInfo parseServerFromXml(@NonNull XmlResourceParser parser)
            throws IOException, XmlPullParserException {
        ServiceManifestInfo serviceInfo = new ServiceManifestInfo();
        String serviceClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        serviceInfo.name = serviceClassName != null ? serviceClassName : "";
        serviceInfo.permission = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_PERMISSION);

        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG && PermissionUtils.equalsString(TAG_SERVICE, tagName)) {
                break;
            }

            if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                if (serviceInfo.intentFilterInfoList == null) {
                    serviceInfo.intentFilterInfoList = new ArrayList<>();
                }
                serviceInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            } else if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_META_DATA, tagName)) {
                if (serviceInfo.metaDataInfoList == null) {
                    serviceInfo.metaDataInfoList = new ArrayList<>();
                }
                serviceInfo.metaDataInfoList.add(parseMetaDataFromXml(parser));
            }
        }

        return serviceInfo;
    }

    @NonNull
    private static BroadcastReceiverManifestInfo parseBroadcastReceiverFromXml(@NonNull XmlResourceParser parser)
            throws IOException, XmlPullParserException {
        BroadcastReceiverManifestInfo receiverInfo = new BroadcastReceiverManifestInfo();
        String broadcastReceiverClassName = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        receiverInfo.name = broadcastReceiverClassName != null ? broadcastReceiverClassName : "";
        receiverInfo.permission = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_PERMISSION);

        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG && PermissionUtils.equalsString(TAG_RECEIVER, tagName)) {
                break;
            }

            if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                if (receiverInfo.intentFilterInfoList == null) {
                    receiverInfo.intentFilterInfoList = new ArrayList<>();
                }
                receiverInfo.intentFilterInfoList.add(parseIntentFilterFromXml(parser));
            } else if (nextTagType == XmlResourceParser.START_TAG && PermissionUtils.equalsString(TAG_META_DATA, tagName)) {
                if (receiverInfo.metaDataInfoList == null) {
                    receiverInfo.metaDataInfoList = new ArrayList<>();
                }
                receiverInfo.metaDataInfoList.add(parseMetaDataFromXml(parser));
            }
        }

        return receiverInfo;
    }

    @NonNull
    private static IntentFilterManifestInfo parseIntentFilterFromXml(@NonNull XmlResourceParser parser)
            throws IOException, XmlPullParserException {
        IntentFilterManifestInfo intentFilterInfo = new IntentFilterManifestInfo();
        while (true) {
            int nextTagType = parser.next();
            String tagName = parser.getName();
            if (nextTagType == XmlResourceParser.END_TAG && PermissionUtils.equalsString(TAG_INTENT_FILTER, tagName)) {
                break;
            }

            if (nextTagType != XmlResourceParser.START_TAG) {
                continue;
            }

            if (PermissionUtils.equalsString(TAG_ACTION, tagName)) {
                intentFilterInfo.actionList.add(parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME));
            } else if (PermissionUtils.equalsString(TAG_CATEGORY, tagName)) {
                intentFilterInfo.categoryList.add(parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME));
            }
        }
        return intentFilterInfo;
    }

    @NonNull
    private static MetaDataManifestInfo parseMetaDataFromXml(@NonNull XmlResourceParser parser)
            throws IOException, XmlPullParserException {
        MetaDataManifestInfo metaDataInfo = new MetaDataManifestInfo();
        metaDataInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME);
        metaDataInfo.value = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_VALUE);
        metaDataInfo.resource = parser.getAttributeResourceValue(ANDROID_NAMESPACE_URI, ATTR_RESOURCE, 0);
        return metaDataInfo;
    }
}
