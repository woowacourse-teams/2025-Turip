package com.on.turip.ui.common

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.on.turip.databinding.DialogFragmentTuripBinding

class TuripDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentTuripBinding? = null
    val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFragmentTuripBinding.inflate(requireActivity().layoutInflater)

        val title: String = requireArguments().getString(ARG_TITLE).orEmpty()
        val description: String = requireArguments().getString(ARG_DESCRIPTION).orEmpty()
        val confirm: String = requireArguments().getString(ARG_CONFIRM).orEmpty()
        val dismiss: String = requireArguments().getString(ARG_DISMISS).orEmpty()

        bind(title, description, confirm, dismiss)

        val dialog: AlertDialog =
            AlertDialog
                .Builder(requireContext())
                .setView(binding.root)
                .create()

        // 기존 다이얼 로그 각진 테두리 제거
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        return dialog
    }

    private fun bind(
        title: String,
        description: String,
        confirm: String,
        dismiss: String,
    ) {
        binding.tvDialogTuripTitle.text = title
        binding.tvDialogTuripDescription.text = description
        binding.tvDialogConfirmation.text = confirm
        binding.tvDialogDismiss.text = dismiss

        binding.tvDialogConfirmation.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(TURIP_DIALOG_RESULT to RESULT_CONFIRM),
            )
            dismiss()
        }

        binding.tvDialogDismiss.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "TURIP_DIALOG_TITLE"
        private const val ARG_DESCRIPTION = "TURIP_DIALOG_DESCRIPTION"
        private const val ARG_CONFIRM = "TURIP_DIALOG_CONFIRM"
        private const val ARG_DISMISS = "TURIP_DIALOG_DISMISS"

        const val REQUEST_KEY = "TURIP_DIALOG"
        const val TURIP_DIALOG_RESULT = "TURIP_DIALOG_RESULT"
        const val RESULT_CONFIRM = "TURIP_DIALOG_RESULT_CONFIRM"

        fun newInstance(
            title: String,
            description: String,
            confirmText: String,
            dismissText: String,
        ): TuripDialogFragment =
            TuripDialogFragment().apply {
                arguments =
                    bundleOf(
                        ARG_TITLE to title,
                        ARG_DESCRIPTION to description,
                        ARG_CONFIRM to confirmText,
                        ARG_DISMISS to dismissText,
                    )
            }
    }
}
