package com.genesys.sample

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager
import com.genesys.sample.databinding.DialogPermissionContentBinding
import com.hjq.permissions.dsl.OnUserResultCallback

class PermissionDialogFragment : BaseDialogFragment<DialogPermissionContentBinding>(DialogPermissionContentBinding::inflate) {

    override val tagName: String = "PermissionDialogFragment"

    @DrawableRes
    private var imageResId: Int = 0
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var positiveButtonText: String
    private lateinit var negativeButtonText: String
    private var onUserResult: OnUserResultCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageResId = requireArguments().getInt(ARG_IMAGE_RES_ID)
        title = requireArguments().getString(ARG_TITLE).orEmpty()
        description = requireArguments().getString(ARG_DESCRIPTION).orEmpty()
        positiveButtonText = requireArguments().getString(ARG_POSITIVE_BUTTON_TEXT).orEmpty()
        negativeButtonText = requireArguments().getString(ARG_NEGATIVE_BUTTON_TEXT).orEmpty()
    }

    override fun initViews() = with(viewBinding) {
        ivPermissionIcon.setImageResource(imageResId)
        tvPermissionTitle.text = title
        tvPermissionDescription.text = description
        btnPermissionPositive.text = positiveButtonText
        btnPermissionNegative.text = negativeButtonText
    }

    override fun initListeners() = with(viewBinding) {
        btnPermissionPositive.setOnClickListener {
            onUserResult?.onResult(true)
            dismiss()
        }

        btnPermissionNegative.setOnClickListener {
            onUserResult?.onResult(false)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onUserResult = null
    }

    companion object {
        private const val ARG_IMAGE_RES_ID = "arg_image_res_id"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_DESCRIPTION = "arg_description"
        private const val ARG_POSITIVE_BUTTON_TEXT = "arg_positive_button_text"
        private const val ARG_NEGATIVE_BUTTON_TEXT = "arg_negative_button_text"

        fun newInstance(
            content: PermissionDialogContent,
            positiveButtonText: String,
            negativeButtonText: String,
            onUserResult: OnUserResultCallback
        ) = PermissionDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_IMAGE_RES_ID, content.imageResId)
                putString(ARG_TITLE, content.title)
                putString(ARG_DESCRIPTION, content.description)
                putString(ARG_POSITIVE_BUTTON_TEXT, positiveButtonText)
                putString(ARG_NEGATIVE_BUTTON_TEXT, negativeButtonText)
            }
            this.onUserResult = onUserResult
            setDialogCancelable(false)
        }

        fun show(
            fragmentManager: FragmentManager,
            content: PermissionDialogContent,
            positiveButtonText: String,
            negativeButtonText: String,
            onUserResult: OnUserResultCallback
        ) {
            newInstance(
                content = content,
                positiveButtonText = positiveButtonText,
                negativeButtonText = negativeButtonText,
                onUserResult = onUserResult
            ).show(fragmentManager)
        }
    }
}
