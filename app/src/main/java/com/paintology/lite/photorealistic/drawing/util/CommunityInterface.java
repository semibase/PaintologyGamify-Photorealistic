package com.paintology.lite.photorealistic.drawing.util;

import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;

public interface CommunityInterface {


    public void ReflectColor(int code);

    public void ShowProfileIcon();

    public boolean isLoggedIn(OperationAfterLogin _operationAfterLogin);

    public void showToolTip();

    public void DisableAllView(int typeToEnable);

    public void enlargeImageView(String _url);

    public void showHideFab(boolean needToShown);

    public void hideSearchBar();


}
