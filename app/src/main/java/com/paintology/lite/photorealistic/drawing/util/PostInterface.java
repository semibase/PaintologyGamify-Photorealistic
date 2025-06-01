package com.paintology.lite.photorealistic.drawing.util;

public interface PostInterface {


    public void postImage(int position, String title, String description, String hashTag, String _youtube_url);

    public void cancelClick();

    public boolean isReserevedUsed(String _string);

    public void deletePost(int pos);

    public void pickPhotos(int pos);


}
