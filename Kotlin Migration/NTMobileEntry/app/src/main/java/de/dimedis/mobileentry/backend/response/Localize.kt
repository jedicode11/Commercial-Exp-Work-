package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.util.dynamicres.SerializedResId

object Localize {
    const val DEVICE_SETTINGS_VIBRATION_LABEL = "DEVICE_SETTINGS_VIBRATION_LABEL"
    const val CANCEL_BUTTON_TEXT = "CANCEL_BUTTON_TEXT"
    const val SCAN_STATUS_LOCAL = "SCAN_STATUS_LOCAL"
    const val VOID_TICKET_TITLE = "VOID_TICKET_TITLE"
    const val SOFTWARE_VERSIONS_APP_LABEL = "SOFTWARE_VERSIONS_APP_LABEL"
    const val ERROR_PAGE_EXCEPTION_TITLE = "ERROR_PAGE_EXCEPTION_TITLE"
    const val RESET_DEVICE_MENU_BUTTON_TEXT = "RESET_DEVICE_MENU_BUTTON_TEXT"
    const val SOFTWARE_VERSIONS_APP_VERSION_UNKNOWN_TEXT = "SOFTWARE_VERSIONS_APP_VERSION_UNKNOWN_TEXT"
    const val SCAN_STATUS_ONLINE = "SCAN_STATUS_ONLINE"
    const val HISTORY_TABLE_ACTION_HEADER = "HISTORY_TABLE_ACTION_HEADER"
    const val DEVICE_INIT_ID_USED_ERROR = "DEVICE_INIT_ID_USED_ERROR"
    const val SPINNER_PROGRESS = "SPINNER_PROGRESS"
    const val DEVICE_INIT_USER_LABEL = "DEVICE_INIT_USER_LABEL"
    const val CLOSE_BUTTON_TEXT = "CLOSE_BUTTON_TEXT"
    const val DEVICE_INIT_SESSION_SINCE_LABEL = "DEVICE_INIT_SESSION_SINCE_LABEL"
    const val TV_HOME_SELECTOR_PROMPT = "TV_HOME_SELECTOR_PROMPT"
    const val TOGGLE_ON_TEXT = "TOGGLE_ON_TEXT"
    const val SCAN_OK_TEXT = "SCAN_OK_TEXT"
    const val TV_SCAN_PROMPT = "TV_SCAN_PROMPT"
    const val VOID_TICKET_BUTTON_LABEL = "VOID_TICKET_BUTTON_LABEL"
    const val LOGFILE_MENU_BUTTON_TEXT = "LOGFILE_MENU_BUTTON_TEXT"
    const val LOGIN_SCAN_LABEL = "LOGIN_SCAN_LABEL"
    const val SOFTWARE_VERSIONS_NONE_TEXT = "SOFTWARE_VERSIONS_NONE_TEXT"
    const val APP_INIT_TITLE = "APP_INIT_TITLE"
    const val EXIT_APP_MENU_BUTTON_TEXT = "EXIT_APP_MENU_BUTTON_TEXT"
    const val SCAN_LOCAL_RECORDS_MANY = "SCAN_LOCAL_RECORDS_MANY"
    const val SCAN_ENTRY_TITLE = "SCAN_ENTRY_TITLE"
    const val ERROR_MESSAGE_INTRO = "ERROR_MESSAGE_INTRO"
    const val DEVICE_SETTINGS_SOUND_LABEL = "DEVICE_SETTINGS_SOUND_LABEL"
    const val DEVICE_INIT_TITLE = "DEVICE_INIT_TITLE"
    const val DEVICE_INIT_LOCATION_LABEL = "DEVICE_INIT_LOCATION_LABEL"
    const val PHONE_TITLE = "PHONE_TITLE"
    const val SCAN_NOT_OK_TEXT = "SCAN_NOT_OK_TEXT"
    const val DEVICE_INIT_INPUT_LABEL = "DEVICE_INIT_INPUT_LABEL"
    const val DEVICE_INIT_SINCE_LABEL = "DEVICE_INIT_SINCE_LABEL"
    const val LOGIN_CODE_LOGIN_BUTTON_TEXT = "LOGIN_CODE_LOGIN_BUTTON_TEXT"
    const val SOFTWARE_VERSIONS_BORDERS_LABEL = "SOFTWARE_VERSIONS_BORDERS_LABEL"
    const val DEVICE_SETTINGS_BRIGHTNESS_LABEL = "DEVICE_SETTINGS_BRIGHTNESS_LABEL"
    const val LOGOUT_TITLE = "LOGOUT_TITLE"
    const val LOGOUT_MENU_BUTTON_TEXT = "LOGOUT_MENU_BUTTON_TEXT"
    const val SOFTWARE_VERSIONS_LANGUAGES_LABEL = "SOFTWARE_VERSIONS_LANGUAGES_LABEL"
    const val SCAN_STATUS_OFFLINE = "SCAN_STATUS_OFFLINE"
    const val PHONE_MENU_BUTTON_TEXT = "PHONE_MENU_BUTTON_TEXT"
    const val PHONE_QUESTION_TEXT = "PHONE_QUESTION_TEXT"
    const val CHOOSE_BORDER_MENU_BUTTON_TEXT = "CHOOSE_BORDER_MENU_BUTTON_TEXT"
    const val CHOOSE_BORDER_TITLE = "CHOOSE_BORDER_TITLE"
    const val FORCE_ENTRY_FAILED = "FORCE_ENTRY_FAILED"
    const val SCAN_PROMPT = "SCAN_PROMPT"
    const val VOID_TICKET_FAIL_MESSAGE = "VOID_TICKET_FAIL_MESSAGE"
    const val HISTORY_TITLE = "HISTORY_TITLE"
    const val LOGIN_BUTTON_LABEL = "LOGIN_BUTTON_LABEL"
    const val SOFTWARE_VERSIONS_MENU_BUTTON_TEXT = "SOFTWARE_VERSIONS_MENU_BUTTON_TEXT"
    const val NETWORK_SETTINGS_TITLE = "NETWORK_SETTINGS_TITLE"
    const val SCAN_LOCAL_RECORDS_ONE = "SCAN_LOCAL_RECORDS_ONE"
    const val CHOOSE_LANGUAGE_TITLE = "CHOOSE_LANGUAGE_TITLE"
    const val LOGFILE_TITLE = "LOGFILE_TITLE"
    const val HISTORY_NOT_AVAILABLE_MESSAGE = "HISTORY_NOT_AVAILABLE_MESSAGE"
    const val HISTORY_OFFLINE_MESSAGE = "HISTORY_OFFLINE_MESSAGE"
    const val HISTORY_TABLE_DATE_HEADER = "HISTORY_TABLE_DATE_HEADER"
    const val SPINNER_LOADING = "SPINNER_LOADING"
    const val HISTORY_MENU_BUTTON_TEXT = "HISTORY_MENU_BUTTON_TEXT"
    const val ABC_BUTTON_TEXT = "ABC_BUTTON_TEXT"
    const val LOGIN_TITLE = "LOGIN_TITLE"
    const val ERROR_PAGE_TITLE = "ERROR_PAGE_TITLE"
    const val LOGFILE_TOGGLE_LABEL = "LOGFILE_TOGGLE_LABEL"
    const val APP_INIT_ERROR = "APP_INIT_ERROR"
    const val OK_BUTTON_TEXT = "OK_BUTTON_TEXT"
    const val SCAN_CHECKOUT_TITLE = "SCAN_CHECKOUT_TITLE"
    const val SOFTWARE_VERSIONS_CODES_ON_DEVICE_LABEL = "SOFTWARE_VERSIONS_CODES_ON_DEVICE_LABEL"
    const val LOGOUT_QUESTION_TEXT = "LOGOUT_QUESTION_TEXT"
    const val TOGGLE_OFF_TEXT = "TOGGLE_OFF_TEXT"
    const val LOGIN_PASSWORD_INPUT_LABEL = "LOGIN_PASSWORD_INPUT_LABEL"
    const val MENU_TITLE = "MENU_TITLE"
    const val SCAN_INPUT_FIELD_TEXT = "SCAN_INPUT_FIELD_TEXT"
    const val APP_INIT_SCAN_LABEL = "APP_INIT_SCAN_LABEL"
    const val FKEY_CONFIG_TITLE = "FKEY_CONFIG_TITLE"
    const val LOGIN_USERNAME_INPUT_LABEL = "LOGIN_USERNAME_INPUT_LABEL"
    const val REGULATOR_MIN_TEXT = "REGULATOR_MIN_TEXT"
    const val SCAN_ENTRY_MENU_BUTTON_TEXT = "SCAN_ENTRY_MENU_BUTTON_TEXT"
    const val DEVICE_INIT_SERVER_INFO = "DEVICE_INIT_SERVER_INFO"
    const val VOID_TICKET_MENU_BUTTON_TEXT = "VOID_TICKET_MENU_BUTTON_TEXT"
    const val DEVICE_INIT_ID_LABEL = "DEVICE_INIT_ID_LABEL"
    const val HISTORY_TABLE_LOCATION_HEADER = "HISTORY_TABLE_LOCATION_HEADER"
    const val DEVICE_SETTINGS_TITLE = "DEVICE_SETTINGS_TITLE"
    const val UPDATE_TEXT = "UPDATE_TEXT"
    const val SCAN_CHECKOUT_MENU_BUTTON_TEXT = "SCAN_CHECKOUT_MENU_BUTTON_TEXT"
    const val FKEY_CONFIG_MENU_BUTTON_TEXT = "FKEY_CONFIG_MENU_BUTTON_TEXT"
    const val RESET_DEVICE_QUESTION_TEXT = "RESET_DEVICE_QUESTION_TEXT"
    const val EXIT_APP_TITLE = "EXIT_APP_TITLE"
    const val ERROR_GENERAL_OFFLINE = "ERROR_GENERAL_OFFLINE"
    const val SOFTWARE_VERSIONS_LOCAL_CONFIG_LABEL = "SOFTWARE_VERSIONS_LOCAL_CONFIG_LABEL"
    const val REGULATOR_MAX_TEXT = "REGULATOR_MAX_TEXT"
    const val EXIT_APP_QUESTION_TEXT = "EXIT_APP_QUESTION_TEXT"
    const val LOGIN_FAILED_MESSAGE = "LOGIN_FAILED_MESSAGE"
    const val FORCE_ENTRY_BUTTON_LABEL = "FORCE_ENTRY_BUTTON_LABEL"
    const val ALERT_TITLE_EXCEPTION = "ALERT_TITLE_EXCEPTION"
    const val VOID_TICKET_SUCCESS_MESSAGE = "VOID_TICKET_SUCCESS_MESSAGE"
    const val SOFTWARE_VERSIONS_UPDATE_INFO = "SOFTWARE_VERSIONS_UPDATE_INFO"
    const val SOFTWARE_VERSIONS_TITLE = "SOFTWARE_VERSIONS_TITLE"
    const val NETWORK_SETTINGS_MENU_BUTTON_TEXT = "NETWORK_SETTINGS_MENU_BUTTON_TEXT"
    const val DEVICE_INIT_STEAL_ID_QUESTION = "DEVICE_INIT_STEAL_ID_QUESTION"
    const val RESET_DEVICE_TITLE = "RESET_DEVICE_TITLE"
    const val NETWORK_SETTINGS_LOCAL_SCAN_TOGGLE_LABEL = "NETWORK_SETTINGS_LOCAL_SCAN_TOGGLE_LABEL"
    const val UPDATE_BUTTON_LABEL = "UPDATE_BUTTON_LABEL"
    const val TO_LOGIN_BUTTON_TEXT = "TO_LOGIN_BUTTON_TEXT"
    const val DEVICE_SETTINGS_MENU_BUTTON_TEXT = "DEVICE_SETTINGS_MENU_BUTTON_TEXT"
    const val SOFTWARE_VERSIONS_LIBRARY_LABEL = "SOFTWARE_VERSIONS_LIBRARY_LABEL"
    const val PUSH_CODE_MENU_BUTTON_TEXT = "PUSH_CODE_MENU_BUTTON_TEXT"
    const val PUSH_CODE_TITLE = "PUSH_CODE_TITLE"
    const val PUSH_CODE_CODES_ON_DEVICE_LABEL = "PUSH_CODE_CODES_ON_DEVICE_LABEL"
    const val PUSH_CODE_INFO_TEXT = "PUSH_CODE_INFO_TEXT"
    const val PUSH_CODE_BUTTON = "PUSH_CODE_BUTTON"
    const val PUSH_CODE_OFFLINE_TEXT = "PUSH_CODE_OFFLINE_TEXT"
    const val PUSH_CODE_NOCODES_TEXT = "PUSH_CODE_NOCODES_TEXT"
    const val DEVICE_SETTINGS_PERMANENT_SCAN_MODE = "DEVICE_SETTINGS_PERMANENT_SCAN_MODE"
    const val DEVICE_SETTINGS_CAMERA_LABEL = "DEVICE_SETTINGS_CAMERA_LABEL"

