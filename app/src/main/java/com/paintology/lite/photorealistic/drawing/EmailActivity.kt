package com.paintology.lite.photorealistic.drawing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.core.ext.isValidEmail
import com.core.ext.showLongToast
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.databinding.ActivityEmailBinding

class EmailActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEmailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(binding.root)
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
               // onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.forgot_password)
            imgFav.hide()
        }
        binding.btnVerifyOtp.onSingleClick {
            validateEmail()
        }
    }

    private fun validateEmail() {
        val email = binding.edtEmail.text.toString().trim()

        if (email.isEmpty() || !email.isValidEmail()) {
            showLongToast(getString(R.string.valid_email))
            return
        }

       startActivity(
            Intent(
                this,
                VerifyOTPActivity::class.java
            ).putExtra("email",email)
        )
    }
}