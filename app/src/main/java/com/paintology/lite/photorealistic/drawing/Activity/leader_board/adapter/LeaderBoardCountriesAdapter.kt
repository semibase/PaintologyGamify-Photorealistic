package com.paintology.lite.photorealistic.drawing.Activity.leader_board.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.data.local.data_store.TopLevel
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.CountriesModel
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutCountriesItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class LeaderBoardCountriesAdapter(
    private val leaderBoardCountriesList: MutableList<CountriesModel> = mutableListOf(),
    private val onCountryClick: OnCountryClick,
    private val topLevel: TopLevel,
    context: Context,
    private val isFromLeaderActivity: Boolean,
    private val countryDrawingsCount: Map<String, Int>? = null

) : RecyclerView.Adapter<LeaderBoardCountriesAdapter.ViewHolder>() {

    var selectedPosition = 0

    init {
        // Read the position from the data store and update selectedPosition
        CoroutineScope(Dispatchers.Main).launch {
            topLevel.onCreate().readLangName(context).collectLatest { country ->
                selectedPosition =
                    leaderBoardCountriesList.indexOfLast {
                        country == it.countryName
                    }
                notifyDataSetChanged()
            }
            /*topLevel.onCreate().readPosition(context).collectLatest {
                selectedPosition = it
                notifyDataSetChanged()
            }*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutCountriesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = leaderBoardCountriesList[position]
        with(holder.binding) {

            item.flag?.let { imgCountry.setImageResource(it) }

            tvCountryName.text = item.countryName
            tvCountryContent.text = item.countryContent

            if (isFromLeaderActivity) {
                if (item.users != null && item.users > 0) {
                    if (item.users == 1L) {
                        tvTotalDrawings.text = "${item.users} User"
                    } else {
                        tvTotalDrawings.text = "${item.users} Users"
                    }
                } else {
                    tvTotalDrawings.text = ""
                }
            } else {

                Log.e("country count", countryDrawingsCount.toString())

                countryDrawingsCount?.let {
                    val count =
                        it.getOrDefault(item.countryCode, 0) + it.getOrDefault(item.countryName, 0)
                    tvTotalDrawings.text = "${count} Drawings"
                    if (count > 0) {
                        tvTotalDrawings.show()
                    } else {
                        tvTotalDrawings.hide()
                    }
                }
                if (position == 0) {
                    val total = countryDrawingsCount?.values?.sumOf { it }
                    tvTotalDrawings.text = "$total Drawings"
                }
            }

            layoutMain.onSingleClick {
                if (isFromLeaderActivity) {
                    if (item.users == null || item.users <= 0) {
                        return@onSingleClick
                    }
                }
                updateSelectedItem(position)
                val countryName = if (countryDrawingsCount?.getOrDefault(
                        item.countryCode,
                        0
                    ) != 0
                ) item.countryCode else item.countryName
                Log.i("Drawing", countryName ?: "")
                onCountryClick.countryClick(item, item.flag, position)
                notifyDataSetChanged()
            }

            // Update UI based on selected position
            /*if (selectedPosition == position) {
                layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board)
                tvCountryName.setTextColor(Color.parseColor("#ffffff"))
                tvCountryContent.setTextColor(Color.parseColor("#ffffff"))
                val tintColor = ContextCompat.getColor(appCompatImageView9.context, R.color.white)
                appCompatImageView9.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                tvTotalDrawings.show()
            } else {
                layoutMain.setBackgroundResource(R.drawable.bg_challenge_level)
//                tvTotalDrawings.hide()
            }*/

            fun setNormalView() {
                tvCountryName.setTextColor(Color.BLACK)
                tvCountryContent.setTextColor(Color.BLACK)
                appCompatImageView9.clearColorFilter()
                layoutMain.setBackgroundResource(R.drawable.bg_challenge_level)
            }

            fun grayOutView() {
                tvCountryName.setTextColor(Color.WHITE)
                tvCountryContent.setTextColor(Color.WHITE)
                val tintColor =
                    ContextCompat.getColor(appCompatImageView9.context, R.color.white)
                appCompatImageView9.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                layoutMain.setBackgroundResource(R.drawable.bg_gray_leader_board)
            }

            when {
                selectedPosition == position -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board)
                    tvCountryName.setTextColor(Color.WHITE)
                    tvCountryContent.setTextColor(Color.WHITE)
                    val tintColor =
                        ContextCompat.getColor(appCompatImageView9.context, R.color.white)
                    appCompatImageView9.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                    tvTotalDrawings.show()
                }

                position == 0 -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_first_item)
                    tvCountryName.setTextColor(Color.BLACK)
                    tvCountryContent.setTextColor(Color.BLACK)
                    appCompatImageView9.clearColorFilter()
                    tvTotalDrawings.show()
                }

                else -> {
                    if (isFromLeaderActivity) {
                        if (item.users != null && item.users > 0) {
                            setNormalView()
                        } else {
                            grayOutView()
                        }
                    } else {
                        setNormalView()
                    }
                }
            }
        }
    }

    private fun updateSelectedItem(position: Int) {
        selectedPosition = position
    }

    override fun getItemCount(): Int {
        return leaderBoardCountriesList.size
    }

    inner class ViewHolder(val binding: LayoutCountriesItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnCountryClick {
        fun countryClick(model: CountriesModel, flag: Int?, position: Int)
    }
}
