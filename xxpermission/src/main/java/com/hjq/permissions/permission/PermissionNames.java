package com.hjq.permissions.permission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Constant set of names for dangerous and special permissions
 */
@SuppressWarnings("unused")
public final class PermissionNames {

    /**
     * Constant string for the "read installed apps" permission.
     * For a permission object, call {@link PermissionLists#getGetInstalledAppsPermission()}.
     */
    public static final String GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS";

    /**
     * Constant string for the full-screen notification permission.
     * For a permission object, call {@link PermissionLists#getUseFullScreenIntentPermission()}.
     */
    public static final String USE_FULL_SCREEN_INTENT = "android.permission.USE_FULL_SCREEN_INTENT";

    /**
     * Constant string for the alarm permission.
     * For a permission object, call {@link PermissionLists#getScheduleExactAlarmPermission()}.
     */
    public static final String SCHEDULE_EXACT_ALARM = "android.permission.SCHEDULE_EXACT_ALARM";

    /**
     * Constant string for the "manage all files" permission.
     * For a permission object, call {@link PermissionLists#getManageExternalStoragePermission()}.
     */
    public static final String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";

    /**
     * Constant string for the "install apps" permission.
     * For a permission object, call {@link PermissionLists#getRequestInstallPackagesPermission()}.
     */
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";

    /**
     * Constant string for the picture-in-picture permission.
     * For a permission object, call {@link PermissionLists#getPictureInPicturePermission()}.
     */
    public static final String PICTURE_IN_PICTURE = "android.permission.PICTURE_IN_PICTURE";

    /**
     * Constant string for the "draw over other apps" (overlay) permission.
     * For a permission object, call {@link PermissionLists#getSystemAlertWindowPermission()}.
     */
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

    /**
     * Constant string for the "write system settings" permission.
     * For a permission object, call {@link PermissionLists#getWriteSettingsPermission()}.
     */
    public static final String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";

    /**
     * Constant string for the "request ignore battery optimizations" permission.
     * For a permission object, call {@link PermissionLists#getRequestIgnoreBatteryOptimizationsPermission()}.
     */
    public static final String REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS";

    /**
     * Constant string for the Do Not Disturb access permission.
     * For a permission object, call {@link PermissionLists#getAccessNotificationPolicyPermission()}.
     */
    public static final String ACCESS_NOTIFICATION_POLICY = "android.permission.ACCESS_NOTIFICATION_POLICY";

    /**
     * Constant string for the "package usage stats" permission.
     * For a permission object, call {@link PermissionLists#getPackageUsageStatsPermission()}.
     */
    public static final String PACKAGE_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";

    /**
     * Constant string for the notification listener service permission.
     * For a permission object, call {@link PermissionLists#getBindNotificationListenerServicePermission(Class)}.
     */
    public static final String BIND_NOTIFICATION_LISTENER_SERVICE = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE";

    /**
     * Constant string for the VPN binding permission.
     * For a permission object, call {@link PermissionLists#getBindVpnServicePermission()}.
     */
    public static final String BIND_VPN_SERVICE = "android.permission.BIND_VPN_SERVICE";

    /**
     * Constant string for the notifications permission.
     * For a permission object, call {@link PermissionLists#getNotificationServicePermission(String)}.
     */
    public static final String NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE";

    /**
     * Constant string for the accessibility service binding permission.
     * For a permission object, call {@link PermissionLists#getBindAccessibilityServicePermission(Class)}.
     */
    public static final String BIND_ACCESSIBILITY_SERVICE = "android.permission.BIND_ACCESSIBILITY_SERVICE";

    /**
     * Constant string for the device admin binding permission.
     * For a permission object, call {@link PermissionLists#getBindDeviceAdminPermission(Class, String)}.
     */
    public static final String BIND_DEVICE_ADMIN = "android.permission.BIND_DEVICE_ADMIN";

    /* ------------------------------------ Fancy separator line ------------------------------------ */

    /**
     * Constant string for "access to selected photos and videos".
     * For a permission object, call {@link PermissionLists#getReadMediaVisualUserSelectedPermission()}.
     */
    public static final String READ_MEDIA_VISUAL_USER_SELECTED = "android.permission.READ_MEDIA_VISUAL_USER_SELECTED";

    /**
     * Constant string for the "post notifications" permission.
     * For a permission object, call {@link PermissionLists#getPostNotificationsPermission()}.
     */
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";

