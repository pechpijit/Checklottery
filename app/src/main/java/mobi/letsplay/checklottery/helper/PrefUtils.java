package mobi.letsplay.checklottery.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;

public class PrefUtils {

    private static final String START_TIME = "countdown_timer";
    private static final String STATUS = "status";
    private static final String MODE = "mode";
    private static final String FUNCTION = "function";
    private static final String TIME = "time";
    private static final String MAXTIME = "maxtime";
    private static final String LOGIN = "login";
    private static final String LANGUAGE = "language";
    private static final String MACHINE = "machine";
    private static final String NOTIFICATION = "notification";
    private SharedPreferences mPreferences;

    public PrefUtils(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getLogin() {
        return mPreferences.getBoolean(LOGIN, false);
    }

    public void setLogin(boolean login) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(LOGIN, login);
        editor.apply();
    }

    public int getStartedTime() {
        return mPreferences.getInt(START_TIME, 0);
    }

    public void setStartedTime(int startedTime) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(START_TIME, startedTime);
        editor.apply();
    }

    public boolean getStatus() {
        return mPreferences.getBoolean(STATUS, false);
    }

    public void setStatus(boolean status) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(STATUS, status);
        editor.apply();
    }

    public String getMode() {
        return mPreferences.getString(MODE,"Automatically wash");
    }

    public void setMode(String mode) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(MODE, mode);
        editor.apply();
    }

    public String getFunction() {
        return mPreferences.getString(FUNCTION,"Automatic");
    }

    public void setFunction(String function) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(FUNCTION, function);
        editor.apply();
    }

    public String getTime() {
        return mPreferences.getString(TIME,"Automatic");
    }

    public void setTime(String time) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(TIME, time);
        editor.apply();
    }

    public int getMaxTime() {
        return mPreferences.getInt(MAXTIME,0);
    }

    public void setMaxTime(int maxTime) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(MAXTIME, maxTime);
        editor.apply();
    }

    public int getLanguage() {
        return mPreferences.getInt(LANGUAGE,0);
    }

    public void setLanguage(int language) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LANGUAGE, language);
        editor.apply();
    }

    public int getMachine() {
        return mPreferences.getInt(MACHINE,0);
    }

    public void setMachine(int machine) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(MACHINE, machine);
        editor.apply();
    }

    public int getNotification(int id) {
        return mPreferences.getInt(NOTIFICATION+id,0);
    }

    public void setNotification(int id,int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(NOTIFICATION+id, value);
        editor.apply();
    }
}
