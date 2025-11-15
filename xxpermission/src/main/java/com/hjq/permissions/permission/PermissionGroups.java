package com.hjq.permissions.permission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/14
 *    desc   : Constant set of permission group names
 */
public final class PermissionGroups {

    /**
     * Suffix name for permission groups
     */
    public static final String SUFFIX = "_group";

    /**
     * Storage permission group, includes the following permissions:
     *
     * {@link PermissionNames#READ_EXTERNAL_STORAGE}
     * {@link PermissionNames#WRITE_EXTERNAL_STORAGE}
     */
    public static final String STORAGE = "storage" + SUFFIX;

    /**
     * Calendar permission group, includes the following permissions:
     *
     * {@link PermissionNames#READ_CALENDAR}
     * {@link PermissionNames#WRITE_CALENDAR}
     */
    public static final String CALENDAR = "calendar" + SUFFIX;

    /**
     * Contacts permission group, includes the following permissions:
     *
     * {@link PermissionNames#READ_CONTACTS}
     * {@link PermissionNames#WRITE_CONTACTS}
     * {@link PermissionNames#GET_ACCOUNTS}
     */
    public static final String CONTACTS = "contacts" + SUFFIX;

    /**
     * SMS permission group, includes the following permissions:
     *
     * {@link PermissionNames#SEND_SMS}
     * {@link PermissionNames#READ_SMS}
     * {@link PermissionNames#RECEIVE_SMS}
     * {@link PermissionNames#RECEIVE_WAP_PUSH}
     * {@link PermissionNames#RECEIVE_MMS}
     */
    public static final String SMS = "sms" + SUFFIX;

    /**
     * Location permission group, includes the following permissions:
     *
     * {@link PermissionNames#ACCESS_COARSE_LOCATION}
     * {@link PermissionNames#ACCESS_FINE_LOCATION}
     * {@link PermissionNames#ACCESS_BACKGROUND_LOCATION}
     *
     * Note: In Android 12, Bluetooth-related permissions were moved to the Nearby Devices group,
     * but in Android 11 and below, they belonged to the Location group.
     * {@link PermissionNames#BLUETOOTH_SCAN}
     * {@link PermissionNames#BLUETOOTH_CONNECT}
     * {@link PermissionNames#BLUETOOTH_ADVERTISE}
     *
     * Note: In Android 13, Wi-Fi-related permissions were moved to the Nearby Devices group,
     * but in Android 12 and below, they belonged to the Location group.
     * {@link PermissionNames#NEARBY_WIFI_DEVICES}
     */
    public static final String LOCATION = "location" + SUFFIX;

    /**
     * Sensors permission group, includes the following permissions:
     *
     * {@link PermissionNames#BODY_SENSORS}
     * {@link PermissionNames#BODY_SENSORS_BACKGROUND}
     */
    public static final String SENSORS = "sensors" + SUFFIX;

    /**
     * Phone permission group, includes the following permissions:
     *
     * {@link PermissionNames#READ_PHONE_STATE}
     * {@link PermissionNames#CALL_PHONE}
     * {@link PermissionNames#ADD_VOICEMAIL}
     * {@link PermissionNames#USE_SIP}
     * {@link PermissionNames#READ_PHONE_NUMBERS}
     * {@link PermissionNames#ANSWER_PHONE_CALLS}
     * {@link PermissionNames#ACCEPT_HANDOVER}
     *
     * Note: In Android 9.0, call log read/write permissions were moved to a separate group.
     * In Android 8.1 and below, they belonged to the Phone group.
     * {@link PermissionNames#READ_CALL_LOG}
     * {@link PermissionNames#WRITE_CALL_LOG}
     * {@link PermissionNames#PROCESS_OUTGOING_CALLS}
     */
    public static final String PHONE = "phone" + SUFFIX;

    /**
     * Call log permission group (introduced in Android 9.0, previously part of Phone group),
     * includes the following permissions:
     *
     * {@link PermissionNames#READ_CALL_LOG}
     * {@link PermissionNames#WRITE_CALL_LOG}
     * {@link PermissionNames#PROCESS_OUTGOING_CALLS}
     */
    public static final String CALL_LOG = "call_log" + SUFFIX;