    /**
     * Constant string for the Wi-Fi nearby devices permission.
     * For a permission object, call {@link PermissionLists#getNearbyWifiDevicesPermission()}.
     */
    public static final String NEARBY_WIFI_DEVICES = "android.permission.NEARBY_WIFI_DEVICES";

    /**
     * Constant string for the background sensors permission.
     * For a permission object, call {@link PermissionLists#getBodySensorsBackgroundPermission()}.
     */
    public static final String BODY_SENSORS_BACKGROUND = "android.permission.BODY_SENSORS_BACKGROUND";

    /**
     * Constant string for the "read images" permission.
     * For a permission object, call {@link PermissionLists#getReadMediaImagesPermission()}.
     */
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";

    /**
     * Constant string for the "read videos" permission.
     * For a permission object, call {@link PermissionLists#getReadMediaVideoPermission()}.
     */
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";

    /**
     * Constant string for the "read audio" permission.
     * For a permission object, call {@link PermissionLists#getReadMediaAudioPermission()}.
     */
    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";

    /**
     * Constant string for Bluetooth scan.
     * For a permission object, call {@link PermissionLists#getBluetoothScanPermission()}.
     */
    public static final String BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN";

    /**
     * Constant string for Bluetooth connect.
     * For a permission object, call {@link PermissionLists#getBluetoothConnectPermission()}.
     */
    public static final String BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT";

    /**
     * Constant string for Bluetooth advertise.
     * For a permission object, call {@link PermissionLists#getBluetoothAdvertisePermission()}.
     */
    public static final String BLUETOOTH_ADVERTISE = "android.permission.BLUETOOTH_ADVERTISE";

    /**
     * Constant string for background location access.
     * For a permission object, call {@link PermissionLists#getAccessBackgroundLocationPermission()}.
     */
    public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    /**
     * Constant string for activity recognition.
     * For a permission object, call {@link PermissionLists#getActivityRecognitionPermission()}.
     */
    public static final String ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";

    /**
     * Constant string for accessing media location info.
     * For a permission object, call {@link PermissionLists#getAccessMediaLocationPermission()}.
     */
    public static final String ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION";

    /**
     * Constant string for accepting call handover.
     * For a permission object, call {@link PermissionLists#getAcceptHandoverPermission()}.
     */
    public static final String ACCEPT_HANDOVER = "android.permission.ACCEPT_HANDOVER";

    /**
     * Constant string for reading phone numbers.
     * For a permission object, call {@link PermissionLists#getReadPhoneNumbersPermission()}.
     */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /**
     * Constant string for answering phone calls.
     * For a permission object, call {@link PermissionLists#getAnswerPhoneCallsPermission()}.
     */
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";

