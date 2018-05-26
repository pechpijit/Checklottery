package mobi.letsplay.checklottery.helper;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mobi.letsplay.checklottery.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        initFont();
        initRealm();
    }

    private void initFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Mitr.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    private void initRealm() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("app.checklottery")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
