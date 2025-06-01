package com.paintology.lite.photorealistic.drawing.Adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Model.AwardDataModel
import com.paintology.lite.photorealistic.drawing.databinding.ItemAwardViewBinding


class AwardAdapter(var mContext: Context, mData: List<AwardDataModel>) :
    RecyclerView.Adapter<AwardAdapter.ViewHolder>() {
    var mDataList: List<AwardDataModel>
    private var mListner: OnItemClickListner? = null


    interface OnItemClickListner {
        fun onItemClick(position: Int)
        fun menudialog(position: Int)
    }

    fun setOnItemClickListner(listner: OnItemClickListner?) {
        mListner = listner
    }

    init {
        mDataList = mData
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemAwardViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //bind data heare

        holder.setData(mDataList[position])

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class ViewHolder(var binding: ItemAwardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(award: AwardDataModel) {
            binding.titleAward.text = award.title
            binding.descAward.text = award.desc


        }

    }
}