package com.paintology.lite.photorealistic.drawing.Activity.your_ranking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.Lists
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.LeaderBoardActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.adapter.YourRankingAdapter
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.model_class.UserFacetCount
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityYourRankingBinding
import com.paintology.lite.photorealistic.drawing.util.SpecingDecoration
import com.paintology.lite.photorealistic.drawing.util.StringConstants

class YourRankingActivity : BaseActivity(), YourRankingAdapter.OnCountryClick {

    var rank: String? = ""
    private val binding by lazy {
        ActivityYourRankingBinding.inflate(layoutInflater)
    }

    private var yourRankingAdapter: YourRankingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getIntentData()
        initToolbar()

        initRecyclerView()

        // Fetch user list with facets
        viewModel.fetchUserListWithFacet()

        // Observe the LiveData
        viewModel.facetCountsList.observe(this, Observer { facetCounts ->
            Log.e("Observer", "Triggered")
            facetCounts?.let { counts ->
                // Update Lists.rankingList with the actual counts
                updateRankingList(counts)
                // Notify the adapter about the data change
                yourRankingAdapter?.notifyDataSetChanged()
            }
        })
    }


    private fun getIntentData() {
        val intent = intent
        if (intent.hasExtra("rank")) {
            rank = intent.getStringExtra("rank")
        } else {
            rank = sharedPref.getString(StringConstants.user_level, StringConstants.beginner)
                .toString()
        }
    }

    private fun initRecyclerView() {
        // Bottom spacing of RecyclerView
        val space = resources.getDimensionPixelSize(R.dimen._80sdp)
        val spacingDecoration = SpecingDecoration(0, space)
        binding.rvRanking.addItemDecoration(spacingDecoration)

        // Set adapter
        yourRankingAdapter = YourRankingAdapter(Lists.rankingList, rank, this)
        binding.rvRanking.apply {
            layoutManager = LinearLayoutManager(this@YourRankingActivity)
            adapter = yourRankingAdapter
        }
        val position = Lists.rankingList.indexOfFirst {
            it.tvRankLevel.equals(rank)
        }
        if (position != -1) {
            binding.rvRanking.scrollToPosition(position)
        }
    }

    private fun updateRankingList(facetCounts: List<UserFacetCount>) {
        // Reset all counts to zero
        Lists.rankingList.forEach { it.tvTotalUsers = "0" }

        // Update counts with actual values from the response
        facetCounts.forEach { facetCount ->
            facetCount.counts.forEach { count ->
                Lists.rankingList.find { it.tvRankLevel == count.value }?.apply {
                    tvTotalUsers = count.count.toString()
                }
            }
        }
    }

    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.ss_climb_levels)
            imgFav.hide()
        }
    }

    override fun countryClick(model: YourRankingModel) {
        startActivity(
            Intent(this@YourRankingActivity, LeaderBoardActivity::class.java)
                .putExtra("tvRankLevel", model.tvRankLevel)
        )
    }


}