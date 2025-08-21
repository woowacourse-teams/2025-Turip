package com.on.turip.ui.common.errorview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.on.turip.R
import com.on.turip.domain.ErrorEvent

class CustomErrorView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private val errorImageView: ImageView
        private val errorTitleTextView: TextView
        private val errorDescription: TextView
        private val errorRetryTextView: TextView

        init {
            // XML 레이아웃 inflate
            LayoutInflater.from(context).inflate(R.layout.item_custom_error, this, true)

            // 뷰 참조 초기화
            errorImageView = findViewById(R.id.iv_server_error)
            errorTitleTextView = findViewById(R.id.tv_server_error)
            errorDescription = findViewById(R.id.tv_server_error_description)
            errorRetryTextView = findViewById(R.id.tv_server_error_retry)
        }

        /**
         * 에러 이미지를 설정합니다
         */
        fun setErrorImage(drawableRes: Int) {
            errorImageView.setImageResource(drawableRes)
        }

        /**
         * 에러 제목을 설정합니다
         */
        fun setErrorTitle(title: String) {
            errorTitleTextView.text = title
        }

        /**
         * 에러 제목을 리소스로 설정합니다
         */
        fun setErrorTitle(titleRes: Int) {
            errorTitleTextView.setText(titleRes)
        }

        /**
         * 에러 설명을 설정합니다
         */
        fun setErrorDescription(title: String) {
            errorTitleTextView.text = title
        }

        /**
         * 에러 설명을 리소스로 설정합니다
         */
        fun setErrorDescription(titleRes: Int) {
            errorTitleTextView.setText(titleRes)
        }

        /**
         * 재시도 메시지를 설정합니다
         */
        fun setRetryMessage(message: String) {
            errorRetryTextView.text = message
        }

        /**
         * 재시도 메시지를 리소스로 설정합니다
         */
        fun setRetryMessage(messageRes: Int) {
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
         */
        fun setupError(errorType: ErrorEvent) {
            when (errorType) {
                ErrorEvent.NETWORK_ERROR -> {
                    setErrorImage(R.drawable.ic_network_error)
                    setErrorTitle(R.string.cannot_connect_network)
                    setErrorDescription(R.string.check_connection_status)
                    setRetryMessage(R.string.retry)
                }

                ErrorEvent.USER_NOT_HAVE_PERMISSION -> {
                    setErrorImage(R.drawable.ic_server_error)
                    setErrorTitle(R.string.server_error)
                    setErrorDescription(R.string.retry_later)
                    setRetryMessage(R.string.retry)
                }

                ErrorEvent.DUPLICATION_FOLDER -> {
                    setErrorImage(R.drawable.ic_server_error)
                    setErrorTitle(R.string.server_error)
                    setErrorDescription(R.string.retry_later)
                    setRetryMessage(R.string.retry)
                }

                ErrorEvent.UNEXPECTED_PROBLEM -> {
                    setErrorImage(R.drawable.ic_server_error)
                    setErrorTitle(R.string.server_error)
                    setErrorDescription(R.string.retry_later)
                    setRetryMessage(R.string.retry)
                }

                ErrorEvent.PARSER_ERROR -> {
                    setErrorImage(R.drawable.ic_server_error)
                    setErrorTitle(R.string.server_error)
                    setErrorDescription(R.string.retry_later)
                    setRetryMessage(R.string.retry)
                }
            }
        }
    }
