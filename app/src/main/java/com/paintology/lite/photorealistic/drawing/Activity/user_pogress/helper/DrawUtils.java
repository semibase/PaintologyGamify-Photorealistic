package com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Enums.drawing_type;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.painting.PaintItem;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.TraceReference;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DrawUtils {


    public ArrayList<TraceReference> listFromPreference(Context context, StringConstants constants) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");
        String overlayJson = appSharedPrefs.getString(constants.getOverlayList_Gson_Key(), "");
        String importJson = appSharedPrefs.getString(constants.getImportImageList_Gson_Key(), "");

        Log.e("tutorial overlayjson",overlayJson);
        Log.e("tutorial trace",json);
        Type type = new TypeToken<ArrayList<TraceReference>>() {
        }.getType();

        ArrayList<TraceReference> traceList = gson.fromJson(json, type);
        if (traceList == null) {
            traceList = new ArrayList<>();
        }

        if (!TextUtils.isEmpty(overlayJson)) {
            traceList.addAll(gson.fromJson(overlayJson, type));
        }

        if (!TextUtils.isEmpty(importJson)) {
            traceList.addAll(gson.fromJson(importJson, type));
        }
        return traceList;
    }

    public void processMovie(String fileName, Context context, StringConstants constants,String postId) {
        try {
            //First check in painting folder.
            File _file = new File(KGlobal.getDownloadedFolderPath(context) + "/" + fileName);
            if (_file.exists()) {
                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(context);
                if (appSharedPrefs.getString(fileName, null) != null) {
                    String fileNameWithOutExt = fileName.replaceFirst("[.][^.]+$", "");
                    Intent intent = new Intent(context, PaintActivity.class);
                    intent.putExtra("TutorialPath", _file.getAbsolutePath());
                    intent.putExtra("EventFilePath", KGlobal.getDownloadedFolderPath(context) + "/EventData_" + fileNameWithOutExt + ".txt");
                    intent.putExtra("StrokeFilePath", KGlobal.getDownloadedFolderPath(context) + "/StrokeData_" + fileNameWithOutExt + ".txt");
                    intent.setAction("FromTutorialMode");
                    intent.putExtra("OverlaidImagePath", appSharedPrefs.getString(fileName, null));
                    intent.putExtra("id", postId);
                    FirebaseUtils.logEvents(context, constants.LoadStrokeFile);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(context, constants.LoadStrokeFile, Toast.LENGTH_SHORT).show();
                    }
                    context.startActivity(intent);
                } else {
                    String fileNameWithOutExt = fileName.replaceFirst("[.][^.]+$", "");
                    Intent intent = new Intent(context, PaintActivity.class);
                    intent.putExtra("TutorialPath", _file.getAbsolutePath());
                    intent.putExtra("EventFilePath", KGlobal.getDownloadedFolderPath(context) + "/EventData_" + fileNameWithOutExt + ".txt");
                    intent.putExtra("StrokeFilePath", KGlobal.getDownloadedFolderPath(context) + "/StrokeData_" + fileNameWithOutExt + ".txt");
                    intent.setAction("FromTutorialMode");
                    intent.putExtra("id", postId);
                    FirebaseUtils.logEvents(context, constants.LoadStrokeFile);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(context, constants.LoadStrokeFile, Toast.LENGTH_SHORT).show();
                    }
                    context.startActivity(intent);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editDrawing(Activity context, PaintItem item, boolean openInTrace, String postId) {
        StringConstants constants = new StringConstants();
        String str2 = item.getFileName();
        Log.e("tutorial mode",item.get_drawing_type().toString());
        if (item.get_drawing_type().equals(drawing_type.Movie)) {
            processMovie(str2, context, constants,postId);
        } else {
            ArrayList<TraceReference> traceList = listFromPreference(context, constants);
            if (traceList != null) {
                for (int i = 0; i < traceList.size(); i++) {
                    drawing_type type = traceList.get(i).get_drawing_type();
                    File _file_trace = new File(traceList.get(i).getTraceImageName());
                    if (str2.equalsIgnoreCase(traceList.get(i).getUserPaintingName())) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(context, constants.Pick_Image_My_Paintings, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(context, constants.Pick_Image_My_Paintings);

                        if (type.equals(drawing_type.TraceDrawaing)) {
                            Intent intent = new Intent(context, PaintActivity.class);
                            intent.setAction("Reload Painting");
                            if (type.equals(drawing_type.TraceDrawaing))
                                intent.putExtra("isTutorialmode", true);
                            else
                                intent.putExtra("isTutorialmode", false);

                            File file;
                            if (traceList.get(i).isFromPaintologyFolder()) {
                                file = new File(KGlobal.getDefaultFolderPath(context) + "/" + traceList.get(i).getTraceImageName());
                            } else
                                file = new File(traceList.get(i).getTraceImageName());

                            intent.putExtra("path", file.getAbsolutePath());
                            intent.putExtra("drawingPath", str2);
                            intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(context));
                            intent.putExtra("isGrayScale", traceList.get(i).isGrayScale());
                            intent.putExtra("id", postId);
                            context.startActivity(intent);
                        } else if (type.equals(drawing_type.OverlayDrawing)) {
                            String path = KGlobal.getMyPaintingFolderPath(context) + "/" + str2;
                            Intent intent = new Intent(context, PaintActivity.class);
                            intent.setAction("LoadWithoutTrace");
                            intent.putExtra("path", str2);
                            intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(context));
                            intent.putExtra("id", postId);
                            context.startActivity(intent);
                        } else if (type.equals(drawing_type.ImportImage)) {
                            if (openInTrace) {
                                String path = KGlobal.getMyPaintingFolderPath(context) + "/" + str2;
                                Intent lIntent1 = new Intent();
                                lIntent1.setClass(context, PaintActivity.class);
                                lIntent1.setAction("Edit Paint");
                                lIntent1.putExtra("FromLocal", true);
                                lIntent1.putExtra("paint_name", path);
                                lIntent1.putExtra("isOverraid", false);
                                lIntent1.putExtra("id", postId);
                                context.startActivity(lIntent1);
                            } else {
                                Intent intent = new Intent(context, PaintActivity.class);
                                intent.setAction("LoadWithoutTrace");
                                intent.putExtra("path", str2);
                                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(context));
                                intent.putExtra("id", postId);
                                context.startActivity(intent);
                            }
                        }

                        return;
                    } else if (str2.equalsIgnoreCase(_file_trace.getName())) {

                        if (type.equals(drawing_type.TraceDrawaing)) {
                            try {
                                String path = KGlobal.getMyPaintingFolderPath(context) + "/" + str2;
                                Intent lIntent1 = new Intent();
                                lIntent1.setClass(context, PaintActivity.class);
                                lIntent1.setAction("Edit Paint");
                                lIntent1.putExtra("FromLocal", true);
                                lIntent1.putExtra("paint_name", path);
                                lIntent1.putExtra("isOverraid", false);
                                lIntent1.putExtra("id", postId);
                                Log.e("TAGGG", "startDoodle paint_name " + path);
                                context.startActivity(lIntent1);
                            } catch (Exception e) {
                                Log.e("MyPaintingsActivity", e.getMessage());
                            }

                            return;
                        } else if (type.equals(drawing_type.OverlayDrawing)) {
                            try {
                                Intent intent = new Intent(context, PaintActivity.class);
                                intent.setAction("LoadWithoutTrace");
                                intent.putExtra("path", str2);
                                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(context));
                                intent.putExtra("id", postId);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Log.e("MyPaintingsActivity", e.getMessage());
                            }

                            return;
                        } else if (type.equals(drawing_type.ImportImage)) {
                            if (openInTrace) {
                                String path = KGlobal.getMyPaintingFolderPath(context) + "/" + str2;
                                Intent lIntent1 = new Intent();
                                lIntent1.setClass(context, PaintActivity.class);
                                lIntent1.setAction("Edit Paint");
                                lIntent1.putExtra("FromLocal", true);
                                lIntent1.putExtra("paint_name", path);
                                lIntent1.putExtra("isOverraid", false);
                                lIntent1.putExtra("id", postId);
                                context.startActivity(lIntent1);
                            } else {
                                Intent intent = new Intent(context, PaintActivity.class);
                                intent.setAction("LoadWithoutTrace");
                                intent.putExtra("path", str2);
                                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(context));
                                intent.putExtra("id", postId);
                                context.startActivity(intent);
                            }

                            return;
                        }
                    }
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, constants.Pick_Image_My_Paintings, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(context, constants.Pick_Image_My_Paintings);
                Intent intent = new Intent(context, PaintActivity.class);
                intent.setAction("LoadWithoutTrace");
                intent.putExtra("path", str2);
                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(context));
                intent.putExtra("id", postId);
                context.startActivity(intent);

            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, constants.Pick_Image_My_Paintings, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(context, constants.Pick_Image_My_Paintings);
                Intent intent = new Intent(context, PaintActivity.class);
                intent.setAction("LoadWithoutTrace");
                intent.putExtra("path", str2);
                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(context));
                intent.putExtra("id", postId);
                context.startActivity(intent);

            }
        }
    }

}


