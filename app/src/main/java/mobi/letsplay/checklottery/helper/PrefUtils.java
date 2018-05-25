package mobi.letsplay.checklottery.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;

public class PrefUtils {

    private static final String START_TIME = "checklottery";
    private static final String PURCHASE = "purchase";

    private SharedPreferences mPreferences;

    public PrefUtils(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getPurchase() {
        return mPreferences.getBoolean(PURCHASE, false);
    }

    public void setPurchase(boolean purchase) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(PURCHASE, purchase);
        editor.apply();
    }
}
