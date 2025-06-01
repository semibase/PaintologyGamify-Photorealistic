package com.paintology.lite.photorealistic.drawing

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.hideKeyboard
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityVerifyOtpBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.showToast

class VerifyOTPActivity : BaseActivity() {

    private var arrayList: MutableList<EditText> = arrayListOf()
    var email = "";
    lateinit var countDownTimer: CountDownTimer
    private val binding by lazy {
        ActivityVerifyOtpBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getIntentData()
        initListeners()
        resendCode()

    }

    fun startTimer() {
        countDownTimer = object : CountDownTimer(60000 * 10, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                runOnUiThread {
                    binding.llResendCode.visibility = View.VISIBLE
                }
            }
        }
        countDownTimer.start()
    }

    private fun setOtpEditTextHandler() {
        for (count in 0..5) {
            val iVal = count
            arrayList[iVal].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    if (iVal == 5 && arrayList[iVal].text.toString().isNotEmpty()) {
                        arrayList[iVal].clearFocus()
                    } else if (arrayList[iVal].text.toString().isNotEmpty()) {
                        arrayList[iVal + 1].requestFocus()
                    }
                }
            })
            arrayList[iVal].setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
                if (event.action != KeyEvent.ACTION_DOWN) {
                    return@setOnKeyListener false
                }
                if (keyCode == KeyEvent.KEYCODE_DEL &&
                    arrayList[iVal].text.toString().isEmpty() && iVal != 0
                ) {
                    arrayList[iVal - 1].setText("")
                    arrayList[iVal - 1].requestFocus()
                }
                false
            }
        }
    }

    private fun getIntentData() {
        email = intent.getStringExtra("email") ?: ""
        binding.txtEmail.text = email
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
               // onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.forgot_password)
            imgFav.hide()
        }
    }

    fun cancalTimer() {
        try {
            countDownTimer.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancalTimer()

    }
    fun resendCode()
    {
        binding.llResendCode.visibility = View.GONE
        FireUtils.showProgressDialog(this@VerifyOTPActivity,getString(R.string.opt_sent))
        FirebaseFirestoreApi.sendOTP(email)
            .addOnCompleteListener { task ->
                FireUtils.hideProgressDialog()
                startTimer()
                if (task.isSuccessful) {
                    showToast(
                        this@VerifyOTPActivity,
                        getString(R.string.opt_sent_ss)
                    )
                } else {
                    if (task.exception != null) {
                        Log.e("TAGRR", task.getException().toString())
                        FireUtils.showCustomDialog(
                            this@VerifyOTPActivity,
                            getString(R.string.app_name),
                            task.exception!!.message.toString()
                        ) {
                        }
                    }
                }
            }
    }

    private fun initListeners() {

        arrayList.add(binding.editOtp1)
        arrayList.add(binding.editOtp2)
        arrayList.add(binding.editOtp3)
        arrayList.add(binding.editOtp4)
        arrayList.add(binding.editOtp5)
        arrayList.add(binding.editOtp6)
        setOtpEditTextHandler()

        binding.apply {
            btnResentCode.onSingleClick {
                resendCode()
            }

            btnVerifyOtp.onSingleClick {
                hideKeyboard(binding.btnVerifyOtp)
                val otp: String = binding.editOtp1.getText()
                    .toString() + binding.editOtp2.getText()
                    .toString() + binding.editOtp3.getText()
                    .toString() + binding.editOtp4.getText()
                    .toString() + binding.editOtp5.getText()
                    .toString() + binding.editOtp6.getText().toString()
                if (otp.isNotEmpty() && otp.length != 4) {
                    verifyOTP(otp)
                } else {
                    Toast.makeText(this@VerifyOTPActivity, getString(R.string.valid_opt), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun verifyOTP(otp: String) {
        FireUtils.showProgressDialog(this@VerifyOTPActivity,getString(R.string.verify_opt))
        FirebaseFirestoreApi.verifyOTP(email, otp)
            .addOnCompleteListener { task ->
                FireUtils.hideProgressDialog()
                if (task.isSuccessful) {

                    val result = task.result.data as HashMap<*, *>
                    if (result.containsKey("custom_token") && result["custom_token"].toString().isNotEmpty()) {
                        startActivity(
                            Intent(
                                this@VerifyOTPActivity,
                                SetPasswordActivity::class.java
                            ).putExtra("token", result["custom_token"].toString())
                        )
                        finish()
                    } else {
                        showToast(this@VerifyOTPActivity, getString(R.string.unknown_error))
                    }
                } else {
                    if (task.exception != null) {
                        Log.e("TAGRR", task.getException().toString())
                        FireUtils.showCustomDialog(
                            this@VerifyOTPActivity,
                            getString(R.string.app_name),
                            task.exception!!.message.toString()
                        ) {
                        }
                    }
                }
            }
    }

}