package de.dimedis.mobileentry.backend;

public interface CommonArgImpl extends BaseArgImpl {
    String getUserSession();

    void setUserSession(String userSession);

    String getUserSuid();

    void setUserSuid(String userSuid);

    String getUserName();

    void setUserName(String userName);

    String getFair();

    void setFair(String fair);

    String getBorder();

    void setBorder(String border);
}
