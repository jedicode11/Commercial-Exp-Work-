package de.dimedis.mobileentry.model;

import de.dimedis.lmlib.myinterfaces.Session;
import de.dimedis.mobileentry.bbapi.Constants;
import de.dimedis.mobileentry.util.SessionUtils;

public class OnUserOfflineLoginCompleted {
    private Session session;

    public OnUserOfflineLoginCompleted(Session session) {
        setSession(session);
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public boolean isSuccessfulLogin() {
        return session.getStatus().equalsIgnoreCase(Constants.OFFLINE_LOGIN_SUCCESS_STATUS);
    }

    public void saveUserSessionPrefs() {
        SessionUtils.setUsersParam(getSession());
        SessionUtils.storeOfflineSession(getSession());
    }
}
