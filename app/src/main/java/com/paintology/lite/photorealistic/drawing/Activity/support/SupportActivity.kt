package com.paintology.lite.photorealistic.drawing.Activity.support

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserProfile
import com.paintology.lite.photorealistic.drawing.Activity.profile.MyProfileActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivitySupportBinding
import com.paintology.lite.photorealistic.drawing.databinding.ImagePickDialogBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.LoadingDialog
import com.paintology.lite.photorealistic.drawing.util.PermissionUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.parseUserProfile
import kotlinx.coroutines.FlowPreview
import java.io.ByteArrayOutputStream
import java.io.File

class SupportActivity : BaseActivity(), ImagesAdapter.OnItemClick,
    HandlePathOzListener.SingleUri {


    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var loadingDialog: LoadingDialog? = null

    private lateinit var handlePathOz: HandlePathOz

    private lateinit var adapter: ImagesAdapter
    var uploadedList: MutableList<String> = arrayListOf()
    var imageList: MutableList<String> = mutableListOf()
    var bitmapList: MutableList<Bitmap?> = mutableListOf()
    var countrys: MutableList<String> = mutableListOf()
    var issues: MutableList<String> = mutableListOf()
    var countryCodeNameList = HashMap<String, String>()
    var constants = StringConstants()

    private val totImage = 3
    private var curImage = 0;
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null

    private val binding by lazy {
        ActivitySupportBinding.inflate(layoutInflater)
    }


    private fun addData() {
        issues.add("Bug")
        issues.add("Idea")
        issues.add("Question")
        issues.add("Other")
        val issueAdapter = ArrayAdapter(
            this@SupportActivity,
            R.layout.view_my_spinner_item,
            issues
        )
        issueAdapter.setDropDownViewResource(R.layout.view_my_spinner_drop_down)
        binding.spinnerIssue.setAdapter(issueAdapter)
    }

    fun setAdapter() {
        val manager = LinearLayoutManager(this, HORIZONTAL, false)
        binding.rvScreenShots.layoutManager = manager
        adapter = ImagesAdapter(this, imageList, bitmapList, this)
        binding.rvScreenShots.adapter = adapter
        showPlus()
    }

    override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        imageList.add(pathOz.path)
        adapter.notifyItemInserted(adapter.itemCount + 1)
        showPlus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        handlePathOz = HandlePathOz(this, this)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        loadingDialog = LoadingDialog(this)
        initToolbar()
        addData()
        setAdapter()
        fetchPrefsData()
    }


    override fun onDelete(position: Int) {
        imageList.removeAt(position)
        bitmapList.removeAt(position)
        adapter.notifyItemRemoved(position)
        showPlus()
    }

    override fun onAdd(position: Int) {
        addPicture()
    }


    private fun addPicture() {
        if (PermissionUtils.checkImageReadPermission(this@SupportActivity)) {
            selectImage()
        } else {
            PermissionUtils.requestStoragePermission(this@SupportActivity, 2)
        }
    }

    private fun showPlus() {
        if (imageList.size <= totImage) {
            if (imageList.size == 0 || imageList[0] != "") {
                imageList.add(0, "")
                bitmapList.add(0, null)
                adapter.notifyItemInserted(0)
            }
        }
        hidePlus()
    }

    private fun hidePlus() {
        if (imageList.size > totImage) {
            imageList.removeAt(0)
            bitmapList.removeAt(0)
            adapter.notifyItemRemoved(0)
        }
    }

    @OptIn(FlowPreview::class)
    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.app_support)
            imgFav.hide()
        }

        binding.btnSubmit.onSingleClick {


//            FireUtils.showProgressDialog(this, "Sending Please Wait")

            loadingDialog?.ShowPleaseWaitDialog(getString(R.string.send_pls_wait))

            curImage = 0;
            updateFeedBack(imageList[curImage])
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val selectedImage: Uri? = data?.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        this@SupportActivity.contentResolver,
                        selectedImage
                    )
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    Log.e("Activity", "Pick from Gallery::>>> ")

                    if (selectedImage != null) {
                        bitmapList.add(bitmap)
                        handlePathOz.getRealPath(selectedImage)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun fetchPrefsData() {
        FireUtils.showProgressDialog(this, getString(R.string.ss_loading_please_wait))
        FirebaseFirestoreApi.fetchProfilePrefsData()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.data as HashMap<*, *>
                    try {
                        val array_cons = data.get("countries")
                                as List<Map<String, HashMap<*, *>>>
                        array_cons.forEach {
                            countrys.add(it.get("name").toString())
                            countryCodeNameList.put(
                                it.get("code").toString(),
                                it.get("name").toString()
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    fetchProfileData()
                } else {
                    fetchProfileData()
                }
            }
    }

    fun fetchProfileData() {
        FirebaseFirestoreApi.userProfileFunction(constants.getString(constants.UserId, this))
            .addOnSuccessListener { result ->
                val data = result.data as Map<String, Any>
                setProfileData(parseUserProfile(data))
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error calling function", e)
                hideCountry()
                hideName()
            }
    }

    private fun hideCountry() {
        binding.tvUserCountry.visibility = View.GONE
        if (countryCodeNameList.size > 0) {
            val countryAdapter = ArrayAdapter(
                this@SupportActivity,
                android.R.layout.simple_spinner_item,
                countrys
            )
            countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCountry.setAdapter(countryAdapter)
            binding.spinnerCountry.visibility = View.VISIBLE
        } else {
            binding.edtUserCountry.visibility = View.VISIBLE
        }
    }

    private fun hideName() {
        binding.tvUserName.visibility = View.GONE
        binding.edtUserName.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun setProfileData(userProfile: UserProfile) {
        try {
            binding.tvUserName.text = userProfile.name
            if (userProfile.country == "") {
                hideCountry()
            } else if (countryCodeNameList.size > 0 && countryCodeNameList.containsKey(userProfile.country)) {
                binding.tvUserCountry.text = countryCodeNameList[userProfile.country]
            } else {
                hideCountry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        FireUtils.hideProgressDialog()
    }


    private fun selectImage() {
        val dialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding: ImagePickDialogBinding =
            ImagePickDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            llCamera.visibility = View.GONE
            llGallery.setOnClickListener { view ->
                dialog.dismiss()
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                galleryLauncher?.launch(pickPhoto)
            }
        }

        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (BuildConfig.DEBUG) {
                showToast(constants.allow_storage_permission)
            }
            FirebaseUtils.logEvents(this, constants.allow_storage_permission)
            if (PermissionUtils.checkImageReadPermission(this)) {
                selectImage()
            } else {
                PermissionUtils.requestStoragePermission(this, 2)
            }
        } else {
            try {
                val permission = ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    showToast(resources.getString(R.string.storage_permission_msg))
                    if (BuildConfig.DEBUG) {
                        showToast(constants.deny_storage_permission)
                    }
                } else {
                    if (PermissionUtils.checkImageReadPermission(this)) {
                        selectImage()
                    } else {
                        PermissionUtils.requestStoragePermission(this, 2)
                    }
                }
            } catch (e: Exception) {
                Log.e(MyProfileActivity::class.java.name, e.message!!)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sendIssue() {
        if (binding.edtTitle.text.isNullOrEmpty())
            binding.edtTitle.error = getString(R.string.enter_titl)
        else if (binding.edtDescription.text.isNullOrEmpty())
            binding.edtDescription.error = getString(R.string.enter_det)
        else {
            FirebaseFirestoreApi.sendIssue(
                binding.edtTitle.text.toString(),
                binding.edtDescription.text.toString(),
                binding.spinnerIssue.selectedItem.toString(),
                uploadedList
            ).addOnCompleteListener {
                loadingDialog?.DismissDialog()
//                FireUtils.hideProgressDialog()
                if (it.isSuccessful) {
                    Log.e("TAGRR", it.result.data.toString());
                    showToast(getString(R.string.feed_ss))
                    binding.edtTitle.setText("")
                    binding.edtDescription.setText("")
                    imageList.clear()
                    bitmapList.clear()
                    uploadedList.clear()
                    adapter.notifyDataSetChanged()
                    showPlus()
                } else {
                    Log.e("TAGRR", it.exception?.message.toString());
                    showToast(resources.getString(R.string.unknown_error))
                }
            }
        }
    }

    private fun changeImage() {
        curImage++;
        if (curImage < imageList.size) {
            updateFeedBack(imageList[curImage])
        } else {
            sendIssue()
        }
    }

    private fun updateFeedBack(imageUri: String) {
        if (imageUri == "") {
            changeImage()
        } else {
            var file: File? = File("")
            file = File(imageList[curImage])
            val name = System.currentTimeMillis().toString() + ".png";
            val ref = storageReference?.child(
                "users/" + constants.getString(constants.UserId, this) + "/feedbacks/$name"
            )
            ref!!.putFile(Uri.fromFile(file))
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener {
                            uploadedList.add(it.toString())
                            changeImage()
                        }.addOnFailureListener {
                            changeImage()
                        }
                    } else {
                        changeImage()
                    }
                }
        }

    }
}