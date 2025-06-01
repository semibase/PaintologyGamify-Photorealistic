package com.paintology.lite.photorealistic.drawing.Adapter;

import static com.paintology.lite.photorealistic.drawing.util.FirebaseUtils.context;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DatabaseHelperForCommunity;
import com.paintology.lite.photorealistic.drawing.Activity.favourite.FavActivity;
import com.paintology.lite.photorealistic.drawing.Activity.utils.ExtensionsKt;
import com.paintology.lite.photorealistic.drawing.Autocomplete.Autocomplete;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;
import com.paintology.lite.photorealistic.drawing.Community.BaseViewHolderCommunity;
import com.paintology.lite.photorealistic.drawing.Community.Community;
import com.paintology.lite.photorealistic.drawing.Community.PostOperation;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.TutorialDetail_Activity;
import com.paintology.lite.photorealistic.drawing.Fragment.MainCollectionFragment;
import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;
import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;
import com.paintology.lite.photorealistic.drawing.Model.UserPostList;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.ReadMoreOption;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.events.RefreshFavoriteEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityPostView extends BaseViewHolderCommunity implements View.OnClickListener {

    Context _context;
    String currentUserID = "";
    StringConstants constants;
    PostOperation obj_interface;

    public ImageView iv_msg_icon, iv_post_comment, iv_blur_image, iv_main_image, iv_menu_icon, iv_like, /*iv_report_icon,*/
            iv_share_icon, iv_download_image;
    RoundedImageView iv_profile_icon;
    //        ImageView iv_dislike;
    TextView tv_uname, tv_date_time, tv_image_title, tv_total_comment, tv_total_likes, tv_total_views;
    EditText edt_comment;
    ImageView viewImage;

    TextView tv_view_all_comment;
    AutoLinkTextView tv_cmnt_1, tv_cmnt_2, tv_cmnt_3, tv_description;
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
    HashMap<String, String> hashMap = new HashMap<>();
    String username = "";
    String user_id = "";
    LinearLayout ll_main;
    String defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/";
    private ReadMoreOption readMoreOption;

    ImageView iv_send_msg, iv_youtube_icon, ivYoutube;
    AppCompatImageView imageViewOnline;
    com.google.android.material.imageview.ShapeableImageView imageView;

    boolean isTablet = false;
    home_fragment_operation interface_home_fragment;

    Autocomplete userAutocomplete;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CommunityPost object;

    boolean IsFromFav;


    public CommunityPostView(View view, PostOperation _interface, Context context, home_fragment_operation _interface_home, boolean IsFromFav, boolean... FromProfileScreen) {
        super(view);

        this.obj_interface = _interface;
        _context = context;
        this.IsFromFav = IsFromFav;
        constants = new StringConstants();
        interface_home_fragment = _interface_home;
        currentUserID = constants.getString(constants.UserId, _context);
        isFromProfileScreen = FromProfileScreen[0];
        username = constants.getString(constants.Username, _context);
//        tv_tuto_name = (TextView) view.findViewById(R.id.tv_tutorial_name);
        isTablet = _context.getResources().getBoolean(R.bool.isTablet);
        imageView = view.findViewById(R.id.imgCountry);
        imageViewOnline = view.findViewById(R.id.imgUserActiveStatus);
//        btn_search = (TextView) view.findViewById(R.id.btn_search);
        rl_profile_section = (RelativeLayout) view.findViewById(R.id.rl_profile_section);
        iv_msg_icon = (ImageView) view.findViewById(R.id.iv_msg_icon);
        iv_post_comment = (ImageView) view.findViewById(R.id.iv_post_comment);
        edt_comment = (EditText) view.findViewById(R.id.edt_comment);
        edt_comment.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        viewImage = view.findViewById(R.id.ViewImage);

        sharedPreferences = _context.getApplicationContext().getSharedPreferences("PaintologyDB", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        iv_send_msg = (ImageView) view.findViewById(R.id.iv_send_msg);
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
        ivYoutube = (ImageView) view.findViewById(R.id.ivYoutube);
//        iv_youtube_icon.setVisibility(View.GONE);
        iv_youtube_icon.setOnClickListener(this::onClick);
        ivYoutube.setOnClickListener(this::onClick);

        iv_like = (ImageView) view.findViewById(R.id.iv_like_icon);
//            iv_dislike = (ImageView) view.findViewById(R.id.iv_dislike);
        iv_blur_image = (ImageView) view.findViewById(R.id.iv_blurred_img);
        iv_menu_icon = (ImageView) view.findViewById(R.id.iv_menu_icon);
        iv_main_image = (ImageView) view.findViewById(R.id.iv_original_image);
        iv_profile_icon = view.findViewById(R.id.iv_profile_icon);
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


        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_context instanceof FavActivity) {

                    Intent intent = new Intent(_context, Community.class);
                    intent.putExtra("community_post_id", object.getPost_id());
                    _context.startActivity(intent);

                }
            }
        });

        iv_msg_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_context instanceof FavActivity) {

                    Intent intent = new Intent(_context, Community.class);
                    intent.putExtra("community_post_id", object.getPost_id());
                    _context.startActivity(intent);

                } else {
                    obj_interface.view_all_comment(getAdapterPosition());

                }
            }
        });

        iv_like.setOnClickListener(this);
        tv_view_all_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj_interface.view_all_comment(getAdapterPosition());
            }
        });

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

                if (_context instanceof FavActivity) {

                    if (object.getPost_id() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("post_id", object.getPost_id());
                        ContextKt.sendUserEventWithParam(context, StringConstants.favorites_community_open, bundle);
                    }

                    Intent intent = new Intent(_context, Community.class);
                    intent.putExtra("community_post_id", object.getPost_id());
                    _context.startActivity(intent);

                } else {
                    CommunityPost _object = (CommunityPost) view.getTag();
                    boolean isVisible = false;
                    if (_object.getLegacy_data() != null && _object.getLegacy_data().getPost_type() == 2) {
                        isVisible = true;
                    }
                    showDialog(isVisible, _object, view);
                }


            }
        });


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
                if (IsFromFav) {
                    Toast.makeText(context, "fav", Toast.LENGTH_SHORT).show();
                }


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

                if (IsFromFav) {
                    Toast.makeText(context, "fav", Toast.LENGTH_SHORT).show();
                }

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


        iv_main_image.setOnClickListener(this);

        rl_profile_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj_interface.viewProfile(getAdapterPosition());
            }
        });

        iv_download_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (object.getPost_id() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("post_id", object.getPost_id());
                    bundle.putString("user_id", object.getAuthor().getUser_id());
                    ContextKt.sendUserEventWithParam(context, StringConstants.community_post_download, bundle);
                }
                obj_interface.downloadImage(getAdapterPosition(), false);
            }
        });
        iv_share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (object.getPost_id() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("post_id", object.getPost_id());
                    bundle.putString("user_id", object.getAuthor().getUser_id());
                    ContextKt.sendUserEventWithParam(context, StringConstants.community_post_share, bundle);
                }
                obj_interface.shareImage(getAdapterPosition());
            }
        });

        iv_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if (_context instanceof FavActivity) {

                    Intent intent = new Intent(_context, Community.class);
                    intent.putExtra("community_post_id", object.getPost_id());
                    _context.startActivity(intent);

                } else {
                    try {
                        View view = view1;
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        System.out.println("Post ID: " + object.getPost_id());
                        String number = String.valueOf(object.getPost_id());
                        System.out.println("Post ID: " + number);
                        OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                        _loginOperationModel.setOperationType(constants.OperationTypeComment);
                        _loginOperationModel.setPosition(getAdapterPosition());
                        OperationAfterLogin.CommentData _comment_data = new OperationAfterLogin.CommentData();
                        _comment_data.set_post_id(number);
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

            }
        });

        edt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_context instanceof FavActivity) {

                    Intent intent = new Intent(_context, Community.class);
                    intent.putExtra("community_post_id", object.getPost_id());
                    _context.startActivity(intent);

                }
            }
        });

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

                        String number = String.valueOf(object.getPost_id());
                        System.out.println("Post ID: " + number);
                        OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                        _loginOperationModel.setOperationType(constants.OperationTypeComment);
                        _loginOperationModel.setPosition(getAdapterPosition());
                        OperationAfterLogin.CommentData _comment_data = new OperationAfterLogin.CommentData();
                        _comment_data.set_post_id(number);
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


    }

    private long doubleClickLastTime = 0L;
    Handler _handler = null;
    Runnable _runnable = null;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.iv_youtube_icon, R.id.ivYoutube: {
                try {
                    CommunityPost _object = (CommunityPost) view.getTag();
                    KGlobal.openInBrowser(_context, _object.getLinks().getYoutube());
//                    KGlobal.openInBrowser(_context,"https://www.youtube.com/watch?v=NSAOrGb9orM");

                    if (BuildConfig.DEBUG) {
                        Toast.makeText(_context, constants.comm_post_youtube_button_click, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(_context, constants.comm_post_youtube_button_click);
                } catch (Exception e) {
                    Log.e("TAGG", "Click YouTube Icon Exception at openBrowser " + e.getMessage(), e);
                }
            }
            break;
            case R.id.iv_like_icon: {

                System.out.println("Post ID: " + object.getPost_id());
                doLike((CommunityPost) view.getTag());
            }
            break;

            case R.id.iv_send_msg: {

                if (_context instanceof FavActivity) {

                    Intent intent = new Intent(_context, Community.class);
                    intent.putExtra("community_post_id", object.getPost_id());
                    _context.startActivity(intent);

                } else {
                    try {
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(_context, constants.chat_community_post_click, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(_context, constants.chat_community_post_click);
                        if (((CommunityPost) view.getTag()).getAuthor().getUser_id().equals(currentUserID)) {
                            Toast.makeText(_context, "Can't chat with own", Toast.LENGTH_LONG).show();
                            obj_interface.openChatsScreen();
                            return;
                        }
                        OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                        _loginOperationModel.setOperationType(constants.OperationTypeChat);
                        _loginOperationModel.setPosition(getAdapterPosition());
                        if (obj_interface.isLoggedIn(_loginOperationModel)) {

                            Log.e("TAGG", "User isLoggedIn");
                        /*Intent _intent = new Intent(_context, ChatActivity.class);
                        _intent.putExtra("userid", ((UserPostList) view.getTag()).getUserKey());
                        _context.startActivity(_intent);*/
                            obj_interface.openChatScreen(((CommunityPost) view.getTag()).getUser_id(), ((UserPostList) view.getTag()).getUserInfo().getUserName(), getAdapterPosition());
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception " + e.getMessage());
                    }
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
//                if (System.currentTimeMillis() - doubleClickLastTime < 300) {
//                    doubleClickLastTime = 0;
////                    Toast.makeText(_context, "double tap!", Toast.LENGTH_SHORT).show();
//                    if (_handler != null) {
//                        _handler.removeCallbacks(_runnable);
//                    }
//                    obj_interface.enlargeImageView(object.getImages().getContent());
//                    Log.e("TAG", "Double Tap Url " + object.getImages().getContent());
//                    /*if (_main_object.getPost_type() == 1)
//                    else {
//                        obj_interface.enlargeImageView(_main_object.getThumbs().getThumb_image_large());
////                        RedirectAdvertisement(_main_object);
//                    }*/
//                    return;
//                } else {
//                    doubleClickLastTime = System.currentTimeMillis();
//                }
//
//                _handler = new Handler();
//                _runnable = new Runnable() {
//                    @Override
//                    public void run() {
////                            FirebaseUtils.logEvents(_context, constants.comm_post_youtube_image_click);
//                        if (object.getLegacy_data() != null && object.getLegacy_data().getPost_type() != 1) {
//                            RedirectAdvertisement(object);
//                        } else if (object.getLinks() != null && object.getLinks().getYoutube() != null && !object.getLinks().getYoutube().isEmpty()) {
//                            KGlobal.openInBrowser(_context, object.getLinks().getYoutube());
//                            if (BuildConfig.DEBUG) {
//
//                                Toast.makeText(_context, constants.comm_post_youtube_image_click, Toast.LENGTH_SHORT).show();
//                            }
//
//
//                            FirebaseUtils.logEvents(_context, constants.comm_post_youtube_image_click);
//                        }
//                    }
//                };
//                _handler.postDelayed(_runnable, 300);

                obj_interface.enlargeImageView(object.getImages().getContent());
            }
            break;
        }
    }

    @SuppressLint("SetTextI18n")
    void doView(CommunityPost obj, TextView textView) {

        if (hashMap.containsKey(obj.getPost_id()))
            return;
        else
            hashMap.put(obj.getPost_id(), obj.getPost_id());

        OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
        _loginOperationModel.setOperationType(constants.OperationTypeView);
        _loginOperationModel.setPosition(getAdapterPosition());

        int total = obj.getStatistic().getViews();

        OperationAfterLogin.ViewData _view_data = new OperationAfterLogin.ViewData();
        _view_data.setPost_id(String.valueOf(obj.getPost_id()));
        _view_data.setUser_id(user_id);
        _view_data.setTotalViews(String.valueOf(total));

        _loginOperationModel.set_obj_view_data(_view_data);

        if (!constants.getBoolean(constants.IsGuestUser, _context)) {

            total = total + 1;
            textView.setText(total + "");
            obj_interface.viewOperation(getAdapterPosition(), total, false);
//            obj.setLiked(_operation_liked);


           /* total = total + 1;
            obj.getStatistic().setViews(total);

            try {
                CommunityPostAdapter._objInterface.notifyItemView(obj.getPost_id(), total);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at notify " + e.getMessage(), e);
            }*/
        } else {

            try {
                total = total + 1;
                textView.setText(total + "");
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at notify " + e.getMessage(), e);
            }

        }
    }

    void doLike(CommunityPost obj) {


        if (_context instanceof FavActivity) {

            Intent intent = new Intent(_context, Community.class);
            intent.putExtra("community_post_id", obj.getPost_id());
            _context.startActivity(intent);

        } else {
            OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
            _loginOperationModel.setOperationType(constants.OperationTypeLike);
            _loginOperationModel.setPosition(getAdapterPosition());

            OperationAfterLogin.LikeData _like_data = new OperationAfterLogin.LikeData();
            _like_data.setPost_id(String.valueOf(obj.getPost_id()));
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
//            int total =  0;
//            if (obj.getStatistic() != null && obj.getStatistic().getLikes() != null) {
//                total = obj.getStatistic().getLikes();
//            } else {
//                CommunityPost.Statistic statistic  = new CommunityPost.Statistic();
//                statistic.setLikes(0);
//                statistic.setComments(0);
//                statistic.setViews(0);
//                obj.setStatistic(statistic);
//            }
//
//            if (_operation_liked) {
//                total = total + 1;
//                obj.getStatistic().setLikes(total);
//            } else {
//                if (total > 0) {
//                    total = total - 1;
//                    obj.getStatistic().setLikes(total);
//                }
//            }

                try {
                    CommunityPostAdapter._objInterface.notifyItem(String.valueOf(obj.getPost_id()));
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at notify " + e.getMessage(), e);
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(_context, constants.open_social_login_community_like_dialog, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(_context, constants.open_social_login_community_like_dialog);

                try {
                    CommunityPostAdapter._objInterface.notifyItem(String.valueOf(obj.getPost_id()));
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at notify " + e.getMessage(), e);
                }
            }
        }


    }

    private void RedirectAdvertisement(CommunityPost _obj) {
        try {
            Log.e("TAG", "RedirectAdvertisement called");
            if (_obj != null && _obj.getLinks().getRedirect() != null && !_obj.getLinks().getRedirect().isEmpty() && !_obj.getLinks().getRedirect().equalsIgnoreCase("canvas")) {
                if (_obj.getTitle() != null && !_obj.getTitle().isEmpty()) {
                    String event = "cr_" + _obj.getTitle();
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
                String url = _obj.getLinks().getRedirect();
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                _context.startActivity(browserIntent);*/
                KGlobal.openInBrowser(_context, url);
            } else if (_obj != null && _obj.getLinks().getRedirect() != null && !_obj.getLinks().getRedirect().isEmpty() && _obj.getLinks().getRedirect().equalsIgnoreCase("canvas")) {
                try {
                    String event = "cr_" + _obj.getTitle();
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

                    getCategoryDetailFromAPI(_obj.getLegacy_data().getCategory_id().toString(), _obj.getPost_id().toString());
                } catch (Exception e) {
                    Log.e("TAG", "Exception " + e.getMessage());
                }
            } else {

                if (BuildConfig.DEBUG) {

                    Toast.makeText(_context, constants.community_OurAds_go_button_click, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(_context, constants.community_OurAds_go_button_click);
                doTutorial((CommunityPost) _obj);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at Go ");
        }
    }

    void doTutorial(CommunityPost _object) {
        try {
            String cat_id = _object.getLegacy_data().getCategory_id().toString();
            String tut_id = _object.getPost_id().toString();
            Intent intent = new Intent(_context, TutorialDetail_Activity.class);
            intent.putExtra("catID", cat_id);
            intent.putExtra("postID", tut_id);
            _context.startActivity(intent);
        } catch (Exception e) {

        }
    }

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
//                        parseResponseManually(response.body());
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


//    void parseResponseManually(String response) {
//        try {
//            JSONArray mainArray = new JSONArray(response);
//            if (mainArray.length() > 0) {
//                ArrayList<videos_and_files> _lst_video_file = new ArrayList<videos_and_files>();
//                JSONObject objectFirst = mainArray.getJSONObject(0);
//                _object = new PostDetailModel();
//                _object.setID(objectFirst.has("ID") ? objectFirst.getString("ID") : "");
//                _object.setCategoryName(objectFirst.has("categoryName") ? objectFirst.getString("categoryName") : "");
//                _object.setCategoryURL(objectFirst.has("categoryURL") ? objectFirst.getString("categoryURL") : "");
//                _object.setExternal_link(objectFirst.has("external_link") ? objectFirst.getString("external_link") : "");
//                _object.setCanvas_color(objectFirst.has("canvas_color") ? objectFirst.getString("canvas_color") : "");
//                _object.setVisitPage(objectFirst.has("VisitPage") ? objectFirst.getString("VisitPage") : "");
//                _object.setMembership_plan(objectFirst.has("membership_plan") ? objectFirst.getString("membership_plan") : "");
//                _object.setPost_content(objectFirst.has("post_content") ? objectFirst.getString("post_content") : "");
//                _object.setPost_date(objectFirst.has("post_date") ? objectFirst.getString("post_date") : "");
//                _object.setPost_title(objectFirst.has("post_title") ? objectFirst.getString("post_title") : "");
//                _object.setRating(objectFirst.has("Rating") ? objectFirst.getString("Rating") : "");
//                _object.setText_descriptions(objectFirst.has("text_descriptions") ? objectFirst.getString("text_descriptions") : "");
//                _object.setThumb_url(objectFirst.has("thumb_url") ? objectFirst.getString("thumb_url") : "");
//                _object.setYoutube_link_list(objectFirst.has("youtube_link") ? objectFirst.getString("youtube_link") : "");
//
//                if (objectFirst.has("ResizeImage") && objectFirst.getString("ResizeImage") != null) {
//                    _object.setResizeImage(objectFirst.getString("ResizeImage"));
//                }
//                if (objectFirst.has("RelatedPostsData")) {
//                    JSONArray related_list_json = objectFirst.getJSONArray("RelatedPostsData");
//                    ArrayList<RelatedPostsData> related_List = new ArrayList<RelatedPostsData>();
//                    if (related_list_json != null && related_list_json.length() > 0) {
//                        for (int i = 0; i < related_list_json.length(); i++) {
//                            RelatedPostsData obj_related = new RelatedPostsData();
//                            JSONObject obj = related_list_json.getJSONObject(i);
//                            if (obj.has("ID")) {
//                                obj_related.setID(obj.getInt("ID"));
//                            }
//                            if (obj.has("post_title") && obj.getString("post_title") != null) {
//                                obj_related.setPost_title(obj.getString("post_title"));
//                            }
//                            if (obj.has("thumbImage") && obj.getString("thumbImage") != null) {
//                                obj_related.setThumbImage(obj.getString("thumbImage"));
//                            }
//                            related_List.add(obj_related);
//                        }
//                        _object.setList_related_post(related_List);
//                    }
//                }
//                ArrayList<ContentSectionModel> contentSectionList = new ArrayList<>();
//                ContentSectionModel obj_content = new ContentSectionModel();
//                obj_content.setUrl(_object.getThumb_url());
//                obj_content.setCaption("Featured");
//                obj_content.setVideoContent(false);
//                contentSectionList.add(obj_content);
//
//                if (objectFirst.has("EmbededData")) {
//                    JSONArray embededVideoList = objectFirst.getJSONArray("EmbededData");
//                    for (int i = 0; i < embededVideoList.length(); i++) {
//                        obj_content = new ContentSectionModel();
//                        JSONObject obj = embededVideoList.getJSONObject(i);
//                        obj_content.setUrl(obj.has("EmbededPath") ? obj.getString("EmbededPath") : "");
//                        obj_content.setCaption(obj.has("Caption") ? obj.getString("Caption") : "");
//
//                        if (obj_content.getUrl() != null && !obj_content.getUrl().isEmpty() && obj_content.getUrl().contains("youtu.be")) {
//
//                            if (obj_content.getUrl().contains("youtu.be")) {
//                                obj_content.setVideoContent(true);
//                                String _youtube_id = obj_content.getUrl().replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
//                                obj_content.setYoutube_url("http://img.youtube.com/vi/" + _youtube_id + "/0.jpg");
//                            }
//                        }
//                        contentSectionList.add(obj_content);
//                    }
//                }
//
//                try {
//                    if (objectFirst.has("EmbededImage")) {
//                        JSONArray embededImageList = objectFirst.getJSONArray("EmbededImage");
//                        for (int i = 0; i < embededImageList.length(); i++) {
//                            JSONObject object = embededImageList.getJSONObject(i);
//                            obj_content = new ContentSectionModel();
//                            obj_content.setUrl(object.has("EmbededPath") ? object.getString("EmbededPath") : "");
//                            obj_content.setCaption(object.has("Caption") ? object.getString("Caption") : "");
//                            obj_content.setVideoContent(false);
//                            contentSectionList.add(obj_content);
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e("TAGG", "Exception at parseembeddd image " + e.getMessage());
//                }
//                _object.setFeaturedImage(contentSectionList);
//                if (objectFirst.has("videos_and_files")) {
//
//                    JSONArray videoArray = null;
//                    try {
//                        videoArray = objectFirst.getJSONArray("videos_and_files");
//                    } catch (Exception e) {
//
//                    }
//                    if (videoArray != null)
//                        for (int i = 0; i < videoArray.length(); i++) {
//                            JSONObject obj = videoArray.getJSONObject(i);
//                            videos_and_files videos_and_files = new videos_and_files();
//                            if (obj.has("text_file") && !obj.getString("text_file").toString().equalsIgnoreCase("false")) {
//                                text_files obj_text_file = new text_files();
//                                JSONObject obj_text = obj.getJSONObject("text_file");
//                                obj_text_file.setID(obj_text.has("ID") ? obj_text.getInt("ID") : 0);
//                                obj_text_file.setTitle(obj_text.has("title") ? obj_text.getString("title") : "");
//                                obj_text_file.setIcon(obj_text.has("icon") ? obj_text.getString("icon") : "");
//                                obj_text_file.setFilename(obj_text.has("filename") ? obj_text.getString("filename") : "");
//                                obj_text_file.setUrl(obj_text.has("url") ? obj_text.getString("url") : "");
//                                videos_and_files.setObj_text_files(obj_text_file);
//                            } else
//                                videos_and_files.setObj_text_files(null);
//
//                            try {
//                                if (obj.has("trace_image") && !obj.getString("trace_image").toString().equalsIgnoreCase("false")) {
//                                    trace_image obj_trace = new trace_image();
//                                    JSONObject obj_trace_object = obj.getJSONObject("trace_image");
//                                    obj_trace.setID(obj_trace_object.has("ID") ? obj_trace_object.getInt("ID") : 0);
//                                    obj_trace.setTitle(obj_trace_object.has("title") ? obj_trace_object.getString("title") : "");
//                                    obj_trace.setIcon(obj_trace_object.has("icon") ? obj_trace_object.getString("icon") : "");
//                                    obj_trace.setFilename(obj_trace_object.has("filename") ? obj_trace_object.getString("filename") : "");
//                                    obj_trace.setUrl(obj_trace_object.has("url") ? obj_trace_object.getString("url") : "");
//                                    if (obj_trace_object.has("sizes")) {
//                                        JSONObject objSize = obj_trace_object.getJSONObject("sizes");
//                                        sizes obj_size = new sizes();
//                                        obj_size.setLarge(objSize.has("large") ? objSize.getString("large") : "");
//                                        obj_trace.setObj_sizes(obj_size);
//                                    } else {
//                                        obj_trace.setObj_sizes(null);
//                                    }
//                                    videos_and_files.setObj_trace_image(obj_trace);
//                                } else
//                                    videos_and_files.setObj_trace_image(null);
//
//                            } catch (Exception e) {
//                                Log.e("TAGGG", "Exception at add traceImage " + e.getMessage());
//                            }
//                            try {
//                                if (obj.has("overlay_image") && !obj.getString("overlay_image").toString().equalsIgnoreCase("false")) {
//                                    Overlaid overlaid = new Overlaid();
//                                    JSONObject obj_overlaid_object = obj.getJSONObject("overlay_image");
//                                    if (obj_overlaid_object != null) {
//                                        overlaid.setTitle(obj_overlaid_object.has("title") ? obj_overlaid_object.getString("title") : "");
//                                        overlaid.setFilename(obj_overlaid_object.has("filename") ? obj_overlaid_object.getString("filename") : "");
//                                        overlaid.setUrl(obj_overlaid_object.has("url") ? obj_overlaid_object.getString("url") : "");
//                                    }
//                                    videos_and_files.setObj_overlaid(overlaid);
//                                } else
//                                    videos_and_files.setObj_overlaid(null);
//
//                            } catch (Exception e) {
//                                Log.e("TAGG", "Exception at getoverlay " + e.getMessage());
//                            }
//                            _lst_video_file.add(videos_and_files);
//                        }
//
//                    if (_lst_video_file != null && !_lst_video_file.isEmpty())
//                        _object.setVideo_and_file_list(_lst_video_file);
//                } else
//                    _object.setVideo_and_file_list(null);
//
//            }
//
//            if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() >= 2 && (_object.getVideo_and_file_list().get(0).getObj_text_files() != null && _object.getVideo_and_file_list().get(1).getObj_text_files() != null) && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
//                if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null || _object.getVideo_and_file_list().get(1).getObj_overlaid() != null) {
//                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window;
//
//                } else if (_object.getVideo_and_file_list().get(0).getObj_trace_image() == null || _object.getVideo_and_file_list().get(1).getObj_trace_image() == null) {
//                    tutorial_type = Tutorial_Type.Strokes_Window;
//
//                } else {
//                    tutorial_type = Tutorial_Type.Strokes_Window;
//
//                }
//            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
//
//                tutorial_type = Tutorial_Type.Video_Tutorial_Trace;
//            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
//
//                tutorial_type = Tutorial_Type.Video_Tutorial_Overraid;
//            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && _object.getYoutube_link_list().isEmpty()) {
//
//                tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY;
//            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && _object.getYoutube_link_list().isEmpty()) {
//
//                tutorial_type = Tutorial_Type.DO_DRAWING_TRACE;
//            } else if (_object.getExternal_link() != null && !_object.getExternal_link().isEmpty()) {
//                if (_object.getExternal_link().contains("youtu.be")) {
//
//                    tutorial_type = Tutorial_Type.SeeVideo_From_External_Link;
//                } else {
//
//                    tutorial_type = Tutorial_Type.Read_Post;
//                }
//            } else if (_object != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty()) {
//
//                tutorial_type = Tutorial_Type.See_Video;
//            } else {
//
//                tutorial_type = Tutorial_Type.READ_POST_DEFAULT;
//            }
//
//            try {
//                if (progressDialog.isShowing())
//                    progressDialog.dismiss();
//            } catch (Exception e) {
//
//            }
//
//            processTutorial();
//        } catch (Exception e) {
//            if (progressDialog != null) {
//                progressDialog.dismiss();
//            }
//            Log.e("TAGGG", "Exception at parse " + e.getMessage() + " " + e.getStackTrace().toString());
//        }
//    }


    private void updateIconAlpha(String text) {
        if (text.isEmpty()) {
            iv_post_comment.setAlpha(0.5f); // Set alpha to 0.5 if EditText is empty
        } else {
            iv_post_comment.setAlpha(1.0f); // Set alpha to 1.0 if EditText is not empty
        }
    }

    void showSnackBar(String msg) {
//        Snackbar snackbar = Snackbar
//                .make(mSwipeRefreshLayout, msg, Snackbar.LENGTH_LONG);
//        snackbar.show();
        Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBindView(CommunityPost object) {
        this.object = object;

        try {
            iv_like.setTag(object);
            iv_main_image.setTag(object);
            iv_youtube_icon.setTag(object);
            ivYoutube.setTag(object);
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
                if (object.getLegacy_data() != null) {
                    isTypeTutorial = object.getLegacy_data().getPost_type();
                }

                if (object.getLinks() != null) {
                    if (object.getLinks().getYoutube() != null && !object.getLinks().getYoutube().isEmpty()) {
                        iv_youtube_icon.setVisibility(View.VISIBLE);
                        ivYoutube.setVisibility(View.VISIBLE);
                    } else{
                        iv_youtube_icon.setVisibility(View.GONE);
                        ivYoutube.setVisibility(View.GONE);
                    }
                } else {
                    iv_youtube_icon.setVisibility(View.GONE);
                    ivYoutube.setVisibility(View.GONE);
                }


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
                    System.out.println("avatar :" + object.getAuthor().getAvatar());


//                    tv_do_tutorial.setVisibility(View.GONE);
                  /*  if (object.getAuthor() != null && object.getAuthor().getAvatar() != null && !object.getAuthor().getAvatar().isEmpty()) {
                        String profileUrl = decodeEscapedUnicode(object.getAuthor().getAvatar());
                        System.out.println("avatar :" + object.getAuthor().getAvatar());
                        Glide.with(_context)
                                .load(object.getAuthor().getAvatar())
                                .error(R.drawable.img_default_avatar)
                                .placeholder(R.drawable.img_default_avatar)
                                .apply(new RequestOptions().placeholder(R.drawable.img_default_avatar).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                                .into(iv_profile_icon);
                    } else {
                        iv_profile_icon.setImageDrawable(ContextCompat.getDrawable(_context,R.drawable.img_default_avatar));
                    }*/

                    ExtensionsKt.getUserProfileData(object.getAuthor().getUser_id(), iv_profile_icon, imageView, null);
                    ExtensionsKt.getUserOnlineStatus(imageViewOnline, object.getAuthor().getUser_id());

                    if (object.getCreated_at() != null) {
                        String time = convertTimestampToLocal(object.getCreated_at().toString());
//                        String time = object.getCreated_at();
                        tv_date_time.setText(time);
                    }

                    if (object.getAuthor() != null && object.getAuthor().getName() != null) {
                        if (isFromProfileScreen) {
                            tv_uname.setTextColor(_context.getResources().getColor(R.color.text_black_color));
                            tv_uname.setText(object.getAuthor().getName());
                            Log.e("TAGGG", "Uname>> isFromProfileScreen " + object.getAuthor().getName());
                        } else {
                            tv_uname.setTextColor(_context.getResources().getColor(R.color.text_black_color));
                            String name = object.getAuthor().getName();
                            tv_uname.setText(name);
                            Log.e("TAGGG", "Uname>> in elase " + name);
                        }
                    } else {
                        tv_uname.setText("");
                    }

                    if (object.getTitle() != null && !object.getTitle().isEmpty()) {
                        tv_image_title.setText(Html.fromHtml(object.getTitle()));
                        tv_image_title.setVisibility(View.VISIBLE);
                    } else
                        tv_image_title.setVisibility(View.GONE);


                    if (object.getDescription() != null && !object.getDescription().isEmpty()) {
                        tv_description.setText(Html.fromHtml(object.getDescription()));
                        readMoreOption.addReadMoreTo(tv_description, Html.fromHtml(object.getDescription()).toString());
                        tv_description.setVisibility(View.VISIBLE);
                    } else
                        tv_description.setVisibility(View.GONE);
                }

                if (object.getTags() != null && !object.getTags().isEmpty()) {
                    String tags = "";
                    for (int i = 0; i < object.getTags().size(); i++) {
                        tags = tags + "#" + object.getTags().get(i) + " ";
                    }
//                    setConainer(object.getTags());
//                    System.out.println("HashTag " + object.getDescription() + " " + tags);
                    System.out.println("Post_id" + object.getPost_id());

                    tv_description.setText(Html.fromHtml(object.getDescription() + " " + tags));
                    readMoreOption.addReadMoreTo(tv_description, Html.fromHtml(object.getDescription() + " " + tags));
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
            iv_send_msg.setVisibility(View.GONE);

            if (object != null) {
                if (object.getImages().getContent() != null && !object.getImages().getContent().isEmpty()) {
                    Glide.with(_context)
                            .load(object.getImages().getContent())
                            .apply(new RequestOptions().placeholder(R.drawable.thumbnaildefault).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(iv_main_image);
                }


                if (object.getStatistic() != null) {
                    if (object.getStatistic().getComments() == 0) {
                        tv_total_comment.setText("");
                        tv_view_all_comment.setVisibility(View.GONE);
                    } else {
                        tv_total_comment.setText(String.valueOf(object.getStatistic().getComments()));
                        if (object.getStatistic().getComments() > 3) {
                            tv_view_all_comment.setText("View All " + (object.getStatistic().getComments()) + " Comments...");
                            tv_view_all_comment.setVisibility(View.VISIBLE);
                        } else
                            tv_view_all_comment.setVisibility(View.GONE);
                    }
                } else {
                    tv_total_comment.setText("");
                }

                if (object.getStatistic() != null) {
                    if (object.getStatistic().getLikes() == 0) {
                        tv_total_likes.setText("");
                    } else {
                        tv_total_likes.setText(String.valueOf(object.getStatistic().getLikes()));
                    }

                } else {
                    tv_total_likes.setText("");
                }
                if (object.getStatistic() != null) {

                    if (object.getStatistic().getViews() == 0) {
                        tv_total_views.setText("");
                    } else {
                        tv_total_views.setText(String.valueOf(object.getStatistic().getViews()));
                    }
                } else {
                    Log.e("TAGGG", "Total view set 0");
                    tv_total_views.setText("");
                }

                doView(object, tv_total_views);

                if (object.getLastComments() != null) {
                    if (object.getLastComments().size() >= 3) {

                        String uname_1 = object.getLastComments().get(0).getName();
                        String uname_2 = object.getLastComments().get(1).getName();
                        String uname_3 = object.getLastComments().get(2).getName();

                        tv_cmnt_1.setVisibility(View.VISIBLE);
                        tv_cmnt_2.setVisibility(View.VISIBLE);
                        tv_cmnt_3.setVisibility(View.VISIBLE);
                        String cment_3 = "<b>" + uname_3 + "</b>" + " " + object.getLastComments().get(2).getComment();
                        tv_cmnt_3.setText(Html.fromHtml(cment_3));
                        String cment_2 = "<b>" + uname_2 + "</b>" + " " + object.getLastComments().get(1).getComment();
                        tv_cmnt_2.setText(Html.fromHtml(cment_2));
                        String cment_1 = "<b>" + uname_1 + "</b>" + " " + object.getLastComments().get(0).getComment();
                        tv_cmnt_1.setText(Html.fromHtml(cment_1));

                    } else if (object.getLastComments().size() >= 2) {

                        String uname_1 = object.getLastComments().get(0).getName();
                        String uname_2 = object.getLastComments().get(1).getName();

                        tv_cmnt_3.setVisibility(View.GONE);
                        tv_cmnt_2.setVisibility(View.VISIBLE);
                        tv_cmnt_1.setVisibility(View.VISIBLE);
                        String cment_2 = "<b>" + uname_2 + "</b>" + " " + object.getLastComments().get(1).getComment();
                        tv_cmnt_2.setText(Html.fromHtml(cment_2));
                        String cment_1 = "<b>" + uname_1 + "</b>" + " " + object.getLastComments().get(0).getComment();
                        tv_cmnt_1.setText(Html.fromHtml(cment_1));
                    } else if (object.getLastComments().size() >= 1) {
                        tv_cmnt_1.setVisibility(View.VISIBLE);
                        tv_cmnt_3.setVisibility(View.GONE);
                        tv_cmnt_2.setVisibility(View.GONE);

                        String uname_1 = object.getLastComments().get(0).getName();

                        String cment_1 = "<b>" + uname_1 + "</b>" + " " + object.getLastComments().get(0).getComment();
//                    myViewHolder.tv_cmnt_1.setText(_userPostList.getObjData().getPost_list().get(i).getCommentsList().getPost_comment_lists().get(0).getComment_content());
                        tv_cmnt_1.setText(Html.fromHtml(cment_1));
                    } else {
                        tv_cmnt_1.setVisibility(View.GONE);
                        tv_cmnt_2.setVisibility(View.GONE);
                        tv_cmnt_3.setVisibility(View.GONE);
                    }
                } else {
                    tv_cmnt_1.setVisibility(View.GONE);
                    tv_cmnt_2.setVisibility(View.GONE);
                    tv_cmnt_3.setVisibility(View.GONE);
                    tv_view_all_comment.setVisibility(View.GONE);
                }


                if (object.isDownloaded())
                    iv_download_image.setImageResource(R.drawable.community_download_icon);
                else
                    iv_download_image.setImageResource(R.drawable.community_download_icon);
            }

        } catch (Exception e) {
            Log.e("TAGGG", "Exception at holder " + e.getMessage(), e);
        }


    }


    public void showDialog(boolean isVisible, CommunityPost _object, View view) {


//        items[items.length - 1] = "";
        if (BuildConfig.DEBUG) {


            Toast.makeText(_context, constants.comm_post_menuitem_press, Toast.LENGTH_SHORT).show();
        }

        FirebaseUtils.logEvents(_context, constants.comm_post_menuitem_press);
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);

        int menuRes = R.menu.popup_community_list_menu;
        int menuMoreRes = R.menu.popup_community_list_menu_more;

        try {

            if (object.getLinks() != null) {
                if (object.getLinks().getYoutube() != null && !object.getLinks().getYoutube().isEmpty()) {

                    menuRes = R.menu.popup_community_list_youtube_menu;
                    menuMoreRes = R.menu.popup_community_list_youtube_menu_more;

                } else {

                    menuRes = R.menu.popup_community_list_menu;
                    menuMoreRes = R.menu.popup_community_list_menu_more;

                }
            } else {

                menuRes = R.menu.popup_community_list_menu;
                menuMoreRes = R.menu.popup_community_list_menu_more;


            }
        } catch (Exception e) {

        }

        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(_context, view);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(isVisible ? menuMoreRes : menuRes, popupMenu.getMenu());
        MenuItem usersPostsMenuItem = popupMenu.getMenu().findItem(R.id.users_posts);
        if (usersPostsMenuItem != null) {
            if (object.getAuthor() != null && object.getAuthor().getName() != null) {
                usersPostsMenuItem.setTitle("Posts by " + object.getAuthor().getName());
            } else {
                usersPostsMenuItem.setTitle("Posts by ");
            }
        }
        DatabaseHelperForCommunity dbHelper = new DatabaseHelperForCommunity(_context);
        MenuItem addFavItem = popupMenu.getMenu().findItem(R.id.AddFav);
        MenuItem removeFavItem = popupMenu.getMenu().findItem(R.id.removeFav);
        if (addFavItem != null && removeFavItem != null) {
            if (dbHelper.checkCommunityPost(_object)) {
                addFavItem.setVisible(false);
                removeFavItem.setVisible(true);
            } else {
                addFavItem.setVisible(true);
                removeFavItem.setVisible(false);
            }
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Toast message on menu item clicked
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.chat:
                        try {
                            if (AppUtils.isLoggedIn()) {
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                System.out.println("Current User ID :- " + currentUserID);
                                System.out.println("Current User UID :- " + uid);
                                System.out.println("Post User UID :- " + _object.getAuthor().getUser_id());
                                if (((CommunityPost) view.getTag()).getAuthor().getUser_id().equals(uid)) {
                                    Toast.makeText(_context, "Can't chat with own", Toast.LENGTH_LONG).show();
                                    obj_interface.openChatsScreen();
                                    break;
                                }
                                OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                                _loginOperationModel.setOperationType(constants.OperationTypeChat);
                                _loginOperationModel.setPosition(getAdapterPosition());
                                if (obj_interface.isLoggedIn(_loginOperationModel)) {
                                    if (object.getPost_id() != null) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("post_id", object.getPost_id());
                                        bundle.putString("user_id", object.getAuthor().getUser_id());
                                        ContextKt.sendUserEventWithParam(context, StringConstants.community_post_chat_author, bundle);
                                    }
                                    obj_interface.openChatScreen(_object.getUser_id(), _object.getAuthor().getName(), getAdapterPosition());
                                }
                            } else {
                                Intent intent = new Intent(_context, LoginActivity.class);
                                _context.startActivity(intent);
                            }

                        } catch (Exception e) {
                            Log.e("TAGG", "Exception " + e.getMessage());
                        }
                        break;
                    case R.id.users_posts:
//                        String _userId = _object.getUser_id();
//                        String userName = _object.getAuthor().getName();
//                        Intent _intent = new Intent(_context, CommunityDetail.class);
//                        _intent.setAction("isFromProfile");
//                        _intent.putExtra("user_id", _userId + "");
//                        _intent.putExtra("user_name", userName + "");
//                        _context.startActivity(_intent);
                        if (AppUtils.isLoggedIn()) {
                            if (object.getPost_id() != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("post_id", object.getPost_id());
                                bundle.putString("user_id", object.getAuthor().getUser_id());
                                ContextKt.sendUserEventWithParam(context, StringConstants.community_post_open_author_posts, bundle);
                            }
                            obj_interface.openUsersPostsListScreen(getAdapterPosition());
                        } else {
                            Intent intent = new Intent(_context, LoginActivity.class);
                            _context.startActivity(intent);
                        }
                        break;
                    case R.id.more_detail:

//                        RedirectAdvertisement(_object);
                        break;
                    case R.id.share:
                        iv_share_icon.performClick();
                        break;
                    case R.id.copy_link:
                        if (object.getPost_id() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("post_id", object.getPost_id());
                            bundle.putString("user_id", object.getAuthor().getUser_id());
                            ContextKt.sendUserEventWithParam(context, StringConstants.community_post_copy_link, bundle);
                        }
                        obj_interface.copyImage(getAdapterPosition());
                        break;
                    case R.id.open_overlay:
                        if (AppUtils.isLoggedIn()) {
                            if (object.getPost_id() != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("post_id", object.getPost_id());
                                bundle.putString("user_id", object.getAuthor().getUser_id());
                                bundle.putString("canvas_type", "overlay");
                                ContextKt.sendUserEventWithParam(context, StringConstants.community_post_open_in_canvas, bundle);
                            }
                            obj_interface.downloadImageOpenInOverlayCanvas(getAdapterPosition());
                        } else {
                            Intent intent = new Intent(_context, LoginActivity.class);
                            _context.startActivity(intent);
                        }
                        break;
                    case R.id.open_trace:

                        if (AppUtils.isLoggedIn()) {
                            if (object.getPost_id() != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("post_id", object.getPost_id());
                                bundle.putString("user_id", object.getAuthor().getUser_id());
                                bundle.putString("canvas_type", "trace");
                                ContextKt.sendUserEventWithParam(context, StringConstants.community_post_open_in_canvas, bundle);
                            }
                            obj_interface.downloadImageOpenInTraceCanvas(getAdapterPosition());
                        } else {
                            Intent intent = new Intent(_context, LoginActivity.class);
                            _context.startActivity(intent);
                        }
                        break;
                    case R.id.save:
                        iv_download_image.performClick();
                        break;
                    case R.id.report:
                        try {
                            OperationAfterLogin _loginOperationModel = new OperationAfterLogin();
                            _loginOperationModel.setOperationType(constants.OperationTypeReport);
                            _loginOperationModel.setPosition(getAdapterPosition());
                            if (obj_interface.isLoggedIn(_loginOperationModel)) {
                                if (object.getPost_id() != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("post_id", object.getPost_id());
                                    bundle.putString("user_id", object.getAuthor().getUser_id());
                                    ContextKt.sendUserEventWithParam(context, StringConstants.community_post_report, bundle);
                                }
                                obj_interface.reportPost(getAdapterPosition());
                            }
                        } catch (Exception e) {
                            Log.e("DetailViewAdapter", e.getMessage());
                        }
                        break;
                    case R.id.yt_video:
                        if (_object != null && _object.getLinks() != null && _object.getLinks().getYoutube() != null && !_object.getLinks().getYoutube().isEmpty()) {

                            if (object.getPost_id() != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("post_id", object.getPost_id());
                                bundle.putString("user_id", object.getAuthor().getUser_id());
                                ContextKt.sendUserEventWithParam(context, StringConstants.community_post_video_link, bundle);
                            }

                            try {
                                KGlobal.openInBrowser(_context, _object.getLinks().getYoutube());
                            } catch (Exception e) {
                                Log.e("DetailViewAdapter", e.getMessage());
                            }

                        }
                        break;


                    case R.id.AddFav:
                        if (object.getPost_id() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("post_id", object.getPost_id());
                            bundle.putString("user_id", object.getAuthor().getUser_id());
                            ContextKt.sendUserEventWithParam(context, StringConstants.community_post_add_favorite, bundle);
                        }
                        AddCommunityToFav(_object);
                        EventBus.getDefault().post(new RefreshFavoriteEvent(3));
                        break;


                    case R.id.removeFav:
                        if (object.getPost_id() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("post_id", object.getPost_id());
                            bundle.putString("user_id", object.getAuthor().getUser_id());
                            ContextKt.sendUserEventWithParam(context, StringConstants.community_post_remove_favorite, bundle);
                        }
                        DatabaseHelperForCommunity dbHelper = new DatabaseHelperForCommunity(_context);
                        dbHelper.removeCommunityPost(object);
                        EventBus.getDefault().post(new RefreshFavoriteEvent(3));
                        break;

                }
                return true;
            }
        });
        // Showing the popup menu
        popupMenu.show();

    }

    private void RemoveCommunityFromFav(CommunityPost object) {

        DatabaseHelperForCommunity dbHelper = new DatabaseHelperForCommunity(_context);
        dbHelper.removeCommunityPost(object);
        MainCollectionFragment.fragment.RemoveItem(getAdapterPosition());
    }


    private void AddCommunityToFav(CommunityPost post) {
        try {
            DatabaseHelperForCommunity dbHelper = new DatabaseHelperForCommunity(_context);
            dbHelper.addCommunityPost(post, _context);
        } catch (Exception e) {
            Toast.makeText(_context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private String convertTimestampToLocal(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String decodeEscapedUnicode(String input) {
        Pattern pattern = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
        Matcher matcher = pattern.matcher(input);
        StringBuffer decodedString = new StringBuffer();

        while (matcher.find()) {
            String unicodeChar = matcher.group(1);
            int code = Integer.parseInt(unicodeChar, 16);
            matcher.appendReplacement(decodedString, Character.toString((char) code));
        }
        matcher.appendTail(decodedString);
        return decodedString.toString();
    }
}
