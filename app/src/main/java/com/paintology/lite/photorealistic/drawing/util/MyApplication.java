package com.paintology.lite.photorealistic.drawing.util;

import static org.koin.core.context.DefaultContextExtKt.startKoin;

import android.content.Context;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.paintology.lite.photorealistic.drawing.Chat.RealTimeDBUtils;
import com.paintology.lite.photorealistic.drawing.ads.koin.KoinModulesKt;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;

import org.koin.android.java.KoinAndroidApplication;
import org.koin.core.KoinApplication;

public class MyApplication extends MultiDexApplication {
    public static MyApplication instance;

    public static RealTimeDBUtils _realTimeDbUtils;
    private static AppDatabase db;
    private static boolean appUsedCountSeen = false;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            database.execSQL("ALTER TABLE SavedTutorialEntity "
                    + " ADD COLUMN colorPalette TEXT");
        }
    };

    public static boolean isAppUsedCountSeen() {
        return appUsedCountSeen;
    }

    public static void setAppUsedCountSeen(boolean appUsedCountSeen) {
        MyApplication.appUsedCountSeen = appUsedCountSeen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.setAppUsedCountSeen(false);
//        AudienceNetworkAds.initialize(this);

        instance = this;

        _realTimeDbUtils = new RealTimeDBUtils(this);

//        printHashKey(instance);

        db = Room.databaseBuilder(this,
                        AppDatabase.class, "Paintology")
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build();

        appUsedCountSeen = false;

        KoinApplication koin = KoinAndroidApplication
                .create(this)
                .modules(KoinModulesKt.getModulesList());
        startKoin(koin);

    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static RealTimeDBUtils get_realTimeDbUtils(Context context) {
        if (_realTimeDbUtils == null)
            _realTimeDbUtils = new RealTimeDBUtils(context);
        return _realTimeDbUtils;
    }


    public static AppDatabase getDb() {
        return db;
    }

//    public static void printHashKey(Context pContext) {
//        try {
//            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("MyApplication", "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("MyApplication", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("MyApplication", "printHashKey()", e);
//        }
//    }
}
