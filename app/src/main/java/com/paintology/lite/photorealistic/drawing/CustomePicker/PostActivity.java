package com.paintology.lite.photorealistic.drawing.CustomePicker;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity;
import com.paintology.lite.photorealistic.drawing.Activity.shared_pref.SharedPref;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Community.Community;
import com.paintology.lite.photorealistic.drawing.Model.AlbumImage;
import com.paintology.lite.photorealistic.drawing.Model.ReserveHashTag;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.databinding.DialogShoPostBinding;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.room.daos.PublishDao;
import com.paintology.lite.photorealistic.drawing.room.entities.PublishEntity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.LoadingDialog;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.PostInterface;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PostActivity extends AppCompatActivity implements PostInterface {


    RecyclerView recyclerView;


    LoadingDialog loadingDialog;

    public PostInterface ObjpostImage;
    show_selected_items_adapter mAdapter;
    StringConstants constants = new StringConstants();
    ArrayList<AlbumImage> _list;

    ApiInterface apiInterface;
    boolean isFromCanvasMode = false;

    StringConstants _constants = new StringConstants();

    ArrayList<String> _lst_hashTag = new ArrayList<>();
    String isPostGallery;
    String drawingType, youtubeVideoId, path, parentFolderPath, tutorialID;
    Boolean isFromDrawing, isTutorialMode;
    String _ip, _country, _city;
    SharedPref sharedPref;

    String imagePath, paintingFileName;
    StorageReference storageReference;
    Boolean isFromMyPainting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Post");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loadingDialog = new LoadingDialog(PostActivity.this);

        toolbar.setNavigationIcon(R.drawable.back_arrow);

        if (BuildConfig.DEBUG) {
            Toast.makeText(PostActivity.this, constants.community_posting_image_screen, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.community_posting_image_screen);
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        storageReference = FirebaseStorage.getInstance().getReference().child("communityPost");
        sharedPref = new SharedPref(PostActivity.this);
        _ip = sharedPref.getString("userIp", "");
        _city = sharedPref.getString("userCity", "");
        _country = sharedPref.getString("userCountry", "");

        getIntentData();
        Intent intent = getIntent();

        // check whether user want to post on gallery or community
        isPostGallery = intent.getStringExtra("isPostGallery");

        if (isPostGallery == null) {
            isPostGallery = "post_community";
        }

        isTutorialMode = intent.getBooleanExtra("isTutorialMode", false);
        drawingType = intent.getStringExtra("drawingType");
        tutorialID = intent.getStringExtra("referenceId");
        youtubeVideoId = intent.getStringExtra("youtube_video_id");
        path = intent.getStringExtra("path");
        parentFolderPath = intent.getStringExtra("ParentFolderPath");

        if (tutorialID == null || tutorialID.isEmpty() || tutorialID.equals("null")) {
            tutorialID = "";
        }
        // default value will be freehand
        if (drawingType == null || drawingType.equals("")) {
            drawingType = "freehand";
        } else if (drawingType.equals("tutorials")) {
            youtubeVideoId = "https://youtu.be/" + youtubeVideoId;
        }
        isFromDrawing = intent.getBooleanExtra("isFromDrawing", false);

      /*  String data = _constants.getString(_constants.hashTagList, this);
        if (data == null || data.isEmpty()) {
//            getReservedHashTag();
        } else {
            Gson gson = new Gson();
            _lst_hashTag = gson.fromJson(data, ArrayList.class);
        }
*/

        ObjpostImage = this;
        recyclerView = findViewById(R.id.rv_post_image);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


        if (getIntent().hasExtra("result")) {
            final ArrayList<String> selectedImage = getIntent().getStringArrayListExtra("result");
            setupRecyclerView(selectedImage);
        }
        if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase("from_canvas")) {
            isFromCanvasMode = true;
        }

        AppUtils.hideKeyboard(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        isFromMyPainting = intent.getBooleanExtra("isFromMyPainting", false);
        paintingFileName = intent.getStringExtra("fileName");
    }

    void setupRecyclerView(ArrayList<String> mediaList) {
        try {
            _list = new ArrayList<>();

            ArrayList<String> _lst_art_fav = new ArrayList<>();
            ArrayList<String> _lst_art_med = new ArrayList<>();
            try {
                _lst_art_med.add(getResources().getStringArray(R.array.art_medium)[getResources().getStringArray(R.array.art_medium).length - 1]);
            } catch (Exception ignored) {
            }
            try {
                _lst_art_fav.add(getResources().getStringArray(R.array.arr_art_fav)[getResources().getStringArray(R.array.arr_art_fav).length - 1]);
            } catch (Exception ignored) {
            }

            try {
                for (int i = 0; i < mediaList.size(); i++) {
                    AlbumImage obj = new AlbumImage();

                    obj.setFilePath(mediaList.get(i));
                    String filename = new File(mediaList.get(i)).getName();
                    obj.setFileName(filename);

                    obj.setStr_art_fav(_lst_art_fav.get(0));
                    obj.setArtFavList(_lst_art_fav);

                    obj.setStr_art_med(_lst_art_med.get(0));
                    obj.setArtMediumList(_lst_art_med);

                    obj.setArt_ability(getResources().getStringArray(R.array.arr_art_ability)[getResources().getStringArray(R.array.arr_art_ability).length - 1]);

                    _list.add(obj);
                }
            } catch (Exception e) {
                Log.e("TAGG", "Exception " + e.getMessage());
            }
            try {
                mAdapter = new show_selected_items_adapter(isPostGallery, _list, PostActivity.this, this, false, _lst_hashTag);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, VERTICAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.addItemDecoration(new MarginDecoration(getApplicationContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);
                setupCount();
            } catch (Exception ignored) {

            }
        } catch (Exception ignored) {

        }

    }

    void setupCount() {
        try {
            if (isPostGallery.equalsIgnoreCase("post_gallery")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.post_to_gallery) + " (" + _list.size() + ")");
            } else {
                getSupportActionBar().setTitle(getString(R.string.ss_post_to_community) + " (" + _list.size() + ")");
            }
            if (_list.size() == 0)
                finish();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void postImage(int position, String title, String description, String hashTag, String _youtube_url) {

        loadingDialog.ShowPleaseWaitDialog("Uploading...");

        AppUtils.hideKeyboard(this);

        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(position, 0);
        }

        if (BuildConfig.DEBUG) {
            Toast.makeText(PostActivity.this, constants.post_image_click, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(PostActivity.this, constants.post_image_click);
        mAdapter.updateStatus(position, 1);

        if (Objects.equals(isPostGallery, "post_gallery")) {

            String imagePath = _list.get(position).getFilePath();
            String actualImagePath = KGlobal.getTraceImageFolderPath(this) + "/" + path;
            Log.e("actualImagePath", "postImage: " + actualImagePath + "imagePath" + imagePath);

            List<String> tags = extractTags(hashTag);

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseFirestoreApi.uploadImageToStorage(imagePath).addOnSuccessListener(downloadUri -> {
                    // Replace null values with empty strings or "0"
                    String safeTitle = title != null ? title : "";
                    String safeDescription = description != null ? description : "";
                    String safeDrawingType = drawingType != null ? drawingType : "";
                    String safePath = path != null ? path : "";
                    String safeParentFolderPath = parentFolderPath != null ? parentFolderPath : "";
                    String safeTutorialID = tutorialID;

                    FirebaseFirestoreApi.drawingPostFunction(
                                    safeTitle,
                                    safeDescription,
                                    downloadUri != null ? downloadUri.toString() : "",
                                    safeDrawingType,
                                    "",
                                    tags,
                                    safePath,
                                    safeParentFolderPath,
                                    safeTutorialID,
                                    _youtube_url)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Function call was successful
                                    Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                                    Log.e("FirebaseTest", "Drawing post result: " + result);

                                    try {
                                        if (!tutorialID.equalsIgnoreCase("") && !tutorialID.equalsIgnoreCase("-1")) {
                                            FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.save_drawing, safeTutorialID);
                                            FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.post_drawing_to_gallery, safeTutorialID);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.post_to_gallery, null);

                                    updateDataToGallery();

                                    mAdapter.updateStatus(position, 2);

                                    try {
                                        if (PaintActivity.mActivity != null) {
                                            PaintActivity.finishWork();
                                            PaintActivity.mActivity.finish();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    showRewardDialog();
//                                    showRewardDialog(isFromDrawing);
                                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                                } else {

                                    loadingDialog.DismissDialog();

                                    // Function call failed
                                    // showRewardDialog(isFromDrawing);

                                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();

                                    mAdapter.updateStatus(position, 0);

                                    Exception e = task.getException();
                                    if (e instanceof FirebaseFunctionsException) {
                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                        Log.e("FirebaseTest", "Error: " + ffe.getDetails(), ffe);
                                    } else {
                                        Log.e("FirebaseTest", "Error: ", e);
                                    }
                                }
                            });
                }).addOnFailureListener(e -> {
                    mAdapter.updateStatus(position, 0);
                    Log.e("PostDrawing", "Error uploading image: " + e.getMessage());
                });

            } else {
                mAdapter.updateStatus(position, 0);
                Toast.makeText(this, "user is not authenticated!", Toast.LENGTH_SHORT).show();
            }
        } else {

            File file = new File(_list.get(position).getFilePath());
            imagePath = file.getPath();
            checkAndUploadFile(position, title, description, hashTag, _youtube_url);
        }


    }


    private void checkAndUploadFile(int position, String title, String description, String hashTag, String _youtube_url) {
        if (imagePath != null) {
            final StorageReference folderRef = storageReference;

            folderRef.listAll().addOnSuccessListener(listResult ->
                    uploadFileToStorage(position, title, description, hashTag, _youtube_url)).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadFileToStorage(position, title, description, hashTag, _youtube_url);
                }
            });
        } else {
            mAdapter.updateStatus(position, 0);
            Toast.makeText(PostActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFileToStorage(int position, String title, String description, String hashTag, String _youtube_url) {
        if (imagePath != null) {

            Uri fileUri = Uri.fromFile(new File(imagePath));
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(PostActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> createpost(position, uri, title, description, hashTag, _youtube_url)).addOnFailureListener(e -> {
                    mAdapter.updateStatus(position, 0);
                    Log.e("Firebase", "Failed to get download URL", e);
                    Toast.makeText(PostActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Failed to upload image", e);
                mAdapter.updateStatus(position, 0);
                Toast.makeText(PostActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void updateDataToGallery() {
        AppDatabase appDatabase = MyApplication.getDb();
        PublishDao paintingDao = appDatabase.publishDao();

        // if post is save in my painting and not uploaded to gallery
        if (isFromMyPainting) {
            new Thread(() -> {
                PublishEntity painting = paintingDao.getPaintingByFileName(paintingFileName);
                if (painting != null) {
                    // Print all data of the retrieved PaintingEntity
                    Log.e("TAG", "ID: " + painting.getId() +
                            ", File Name: " + painting.getFileName() +
                            ", Type: " + painting.getType() +
                            ", Is Uploaded: " + painting.isUploadedGallery());

                    // Update the painting's isUploaded field to true
                    painting.setUploadedGallery(true);
                    paintingDao.updatePainting(painting);
                } else {
                    // Handle case where no painting is found
                    Log.e("TAG", "No painting found with fileName: " + paintingFileName);
                }
            }).start();
        } else {
            // Updating a painting
            //  Get the last item (latest painting)
            new Thread(() -> {
                PublishEntity lastPainting = paintingDao.getLatestPainting();
                if (lastPainting != null) {
                    //  Update isUploaded to true
                    lastPainting.setUploadedGallery(true);

                    //  Update the painting in the database
                    paintingDao.updatePainting(lastPainting);
                } else {
                    Log.e("TAG", "run: error");
                    // Handle case where there are no paintings in the database
                }
            }).start();
        }
    }

    public void updateDataToCommunity() {
        AppDatabase appDatabase = MyApplication.getDb();
        PublishDao paintingDao = appDatabase.publishDao();

        // if post is save in my painting and not uploaded to gallery
        if (isFromMyPainting) {
            new Thread(() -> {
                PublishEntity painting = paintingDao.getPaintingByFileName(paintingFileName);
                if (painting != null) {
                    // Print all data of the retrieved PaintingEntity
                    Log.e("TAG", "ID: " + painting.getId() +
                            ", File Name: " + painting.getFileName() +
                            ", Type: " + painting.getType() +
                            ", Is Uploaded: " + painting.isUploadedGallery());

                    // Update the painting's isUploaded field to true
                    painting.setUploadedCommunity(true);
                    paintingDao.updatePainting(painting);
                } else {
                    // Handle case where no painting is found
                    Log.e("TAG", "No painting found with fileName: " + paintingFileName);
                }
            }).start();
        } else {
            // Updating a painting
            //  Get the last item (latest painting)
            new Thread(() -> {
                PublishEntity lastPainting = paintingDao.getLatestPainting();
                if (lastPainting != null) {
                    //  Update isUploaded to true
                    lastPainting.setUploadedCommunity(true);

                    //  Update the painting in the database
                    paintingDao.updatePainting(lastPainting);
                } else {
                    Log.e("TAG", "run: error");
                    // Handle case where there are no paintings in the database
                }
            }).start();
        }
    }

    private void createpost(int position, Uri uri, String title, String description, String hashTag, String _youtube_url) {
        String downloadUrl = uri.toString();

        List<String> hashTagList = extractTags(hashTag);
        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("description", description);
        post.put("image_url", downloadUrl);
        post.put("tags", hashTagList);
        post.put("youtube_url", _youtube_url);
        post.put("app", StringConstants.APP_NAME);

        FirebaseFunctions.getInstance().getHttpsCallable("communityPost-post").call(post).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PostActivity.this, "Comment added successfully.", Toast.LENGTH_SHORT).show();
                    Log.d("CloudFunctions", "Post Created added successfully");
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PostActivity.this, constants.post_image_click_success, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PostActivity.this, constants.post_image_click_success);

                    mAdapter.updateStatus(position, 2);
                    updateDataToCommunity();
                    postRewardPoint();
                    if (!TextUtils.isEmpty(hashTag)) {
                        postHashTagRewardPoint();
                    }
                    loadingDialog.DismissDialog();
                    Toast.makeText(PostActivity.this, R.string.posting_to_community_successful, Toast.LENGTH_SHORT).show();

                    try {
                        if (PaintActivity.mActivity != null) {
                            PaintActivity.finishWork();
                            PaintActivity.mActivity.finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final Dialog exitDialog = new Dialog(PostActivity.this, R.style.my_dialog);
                    final DialogShoPostBinding dialogBinding = DialogShoPostBinding.inflate(getLayoutInflater());
                    exitDialog.setContentView(dialogBinding.getRoot());

                    exitDialog.setCancelable(false);
                    exitDialog.setCanceledOnTouchOutside(false);
                    dialogBinding.tvMessage.setText(R.string.your_drawing_has_been_posted_to_the_community);

                    String title = "";
                    title += getString(R.string.ss_way_to_go) + "\n";
                    title += getString(R.string.ss_you_ve_earned_20_points);
                    dialogBinding.tvDialogTitle.setText(title);

                    dialogBinding.imgCross.setVisibility(View.GONE);


                    dialogBinding.btnSeePost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(PostActivity.this, Community.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            if (_list.size() == 1) {
                                finish();
                            }
                        }
                    });


                    dialogBinding.btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exitDialog.dismiss();
                            if (_list.size() == 1) {
                                finish();
                            }
                          /*  if (isFromCanvasMode) {
                                finish();
                            } else {
                                exitDialog.dismiss();
                            }*/
                        }
                    });


                    exitDialog.show();


//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PostActivity.this);
//                    builder1.setMessage(R.string.your_drawing_has_been_posted_to_the_community + "bro we did it").setPositiveButton(R.string.see_it_now, (dialog, which) -> {
//                        Intent intent = new Intent(PostActivity.this, Community.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    }).setNegativeButton(R.string.ok_label, (dialog, which) -> {
////                                            mAdapter.updateStatus(position, 0);
//                        dialog.dismiss();
//                    }).setOnDismissListener(dialog -> {
//                        if (isFromCanvasMode) finish();
//                    }).show();
                } else {
                    mAdapter.updateStatus(position, 0);
                    Exception e = task.getException();
                    if (e != null) {
                        Toast.makeText(PostActivity.this, "Failed To Create Post.", Toast.LENGTH_SHORT).show();
                        Log.e("CloudFunctions", "Error: " + e);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAdapter.updateStatus(position, 0);
                Log.e("CloudFunctions", "Failure: " + e);
            }
        });
    }


    public List<String> extractTags(String input) {
        List<String> tags = new ArrayList<>();

        // Split the input string based on "|"
        String[] parts = input.split("\\|");

        // Loop through each part and process it
        for (String part : parts) {
            int hashIndex = part.indexOf('#');
            if (hashIndex != -1) {
                // Extract the tag after the "#"
                String tag = part.substring(hashIndex + 1).trim();
                tags.add(tag);
            }
        }

        return tags;
    }

    @SuppressLint("SetTextI18n")
    private void showRewardDialog() {
        final Dialog exitDialog = new Dialog(this, R.style.my_dialog);
        final DialogShoPostBinding dialogBinding = DialogShoPostBinding.inflate(getLayoutInflater());
        exitDialog.setContentView(dialogBinding.getRoot());
        exitDialog.setCancelable(false);
        exitDialog.setCanceledOnTouchOutside(false);

        String title = "";
        title += getString(R.string.ss_way_to_go) + "\n";
        title += getString(R.string.ss_you_ve_earned_40_points);
        dialogBinding.tvDialogTitle.setText(title);
        loadingDialog.DismissDialog();
        dialogBinding.tvMessage.setText(R.string.ss_gallery_posted);

        dialogBinding.btnOk.setOnClickListener(view -> {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
            }
            if (_list.size() == 1) {
                finish();
            }
        });

        dialogBinding.imgCross.setVisibility(View.GONE);


        dialogBinding.btnSeePost.setOnClickListener(v -> {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
                Intent intent = new Intent(PostActivity.this, GalleryActivity.class);
                intent.putExtra("type", drawingType);
                startActivity(intent);
                if (_list.size() == 1) {
                    finish();
                }
               /* if (isFromDrawing) {
                    Intent intent = new Intent(PostActivity.this, GalleryActivity.class);
                    intent.putExtra("type", drawingType);
                    startActivity(intent);
                    if (_list.size() == 1) {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Is not from drawing", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        exitDialog.show();
    }


    @Override
    public void cancelClick() {
        setupCount();
    }

    @Override
    public boolean isReserevedUsed(String _string) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(_string, "|");
            if (_lst_hashTag == null && _lst_hashTag.size() == 0)
                return false;

            StringBuilder str_builder = new StringBuilder();
            do {
                String _token = tokenizer.nextToken().replace("#", "").trim();
                for (int i = 0; i < _lst_hashTag.size(); i++) {
                    if (_token.equalsIgnoreCase(_lst_hashTag.get(i))) {
                        str_builder.append("#" + _token + " ");
                        break;
                    }
                }
            } while (tokenizer.hasMoreTokens());

            Log.e("TAGGG", "Reserved Used " + str_builder.toString());
            if (str_builder.toString().isEmpty())
                return false;
            else {
                showDialog(str_builder.toString());
                return true;
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at reservetag " + e.getMessage());
        }
        return false;
    }

    @Override
    public void deletePost(int pos) {

    }

    @Override
    public void pickPhotos(int pos) {

    }


    void showDialog(String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PostActivity.this);

        builder1.setTitle(getResources().getString(R.string.reserved_hashtag));
//        String _name = "<b>" + msg + "</b>" + " You cannot use reserved HashTag";
//        builder1.setMessage(Html.fromHtml(_name));
        builder1.setMessage(getString(R.string.reserved_hashtag_msg));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void getReservedHashTag() {
        Log.e("TAGG", "getReservedHashTag called");

        Observable<ReserveHashTag> observable = apiInterface.getReservedHashTag(ApiClient.SECRET_KEY);
        showProgress();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ReserveHashTag>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReserveHashTag reserveHashTag) {

                if (reserveHashTag != null) {
                    if (reserveHashTag.get_lst_hashTag() != null) {
                        Log.e("TAGG", "onNext Called " + reserveHashTag.get_lst_hashTag().size());
                        Gson gson = new Gson();
                        _lst_hashTag = reserveHashTag.get_lst_hashTag();
                        String arrayData = gson.toJson(reserveHashTag.get_lst_hashTag());
                        StringConstants _constants = new StringConstants();
                        //    _constants.putString(_constants.hashTagList, arrayData, PostActivity.this);

                    }
                }
                hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "OnError Called " + e.getMessage(), e);
                hideProgress();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    ProgressDialog progressDialog;

    void showProgress() {
        try {
            progressDialog = new ProgressDialog(PostActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    void hideProgress() {
        try {
            if (progressDialog != null && progressDialog.isShowing() && !PostActivity.this.isDestroyed()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_community:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PostActivity.this, constants.CLICK_COMMUNITY, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PostActivity.this, constants.CLICK_COMMUNITY);
                Intent intent = new Intent(PostActivity.this, Community.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //   finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.post_to_community, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(PostActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "community_post_posted",
                        rewardSetup.getCommunity_post_posted() == null ? 0 : rewardSetup.getCommunity_post_posted(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

    private void postHashTagRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.community_post_posted_hashtag, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(PostActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "community_post_posted_hashtag",
                        rewardSetup.getCommunity_post_posted_hashtag() == null ? 0 : rewardSetup.getCommunity_post_posted_hashtag(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

}
