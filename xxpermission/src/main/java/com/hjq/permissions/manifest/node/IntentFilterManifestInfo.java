package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the manifest information of an {@code intent-filter} node
 * defined in the AndroidManifest.xml.
 *
 * <p>This class is used for parsing and holding the intent filter details,
 * including actions and categories associated with a component.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2025/07/15
 */
public final class IntentFilterManifestInfo {

    /**
     * The list of actions declared inside the {@code intent-filter}.
     *
     * <p>For example: {@code <action android:name="android.intent.action.MAIN" />}</p>
     */
    @NonNull
    public final List<String> actionList = new ArrayList<>();

    /**
     * The list of categories declared inside the {@code intent-filter}.
     *
     * <p>For example: {@code <category android:name="android.intent.category.LAUNCHER" />}</p>
     */
    @NonNull
    public final List<String> categoryList = new ArrayList<>();
}
