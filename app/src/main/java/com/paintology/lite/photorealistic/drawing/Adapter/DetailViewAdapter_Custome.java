package com.paintology.lite.photorealistic.drawing.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.paintology.lite.photorealistic.drawing.Autocomplete.Autocomplete;
import com.paintology.lite.photorealistic.drawing.Autocomplete.AutocompleteCallback;
import com.paintology.lite.photorealistic.drawing.Autocomplete.AutocompletePolicy;
import com.paintology.lite.photorealistic.drawing.Autocomplete.AutocompletePresenter;
import com.paintology.lite.photorealistic.drawing.Autocomplete.CharPolicy;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail;
import com.paintology.lite.photorealistic.drawing.Community.PostOperation;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.TutorialDetail_Activity;
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type;
import com.paintology.lite.photorealistic.drawing.Fragment.UserPresenter;
import com.paintology.lite.photorealistic.drawing.Model.ContentSectionModel;
import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;
import com.paintology.lite.photorealistic.drawing.Model.Overlaid;
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel;
import com.paintology.lite.photorealistic.drawing.Model.RelatedPostsData;
import com.paintology.lite.photorealistic.drawing.Model.UserPostList;
import com.paintology.lite.photorealistic.drawing.Model.sizes;
import com.paintology.lite.photorealistic.drawing.Model.text_files;
import com.paintology.lite.photorealistic.drawing.Model.trace_image;
import com.paintology.lite.photorealistic.drawing.Model.videos_and_files;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo;
import com.paintology.lite.photorealistic.drawing.util.BaseViewHolder;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.ReadMoreOption;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import org.json.JSONArray;
import org.json.JSONObject;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewAdapter_Custome extends BaseViewHolder implements View.OnClickListener {

    Context _context;
    String currentUserID = "";
    StringConstants constants;
    PostOperation obj_interface;

    UserPostList _main_object;

    public ImageView iv_msg_icon, iv_post_comment, iv_blur_image, iv_main_image, iv_menu_icon, iv_like, /*iv_report_icon,*/
            iv_share_icon, iv_download_image;
    RoundedImageView iv_profile_icon;
    //        ImageView iv_dislike;
    TextView tv_uname, tv_date_time, tv_image_title, tv_total_comment, tv_total_likes, tv_total_views;
    EditText edt_comment;

    TextView tv_view_all_comment;
    AutoLinkTextView tv_cmnt_1, tv_cmnt_2, tv_cmnt_3,tv_description;
    //        TextView tv_user_1, tv_user_2, tv_user_3;
    RelativeLayout rl_profile_section;
    CardView card_view;
    //    TextView tv_tuto_name;
    LinearLayout ll_container;
    //    TextView btn_search;
    LinearLayout ll_like;
    //    EditText edt_enter_tag;
//    ImageView tv_do_tutorial;
    boolean isFromProfileScreen;
    String username = "";
    String user_id = "";
    LinearLayout ll_main;
    String defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/";
    private ReadMoreOption readMoreOption;

    ImageView iv_send_msg, iv_youtube_icon;
    View view_online, view_offline;

    boolean isTablet = false;
    home_fragment_operation interface_home_fragment;

    Autocomplete userAutocomplete;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public DetailViewAdapter_Custome(View view, PostOperation _interface, Context context, home_fragment_operation _interface_home, boolean... FromProfileScreen) {
        super(view);
        this.obj_interface = _interface;
        _context = context;
        constants = new StringConstants();
        interface_home_fragment = _interface_home;
        currentUserID = constants.getString(constants.UserId, _context);
        isFromProfileScreen = FromProfileScreen[0];
        username = constants.getString(constants.Username, _context);
//        tv_tuto_name = (TextView) view.findViewById(R.id.tv_tutorial_name);
        isTablet = _context.getResources().getBoolean(R.bool.isTablet);

//        btn_search = (TextView) view.findViewById(R.id.btn_search);
        rl_profile_section = (RelativeLayout) view.findViewById(R.id.rl_profile_section);
        iv_msg_icon = (ImageView) view.findViewById(R.id.iv_msg_icon);
        iv_post_comment = (ImageView) view.findViewById(R.id.iv_post_comment);
        edt_comment = (EditText) view.findViewById(R.id.edt_comment);
        edt_comment.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        sharedPreferences = _context.getApplicationContext().getSharedPreferences("PaintologyDB", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        iv_send_msg = (ImageView) view.findViewById(R.id.iv_send_msg);
        view_online = (View) view.findViewById(R.id.view_online);
        view_offline = (View) view.findViewById(R.id.view_offline);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);

        readMoreOption = new ReadMoreOption.Builder(context)
                .textLength(isTablet ? 100 : 50, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel("MORE")
                .lessLabel("LESS")
                .moreLabelColor(Color.RED)
                .lessLabelColor(Color.RED)
                .labelUnderLine(true)
                .expandAnimation(true)
                .build();


        ll_container = (LinearLayout) view.findViewById(R.id.ll_container);
        iv_share_icon = (ImageView) view.findViewById(R.id.iv_share_icon);
//        iv_report_icon = (ImageView) view.findViewById(R.id.iv_report_icon);
        iv_download_image = (ImageView) view.findViewById(R.id.iv_download_image);

        user_id = constants.getString(constants.UserId, _context);

        iv_youtube_icon = (ImageView) view.findViewById(R.id.iv_youtube_icon);
//        iv_youtube_icon.setVisibility(View.GONE);
        iv_youtube_icon.setOnClickListener(this::onClick);

        iv_like = (ImageView) view.findViewById(R.id.iv_like_icon);
//            iv_dislike = (ImageView) view.findViewById(R.id.iv_dislike);
        iv_blur_image = (ImageView) view.findViewById(R.id.iv_blurred_img);
        iv_menu_icon = (ImageView) view.findViewById(R.id.iv_menu_icon);
        iv_main_image = (ImageView) view.findViewById(R.id.iv_original_image);
        iv_profile_icon = (RoundedImageView) view.findViewById(R.id.iv_profile_icon);
        tv_uname = (TextView) view.findViewById(R.id.tv_uname);
        tv_date_time = (TextView) view.findViewById(R.id.tv_date_time);

//        tv_do_tutorial = (ImageView) view.findViewById(R.id.tv_do_tutorial);
        tv_image_title = (TextView) view.findViewById(R.id.tv_title);
        tv_description = (AutoLinkTextView) view.findViewById(R.id.tv_description);

        tv_total_comment = (TextView) view.findViewById(R.id.tv_total_comment);
        tv_total_likes = (TextView) view.findViewById(R.id.tv_total_likes);
        tv_total_views = (TextView) view.findViewById(R.id.tv_total_views);
        tv_view_all_comment = (TextView) view.findViewById(R.id.tv_view_all_comment);

        tv_cmnt_1 = (AutoLinkTextView) view.findViewById(R.id.tv_user_1_cmnt);
        tv_cmnt_2 = (AutoLinkTextView) view.findViewById(R.id.tv_user_2_cmnt);
        tv_cmnt_3 = (AutoLinkTextView) view.findViewById(R.id.tv_user_3_cmnt);

        tv_cmnt_1.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_MENTION);
        tv_description.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG);
//        tv_cmnt_1.setHashtagModeColor(ContextCompat.getColor(context, R.color.com_facebook_blue));

        tv_cmnt_2.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_MENTION);
//        tv_cmnt_2.setHashtagModeColor(ContextCompat.getColor(context, R.color.com_facebook_blue));

        tv_cmnt_3.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_MENTION);
//        tv_cmnt_3.setHashtagModeColor(ContextCompat.getColor(context, R.color.com_facebook_blue));

        ll_like = (LinearLayout) view.findViewById(R.id.ll_like);

        tv_cmnt_1.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
            @Override
            public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                try {
                    if (autoLinkMode.equals(AutoLinkMode.MODE_MENTION)) {
                        String _matchText = matchedText.replace("@", "");
                        for (int i = 0; i < _interface_home.getFirebaseUserList().size(); i++) {
                            String _name = _interface_home.getFirebaseUserList().get(i).getUser_name().replace(" ", "");

                            if (_matchText.trim().equalsIgnoreCase(_name.trim())) {
                                FireUtils.openProfileScreen(context, _interface_home.getFirebaseUserList().get(i).getKey());
                                break;
                            }
                        }
                    } else
                        obj_interface.seachByHashTag(matchedText);
                } catch (Exception e) {

                }
            }
        });

        tv_cmnt_2.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
            @Override
            public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                try {
                    if (autoLinkMode.equals(AutoLinkMode.MODE_MENTION)) {
                        String _matchText = matchedText.replace("@", "");
                        for (int i = 0; i < _interface_home.getFirebaseUserList().size(); i++) {
                            String _name = _interface_home.getFirebaseUserList().get(i).getUser_name().replace(" ", "");
                            if (_matchText.trim().equalsIgnoreCase(_name.trim())) {
                                FireUtils.openProfileScreen(context, _interface_home.getFirebaseUserList().get(i).getKey());
                                break;
                            }
                        }
                    } else
                        obj_interface.seachByHashTag(matchedText);
                } catch (Exception e) {

                }
            }
        });

        tv_cmnt_3.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
            @Override
            public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                try {
                    if (autoLinkMode.equals(AutoLinkMode.MODE_MENTION)) {
                        String _matchText = matchedText.replace("@", "");
                        for (int i = 0; i < _interface_home.getFirebaseUserList().size(); i++) {
                            String _name = _interface_home.getFirebaseUserList().get(i).getUser_name().replace(" ", "");
                            if (_matchText.trim().equalsIgnoreCase(_name.trim())) {
                                FireUtils.openProfileScreen(context, _interface_home.getFirebaseUserList().get(i).getKey());
                                break;
                            }
                        }
                    } else
                        obj_interface.seachByHashTag(matchedText);
                } catch (Exception e) {

                }
            }
        });

        card_view = (CardView) view.findViewById(R.id.card_view);
