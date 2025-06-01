package com.paintology.lite.photorealistic.drawing.Activity.profile.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.paintology.lite.photorealistic.drawing.databinding.DialogChatLoadingBinding

var dialog: Dialog? = null

fun Activity.dialogChatLoading(message: String) {
    try {
        dialog = Dialog(this)
        dialog?.let { dialog ->
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            val binding = DialogChatLoadingBinding.inflate(layoutInflater)
            dialog.setContentView(binding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.DescriptionTxt.text = message
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

            try {
                dialog.show()
            } catch (_: Exception) {
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun dismissChatDialog() {
    dialog?.dismiss()
}