package at.aau.anti_mon.server.utilities;

import org.tinylog.Logger;

public class StringUtility {

    public static String extractUserID(String query) {
        String[] params = query.split("&");
        String userIdKey = "userID=";
        for (String param : params) {
            if (param.startsWith(userIdKey)) {
                return param.substring(userIdKey.length());
            }
        }
        Logger.error("UserID konnte nicht extrahiert werden.");
        throw new IllegalArgumentException("UserID konnte nicht extrahiert werden.");
    }

}
