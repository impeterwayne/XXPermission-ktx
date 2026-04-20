package com.hjq.permissions.permission;

/**
 * String constants for dangerous and special permission names.
 */
@SuppressWarnings("unused")
public final class PermissionNames {

    /** Read installed apps permission name. Use {@link PermissionLists#getGetInstalledAppsPermission()}. */
    public static final String GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS";

    /** Full-screen intent permission name. Use {@link PermissionLists#getUseFullScreenIntentPermission()}. */
    public static final String USE_FULL_SCREEN_INTENT = "android.permission.USE_FULL_SCREEN_INTENT";

    /** Exact alarm permission name. Use {@link PermissionLists#getScheduleExactAlarmPermission()}. */
    public static final String SCHEDULE_EXACT_ALARM = "android.permission.SCHEDULE_EXACT_ALARM";

    /** Manage media permission name. Use {@link PermissionLists#getManageMediaPermission()}. */
    public static final String MANAGE_MEDIA = "android.permission.MANAGE_MEDIA";

    /** Manage external storage permission name. Use {@link PermissionLists#getManageExternalStoragePermission()}. */
    public static final String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";

    /** Install unknown apps permission name. Use {@link PermissionLists#getRequestInstallPackagesPermission()}. */
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";

    /** Picture-in-picture permission name. Use {@link PermissionLists#getPictureInPicturePermission()}. */
    public static final String PICTURE_IN_PICTURE = "android.permission.PICTURE_IN_PICTURE";

    /** System alert window permission name. Use {@link PermissionLists#getSystemAlertWindowPermission()}. */
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

    /** Write system settings permission name. Use {@link PermissionLists#getWriteSettingsPermission()}. */
    public static final String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";

    /** Ignore battery optimizations permission name. Use {@link PermissionLists#getRequestIgnoreBatteryOptimizationsPermission()}. */
    public static final String REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS";

    /** Do Not Disturb permission name. Use {@link PermissionLists#getAccessNotificationPolicyPermission()}. */
    public static final String ACCESS_NOTIFICATION_POLICY = "android.permission.ACCESS_NOTIFICATION_POLICY";

    /** Usage access permission name. Use {@link PermissionLists#getPackageUsageStatsPermission()}. */
    public static final String PACKAGE_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";

    /** Notification listener permission name. Use {@link PermissionLists#getBindNotificationListenerServicePermission(Class)}. */
    public static final String BIND_NOTIFICATION_LISTENER_SERVICE = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE";

    /** VPN permission name. Use {@link PermissionLists#getBindVpnServicePermission()}. */
    public static final String BIND_VPN_SERVICE = "android.permission.BIND_VPN_SERVICE";

    /** Notification service permission name. Use {@link PermissionLists#getNotificationServicePermission(String)}. */
    public static final String NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE";

    /** Accessibility service permission name. Use {@link PermissionLists#getBindAccessibilityServicePermission(Class)}. */
    public static final String BIND_ACCESSIBILITY_SERVICE = "android.permission.BIND_ACCESSIBILITY_SERVICE";

    /** Device admin permission name. Use {@link PermissionLists#getBindDeviceAdminPermission(Class, String)}. */
    public static final String BIND_DEVICE_ADMIN = "android.permission.BIND_DEVICE_ADMIN";

    /* ------------------------------------ Decorative separator ------------------------------------ */

    /** Partial photo and video access permission name. Use {@link PermissionLists#getReadMediaVisualUserSelectedPermission()}. */
    public static final String READ_MEDIA_VISUAL_USER_SELECTED = "android.permission.READ_MEDIA_VISUAL_USER_SELECTED";

    /** Post notifications permission name. Use {@link PermissionLists#getPostNotificationsPermission()}. */
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";

    /** Nearby Wi-Fi devices permission name. Use {@link PermissionLists#getNearbyWifiDevicesPermission()}. */
    public static final String NEARBY_WIFI_DEVICES = "android.permission.NEARBY_WIFI_DEVICES";

    /** Background body sensors permission name. Use {@link PermissionLists#getBodySensorsBackgroundPermission()}. */
    public static final String BODY_SENSORS_BACKGROUND = "android.permission.BODY_SENSORS_BACKGROUND";

