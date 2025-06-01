package com.paintology.lite.photorealistic.drawing.Activity.notifications.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.paintology.lite.photorealistic.drawing.databinding.DialogDiscardBinding

fun Activity.dialogDiscard() {
    try {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val binding = DialogDiscardBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.cancel.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }

        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        try {
            dialog.show()
        } catch (_: Exception) {
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}