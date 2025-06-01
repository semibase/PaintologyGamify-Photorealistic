package com.core.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.viewbinding.ViewBinding


abstract class BaseDialog<VB : ViewBinding> constructor(context: Context) : Dialog(context) {
    /**
     * @getBinding() abstract method will be responsible for
     * generating the binding for UI classes
     **/
    abstract fun getBinding(): VB
    /**
     * @views variable will return the
     * real instance for accessing the UI components
     **/
    lateinit var views: VB
    /**
     * @isDialogCancelable() to check if dialog is cancelable or not
     **/
    abstract fun isDialogCancelable(): Boolean

    /**
     * @isCanceledOnTouchOutside() to check if dialog can ce canceled of touched from out side
     **/
    abstract fun isCanceledOnTouchOutside(): Boolean

    abstract fun setDialogBTransparency() : Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (setDialogBTransparency()){
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        views = getBinding()
        setContentView(views.root)
        setCancelable(isDialogCancelable())
        setCanceledOnTouchOutside(isCanceledOnTouchOutside())
    }



}