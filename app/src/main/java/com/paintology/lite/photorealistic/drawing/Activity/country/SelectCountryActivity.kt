package com.paintology.lite.photorealistic.drawing.Activity.country

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.paintology.lite.photorealistic.drawing.Activity.country.adapter.CountryAdapter
import com.paintology.lite.photorealistic.drawing.Activity.country.adapter.CountryModel
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivitySelectCountryBinding
import com.paintology.lite.photorealistic.drawing.databinding.DialogProfileSavedBinding
import com.paintology.lite.photorealistic.drawing.databinding.DialogShoPostBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants


class SelectCountryActivity : AppCompatActivity() {

    private lateinit var adapter: CountryAdapter
    private lateinit var countryModel: CountryModel
    private val binding by lazy {
        ActivitySelectCountryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setAdapter()
    }

    fun setAdapter() {
        binding.btnDone.onSingleClick {
            checkCountry()
        }
        binding.ivBack.onSingleClick {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.rvCountry.layoutManager = LinearLayoutManager(this)
        adapter = CountryAdapter(this, arrayListOf(), object : CountryAdapter.CountryClickListener {
            override fun onClick(country: CountryModel, position: Int) {
                countryModel = country
                binding.btnDone.visibility = View.VISIBLE
            }
        })
        binding.rvCountry.adapter = adapter
        fetchFromFirebase()
    }

    private fun fetchFromFirebase() {
        binding.includedError.progressBar.visibility = View.VISIBLE
        FirebaseFirestoreApi.fetchProfilePrefsData()
            .addOnCompleteListener {
                binding.includedError.progressBar.visibility = View.GONE
                if (it.isSuccessful) {
                    try {
                        val data = it.result.data as HashMap<*, *>
                        val models = mutableListOf<CountryModel>()
                        val arraycons = data.get("countries")
                                as List<Map<String, HashMap<*, *>>>
                        arraycons.forEach {
                            models.add(
                                CountryModel(
                                    it["code"].toString(),
                                    it["name"].toString()
                                )
                            )
                        }
                        if (models.size > 0) {
                            adapter.refresh(models)
                            binding.textCountrySelect.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(
                                    charSequence: CharSequence,
                                    i: Int,
                                    i1: Int,
                                    i2: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    charSequence: CharSequence,
                                    i: Int,
                                    i1: Int,
                                    i2: Int
                                ) {
                                }

                                override fun afterTextChanged(editable: Editable) {
                                    adapter.filterList(editable.toString())
                                }
                            })
                        } else {
                            binding.includedError.txtError.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.includedError.txtError.visibility = View.VISIBLE
                    }
                } else {
                    binding.includedError.txtError.visibility = View.VISIBLE
                }
            }
    }

    private fun checkCountry() {

        if (countryModel == null)
            return

        val dialog = Dialog(this, R.style.my_dialog)
        val dialogBinding: DialogShoPostBinding = DialogShoPostBinding.inflate(
            layoutInflater
        )
        dialog.setContentView(dialogBinding.getRoot())

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        var title = getString(R.string.app_name)
        var des = getString(R.string.ss_link_country)+ "\n"
        des += countryModel.name + "\n\n"
        des += getString(R.string.ss_change_later) + "\n"
        des += getString(R.string.ss_be_sure)
        dialogBinding.tvMessage.text = des
        dialogBinding.tvDialogTitle.text = title

        dialogBinding.btnOk.text = getString(R.string.back)
        dialogBinding.btnSeePost.text = getString(R.string.ss_submit)

        dialogBinding.imgCross.onSingleClick {
            dialog.dismiss()
        }

        dialogBinding.btnSeePost.onSingleClick {
            dialog.setOnDismissListener {
                updateProfile()
            }
            dialog.dismiss()
        }

        dialogBinding.btnOk.onSingleClick {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateProfile() {
        FireUtils.showProgressDialog(
            this,
            getString(R.string.ss_updating_profile_data)
        )
        FirebaseFirestoreApi.updateCountry(
            countryModel.code
        ).addOnCompleteListener {
            FireUtils.hideProgressDialog()
            if (it.isSuccessful) {
                showToast("success")
                StringConstants.constants.putString(
                    StringConstants.constants.UserCountry,
                    countryModel.code,
                    this@SelectCountryActivity
                )
                showSuccessDialog(getString(R.string.profile_success), true)
            } else {
                Log.e("TAG", it.exception.toString())
                showToast("error")
                showSuccessDialog(getString(R.string.profile_error), false)
            }
        }
    }

    private fun showSuccessDialog(
        message: String,
        boolean: Boolean
    ) {
        try {
            val dialog = Dialog(this)
            val binding: DialogProfileSavedBinding =
                DialogProfileSavedBinding.inflate(LayoutInflater.from(this), null, false)
            dialog.setContentView(binding.getRoot())
            binding.tvDialogContent.text = message
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            dialog.show()
            binding.btnUnlock.setOnClickListener { v ->
                dialog.setOnDismissListener {
                    if (boolean) {
                        StringConstants.constants.putString(
                            StringConstants.constants.UserCountry,
                            countryModel.code,
                            this@SelectCountryActivity
                        )
                        finish()
                    }
                }
                dialog.dismiss()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}