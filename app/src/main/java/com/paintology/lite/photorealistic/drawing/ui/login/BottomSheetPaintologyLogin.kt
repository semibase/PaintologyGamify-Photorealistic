package com.paintology.lite.photorealistic.drawing.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.base.BaseBottomSheetDialogFragment
import com.core.ext.isPasswordValid
import com.core.ext.isValidEmail
import com.core.ext.setNoSpaceInputFilter
import com.core.ext.showLongToast
import com.paintology.lite.photorealistic.drawing.EmailActivity
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.BottomsheetPaintLogyLoginBinding

class BottomSheetPaintologyLogin(
    private val context: Context,
    private val isNameShow: Boolean,
    private val callback: (LoginRequestModel) -> Unit
) :

    BaseBottomSheetDialogFragment<BottomsheetPaintLogyLoginBinding>(),
    View.OnClickListener {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetPaintLogyLoginBinding {
        return BottomsheetPaintLogyLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableSpaceOfEditText()
        setUpClicks()
    }

    private fun disableSpaceOfEditText() {
        views.etUsername.setNoSpaceInputFilter()
        if (isNameShow) {
            views.etUsername.visibility = View.VISIBLE
            views.tvForgotPassword.visibility = View.INVISIBLE
            views.btnLogin.text = getString(R.string.signup)
        } else {
            views.etUsername.visibility = View.GONE
            views.tvForgotPassword.visibility = View.VISIBLE
            views.btnLogin.text = getString(R.string.login)
        }
    }

    private fun setUpClicks() {
        views.btnLogin.setOnClickListener(this@BottomSheetPaintologyLogin)
        if (!isNameShow) {
            views.tvForgotPassword.setOnClickListener(this@BottomSheetPaintologyLogin)
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            views.btnLogin -> {
                validateUser()
            }

            views.tvForgotPassword -> {
                validateEmail()
            }
        }
    }

    private fun validateEmail() {
        context.startActivity(
            Intent(
                context,
                EmailActivity::class.java
            )
        )
        dismiss()
    }

    private fun validateUser() {
        val username = views.etUsername.text.toString().trim()
        val email = views.etEmail.text.toString().trim()
        val password = views.etPassword.text.toString().trim()

        if (isNameShow) {
            if (username.isEmpty() || username.isBlank()) {
                activity?.showLongToast("Please enter name")
                return
            }
        }


        if (email.isEmpty() || !email.isValidEmail()) {
            activity?.showLongToast("Please enter valid email address")
            return
        }

        if (!password.isPasswordValid()) {
            activity?.showLongToast("Password must contain 6 characters")
            return
        }

        callback.invoke(LoginRequestModel("", username, email, password))
        dismiss()
    }
}