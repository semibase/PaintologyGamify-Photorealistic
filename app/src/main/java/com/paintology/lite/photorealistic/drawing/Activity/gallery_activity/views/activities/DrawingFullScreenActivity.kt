package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityDrawingFullScreenBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import com.paintology.lite.photorealistic.drawing.util.showEnlargeImage
import java.io.File
import java.io.FileOutputStream


class DrawingFullScreenActivity : BaseActivity() {

    private val binding by lazy {
        ActivityDrawingFullScreenBinding.inflate(layoutInflater)
    }
    private var progressDialog: ProgressDialog? = null

    private var getModel: NewDrawing? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getIntentData()

        initListener()

        initToolbar()

        initProgressBar()

    }

    private fun initProgressBar() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle(resources.getString(R.string.please_wait))
        progressDialog?.setMessage(getString(R.string.load_tut))
        progressDialog?.setCanceledOnTouchOutside(false)
    }

    @SuppressLint("SetTextI18n")
    private fun initListener() {
        if (!getModel?.type.equals("freehand")) {
            if (getModel?.metadata?.tutorialId.toString() == "") {
                binding.imgArrow.text = resources.getString(R.string.do_tutorial)
            }else{
                binding.imgArrow.text = resources.getString(R.string.do_tutorial)+" - #"+getModel?.metadata?.tutorialId.toString()
            }
        }else{
            binding.imgArrow.text = resources.getString(R.string.learn_drawing)
        }


        binding.imgArrow.onSingleClick {
            if (!getModel?.type.equals("freehand")) {
                if (getModel?.metadata?.tutorialId?.isEmpty() == true) {
                    Toast.makeText(
                        this@DrawingFullScreenActivity,
                        getString(R.string.no_tut),
                        Toast.LENGTH_SHORT
                    ).show()
                    openActivity(CategoryActivity::class.java)
                } else {
                    FireUtils.showProgressDialog(
                        this@DrawingFullScreenActivity,
                        getString(R.string.please_wait)
                    )
                    TutorialUtils(this@DrawingFullScreenActivity).parseTutorial(getModel?.metadata?.tutorialId.toString())
                }
            } else {
                openActivity(CategoryActivity::class.java)
            }

        }
        binding.imageFullScreen.onSingleClick {
            getModel?.images?.content?.let { showEnlargeImage(this, it) }
        }
    }


    private fun getIntentData() {
        val intent = intent
        getModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("drawing_model", NewDrawing::class.java)
        } else {
            intent.getParcelableExtra("drawing_model")
        }

        Glide.with(this)
            .load(getModel?.images?.content)
            .placeholder(R.drawable.img_cat_dummy)
            .error(R.drawable.img_cat_dummy)
            .into(binding.imageFullScreen)

        binding.apply {
            tvDescriptionContent.text = getModel?.description
            tvLikes.text = getModel?.statistic?.likes.toString()
            tvComments.text = getModel?.statistic?.comments.toString()
            tvViewCounts.text = getModel?.statistic?.views.toString()
            tvRateUsCount.text = getModel?.statistic?.ratings.toString()

            imgShare.onSingleClick {
                val model = getModel ?: return@onSingleClick
                val bundle = Bundle()
                bundle.putString("post_id", model.id)
                bundle.putString(
                    "post_type", model.type
                )
                bundle.putString("user_id", model.author.userId)
                sendUserEventWithParam(StringConstants.gallery_post_share, bundle)
                loadImageAndSave()
            }
        }
    }

    private fun loadImageAndSave() {
        val imageUrl = getModel?.images?.content
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .apply(
                RequestOptions().placeholder(R.drawable.img_cat_dummy).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Save the bitmap to a file
                    val file = File(applicationContext.cacheDir, "shared_image.png")
                    val outStream = FileOutputStream(file)
                    resource.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                    outStream.close()
                    // Share the image
                    shareImage(file)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle load cleared
                }
            })
    }

    fun shareImage(file: File) {

        FirebaseFirestoreApi.shareCountDrawing(getModel?.id.toString())
            .addOnCompleteListener { task ->

            }

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            file
        )


        var message = ""
        var url = getModel?.links?.youtube ?: ""
        var name = getModel?.author?.name ?: ""

        if (url.isNotEmpty() && !url.endsWith("null")) {
            message += "Watch video : " + "\n"
            message += url + "\n\n"
        }

        message += "Check out this Gallery drawing by user $name on the Paintology app.\n" +
                "App:\n" +
                "https://play.google.com/store/apps/details?id=com.paintology.lite\n"


        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Adding subject and body for Gmail
            putExtra(Intent.EXTRA_SUBJECT, "Gallery image from Paintology")
            putExtra(
                Intent.EXTRA_TEXT,
                message
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Share image using"))
    }


    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        binding.toolbar.apply {
            imgMenu.onSingleClick {
                finish()
            }
            imgFav.hide()
            tvTitle.text = "${getModel?.author?.name} Gallery"
        }
    }


}