package com.paintology.lite.photorealistic.drawing.videoguide

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.VideoGuideItemsBinding
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils

class VideoGuideAdapter(
    val context: Context,
    private val tutorialList: List<GetCategoryPostModel.postData>,
    val listener: ItemClickListener
) : RecyclerView.Adapter<VideoGuideAdapter.MyViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class MyViewHolder(val binding: VideoGuideItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            VideoGuideItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    // bind the items with each item
    // of the list tutorialList
    // which than will be
    // shown in recycler view
    // to keep it simple we are
    // not setting any image data to view
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.binding.tvName.text = tutorialList[position].objdata.post_title


        with(holder) {
            with(tutorialList[position]) {
                binding.tvName.text = this.objdata.post_title

//                if (!TextUtils.isEmpty(this.objdata.youtube_link)) {
//                    binding.ivMovieIcon.visibility = View.VISIBLE
//                } else {
//                    binding.ivMovieIcon.visibility = View.GONE
//                }

                val imageLink = this.objdata.thumbImage

                val options: RequestOptions =
                    RequestOptions() //                        .centerCrop()
                        .fitCenter()
                        .placeholder(R.drawable.thumbnaildefault)
                        .error(R.drawable.thumbnaildefault)
                Glide.with(context).load(imageLink).apply(options).into(binding.ivThumb)

                binding.root.setOnClickListener {

                    var event = this.objdata.post_title.replace(" ", "_")
                    event = event.lowercase()

                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            context,
                            "vguide_$event",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    FirebaseUtils.logEvents(context, "vguide_$event")

                    listener.onItemClick(it, this, position)

                }

                binding.tvName.setOnClickListener {
                    listener.onSubMenuClick(it, this, position)
                }
                binding.ivMore.setOnClickListener {
                    listener.onSubMenuClick(it, this, position)
                }
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return tutorialList.size
    }

    interface ItemClickListener {
        fun onItemClick(view: View, item: GetCategoryPostModel.postData, position: Int)
        fun onSubMenuClick(view: View, item: GetCategoryPostModel.postData, position: Int)
    }
}