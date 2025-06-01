package com.paintology.lite.photorealistic.drawing

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivitySetPasswordBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.showToast

class SetPasswordActivity : BaseActivity() {

    var token = ""
    private lateinit var auth: FirebaseAuth

    private val binding by lazy {
        ActivitySetPasswordBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        getIntentData()
        initListeners()
    }

    private fun getIntentData() {
        token = intent.getStringExtra("token") ?: ""
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                // onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.reset_password)
            imgFav.hide()
        }
    }


    private fun initListeners() {
        binding.btnSend.onSingleClick {
            val pass = binding.edtPass.text.toString().trim()
            val pass2 = binding.edtConfirmPass.text.toString().trim()
            if (pass.isEmpty()) {
                binding.edtPass.error = getString(R.string.enter_pass)
            } else if (pass2.isEmpty()) {
                binding.edtConfirmPass.error = getString(R.string.enter_pass_new)
            } else if (pass.length < 6) {
                binding.edtPass.error = getString(R.string.enter_six_min)
            } else if (pass2.length < 6) {
                binding.edtConfirmPass.error = getString(R.string.enter_six_min)
            } else if (pass != pass2) {
                binding.edtConfirmPass.error = getString(R.string.pass_match)
            } else {

                if (token.isNotEmpty()) {
                    if (auth.currentUser != null) {
                        auth.signOut()
                    }
                    FireUtils.showProgressDialog(
                        this@SetPasswordActivity,
                        getString(R.string.setting_pass)
                    )
                    auth.signInWithCustomToken(token)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                resetPassword()
                            } else {
                                FireUtils.hideProgressDialog()
                                FireUtils.showCustomDialog(
                                    this@SetPasswordActivity,
                                    getString(R.string.app_name),
                                    getString(R.string.token_expire)
                                ) {
                                    finish()
                                }
                            }
                        }
                } else {
                    showToast(getString(R.string.unknown_error))
                }
            }
        }
    }

    fun resetPassword() {
        val pass = binding.edtPass.text.toString().trim()
        FirebaseFirestoreApi.resetPassword(pass)
            .addOnCompleteListener { task ->
                FireUtils.hideProgressDialog()
                if (task.isSuccessful) {
                    auth.signOut()
                    FireUtils.showCustomDialog(
                        this@SetPasswordActivity,
                        getString(R.string.app_name),
                        getString(R.string.reset_success)
                    ) {
                        finish()
                    }
                } else {
                    if (task.exception != null) {
                        showToast(
                            this@SetPasswordActivity,
                            task.exception?.message ?: ""
                        )
                        FireUtils.showCustomDialog(
                            this@SetPasswordActivity,
                            getString(R.string.app_name),
                            task.exception!!.message.toString()
                        ) {
                        }
                    }
                }
            }
    }
}