package com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.Activity.utils.ExtensionsKt;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.NewSubCategoryActivity;
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

public class TutorialUtils {
    Context context;

    public TutorialUtils(Context context) {
        this.context = context;
    }


    public void gotoUrl(String url) {
        try {
            Intent viewIntent =
                    new Intent("android.intent.action.VIEW",
                            Uri.parse(url));
            context.startActivity(viewIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void parseTutorial(@Nullable String itemId) {

        if (itemId == null) {
            FireUtils.hideProgressDialog();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("tutorial_id", itemId);
        ContextKt.sendUserEventWithParam(context, StringConstants.tutorials_open, bundle);

        FirebaseFirestoreApi.getTutorialDetail(itemId).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                if (ds.contains("type")) {

                    FirebaseFirestoreApi.viewTutorial(itemId);

                    String canvas_color = ds.get("canvas_color").toString();
                    String type = ds.get("type").toString();


                    Map<String, Object> data = ds.getData();

                    Map<String, Object> links = (Map<String, Object>) data.get("links");
                    String redirect = "";
                    if (links != null && links.get("redirect") != null) {
                        redirect = links.get("redirect").toString();
                    }

                    if (links != null && links.get("external") != null) {
                        if (!links.get("external").toString().equalsIgnoreCase("")) {
                            if (context instanceof GalleryDashboard) {
                                FireUtils.hideProgressDialog();
                                gotoUrl(links.get("external").toString());
                                return;
                            }
                        }
                    }


                    Map<String, Object> files = (Map<String, Object>) data.get("files");
                    if (!type.isEmpty() || !redirect.isEmpty()) {


                        String textFileName = "";
                        String textFile = "";
                        String textFileName2 = "";
                        String textFile2 = "";
                        if (files.get("text_file_1") != null) {
                            Map<String, Object> text1 = (Map<String, Object>) files.get("text_file_1");
                            textFileName = text1.get("name").toString();
                            textFile = text1.get("url").toString();
                        }

                        if (files.get("text_file_2") != null) {
                            Map<String, Object> text1 = (Map<String, Object>) files.get("text_file_2");
                            textFileName2 = text1.get("name").toString();
                            textFile2 = text1.get("url").toString();
                        }


                        String youtubeLink = "";
                        if (links.get("youtube") != null) {
                            youtubeLink = links.get("youtube").toString();
                        }

                        Map<String, Object> images = (Map<String, Object>) data.get("images");
                        String image = "";
                        if (images.get("content") != null) {
                            image = images.get("content").toString();
                        }


                        Map<String, Object> options = (Map<String, Object>) data.get("options");
                        boolean singleTap = (boolean) options.get("single_tap");
                        boolean line = (boolean) options.get("straight_lines");
                        boolean greyScal = (boolean) options.get("grayscale");

                        boolean block_colorin = false;
                        if (options.containsKey("block_coloring")) {
                            block_colorin = (boolean) options.get("block_coloring");
                        }


                        SharedPreferences sharedPref = context.getSharedPreferences("brush", 0);

                        SharedPreferences.Editor editor = sharedPref.edit();
                        Log.e("greyScal", greyScal + "");
                        editor.putBoolean("singleTap", singleTap);
                        editor.putBoolean("line", line);
                        editor.putBoolean("gray_scale", greyScal);
                        editor.putBoolean("block_coloring", block_colorin);
                        editor.commit();
                        editor.apply();


                        Map<String, Object> brush = (Map<String, Object>) options.get("brush");
                        Float mPrefBrushSize = Float.valueOf(brush.get("size").toString());

                        String brushcolor = brush.get("color").toString();
                        Long mPrefAlpha1 = (Long) brush.get("density");
                        int mPrefAlpha = Integer.valueOf(mPrefAlpha1.toString());
                        mPrefAlpha = (255 * mPrefAlpha) / 100;
                        Long mPrefFlow1 = (Long) brush.get("hardness");
                        int mPrefFlow = Integer.valueOf(mPrefFlow1.toString());
                        mPrefFlow = (255 * mPrefFlow) / 100;
                        SharedPreferences.Editor lEditor1 = context.getSharedPreferences("brush", 0).edit();

                        String brushMode = brush.get("type").toString();
                        int mPrefBrushStyle = getBrushMode(brushMode);
                        lEditor1.putString("pref-saved-tutorials", "yes");
                        lEditor1.putString("pref-saved", "yes");
                        // lEditor1.putInt("background-color", mPrefBackgroundColor);
                        lEditor1.putInt("brush-style", mPrefBrushStyle);
                        lEditor1.putFloat("brush-size", mPrefBrushSize);
                        lEditor1.putInt("brush-color", Color.parseColor(brushcolor));
                        //   lEditor1.putInt("brush-mode", mPrefBrushMode);
                        lEditor1.putInt("brush-alpha", mPrefAlpha);
                        lEditor1.putInt("brush-pressure", mPrefAlpha);
                        lEditor1.putInt("brush-flow", mPrefFlow);
                        lEditor1.commit();

                        ArrayList<String> colorSwatch = (ArrayList<String>) data.get("color_swatch");
                        Log.e("swatchesArray size", colorSwatch.size() + "");
                        ArrayList<String> swatchesArray = new ArrayList<>();
                        for (int i = 0; i < colorSwatch.size(); i++) {
                            //   String index = String.valueOf(i);
                            if (colorSwatch.get(i) != null) {
                                swatchesArray.add(colorSwatch.get(i).toString());
                                Log.e("swatchesArray ", i + " : " + swatchesArray.get(i));
                            } else {
                                break;
                            }

                        }

                        ArrayList<ColorSwatch> swatches = new ArrayList<>();

                        if (swatchesArray != null && swatchesArray.size() > 0) {
                            for (int i = 0; i < swatchesArray.size(); i++) {

                                String swatch = swatchesArray.get(i);
                                ColorSwatch colorSwatchItem = new ColorSwatch();
                                colorSwatchItem.setColor_swatch(swatch);
                                swatches.add(colorSwatchItem);
                            }

                        }


                        if (!textFileName.isEmpty()) {
                            String _youtube_id = "";
                            if (!youtubeLink.isEmpty()) {
                                _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "").replace("?feature=share", "").replace("https://youtube.com/shorts/", "");
                            }
                            new DownloadsTextFilesFirebase(youtubeLink, canvas_color, itemId, swatches, textFileName, textFile, textFileName2, textFile2, context).execute();

                        } else {

                            if (type.equalsIgnoreCase("trace") || type.equalsIgnoreCase("overlay") || type.equalsIgnoreCase("blank")) {
                                try {

                                    Boolean isTrace = false;
                                    if (type.equalsIgnoreCase("trace")) {
                                        isTrace = false;
                                    }
                                    String _youtube_id = "";
                                    if (!youtubeLink.isEmpty()) {
                                        _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "").replace("?feature=share", "").replace("https://youtube.com/shorts/", "");
                                    }


                                    String fileName = image.substring(image.lastIndexOf('/') + 1);
                                    Log.e("newfilename", fileName);
                                    if (fileName.contains("token")) {
                                        //  String temp = image.substring(image.lastIndexOf('/') + 1);
                                        fileName = fileName.substring(0, fileName.indexOf("?"));

                                        /* fileName = fileName.split(".")[0];*/
                                        //fileName+=".jpg";
                                        Log.e("newfilename", fileName);
                                    }
                                    File file = new File(KGlobal.getTraceImageFolderPath(context) + "/" + fileName);
                                    if (!file.exists())
                                        new DownloadsImageFirebase(_youtube_id, image, isTrace, fileName, canvas_color, itemId, swatches, type, context).execute(image);
                                    else {
//                                if (_object.getPost_title() != null)
//                                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());


                                        FireUtils.hideProgressDialog();

                                        openTutorialsRewardPoint(itemId);
                                        Log.e("PaintActivity", "Paint Flow 3");
                                        StringConstants.IsFromDetailPage = false;
                                        Intent intent = new Intent(context, PaintActivity.class);
                                       /* if (!_youtube_id.isEmpty()) {
                                            intent.putExtra("youtube_video_id", _youtube_id);
                                            intent.setAction("YOUTUBE_TUTORIAL");
                                            intent.putExtra("paint_name", file.getAbsolutePath());
                                            intent.putExtra("tutorial_type", type);
                                        } else {*/
                                        if (type.equalsIgnoreCase("trace")) {
                                            intent.setAction("Edit Paint");
                                            intent.putExtra("paint_name", file.getAbsolutePath());
                                            intent.putExtra("FromLocal", true);
                                            //intent.setAction("LoadWithoutTrace");
                                        } else if (type.equalsIgnoreCase("blank")) {
                                            intent.setAction("New Paint");
                                            //intent.setAction("LoadWithoutTrace");
                                            intent.putExtra("isPickFromOverlaid", true);
                                        } else {
                                            intent.setAction("LoadWithoutTrace");
                                            intent.putExtra("isPickFromOverlaid", true);
                                        }
                                        if (!_youtube_id.isEmpty()) {
                                            intent.putExtra("youtube_video_id", _youtube_id);
                                        }
                                        intent.putExtra("path", fileName);
                                        intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(context));
                                        //}
                                        intent.putExtra("drawingType", "TUTORAILS");
                                        if (swatches.size() > 0) {
                                            Gson gson = new Gson();
                                            String swatchesJson = gson.toJson(swatches);

                                            intent.putExtra("swatches", swatchesJson);
                                        }
                                        // if (!_object.getCanvas_color().isEmpty()) {
                                        intent.putExtra("canvas_color", canvas_color);
                                        //}
                                        intent.putExtra("id", itemId);
                                        context.startActivity(intent);


                                    }


                                } catch (Exception e) {
                                    Toast.makeText(context, "Failed To Load!", Toast.LENGTH_SHORT).show();
                                    FireUtils.hideProgressDialog();
                                    if (!(context instanceof NewSubCategoryActivity)) {
                                        if (itemId.matches("\\d+")) {
                                            context.startActivity(new Intent(context, CategoryActivity.class).putExtra("cate_id", itemId));
                                        } else {
                                            ExtensionsKt.openActivity(context, CategoryActivity.class);
                                        }
                                    }
                                }
                            } else {
                                FireUtils.hideProgressDialog();
                                Toast.makeText(context, "Failed To Load!", Toast.LENGTH_SHORT).show();
                                if (!(context instanceof NewSubCategoryActivity)) {
                                    if (itemId.matches("\\d+")) {
                                        context.startActivity(new Intent(context, CategoryActivity.class).putExtra("cate_id", itemId));
                                    } else {
                                        ExtensionsKt.openActivity(context, CategoryActivity.class);
                                    }
                                }
                            }
                        }
                    } else {
                        FireUtils.hideProgressDialog();
                        if (links.get("external") != null) {
                            String ref = links.get("external").toString();
                            KGlobal.openInBrowser(context, ref);
                        } else {
                            String ref = ds.get("ref").toString();
                            KGlobal.openInBrowser(context, ref);
                        }
                    }
                } else {
                    FireUtils.hideProgressDialog();
                    Toast.makeText(context, "Tutorial Data Not Found", Toast.LENGTH_SHORT).show();
                    if (!(context instanceof NewSubCategoryActivity)) {
                        if (itemId.matches("\\d+")) {
                            context.startActivity(new Intent(context, CategoryActivity.class).putExtra("cate_id", itemId));
                        } else {
                            ExtensionsKt.openActivity(context, CategoryActivity.class);
                        }
                    }
                }
            }

        }).addOnFailureListener(e -> {
            FireUtils.hideProgressDialog();
            Toast.makeText(context, "Tutorial Data Not Found", Toast.LENGTH_SHORT).show();
            if (!(context instanceof NewSubCategoryActivity)) {
                if (itemId.matches("\\d+")) {
                    context.startActivity(new Intent(context, CategoryActivity.class).putExtra("cate_id", itemId));
                } else {
                    ExtensionsKt.openActivity(context, CategoryActivity.class);
                }
            }
        });

    }

    private int getBrushMode(String name) {
        int mode = 0;

        switch (name) {
            case "sticks": {
                mode = 528;
                break;
            }

            case "meadow": {
                mode = 656;
                break;
            }

            case "haze light": {
                mode = 640;
                break;
            }

            case "haze dark": {
                mode = 642;
                break;
            }

            case "line": {
                mode = 81;
                break;
            }

            case "mist": {
                mode = 784;
                break;
            }

            case "land patch": {
                mode = 608;
                break;
            }

            case "grass": {
                mode = 624;
                break;
            }
            case "industry": {
                mode = 768;
                break;
            }

            case "chalk": {
                mode = 512;
                break;
            }

            case "charcoal": {
                mode = 576;
                break;
            }

            case "flower": {
                mode = 592;
                break;
            }

            case "wave": {
                mode = 560;
                break;
            }

            case "eraser": {
                mode = 112;
                break;
            }


            case "shade": {
                mode = 80;
                break;
            }


            case "watercolor": {
                mode = 55;
                break;
            }

            case "sketch oval": {
                mode = 272;
                break;
            }


            case "sketch fill": {
                mode = 256;
                break;
            }

            case "sketch pen": {
                mode = 264;
                break;
            }

            case "sketch wire": {
                mode = 257;
                break;
            }

            case "emboss": {
                mode = 96;
                break;
            }

            case "rainbow": {
                mode = 39;
                break;
            }

            case "inkpen": {
                mode = 56;
                break;
            }

            case "fountain": {
                mode = 561;
                break;
            }

            case "lane": {
                mode = 559;
                break;
            }

            case "streak": {
                mode = 562;
                break;
            }


            case "foliage": {
                mode = 563;
                break;
            }

            case "felt": {
                mode = 45;
                break;
            }

            case "halo": {
                mode = 46;
                break;
            }

            case "outline": {
                mode = 47;
                break;
            }

            case "cube line": {
                mode = 54;
                break;
            }

            case "dash line": {
                mode = 48;
                break;
            }
        }

        Log.e("brushsize mode", mode + "");

        return mode;
    }

    private void openTutorialsRewardPoint(String mId) {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.open_tutorial, mId);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(context);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "opening_tutorials",
                        rewardSetup.getOpening_tutorials() == null ? 0 : rewardSetup.getOpening_tutorials(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

    public class DownloadsImageFirebase extends AsyncTask<String, Void, String> {

        String youtubeLink, traceImageLink, canvas, fileName, id, type;

        Boolean isFromTrace = false;

        ArrayList<ColorSwatch> swatches;

        Context activity;


        public DownloadsImageFirebase(String youtubeLink, String traceImageLink, Boolean isFromTrace, String fileName, String canvas, String id, ArrayList<ColorSwatch> swatches, String type, Context activity) {

            this.youtubeLink = youtubeLink;
            this.traceImageLink = traceImageLink;
            this.isFromTrace = isFromTrace;
            this.canvas = canvas;
            this.id = id;
            this.fileName = fileName;
            this.swatches = swatches;
            this.type = type;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Create Path to save Image
            File path = new File(KGlobal.getTraceImageFolderPath(activity)); //Creates app specific folder

            if (!path.exists()) {
                path.mkdirs();
            }
            //File imageFile = new File(path, traceImageLink.substring(traceImageLink.lastIndexOf('/') + 1)); // Imagename.png
            File imageFile = new File(path, fileName); // Imagename.png
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
            } catch (Exception e) {
                Log.e("TAGG", "Exception at download " + e.getMessage());
            }
            return imageFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            try {

                FireUtils.hideProgressDialog();
//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                Log.e("newfilename", path);

                openTutorialsRewardPoint(id);

            /*    if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false;
                    Log.e("PaintActivity", "Paint Flow 7");
                    Intent intent = new Intent(NewSubCategoryActivity.this, PaintActivity.class);
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("drawingType", "TUTORAILS");
                    intent.putExtra("paint_name", path);
                   // if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", canvas);
                    //}
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                else {
                    StringConstants.IsFromDetailPage = false;
                    Log.e("PaintActivity", "Paint Flow 8");
                    Intent intent = new Intent(NewSubCategoryActivity.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("drawingType", "TUTORAILS");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(NewSubCategoryActivity.this));
                    intent.putExtra("youtube_video_id", youtubeLink);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", canvas);
                    }

                    List<ColorSwatch> swatches = _object.getSwatches();

                   *//* Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", id);*//*
                    startActivity(intent);
                }*/
                Intent intent = new Intent(activity, PaintActivity.class);

               /* if(!youtubeLink.isEmpty()) {
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("paint_name", path);
                    intent.putExtra("tutorial_type", type);
                }else{*/
                if (type.equalsIgnoreCase("trace")) {

                    Log.e("Tag visit", "trace");

                    intent.setAction("Edit Paint");
                    // intent.setAction("LoadWithoutTrace");
                } else if (type.equalsIgnoreCase("blank")) {
                    intent.setAction("New Paint");
                    //intent.setAction("LoadWithoutTrace");
                } else {
                    intent.setAction("LoadWithoutTrace");
                }
                if (type.equalsIgnoreCase("trace")) {
                    intent.setAction("Edit Paint");
                    intent.putExtra("paint_name", path);
                    intent.putExtra("FromLocal", true);
                    // intent.setAction("LoadWithoutTrace");
                } else if (type.equalsIgnoreCase("blank")) {
                    intent.setAction("New Paint");
                    intent.putExtra("isPickFromOverlaid", true);
                    //intent.setAction("LoadWithoutTrace");
                } else {
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("isPickFromOverlaid", true);
                }

                if (!youtubeLink.isEmpty()) {
                    intent.putExtra("youtube_video_id", youtubeLink);
                }

                intent.putExtra("path", fileName);
                intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(activity));
                //}
                intent.putExtra("drawingType", "TUTORAILS");

                // if (!_object.getCanvas_color().isEmpty()) {
                intent.putExtra("canvas_color", canvas);
                //}

                if (swatches.size() > 0) {
                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                }

                intent.putExtra("id", id);

                Log.e("Tag visit", "start acticity");

                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    class DownloadsTextFilesFirebase extends AsyncTask<Void, Void, ArrayList<String>> {


        String youtubeLink, canvas, textFileName, textUrl, textfileName2, texturl2, id;

        ArrayList<ColorSwatch> swatches;
        Context activity;

        public DownloadsTextFilesFirebase(String youtubeLink, String canvas, String id, ArrayList<ColorSwatch> swatches, String textFileName, String textUrl, String textfileName2, String texturl2, Context activity) {
            this.youtubeLink = youtubeLink;
            this.canvas = canvas;
            this.id = id;
            this.swatches = swatches;
            this.textFileName = textFileName;
            this.textfileName2 = textfileName2;
            this.textUrl = textUrl;
            this.texturl2 = texturl2;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(activity));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = "";

                String fileName = "";


                if (i == 0) {
                    textFileLink = textUrl;
                    fileName = textFileName;
                } else {
                    textFileLink = texturl2;
                    fileName = textfileName2;
                }

                File file = new File(file1, fileName);
                if (file.exists()) {
                    lst_fileNames.add(file.getAbsolutePath());
                } else {
                    try {
                        URL url = new URL(textFileLink);
                        URLConnection ucon = url.openConnection();
                        ucon.setReadTimeout(50000);
                        ucon.setConnectTimeout(100000);
                        InputStream is = ucon.getInputStream();
                        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

                        if (file.exists()) {
                            lst_fileNames.add(file.getAbsolutePath());
                            break;
                        }
                        FileOutputStream outStream = new FileOutputStream(file);
                        byte[] buff = new byte[5 * 1024];

                        int len;
                        while ((len = inStream.read(buff)) != -1) {
                            outStream.write(buff, 0, len);
                        }
                        outStream.flush();
                        outStream.close();
                        inStream.close();
                        lst_fileNames.add(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return lst_fileNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);
            try {

                FireUtils.hideProgressDialog();

                openTutorialsRewardPoint(id);

                StringConstants.IsFromDetailPage = false;
                Log.e("PaintActivity", "Paint Flow 9");
                Intent intent = new Intent(activity, PaintActivity.class);

                intent.putExtra("drawingType", "TUTORAILS");
                if (!canvas.isEmpty()) {
                    intent.putExtra("canvas_color", canvas);
                }
                // String youtubeLink = yout;
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    intent.putExtra("youtube_video_id", _youtube_id);
                }
                intent.setAction("YOUTUBE_TUTORIAL_WITH_FILE");
                if (list.size() == 2) {
                    intent.putExtra("StrokeFilePath", list.get(0));
                    intent.putExtra("EventFilePath", list.get(1));
                } else
                    Toast.makeText(activity, "Stroke Event File Not Downloaded Properly", Toast.LENGTH_SHORT).show();

//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                intent.putExtra("id", id);

                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception " + e.getMessage());
            }
        }
    }

}