    const val DELETE_CODES_TITLE = "DELETE_CODES_TITLE"
    const val DELETE_CODES_CODES_ON_DEVICE_LABEL = "DELETE_CODES_CODES_ON_DEVICE_LABEL"
    const val DELETE_CODES_INFO_TEXT = "DELETE_CODES_INFO_TEXT"
    const val DELETE_CODES_NOCODES_TEXT = "DELETE_CODES_NOCODES_TEXT"
    const val DELETE_CODES_BUTTON = "DELETE_CODES_BUTTON"
    const val DELETE_CODES_MENU_BUTTON_TEXT = "DELETE_CODES_MENU_BUTTON_TEXT"

    @SerializedResId(R.string.DEVICE_SETTINGS_PERMANENT_SCAN_MODE)
    @SerializedName(DEVICE_SETTINGS_PERMANENT_SCAN_MODE)
    private val mDEVICE_SETTINGS_PERMANENT_SCAN_MODE: String? = null

    @SerializedResId(R.string.DEVICE_SETTINGS_CAMERA_LABEL)
    @SerializedName(DEVICE_SETTINGS_CAMERA_LABEL)
    private val mDEVICE_SETTINGS_CAMERA_LABEL: String? = null

    @SerializedResId(R.string.DEVICE_SETTINGS_VIBRATION_LABEL)
    @SerializedName(DEVICE_SETTINGS_VIBRATION_LABEL)
    private val mDEVICE_SETTINGS_VIBRATION_LABEL: String? = null

