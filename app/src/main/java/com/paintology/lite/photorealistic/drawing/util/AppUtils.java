package com.paintology.lite.photorealistic.drawing.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Token;
import com.paintology.lite.photorealistic.drawing.Enums.LoginType;
import com.paintology.lite.photorealistic.drawing.Model.AppBanner;
import com.paintology.lite.photorealistic.drawing.Model.BannerModel;
import com.paintology.lite.photorealistic.drawing.Model.NotificationModel;
import com.paintology.lite.photorealistic.drawing.Model.RewardSetup;
import com.paintology.lite.photorealistic.drawing.Model.SlideInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AppUtils {

    public static final String POLICY_LINK = "main_policy_link";
    public static final String TERMS_LINK = "main_terms_link";
    private static final String EXTRA_KEY_TUT_BANNERS = "tut_banners";
    private static final String EXTRA_KEY_TOTAL_TUT_BANNERS = "total_tut_banners";

    private static final String EXTRA_KEY_NOTIFICATIONS = "notifications";
    private static final String EXTRA_KEY_SLIDERS = "sliders";
    private static final String EXTRA_KEY_BANNERS = "banners";
    private static final String EXTRA_KEY_DRAW_BANNERS = "draw_banners";
    private static final String EXTRA_KEY_TOTAL_SLIDERS = "total_slider";
    private static final String EXTRA_KEY_TOTAL_BANNERS = "total_banners";
    private static final String EXTRA_KEY_TOTAL_DRAW_BANNERS = "total_draw_banners";
    private static final String EXTRA_KEY_HOME_SLIDERS = "home_sliders";
    private static final String EXTRA_KEY_TOTAL_HOME_SLIDERS = "total_home_slider";
    private static final String EXTRA_KEY_HOME_SLIDE_INTERVAL = "home_slide_interval";
    private static final String EXTRA_KEY_UNREAD_NOTIFICATIONS = "unread_notifications";
    private static final String EXTRA_KEY_UNREAD_CHAT = "unread_chat";
    private static final String EXTRA_KEY_FACEBOOK_LOGIN_SUPPORT = "facebook_login_support";
    private static final String EXTRA_KEY_REWARD_SETUP = "reward_setup";

    private static String searchResponse = null;

    public static List<AppBanner> mBanners = new ArrayList<>();
    private static MediaProjectionManager mMediaProjectionManager;
    private static MediaProjection mProjection;
    private static Intent dataFromResult;
    private static Intent dataFromResultSS;

    public static HashMap<String, String> storeProducts = new HashMap<>();
    public static HashMap<String, String> storeProductsCommon = new HashMap<>();
    public static List<String> purchasedProducts = new ArrayList<>();
    public static List<String> purchasedBrushes = new ArrayList<>();

    public static DocumentSnapshot snapshot;

    public static List<AppBanner> getAppBanners() {
        return mBanners;
    }

    public static void saveAppBanners(List<AppBanner> banners) {
        mBanners = banners;
    }

    public static HashMap<String, String> getStoreProducts() {
        return storeProducts;
    }

    public static HashMap<String, String> getStoreProductsCommon() {
        return storeProductsCommon;
    }


    public static void savePurchasedProducts(List<String> hashMap) {
        purchasedProducts = hashMap;
    }

    public static List<String> getPurchasedProducts() {
        return purchasedProducts;
    }

    public static void savePurchasedBrushes(List<String> hashMap) {
        purchasedBrushes = hashMap;
    }

    public static List<String> getPurchasedBrushes() {
        return purchasedBrushes;
    }

    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

   /* public static void setLoggedIn(boolean loggedIn) {
        AppUtils.loggedIn = loggedIn;
    }*/

    public static void saveTotalSlides(Context context, int total) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putInt(EXTRA_KEY_TOTAL_SLIDERS, total);
        editor.apply();
    }

    public static void saveTotalBanners(Context context, int total) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putInt(EXTRA_KEY_TOTAL_BANNERS, total);
        editor.apply();
    }

    public static void saveTotalDrawBanners(Context context, int total) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putInt(EXTRA_KEY_TOTAL_DRAW_BANNERS, total);
        editor.apply();
    }

    public static void saveTotalTutBanners(Context context, int total) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putInt(EXTRA_KEY_TOTAL_TUT_BANNERS, total);
        editor.apply();
    }


    public static int getTotalSlides(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return appSharedPrefs.getInt(EXTRA_KEY_TOTAL_SLIDERS, 0);
    }

    public static void saveFacebookLoginSupport(Context context, boolean total) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putBoolean(EXTRA_KEY_FACEBOOK_LOGIN_SUPPORT, total);
        editor.apply();
    }

    public static boolean isFacebookLoginSupport(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return appSharedPrefs.getBoolean(EXTRA_KEY_FACEBOOK_LOGIN_SUPPORT, false);
    }

    public static void saveLink(Context context, String name, String value) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static String getLink(Context context, String name) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String json = appSharedPrefs.getString(name, "");
        if (TextUtils.isEmpty(json)) {
            return "";
        } else {
            return json;
        }
    }

    public static void saveSliders(Context context, List<SlideInfo> slideInfos) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = gson.toJson(slideInfos);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putString(EXTRA_KEY_SLIDERS, json);
        editor.apply();
    }

    public static ArrayList<SlideInfo> getSliders(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(EXTRA_KEY_SLIDERS, "");

        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<SlideInfo>>() {
            }.getType();

            return gson.fromJson(json, type);
        }
    }

    public static void saveBanners(Context context, List<BannerModel> slideInfos) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = gson.toJson(slideInfos);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putString(EXTRA_KEY_BANNERS, json);
        editor.apply();
    }

    public static ArrayList<BannerModel> getBanners(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(EXTRA_KEY_BANNERS, "");

        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<BannerModel>>() {
            }.getType();

            return gson.fromJson(json, type);
        }
    }

    public static void saveDrawBanners(Context context, List<BannerModel> slideInfos) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = gson.toJson(slideInfos);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putString(EXTRA_KEY_DRAW_BANNERS, json);
        editor.apply();
    }

    public static ArrayList<BannerModel> getDrawBanners(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(EXTRA_KEY_DRAW_BANNERS, "");

        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<BannerModel>>() {
            }.getType();

            return gson.fromJson(json, type);
        }
    }

    public static void saveTutBanners(Context context, List<BannerModel> slideInfos) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = gson.toJson(slideInfos);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putString(EXTRA_KEY_TUT_BANNERS, json);
        editor.apply();
    }

    public static ArrayList<BannerModel> getTutBanners(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(EXTRA_KEY_TUT_BANNERS, "");

        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<BannerModel>>() {
            }.getType();

            return gson.fromJson(json, type);
        }
    }


    public static void saveHomeSlideInterval(Context context, int total) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putInt(EXTRA_KEY_HOME_SLIDE_INTERVAL, total);
        editor.apply();
    }

    public static int getHomeSlideInterval(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return appSharedPrefs.getInt(EXTRA_KEY_HOME_SLIDE_INTERVAL, 0);
    }

    public static void saveTotalHomeSlides(Context context, int total) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putInt(EXTRA_KEY_TOTAL_HOME_SLIDERS, total);
        editor.apply();
    }

    public static int getTotalHomeSlides(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return appSharedPrefs.getInt(EXTRA_KEY_TOTAL_HOME_SLIDERS, 0);
    }

    public static void saveHomeSliders(Context context, List<SlideInfo> slideInfos) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = gson.toJson(slideInfos);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putString(EXTRA_KEY_HOME_SLIDERS, json);
        editor.apply();
    }

    public static ArrayList<SlideInfo> getHomeSliders(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(EXTRA_KEY_HOME_SLIDERS, "");

        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<SlideInfo>>() {
            }.getType();

            return gson.fromJson(json, type);
        }
    }

    public static void saveRewardSetup(Context context, RewardSetup rewardSetup) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = gson.toJson(rewardSetup);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putString(EXTRA_KEY_REWARD_SETUP, json);
        editor.apply();
    }

    public static RewardSetup getRewardSetup(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(EXTRA_KEY_REWARD_SETUP, "");

        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            return gson.fromJson(json, RewardSetup.class);
        }
    }

    public static void saveEventSetup(Context context, DocumentSnapshot rewardSetup) {
        snapshot = rewardSetup;
    }

    public static DocumentSnapshot getEventSetup(Context context) {
        return snapshot;
    }


    public static void updateToken(String token) {
        try {
            Log.e("TAG", "Authenticate Update Token Called");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tokens");
                Token token1 = new Token(token);
                reference.child(user.getUid()).setValue(token1);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at update token " + e.getMessage());
        }
    }

    public static String getValueToOneDecimal(Object value) {
        return String.format("%.1f", value);
    }

    public static String getValueToTwoDecimal(Object value) {
        return String.format("%.2f", value);
    }

    public static String getFileNameWithoutExtension(File file) {
        String fileName = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                fileName = name.replaceFirst("[.][^.]+$", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileName = "";
        }

        return fileName;

    }

    public static String getFileNameWithExtension(File file) {
        String fileName = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                fileName = name;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileName = "";
        }

        return fileName;

    }

    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream var2 = new FileInputStream(src);
        FileOutputStream var3 = new FileOutputStream(dst);
        byte[] var4 = new byte[1024];

        int var5;
        while ((var5 = var2.read(var4)) > 0) {
            var3.write(var4, 0, var5);
        }

        var2.close();
        var3.close();
    }

    public static String getLoginType(int loginType) {
        String type = "";
        if (loginType == LoginType.facebook.ordinal()) {
            type = "Facebook";
        } else if (loginType == LoginType.google.ordinal()) {
            type = "google";
        } else if (loginType == LoginType.paintology.ordinal()) {
            type = "Paintology";
        }

        return type;
    }

    public static void setSearchResponse(String response) {
        searchResponse = response;
    }

    public static String getSearchResponse() {
        return searchResponse;
    }

    public static List<NotificationModel> getNotificationsLocally(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(EXTRA_KEY_NOTIFICATIONS, "");

        List<NotificationModel> filterList = new ArrayList<>();
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<NotificationModel>>() {
            }.getType();
//            return gson.fromJson(json, type);

            ArrayList<NotificationModel> list = gson.fromJson(json, type);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filterList = list.stream()
                        .filter(c -> c.getType() != NotificationType.CHAT)
                        .collect(Collectors.toList());
            } else {
                for (NotificationModel model :
                        list) {
                    NotificationType nt = model.getType();
                    if (nt != NotificationType.CHAT) {
                        filterList.add(model);
                    }
                }
            }
        }

        return filterList;
    }

    public static List<NotificationModel> getChatNotificationsLocally(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(EXTRA_KEY_NOTIFICATIONS, "");

        List<NotificationModel> filterList = new ArrayList<>();
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<NotificationModel>>() {
            }.getType();

            ArrayList<NotificationModel> list = gson.fromJson(json, type);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filterList = list.stream()
                        .filter(c -> c.getType() == NotificationType.CHAT)
                        .collect(Collectors.toList());
            } else {
                for (NotificationModel model :
                        list) {
                    NotificationType nt = model.getType();
                    if (nt == NotificationType.CHAT) {
                        filterList.add(model);
                    }
                }
            }

            return filterList;
        }
    }

    public static void saveNotificationLocally(Context context, NotificationModel notification) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        List<NotificationModel> notificationList = getNotificationsLocally(context);
        notificationList.add(notification);

        Gson gson = new Gson();
        String json = gson.toJson(notificationList);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putString(EXTRA_KEY_NOTIFICATIONS, json);
        editor.apply();

        saveHasUnreadNotifications(context, true);
    }

    public static void saveHasUnreadNotifications(Context context, boolean value) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putBoolean(EXTRA_KEY_UNREAD_NOTIFICATIONS, value);
        editor.apply();
    }

    public static boolean hasUnreadNotifications(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return appSharedPrefs.getBoolean(EXTRA_KEY_UNREAD_NOTIFICATIONS, false);
    }

    public static void saveHasUnreadChat(Context context, boolean value) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = appSharedPrefs.edit();
        editor.putBoolean(EXTRA_KEY_UNREAD_CHAT, value);
        editor.apply();
    }

    public static boolean hasUnreadChat(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return appSharedPrefs.getBoolean(EXTRA_KEY_UNREAD_CHAT, false);
    }

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static MediaProjectionManager getMediaProjectionManager() {
        return mMediaProjectionManager;
    }

    public static void setMediaProjectionManager(MediaProjectionManager mediaProjectionManager) {
        AppUtils.mMediaProjectionManager = mediaProjectionManager;
    }

    public static MediaProjection getProjection() {
        return mProjection;
    }

    public static void setProjection(MediaProjection mProjection) {
        AppUtils.mProjection = mProjection;
    }

    public static Intent getDataFromResult() {
        return dataFromResult;
    }

    public static void setDataFromResult(Intent dataFromResult) {
        AppUtils.dataFromResult = dataFromResult;
    }

    public static Intent getDataFromResultSS() {
        return dataFromResultSS;
    }

    public static void setDataFromResultSS(Intent dataFromResultSS) {
        AppUtils.dataFromResultSS = dataFromResultSS;
    }

    public static Point getDeviceResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size);
        String resolution = size.x + "x" + size.y;
        Log.d("DeviceResolution", resolution);
        return size;
    }

    // Generates a random int with n digits
    public static int generateRandomDigits(int n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }
}
