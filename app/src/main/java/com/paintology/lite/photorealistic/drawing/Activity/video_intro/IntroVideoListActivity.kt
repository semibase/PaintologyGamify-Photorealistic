package com.paintology.lite.photorealistic.drawing.Activity.video_intro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityIntroVideoListBinding
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam

class IntroVideoListActivity : BaseActivity() {

    private var isLoading = false
    private var introList = mutableListOf<Any?>()
    private var isLastPage = false
    private var introAdapter: IntroAdapter? = null
    var pageNo = 1;
    private val binding by lazy {
        ActivityIntroVideoListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        setData()
        fetchVideos()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val titleTextView = TextView(this)
        titleTextView.text = getString(R.string.video_guides)
        titleTextView.textSize = 20f
        titleTextView.setTypeface(null, Typeface.BOLD)
        titleTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        titleTextView.gravity = Gravity.CENTER
        val layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        binding.toolbar.addView(titleTextView, layoutParams)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchVideos() {
        isLoading = true
        FirebaseFirestoreApi.fetchIntroVideos(pageNo, "", "")
            .addOnCompleteListener {
                isLoading = false
                if (it.isSuccessful) {
                    val data = it.getResult().getData() as HashMap<*, *>
                    val mlist = data.get("data") as List<*>
                    if (mlist.isNotEmpty()) {
                        for (tlist in mlist) {
                            val item = tlist as HashMap<*, *>
                            introList.add(item)
                        }
                        introAdapter?.notifyDataSetChanged()
                    } else {
                        isLastPage = true
                    }
                } else {
                    Log.e("TAG", it.exception.toString())
                }
            }
    }

    private fun setData() {
        introAdapter = IntroAdapter(this, introList) { position, item ->
            val data = item as HashMap<*, *>
            if (data.contains("id")) {
                val bundle = Bundle()
                bundle.putString("guide_id", data["id"].toString())
                bundle.putString("category", data["category"].toString())
                bundle.putString("title", data["title"].toString())
                sendUserEventWithParam(
                    StringConstants.video_guides_open, bundle
                )
                startActivity(
                    Intent(
                        this@IntroVideoListActivity,
                        IntroVideoActivity::class.java
                    ).putExtra("video_id", data["id"].toString())
                )
            }
        }
        binding.rvIntroList.apply {
            layoutManager = LinearLayoutManager(this@IntroVideoListActivity)
            adapter = introAdapter
        }
        binding.rvIntroList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition =
                    layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        pageNo++
                        fetchVideos()
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true;
        }
        return super.onOptionsItemSelected(item)
    }
}