package com.genesys.sample

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding

typealias Inflater<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

abstract class BaseDialogFragment<VB>(
    private val inflate: Inflater<VB>
) : DialogFragment() where VB : ViewBinding {

    private var _viewBinding: VB? = null
    protected val viewBinding get() = _viewBinding!!

    abstract val tagName: String

    var isCreated: Boolean = false
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflate(inflater, container, false).also { _viewBinding = it }.root.let {
        if (it.parent != null) {
            (it.parent as ViewGroup).removeView(it)
        }
        it
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCreated = true

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            val windowParams: WindowManager.LayoutParams = attributes
            windowParams.dimAmount = dimAmount()
            windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            attributes = windowParams
        }

        initViews()
        initObservers()
        initListeners()
    }

    abstract fun initViews()
    protected open fun initObservers() {}
    protected open fun initListeners() {}

    protected open fun dimAmount(): Float = 0.6f
    protected open fun width(): Float = 0.95f

    fun show(fragmentManager: FragmentManager) {
        if (isAdded || isVisible || isRemoving || isStateSaved) {
            return
        }
        show(fragmentManager, tagName)
    }

    fun setDialogCancelable(isCancelable: Boolean) {
        this.isCancelable = isCancelable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isCreated = false
        _viewBinding = null
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.let { window ->
            val params: WindowManager.LayoutParams = window.attributes
            val displayMetrics = context?.resources?.displayMetrics
            params.width = (displayMetrics?.widthPixels?.times(width()))?.toInt() ?: params.width
            window.attributes = params
        }
    }
}
