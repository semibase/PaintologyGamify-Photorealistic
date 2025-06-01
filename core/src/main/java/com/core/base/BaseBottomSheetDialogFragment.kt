package com.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: VB? = null

    protected val views: VB
        get() = _binding!!

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getBinding(inflater, container)
        return views.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}