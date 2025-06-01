package com.paintology.lite.photorealistic.drawing.search

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.paintology.lite.photorealistic.drawing.Enums.SearchResultType
import com.paintology.lite.photorealistic.drawing.Model.Tutorial
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.interfaces.SearchItemClickListener

class TutorialAdapter(
    val context: Context,
    val list: List<Tutorial>,
    objInterface: SearchItemClickListener,
    type: SearchResultType
) :
    RecyclerView.Adapter<TutorialAdapter.MyViewHolder>() {

    var tutorialList: List<Tutorial>? = null

    private var objInterface: SearchItemClickListener
    private var type: SearchResultType

    init {
        this.tutorialList = list
        this.objInterface = objInterface
        this.type = type
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.new_subcategory_item, viewGroup, false)
        return MyViewHolder(itemView, objInterface, type)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val tutorial = tutorialList!![i]
        if (tutorial != null) {

            if (!TextUtils.isEmpty(tutorial.postTitle)) {
                myViewHolder.tvCategory.text = tutorial.postTitle
            }

            if (!TextUtils.isEmpty(tutorial.postContentText)) {
                myViewHolder.tvTutorialDesc.text = HtmlCompat.fromHtml(
                    tutorial.postContentText!!,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }

            val ytLink = tutorial.youtubeLink
            if (!TextUtils.isEmpty(ytLink.toString())) {
                myViewHolder.image.visibility = View.VISIBLE
            } else {
                myViewHolder.image.visibility = View.GONE
            }

            if (tutorial.resizeImage != null && !tutorial.resizeImage.equals(
                    "false",
                    ignoreCase = true
                )
            ) {
                val imageLink = tutorial.resizeImage
                Log.d("imagesLinks", "onBindViewHolder: $imageLink")
                val options: RequestOptions =
                    RequestOptions() //                        .centerCrop()
                        .fitCenter()
                        .placeholder(R.drawable.thumbnaildefault)
                        .error(R.drawable.thumbnaildefault)
                Glide.with(context).load(imageLink).apply(options).into(myViewHolder.thumbnail)
            }
        }
    }

    override fun getItemCount(): Int {
        return tutorialList!!.size
    }

    class MyViewHolder(view: View, objInterface: SearchItemClickListener, type: SearchResultType) :
        RecyclerView.ViewHolder(view) {
        var thumbnail: ImageView = view.findViewById<View>(R.id.iv_tutorial_category) as ImageView
        var image: ImageView = view.findViewById<View>(R.id.iv_yt) as ImageView
        var tvCategory: TextView = view.findViewById<View>(R.id.tv_category) as TextView
        var tvTutorialDesc: TextView = view.findViewById<View>(R.id.tv_category_desc) as TextView

        init {
            itemView.setOnClickListener {
                objInterface.selectItem(adapterPosition, type)
            }
        }
    }
}

//class TutorialAdapter(
//    val context: Context,
//    val list: MutableList<PostDetailModel>,
//    objInterface: SearchItemClickListener,
//    type: SearchResultType
//) :
//    RecyclerView.Adapter<TutorialAdapter.MyViewHolder>() {
//
//    var tutorialList: MutableList<PostDetailModel>? = null
//
//    private var objInterface: SearchItemClickListener
//    private var type: SearchResultType
//
//    init {
//        this.tutorialList = list
//        this.objInterface = objInterface
//        this.type = type
//    }
//
//    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
//        val itemView: View = LayoutInflater.from(viewGroup.context)
//            .inflate(R.layout.subcategory_item, viewGroup, false)
//        return MyViewHolder(itemView, objInterface, type)
//    }
//
//    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
//        val tutorial = tutorialList!![i]
//        if (tutorial != null) {
//            if (!TextUtils.isEmpty(tutorial.post_title)) {
//                myViewHolder.tvTutorialName.text = tutorial.post_title
//            }
//
//            val ytLink = tutorial.youtube_link()
//            if (!TextUtils.isEmpty(ytLink.toString())) {
//                myViewHolder.image.visibility = View.VISIBLE
//            } else {
//                myViewHolder.image.visibility = View.GONE
//            }
//
//            if (tutorial.resizeImage != null && !tutorial.resizeImage.equals(
//                    "false",
//                    ignoreCase = true
//                )
//            ) {
//                val imageLink = tutorial.resizeImage
//                Log.d("imagesLinks", "onBindViewHolder: $imageLink")
//                val options: RequestOptions =
//                    RequestOptions() //                        .centerCrop()
//                        .fitCenter()
//                        .placeholder(R.drawable.thumbnaildefault)
//                        .error(R.drawable.thumbnaildefault)
//                Glide.with(context).load(imageLink).apply(options).into(myViewHolder.thumbnail)
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return tutorialList!!.size
//    }
//
//    class MyViewHolder(view: View, objInterface: SearchItemClickListener, type: SearchResultType) : RecyclerView.ViewHolder(view) {
//        var thumbnail: ImageView = view.findViewById<View>(R.id.iv_tutorial_category) as ImageView
//        var image: ImageView = view.findViewById<View>(R.id.iv_yt) as ImageView
//        var tvTutorialName: TextView = view.findViewById<View>(R.id.tv_category_name) as TextView
//        var frmDefault: FrameLayout = view.findViewById<View>(R.id.frm_default) as FrameLayout
//
//        init {
//            frmDefault.setOnClickListener {
//                objInterface.selectItem(adapterPosition, type)
//            }
//        }
//    }
//}