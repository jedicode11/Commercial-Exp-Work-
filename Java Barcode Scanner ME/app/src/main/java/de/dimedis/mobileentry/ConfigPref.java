package de.dimedis.mobileentry;

public interface ConfigPref {

    String currentLanguage();

    void setCurrentLanguage(String value);

    //customer-token
    String customerToken();

    void setCustomerToken(String value);

    String rpcUrl();

    void setRpcUrl(String value);

    String languagesContainer();

    void setLanguagesContainer(String value);

    String languagesContainer2();

    void setLanguagesContainer2(String value);

    String serverName();

    void setServerName(String value);

    String deviceID();

    void setDeviceID(String value);

    String login();

    void setLogin(String value);

    String passwd();

    void setPasswd(String value);

    //comm_key
    String commKey();

    void setCommKey(String value);

    //device_suid
    String deviceSuid();

    void setDeviceSuid(String value);

    String deviceType();

    void setDeviceType(String value);

    String loginBarcode();

    void setLoginBarcode(String value);

    //user_suid
    String userSuid();

    void setUserSuid(String value);

    //user_name
    String userName();

    void setUserName(String value);

    String userFullName();

    void setUserFullName(String value);

    //user_session
    String userSession();

    void setUserSession(String value);

    //local_records
    boolean localRecords();

    void setLocalRecords(boolean value);

    String app();

    void setApp(String value);

    // List<String>
    String listUserFunctions();

    void setListUserFunctions(String value);

    //Map<String, String>
    String userPrefs();

    void setUserPrefs(String value);

    String versionsContainer();

    void setVersionsContainer(String value);

    String versionsFromServerContainer();

    void setVersionsFromServerContainer(String value);

    String bordersContainer();

    void setBordersContainer(String value);

    //border
    String usersBorder();

    void setUsersBorder(String value);

    int progressSound();

    void setProgressSound(int value);

    int progressBrightness();

    void setProgressBrightness(int value);

    boolean isUpdatesAvailable();

    void setIsUpdatesAvailable(boolean value);

    int heartbeatInterval();

    void setHeartbeatInterval(int value);

    String lmlibFilePathOld();

    void setLmlibFilePathOld(String value);

    String lmlibFilePath();

    void setLmlibFilePath(String value);

    String offlineConfig();

    void setOfflineConfig(String value);

    String offlineConfigServer();

    void setOfflineConfigServer(String value);

    boolean isBordersInit();

    void setIsBordersInit(boolean value);

    boolean isLanguagesInit();

    void setIsLanguagesInit(boolean value);
}
