package com.paintology.lite.photorealistic.drawing;

import androidx.appcompat.widget.SwitchCompat;

public interface MainInterface {

    public void setColorInBox(int code);

    public boolean isIndicatorVisible();

    public void clearPaintingAndSetNew();

    public void disableColorPenMode();

    public void exitFromAPP();

    public void showCursor();

    public boolean isInTutorialMode();

    public String getBackgroundImagePath();

    public void resetTimer();

    public void addStroke(String strokeData);

    public void addRemoveStrokeInRedoList(boolean isAdd);

    public void clearAddRemoveStrokeInRedoList();

    public void storeInTraceList(String drawingFileName);

    public void storeInOverlayList(String drawingFileName);

    public void hidePlayer();

    public void saveToLocal(String path, int Tag);

    public void GetCatchColor(String path);

    public void hideShowCross(boolean isHide);

    public void setSize();

    public void setSpecialFunctionState(SwitchCompat _switch);

    void cancelBrushDialogListener();

    void brushSetting();

}
