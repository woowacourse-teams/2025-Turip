package com.on.turip.ui.common

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R

/*
 * ⚠️ 현재 스낵바를 상단에 표시하는 기능은 CoordinatorLayout 에 한해서 제공하고 있습니다.
 *     만약 다른 ViewGroup 에서 topMargin을 설정하더라도 적용이 되지 않습니다.
 */
class TuripSnackbar private constructor(
    private val rootView: View,
    private val messageResource: Int,
    private val duration: Int,
    private val layoutInflater: LayoutInflater,
) {
    private var snackbarIconResource: Int? = null
    private var topMargin: Int? = null
    private var actionTextResource: Int? = null
    private var actionClickListener: (() -> Unit)? = null

    fun icon(
        @DrawableRes iconResource: Int,
    ): TuripSnackbar {
        snackbarIconResource = iconResource
        return this
    }

    fun topMarginInCoordinatorLayout(margin: Int): TuripSnackbar {
        topMargin = margin
        return this
    }

    fun action(
        @StringRes textResource: Int,
        listener: (() -> Unit)? = null,
    ): TuripSnackbar {
        actionTextResource = textResource
        actionClickListener = listener
        return this
    }

    fun show() {
        val snackbar: Snackbar = Snackbar.make(rootView, messageResource, duration)
        snackbar.view.background = null

        val snackbarLayout: ViewGroup = snackbar.view as ViewGroup
        val turipSnackbarView: View =
            layoutInflater.inflate(R.layout.layout_snackbar, snackbarLayout, false)
        snackbarLayout.addView(turipSnackbarView)

        turipSnackbarView.findViewById<TextView>(R.id.tv_snackbar_message).setText(messageResource)
        snackbarIconResource?.let { drawable: Int ->
            turipSnackbarView
                .findViewById<ImageView>(R.id.iv_snackbar_icon)
                .setImageResource(drawable)
        }
        topMargin?.let { margin: Int ->
            val params = snackbar.view.layoutParams
            if (params is CoordinatorLayout.LayoutParams) {
                params.gravity = Gravity.TOP
                params.topMargin = margin
                snackbar.view.layoutParams = params
            }
        }
        actionTextResource?.let { textResource: Int ->
            turipSnackbarView.findViewById<TextView>(R.id.tv_snackbar_button).setText(textResource)
            snackbar.setAction(textResource) {
                actionClickListener?.invoke()
                snackbar.dismiss()
            }
        }

        snackbar.show()
    }

    companion object {
        fun make(
            rootView: View,
            @StringRes messageResource: Int,
            duration: Int,
            layoutInflater: LayoutInflater,
        ): TuripSnackbar = TuripSnackbar(rootView, messageResource, duration, layoutInflater)
    }
}
