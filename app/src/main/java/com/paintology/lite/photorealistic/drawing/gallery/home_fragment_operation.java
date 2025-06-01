package com.paintology.lite.photorealistic.drawing.gallery;

import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;

import java.util.ArrayList;

public interface home_fragment_operation {

    public boolean changeListFormat(int rowCount);

    public void selectItem(int pos, boolean isFromRelatedPost);

    public void openTutorialDetail(String cat_id, String tut_id, int pos);

    public void setUserID(String _id);


    public void showHashTagHint();

    public ArrayList<Firebase_User> getFirebaseUserList();
}
