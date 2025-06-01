package com.paintology.lite.photorealistic.drawing.Youtube.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class Utils {
    @SuppressLint("DefaultLocale")
    public static String formatTime(float sec) {
        int minutes = (int) (sec / 60);
        int seconds = (int) (sec % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    public static StringConstants _constant = new StringConstants();

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr.getActiveNetworkInfo() == null) {
            int count = _constant.getInt("no_internet", context);
            count = count + 1;
            _constant.putInt("no_internet", count, context);
        } else {
            sendEvent(context);
        }
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void sendEvent(Context _context) {
        try {
            int count = _constant.getInt("no_internet", _context);

            for (int i = 0; i < count; i++) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(_context, "no internet", Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(_context, "no internet");
            }
            _constant.putInt("no_internet", 0, _context);
        } catch (Exception e) {
        }
    }
}
