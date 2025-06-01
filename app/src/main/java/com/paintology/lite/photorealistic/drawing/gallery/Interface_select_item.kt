package com.paintology.lite.photorealistic.drawing.gallery

import android.view.View

interface Interface_select_item {
    fun selectItem(pos: Int, isFromRelatedPost: Boolean)
    fun openTutorialDetail(cat_id: String?, tut_id: String?, pos: Int)
    fun onSubMenuClick(view: View?, item: model_DownloadedTutorial?, position: Int)
    fun onMovieIconClick(view: View?, item: model_DownloadedTutorial?, position: Int)
    fun onEditClick(view: View?, item: model_DownloadedTutorial?, position: Int)
    fun onDeleteClick(view: View?, item: model_DownloadedTutorial?, position: Int)
    fun onShareClick(view: View?, item: model_DownloadedTutorial?, position: Int)
    fun onPostClick(view: View?, item: model_DownloadedTutorial?, position: Int)
}