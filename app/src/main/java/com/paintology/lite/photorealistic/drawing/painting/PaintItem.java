package com.paintology.lite.photorealistic.drawing.painting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.paintology.lite.photorealistic.drawing.Enums.drawing_type;

public class PaintItem {
    public String mFileName, fileThumbName;
    public Bitmap mThumbnail;
    public Boolean isSelected = false;
    public long lastModifiedTime = 0;
    private boolean isDefaultImageLoaded;
    private String type; // New type attribute


    public drawing_type _drawing_type;

    public PaintItem(Bitmap pBitmap, String pString, long lastModifiedTime, String fileThumbName,
                     drawing_type _drawing_type, boolean isDefaultImageLoaded) {
        mThumbnail = pBitmap;
        mFileName = pString;
        this.isDefaultImageLoaded = isDefaultImageLoaded;
        this.lastModifiedTime = lastModifiedTime;
        this.fileThumbName = fileThumbName;
        this._drawing_type = _drawing_type;
//        this.type = type; // Initialize new type attribute

    }

    public void freeThumbnail() {
        if ((mThumbnail == null) || (mThumbnail.isRecycled()))
            return;

        mThumbnail.recycle();
        mThumbnail = null;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getThumbnail() {
        try {
            if ((mThumbnail == null) || (mThumbnail.isRecycled())) {
                Bitmap lBitmap = BitmapFactory.decodeFile(mFileName);
                mThumbnail = lBitmap;
            }
            return mThumbnail;
        } catch (Exception lException) {
            mThumbnail = null;
            return null;
        }
    }

    public String getFileThumbName() {
        return fileThumbName;
    }

    public void setFileThumbName(String fileThumbName) {
        this.fileThumbName = fileThumbName;
    }

    public void setThumbnail(Bitmap pBitmap) {
        mThumbnail = pBitmap;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public drawing_type get_drawing_type() {
        return _drawing_type;
    }

    public void set_drawing_type(drawing_type _drawing_type) {
        this._drawing_type = _drawing_type;
    }

    public boolean isDefaultImageLoaded() {
        return isDefaultImageLoaded;
    }

    public void setDefaultImageLoaded(boolean defaultImageLoaded) {
        isDefaultImageLoaded = defaultImageLoaded;
    }
}
