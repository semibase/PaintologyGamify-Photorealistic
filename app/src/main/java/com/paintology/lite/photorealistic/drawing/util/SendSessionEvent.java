package com.paintology.lite.photorealistic.drawing.util;

import android.app.IntentService;
import android.content.Intent;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.concurrent.TimeUnit;

public class SendSessionEvent extends IntentService implements serviceCloseInterface {

    private final String TAG = "SendSessionEvent";
    CountDownTimer cdt = null;
    FirebaseFirestore db_firebase;

    public static serviceCloseInterface _interface;

    public SendSessionEvent() {
        super("");
    }

    int list_size = 0;
    StringConstants constants = new StringConstants();
    long INTERVAL;

    int event_gap_count = 0;

    public String tag = "SendSessionEvent";

    @Override
    public void onCreate() {
        super.onCreate();
        _interface = this;
        Log.e(TAG, "Starting timer...");

        try {
            FirebaseFirestore.setLoggingEnabled(true);
            db_firebase = FirebaseFirestore.getInstance();

            INTERVAL = KGlobal.get_session_model().getInterval_time();
            Log.e("TAGG", "Interval Time in service " + INTERVAL);
            cdt = new CountDownTimer(INTERVAL, INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.e(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    try {
                        Log.e(TAG, "Timer finished size " + KGlobal._session_model.get_event_list().size() + " event_gap_count " + event_gap_count + " list_size " + list_size);
                        if (KGlobal._session_model.get_event_list().size() != 0 && KGlobal._session_model.get_event_list().size() == list_size) {
                            event_gap_count++;
                            Log.e(TAG, "Timer finished counter increase " + event_gap_count);
                        } else if (event_gap_count != 0 && KGlobal._session_model.get_event_list().size() > 0) {
                            try {
                                long minutes = TimeUnit.MILLISECONDS.toMinutes(INTERVAL * event_gap_count);
                                KGlobal._session_model.get_event_list().add(list_size, minutes + "_minute_" + constants.gap_event);
                                event_gap_count = 0;
                                list_size = KGlobal._session_model.get_event_list().size();
                            } catch (Exception e) {
                            }
                            sendSessionData();
                        } else {
                            Log.e(TAG, "Timer finished counter send data from else");
                            list_size = KGlobal._session_model.get_event_list().size();
                            sendSessionData();
                        }
                    } catch (Exception e) {
                    }
                    cdt.start();
                }
            };
            cdt.start();
        } catch (Exception e) {
            Log.e(TAG, "Exception at timer " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
//        cdt.cancel();
        Log.e(tag, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(tag, "Timer onUnbind");
        cdt.cancel();
        return super.onUnbind(intent);
    }


    void sendSessionData() {
        try {


            KGlobal.get_session_model().get_map().put(KGlobal.get_session_model().getDoc_name(), KGlobal._session_model.get_event_list());

            String androidId = "";
            if (constants.getString(constants._android_device_id, getApplicationContext()).isEmpty()) {
                androidId = "user_" + Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                constants.putString(constants._android_device_id, androidId, getApplicationContext());
//                Log.e("TAGGG", "Android ID from If Service " + androidId);
            } else {
                androidId = constants.getString(constants._android_device_id, getApplicationContext());
//                Log.e("TAGGG", "Android ID from else Service " + androidId);
            }

            db_firebase.collection(constants.collection_name).document(androidId).set(KGlobal.get_session_model().get_map(), SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    Toast.makeText(SendSessionEvent.this, KGlobal._session_model.get_event_list().size() + " event send to cloud", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            cdt.cancel();
            Log.e(TAG, "Exception sendSessionData " + e.getMessage());
        }
    }

    @Override
    public void closeService() {
        try {
            Log.e(TAG, "closeService called");
            cdt.cancel();
            stopSelf();
        } catch (Exception e) {
            Log.e(TAG, "closeService exception " + e.getMessage());
        }
    }
}