    @SerializedResId(R.string.CANCEL_BUTTON_TEXT)
    @SerializedName(CANCEL_BUTTON_TEXT)
    private val mCANCEL_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.SCAN_STATUS_LOCAL)
    @SerializedName(SCAN_STATUS_LOCAL)
    private val mSCAN_STATUS_LOCAL: String? = null

    @SerializedResId(R.string.VOID_TICKET_TITLE)
    @SerializedName(VOID_TICKET_TITLE)
    private val mVOID_TICKET_TITLE: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_APP_LABEL)
    @SerializedName(SOFTWARE_VERSIONS_APP_LABEL)
    private val mSOFTWARE_VERSIONS_APP_LABEL: String? = null

    @SerializedName(ERROR_PAGE_EXCEPTION_TITLE)
    private val mERROR_PAGE_EXCEPTION_TITLE: String? = null

    @SerializedResId(R.string.RESET_DEVICE_MENU_BUTTON_TEXT)
    @SerializedName(RESET_DEVICE_MENU_BUTTON_TEXT)
    private val mRESET_DEVICE_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_APP_VERSION_UNKNOWN_TEXT)
    @SerializedName(SOFTWARE_VERSIONS_APP_VERSION_UNKNOWN_TEXT)
    private val mSOFTWARE_VERSIONS_APP_VERSION_UNKNOWN_TEXT: String? = null

    @SerializedResId(R.string.SCAN_STATUS_ONLINE)
    @SerializedName(SCAN_STATUS_ONLINE)
    private val mSCAN_STATUS_ONLINE: String? = null

    @SerializedResId(R.string.HISTORY_TABLE_ACTION_HEADER)
    @SerializedName(HISTORY_TABLE_ACTION_HEADER)
    private val mHISTORY_TABLE_ACTION_HEADER: String? = null

    @SerializedResId(R.string.DEVICE_INIT_ID_USED_ERROR)
    @SerializedName(DEVICE_INIT_ID_USED_ERROR)
    private val mDEVICE_INIT_ID_USED_ERROR: String? = null

    @SerializedResId(R.string.SPINNER_PROGRESS)
    @SerializedName(SPINNER_PROGRESS)
    private val mSPINNER_PROGRESS: String? = null

    @SerializedResId(R.string.DEVICE_INIT_USER_LABEL)
    @SerializedName(DEVICE_INIT_USER_LABEL)
    private val mDEVICE_INIT_USER_LABEL: String? = null

    @SerializedResId(R.string.CLOSE_BUTTON_TEXT)
    @SerializedName(CLOSE_BUTTON_TEXT)
    private val mCLOSE_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.DEVICE_INIT_SESSION_SINCE_LABEL)
    @SerializedName(DEVICE_INIT_SESSION_SINCE_LABEL)
    private val mDEVICE_INIT_SESSION_SINCE_LABEL: String? = null

    @SerializedName(TV_HOME_SELECTOR_PROMPT)
    private val mTV_HOME_SELECTOR_PROMPT: String? = null

    @SerializedResId(R.string.TOGGLE_ON_TEXT)
    @SerializedName(TOGGLE_ON_TEXT)
    private val mTOGGLE_ON_TEXT: String? = null

    @SerializedResId(R.string.SCAN_OK_TEXT)
    @SerializedName(SCAN_OK_TEXT)
    private val mSCAN_OK_TEXT: String? = null

    @SerializedName(TV_SCAN_PROMPT)
    private val mTV_SCAN_PROMPT: String? = null

    @SerializedResId(R.string.VOID_TICKET_BUTTON_LABEL)
    @SerializedName(VOID_TICKET_BUTTON_LABEL)
    private val mVOID_TICKET_BUTTON_LABEL: String? = null

    @SerializedResId(R.string.LOGFILE_MENU_BUTTON_TEXT)
    @SerializedName(LOGFILE_MENU_BUTTON_TEXT)
    private val mLOGFILE_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.LOGIN_SCAN_LABEL)
    @SerializedName(LOGIN_SCAN_LABEL)
    private val mLOGIN_SCAN_LABEL: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_NONE_TEXT)
    @SerializedName(SOFTWARE_VERSIONS_NONE_TEXT)
    private val mSOFTWARE_VERSIONS_NONE_TEXT: String? = null

    @SerializedResId(R.string.APP_INIT_TITLE)
    @SerializedName(APP_INIT_TITLE)
    private val mAPP_INIT_TITLE: String? = null

    @SerializedResId(R.string.EXIT_APP_MENU_BUTTON_TEXT)
    @SerializedName(EXIT_APP_MENU_BUTTON_TEXT)
    private val mEXIT_APP_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.SCAN_LOCAL_RECORDS_MANY)
    @SerializedName(SCAN_LOCAL_RECORDS_MANY)
    private val mSCAN_LOCAL_RECORDS_MANY: String? = null

    @SerializedResId(R.string.SCAN_ENTRY_TITLE)
    @SerializedName(SCAN_ENTRY_TITLE)
    private val mSCAN_ENTRY_TITLE: String? = null

    @SerializedResId(R.string.ERROR_MESSAGE_INTRO)
    @SerializedName(ERROR_MESSAGE_INTRO)
    private val mERROR_MESSAGE_INTRO: String? = null

    @SerializedResId(R.string.DEVICE_SETTINGS_SOUND_LABEL)
    @SerializedName(DEVICE_SETTINGS_SOUND_LABEL)
    private val mDEVICE_SETTINGS_SOUND_LABEL: String? = null

    @SerializedResId(R.string.DEVICE_INIT_TITLE)
    @SerializedName(DEVICE_INIT_TITLE)
    private val mDEVICE_INIT_TITLE: String? = null

    @SerializedResId(R.string.DEVICE_INIT_LOCATION_LABEL)
    @SerializedName(DEVICE_INIT_LOCATION_LABEL)
    private val mDEVICE_INIT_LOCATION_LABEL: String? = null

    @SerializedResId(R.string.PHONE_TITLE)
    @SerializedName(PHONE_TITLE)
    private val mPHONE_TITLE: String? = null

    @SerializedResId(R.string.SCAN_NOT_OK_TEXT)
    @SerializedName(SCAN_NOT_OK_TEXT)
    private val mSCAN_NOT_OK_TEXT: String? = null

    @SerializedResId(R.string.DEVICE_INIT_INPUT_LABEL)
    @SerializedName(DEVICE_INIT_INPUT_LABEL)
    private val mDEVICE_INIT_INPUT_LABEL: String? = null

    @SerializedResId(R.string.DEVICE_INIT_SINCE_LABEL)
    @SerializedName(DEVICE_INIT_SINCE_LABEL)
    private val mDEVICE_INIT_SINCE_LABEL: String? = null

    @SerializedResId(R.string.LOGIN_CODE_LOGIN_BUTTON_TEXT)
    @SerializedName(LOGIN_CODE_LOGIN_BUTTON_TEXT)
    private val mLOGIN_CODE_LOGIN_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_BORDERS_LABEL)
    @SerializedName(SOFTWARE_VERSIONS_BORDERS_LABEL)
    private val mSOFTWARE_VERSIONS_BORDERS_LABEL: String? = null

    @SerializedResId(R.string.DEVICE_SETTINGS_BRIGHTNESS_LABEL)
    @SerializedName(DEVICE_SETTINGS_BRIGHTNESS_LABEL)
    private val mDEVICE_SETTINGS_BRIGHTNESS_LABEL: String? = null

    @SerializedResId(R.string.LOGOUT_TITLE)
    @SerializedName(LOGOUT_TITLE)
    private val mLOGOUT_TITLE: String? = null

    @SerializedResId(R.string.LOGOUT_MENU_BUTTON_TEXT)
    @SerializedName(LOGOUT_MENU_BUTTON_TEXT)
    private val mLOGOUT_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_LANGUAGES_LABEL)
    @SerializedName(SOFTWARE_VERSIONS_LANGUAGES_LABEL)
    private val mSOFTWARE_VERSIONS_LANGUAGES_LABEL: String? = null

    @SerializedResId(R.string.SCAN_STATUS_OFFLINE)
    @SerializedName(SCAN_STATUS_OFFLINE)
    private val mSCAN_STATUS_OFFLINE: String? = null

    @SerializedResId(R.string.PHONE_MENU_BUTTON_TEXT)
    @SerializedName(PHONE_MENU_BUTTON_TEXT)
    private val mPHONE_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.PHONE_QUESTION_TEXT)
    @SerializedName(PHONE_QUESTION_TEXT)
    private val mPHONE_QUESTION_TEXT: String? = null

    @SerializedResId(R.string.CHOOSE_BORDER_MENU_BUTTON_TEXT)
    @SerializedName(CHOOSE_BORDER_MENU_BUTTON_TEXT)
    private val mCHOOSE_BORDER_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.CHOOSE_BORDER_TITLE)
    @SerializedName(CHOOSE_BORDER_TITLE)
    private val mCHOOSE_BORDER_TITLE: String? = null

    @SerializedName(FORCE_ENTRY_FAILED)
    private val mFORCE_ENTRY_FAILED: String? = null

    @SerializedResId(R.string.SCAN_PROMPT)
    @SerializedName(SCAN_PROMPT)
    private val mSCAN_PROMPT: String? = null

    @SerializedResId(R.string.VOID_TICKET_FAIL_MESSAGE)
    @SerializedName(VOID_TICKET_FAIL_MESSAGE)
    private val mVOID_TICKET_FAIL_MESSAGE: String? = null

    @SerializedResId(R.string.HISTORY_TITLE)
    @SerializedName(HISTORY_TITLE)
    private val mHISTORY_TITLE: String? = null

    @SerializedResId(R.string.LOGIN_BUTTON_LABEL)
    @SerializedName(LOGIN_BUTTON_LABEL)
    private val mLOGIN_BUTTON_LABEL: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_MENU_BUTTON_TEXT)
    @SerializedName(SOFTWARE_VERSIONS_MENU_BUTTON_TEXT)
    private val mSOFTWARE_VERSIONS_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.NETWORK_SETTINGS_TITLE)
    @SerializedName(NETWORK_SETTINGS_TITLE)
    private val mNETWORK_SETTINGS_TITLE: String? = null

    @SerializedResId(R.string.SCAN_LOCAL_RECORDS_ONE)
    @SerializedName(SCAN_LOCAL_RECORDS_ONE)
    private val mSCAN_LOCAL_RECORDS_ONE: String? = null

    @SerializedResId(R.string.CHOOSE_LANGUAGE_TITLE)
    @SerializedName(CHOOSE_LANGUAGE_TITLE)
    private val mCHOOSE_LANGUAGE_TITLE: String? = null

    @SerializedResId(R.string.LOGFILE_TITLE)
    @SerializedName(LOGFILE_TITLE)
    private val mLOGFILE_TITLE: String? = null

    @SerializedResId(R.string.HISTORY_NOT_AVAILABLE_MESSAGE)
    @SerializedName(HISTORY_NOT_AVAILABLE_MESSAGE)
    private val mHISTORY_NOT_AVAILABLE_MESSAGE: String? = null

    @SerializedResId(R.string.HISTORY_OFFLINE_MESSAGE)
    @SerializedName(HISTORY_OFFLINE_MESSAGE)
    private val mHISTORY_OFFLINE_MESSAGE: String? = null

    @SerializedResId(R.string.HISTORY_TABLE_DATE_HEADER)
    @SerializedName(HISTORY_TABLE_DATE_HEADER)
    private val mHISTORY_TABLE_DATE_HEADER: String? = null

    @SerializedResId(R.string.SPINNER_LOADING)
    @SerializedName(SPINNER_LOADING)
    private val mSPINNER_LOADING: String? = null

    @SerializedResId(R.string.HISTORY_MENU_BUTTON_TEXT)
    @SerializedName(HISTORY_MENU_BUTTON_TEXT)
    private val mHISTORY_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.ABC_BUTTON_TEXT)
    @SerializedName(ABC_BUTTON_TEXT)
    private val mABC_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.LOGIN_TITLE)
    @SerializedName(LOGIN_TITLE)
    private val mLOGIN_TITLE: String? = null

    @SerializedResId(R.string.ERROR_PAGE_TITLE)
    @SerializedName(ERROR_PAGE_TITLE)
    private val mERROR_PAGE_TITLE: String? = null

    @SerializedResId(R.string.LOGFILE_TOGGLE_LABEL)
    @SerializedName(LOGFILE_TOGGLE_LABEL)
    private val mLOGFILE_TOGGLE_LABEL: String? = null

    @SerializedResId(R.string.APP_INIT_ERROR)
    @SerializedName(APP_INIT_ERROR)
    private val mAPP_INIT_ERROR: String? = null

    @SerializedResId(R.string.OK_BUTTON_TEXT)
    @SerializedName(OK_BUTTON_TEXT)
    private val mOK_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.SCAN_CHECKOUT_TITLE)
    @SerializedName(SCAN_CHECKOUT_TITLE)
    private val mSCAN_CHECKOUT_TITLE: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_CODES_ON_DEVICE_LABEL)
    @SerializedName(SOFTWARE_VERSIONS_CODES_ON_DEVICE_LABEL)
    private val mSOFTWARE_VERSIONS_CODES_ON_DEVICE_LABEL: String? = null

    @SerializedResId(R.string.LOGOUT_QUESTION_TEXT)
    @SerializedName(LOGOUT_QUESTION_TEXT)
    private val mLOGOUT_QUESTION_TEXT: String? = null

    @SerializedResId(R.string.TOGGLE_OFF_TEXT)
    @SerializedName(TOGGLE_OFF_TEXT)
    private val mTOGGLE_OFF_TEXT: String? = null

    @SerializedResId(R.string.LOGIN_PASSWORD_INPUT_LABEL)
    @SerializedName(LOGIN_PASSWORD_INPUT_LABEL)
    private val mLOGIN_PASSWORD_INPUT_LABEL: String? = null

    @SerializedResId(R.string.MENU_TITLE)
    @SerializedName(MENU_TITLE)
    private val mMENU_TITLE: String? = null

    @SerializedResId(R.string.SCAN_INPUT_FIELD_TEXT)
    @SerializedName(SCAN_INPUT_FIELD_TEXT)
    private val mSCAN_INPUT_FIELD_TEXT: String? = null

    @SerializedResId(R.string.APP_INIT_SCAN_LABEL)
    @SerializedName(APP_INIT_SCAN_LABEL)
    private val mAPP_INIT_SCAN_LABEL: String? = null

    @SerializedResId(R.string.FKEY_CONFIG_TITLE)
    @SerializedName(FKEY_CONFIG_TITLE)
    private val mFKEY_CONFIG_TITLE: String? = null

    @SerializedResId(R.string.LOGIN_USERNAME_INPUT_LABEL)
    @SerializedName(LOGIN_USERNAME_INPUT_LABEL)
    private val mLOGIN_USERNAME_INPUT_LABEL: String? = null

    @SerializedResId(R.string.REGULATOR_MIN_TEXT)
    @SerializedName(REGULATOR_MIN_TEXT)
    private val mREGULATOR_MIN_TEXT: String? = null

    @SerializedResId(R.string.SCAN_ENTRY_MENU_BUTTON_TEXT)
    @SerializedName(SCAN_ENTRY_MENU_BUTTON_TEXT)
    private val mSCAN_ENTRY_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.DEVICE_INIT_SERVER_INFO)
    @SerializedName(DEVICE_INIT_SERVER_INFO)
    private val mDEVICE_INIT_SERVER_INFO: String? = null

    @SerializedResId(R.string.VOID_TICKET_MENU_BUTTON_TEXT)
    @SerializedName(VOID_TICKET_MENU_BUTTON_TEXT)
    private val mVOID_TICKET_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.DEVICE_INIT_ID_LABEL)
    @SerializedName(DEVICE_INIT_ID_LABEL)
    private val mDEVICE_INIT_ID_LABEL: String? = null

    @SerializedResId(R.string.HISTORY_TABLE_LOCATION_HEADER)
    @SerializedName(HISTORY_TABLE_LOCATION_HEADER)
    private val mHISTORY_TABLE_LOCATION_HEADER: String? = null

    @SerializedResId(R.string.DEVICE_SETTINGS_TITLE)
    @SerializedName(DEVICE_SETTINGS_TITLE)
    private val mDEVICE_SETTINGS_TITLE: String? = null

    @SerializedResId(R.string.UPDATE_TEXT)
    @SerializedName(UPDATE_TEXT)
    private val mUPDATE_TEXT: String? = null

    @SerializedResId(R.string.SCAN_CHECKOUT_MENU_BUTTON_TEXT)
    @SerializedName(SCAN_CHECKOUT_MENU_BUTTON_TEXT)
    private val mSCAN_CHECKOUT_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.FKEY_CONFIG_MENU_BUTTON_TEXT)
    @SerializedName(FKEY_CONFIG_MENU_BUTTON_TEXT)
    private val mFKEY_CONFIG_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.RESET_DEVICE_QUESTION_TEXT)
    @SerializedName(RESET_DEVICE_QUESTION_TEXT)
    private val mRESET_DEVICE_QUESTION_TEXT: String? = null

    @SerializedResId(R.string.EXIT_APP_TITLE)
    @SerializedName(EXIT_APP_TITLE)
    private val mEXIT_APP_TITLE: String? = null

    @SerializedName(ERROR_GENERAL_OFFLINE)
    private val mERROR_GENERAL_OFFLINE: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_LOCAL_CONFIG_LABEL)
    @SerializedName(SOFTWARE_VERSIONS_LOCAL_CONFIG_LABEL)
    private val mSOFTWARE_VERSIONS_LOCAL_CONFIG_LABEL: String? = null

    @SerializedResId(R.string.REGULATOR_MAX_TEXT)
    @SerializedName(REGULATOR_MAX_TEXT)
    private val mREGULATOR_MAX_TEXT: String? = null

    @SerializedResId(R.string.EXIT_APP_QUESTION_TEXT)
    @SerializedName(EXIT_APP_QUESTION_TEXT)
    private val mEXIT_APP_QUESTION_TEXT: String? = null

    @SerializedResId(R.string.LOGIN_FAILED_MESSAGE)
    @SerializedName(LOGIN_FAILED_MESSAGE)
    private val mLOGIN_FAILED_MESSAGE: String? = null

    @SerializedResId(R.string.FORCE_ENTRY_BUTTON_LABEL)
    @SerializedName(FORCE_ENTRY_BUTTON_LABEL)
    private val mFORCE_ENTRY_BUTTON_LABEL: String? = null

    @SerializedName(ALERT_TITLE_EXCEPTION)
    private val mALERT_TITLE_EXCEPTION: String? = null

    @SerializedResId(R.string.VOID_TICKET_SUCCESS_MESSAGE)
    @SerializedName(VOID_TICKET_SUCCESS_MESSAGE)
    private val mVOID_TICKET_SUCCESS_MESSAGE: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_UPDATE_INFO)
    @SerializedName(SOFTWARE_VERSIONS_UPDATE_INFO)
    private val mSOFTWARE_VERSIONS_UPDATE_INFO: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_TITLE)
    @SerializedName(SOFTWARE_VERSIONS_TITLE)
    private val mSOFTWARE_VERSIONS_TITLE: String? = null

    @SerializedResId(R.string.NETWORK_SETTINGS_MENU_BUTTON_TEXT)
    @SerializedName(NETWORK_SETTINGS_MENU_BUTTON_TEXT)
    private val mNETWORK_SETTINGS_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.DEVICE_INIT_STEAL_ID_QUESTION)
    @SerializedName(DEVICE_INIT_STEAL_ID_QUESTION)
    private val mDEVICE_INIT_STEAL_ID_QUESTION: String? = null

    @SerializedResId(R.string.RESET_DEVICE_TITLE)
    @SerializedName(RESET_DEVICE_TITLE)
    private val mRESET_DEVICE_TITLE: String? = null

    @SerializedResId(R.string.NETWORK_SETTINGS_LOCAL_SCAN_TOGGLE_LABEL)
    @SerializedName(NETWORK_SETTINGS_LOCAL_SCAN_TOGGLE_LABEL)
    private val mNETWORK_SETTINGS_LOCAL_SCAN_TOGGLE_LABEL: String? = null

    @SerializedResId(R.string.UPDATE_BUTTON_LABEL)
    @SerializedName(UPDATE_BUTTON_LABEL)
    private val mUPDATE_BUTTON_LABEL: String? = null

    @SerializedResId(R.string.TO_LOGIN_BUTTON_TEXT)
    @SerializedName(TO_LOGIN_BUTTON_TEXT)
    private val mTO_LOGIN_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.DEVICE_SETTINGS_MENU_BUTTON_TEXT)
    @SerializedName(DEVICE_SETTINGS_MENU_BUTTON_TEXT)
    private val mDEVICE_SETTINGS_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.SOFTWARE_VERSIONS_LIBRARY_LABEL)
    @SerializedName(SOFTWARE_VERSIONS_LIBRARY_LABEL)
    private val mSOFTWARE_VERSIONS_LIBRARY_LABEL: String? = null

    @SerializedResId(R.string.PUSH_CODE_MENU_BUTTON_TEXT)
    @SerializedName(PUSH_CODE_MENU_BUTTON_TEXT)
    private val mPUSH_CODE_MENU_BUTTON_TEXT: String? = null

    @SerializedResId(R.string.PUSH_CODE_TITLE)
    @SerializedName(PUSH_CODE_TITLE)
    private val mPUSH_CODE_TITLE: String? = null

    @SerializedResId(R.string.PUSH_CODE_CODES_ON_DEVICE_LABEL)
    @SerializedName(PUSH_CODE_CODES_ON_DEVICE_LABEL)
    private val mPUSH_CODE_CODES_ON_DEVICE_LABEL: String? = null

    @SerializedResId(R.string.PUSH_CODE_INFO_TEXT)
    @SerializedName(PUSH_CODE_INFO_TEXT)
    private val mPUSH_CODE_INFO_TEXT: String? = null

    @SerializedResId(R.string.PUSH_CODE_BUTTON)
    @SerializedName(PUSH_CODE_BUTTON)
    private val mPUSH_CODE_BUTTON: String? = null

    @SerializedResId(R.string.PUSH_CODE_OFFLINE_TEXT)
    @SerializedName(PUSH_CODE_OFFLINE_TEXT)
    private val mPUSH_CODE_OFFLINE_TEXT: String? = null

    @SerializedResId(R.string.PUSH_CODE_NOCODES_TEXT)
    @SerializedName(PUSH_CODE_NOCODES_TEXT)
    private val mPUSH_CODE_NOCODES_TEXT: String? = null

    fun getDEVICESETTINGSVIBRATIONLABEL(): String? {
        return mDEVICE_SETTINGS_VIBRATION_LABEL
    }

    fun getCANCELBUTTONTEXT(): String? {
        return mCANCEL_BUTTON_TEXT
    }

    fun getSCANSTATUSLOCAL(): String? {
        return mSCAN_STATUS_LOCAL
    }

    fun getVOIDTICKETTITLE(): String? {
        return mVOID_TICKET_TITLE
    }

    fun getSOFTWAREVERSIONSAPPLABEL(): String? {
        return mSOFTWARE_VERSIONS_APP_LABEL
    }

    fun getERRORPAGEEXCEPTIONTITLE(): String? {
        return mERROR_PAGE_EXCEPTION_TITLE
    }

    fun getRESETDEVICEMENUBUTTONTEXT(): String? {
        return mRESET_DEVICE_MENU_BUTTON_TEXT
    }

    fun getSOFTWAREVERSIONSAPPVERSIONUNKNOWNTEXT(): String? {
        return mSOFTWARE_VERSIONS_APP_VERSION_UNKNOWN_TEXT
    }

    fun getSCANSTATUSONLINE(): String? {
        return mSCAN_STATUS_ONLINE
    }

    fun getHISTORYTABLEACTIONHEADER(): String? {
        return mHISTORY_TABLE_ACTION_HEADER
    }

    fun getDEVICEINITIDUSEDERROR(): String? {
        return mDEVICE_INIT_ID_USED_ERROR
    }

    fun getSPINNERPROGRESS(): String? {
        return mSPINNER_PROGRESS
    }

    fun getDEVICEINITUSERLABEL(): String? {
        return mDEVICE_INIT_USER_LABEL
    }

    fun getCLOSEBUTTONTEXT(): String? {
        return mCLOSE_BUTTON_TEXT
    }

    fun getDEVICEINITSESSIONSINCELABEL(): String? {
        return mDEVICE_INIT_SESSION_SINCE_LABEL
    }

    fun getTVHOMESELECTORPROMPT(): String? {
        return mTV_HOME_SELECTOR_PROMPT
    }

    fun getTOGGLEONTEXT(): String? {
        return mTOGGLE_ON_TEXT
    }

    fun getSCANOKTEXT(): String? {
        return mSCAN_OK_TEXT
    }

    fun getTVSCANPROMPT(): String? {
        return mTV_SCAN_PROMPT
    }

    fun getVOIDTICKETBUTTONLABEL(): String? {
        return mVOID_TICKET_BUTTON_LABEL
    }

    fun getLOGFILEMENUBUTTONTEXT(): String? {
        return mLOGFILE_MENU_BUTTON_TEXT
    }

    fun getLOGINSCANLABEL(): String? {
        return mLOGIN_SCAN_LABEL
    }

    fun getSOFTWAREVERSIONSNONETEXT(): String? {
        return mSOFTWARE_VERSIONS_NONE_TEXT
    }

    fun getAPPINITTITLE(): String? {
        return mAPP_INIT_TITLE
    }

    fun getEXITAPPMENUBUTTONTEXT(): String? {
        return mEXIT_APP_MENU_BUTTON_TEXT
    }

    fun getSCANLOCALRECORDSMANY(): String? {
        return mSCAN_LOCAL_RECORDS_MANY
    }

    fun getSCANENTRYTITLE(): String? {
        return mSCAN_ENTRY_TITLE
    }

    fun getERRORMESSAGEINTRO(): String? {
        return mERROR_MESSAGE_INTRO
    }

    fun getDEVICESETTINGSSOUNDLABEL(): String? {
        return mDEVICE_SETTINGS_SOUND_LABEL
    }

    fun getDEVICEINITTITLE(): String? {
        return mDEVICE_INIT_TITLE
    }

    fun getDEVICEINITLOCATIONLABEL(): String? {
        return mDEVICE_INIT_LOCATION_LABEL
    }

    fun getPHONETITLE(): String? {
        return mPHONE_TITLE
    }

    fun getSCANNOTOKTEXT(): String? {
        return mSCAN_NOT_OK_TEXT
    }

    fun getDEVICEINITINPUTLABEL(): String? {
        return mDEVICE_INIT_INPUT_LABEL
    }

    fun getDEVICEINITSINCELABEL(): String? {
        return mDEVICE_INIT_SINCE_LABEL
    }

    fun getLOGINCODELOGINBUTTONTEXT(): String? {
        return mLOGIN_CODE_LOGIN_BUTTON_TEXT
    }

    fun getSOFTWAREVERSIONSBORDERSLABEL(): String? {
        return mSOFTWARE_VERSIONS_BORDERS_LABEL
    }

    fun getDEVICESETTINGSBRIGHTNESSLABEL(): String? {
        return mDEVICE_SETTINGS_BRIGHTNESS_LABEL
    }

    fun getLOGOUTTITLE(): String? {
        return mLOGOUT_TITLE
    }

    fun getLOGOUTMENUBUTTONTEXT(): String? {
        return mLOGOUT_MENU_BUTTON_TEXT
    }

    fun getSOFTWAREVERSIONSLANGUAGESLABEL(): String? {
        return mSOFTWARE_VERSIONS_LANGUAGES_LABEL
    }

    fun getSCANSTATUSOFFLINE(): String? {
        return mSCAN_STATUS_OFFLINE
    }

    fun getPHONEMENUBUTTONTEXT(): String? {
        return mPHONE_MENU_BUTTON_TEXT
    }

    fun getPHONEQUESTIONTEXT(): String? {
        return mPHONE_QUESTION_TEXT
    }

    fun getCHOOSEBORDERMENUBUTTONTEXT(): String? {
        return mCHOOSE_BORDER_MENU_BUTTON_TEXT
    }

    fun getCHOOSEBORDERTITLE(): String? {
        return mCHOOSE_BORDER_TITLE
    }

    fun getFORCEENTRYFAILED(): String? {
        return mFORCE_ENTRY_FAILED
    }

    fun getSCANPROMPT(): String? {
        return mSCAN_PROMPT
    }

    fun getVOIDTICKETFAILMESSAGE(): String? {
        return mVOID_TICKET_FAIL_MESSAGE
    }

    fun getHISTORYTITLE(): String? {
        return mHISTORY_TITLE
    }

    fun getLOGINBUTTONLABEL(): String? {
        return mLOGIN_BUTTON_LABEL
    }

    fun getSOFTWAREVERSIONSMENUBUTTONTEXT(): String? {
        return mSOFTWARE_VERSIONS_MENU_BUTTON_TEXT
    }

    fun getNETWORKSETTINGSTITLE(): String? {
        return mNETWORK_SETTINGS_TITLE
    }

    fun getSCANLOCALRECORDSONE(): String? {
        return mSCAN_LOCAL_RECORDS_ONE
    }

    fun getCHOOSELANGUAGETITLE(): String? {
        return mCHOOSE_LANGUAGE_TITLE
    }

    fun getLOGFILETITLE(): String? {
        return mLOGFILE_TITLE
    }

    fun getHISTORYNOTAVAILABLEMESSAGE(): String? {
        return mHISTORY_NOT_AVAILABLE_MESSAGE
    }

    fun getHISTORYOFFLINEMESSAGE(): String? {
        return mHISTORY_OFFLINE_MESSAGE
    }

    fun getHISTORYTABLEDATEHEADER(): String? {
        return mHISTORY_TABLE_DATE_HEADER
    }

    fun getSPINNERLOADING(): String? {
        return mSPINNER_LOADING
    }

    fun getHISTORYMENUBUTTONTEXT(): String? {
        return mHISTORY_MENU_BUTTON_TEXT
    }

    fun getABCBUTTONTEXT(): String? {
        return mABC_BUTTON_TEXT
    }

    fun getLOGINTITLE(): String? {
        return mLOGIN_TITLE
    }

    fun getERRORPAGETITLE(): String? {
        return mERROR_PAGE_TITLE
    }

    fun getLOGFILETOGGLELABEL(): String? {
        return mLOGFILE_TOGGLE_LABEL
    }

    fun getAPPINITERROR(): String? {
        return mAPP_INIT_ERROR
    }

    fun getOKBUTTONTEXT(): String? {
        return mOK_BUTTON_TEXT
    }

    fun getSCANCHECKOUTTITLE(): String? {
        return mSCAN_CHECKOUT_TITLE
    }

    fun getSOFTWAREVERSIONSCODESONDEVICELABEL(): String? {
        return mSOFTWARE_VERSIONS_CODES_ON_DEVICE_LABEL
    }

    fun getLOGOUTQUESTIONTEXT(): String? {
        return mLOGOUT_QUESTION_TEXT
    }

    fun getTOGGLEOFFTEXT(): String? {
        return mTOGGLE_OFF_TEXT
    }

    fun getLOGINPASSWORDINPUTLABEL(): String? {
        return mLOGIN_PASSWORD_INPUT_LABEL
    }

    fun getMENUTITLE(): String? {
        return mMENU_TITLE
    }

    fun getSCANINPUTFIELDTEXT(): String? {
        return mSCAN_INPUT_FIELD_TEXT
    }

    fun getAPPINITSCANLABEL(): String? {
        return mAPP_INIT_SCAN_LABEL
    }

    fun getFKEYCONFIGTITLE(): String? {
        return mFKEY_CONFIG_TITLE
    }

    fun getLOGINUSERNAMEINPUTLABEL(): String? {
        return mLOGIN_USERNAME_INPUT_LABEL
    }

    fun getREGULATORMINTEXT(): String? {
        return mREGULATOR_MIN_TEXT
    }

    fun getSCANENTRYMENUBUTTONTEXT(): String? {
        return mSCAN_ENTRY_MENU_BUTTON_TEXT
    }

    fun getDEVICEINITSERVERINFO(): String? {
        return mDEVICE_INIT_SERVER_INFO
    }

    fun getVOIDTICKETMENUBUTTONTEXT(): String? {
        return mVOID_TICKET_MENU_BUTTON_TEXT
    }

    fun getDEVICEINITIDLABEL(): String? {
        return mDEVICE_INIT_ID_LABEL
    }

    fun getHISTORYTABLELOCATIONHEADER(): String? {
        return mHISTORY_TABLE_LOCATION_HEADER
    }

    fun getDEVICESETTINGSTITLE(): String? {
        return mDEVICE_SETTINGS_TITLE
    }

    fun getUPDATETEXT(): String? {
        return mUPDATE_TEXT
    }

    fun getSCANCHECKOUTMENUBUTTONTEXT(): String? {
        return mSCAN_CHECKOUT_MENU_BUTTON_TEXT
    }

    fun getFKEYCONFIGMENUBUTTONTEXT(): String? {
        return mFKEY_CONFIG_MENU_BUTTON_TEXT
    }

    fun getRESETDEVICEQUESTIONTEXT(): String? {
        return mRESET_DEVICE_QUESTION_TEXT
    }

    fun getEXITAPPTITLE(): String? {
        return mEXIT_APP_TITLE
    }

    fun getERRORGENERALOFFLINE(): String? {
        return mERROR_GENERAL_OFFLINE
    }

    fun getSOFTWAREVERSIONSLOCALCONFIGLABEL(): String? {
        return mSOFTWARE_VERSIONS_LOCAL_CONFIG_LABEL
    }

    fun getREGULATORMAXTEXT(): String? {
        return mREGULATOR_MAX_TEXT
    }

    fun getEXITAPPQUESTIONTEXT(): String? {
        return mEXIT_APP_QUESTION_TEXT
    }

    fun getLOGINFAILEDMESSAGE(): String? {
        return mLOGIN_FAILED_MESSAGE
    }

    fun getFORCEENTRYBUTTONLABEL(): String? {
        return mFORCE_ENTRY_BUTTON_LABEL
    }

    fun getALERTTITLEEXCEPTION(): String? {
        return mALERT_TITLE_EXCEPTION
    }

    fun getVOIDTICKETSUCCESSMESSAGE(): String? {
        return mVOID_TICKET_SUCCESS_MESSAGE
    }

    fun getSOFTWAREVERSIONSUPDATEINFO(): String? {
        return mSOFTWARE_VERSIONS_UPDATE_INFO
    }

    fun getSOFTWAREVERSIONSTITLE(): String? {
        return mSOFTWARE_VERSIONS_TITLE
    }

    fun getNETWORKSETTINGSMENUBUTTONTEXT(): String? {
        return mNETWORK_SETTINGS_MENU_BUTTON_TEXT
    }

    fun getDEVICEINITSTEALIDQUESTION(): String? {
        return mDEVICE_INIT_STEAL_ID_QUESTION
    }

    fun getRESETDEVICETITLE(): String? {
        return mRESET_DEVICE_TITLE
    }

    fun getNETWORKSETTINGSLOCALSCANTOGGLELABEL(): String? {
        return mNETWORK_SETTINGS_LOCAL_SCAN_TOGGLE_LABEL
    }

    fun getUPDATEBUTTONLABEL(): String? {
        return mUPDATE_BUTTON_LABEL
    }

    fun getTOLOGINBUTTONTEXT(): String? {
        return mTO_LOGIN_BUTTON_TEXT
    }

    fun getDEVICESETTINGSMENUBUTTONTEXT(): String? {
        return mDEVICE_SETTINGS_MENU_BUTTON_TEXT
    }

    fun getSOFTWAREVERSIONSLIBRARYLABEL(): String? {
        return mSOFTWARE_VERSIONS_LIBRARY_LABEL
    }

    fun getDEVICESETTINGSCAMERALABEL(): String? {
        return mDEVICE_SETTINGS_CAMERA_LABEL
    }

    fun getDEVICESETTINGSPERMANENTSCANMODE(): String? {
        return mDEVICE_SETTINGS_PERMANENT_SCAN_MODE
    }

    fun getPUSHCODEMENUBUTTONTEXT(): String? {
        return mPUSH_CODE_MENU_BUTTON_TEXT
    }

    fun getPUSHCODETITLE(): String? {
        return mPUSH_CODE_TITLE
    }

    fun getPUSHCODECODESONDEVICELABEL(): String? {
        return mPUSH_CODE_CODES_ON_DEVICE_LABEL
    }

    fun getPUSHCODEINFOTEXT(): String? {
        return mPUSH_CODE_INFO_TEXT
    }

    fun getPUSHCODEBUTTON(): String? {
        return mPUSH_CODE_BUTTON
    }

    fun getPUSHCODEOFFLINE_TEXT(): String? {
        return mPUSH_CODE_OFFLINE_TEXT
    }

    fun getPUSHCODENOCODES_TEXT(): String? {
        return mPUSH_CODE_NOCODES_TEXT
    }
}//end of class