//        edt_enter_tag = (EditText) view.findViewById(R.id.edt_enter_tag);

        edt_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateIconAlpha(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        edt_comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (event != null && actionId == EditorInfo.IME_ACTION_DONE) {
                        Log.e("TAG", "Enter pressed");

                        OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                        _loginOperationModel.setOperationType(constants.OperationTypeComment);
                        _loginOperationModel.setPosition(getAdapterPosition());
                        OperationAfterLogin.CommentData _comment_data = new OperationAfterLogin.CommentData();
                        _comment_data.set_post_id(_main_object.getPost_id());
                        _comment_data.set_user_comment(edt_comment.getText().toString());
                        _comment_data.set_username(username);
                        _loginOperationModel.set_obj_comment_data(_comment_data);

                        String[] _user_lst = edt_comment.getText().toString().trim().split(" ");
                        ArrayList<Firebase_User> _user_list = new ArrayList<>();
                        try {
                            for (String str : _user_lst) {
                                if (str.startsWith("@")) {
                                    for (int i = 0; i < _interface_home.getFirebaseUserList().size(); i++) {
                                        if (str.equalsIgnoreCase("@" + _interface_home.getFirebaseUserList().get(i).getUser_name().replace(" ", ""))) {
                                            _user_list.add(_interface_home.getFirebaseUserList().get(i));
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }
                        _comment_data.set_user_list(_user_list);
                        if (obj_interface.isLoggedIn(_loginOperationModel)) {
                            if (!edt_comment.getText().toString().trim().isEmpty()) {
                                obj_interface.addComment(getAdapterPosition(), edt_comment.getText().toString(), _user_list);
                                edt_comment.setText("");
                            } else {
                                edt_comment.requestFocus();
                                edt_comment.setError(context.getResources().getString(R.string.required));
//                            Toast.makeText(_context, "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (Exception e) {

                }
                return false;
            }
        });

        edt_comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                try {
                    if (b)
                        obj_interface.showHideFab(false);
                    else
                        obj_interface.showHideFab(true);
                } catch (Exception e) {

                }
            }


        });

//        edt_enter_tag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//
//                try {
//                    if (b)
//                        obj_interface.showHideFab(false);
//                    else
//                        obj_interface.showHideFab(true);
//                } catch (Exception e) {
//
//                }
//            }
//        });

        iv_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                    _loginOperationModel.setOperationType(constants.OperationTypeComment);
                    _loginOperationModel.setPosition(getAdapterPosition());
                    OperationAfterLogin.CommentData _comment_data = new OperationAfterLogin.CommentData();
                    _comment_data.set_post_id(_main_object.getPost_id());
                    _comment_data.set_user_comment(edt_comment.getText().toString());
                    _comment_data.set_username(username);
                    _loginOperationModel.set_obj_comment_data(_comment_data);

//                    String[] _user_lst = edt_comment.getText().toString().trim().split(" ");
//                    ArrayList<Firebase_User> _user_list = new ArrayList<>();
//                    try {
//                        for (int i = 0; i < _interface_home.getFirebaseUserList().size(); i++) {
//                            if (str.equalsIgnoreCase("@" + _interface_home.getFirebaseUserList().get(i).getUser_name().replace(" ", ""))) {
//                                _user_list.add(_interface_home.getFirebaseUserList().get(i));
//                                Log.e("TAG", "Selected User ID " + _interface_home.getFirebaseUserList().get(i).getUser_id() + " " + _interface_home.getFirebaseUserList().get(i).getKey());
//                                break;
//                            }
//                        }
////                        for (String str : _user_lst) {
////                            if (str.startsWith("@")) {
////                                Log.e("TAG", "Selected User " + str);
//////                        _lst.add(str);
////                                for (int i = 0; i < _interface_home.getFirebaseUserList().size(); i++) {
////                                    if (str.equalsIgnoreCase("@" + _interface_home.getFirebaseUserList().get(i).getUser_name().replace(" ", ""))) {
////                                        _user_list.add(_interface_home.getFirebaseUserList().get(i));
////                                        Log.e("TAG", "Selected User ID " + _interface_home.getFirebaseUserList().get(i).getUser_id() + " " + _interface_home.getFirebaseUserList().get(i).getKey());
////                                        break;
////                                    }
////                                }
////                            }
////                        }
//                    } catch (Exception e) {
//
//                    }

                    String[] _user_lst = edt_comment.getText().toString().trim().split(" ");
                    ArrayList<Firebase_User> _user_list = new ArrayList<>();
                    try {
                        for (String str : _user_lst) {
                            if (str.startsWith("@")) {
                                for (int i = 0; i < _interface_home.getFirebaseUserList().size(); i++) {
                                    if (str.equalsIgnoreCase("@" + _interface_home.getFirebaseUserList().get(i).getUser_name().replace(" ", ""))) {
                                        _user_list.add(_interface_home.getFirebaseUserList().get(i));
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                    _comment_data.set_user_list(_user_list);
                    if (obj_interface.isLoggedIn(_loginOperationModel)) {
                        if (!edt_comment.getText().toString().isEmpty()) {
                            obj_interface.addComment(getAdapterPosition(), edt_comment.getText().toString(), _user_list);
                            edt_comment.setText("");
                        } else {
                            edt_comment.requestFocus();
                            edt_comment.setError(context.getResources().getString(R.string.required));
                        }
                    }
                } catch (Exception e) {

                }
            }
        });

        rl_profile_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj_interface.viewProfile(getAdapterPosition());
            }
        });

        iv_download_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj_interface.downloadImage(getAdapterPosition(), false);
            }
        });
        iv_share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj_interface.shareImage(getAdapterPosition());
            }
        });

//        iv_report_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
//                    _loginOperationModel.setOperationType(constants.OperationTypeReport);
//                    _loginOperationModel.setPosition(getAdapterPosition());
//                    if (obj_interface.isLoggedIn(_loginOperationModel)) {
//
//                        if (BuildConfig.DEBUG){
//                            Toast.makeText(_context, constants.open_report_dialog, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(_context, constants.open_report_dialog);
//                        obj_interface.reportPost(getAdapterPosition());
//                    } else {
//                        if (BuildConfig.DEBUG){
//                            Toast.makeText(_context,constants.open_social_login_report_dialog, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(_context, constants.open_social_login_report_dialog);
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        });

        tv_view_all_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj_interface.view_all_comment(getAdapterPosition());
            }
        });


        iv_like.setOnClickListener(this);
