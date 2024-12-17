package de.dimedis.mobileentry.bbapi

object Constants {
    const val ACTION_BARCODE_OPEN = "kr.co.bluebird.android.bbapi.action.BARCODE_OPEN"
    const val ACTION_BARCODE_CLOSE = "kr.co.bluebird.android.bbapi.action.BARCODE_CLOSE"
    const val ACTION_BARCODE_SET_TRIGGER = "kr.co.bluebird.android.bbapi.action.BARCODE_SET_TRIGGER"
    const val ACTION_BARCODE_SET_DEFAULT_PROFILE = "kr.co.bluebird.android.bbapi.action.BARCODE_SET_DEFAULT_PROFILE"
    const val ACTION_BARCODE_SETTING_CHANGED = "kr.co.bluebird.android.bbapi.action.BARCODE_SETTING_CHANGED"
    const val ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS = "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_REQUEST_SUCCESS"
    const val ACTION_BARCODE_CALLBACK_REQUEST_FAILED = "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_REQUEST_FAILED"
    const val ACTION_BARCODE_CALLBACK_DECODING_DATA = "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_DECODING_DATA"
    const val ACTION_BARCODE_GET_STATUS = "kr.co.bluebird.android.action.BARCODE_GET_STATUS"
    const val ACTION_BARCODE_CALLBACK_GET_STATUS = "kr.co.bluebird.android.action.BARCODE_CALLBACK_GET_STATUS"
    const val EXTRA_BARCODE_BOOT_COMPLETE = "EXTRA_BARCODE_BOOT_COMPLETE"
    const val EXTRA_BARCODE_PROFILE_NAME = "EXTRA_BARCODE_PROFILE_NAME"
    const val EXTRA_BARCODE_TRIGGER = "EXTRA_BARCODE_TRIGGER"
    const val EXTRA_BARCODE_DECODING_DATA = "EXTRA_BARCODE_DECODING_DATA"
    const val EXTRA_HANDLE = "EXTRA_HANDLE"
    const val EXTRA_INT_DATA2 = "EXTRA_INT_DATA2"
    const val EXTRA_STR_DATA1 = "EXTRA_STR_DATA1"
    const val EXTRA_INT_DATA3 = "EXTRA_INT_DATA3"

    //20140527
    const val ACTION_BARCODE_SET_PARAMETER = "kr.co.bluebird.android.bbapi.action.BARCODE_SET_PARAMETER"
    const val ACTION_BARCODE_GET_PARAMETER = "kr.co.bluebird.android.bbapi.action.BARCODE_GET_PARAMETER"
    const val ACTION_BARCODE_CALLBACK_PARAMETER = "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_PARAMETER"
    const val ERROR_FAILED = -1
    const val ERROR_NOT_SUPPORTED = -2
    const val ERROR_ERROR_CHECKSUM = -3
    const val ERROR_NO_RESPONSE = -4
    const val ERROR_BATTERY_LOW = -5
    const val ERROR_BARCODE_DECODING_TIMEOUT = -6
    const val ERROR_BARCODE_ERROR_USE_TIMEOUT = -7
    const val ERROR_BARCODE_ERROR_ALREADY_OPENED = -8
    const val ERROR_ERROR_BARCODE_CAMERA_USED = -9
    const val DOT: Long = 200
    const val DASH = DOT * 3 // http://www.nu-ware.com/NuCode%20Help/index.html?morse_code_structure_and_timing_.htm
    const val SHORT_GAP = DOT // http://www.nu-ware.com/NuCode%20Help/index.html?morse_code_structure_and_timing_.htm
    val VIBRATION_PATTERN_OK = longArrayOf(0, DOT, SHORT_GAP, DOT) //". .";
    val VIBRATION_PATTERN_OK_REDUCED_TICKET = longArrayOf(0, DOT, SHORT_GAP, DOT, SHORT_GAP, DASH) // ". . -";
    val VIBRATION_PATTERN_TICKET_STATUS_NOT_OK = longArrayOf(0, DASH, SHORT_GAP, DASH) // "- -";
    val VIBRATION_PATTERN_TICKET_ACCEPTED = longArrayOf(0, DOT) // ".";
    val VIBRATION_PATTERN_CODE_REFUSED = longArrayOf(0, DASH) // "-";
    val VIBRATION_PATTERN_GENERAL_ERROR = longArrayOf(0, DASH, SHORT_GAP, DASH, SHORT_GAP, DOT, SHORT_GAP, DASH, SHORT_GAP, DASH) // "- - . - -";

    const val OFFLINE_LOGIN_SUCCESS_STATUS = "success"
    const val USER_PREFERENCES_FKEYS_KEY = "keys"
    const val FKEY_FUNCTION_1 = "f1"
    const val FKEY_FUNCTION_2 = "f2"
    const val FKEY_FUNCTION_3 = "f3"
    const val FKEY_FUNCTION_4 = "f4"
    const val USER_PREFS_DATABASE_FORMAT = "f1:%s,f2:%s,f3:%s,f4:%s"
    const val AUTH_METHOD_BARCODE = "barcode"
    const val AUTH_METHOD_CREDENTIALS = "username_and_password"
    const val AUTH_VALUE_FIELD_NAME_BARCODE = "auth_barcode"
    const val AUTH_VALUE_FIELD_NAME_CREDENTIALS = "auth_username"
    const val STATUS_REJECTED = "rejected"
    const val STATUS_NO_ACTIVE_SESSION = "no_active_session"
    const val STATUS_ACCEPTED = "accepted"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val BAD_SESSION_MARKER = "Failed to process session"
}