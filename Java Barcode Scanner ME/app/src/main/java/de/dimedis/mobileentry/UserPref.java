package de.dimedis.mobileentry;

public interface UserPref {//

    //user_suid
    String userSuid();

    //user_name
    String userName();

    String userFullName();

    //Map<String, String>
    String userPrefs();

    //border
    String usersBorder();

    // List<String>
    String listUserFunctions();

    //user_session
    String userSession();
}
