package adrean.thesis.puocc;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreference {

    private static final String PREFS_NAME = "user_pref";
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_ADDRESS = "userAddress";
    private static final String USER_PHONE = "userPhone";
    private static final String USER_ROLE = "userRole";
    private static final String USER_RESPONSE = "userResponse";
    private static final String USER_MESSAGE = "userMessage";
    private static final String USER_PASSWORD = "userPassword";


    private final SharedPreferences preferences;

    UserPreference(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setUser(UserModel value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, value.getUserId());
        editor.putString(USER_PASSWORD, value.getUserPassword());
        editor.putString(USER_NAME, value.getUserName());
        editor.putString(USER_EMAIL, value.getUserEmail());
        editor.putString(USER_ADDRESS, value.getUserAddress());
        editor.putString(USER_PHONE, value.getUserPhone());
        editor.putString(USER_ROLE, value.getUserRole());
        editor.putString(USER_RESPONSE, value.getUserResponse());
        editor.putString(USER_MESSAGE, value.getUserMessage());

        editor.apply();
    }

    UserModel getUser() {
        UserModel model = new UserModel();
        model.setUserId(preferences.getString(USER_ID, ""));
        model.setUserPassword(preferences.getString(USER_PASSWORD, ""));
        model.setUserName(preferences.getString(USER_NAME, ""));
        model.setUserEmail(preferences.getString(USER_EMAIL, ""));
        model.setUserAddress(preferences.getString(USER_ADDRESS, ""));
        model.setUserPhone(preferences.getString(USER_PHONE, ""));
        model.setUserRole(preferences.getString(USER_ROLE, ""));
        model.setUserResponse(preferences.getString(USER_RESPONSE, ""));
        model.setUserMessage(preferences.getString(USER_MESSAGE, ""));

        return model;
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
