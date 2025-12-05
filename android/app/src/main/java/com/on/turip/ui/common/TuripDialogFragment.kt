package com.on.turip.ui.common

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.on.turip.R

class TuripDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View =
            requireActivity()
                .layoutInflater
                .inflate(R.layout.dialog_fragment_turip, null)

        val title = requireArguments().getString(ARG_TITLE)
        val description = requireArguments().getString(ARG_DESCRIPTION)
        val confirm = requireArguments().getString(ARG_CONFIRM)
        val dismiss = requireArguments().getString(ARG_DISMISS)

        bind(view, title, description, confirm, dismiss)

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(view)
                .create()

        // 기존 다이얼 로그 각진 테두리 제거
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        return dialog
    }

    private fun bind(
        view: View,
        title: String?,
        description: String?,
        confirm: String?,
        dismiss: String?,
    ) {
        view.findViewById<TextView>(R.id.tv_dialog_turip_title).apply { text = title }
        view.findViewById<TextView>(R.id.tv_dialog_turip_description).apply { text = description }
        view.findViewById<TextView>(R.id.tv_dialog_confirmation).apply { text = confirm }
        view.findViewById<TextView>(R.id.tv_dialog_dismiss).apply { text = dismiss }

        view.findViewById<TextView>(R.id.tv_dialog_confirmation).setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(TURIP_DIALOG_RESULT to RESULT_CONFIRM),
            )
            dismiss()
        }

        view.findViewById<TextView>(R.id.tv_dialog_dismiss).setOnClickListener {
            dismiss()
        }
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
