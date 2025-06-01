package com.paintology.lite.photorealistic.drawing.Community;

import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;
import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;

import java.util.ArrayList;

public interface PostOperation {

    public void doOperationOnPost(int position, int operationType);

    public void likeOperation(int pos, boolean isLike, boolean isFromSocialLogin);

    public void viewOperation(int pos, int totalViews, boolean isFromSocialLogin);

    public void addComment(int pos, String comment, ArrayList<Firebase_User> _user_list);

    public boolean isLoggedIn(OperationAfterLogin _loginOperationModel);

    public void downloadImage(int pos, boolean NeedToopenInCanvas);

    public void downloadImageOpenInOverlayCanvas(int pos);

    public void downloadImageOpenInTraceCanvas(int pos);

    public void shareImage(int pos);

    public void copyImage(int pos);

    public void reportPost(int pos);

    public void view_all_comment(int pos);

    public void seachByHashTag(String tag);

    public void viewProfile(int _user_id);

    public void enlargeImageView(String _url);

    public void showHideFab(boolean needToShown);

    public void openChatScreen(String key, String user_id, int position);

    public void openChatsScreen();


    public void openUsersPostsListScreen(int pos);
}
