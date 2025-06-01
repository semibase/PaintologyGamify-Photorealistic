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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.paintology.lite.photorealistic.drawing.Enums.SearchResultType
import com.paintology.lite.photorealistic.drawing.Model.Blogpost
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.interfaces.SearchItemClickListener

class BlogPostAdapter internal constructor(
    val context: Context,
    private val tutorialList: List<Blogpost>,
    private val viewPager2: ViewPager2,
    private val objInterface: SearchItemClickListener,
    private val type: SearchResultType
) : RecyclerView.Adapter<BlogPostAdapter.SliderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_item, parent, false
            ),
            objInterface,
            type
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, i: Int) {
        val tutorial = tutorialList[i]
        if (tutorial != null) {

            if (!TextUtils.isEmpty(tutorial.postTitle)) {
                holder.tvCategory.text = tutorial.postTitle
            }

            if (!TextUtils.isEmpty(tutorial.postContentHTML)) {
                holder.tvTutorialDesc.text = HtmlCompat.fromHtml(
                    tutorial.postContentHTML!!,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }

//            val ytLink = tutorial.youtubeLink
//            if (!TextUtils.isEmpty(ytLink.toString())) {
//                holder.ytImage.visibility = View.VISIBLE
//            } else {
//                holder.ytImage.visibility = View.GONE
//            }
//
            if (tutorial.thumbUrl != null && !tutorial.thumbUrl.equals(
                    "false",
                    ignoreCase = true
                )
            ) {
                val imageLink = tutorial.thumbUrl
                Log.d("imagesLinks", "onBindViewHolder: $imageLink")
                val options: RequestOptions =
                    RequestOptions() //                        .centerCrop()
                        .fitCenter()
                        .placeholder(R.drawable.thumbnaildefault)
                        .error(R.drawable.thumbnaildefault)
                Glide.with(context).load(imageLink).apply(options).into(holder.thumbnail)
            }
        }
    }

//    private val runnable = Runnable {
//        tutorialList.addAll(tutorialList)
//        notifyDataSetChanged()
//    }

    override fun getItemCount(): Int {
        return tutorialList.size
    }

    inner class SliderViewHolder(
        itemView: View,
        objInterface: SearchItemClickListener,
        type: SearchResultType
    ) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.iv_tutorial_category)
        val ytImage: ImageView = itemView.findViewById(R.id.iv_yt)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        val tvTutorialDesc: TextView = itemView.findViewById(R.id.tv_category_desc)

        init {
            itemView.setOnClickListener {
                objInterface.selectItem(adapterPosition, type)
            }
        }
    }
}

//class BlogPostAdapter (
//    val context: Context,
//    val list: List<Blogpost>,
//    objInterface: SearchItemClickListener,
//    type: SearchResultType
//) :
//    RecyclerView.Adapter<BlogPostAdapter.MyViewHolder>() {
//
//    var tutorialList: List<Blogpost>? = null
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
//            if (!TextUtils.isEmpty(tutorial.postTitle)) {
//                myViewHolder.tvTutorialName.text = tutorial.postTitle
//            }
//
////            val ytLink = tutorial.youtubeLink
////            if (!TextUtils.isEmpty(ytLink)) {
////                myViewHolder.image.visibility = View.VISIBLE
////            } else {
////                myViewHolder.image.visibility = View.GONE
////            }
////
////            if (tutorial.resizeImage != null && !tutorial.resizeImage.equals(
////                    "false",
////                    ignoreCase = true
////                )
////            ) {
////                val imageLink = tutorial.resizeImage
////                Log.d("imagesLinks", "onBindViewHolder: $imageLink")
////                val options: RequestOptions =
////                    RequestOptions() //                        .centerCrop()
////                        .fitCenter()
////                        .placeholder(R.drawable.thumbnaildefault)
////                        .error(R.drawable.thumbnaildefault)
////                Glide.with(context).load(imageLink).apply(options).into(myViewHolder.thumbnail)
////            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return tutorialList!!.size
//    }
//
//    class MyViewHolder(view: View, objInterface: SearchItemClickListener, type: SearchResultType) : RecyclerView.ViewHolder(view) {
//        var thumbnail: ImageView = view.findViewById<View>(R.id.iv_tutorial_category) as ImageView
////        var image: ImageView = view.findViewById<View>(R.id.iv_yt) as ImageView
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