//        tv_do_tutorial.setOnClickListener(this);

        ll_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_like.performClick();
            }
        });

        iv_menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserPostList _object = (UserPostList) view.getTag();
                boolean isVisible = false;
                if (_object.getPost_type() == 2) {
                    isVisible = true;
                }
                showDialog(isVisible, _object, view);
            }
        });
        iv_main_image.setOnClickListener(this);

        iv_msg_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_comment.setFocusable(true);
                edt_comment.setSelection(0);
                edt_comment.requestFocus();
            }
        });

        iv_send_msg.setOnClickListener(this);
        AutocompletePresenter<Firebase_User> presenter = new UserPresenter(context, interface_home_fragment.getFirebaseUserList());

        AutocompleteCallback<Firebase_User> callback = new AutocompleteCallback<Firebase_User>() {
            @Override
            public boolean onPopupItemClicked(@NonNull Editable editable, @NonNull Firebase_User item, String query) {

                try {
                    if (query != null && !query.isEmpty()) {
                        String _data = edt_comment.getText().toString();
                        edt_comment.getText().clear();
                        String new_str = _data.replace("@" + query, "");
                        editable.append(new_str);
                        editable.append("@" + item.getUser_name().replace(" ", ""));
                    } else {
                        editable.append(item.getUser_name().replace(" ", ""));
                    }
                } catch (Exception e) {

                }

                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
            }
        };
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);

        AutocompletePolicy policy = new CharPolicy('@');
        userAutocomplete = Autocomplete.<Firebase_User>on(edt_comment)
                .with(elevation)
                .with(backgroundDrawable)
                .with(policy)
                .with(presenter)
                .with(callback)
                .build();
    }

    private void updateIconAlpha(String text) {
        if (text.isEmpty()) {
            iv_post_comment.setAlpha(0.5f); // Set alpha to 0.5 if EditText is empty
        } else {
            iv_post_comment.setAlpha(1.0f); // Set alpha to 1.0 if EditText is not empty
        }
    }


    @Override
    public void onBindView(UserPostList object) {
        try {
            _main_object = object;
            iv_like.setTag(object);
            iv_main_image.setTag(object);
            iv_youtube_icon.setTag(object);
            iv_menu_icon.setTag(object);
            iv_send_msg.setTag(object);
            iv_share_icon.setTag(object);
            iv_download_image.setTag(object);
//            iv_report_icon.setTag(object);
//            tv_do_tutorial.setTag(object);
//            btn_search.setVisibility(View.GONE);
            ll_container.removeAllViews();
//            edt_enter_tag.setVisibility(View.GONE);
            edt_comment.setError(null);
            edt_comment.setText("");

            if (object != null) {
                int isTypeTutorial = 1;
                isTypeTutorial = object.getPost_type();


                if (object.getYoutube_url() != null && !object.getYoutube_url().isEmpty()) {
                    iv_youtube_icon.setVisibility(View.VISIBLE);
                } else
                    iv_youtube_icon.setVisibility(View.GONE);

                if (isTypeTutorial == 2) {
                    rl_profile_section.setVisibility(View.GONE);
//                    tv_do_tutorial.setVisibility(View.VISIBLE);
//                    btn_search.setVisibility(View.GONE);
//                    ll_container.removeAllViews();
                    iv_profile_icon.setVisibility(View.GONE);
//                    tv_tuto_name.setVisibility(View.VISIBLE);
//                    if (object.getImage_title() != null) {
//                        tv_tuto_name.setText(Html.fromHtml(object.getImage_title()));
//                    } else
//                        tv_tuto_name.setVisibility(View.GONE);
                } else {
//                    tv_tuto_name.setVisibility(View.GONE);
                    rl_profile_section.setVisibility(View.VISIBLE);
                    iv_profile_icon.setVisibility(View.VISIBLE);
//                    tv_do_tutorial.setVisibility(View.GONE);
                    if (object.getUserInfo() != null && object.getUserInfo().getUserProfilePic() != null && !object.getUserInfo().getUserProfilePic().isEmpty())
                        Glide.with(_context)
                                .load(object.getUserInfo().getUserProfilePic())
                                .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                                .into(iv_profile_icon);
                    else
                        iv_profile_icon.setImageDrawable(_context.getResources().getDrawable(R.drawable.profile_icon));

                    if (object.getUserInfo() != null && object.getPost_date_time() != null)
                        tv_date_time.setText(object.getPost_date_time());

                    if (object.getUserInfo() != null && object.getUserInfo().getUserName() != null) {
                        if (isFromProfileScreen) {
                            tv_uname.setTextColor(_context.getResources().getColor(R.color.text_black_color));
                            tv_uname.setText(object.getUserInfo().getUserName());
                            Log.e("TAGGG", "Uname>> isFromProfileScreen " + object.getUserInfo().getUserName());
                        } else {
                            tv_uname.setTextColor(_context.getResources().getColor(R.color.text_black_color));
                            String name = object.getUserInfo().getUserName() ;
                            tv_uname.setText(name);
                            Log.e("TAGGG", "Uname>> in elase " + name);
                        }
                    } else {
                        tv_uname.setText("");
                    }

                    if (object.getImage_title() != null && !object.getImage_title().isEmpty()) {
                        tv_image_title.setText(Html.fromHtml(object.getImage_title()));
                        tv_image_title.setVisibility(View.VISIBLE);
                    } else
                        tv_image_title.setVisibility(View.GONE);


                    if (object.getImage_description() != null && !object.getImage_description().isEmpty()) {
                        tv_description.setText(Html.fromHtml(object.getImage_description()));
                        readMoreOption.addReadMoreTo(tv_description, Html.fromHtml(object.getImage_description()).toString());
                        tv_description.setVisibility(View.VISIBLE);
                    } else
                        tv_description.setVisibility(View.GONE);
                }

                if (object.getImage_hashtag() != null && !object.getImage_hashtag().isEmpty()) {
                    setConainer(object.getImage_hashtag());
                    System.out.println("HashTag " + object.getImage_description()+" "+ object.getImage_hashtag());
                    tv_description.setText(Html.fromHtml(object.getImage_description()+" "+ object.getImage_hashtag()));
                    readMoreOption.addReadMoreTo(tv_description, Html.fromHtml(object.getImage_description()+" "+ object.getImage_hashtag()));
                    tv_description.setVisibility(View.VISIBLE);
                } else {
                    ll_container.removeAllViews();
//                    btn_search.setVisibility(View.GONE);
                }
            }

            tv_description.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    try {
                        if (autoLinkMode.equals(AutoLinkMode.MODE_HASHTAG)) {
                            obj_interface.seachByHashTag(matchedText);
//                        Toast.makeText(_context, textView.getText() + "", Toast.LENGTH_SHORT).show();

                            if (BuildConfig.DEBUG) {
                                Toast.makeText(_context, constants.search_single_query, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(_context, constants.search_single_query);
                        }
                    } catch (Exception e) {

                    }
                }
            });


            //Chat icon setup
            try {
                if (object != null) {
                    if (object.getChat_status().equalsIgnoreCase("0") || object.getChat_status().equalsIgnoreCase("1")) {
//                        iv_send_msg.setVisibility(View.VISIBLE);
                        if (object.getChat_status().equalsIgnoreCase("1")) {
                            view_online.setVisibility(View.VISIBLE);
                            view_offline.setVisibility(View.GONE);
                        } else {
                            view_online.setVisibility(View.GONE);
                            view_offline.setVisibility(View.VISIBLE);
                        }
                    } else {
                        iv_send_msg.setVisibility(View.GONE);
                        view_offline.setVisibility(View.GONE);
                        view_online.setVisibility(View.GONE);
                    }

                }
            } catch (Exception e) {

            }

            if (object != null) {
                if (object.getThumbs() != null && !object.getThumbs().getThumb_image_large().isEmpty()) {
//                    Glide.with(_context)
//                            .load(object.getThumbs().getThumb_image_large())
//                            .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
//                            .into(iv_blur_image);

                    Glide.with(_context)
                            .load(object.getThumbs().getThumb_image_large())
                            .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(iv_main_image);
                } else {
//                    Glide.with(_context)
//                            .load(object.getImage_Url())
//                            .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
//                            .into(iv_blur_image);

                    Glide.with(_context)
                            .load(object.getImage_Url())
                            .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(iv_main_image);
                }

                if (object.getCommentsList() != null && object.getCommentsList().getPost_comment_lists() != null) {
                    if (object.getCommentsList().getTotal_comments().equals("0")) {
                        iv_msg_icon.setImageResource(R.drawable.community_comments_icon);
                        tv_total_comment.setText("");
                    } else {
                        iv_msg_icon.setImageResource(R.drawable.community_comments_icon);
                        tv_total_comment.setText(object.getCommentsList().getTotal_comments() + "");
                    }
                } else {
                    tv_total_comment.setText("");
                }

                if (object.getObjLikes() != null && object.getObjLikes().getTotal_likes() != null) {
                    if (object.getObjLikes().getTotal_likes().equalsIgnoreCase("0")) {
                        tv_total_likes.setText("");
                        iv_like.setImageResource(R.drawable.community_likes_icon);
                    } else {
                        iv_like.setImageResource(R.drawable.community_likes_icon);
                        tv_total_likes.setText(object.getObjLikes().getTotal_likes() + "");
                    }
                } else {
                    tv_total_likes.setText("");
                }

                //View count
                // Get locally saved total views
//                String count = sharedPreferences.getString(object.getPost_id(), "0");
//                //Increase view
//                int views = Integer.parseInt(count) + 1;
//                //Show
//                tv_total_views.setText(String.valueOf(views));
//                // Save locally updated total views
//                editor.putString(object.getPost_id(), String.valueOf(views));
//                editor.apply();
//
//                object.getObjView().setTotal_views(String.valueOf(views));

                if (object.getObjView() != null && object.getObjView().getTotal_views() != null) {
                    tv_total_views.setText(object.getObjView().getTotal_views() + "");
                } else {
                    Log.e("TAGGG", "Total view set 0");
                    tv_total_views.setText("");
                }

//                doView(object);

                if (object.getCommentsList() != null && object.getCommentsList().getPost_comment_lists() != null) {
                    if (object.getCommentsList().getPost_comment_lists().size() >= 3) {

                        String uname_1 = object.getCommentsList().getPost_comment_lists().get(0).getUsername();
                        String uname_2 = object.getCommentsList().getPost_comment_lists().get(1).getUsername();
                        String uname_3 = object.getCommentsList().getPost_comment_lists().get(2).getUsername();

                        tv_cmnt_1.setVisibility(View.VISIBLE);
                        tv_cmnt_2.setVisibility(View.VISIBLE);
                        tv_cmnt_3.setVisibility(View.VISIBLE);
                        String cment_3 = "<b>" + uname_3 + "</b>" + " " + object.getCommentsList().getPost_comment_lists().get(2).getComment_content();
                        tv_cmnt_3.setText(Html.fromHtml(cment_3));
                        String cment_2 = "<b>" + uname_2 + "</b>" + " " + object.getCommentsList().getPost_comment_lists().get(1).getComment_content();
                        tv_cmnt_2.setText(Html.fromHtml(cment_2));
                        String cment_1 = "<b>" + uname_1 + "</b>" + " " + object.getCommentsList().getPost_comment_lists().get(0).getComment_content();
                        tv_cmnt_1.setText(Html.fromHtml(cment_1));

                    } else if (object.getCommentsList().getPost_comment_lists().size() >= 2) {

                        String uname_2 = object.getCommentsList().getPost_comment_lists().get(1).getUsername();
                        String uname_1 = object.getCommentsList().getPost_comment_lists().get(0).getUsername();

                        tv_cmnt_3.setVisibility(View.GONE);
                        tv_cmnt_2.setVisibility(View.VISIBLE);
                        tv_cmnt_1.setVisibility(View.VISIBLE);
                        String cment_2 = "<b>" + uname_2 + "</b>" + " " + object.getCommentsList().getPost_comment_lists().get(1).getComment_content();
                        tv_cmnt_2.setText(Html.fromHtml(cment_2));
                        String cment_1 = "<b>" + uname_1 + "</b>" + " " + object.getCommentsList().getPost_comment_lists().get(0).getComment_content();
                        tv_cmnt_1.setText(Html.fromHtml(cment_1));
                    } else if (object.getCommentsList().getPost_comment_lists().size() >= 1) {
                        tv_cmnt_1.setVisibility(View.VISIBLE);
                        tv_cmnt_3.setVisibility(View.GONE);
                        tv_cmnt_2.setVisibility(View.GONE);

                        String uname_1 = object.getCommentsList().getPost_comment_lists().get(0).getUsername();

                        String cment_1 = "<b>" + uname_1 + "</b>" + " " + object.getCommentsList().getPost_comment_lists().get(0).getComment_content();
//                    myViewHolder.tv_cmnt_1.setText(_userPostList.getObjData().getPost_list().get(i).getCommentsList().getPost_comment_lists().get(0).getComment_content());
                        tv_cmnt_1.setText(Html.fromHtml(cment_1));
                    } else {
                        tv_cmnt_1.setVisibility(View.GONE);
                        tv_cmnt_2.setVisibility(View.GONE);
                        tv_cmnt_3.setVisibility(View.GONE);
                    }
                }

                if (object.getCommentsList() != null && object.getCommentsList().getPost_comment_lists().size() >= 3) {
                    tv_view_all_comment.setText("View All " + object.getCommentsList().getTotal_comments() + " Comments...");
                    tv_view_all_comment.setVisibility(View.VISIBLE);
                } else
                    tv_view_all_comment.setVisibility(View.GONE);

                if (object.isDownloaded())
                    iv_download_image.setImageResource(R.drawable.community_download_icon);
                else
                    iv_download_image.setImageResource(R.drawable.community_download_icon);
            }

        } catch (Exception e) {
            Log.e("TAGGG", "Exception at holder " + e.getMessage(), e);
        }
    }

    void setConainer(String data) {
        String[] arr = data.split(" ");


        ll_container.removeAllViews();
        for (int i = 0; i < arr.length; i++) {
            LinearLayout.LayoutParams params_check = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (isTablet)
                params_check.setMargins(0, 0, -5, 0);
            else
                params_check.setMargins(0, 0, -13, 0);

            AppCompatCheckBox checkBox = new AppCompatCheckBox(_context);
            checkBox.setLayoutParams(params_check);
            checkBox.setVisibility(View.GONE);
            checkBox.setVerticalScrollBarEnabled(true);
            ll_container.addView(checkBox);
            TextView textView = new TextView(_context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (isTablet) {
                textView.setTextSize(14);
                params.setMargins(0, 0, 10, 0);
            } else {
                textView.setTextSize(12);
                params.setMargins(0, 0, 15, 0);
            }
            textView.setText(arr[i].trim());
            textView.setId(i);
            textView.setSingleLine(true);
            textView.setVerticalScrollBarEnabled(true);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textView.setLetterSpacing(0.1f);
            }*/
//            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setTextColor(_context.getResources().getColor(R.color.hashtag_color));
            textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
//            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textView.setPaintFlags(textView.getPaintFlags());
            textView.setLayoutParams(params);

            ll_container.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    obj_interface.seachByHashTag(textView.getText().toString());
//                        Toast.makeText(_context, textView.getText() + "", Toast.LENGTH_SHORT).show();

                    if (BuildConfig.DEBUG) {
                        Toast.makeText(_context, constants.search_single_query, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(_context, constants.search_single_query);
                }
            });

            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//                        checkBox.setVisibility(View.VISIBLE);
                    for (int i = 0; i < ll_container.getChildCount(); i++) {
                        if (ll_container.getChildAt(i) instanceof AppCompatCheckBox) {
                            ll_container.getChildAt(i).setVisibility(View.VISIBLE);
                        }
                    }
//                    btn_search.setVisibility(View.VISIBLE);
//                    edt_enter_tag.setVisibility(View.VISIBLE);
                    return true;
                }
            });