    /**
     * read images permission string constant, To the permission object, call {@link PermissionLists#getReadMediaImagesPermission()}
     */
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";

    /**
     * read videos permission string constant, To the permission object, call {@link PermissionLists#getReadMediaVideoPermission()}
     */
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";

    /**
     * read audio permission string constant, To the permission object, call {@link PermissionLists#getReadMediaAudioPermission()}
     */
    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";

    /**
     * Bluetooth scan permission string constant, To the permission object, call {@link PermissionLists#getBluetoothScanPermission()}
     */
    public static final String BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN";

    /**
     * Bluetooth connect permission string constant, To the permission object, call {@link PermissionLists#getBluetoothConnectPermission()}
     */
    public static final String BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT";

    /**
     * Bluetooth advertise permission string constant, To the permission object, call {@link PermissionLists#getBluetoothAdvertisePermission()}
     */
    public static final String BLUETOOTH_ADVERTISE = "android.permission.BLUETOOTH_ADVERTISE";

    /**
     * background location permission string constant, To the permission object, call {@link PermissionLists#getAccessBackgroundLocationPermission()}
     */
    public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    /**
     * activity recognition permission string constant, To the permission object, call {@link PermissionLists#getActivityRecognitionPermission()}
     */
    public static final String ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";

    /**
     * access media location permission string constant, To the permission object, call {@link PermissionLists#getAccessMediaLocationPermission()}
     */
    public static final String ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION";

    /**
     * Accept handover permission string constant, To the permission object, call {@link PermissionLists#getAcceptHandoverPermission()}
     */
    public static final String ACCEPT_HANDOVER = "android.permission.ACCEPT_HANDOVER";

    /**
     * read phone numbers permission string constant, To the permission object, call {@link PermissionLists#getReadPhoneNumbersPermission()}
     */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /**
     * answer phone calls permission string constant, To the permission object, call {@link PermissionLists#getAnswerPhoneCallsPermission()}
     */
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";