    /**
     * Nearby devices permission group, includes the following permissions:
     *
     * In Android 12, Bluetooth-related permissions were moved here,
     * but in Android 11 and below, they belonged to the Location group.
     * {@link PermissionNames#BLUETOOTH_SCAN}
     * {@link PermissionNames#BLUETOOTH_CONNECT}
     * {@link PermissionNames#BLUETOOTH_ADVERTISE}
     *
     * Note: In Android 13, Wi-Fi-related permissions were moved here,
     * but in Android 12 and below, they belonged to the Location group.
     * {@link PermissionNames#NEARBY_WIFI_DEVICES}
     */
    public static final String NEARBY_DEVICES = "nearby_devices" + SUFFIX;

    /**
     * Photos and videos permission group (Note: does not include audio permissions),
     * includes the following permissions:
     *
     * {@link PermissionNames#READ_MEDIA_IMAGES}
     * {@link PermissionNames#READ_MEDIA_VIDEO}
     * {@link PermissionNames#READ_MEDIA_VISUAL_USER_SELECTED}
     */
    public static final String IMAGE_AND_VIDEO_MEDIA = "image_and_video_media" + SUFFIX;

    /**
     * Health permission group, includes the following permissions:
     *
     * Background and history access:
     * {@link PermissionNames#READ_HEALTH_DATA_IN_BACKGROUND}
     * {@link PermissionNames#READ_HEALTH_DATA_HISTORY}
     *
     * Metrics (calories, activity, body data, etc.):
     * {@link PermissionNames#READ_ACTIVE_CALORIES_BURNED}
     * {@link PermissionNames#WRITE_ACTIVE_CALORIES_BURNED}
     * {@link PermissionNames#READ_ACTIVITY_INTENSITY}
     * {@link PermissionNames#WRITE_ACTIVITY_INTENSITY}
     * {@link PermissionNames#READ_BASAL_BODY_TEMPERATURE}
     * {@link PermissionNames#WRITE_BASAL_BODY_TEMPERATURE}
     * {@link PermissionNames#READ_BASAL_METABOLIC_RATE}
     * {@link PermissionNames#WRITE_BASAL_METABOLIC_RATE}
     * {@link PermissionNames#READ_BLOOD_GLUCOSE}
     * {@link PermissionNames#WRITE_BLOOD_GLUCOSE}
     * {@link PermissionNames#READ_BLOOD_PRESSURE}
     * {@link PermissionNames#WRITE_BLOOD_PRESSURE}
     * {@link PermissionNames#READ_BODY_FAT}
     * {@link PermissionNames#WRITE_BODY_FAT}
     * {@link PermissionNames#READ_BODY_TEMPERATURE}
     * {@link PermissionNames#WRITE_BODY_TEMPERATURE}
     * {@link PermissionNames#READ_BODY_WATER_MASS}
     * {@link PermissionNames#WRITE_BODY_WATER_MASS}
     * {@link PermissionNames#READ_BONE_MASS}
     * {@link PermissionNames#WRITE_BONE_MASS}
     * {@link PermissionNames#READ_CERVICAL_MUCUS}
     * {@link PermissionNames#WRITE_CERVICAL_MUCUS}
     * {@link PermissionNames#READ_DISTANCE}
     * {@link PermissionNames#WRITE_DISTANCE}
     * {@link PermissionNames#READ_ELEVATION_GAINED}
     * {@link PermissionNames#WRITE_ELEVATION_GAINED}
     * {@link PermissionNames#READ_EXERCISE}
     * {@link PermissionNames#WRITE_EXERCISE}
     * {@link PermissionNames#READ_EXERCISE_ROUTES}
     * {@link PermissionNames#WRITE_EXERCISE_ROUTE}
     * {@link PermissionNames#READ_FLOORS_CLIMBED}
     * {@link PermissionNames#WRITE_FLOORS_CLIMBED}
     * {@link PermissionNames#READ_HEART_RATE}
     * {@link PermissionNames#WRITE_HEART_RATE}
     * {@link PermissionNames#READ_HEART_RATE_VARIABILITY}
     * {@link PermissionNames#WRITE_HEART_RATE_VARIABILITY}
     * {@link PermissionNames#READ_HEIGHT}
     * {@link PermissionNames#WRITE_HEIGHT}
     * {@link PermissionNames#READ_HYDRATION}
     * {@link PermissionNames#WRITE_HYDRATION}
     * {@link PermissionNames#READ_INTERMENSTRUAL_BLEEDING}
     * {@link PermissionNames#WRITE_INTERMENSTRUAL_BLEEDING}
     * {@link PermissionNames#READ_LEAN_BODY_MASS}
     * {@link PermissionNames#WRITE_LEAN_BODY_MASS}
     * {@link PermissionNames#READ_MENSTRUATION}
     * {@link PermissionNames#WRITE_MENSTRUATION}
     * {@link PermissionNames#READ_MINDFULNESS}
     * {@link PermissionNames#WRITE_MINDFULNESS}
     * {@link PermissionNames#READ_NUTRITION}
     * {@link PermissionNames#WRITE_NUTRITION}
     * {@link PermissionNames#READ_OVULATION_TEST}
     * {@link PermissionNames#WRITE_OVULATION_TEST}
     * {@link PermissionNames#READ_OXYGEN_SATURATION}
     * {@link PermissionNames#WRITE_OXYGEN_SATURATION}
     * {@link PermissionNames#READ_PLANNED_EXERCISE}
     * {@link PermissionNames#WRITE_PLANNED_EXERCISE}
     * {@link PermissionNames#READ_POWER}
     * {@link PermissionNames#WRITE_POWER}
     * {@link PermissionNames#READ_RESPIRATORY_RATE}
     * {@link PermissionNames#WRITE_RESPIRATORY_RATE}
     * {@link PermissionNames#READ_RESTING_HEART_RATE}
     * {@link PermissionNames#WRITE_RESTING_HEART_RATE}
     * {@link PermissionNames#READ_SEXUAL_ACTIVITY}
     * {@link PermissionNames#WRITE_SEXUAL_ACTIVITY}
     * {@link PermissionNames#READ_SKIN_TEMPERATURE}
     * {@link PermissionNames#WRITE_SKIN_TEMPERATURE}
     * {@link PermissionNames#READ_SLEEP}
     * {@link PermissionNames#WRITE_SLEEP}
     * {@link PermissionNames#READ_SPEED}
     * {@link PermissionNames#WRITE_SPEED}
     * {@link PermissionNames#READ_STEPS}
     * {@link PermissionNames#WRITE_STEPS}
     * {@link PermissionNames#READ_TOTAL_CALORIES_BURNED}
     * {@link PermissionNames#WRITE_TOTAL_CALORIES_BURNED}
     * {@link PermissionNames#READ_VO2_MAX}
     * {@link PermissionNames#WRITE_VO2_MAX}
     * {@link PermissionNames#READ_WEIGHT}
     * {@link PermissionNames#WRITE_WEIGHT}
     * {@link PermissionNames#READ_WHEELCHAIR_PUSHES}
     * {@link PermissionNames#WRITE_WHEELCHAIR_PUSHES}
     *
     * Medical data permissions:
     * {@link PermissionNames#READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES}
     * {@link PermissionNames#READ_MEDICAL_DATA_CONDITIONS}
     * {@link PermissionNames#READ_MEDICAL_DATA_LABORATORY_RESULTS}
     * {@link PermissionNames#READ_MEDICAL_DATA_MEDICATIONS}
     * {@link PermissionNames#READ_MEDICAL_DATA_PERSONAL_DETAILS}
     * {@link PermissionNames#READ_MEDICAL_DATA_PRACTITIONER_DETAILS}
     * {@link PermissionNames#READ_MEDICAL_DATA_PREGNANCY}
     * {@link PermissionNames#READ_MEDICAL_DATA_PROCEDURES}
     * {@link PermissionNames#READ_MEDICAL_DATA_SOCIAL_HISTORY}
     * {@link PermissionNames#READ_MEDICAL_DATA_VACCINES}
     * {@link PermissionNames#READ_MEDICAL_DATA_VISITS}
     * {@link PermissionNames#READ_MEDICAL_DATA_VITAL_SIGNS}
     * {@link PermissionNames#WRITE_MEDICAL_DATA}
     */
    public static final String HEALTH = "health" + SUFFIX;
}