//            btn_search.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        StringBuilder stringBuilder = new StringBuilder();
//                        boolean isMultipleSelected = false;
//                        for (int i = 0; i < ll_container.getChildCount(); i++) {
//                            if (ll_container.getChildAt(i) instanceof AppCompatCheckBox) {
//                                AppCompatCheckBox cb = (AppCompatCheckBox) ll_container.getChildAt(i);
//                                if (cb.isChecked()) {
//                                    TextView tv = (TextView) ll_container.getChildAt(i + 1);
//                                    stringBuilder.append(tv.getText().toString() + "|");
//                                }
//                            }
//                        }
//                        if (stringBuilder != null && !stringBuilder.toString().isEmpty()) {
//                            isMultipleSelected = true;
//                        }
//
//                        boolean isManuallyAdded = false;
//                        try {
//                            StringTokenizer tokenizer = new StringTokenizer(edt_enter_tag.getText().toString(), " ");
//                            if (tokenizer.countTokens() != 0) {
//                                isManuallyAdded = true;
//                                do {
//                                    String nextElem = tokenizer.nextToken().toString();
//                                    if (nextElem.startsWith("#") && nextElem.length() > 1)
//                                        stringBuilder.append(nextElem + "|");
//                                    else
//                                        stringBuilder.append("#" + nextElem + "|");
//                                } while (tokenizer.hasMoreTokens());
//                            }
//                        } catch (Exception e) {
//                            Log.e("TAGG", "Exception on find tag " + e.getMessage(), e);
//                        }
//                        String hasTag = stringBuilder.toString();
//                        if (hasTag.isEmpty()) {
//                            Toast.makeText(_context, "Nothing selected! ", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        if (isMultipleSelected && isManuallyAdded) {
//                            if (BuildConfig.DEBUG){
//
//                                Toast.makeText(_context,constants.search_multiple_textentry_query, Toast.LENGTH_SHORT).show();
//                            }
//
//                            FirebaseUtils.logEvents(_context, constants.search_multiple_textentry_query);
//                        } else if (isMultipleSelected && !isManuallyAdded) {
//                            if (BuildConfig.DEBUG){
//
//                                Toast.makeText(_context,constants.search_multiple_query, Toast.LENGTH_SHORT).show();
//                            }
//
//                            FirebaseUtils.logEvents(_context, constants.search_multiple_query);
//                        } else if (!isMultipleSelected && isManuallyAdded) {
//
//                            if (BuildConfig.DEBUG){
//
//                                Toast.makeText(_context,constants.search_textentry_query, Toast.LENGTH_SHORT).show();
//                            }
//                            FirebaseUtils.logEvents(_context, constants.search_textentry_query);
//                        }
//                        if (hasTag != null && hasTag.length() > 0 && hasTag.charAt(hasTag.length() - 1) == '|') {
//                            hasTag = hasTag.substring(0, hasTag.length() - 1);
//                        }
//                        obj_interface.seachByHashTag(hasTag);
//                        Log.e("TAGG", "Selected Data " + hasTag);
//                    } catch (Exception e) {
//                        Log.e("TAGGG", "Exception at setGet Tag " + e.getMessage(), e);
//                    }
//                }
//            });
        }
    }

    CharSequence[] items = {"More Details", "Share", "Copy Link", "Open in Canvas (overlay)", "Open in Canvas (trace)", "Users Posts", "Chat", "Download", "Report", "Go to youtube video"};
    CharSequence[] item_option = {"Share", "Copy Link", "Open in Canvas (overlay)", "Open in Canvas (trace)", "Users Posts", "Chat", "Download", "Report", "Go to youtube video"};

    void showDialog(boolean isVisible, UserPostList _object, View view) {

//        items[items.length - 1] = "";
        if (BuildConfig.DEBUG) {

            Toast.makeText(_context, constants.comm_post_menuitem_press, Toast.LENGTH_SHORT).show();
        }


        FirebaseUtils.logEvents(_context, constants.comm_post_menuitem_press);
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);

        int menuRes = R.menu.popup_community_list_menu;
        int menuMoreRes = R.menu.popup_community_list_menu_more;
        try {
            if (_object != null && (_object.getYoutube_url() == null || _object.getYoutube_url().isEmpty())) {
                items = new CharSequence[]{"More Details", "Share", "Copy Link", "Open in Canvas (overlay)", "Open in Canvas (trace)", "Users Posts", "Chat", "Download", "Report"};
                item_option = new CharSequence[]{"Share", "Copy Link", "Open in Canvas (overlay)", "Open in Canvas (trace)", "Users Posts", "Chat", "Download", "Report"};

                menuRes = R.menu.popup_community_list_menu;
                menuMoreRes = R.menu.popup_community_list_menu_more;

            } else {
                menuRes = R.menu.popup_community_list_youtube_menu;
                menuMoreRes = R.menu.popup_community_list_youtube_menu_more;
            }
        } catch (Exception e) {

        }

        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(_context, view);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(isVisible ? menuMoreRes : menuRes, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Toast message on menu item clicked
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.chat:
                        try {
                            if (BuildConfig.DEBUG) {

                                Toast.makeText(_context, constants.chat_community_post_click, Toast.LENGTH_SHORT).show();
                            }

                            FirebaseUtils.logEvents(_context, constants.chat_community_post_click);
                            if (((UserPostList) view.getTag()).getUserInfo().getUserId().equals(currentUserID)) {
//                        Toast.makeText(_context, "Can't chat with own", Toast.LENGTH_LONG).show();
                                obj_interface.openChatsScreen();
                                break;
                            }
                            OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                            _loginOperationModel.setOperationType(constants.OperationTypeChat);
                            _loginOperationModel.setPosition(getAdapterPosition());
                            if (obj_interface.isLoggedIn(_loginOperationModel)) {
                        /*Intent _intent = new Intent(_context, ChatActivity.class);
                        _intent.putExtra("userid", ((UserPostList) view.getTag()).getUserKey());
                        _context.startActivity(_intent);*/
                                obj_interface.openChatScreen(_object.getUser_key(), _object.getUserInfo().getUserName(), getAdapterPosition());
                            }
                        } catch (Exception e) {
                            Log.e("TAGG", "Exception " + e.getMessage());
                        }
                        break;
                    case R.id.users_posts:
                        String _userId = _object.getUserInfo().getUserId();
                        String userName = _object.getUserInfo().getUserName();

                        Intent _intent = new Intent(_context, CommunityDetail.class);
                        _intent.setAction("isFromProfile");
                        _intent.putExtra("user_id", _userId + "");
                        _intent.putExtra("user_name", userName + "");
                        _context.startActivity(_intent);
                        break;
                    case R.id.more_detail:
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, constants.post_menu_click_more_detail, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(_context, constants.post_menu_click_more_detail);
                        RedirectAdvertisement(_object);
                        break;
                    case R.id.share:
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, constants.comm_post_menuitem_share, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(_context, constants.comm_post_menuitem_share);
                        iv_share_icon.performClick();
                        break;
                    case R.id.copy_link:
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, constants.comm_post_menuitem_copy_link, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(_context, constants.comm_post_menuitem_copy_link);
                        obj_interface.copyImage(getAdapterPosition());
                        break;
                    case R.id.open_overlay:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(_context, constants.comm_post_menuitem_open_canvas_overlay, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(_context, constants.comm_post_menuitem_open_canvas_overlay);
                        obj_interface.downloadImageOpenInOverlayCanvas(getAdapterPosition());
                        break;
                    case R.id.open_trace:
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, constants.comm_post_menuitem_open_canvas_trace, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(_context, constants.comm_post_menuitem_open_canvas_trace);
                        obj_interface.downloadImageOpenInTraceCanvas(getAdapterPosition());
                        break;
                    case R.id.save:
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, constants.comm_post_menuitem_download, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(_context, constants.comm_post_menuitem_download);
                        iv_download_image.performClick();
                        break;
                    case R.id.report:
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, constants.comm_post_menuitem_report, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(_context, constants.comm_post_menuitem_report);
//                                iv_report_icon.performClick();
                        try {
                            OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                            _loginOperationModel.setOperationType(constants.OperationTypeReport);
                            _loginOperationModel.setPosition(getAdapterPosition());
                            if (obj_interface.isLoggedIn(_loginOperationModel)) {

                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(_context, constants.open_report_dialog, Toast.LENGTH_SHORT).show();
                                }
                                FirebaseUtils.logEvents(_context, constants.open_report_dialog);
                                obj_interface.reportPost(getAdapterPosition());
                            } else {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(_context, constants.open_social_login_report_dialog, Toast.LENGTH_SHORT).show();
                                }
                                FirebaseUtils.logEvents(_context, constants.open_social_login_report_dialog);
                            }
                        } catch (Exception e) {
                            Log.e("DetailViewAdapter", e.getMessage());
                        }
                        break;
                    case R.id.yt_video:
                        if (_object != null && _object.getYoutube_url() != null && !_object.getYoutube_url().isEmpty()) {
                            try {
                                KGlobal.openInBrowser(_context, _object.getYoutube_url());
                            } catch (Exception e) {
                                Log.e("DetailViewAdapter", e.getMessage());
                            }
                            if (BuildConfig.DEBUG) {

                                Toast.makeText(_context, constants.post_menu_click_open_youtube, Toast.LENGTH_SHORT).show();
                            }

                            FirebaseUtils.logEvents(_context, constants.post_menu_click_open_youtube);
                        }
                        break;
                }
                Toast.makeText(_context, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        // Showing the popup menu
        popupMenu.show();

    }

    private long doubleClickLastTime = 0L;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_youtube_icon: {
                try {
                    UserPostList _object = (UserPostList) view.getTag();
                    KGlobal.openInBrowser(_context, _object.getYoutube_url());

                    if (BuildConfig.DEBUG) {

                        Toast.makeText(_context, constants.comm_post_youtube_button_click, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(_context, constants.comm_post_youtube_button_click);
                } catch (Exception e) {

                }
            }
            break;
            case R.id.iv_like_icon: {
                doLike((UserPostList) view.getTag());
            }
            break;

            case R.id.iv_send_msg: {
                try {
                    if (BuildConfig.DEBUG) {

                        Toast.makeText(_context, constants.chat_community_post_click, Toast.LENGTH_SHORT).show();
                    }

                    FirebaseUtils.logEvents(_context, constants.chat_community_post_click);
                    if (((UserPostList) view.getTag()).getUserInfo().getUserId().equals(currentUserID)) {
//                        Toast.makeText(_context, "Can't chat with own", Toast.LENGTH_LONG).show();
                        obj_interface.openChatsScreen();
                        return;
                    }
                    OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                    _loginOperationModel.setOperationType(constants.OperationTypeChat);
                    _loginOperationModel.setPosition(getAdapterPosition());
                    if (obj_interface.isLoggedIn(_loginOperationModel)) {
                        /*Intent _intent = new Intent(_context, ChatActivity.class);
                        _intent.putExtra("userid", ((UserPostList) view.getTag()).getUserKey());
                        _context.startActivity(_intent);*/
                        obj_interface.openChatScreen(((UserPostList) view.getTag()).getUser_key(), ((UserPostList) view.getTag()).getUserInfo().getUserName(), getAdapterPosition());
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }
            break;
//            case R.id.tv_do_tutorial: {
//
//                UserPostList _object = (UserPostList) view.getTag();
//                boolean isVisible = false;
//                if (_object.getPost_type() == 2) {
//                    isVisible = true;
//                }
//                showDialog(isVisible, _object);
//            }
//            break;

            case R.id.iv_original_image: {
                if (System.currentTimeMillis() - doubleClickLastTime < 300) {
                    doubleClickLastTime = 0;
//                    Toast.makeText(_context, "double tap!", Toast.LENGTH_SHORT).show();
                    if (_handler != null) {
                        _handler.removeCallbacks(_runnable);
                    }
                    obj_interface.enlargeImageView(_main_object.getThumbs().getThumb_image_large());
                    Log.e("TAG", "Double Tap Url " + _main_object.getThumbs().getThumb_image_large());
                    /*if (_main_object.getPost_type() == 1)
                    else {
                        obj_interface.enlargeImageView(_main_object.getThumbs().getThumb_image_large());
//                        RedirectAdvertisement(_main_object);
                    }*/
                    return;
                } else {
                    doubleClickLastTime = System.currentTimeMillis();
                }

                _handler = new Handler();
                _runnable = new Runnable() {
                    @Override
                    public void run() {
//                            FirebaseUtils.logEvents(_context, constants.comm_post_youtube_image_click);
                        if (_main_object.getPost_type() != 1)
                            RedirectAdvertisement(_main_object);
                        else if (_main_object.getYoutube_url() != null && !_main_object.getYoutube_url().isEmpty()) {
                            KGlobal.openInBrowser(_context, _main_object.getYoutube_url());
                            if (BuildConfig.DEBUG) {

                                Toast.makeText(_context, constants.comm_post_youtube_image_click, Toast.LENGTH_SHORT).show();
                            }


                            FirebaseUtils.logEvents(_context, constants.comm_post_youtube_image_click);
                        }
                    }
                };
                _handler.postDelayed(_runnable, 300);
            }
            break;
        }
    }

    Handler _handler = null;
    Runnable _runnable = null;

    void doLike(UserPostList obj) {

        OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
        _loginOperationModel.setOperationType(constants.OperationTypeLike);
        _loginOperationModel.setPosition(getAdapterPosition());

        OperationAfterLogin.LikeData _like_data = new OperationAfterLogin.LikeData();
        _like_data.setPost_id(_main_object.getPost_id());
        _like_data.setUser_id(user_id);

        _loginOperationModel.set_obj_like_data(_like_data);

        if (obj_interface.isLoggedIn(_loginOperationModel)) {
            if (obj.isLiked()) {
//                Toast.makeText(_context, "Alredy liked this post.", Toast.LENGTH_SHORT).show();
                Toast.makeText(_context, _context.getResources().getString(R.string.already_liked), Toast.LENGTH_SHORT).show();
                return;
            }
//            Log.e("TAGGG", "Is Post Liked " + _userPostList.getObjData().getPost_list().get(getAdapterPosition()).isLiked());
            boolean _operation_liked = !obj.isLiked();
            Log.e("TAGGG", "Is Post Liked _operation_liked " + _operation_liked);
            obj_interface.likeOperation(getAdapterPosition(), _operation_liked, false);
            obj.setLiked(_operation_liked);
            int total = Integer.parseInt(obj.getObjLikes().getTotal_likes());

            if (_operation_liked) {
                total = total + 1;
                obj.getObjLikes().setTotal_likes(total + "");
            } else {
                if (total > 0) {
                    total = total - 1;
                    obj.getObjLikes().setTotal_likes(total + "");
                }
            }

            try {
                ShowCommunityListAdapter._objInterface.notifyItem(obj.getPost_id());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at notify " + e.getMessage(), e);
            }
        } else {

            try {
                ShowCommunityListAdapter._objInterface.notifyItem(obj.getPost_id());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at notify " + e.getMessage(), e);
            }

        }
    }

    void doView(UserPostList obj) {

        OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
        _loginOperationModel.setOperationType(constants.OperationTypeView);
        _loginOperationModel.setPosition(getAdapterPosition());

        int total = Integer.parseInt(obj.getObjView().getTotal_views());

        OperationAfterLogin.ViewData _view_data = new OperationAfterLogin.ViewData();
        _view_data.setPost_id(_main_object.getPost_id());
        _view_data.setUser_id(user_id);
        _view_data.setTotalViews(String.valueOf(total));

        _loginOperationModel.set_obj_view_data(_view_data);

        if (obj_interface.isLoggedIn(_loginOperationModel)) {


            obj_interface.viewOperation(getAdapterPosition(), total, false);
//            obj.setLiked(_operation_liked);


            total = total + 1;
            obj.getObjView().setTotal_views(total + "");

            try {
                ShowCommunityListAdapter._objInterface.notifyItemView(obj.getPost_id(), total);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at notify " + e.getMessage(), e);
            }
        } else {

            try {
                total = total + 1;
                ShowCommunityListAdapter._objInterface.notifyItemView(obj.getPost_id(), total);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at notify " + e.getMessage(), e);
            }

        }
    }

    void doTutorial(UserPostList _object) {
        try {
            String cat_id = _object.getCat_id();
            String tut_id = _object.getPost_id();
            Intent intent = new Intent(_context, TutorialDetail_Activity.class);
            intent.putExtra("catID", cat_id);
            intent.putExtra("postID", tut_id);
            _context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    private void RedirectAdvertisement(UserPostList _obj) {
        try {
            Log.e("TAG", "RedirectAdvertisement called");
            if (_obj != null && _obj.getRedirect_url() != null && !_obj.getRedirect_url().isEmpty() && !_obj.getRedirect_url().equalsIgnoreCase("canvas")) {
                if (_obj.getImage_title() != null && !_obj.getImage_title().isEmpty()) {
                    String event = "cr_" + _obj.getImage_title();
                    String eventName = event.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                    if (eventName.length() >= 35) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                        FirebaseUtils.logEvents(_context, upToNCharacters);

                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(_context, eventName);
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, eventName, Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                String url = _obj.getRedirect_url();
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                _context.startActivity(browserIntent);*/
                KGlobal.openInBrowser(_context, url);
            } else if (_obj != null && _obj.getRedirect_url() != null && !_obj.getRedirect_url().isEmpty() && _obj.getRedirect_url().equalsIgnoreCase("canvas")) {
                try {
                    String event = "cr_" + _obj.getImage_title();
                    String eventName = event.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                    if (eventName.length() >= 35) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                        FirebaseUtils.logEvents(_context, upToNCharacters);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(_context, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(_context, eventName);
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, eventName, Toast.LENGTH_SHORT).show();
                        }

                    }

                    getCategoryDetailFromAPI(_obj.getCat_id(), _obj.getPost_id());
                } catch (Exception e) {
                    Log.e("TAG", "Exception " + e.getMessage());
                }
            } else {

                if (BuildConfig.DEBUG) {

                    Toast.makeText(_context, constants.community_OurAds_go_button_click, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(_context, constants.community_OurAds_go_button_click);
                doTutorial((UserPostList) _obj);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at Go ");
        }
    }

    void processTutorial() {

        if (tutorial_type == Tutorial_Type.See_Video) {
            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(_context, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getYoutube_link_list());
            intent.putExtra("isVideo", true);
            _context.startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Read_Post) {
            String eventName = "read_post_";

            Log.e("TAGGG", "ExtLinks " + _object.getExternal_link());
            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
            _context.startActivity(browserIntent);*/
            KGlobal.openInBrowser(_context, _object.getExternal_link());
            return;
        } else if (tutorial_type == Tutorial_Type.SeeVideo_From_External_Link) {
            String eventName = "watch_video_from_external_link_";

            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(_context, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getExternal_link());
            intent.putExtra("isVideo", true);
            Log.e("TAGGG", "URL " + _object.getExternal_link());
            _context.startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Overraid) {
            String eventName = "video_tutorial_overlaid_";
            String fileName = _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename();
            File file = new File(KGlobal.getTraceImageFolderPath(_context) + "/" + fileName);
            String youtubeLink = _object.getYoutube_link_list();
            if (youtubeLink != null) {
                String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                if (!file.exists()) {
                    new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_overlaid.getUrl(), false, _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename()).execute(_object.getVideo_and_file_list().get(0).obj_overlaid.getUrl());
                    return;
                } else {
                    /*if (_object.getPost_title() != null)
                        FirebaseUtils.logEvents(_context, "Try " + _object.getPost_title());
*/
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(_context, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("youtube_video_id", _youtube_id);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(_context));
                    _context.startActivity(intent);
                    return;
                }
            } else {
                Toast.makeText(_context, "Youtube Link Not Found!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_OVERLAY) {
            String eventName = "do_drawing_overlay_";

            String fileName = _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename();
            String fileURL = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
            new DownloadOverlayFromDoDrawing(fileURL, fileName, false).execute();
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_TRACE) {

            String fileName = _object.getVideo_and_file_list().get(0).getObj_trace_image().getFilename();
            String fileURL = _object.getVideo_and_file_list().get(0).getObj_trace_image().getUrl();
            new DownloadOverlayFromDoDrawing(fileURL, fileName, true).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Window) {
            new DownloadsTextFiles(_object).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Overlaid_Window) {

            String OverLayName = "", OverLayUrl = "";

            if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null) {
                OverLayName = (_object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
            } else {
                OverLayName = (_object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideo_and_file_list().get(1).getObj_overlaid().getUrl();
            }
            new DownloadOverlayImage(OverLayUrl, OverLayName).execute();
        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Trace) {
            try {
                String youtubeLink = _object.getYoutube_link_list();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    if (_object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().get(0).obj_trace_image != null && _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes() != null) {
                        if (_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge() != null) {
                            String fileName = _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge().substring(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge().lastIndexOf('/') + 1);
                            File file = new File(KGlobal.getTraceImageFolderPath(_context) + "/" + fileName);
                            if (!file.exists())
                                new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge(), true, "").execute(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge());
                            else {
                                /*if (_object.getPost_title() != null)
                                    FirebaseUtils.logEvents(_context, "Try " + _object.getPost_title());*/

                                StringConstants.IsFromDetailPage = false;
                                Intent intent = new Intent(_context, PaintActivity.class);
                                intent.putExtra("youtube_video_id", _youtube_id);
                                intent.setAction("YOUTUBE_TUTORIAL");
                                intent.putExtra("paint_name", file.getAbsolutePath());
                                if (!_object.getCanvas_color().isEmpty()) {
                                    intent.putExtra("canvas_color", _object.getCanvas_color());
                                }
                                _context.startActivity(intent);
                            }
                        }
                    } else {
                        /*if (_object.getPost_title() != null)
                            FirebaseUtils.logEvents(_context, "Try " + _object.getPost_title());*/
                        StringConstants.IsFromDetailPage = false;
                        Intent intent = new Intent(_context, PaintActivity.class);
                        intent.putExtra("youtube_video_id", _youtube_id);
                        intent.setAction("YOUTUBE_TUTORIAL");
                        _context.startActivity(intent);
                    }
                }

            } catch (Exception e) {
                Toast.makeText(_context, "Failed To Load!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.READ_POST_DEFAULT) {
            String eventName = "read_post_default_";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(defaultLink.trim()));
            _context.startActivity(browserIntent);
        }
    }

    class DownloadsImage extends AsyncTask<String, Void, String> {
        String youtubeLink, traceImageLink, fileName;
        Boolean isFromTrace = false;

        public DownloadsImage(String youtubeLink, String traceImageLink, Boolean isFromTrace, String fileName) {
            this.youtubeLink = youtubeLink;
            this.traceImageLink = traceImageLink;
            this.isFromTrace = isFromTrace;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(_context);
            progressDialog.setMessage(_context.getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

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
            File path = new File(KGlobal.getTraceImageFolderPath(_context)); //Creates app specific folder

            if (!path.exists()) {
                path.mkdirs();
            }
            File imageFile = new File(path, traceImageLink.substring(traceImageLink.lastIndexOf('/') + 1)); // Imagename.png
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
                progressDialog.dismiss();
                /*if (_object.getPost_title() != null)
                    FirebaseUtils.logEvents(_context, "Try " + _object.getPost_title());*/

                if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(_context, PaintActivity.class);
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("paint_name", path);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }
                    _context.startActivity(intent);
                } else {
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(_context, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(_context));
                    intent.putExtra("youtube_video_id", youtubeLink);
                    _context.startActivity(intent);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }


    class DownloadsTextFiles extends AsyncTask<Void, Void, ArrayList<String>> {
        PostDetailModel _objects;

        public DownloadsTextFiles(PostDetailModel _objects) {
            this._objects = _objects;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(_context);
            progressDialog.setMessage(_context.getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(_context));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = _objects.getVideo_and_file_list().get(i).getObj_text_files().getUrl();
                String fileName = _objects.getVideo_and_file_list().get(i).getObj_text_files().getFilename();

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

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                StringConstants.IsFromDetailPage = false;
                Intent intent = new Intent(_context, PaintActivity.class);
                String youtubeLink = _objects.getYoutube_link_list();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    intent.putExtra("youtube_video_id", _youtube_id);
                }
                intent.setAction("YOUTUBE_TUTORIAL_WITH_FILE");
                if (list.size() == 2) {
                    intent.putExtra("StrokeFilePath", list.get(0));
                    intent.putExtra("EventFilePath", list.get(1));
                } else
                    Toast.makeText(_context, "Stroke Event File Not Downloaded Properly", Toast.LENGTH_SHORT).show();

               /* if (_object.getPost_title() != null)
                    FirebaseUtils.logEvents(_context, "Try " + _object.getPost_title());*/

                _context.startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception " + e.getMessage());
            }
        }
    }

    class DownloadOverlayImage extends AsyncTask<Void, Void, ArrayList<String>> {
        String traceImageLink, fileName;

        public DownloadOverlayImage(String traceImageLink, String fileName) {
            this.traceImageLink = traceImageLink;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(_context);
            progressDialog.setMessage(_context.getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> filesList = downloadTextFiles();

            File file = new File(KGlobal.getTraceImageFolderPath(_context), fileName);

            if (file.exists()) {
                return filesList;
            } else {
                URL url = null;
                try {
                    url = new URL(traceImageLink);
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
                File path = new File(KGlobal.getTraceImageFolderPath(_context)); //Creates app specific folder

                if (!path.exists()) {
                    path.mkdirs();
                }
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
                return filesList;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> lst_main) {
            super.onPostExecute(lst_main);
            try {
                progressDialog.dismiss();
                /*if (_object.getPost_title() != null)
                    FirebaseUtils.logEvents(_context, "Try " + _object.getPost_title());*/

                StringConstants.IsFromDetailPage = false;
                Intent intent = new Intent(_context, PaintActivity.class);
                String youtubeLink = _object.getYoutube_link_list();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    intent.putExtra("youtube_video_id", _youtube_id);
                }
                intent.setAction("YOUTUBE_TUTORIAL_WITH_OVERLAID");
                if (lst_main.size() == 2) {
                    intent.putExtra("StrokeFilePath", lst_main.get(0));
                    intent.putExtra("EventFilePath", lst_main.get(1));
                }
                intent.putExtra("OverlaidImagePath", new File(KGlobal.getTraceImageFolderPath(_context), fileName).getAbsolutePath());
                _context.startActivity(intent);
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + lst_main.size());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }


        public ArrayList<String> downloadTextFiles() {
            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(_context));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = _object.getVideo_and_file_list().get(i).getObj_text_files().getUrl();
                String fileName = _object.getVideo_and_file_list().get(i).getObj_text_files().getFilename();

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
    }

    //To Process CANVAS type of posts

    ProgressDialog progressDialog;
    ApiInterface apiInterface;

    void getCategoryDetailFromAPI(String catID, String postID) {
        apiInterface = ApiClient.getRetroClient().create(ApiInterface.class);


        Call<String> call = apiInterface.getPostDetail(ApiClient.SECRET_KEY, catID, postID);
//        Call<String> call = apiInterface.getPostDetail(BASE_URL + "wp-json/wcra/v1/getTutorialPostsData/?cat_id=" + catID + "&post_id=" + postID + "&post_type=tutorials");

        progressDialog = new ProgressDialog(_context);
        progressDialog.setTitle(_context.getResources().getString(R.string.please_wait));
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    } catch (Exception e) {

                    }
                    if (response != null && response.body() != null) {
                        Log.e("TAGGG", "Response Data " + response.body());
                        parseResponseManually(response.body());
                    } else {

                        showSnackBar("Failed To Load");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    showSnackBar("Failed To Retrieve Content!");
                }
            });
        } catch (Exception e) {

            try {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            } catch (Exception ex) {
            }

            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    PostDetailModel _object;
    Tutorial_Type tutorial_type;

    void parseResponseManually(String response) {
        try {
            JSONArray mainArray = new JSONArray(response);
            if (mainArray.length() > 0) {
                ArrayList<videos_and_files> _lst_video_file = new ArrayList<videos_and_files>();
                JSONObject objectFirst = mainArray.getJSONObject(0);
                _object = new PostDetailModel();
                _object.setID(objectFirst.has("ID") ? objectFirst.getString("ID") : "");
                _object.setCategoryName(objectFirst.has("categoryName") ? objectFirst.getString("categoryName") : "");
                _object.setCategoryURL(objectFirst.has("categoryURL") ? objectFirst.getString("categoryURL") : "");
                _object.setExternal_link(objectFirst.has("external_link") ? objectFirst.getString("external_link") : "");
                _object.setCanvas_color(objectFirst.has("canvas_color") ? objectFirst.getString("canvas_color") : "");
                _object.setVisitPage(objectFirst.has("VisitPage") ? objectFirst.getString("VisitPage") : "");
                _object.setMembership_plan(objectFirst.has("membership_plan") ? objectFirst.getString("membership_plan") : "");
                _object.setPost_content(objectFirst.has("post_content") ? objectFirst.getString("post_content") : "");
                _object.setPost_date(objectFirst.has("post_date") ? objectFirst.getString("post_date") : "");
                _object.setPost_title(objectFirst.has("post_title") ? objectFirst.getString("post_title") : "");
                _object.setRating(objectFirst.has("Rating") ? objectFirst.getString("Rating") : "");
                _object.setText_descriptions(objectFirst.has("text_descriptions") ? objectFirst.getString("text_descriptions") : "");
                _object.setThumb_url(objectFirst.has("thumb_url") ? objectFirst.getString("thumb_url") : "");
                _object.setYoutube_link_list(objectFirst.has("youtube_link") ? objectFirst.getString("youtube_link") : "");

                if (objectFirst.has("ResizeImage") && objectFirst.getString("ResizeImage") != null) {
                    _object.setResizeImage(objectFirst.getString("ResizeImage"));
                }
                if (objectFirst.has("RelatedPostsData")) {
                    JSONArray related_list_json = objectFirst.getJSONArray("RelatedPostsData");
                    ArrayList<RelatedPostsData> related_List = new ArrayList<RelatedPostsData>();
                    if (related_list_json != null && related_list_json.length() > 0) {
                        for (int i = 0; i < related_list_json.length(); i++) {
                            RelatedPostsData obj_related = new RelatedPostsData();
                            JSONObject obj = related_list_json.getJSONObject(i);
                            if (obj.has("ID")) {
                                obj_related.setID(obj.getInt("ID"));
                            }
                            if (obj.has("post_title") && obj.getString("post_title") != null) {
                                obj_related.setPost_title(obj.getString("post_title"));
                            }
                            if (obj.has("thumbImage") && obj.getString("thumbImage") != null) {
                                obj_related.setThumbImage(obj.getString("thumbImage"));
                            }
                            related_List.add(obj_related);
                        }
                        _object.setList_related_post(related_List);
                    }
                }
                ArrayList<ContentSectionModel> contentSectionList = new ArrayList<>();
                ContentSectionModel obj_content = new ContentSectionModel();
                obj_content.setUrl(_object.getThumb_url());
                obj_content.setCaption("Featured");
                obj_content.setVideoContent(false);
                contentSectionList.add(obj_content);

                if (objectFirst.has("EmbededData")) {
                    JSONArray embededVideoList = objectFirst.getJSONArray("EmbededData");
                    for (int i = 0; i < embededVideoList.length(); i++) {
                        obj_content = new ContentSectionModel();
                        JSONObject obj = embededVideoList.getJSONObject(i);
                        obj_content.setUrl(obj.has("EmbededPath") ? obj.getString("EmbededPath") : "");
                        obj_content.setCaption(obj.has("Caption") ? obj.getString("Caption") : "");

                        if (obj_content.getUrl() != null && !obj_content.getUrl().isEmpty() && obj_content.getUrl().contains("youtu.be")) {

                            if (obj_content.getUrl().contains("youtu.be")) {
                                obj_content.setVideoContent(true);
                                String _youtube_id = obj_content.getUrl().replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                                obj_content.setYoutube_url("http://img.youtube.com/vi/" + _youtube_id + "/0.jpg");
                            }
                        }
                        contentSectionList.add(obj_content);
                    }
                }

                try {
                    if (objectFirst.has("EmbededImage")) {
                        JSONArray embededImageList = objectFirst.getJSONArray("EmbededImage");
                        for (int i = 0; i < embededImageList.length(); i++) {
                            JSONObject object = embededImageList.getJSONObject(i);
                            obj_content = new ContentSectionModel();
                            obj_content.setUrl(object.has("EmbededPath") ? object.getString("EmbededPath") : "");
                            obj_content.setCaption(object.has("Caption") ? object.getString("Caption") : "");
                            obj_content.setVideoContent(false);
                            contentSectionList.add(obj_content);
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at parseembeddd image " + e.getMessage());
                }
                _object.setFeaturedImage(contentSectionList);
                if (objectFirst.has("videos_and_files")) {

                    JSONArray videoArray = null;
                    try {
                        videoArray = objectFirst.getJSONArray("videos_and_files");
                    } catch (Exception e) {

                    }
                    if (videoArray != null)
                        for (int i = 0; i < videoArray.length(); i++) {
                            JSONObject obj = videoArray.getJSONObject(i);
                            videos_and_files videos_and_files = new videos_and_files();
                            if (obj.has("text_file") && !obj.getString("text_file").toString().equalsIgnoreCase("false")) {
                                text_files obj_text_file = new text_files();
                                JSONObject obj_text = obj.getJSONObject("text_file");
                                obj_text_file.setID(obj_text.has("ID") ? obj_text.getInt("ID") : 0);
                                obj_text_file.setTitle(obj_text.has("title") ? obj_text.getString("title") : "");
                                obj_text_file.setIcon(obj_text.has("icon") ? obj_text.getString("icon") : "");
                                obj_text_file.setFilename(obj_text.has("filename") ? obj_text.getString("filename") : "");
                                obj_text_file.setUrl(obj_text.has("url") ? obj_text.getString("url") : "");
                                videos_and_files.setObj_text_files(obj_text_file);
                            } else
                                videos_and_files.setObj_text_files(null);

                            try {
                                if (obj.has("trace_image") && !obj.getString("trace_image").toString().equalsIgnoreCase("false")) {
                                    trace_image obj_trace = new trace_image();
                                    JSONObject obj_trace_object = obj.getJSONObject("trace_image");
                                    obj_trace.setID(obj_trace_object.has("ID") ? obj_trace_object.getInt("ID") : 0);
                                    obj_trace.setTitle(obj_trace_object.has("title") ? obj_trace_object.getString("title") : "");
                                    obj_trace.setIcon(obj_trace_object.has("icon") ? obj_trace_object.getString("icon") : "");
                                    obj_trace.setFilename(obj_trace_object.has("filename") ? obj_trace_object.getString("filename") : "");
                                    obj_trace.setUrl(obj_trace_object.has("url") ? obj_trace_object.getString("url") : "");
                                    if (obj_trace_object.has("sizes")) {
                                        JSONObject objSize = obj_trace_object.getJSONObject("sizes");
                                        sizes obj_size = new sizes();
                                        obj_size.setLarge(objSize.has("large") ? objSize.getString("large") : "");
                                        obj_trace.setObj_sizes(obj_size);
                                    } else {
                                        obj_trace.setObj_sizes(null);
                                    }
                                    videos_and_files.setObj_trace_image(obj_trace);
                                } else
                                    videos_and_files.setObj_trace_image(null);

                            } catch (Exception e) {
                                Log.e("TAGGG", "Exception at add traceImage " + e.getMessage());
                            }
                            try {
                                if (obj.has("overlay_image") && !obj.getString("overlay_image").toString().equalsIgnoreCase("false")) {
                                    Overlaid overlaid = new Overlaid();
                                    JSONObject obj_overlaid_object = obj.getJSONObject("overlay_image");
                                    if (obj_overlaid_object != null) {
                                        overlaid.setTitle(obj_overlaid_object.has("title") ? obj_overlaid_object.getString("title") : "");
                                        overlaid.setFilename(obj_overlaid_object.has("filename") ? obj_overlaid_object.getString("filename") : "");
                                        overlaid.setUrl(obj_overlaid_object.has("url") ? obj_overlaid_object.getString("url") : "");
                                    }
                                    videos_and_files.setObj_overlaid(overlaid);
                                } else
                                    videos_and_files.setObj_overlaid(null);

                            } catch (Exception e) {
                                Log.e("TAGG", "Exception at getoverlay " + e.getMessage());
                            }
                            _lst_video_file.add(videos_and_files);
                        }

                    if (_lst_video_file != null && !_lst_video_file.isEmpty())
                        _object.setVideo_and_file_list(_lst_video_file);
                } else
                    _object.setVideo_and_file_list(null);

            }

            if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() >= 2 && (_object.getVideo_and_file_list().get(0).getObj_text_files() != null && _object.getVideo_and_file_list().get(1).getObj_text_files() != null) && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
                if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null || _object.getVideo_and_file_list().get(1).getObj_overlaid() != null) {
                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window;

                } else if (_object.getVideo_and_file_list().get(0).getObj_trace_image() == null || _object.getVideo_and_file_list().get(1).getObj_trace_image() == null) {
                    tutorial_type = Tutorial_Type.Strokes_Window;

                } else {
                    tutorial_type = Tutorial_Type.Strokes_Window;

                }
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {

                tutorial_type = Tutorial_Type.Video_Tutorial_Trace;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {

                tutorial_type = Tutorial_Type.Video_Tutorial_Overraid;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && _object.getYoutube_link_list().isEmpty()) {

                tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && _object.getYoutube_link_list().isEmpty()) {

                tutorial_type = Tutorial_Type.DO_DRAWING_TRACE;
            } else if (_object.getExternal_link() != null && !_object.getExternal_link().isEmpty()) {
                if (_object.getExternal_link().contains("youtu.be")) {

                    tutorial_type = Tutorial_Type.SeeVideo_From_External_Link;
                } else {

                    tutorial_type = Tutorial_Type.Read_Post;
                }
            } else if (_object != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty()) {

                tutorial_type = Tutorial_Type.See_Video;
            } else {

                tutorial_type = Tutorial_Type.READ_POST_DEFAULT;
            }

            try {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            } catch (Exception e) {

            }

            processTutorial();
        } catch (Exception e) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.e("TAGGG", "Exception at parse " + e.getMessage() + " " + e.getStackTrace().toString());
        }
    }

    class DownloadOverlayFromDoDrawing extends AsyncTask<Void, Void, String> {
        String traceImageLink, fileName;
        Boolean isFromTrace = false;

        public DownloadOverlayFromDoDrawing(String traceImageLink, String fileName, Boolean isFromTrace) {
            this.traceImageLink = traceImageLink;
            this.fileName = fileName;
            this.isFromTrace = isFromTrace;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(_context);
            progressDialog.setMessage(_context.getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... strings) {

            File file = new File(KGlobal.getTraceImageFolderPath(_context), fileName);
            if (file.exists()) {
                return file.getAbsolutePath();
            } else {
                URL url = null;
                try {
                    url = new URL(traceImageLink);
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
                File path = new File(KGlobal.getTraceImageFolderPath(_context)); //Creates app specific folder

                if (!path.exists()) {
                    path.mkdirs();
                }
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
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            try {
                progressDialog.dismiss();
               /* if (_object.getPost_title() != null)
                    FirebaseUtils.logEvents(_context, "Try_" + _object.getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_"));
*/
                StringConstants.IsFromDetailPage = false;
                if (isFromTrace) {
                    Intent intent = new Intent(_context, PaintActivity.class);
                    intent.setAction("Edit Paint");
                    intent.putExtra("FromLocal", true);
                    intent.putExtra("paint_name", path);
                    _context.startActivity(intent);
                } else {
                    Intent intent = new Intent(_context, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(_context));
                    _context.startActivity(intent);
                }
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + path);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    void showSnackBar(String msg) {
//        Snackbar snackbar = Snackbar
//                .make(mSwipeRefreshLayout, msg, Snackbar.LENGTH_LONG);
//        snackbar.show();
        Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
    }


}