    /**
     * read external storage permission string constant, To the permission object, call {@link PermissionLists#getReadExternalStoragePermission()}
     */
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    /**
     * write external storage permission string constant, To the permission object, call {@link PermissionLists#getWriteExternalStoragePermission()}
     */
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * camera permission string constant, To the permission object, call {@link PermissionLists#getCameraPermission()}
     */
    public static final String CAMERA = "android.permission.CAMERA";

    /**
     * microphone permission string constant, To the permission object, call {@link PermissionLists#getRecordAudioPermission()}
     */
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    /**
     * precise location permission string constant, To the permission object, call {@link PermissionLists#getAccessFineLocationPermission()}
     */
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";

    /**
     * approximate location permission string constant, To the permission object, call {@link PermissionLists#getAccessCoarseLocationPermission()}
     */
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    /**
     * read contacts permission string constant, To the permission object, call {@link PermissionLists#getReadContactsPermission()}
     */
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";

    /**
     * write contacts permission string constant, To the permission object, call {@link PermissionLists#getWriteContactsPermission()}
     */
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";

    /**
     * get accounts permission string constant, To the permission object, call {@link PermissionLists#getGetAccountsPermission()}
     */
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    /**
     * read calendar permission string constant, To the permission object, call {@link PermissionLists#getReadCalendarPermission()}
     */
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";

    /**
     * write calendar permission string constant, To the permission object, call {@link PermissionLists#getWriteCalendarPermission()}
     */
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";

    /**
     * read phone state permission string constant, To the permission object, call {@link PermissionLists#getReadPhoneStatePermission()}
     */
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";

    /**
     * call phone permission string constant, To the permission object, call {@link PermissionLists#getCallPhonePermission()}
     */
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";

    /**
     * read call log permission string constant, To the permission object, call {@link PermissionLists#getReadCallLogPermission()}
     */
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";

    /**
     * write call log permission string constant, To the permission object, call {@link PermissionLists#getWriteCallLogPermission()}
     */
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";

    /**
     * add voicemail permission string constant, To the permission object, call {@link PermissionLists#getAddVoicemailPermission()}
     */
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";

    /**
     * use SIP permission string constant, To the permission object, call {@link PermissionLists#getUseSipPermission()}
     */
    public static final String USE_SIP = "android.permission.USE_SIP";

    /**
     * process outgoing calls permission string constant, To the permission object, call {@link PermissionLists#getProcessOutgoingCallsPermission()}
     */
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";

    /**
     * body sensors permission string constant, To the permission object, call {@link PermissionLists#getBodySensorsPermission()}
     */
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";

    /**
     * send SMS permission string constant, To the permission object, call {@link PermissionLists#getSendSmsPermission()}
     */
    public static final String SEND_SMS = "android.permission.SEND_SMS";

    /**
     * receive SMS permission string constant, To the permission object, call {@link PermissionLists#getReceiveSmsPermission()}
     */
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";

    /**
     * read SMS permission string constant, To the permission object, call {@link PermissionLists#getReadSmsPermission()} ()}
     */
    public static final String READ_SMS = "android.permission.READ_SMS";

    /**
     * receive WAP push permission string constant, To the permission object, call {@link PermissionLists#getReceiveWapPushPermission()}
     */
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";

    /**
     * receive MMS permission string constant, To the permission object, call {@link PermissionLists#getReceiveMmsPermission()}
     */
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";

    /* ------------------------------------ Decorative separator ------------------------------------ */

    /**
     * Read health data in the background permission, for any type, To the permission object, call {@link PermissionLists#getReadHealthDataInBackgroundPermission()}
     */
    public static final String READ_HEALTH_DATA_IN_BACKGROUND = "android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND";

    /**
     * Read historical health data permission, for any type, To the permission object, call {@link PermissionLists#getReadHealthDataHistoryPermission()}
     */
    public static final String READ_HEALTH_DATA_HISTORY = "android.permission.health.READ_HEALTH_DATA_HISTORY";

    /**
     * read active calories burned permission, To the permission object, call {@link PermissionLists#getReadActiveCaloriesBurnedPermission()}
     */
    public static final String READ_ACTIVE_CALORIES_BURNED = "android.permission.health.READ_ACTIVE_CALORIES_BURNED";

    /**
     * write active calories burned permission, To the permission object, call {@link PermissionLists#getWriteActiveCaloriesBurnedPermission()}
     */
    public static final String WRITE_ACTIVE_CALORIES_BURNED = "android.permission.health.WRITE_ACTIVE_CALORIES_BURNED";

    /**
     * read activity intensity permission, To the permission object, call {@link PermissionLists#getReadActivityIntensityPermission()}
     */
    public static final String READ_ACTIVITY_INTENSITY = "android.permission.health.READ_ACTIVITY_INTENSITY";

    /**
     * write activity intensity permission, To the permission object, call {@link PermissionLists#getWriteActivityIntensityPermission()}
     */
    public static final String WRITE_ACTIVITY_INTENSITY = "android.permission.health.WRITE_ACTIVITY_INTENSITY";

    /**
     * read basal body temperature permission, To the permission object, call {@link PermissionLists#getReadBasalBodyTemperaturePermission()}
     */
    public static final String READ_BASAL_BODY_TEMPERATURE = "android.permission.health.READ_BASAL_BODY_TEMPERATURE";

    /**
     * write basal body temperature permission, To the permission object, call {@link PermissionLists#getWriteBasalBodyTemperaturePermission()}
     */
    public static final String WRITE_BASAL_BODY_TEMPERATURE = "android.permission.health.WRITE_BASAL_BODY_TEMPERATURE";

    /**
     * read basal metabolic rate permission, To the permission object, call {@link PermissionLists#getReadBasalMetabolicRatePermission()}
     */
    public static final String READ_BASAL_METABOLIC_RATE = "android.permission.health.READ_BASAL_METABOLIC_RATE";

    /**
     * write basal metabolic rate permission, To the permission object, call {@link PermissionLists#getWriteBasalMetabolicRatePermission()}
     */
    public static final String WRITE_BASAL_METABOLIC_RATE = "android.permission.health.WRITE_BASAL_METABOLIC_RATE";

    /**
     * read blood glucose permission, To the permission object, call {@link PermissionLists#getReadBloodGlucosePermission()}
     */
    public static final String READ_BLOOD_GLUCOSE = "android.permission.health.READ_BLOOD_GLUCOSE";

    /**
     * write blood glucose permission, To the permission object, call {@link PermissionLists#getWriteBloodGlucosePermission()}
     */
    public static final String WRITE_BLOOD_GLUCOSE = "android.permission.health.WRITE_BLOOD_GLUCOSE";

    /**
     * read blood pressure permission, To the permission object, call {@link PermissionLists#getReadBloodPressurePermission()}
     */
    public static final String READ_BLOOD_PRESSURE = "android.permission.health.READ_BLOOD_PRESSURE";

    /**
     * write blood pressure permission, To the permission object, call {@link PermissionLists#getWriteBloodPressurePermission()}
     */
    public static final String WRITE_BLOOD_PRESSURE = "android.permission.health.WRITE_BLOOD_PRESSURE";

    /**
     * read body fat permission, To the permission object, call {@link PermissionLists#getReadBodyFatPermission()}
     */
    public static final String READ_BODY_FAT = "android.permission.health.READ_BODY_FAT";

    /**
     * write body fat permission, To the permission object, call {@link PermissionLists#getWriteBodyFatPermission()}
     */
    public static final String WRITE_BODY_FAT = "android.permission.health.WRITE_BODY_FAT";

    /**
     * read body temperature permission, To the permission object, call {@link PermissionLists#getReadBodyTemperaturePermission()}
     */
    public static final String READ_BODY_TEMPERATURE = "android.permission.health.READ_BODY_TEMPERATURE";

    /**
     * write body temperature permission, To the permission object, call {@link PermissionLists#getWriteBodyTemperaturePermission()}
     */
    public static final String WRITE_BODY_TEMPERATURE = "android.permission.health.WRITE_BODY_TEMPERATURE";

    /**
     * read body water mass permission, To the permission object, call {@link PermissionLists#getReadBodyWaterMassPermission()}
     */
    public static final String READ_BODY_WATER_MASS = "android.permission.health.READ_BODY_WATER_MASS";

    /**
     * write body water mass permission, To the permission object, call {@link PermissionLists#getWriteBodyWaterMassPermission()}
     */
    public static final String WRITE_BODY_WATER_MASS = "android.permission.health.WRITE_BODY_WATER_MASS";

    /**
     * read bone mass permission, To the permission object, call {@link PermissionLists#getReadBoneMassPermission()}
     */
    public static final String READ_BONE_MASS = "android.permission.health.READ_BONE_MASS";

    /**
     * write bone mass permission, To the permission object, call {@link PermissionLists#getWriteBoneMassPermission()}
     */
    public static final String WRITE_BONE_MASS = "android.permission.health.WRITE_BONE_MASS";

    /**
     * read cervical mucus permission, To the permission object, call {@link PermissionLists#getReadCervicalMucusPermission()}
     */
    public static final String READ_CERVICAL_MUCUS = "android.permission.health.READ_CERVICAL_MUCUS";

    /**
     * write cervical mucus permission, To the permission object, call {@link PermissionLists#getWriteCervicalMucusPermission()}
     */
    public static final String WRITE_CERVICAL_MUCUS = "android.permission.health.WRITE_CERVICAL_MUCUS";

    /**
     * read distance permission, To the permission object, call {@link PermissionLists#getReadDistancePermission()}
     */
    public static final String READ_DISTANCE = "android.permission.health.READ_DISTANCE";

    /**
     * write distance permission, To the permission object, call {@link PermissionLists#getWriteDistancePermission()}
     */
    public static final String WRITE_DISTANCE = "android.permission.health.WRITE_DISTANCE";

    /**
     * read elevation gained permission, To the permission object, call {@link PermissionLists#getReadElevationGainedPermission()}
     */
    public static final String READ_ELEVATION_GAINED = "android.permission.health.READ_ELEVATION_GAINED";

    /**
     * write elevation gained permission, To the permission object, call {@link PermissionLists#getWriteElevationGainedPermission()}
     */
    public static final String WRITE_ELEVATION_GAINED = "android.permission.health.WRITE_ELEVATION_GAINED";

    /**
     * read exercise permission, To the permission object, call {@link PermissionLists#getReadExercisePermission()}
     */
    public static final String READ_EXERCISE = "android.permission.health.READ_EXERCISE";

    /**
     * write exercise permission, To the permission object, call {@link PermissionLists#getWriteExercisePermission()}
     */
    public static final String WRITE_EXERCISE = "android.permission.health.WRITE_EXERCISE";

    /**
     * read exercise route permission, To the permission object, call {@link PermissionLists#getReadExerciseRoutesPermission()}
     */
    public static final String READ_EXERCISE_ROUTES = "android.permission.health.READ_EXERCISE_ROUTES";

    /**
     * write exercise route permission, To the permission object, call {@link PermissionLists#getWriteExerciseRoutePermission()}
     */
    public static final String WRITE_EXERCISE_ROUTE = "android.permission.health.WRITE_EXERCISE_ROUTE";

    /**
     * read floors climbed permission, To the permission object, call {@link PermissionLists#getReadFloorsClimbedPermission()}
     */
    public static final String READ_FLOORS_CLIMBED = "android.permission.health.READ_FLOORS_CLIMBED";

    /**
     * write floors climbed permission, To the permission object, call {@link PermissionLists#getWriteFloorsClimbedPermission()}
     */
    public static final String WRITE_FLOORS_CLIMBED = "android.permission.health.WRITE_FLOORS_CLIMBED";

    /**
     * read heart rate permission, To the permission object, call {@link PermissionLists#getReadHeartRatePermission()}
     */
    public static final String READ_HEART_RATE = "android.permission.health.READ_HEART_RATE";

    /**
     * write heart rate permission, To the permission object, call {@link PermissionLists#getWriteHeartRatePermission()}
     */
    public static final String WRITE_HEART_RATE = "android.permission.health.WRITE_HEART_RATE";

    /**
     * read heart rate variability permission, To the permission object, call {@link PermissionLists#getReadHeartRateVariabilityPermission()}
     */
    public static final String READ_HEART_RATE_VARIABILITY = "android.permission.health.READ_HEART_RATE_VARIABILITY";

    /**
     * write heart rate variability permission, To the permission object, call {@link PermissionLists#getWriteHeartRateVariabilityPermission()}
     */
    public static final String WRITE_HEART_RATE_VARIABILITY = "android.permission.health.WRITE_HEART_RATE_VARIABILITY";

    /**
     * read height permission, To the permission object, call {@link PermissionLists#getReadHeightPermission()}
     */
    public static final String READ_HEIGHT = "android.permission.health.READ_HEIGHT";

    /**
     * write height permission, To the permission object, call {@link PermissionLists#getWriteHeightPermission()}
     */
    public static final String WRITE_HEIGHT = "android.permission.health.WRITE_HEIGHT";

    /**
     * read hydration permission, To the permission object, call {@link PermissionLists#getReadHydrationPermission()}
     */
    public static final String READ_HYDRATION = "android.permission.health.READ_HYDRATION";

    /**
     * write hydration permission, To the permission object, call {@link PermissionLists#getWriteHydrationPermission()}
     */
    public static final String WRITE_HYDRATION = "android.permission.health.WRITE_HYDRATION";

    /**
     * read intermenstrual bleeding permission, To the permission object, call {@link PermissionLists#getReadIntermenstrualBleedingPermission()}
     */
    public static final String READ_INTERMENSTRUAL_BLEEDING = "android.permission.health.READ_INTERMENSTRUAL_BLEEDING";

    /**
     * write intermenstrual bleeding permission, To the permission object, call {@link PermissionLists#getWriteIntermenstrualBleedingPermission()}
     */
    public static final String WRITE_INTERMENSTRUAL_BLEEDING = "android.permission.health.WRITE_INTERMENSTRUAL_BLEEDING";

    /**
     * read lean body mass permission, To the permission object, call {@link PermissionLists#getReadLeanBodyMassPermission()}
     */
    public static final String READ_LEAN_BODY_MASS = "android.permission.health.READ_LEAN_BODY_MASS";

    /**
     * write lean body mass permission, To the permission object, call {@link PermissionLists#getWriteLeanBodyMassPermission()}
     */
    public static final String WRITE_LEAN_BODY_MASS = "android.permission.health.WRITE_LEAN_BODY_MASS";

    /**
     * read menstruation permission, To the permission object, call {@link PermissionLists#getReadMenstruationPermission()}
     */
    public static final String READ_MENSTRUATION = "android.permission.health.READ_MENSTRUATION";

    /**
     * write menstruation permission, To the permission object, call {@link PermissionLists#getWriteMenstruationPermission()}
     */
    public static final String WRITE_MENSTRUATION = "android.permission.health.WRITE_MENSTRUATION";

    /**
     * read mindfulness permission, To the permission object, call {@link PermissionLists#getReadMindfulnessPermission()}
     */
    public static final String READ_MINDFULNESS = "android.permission.health.READ_MINDFULNESS";

    /**
     * write mindfulness permission, To the permission object, call {@link PermissionLists#getWriteMindfulnessPermission()}
     */
    public static final String WRITE_MINDFULNESS = "android.permission.health.WRITE_MINDFULNESS";

    /**
     * read nutrition permission, To the permission object, call {@link PermissionLists#getReadNutritionPermission()}
     */
    public static final String READ_NUTRITION = "android.permission.health.READ_NUTRITION";

    /**
     * write nutrition permission, To the permission object, call {@link PermissionLists#getWriteNutritionPermission()}
     */
    public static final String WRITE_NUTRITION = "android.permission.health.WRITE_NUTRITION";

    /**
     * read ovulation test permission, To the permission object, call {@link PermissionLists#getReadOvulationTestPermission()}
     */
    public static final String READ_OVULATION_TEST = "android.permission.health.READ_OVULATION_TEST";

    /**
     * write ovulation test permission, To the permission object, call {@link PermissionLists#getWriteOvulationTestPermission()}
     */
    public static final String WRITE_OVULATION_TEST = "android.permission.health.WRITE_OVULATION_TEST";

    /**
     * read oxygen saturation permission, To the permission object, call {@link PermissionLists#getReadOxygenSaturationPermission()}
     */
    public static final String READ_OXYGEN_SATURATION = "android.permission.health.READ_OXYGEN_SATURATION";

    /**
     * write oxygen saturation permission, To the permission object, call {@link PermissionLists#getWriteOxygenSaturationPermission()}
     */
    public static final String WRITE_OXYGEN_SATURATION = "android.permission.health.WRITE_OXYGEN_SATURATION";

    /**
     * read planned exercise permission, To the permission object, call {@link PermissionLists#getReadPlannedExercisePermission()}
     */
    public static final String READ_PLANNED_EXERCISE = "android.permission.health.READ_PLANNED_EXERCISE";

    /**
     * write planned exercise permission, To the permission object, call {@link PermissionLists#getWritePlannedExercisePermission()}
     */
    public static final String WRITE_PLANNED_EXERCISE = "android.permission.health.WRITE_PLANNED_EXERCISE";

    /**
     * read power permission, To the permission object, call {@link PermissionLists#getReadPowerPermission()}
     */
    public static final String READ_POWER = "android.permission.health.READ_POWER";

    /**
     * write power permission, To the permission object, call {@link PermissionLists#getWritePowerPermission()}
     */
    public static final String WRITE_POWER = "android.permission.health.WRITE_POWER";

    /**
     * read respiratory rate permission, To the permission object, call {@link PermissionLists#getReadRespiratoryRatePermission()}
     */
    public static final String READ_RESPIRATORY_RATE = "android.permission.health.READ_RESPIRATORY_RATE";

    /**
     * write respiratory rate permission, To the permission object, call {@link PermissionLists#getWriteRespiratoryRatePermission()}
     */
    public static final String WRITE_RESPIRATORY_RATE = "android.permission.health.WRITE_RESPIRATORY_RATE";

    /**
     * read resting heart rate permission, To the permission object, call {@link PermissionLists#getReadRestingHeartRatePermission()}
     */
    public static final String READ_RESTING_HEART_RATE = "android.permission.health.READ_RESTING_HEART_RATE";

    /**
     * write resting heart rate permission, To the permission object, call {@link PermissionLists#getWriteRestingHeartRatePermission()}
     */
    public static final String WRITE_RESTING_HEART_RATE = "android.permission.health.WRITE_RESTING_HEART_RATE";

    /**
     * read sexual activity permission, To the permission object, call {@link PermissionLists#getReadSexualActivityPermission()}
     */
    public static final String READ_SEXUAL_ACTIVITY = "android.permission.health.READ_SEXUAL_ACTIVITY";

    /**
     * write sexual activity permission, To the permission object, call {@link PermissionLists#getWriteSexualActivityPermission()}
     */
    public static final String WRITE_SEXUAL_ACTIVITY = "android.permission.health.WRITE_SEXUAL_ACTIVITY";

    /**
     * read skin temperature permission, To the permission object, call {@link PermissionLists#getReadSkinTemperaturePermission()}
     */
    public static final String READ_SKIN_TEMPERATURE = "android.permission.health.READ_SKIN_TEMPERATURE";

    /**
     * write skin temperature permission, To the permission object, call {@link PermissionLists#getWriteSkinTemperaturePermission()}
     */
    public static final String WRITE_SKIN_TEMPERATURE = "android.permission.health.WRITE_SKIN_TEMPERATURE";

    /**
     * read sleep permission, To the permission object, call {@link PermissionLists#getReadSleepPermission()}
     */
    public static final String READ_SLEEP = "android.permission.health.READ_SLEEP";

    /**
     * write sleep permission, To the permission object, call {@link PermissionLists#getWriteSleepPermission()}
     */
    public static final String WRITE_SLEEP = "android.permission.health.WRITE_SLEEP";

    /**
     * read speed permission, To the permission object, call {@link PermissionLists#getReadSpeedPermission()}
     */
    public static final String READ_SPEED = "android.permission.health.READ_SPEED";

    /**
     * write speed permission, To the permission object, call {@link PermissionLists#getWriteSpeedPermission()}
     */
    public static final String WRITE_SPEED = "android.permission.health.WRITE_SPEED";

    /**
     * read steps permission, To the permission object, call {@link PermissionLists#getReadStepsPermission()}
     */
    public static final String READ_STEPS = "android.permission.health.READ_STEPS";

    /**
     * write steps permission, To the permission object, call {@link PermissionLists#getWriteStepsPermission()}
     */
    public static final String WRITE_STEPS = "android.permission.health.WRITE_STEPS";

    /**
     * read total calories burned permission, To the permission object, call {@link PermissionLists#getReadTotalCaloriesBurnedPermission()}
     */
    public static final String READ_TOTAL_CALORIES_BURNED = "android.permission.health.READ_TOTAL_CALORIES_BURNED";

    /**
     * write total calories burned permission, To the permission object, call {@link PermissionLists#getWriteTotalCaloriesBurnedPermission()}
     */
    public static final String WRITE_TOTAL_CALORIES_BURNED = "android.permission.health.WRITE_TOTAL_CALORIES_BURNED";

    /**
     * read VO2 max permission, To the permission object, call {@link PermissionLists#getReadVo2MaxPermission()}
     */
    public static final String READ_VO2_MAX = "android.permission.health.READ_VO2_MAX";

    /**
     * write VO2 max permission, To the permission object, call {@link PermissionLists#getWriteVo2MaxPermission()}
     */
    public static final String WRITE_VO2_MAX = "android.permission.health.WRITE_VO2_MAX";

    /**
     * read weight permission, To the permission object, call {@link PermissionLists#getReadWeightPermission()}
     */
    public static final String READ_WEIGHT = "android.permission.health.READ_WEIGHT";

    /**
     * write weight permission, To the permission object, call {@link PermissionLists#getWriteWeightPermission()}
     */
    public static final String WRITE_WEIGHT = "android.permission.health.WRITE_WEIGHT";

    /**
     * read wheelchair pushes permission, To the permission object, call {@link PermissionLists#getReadWheelchairPushesPermission()}
     */
    public static final String READ_WHEELCHAIR_PUSHES = "android.permission.health.READ_WHEELCHAIR_PUSHES";

    /**
     * write wheelchair pushes permission, To the permission object, call {@link PermissionLists#getWriteWheelchairPushesPermission()}
     */
    public static final String WRITE_WHEELCHAIR_PUSHES = "android.permission.health.WRITE_WHEELCHAIR_PUSHES";

    /* ------------------------------------ Decorative separator ------------------------------------ */

    /**
     * read allergies and intolerances permission, To the permission object, call {@link PermissionLists#getReadMedicalDataAllergiesIntolerancesPermission()}
     */
    public static final String READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES = "android.permission.health.READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES";

    /**
     * read conditions permission, To the permission object, call {@link PermissionLists#getReadMedicalDataConditionsPermission()}
     */
    public static final String READ_MEDICAL_DATA_CONDITIONS = "android.permission.health.READ_MEDICAL_DATA_CONDITIONS";

    /**
     * Read laboratory results permission, To the permission object, call {@link PermissionLists#getReadMedicalDataLaboratoryResultsPermission()}
     */
    public static final String READ_MEDICAL_DATA_LABORATORY_RESULTS = "android.permission.health.READ_MEDICAL_DATA_LABORATORY_RESULTS";

    /**
     * read medications permission, To the permission object, call {@link PermissionLists#getReadMedicalDataMedicationsPermission()}
     */
    public static final String READ_MEDICAL_DATA_MEDICATIONS = "android.permission.health.READ_MEDICAL_DATA_MEDICATIONS";

    /**
     * read personal details permission, To the permission object, call {@link PermissionLists#getReadMedicalDataPersonalDetailsPermission()}
     */
    public static final String READ_MEDICAL_DATA_PERSONAL_DETAILS = "android.permission.health.READ_MEDICAL_DATA_PERSONAL_DETAILS";

    /**
     * read practitioner details permission, To the permission object, call {@link PermissionLists#getReadMedicalDataPractitionerDetailsPermission()}
     */
    public static final String READ_MEDICAL_DATA_PRACTITIONER_DETAILS = "android.permission.health.READ_MEDICAL_DATA_PRACTITIONER_DETAILS";

    /**
     * read pregnancy permission, To the permission object, call {@link PermissionLists#getReadMedicalDataPregnancyPermission()}
     */
    public static final String READ_MEDICAL_DATA_PREGNANCY = "android.permission.health.READ_MEDICAL_DATA_PREGNANCY";

    /**
     * read procedures permission, To the permission object, call {@link PermissionLists#getReadMedicalDataProceduresPermission()}
     */
    public static final String READ_MEDICAL_DATA_PROCEDURES = "android.permission.health.READ_MEDICAL_DATA_PROCEDURES";

    /**
     * read social history permission, To the permission object, call {@link PermissionLists#getReadMedicalDataSocialHistoryPermission()}
     */
    public static final String READ_MEDICAL_DATA_SOCIAL_HISTORY = "android.permission.health.READ_MEDICAL_DATA_SOCIAL_HISTORY";

    /**
     * read vaccines permission, To the permission object, call {@link PermissionLists#getReadMedicalDataVaccinesPermission()}
     */
    public static final String READ_MEDICAL_DATA_VACCINES = "android.permission.health.READ_MEDICAL_DATA_VACCINES";

    /**
     * Read visits permission, including data such as location, appointment time, and the healthcare organization name, To the permission object, call {@link PermissionLists#getReadMedicalDataVisitsPermission()}
     */
    public static final String READ_MEDICAL_DATA_VISITS = "android.permission.health.READ_MEDICAL_DATA_VISITS";

    /**
     * read vital signs permission, To the permission object, call {@link PermissionLists#getReadMedicalDataVitalSignsPermission()}
     */
    public static final String READ_MEDICAL_DATA_VITAL_SIGNS = "android.permission.health.READ_MEDICAL_DATA_VITAL_SIGNS";

    /**
     * write medical data permission, To the permission object, call {@link PermissionLists#getWriteMedicalDataPermission()}
     */
    public static final String WRITE_MEDICAL_DATA = "android.permission.health.WRITE_MEDICAL_DATA";

    /** Private constructor */
    private PermissionNames() {
        // default implementation ignored
    }
}
