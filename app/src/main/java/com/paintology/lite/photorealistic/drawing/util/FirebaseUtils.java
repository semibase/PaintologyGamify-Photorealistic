package com.paintology.lite.photorealistic.drawing.util;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.LeaderBoardRankingModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FirebaseUtils {

    public static Context context;

    public static FirebaseAnalytics firebaseAnalytics;
    public static String gap_event = "gap";
    public static int _index = 0;
    public static int _index_repeated = -1;

    public static String _event_name = "";

    public static HashMap<String, Integer> hashMap = new HashMap<>();

    public static void resetHashMap() {
        hashMap.clear();
    }

    public static void SetInstance(Context c) {
        context = c;
        firebaseAnalytics = FirebaseAnalytics.getInstance(c);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
//        firebaseAnalytics.setMinimumSessionDuration(480000);
        firebaseAnalytics.setSessionTimeoutDuration(480000);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseAnalytics.setUserId(firebaseUser.getUid());
        }

    }

    public static void logEvents(Context context, String event, int tutorial_id) {
        try {
            String upToNCharacters = "";
            if (event.length() >= 40) {
                upToNCharacters = event.substring(0, Math.min(event.length(), 39));
            } else {
                upToNCharacters = event;
            }
            String eventName = upToNCharacters.replaceAll("[^a-zA-Z0-9]", "_");
            if (eventName.equalsIgnoreCase(StringConstants.canvas_draw_stroke)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_strokes_draw_strokes)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_tsstrokes_draw_strokes)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_colorbar_select)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_undo_stroke)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_strokes_undo_strokes)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_tsstrokes_undo_strokes)) {

                if (hashMap.containsKey(eventName)) {
                    int index = hashMap.get(eventName);
                    index++;
                    hashMap.put(eventName, index);
                    if (index % 10 == 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("stroke_count", getIndex(index));
                        if (tutorial_id != -1) {
                            bundle.putString("tutorial_id", String.valueOf(tutorial_id));
                        }
                        ContextKt.sendUserEventWithParam(context,eventName, bundle);
                    }
                } else {
                    hashMap.put(eventName, 1);
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at logevent " + e.getMessage() + e.toString());
        }
    }

    public static void logEvents(Context context, String event) {
        try {
            String upToNCharacters = "";
            if (event.length() >= 40) {
                upToNCharacters = event.substring(0, Math.min(event.length(), 39));
            } else {
                upToNCharacters = event;
            }
            String eventName = upToNCharacters.replaceAll("[^a-zA-Z0-9]", "_");
            if (eventName.equalsIgnoreCase(StringConstants.canvas_draw_stroke)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_strokes_draw_strokes)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_tsstrokes_draw_strokes)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_colorbar_select)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_undo_stroke)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_strokes_undo_strokes)
                    || eventName.equalsIgnoreCase(StringConstants.canvas_tsstrokes_undo_strokes)) {
                if (_index > 0 && !_event_name.equalsIgnoreCase(eventName)) {
                    KGlobal._session_model.get_event_list().add(_event_name + "_" + _index);
                    sendEvent(context, getEventName(_event_name, _index));
                    _index = 1;
                    _event_name = eventName;
                } else {
                    _event_name = eventName;
                    _index = _index + 1;
                }
            } else {
                if (_index > 0) {
                    KGlobal._session_model.get_event_list().add(_event_name + "_" + _index);
                    sendEvent(context, getEventName(_event_name, _index));
                    _index = 0;
                    _event_name = "";
                    KGlobal._session_model.get_event_list().add(eventName);
                    sendEvent(context, eventName);
                } else {
                    KGlobal._session_model.get_event_list().add(eventName);
                    sendEvent(context, eventName);
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at logevent " + e.getMessage() + e.toString());
        }
    }


    static void sendEvent(Context context, String eventName) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("Action", eventName);
            if (firebaseAnalytics == null) {
                SetInstance(context);
            }
            Log.e("TAGG", "Analytics Sent Events " + eventName);
            firebaseAnalytics.logEvent(eventName, bundle);
        } catch (Exception e) {
            Log.e("TAGG", "Exception in send event " + e.getMessage());
        }
    }

    public static String getIndex(int _index) {
        int answer = ((_index) / 10) * 10;
        return String.valueOf(_index);
    }

    public static String getEventName(String eventName, int _index) {
        String _name = "";
        int answer = ((_index + 10) / 10) * 10;
        _name = eventName + "_" + answer;
        Log.e("TAGG", "getEventName " + _name + " _index " + _index);
        return _name;
    }

    public static String getShortCategoryNameForEvent(String originalEvent, String suffixText) {
        String newEvent = originalEvent;

        if (originalEvent.contains("paint_by_number")) {
            newEvent = originalEvent.replace("paint_by_number", "paint_" + suffixText);
        } else if (originalEvent.contains("paint_by_numbers")) {
            newEvent = originalEvent.replace("paint_by_numbers", "paint_" + suffixText);
        } else if (originalEvent.contains("connect_the_dots")) {
            newEvent = originalEvent.replace("connect_the_dots", "conne_" + suffixText);
        } else if (originalEvent.contains("absolute_beginners")) {
            newEvent = originalEvent.replace("absolute_beginners", "absol_" + suffixText);
        } else if (originalEvent.contains("beginners_tutorials")) {
            newEvent = originalEvent.replace("beginners_tutorials", "begin_" + suffixText);
        } else if (originalEvent.contains("intermediate_tutorials")) {
            newEvent = originalEvent.replace("intermediate_tutorials", "inter_" + suffixText);
        } else if (originalEvent.contains("advanced_tutorials")) {
            newEvent = originalEvent.replace("advanced_tutorials", "advan_" + suffixText);
        } else if (originalEvent.contains("pencil_drawing")) {
            newEvent = originalEvent.replace("pencil_drawing", "penci_" + suffixText);
        } else if (originalEvent.contains("landscape_tutorials")) {
            newEvent = originalEvent.replace("landscape_tutorials", "lands_" + suffixText);
        } else if (originalEvent.contains("portrait_tutorials")) {
            newEvent = originalEvent.replace("portrait_tutorials", "portr_" + suffixText);
        } else if (originalEvent.contains("help_amp_guide")) {
            newEvent = originalEvent.replace("help_amp_guide", "helpg_" + suffixText);
        } else if (originalEvent.contains("whats_new")) {
            newEvent = originalEvent.replace("whats_new", "whnew_" + suffixText);
        } else if (originalEvent.contains("articles")) {
            newEvent = originalEvent.replace("articles", "artic_" + suffixText);
        } else if (originalEvent.contains("traditional_medium")) {
            newEvent = originalEvent.replace("traditional_medium", "tradi_" + suffixText);
        } else if (originalEvent.contains("paintology")) {
            newEvent = originalEvent.replace("paintology", "paint_" + suffixText);
        } else if (originalEvent.contains("community")) {
            newEvent = originalEvent.replace("community", "commu_" + suffixText);
        } else if (originalEvent.contains("3d_drawing")) {
            newEvent = originalEvent.replace("3d_drawing", "3draw_" + suffixText);
        } else if (originalEvent.contains("trace_drawing")) {
            newEvent = originalEvent.replace("trace_drawing", "trace_" + suffixText);
        } else if (originalEvent.contains("photorealistic_drawings")) {
            newEvent = originalEvent.replace("photorealistic_drawings", "photo_" + suffixText);
        } else if (originalEvent.contains("block_coloring")) {
            newEvent = originalEvent.replace("block_coloring", "block_" + suffixText);
        }

        return newEvent;
    }

    public static String getCurrentUserId(Context ct) {
        StringConstants constants = new StringConstants();
        return constants.getString(constants.UserId, ct);
    }

    public static boolean isYOU(Context ct, String id) {
        //Match current logged in user-id with id
        return TextUtils.equals(id, getCurrentUserId(ct));
    }

    public static void getUsersByPoints(String countryCode, final FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .whereGreaterThan("points", 0)
                .limit(101);
        if (countryCode != null) {
            query = query.whereEqualTo("country", countryCode);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<LeaderBoardRankingModel> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Log.d("Firebase UTILS : ", "USERS: " + document.getData());

                    String name = document.getString("name");
                    String avatar = document.getString("avatar");
                    Number points = document.getLong("points");
                    String level = document.getString("level");
                    String externalId = document.getString("external_id");


                    var pts = 0;

                    if (points != null) {
                        pts = points.intValue();
                    }

                    Log.d("USERS: ", "" + pts);

                    LeaderBoardRankingModel user = new LeaderBoardRankingModel(
                            document.getId(),
                            externalId != null ? externalId : "",
                            avatar != null ? avatar : "default_avatar_url",  // Handle null avatar
                            name != null ? name : "Unknown",  // Handle null name
                            0,  // Static awards count
                            pts, // Points,
                            level
                    );

                    users.add(user);
                }

                callback.onCallback(users);

            } else {
                Log.e("FIREBASE_UTILS", "Error getting documents: ", task.getException());
            }
        });
    }

    public static void getCurrentUsersPoints(final FirebaseCallbackCurrentUsersPoints callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = getCurrentUserId(context);
        if (TextUtils.equals(userId, "")) {
            callback.onCallback(null);
            return;
        }
        try {
            db.collection("users")
                    .orderBy("points", Query.Direction.DESCENDING)
                    .whereEqualTo("external_id", userId)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        String res = task.getResult().getDocuments().toString();
                        Log.d("FIREBASE_UTILS", "getCurrentUsersPoints: " + res);

                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                callback.onCallback(null);
                                return;
                            }
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String name = document.getString("name");
                            String avatar = document.getString("avatar");
                            Number points = document.getLong("points");
                            String externalId = document.getString("external_id");
                            String level = document.getString("level");

                            var pts = 0;
                            if (points != null) {
                                pts = points.intValue();
                            }

                            LeaderBoardRankingModel user = new LeaderBoardRankingModel(
                                    document.getId(),
                                    externalId != null ? externalId : "",
                                    avatar != null ? avatar : "default_avatar_url",  // Handle null avatar
                                    name != null ? name : "Unknown",  // Handle null name
                                    0,  // Static awards count
                                    pts, // Points,
                                    level
                            );
                            callback.onCallback(user);
                        } else {
                            Log.e("FIREBASE_UTILS", "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onCallback(null);
        }
    }

    public interface FirebaseCallback {
        void onCallback(List<LeaderBoardRankingModel> userList);
    }

    public interface FirebaseCallbackCurrentUsersPoints {
        void onCallback(LeaderBoardRankingModel user);
    }
}
