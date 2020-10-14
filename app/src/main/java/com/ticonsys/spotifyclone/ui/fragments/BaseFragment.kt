package com.ticonsys.spotifyclone.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding>(
    @LayoutRes resId: Int
) : Fragment(resId) {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = initializeViewBinding(view)
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected abstract fun initializeViewBinding(view: View): VB

}