    /**
     * Constant string for reading external storage.
     * For a permission object, call {@link PermissionLists#getReadExternalStoragePermission()}.
     */
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    /**
     * Constant string for writing external storage.
     * For a permission object, call {@link PermissionLists#getWriteExternalStoragePermission()}.
     */
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * Constant string for the camera permission.
     * For a permission object, call {@link PermissionLists#getCameraPermission()}.
     */
    public static final String CAMERA = "android.permission.CAMERA";

    /**
     * Constant string for the microphone (record audio) permission.
     * For a permission object, call {@link PermissionLists#getRecordAudioPermission()}.
     */
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    /**
     * Constant string for precise location.
     * For a permission object, call {@link PermissionLists#getAccessFineLocationPermission()}.
     */
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";

    /**
     * Constant string for coarse location.
     * For a permission object, call {@link PermissionLists#getAccessCoarseLocationPermission()}.
     */
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    /**
     * Constant string for reading contacts.
     * For a permission object, call {@link PermissionLists#getReadContactsPermission()}.
     */
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";

    /**
     * Constant string for writing contacts.
     * For a permission object, call {@link PermissionLists#getWriteContactsPermission()}.
     */
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";

    /**
     * Constant string for accessing account list.
     * For a permission object, call {@link PermissionLists#getGetAccountsPermission()}.
     */
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    /**
     * Constant string for reading calendar.
     * For a permission object, call {@link PermissionLists#getReadCalendarPermission()}.
     */
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";

    /**
     * Constant string for writing calendar.
     * For a permission object, call {@link PermissionLists#getWriteCalendarPermission()}.
     */
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";

    /**
     * Constant string for reading phone state.
     * For a permission object, call {@link PermissionLists#getReadPhoneStatePermission()}.
     */
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";

    /**
     * Constant string for making phone calls.
     * For a permission object, call {@link PermissionLists#getCallPhonePermission()}.
     */
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";

    /**
     * Constant string for reading call logs.
     * For a permission object, call {@link PermissionLists#getReadCallLogPermission()}.
     */
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";

    /**
     * Constant string for writing call logs.
     * For a permission object, call {@link PermissionLists#getWriteCallLogPermission()}.
     */
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";

    /**
     * Constant string for adding voicemail.
     * For a permission object, call {@link PermissionLists#getAddVoicemailPermission()}.
     */
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";

    /**
     * Constant string for using SIP.
     * For a permission object, call {@link PermissionLists#getUseSipPermission()}.
     */
    public static final String USE_SIP = "android.permission.USE_SIP";

    /**
     * Constant string for processing outgoing calls.
     * For a permission object, call {@link PermissionLists#getProcessOutgoingCallsPermission()}.
     */
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";

    /**
     * Constant string for body sensors.
     * For a permission object, call {@link PermissionLists#getBodySensorsPermission()}.
     */
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";

    /**
     * Constant string for sending SMS.
     * For a permission object, call {@link PermissionLists#getSendSmsPermission()}.
     */
    public static final String SEND_SMS = "android.permission.SEND_SMS";

    /**
     * Constant string for receiving SMS.
     * For a permission object, call {@link PermissionLists#getReceiveSmsPermission()}.
     */
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";

    /**
     * Constant string for reading SMS.
     * For a permission object, call {@link PermissionLists#getReadSmsPermission()}.
     */
    public static final String READ_SMS = "android.permission.READ_SMS";

    /**
     * Constant string for receiving WAP push messages.
     * For a permission object, call {@link PermissionLists#getReceiveWapPushPermission()}.
     */
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";

    /**
     * Constant string for receiving MMS.
     * For a permission object, call {@link PermissionLists#getReceiveMmsPermission()}.
     */
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";

    /* ------------------------------------ Fancy separator line ------------------------------------ */

    /**
     * Constant string for reading health data in the background (any type).
     * For a permission object, call {@link PermissionLists#getReadHealthDataInBackgroundPermission()}.
     */
    public static final String READ_HEALTH_DATA_IN_BACKGROUND = "android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND";

    /**
     * Constant string for reading historical health data (any type).
     * For a permission object, call {@link PermissionLists#getReadHealthDataHistoryPermission()}.
     */
    public static final String READ_HEALTH_DATA_HISTORY = "android.permission.health.READ_HEALTH_DATA_HISTORY";

    /**
     * Constant string for reading active calories burned.
     * For a permission object, call {@link PermissionLists#getReadActiveCaloriesBurnedPermission()}.
     */
    public static final String READ_ACTIVE_CALORIES_BURNED = "android.permission.health.READ_ACTIVE_CALORIES_BURNED";

    /**
     * Constant string for writing active calories burned.
     * For a permission object, call {@link PermissionLists#getWriteActiveCaloriesBurnedPermission()}.
     */
    public static final String WRITE_ACTIVE_CALORIES_BURNED = "android.permission.health.WRITE_ACTIVE_CALORIES_BURNED";

    /**
     * Constant string for reading activity intensity.
     * For a permission object, call {@link PermissionLists#getReadActivityIntensityPermission()}.
     */
    public static final String READ_ACTIVITY_INTENSITY = "android.permission.health.READ_ACTIVITY_INTENSITY";

    /**
     * Constant string for writing activity intensity.
     * For a permission object, call {@link PermissionLists#getWriteActivityIntensityPermission()}.
     */
    public static final String WRITE_ACTIVITY_INTENSITY = "android.permission.health.WRITE_ACTIVITY_INTENSITY";

    /**
     * Constant string for reading basal body temperature.
     * For a permission object, call {@link PermissionLists#getReadBasalBodyTemperaturePermission()}.
     */
    public static final String READ_BASAL_BODY_TEMPERATURE = "android.permission.health.READ_BASAL_BODY_TEMPERATURE";

    /**
     * Constant string for writing basal body temperature.
     * For a permission object, call {@link PermissionLists#getWriteBasalBodyTemperaturePermission()}.
     */
    public static final String WRITE_BASAL_BODY_TEMPERATURE = "android.permission.health.WRITE_BASAL_BODY_TEMPERATURE";

    /**
     * Constant string for reading basal metabolic rate.
     * For a permission object, call {@link PermissionLists#getReadBasalMetabolicRatePermission()}.
     */
    public static final String READ_BASAL_METABOLIC_RATE = "android.permission.health.READ_BASAL_METABOLIC_RATE";

    /**
     * Constant string for writing basal metabolic rate.
     * For a permission object, call {@link PermissionLists#getWriteBasalMetabolicRatePermission()}.
     */
    public static final String WRITE_BASAL_METABOLIC_RATE = "android.permission.health.WRITE_BASAL_METABOLIC_RATE";

    /**
     * Constant string for reading blood glucose.
     * For a permission object, call {@link PermissionLists#getReadBloodGlucosePermission()}.
     */
    public static final String READ_BLOOD_GLUCOSE = "android.permission.health.READ_BLOOD_GLUCOSE";

    /**
     * Constant string for writing blood glucose.
     * For a permission object, call {@link PermissionLists#getWriteBloodGlucosePermission()}.
     */
    public static final String WRITE_BLOOD_GLUCOSE = "android.permission.health.WRITE_BLOOD_GLUCOSE";

    /**
     * Constant string for reading blood pressure.
     * For a permission object, call {@link PermissionLists#getReadBloodPressurePermission()}.
     */
    public static final String READ_BLOOD_PRESSURE = "android.permission.health.READ_BLOOD_PRESSURE";

    /**
     * Constant string for writing blood pressure.
     * For a permission object, call {@link PermissionLists#getWriteBloodPressurePermission()}.
     */
    public static final String WRITE_BLOOD_PRESSURE = "android.permission.health.WRITE_BLOOD_PRESSURE";

    /**
     * Constant string for reading body fat.
     * For a permission object, call {@link PermissionLists#getReadBodyFatPermission()}.
     */
    public static final String READ_BODY_FAT = "android.permission.health.READ_BODY_FAT";

    /**
     * Constant string for writing body fat.
     * For a permission object, call {@link PermissionLists#getWriteBodyFatPermission()}.
     */
    public static final String WRITE_BODY_FAT = "android.permission.health.WRITE_BODY_FAT";

    /**
     * Constant string for reading body temperature.
     * For a permission object, call {@link PermissionLists#getReadBodyTemperaturePermission()}.
     */
    public static final String READ_BODY_TEMPERATURE = "android.permission.health.READ_BODY_TEMPERATURE";

    /**
     * Constant string for writing body temperature.
     * For a permission object, call {@link PermissionLists#getWriteBodyTemperaturePermission()}.
     */
    public static final String WRITE_BODY_TEMPERATURE = "android.permission.health.WRITE_BODY_TEMPERATURE";

    /**
     * Constant string for reading body water mass.
     * For a permission object, call {@link PermissionLists#getReadBodyWaterMassPermission()}.
     */
    public static final String READ_BODY_WATER_MASS = "android.permission.health.READ_BODY_WATER_MASS";

    /**
     * Constant string for writing body water mass.
     * For a permission object, call {@link PermissionLists#getWriteBodyWaterMassPermission()}.
     */
    public static final String WRITE_BODY_WATER_MASS = "android.permission.health.WRITE_BODY_WATER_MASS";

    /**
     * Constant string for reading bone mass.
     * For a permission object, call {@link PermissionLists#getReadBoneMassPermission()}.
     */
    public static final String READ_BONE_MASS = "android.permission.health.READ_BONE_MASS";

    /**
     * Constant string for writing bone mass.
     * For a permission object, call {@link PermissionLists#getWriteBoneMassPermission()}.
     */
    public static final String WRITE_BONE_MASS = "android.permission.health.WRITE_BONE_MASS";

    /**
     * Constant string for reading cervical mucus.
     * For a permission object, call {@link PermissionLists#getReadCervicalMucusPermission()}.
     */
    public static final String READ_CERVICAL_MUCUS = "android.permission.health.READ_CERVICAL_MUCUS";

    /**
     * Constant string for writing cervical mucus.
     * For a permission object, call {@link PermissionLists#getWriteCervicalMucusPermission()}.
     */
    public static final String WRITE_CERVICAL_MUCUS = "android.permission.health.WRITE_CERVICAL_MUCUS";

    /**
     * Constant string for reading distance.
     * For a permission object, call {@link PermissionLists#getReadDistancePermission()}.
     */
    public static final String READ_DISTANCE = "android.permission.health.READ_DISTANCE";

    /**
     * Constant string for writing distance.
     * For a permission object, call {@link PermissionLists#getWriteDistancePermission()}.
     */
    public static final String WRITE_DISTANCE = "android.permission.health.WRITE_DISTANCE";

    /**
     * Constant string for reading elevation gained.
     * For a permission object, call {@link PermissionLists#getReadElevationGainedPermission()}.
     */
    public static final String READ_ELEVATION_GAINED = "android.permission.health.READ_ELEVATION_GAINED";

    /**
     * Constant string for writing elevation gained.
     * For a permission object, call {@link PermissionLists#getWriteElevationGainedPermission()}.
     */
    public static final String WRITE_ELEVATION_GAINED = "android.permission.health.WRITE_ELEVATION_GAINED";

    /**
     * Constant string for reading exercise.
     * For a permission object, call {@link PermissionLists#getReadExercisePermission()}.
     */
    public static final String READ_EXERCISE = "android.permission.health.READ_EXERCISE";

    /**
     * Constant string for writing exercise.
     * For a permission object, call {@link PermissionLists#getWriteExercisePermission()}.
     */
    public static final String WRITE_EXERCISE = "android.permission.health.WRITE_EXERCISE";

    /**
     * Constant string for reading exercise routes.
     * For a permission object, call {@link PermissionLists#getReadExerciseRoutesPermission()}.
     */
    public static final String READ_EXERCISE_ROUTES = "android.permission.health.READ_EXERCISE_ROUTES";

    /**
     * Constant string for writing an exercise route.
     * For a permission object, call {@link PermissionLists#getWriteExerciseRoutePermission()}.
     */
    public static final String WRITE_EXERCISE_ROUTE = "android.permission.health.WRITE_EXERCISE_ROUTE";

    /**
     * Constant string for reading floors climbed.
     * For a permission object, call {@link PermissionLists#getReadFloorsClimbedPermission()}.
     */
    public static final String READ_FLOORS_CLIMBED = "android.permission.health.READ_FLOORS_CLIMBED";

    /**
     * Constant string for writing floors climbed.
     * For a permission object, call {@link PermissionLists#getWriteFloorsClimbedPermission()}.
     */
    public static final String WRITE_FLOORS_CLIMBED = "android.permission.health.WRITE_FLOORS_CLIMBED";

    /**
     * Constant string for reading heart rate.
     * For a permission object, call {@link PermissionLists#getReadHeartRatePermission()}.
     */
    public static final String READ_HEART_RATE = "android.permission.health.READ_HEART_RATE";

    /**
     * Constant string for writing heart rate.
     * For a permission object, call {@link PermissionLists#getWriteHeartRatePermission()}.
     */
    public static final String WRITE_HEART_RATE = "android.permission.health.WRITE_HEART_RATE";

    /**
     * Constant string for reading heart rate variability.
     * For a permission object, call {@link PermissionLists#getReadHeartRateVariabilityPermission()}.
     */
    public static final String READ_HEART_RATE_VARIABILITY = "android.permission.health.READ_HEART_RATE_VARIABILITY";

    /**
     * Constant string for writing heart rate variability.
     * For a permission object, call {@link PermissionLists#getWriteHeartRateVariabilityPermission()}.
     */
    public static final String WRITE_HEART_RATE_VARIABILITY = "android.permission.health.WRITE_HEART_RATE_VARIABILITY";

    /**
     * Constant string for reading height.
     * For a permission object, call {@link PermissionLists#getReadHeightPermission()}.
     */
    public static final String READ_HEIGHT = "android.permission.health.READ_HEIGHT";

    /**
     * Constant string for writing height.
     * For a permission object, call {@link PermissionLists#getWriteHeightPermission()}.
     */
    public static final String WRITE_HEIGHT = "android.permission.health.WRITE_HEIGHT";

    /**
     * Constant string for reading hydration.
     * For a permission object, call {@link PermissionLists#getReadHydrationPermission()}.
     */
    public static final String READ_HYDRATION = "android.permission.health.READ_HYDRATION";

    /**
     * Constant string for writing hydration.
     * For a permission object, call {@link PermissionLists#getWriteHydrationPermission()}.
     */
    public static final String WRITE_HYDRATION = "android.permission.health.WRITE_HYDRATION";

    /**
     * Constant string for reading intermenstrual bleeding.
     * For a permission object, call {@link PermissionLists#getReadIntermenstrualBleedingPermission()}.
     */
    public static final String READ_INTERMENSTRUAL_BLEEDING = "android.permission.health.READ_INTERMENSTRUAL_BLEEDING";

    /**
     * Constant string for writing intermenstrual bleeding.
     * For a permission object, call {@link PermissionLists#getWriteIntermenstrualBleedingPermission()}.
     */
    public static final String WRITE_INTERMENSTRUAL_BLEEDING = "android.permission.health.WRITE_INTERMENSTRUAL_BLEEDING";

    /**
     * Constant string for reading lean body mass.
     * For a permission object, call {@link PermissionLists#getReadLeanBodyMassPermission()}.
     */
    public static final String READ_LEAN_BODY_MASS = "android.permission.health.READ_LEAN_BODY_MASS";

    /**
     * Constant string for writing lean body mass.
     * For a permission object, call {@link PermissionLists#getWriteLeanBodyMassPermission()}.
     */
    public static final String WRITE_LEAN_BODY_MASS = "android.permission.health.WRITE_LEAN_BODY_MASS";

    /**
     * Constant string for reading menstruation.
     * For a permission object, call {@link PermissionLists#getReadMenstruationPermission()}.
     */
    public static final String READ_MENSTRUATION = "android.permission.health.READ_MENSTRUATION";

    /**
     * Constant string for writing menstruation.
     * For a permission object, call {@link PermissionLists#getWriteMenstruationPermission()}.
     */
    public static final String WRITE_MENSTRUATION = "android.permission.health.WRITE_MENSTRUATION";

    /**
     * Constant string for reading mindfulness.
     * For a permission object, call {@link PermissionLists#getReadMindfulnessPermission()}.
     */
    public static final String READ_MINDFULNESS = "android.permission.health.READ_MINDFULNESS";

    /**
     * Constant string for writing mindfulness.
     * For a permission object, call {@link PermissionLists#getWriteMindfulnessPermission()}.
     */
    public static final String WRITE_MINDFULNESS = "android.permission.health.WRITE_MINDFULNESS";

    /**
     * Constant string for reading nutrition data.
     * For a permission object, call {@link PermissionLists#getReadNutritionPermission()}.
     */
    public static final String READ_NUTRITION = "android.permission.health.READ_NUTRITION";

    /**
     * Constant string for writing nutrition data.
     * For a permission object, call {@link PermissionLists#getWriteNutritionPermission()}.
     */
    public static final String WRITE_NUTRITION = "android.permission.health.WRITE_NUTRITION";

    /**
     * Constant string for reading ovulation test data.
     * For a permission object, call {@link PermissionLists#getReadOvulationTestPermission()}.
     */
    public static final String READ_OVULATION_TEST = "android.permission.health.READ_OVULATION_TEST";

    /**
     * Constant string for writing ovulation test data.
     * For a permission object, call {@link PermissionLists#getWriteOvulationTestPermission()}.
     */
    public static final String WRITE_OVULATION_TEST = "android.permission.health.WRITE_OVULATION_TEST";

    /**
     * Constant string for reading oxygen saturation.
     * For a permission object, call {@link PermissionLists#getReadOxygenSaturationPermission()}.
     */
    public static final String READ_OXYGEN_SATURATION = "android.permission.health.READ_OXYGEN_SATURATION";

    /**
     * Constant string for writing oxygen saturation.
     * For a permission object, call {@link PermissionLists#getWriteOxygenSaturationPermission()}.
     */
    public static final String WRITE_OXYGEN_SATURATION = "android.permission.health.WRITE_OXYGEN_SATURATION";

    /**
     * Constant string for reading planned exercise.
     * For a permission object, call {@link PermissionLists#getReadPlannedExercisePermission()}.
     */
    public static final String READ_PLANNED_EXERCISE = "android.permission.health.READ_PLANNED_EXERCISE";

    /**
     * Constant string for writing planned exercise.
     * For a permission object, call {@link PermissionLists#getWritePlannedExercisePermission()}.
     */
    public static final String WRITE_PLANNED_EXERCISE = "android.permission.health.WRITE_PLANNED_EXERCISE";

    /**
     * Constant string for reading power (fitness) data.
     * For a permission object, call {@link PermissionLists#getReadPowerPermission()}.
     */
    public static final String READ_POWER = "android.permission.health.READ_POWER";

    /**
     * Constant string for writing power (fitness) data.
     * For a permission object, call {@link PermissionLists#getWritePowerPermission()}.
     */
    public static final String WRITE_POWER = "android.permission.health.WRITE_POWER";

    /**
     * Constant string for reading respiratory rate.
     * For a permission object, call {@link PermissionLists#getReadRespiratoryRatePermission()}.
     */
    public static final String READ_RESPIRATORY_RATE = "android.permission.health.READ_RESPIRATORY_RATE";

    /**
     * Constant string for writing respiratory rate.
     * For a permission object, call {@link PermissionLists#getWriteRespiratoryRatePermission()}.
     */
    public static final String WRITE_RESPIRATORY_RATE = "android.permission.health.WRITE_RESPIRATORY_RATE";

    /**
     * Constant string for reading resting heart rate.
     * For a permission object, call {@link PermissionLists#getReadRestingHeartRatePermission()}.
     */
    public static final String READ_RESTING_HEART_RATE = "android.permission.health.READ_RESTING_HEART_RATE";

    /**
     * Constant string for writing resting heart rate.
     * For a permission object, call {@link PermissionLists#getWriteRestingHeartRatePermission()}.
     */
    public static final String WRITE_RESTING_HEART_RATE = "android.permission.health.WRITE_RESTING_HEART_RATE";

    /**
     * Constant string for reading sexual activity.
     * For a permission object, call {@link PermissionLists#getReadSexualActivityPermission()}.
     */
    public static final String READ_SEXUAL_ACTIVITY = "android.permission.health.READ_SEXUAL_ACTIVITY";

    /**
     * Constant string for writing sexual activity.
     * For a permission object, call {@link PermissionLists#getWriteSexualActivityPermission()}.
     */
    public static final String WRITE_SEXUAL_ACTIVITY = "android.permission.health.WRITE_SEXUAL_ACTIVITY";

    /**
     * Constant string for reading skin temperature.
     * For a permission object, call {@link PermissionLists#getReadSkinTemperaturePermission()}.
     */
    public static final String READ_SKIN_TEMPERATURE = "android.permission.health.READ_SKIN_TEMPERATURE";

    /**
     * Constant string for writing skin temperature.
     * For a permission object, call {@link PermissionLists#getWriteSkinTemperaturePermission()}.
     */
    public static final String WRITE_SKIN_TEMPERATURE = "android.permission.health.WRITE_SKIN_TEMPERATURE";

    /**
     * Constant string for reading sleep.
     * For a permission object, call {@link PermissionLists#getReadSleepPermission()}.
     */
    public static final String READ_SLEEP = "android.permission.health.READ_SLEEP";

    /**
     * Constant string for writing sleep.
     * For a permission object, call {@link PermissionLists#getWriteSleepPermission()}.
     */
    public static final String WRITE_SLEEP = "android.permission.health.WRITE_SLEEP";

    /**
     * Constant string for reading speed.
     * For a permission object, call {@link PermissionLists#getReadSpeedPermission()}.
     */
    public static final String READ_SPEED = "android.permission.health.READ_SPEED";

    /**
     * Constant string for writing speed.
     * For a permission object, call {@link PermissionLists#getWriteSpeedPermission()}.
     */
    public static final String WRITE_SPEED = "android.permission.health.WRITE_SPEED";

    /**
     * Constant string for reading steps.
     * For a permission object, call {@link PermissionLists#getReadStepsPermission()}.
     */
    public static final String READ_STEPS = "android.permission.health.READ_STEPS";

    /**
     * Constant string for writing steps.
     * For a permission object, call {@link PermissionLists#getWriteStepsPermission()}.
     */
    public static final String WRITE_STEPS = "android.permission.health.WRITE_STEPS";

    /**
     * Constant string for reading total calories burned.
     * For a permission object, call {@link PermissionLists#getReadTotalCaloriesBurnedPermission()}.
     */
    public static final String READ_TOTAL_CALORIES_BURNED = "android.permission.health.READ_TOTAL_CALORIES_BURNED";

    /**
     * Constant string for writing total calories burned.
     * For a permission object, call {@link PermissionLists#getWriteTotalCaloriesBurnedPermission()}.
     */
    public static final String WRITE_TOTAL_CALORIES_BURNED = "android.permission.health.WRITE_TOTAL_CALORIES_BURNED";

    /**
     * Constant string for reading VO2 max.
     * For a permission object, call {@link PermissionLists#getReadVo2MaxPermission()}.
     */
    public static final String READ_VO2_MAX = "android.permission.health.READ_VO2_MAX";

    /**
     * Constant string for writing VO2 max.
     * For a permission object, call {@link PermissionLists#getWriteVo2MaxPermission()}.
     */
    public static final String WRITE_VO2_MAX = "android.permission.health.WRITE_VO2_MAX";

    /**
     * Constant string for reading weight.
     * For a permission object, call {@link PermissionLists#getReadWeightPermission()}.
     */
    public static final String READ_WEIGHT = "android.permission.health.READ_WEIGHT";

    /**
     * Constant string for writing weight.
     * For a permission object, call {@link PermissionLists#getWriteWeightPermission()}.
     */
    public static final String WRITE_WEIGHT = "android.permission.health.WRITE_WEIGHT";

    /**
     * Constant string for reading wheelchair pushes.
     * For a permission object, call {@link PermissionLists#getReadWheelchairPushesPermission()}.
     */
    public static final String READ_WHEELCHAIR_PUSHES = "android.permission.health.READ_WHEELCHAIR_PUSHES";

    /**
     * Constant string for writing wheelchair pushes.
     * For a permission object, call {@link PermissionLists#getWriteWheelchairPushesPermission()}.
     */
    public static final String WRITE_WHEELCHAIR_PUSHES = "android.permission.health.WRITE_WHEELCHAIR_PUSHES";

    /* ------------------------------------ Fancy separator line ------------------------------------ */

    /**
     * Constant string for reading allergy/intolerance medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataAllergiesIntolerancesPermission()}.
     */
    public static final String READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES = "android.permission.health.READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES";

    /**
     * Constant string for reading conditions medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataConditionsPermission()}.
     */
    public static final String READ_MEDICAL_DATA_CONDITIONS = "android.permission.health.READ_MEDICAL_DATA_CONDITIONS";

    /**
     * Constant string for reading laboratory results medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataLaboratoryResultsPermission()}.
     */
    public static final String READ_MEDICAL_DATA_LABORATORY_RESULTS = "android.permission.health.READ_MEDICAL_DATA_LABORATORY_RESULTS";

    /**
     * Constant string for reading medications medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataMedicationsPermission()}.
     */
    public static final String READ_MEDICAL_DATA_MEDICATIONS = "android.permission.health.READ_MEDICAL_DATA_MEDICATIONS";

    /**
     * Constant string for reading personal details medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataPersonalDetailsPermission()}.
     */
    public static final String READ_MEDICAL_DATA_PERSONAL_DETAILS = "android.permission.health.READ_MEDICAL_DATA_PERSONAL_DETAILS";

    /**
     * Constant string for reading practitioner details medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataPractitionerDetailsPermission()}.
     */
    public static final String READ_MEDICAL_DATA_PRACTITIONER_DETAILS = "android.permission.health.READ_MEDICAL_DATA_PRACTITIONER_DETAILS";

    /**
     * Constant string for reading pregnancy medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataPregnancyPermission()}.
     */
    public static final String READ_MEDICAL_DATA_PREGNANCY = "android.permission.health.READ_MEDICAL_DATA_PREGNANCY";

    /**
     * Constant string for reading procedures medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataProceduresPermission()}.
     */
    public static final String READ_MEDICAL_DATA_PROCEDURES = "android.permission.health.READ_MEDICAL_DATA_PROCEDURES";

    /**
     * Constant string for reading social history medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataSocialHistoryPermission()}.
     */
    public static final String READ_MEDICAL_DATA_SOCIAL_HISTORY = "android.permission.health.READ_MEDICAL_DATA_SOCIAL_HISTORY";

    /**
     * Constant string for reading vaccines medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataVaccinesPermission()}.
     */
    public static final String READ_MEDICAL_DATA_VACCINES = "android.permission.health.READ_MEDICAL_DATA_VACCINES";

    /**
     * Constant string for reading visit details medical data (including place, appointment time, and organization).
     * For a permission object, call {@link PermissionLists#getReadMedicalDataVisitsPermission()}.
     */
    public static final String READ_MEDICAL_DATA_VISITS = "android.permission.health.READ_MEDICAL_DATA_VISITS";

    /**
     * Constant string for reading vital signs medical data.
     * For a permission object, call {@link PermissionLists#getReadMedicalDataVitalSignsPermission()}.
     */
    public static final String READ_MEDICAL_DATA_VITAL_SIGNS = "android.permission.health.READ_MEDICAL_DATA_VITAL_SIGNS";

    /**
     * Constant string for writing (all) medical records.
     * For a permission object, call {@link PermissionLists#getWriteMedicalDataPermission()}.
     */
    public static final String WRITE_MEDICAL_DATA = "android.permission.health.WRITE_MEDICAL_DATA";

    /** Private constructor */
    private PermissionNames() {
        // default implementation ignored
    }
}
