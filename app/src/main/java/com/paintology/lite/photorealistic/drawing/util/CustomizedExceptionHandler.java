package com.paintology.lite.photorealistic.drawing.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomizedExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    private String localPath;
    public Activity activity;

    public CustomizedExceptionHandler(String localPath, Activity context) {
        this.localPath = localPath;
        //Getting the the default exception handler
        //that's executed when uncaught exception terminates a thread
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.activity = context;
    }

    public void uncaughtException(Thread t, Throwable e) {

        //Write a printable representation of this Throwable
        //The StringWriter gives the lock used to synchronize access to this writer.
        final Writer stringBuffSync = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringBuffSync);
        e.printStackTrace(printWriter);
        String stacktrace = stringBuffSync.toString();
        printWriter.close();

        if (localPath != null) {
            writeToFile(stacktrace);
        }

        restartApplicationCode();
//Used only to prevent from any code getting executed.
        // Not needed in this example
        defaultUEH.uncaughtException(t, e);

    }


    public void restartApplicationCode() {
        try {
            Intent intent = new Intent(activity, GalleryDashboard.class);
            intent.putExtra("crash", true);
            intent.putExtra("isTutorialmode", (PaintActivity.obj_interface != null ? PaintActivity.obj_interface.isInTutorialMode() : false));
            intent.putExtra("path", PaintActivity.obj_interface.getBackgroundImagePath());

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getActivity(MyApplication.getInstance().getBaseContext(),
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager mgr = (AlarmManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
            activity.finish();
            System.exit(2);
        } catch (Exception e) {
            Log.e("TAGG", "Exception at restartApplicationCode");
        }
    }

    private void writeToFile(String currentStacktrace) {
        try {
            //Gets the Android external storage directory & Create new folder Crash_Reports
            File dir = new File(Environment.getExternalStorageDirectory(),
                    "Crash_Reports");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            Date date = new Date();
            String filename = dateFormat.format(date) + ".txt";

            // Write the file into the folder
            File reportFile = new File(dir, filename);
            FileWriter fileWriter = new FileWriter(reportFile);
            fileWriter.append(currentStacktrace);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            Log.e("ExceptionHandler", e.getMessage());
        }
    }

}
