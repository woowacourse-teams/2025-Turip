package com.on.turip.ui.common.errorview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.on.turip.databinding.ItemCustomErrorBinding
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel

class CustomErrorView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ItemCustomErrorBinding =
        ItemCustomErrorBinding.inflate(LayoutInflater.from(context), this)

    private val errorImageView: ImageView = binding.ivCustomError
    private val errorTitleTextView: TextView = binding.tvCustomErrorTitle
    private val errorDescription: TextView = binding.tvCustomErrorDescription
    private val errorRetryTextView: TextView = binding.tvCustomErrorRetry

    /**
     * 에러 이미지를 설정합니다
     */
    private fun setErrorImage(
        @DrawableRes drawableRes: Int,
    ) {
        errorImageView.setImageResource(drawableRes)
    }

    /**
     * 에러 제목을 리소스로 설정합니다
     */
    private fun setErrorTitle(
        @StringRes titleRes: Int,
    ) {
        errorTitleTextView.setText(titleRes)
    }

    /**
     * 에러 설명을 리소스로 설정합니다
     */
    private fun setErrorDescription(
        @StringRes descriptionRes: Int,
    ) {
        errorDescription.setText(descriptionRes)
    }

    /**
     * 재시도 메시지를 리소스로 설정합니다
     */
    private fun setRetryMessage(
        @StringRes messageRes: Int,
    ) {
        errorRetryTextView.setText(messageRes)
    }

    /**
     * 재시도 텍스트에 클릭 리스너를 설정합니다
     */
    fun setOnRetryClickListener(listener: OnClickListener) {
        errorRetryTextView.setOnClickListener(listener)
    }

    /**
     * 에러 타입에 따른 기본 설정
     *
     * 네트워크 에러, 서버 에러(500번대)의 경우 재시도 가능한 화면을 활성화한다.
     */
    fun showErrorView(errorUiState: ErrorUiState) {
        val errorUiModel: ErrorUiModel = errorUiState.toUiModel() ?: return

        setErrorImage(errorUiModel.imageRes)
        setErrorTitle(errorUiModel.titleRes)
        setErrorDescription(errorUiModel.descriptionRes)
        setRetryMessage(errorUiModel.retryTextRes)
    }
}
