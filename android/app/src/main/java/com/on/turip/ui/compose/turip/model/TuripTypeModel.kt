package com.on.turip.ui.compose.turip.model

enum class TuripTypeModel {
    SOLO,
    TOGETHER,
    ;

    companion object {
        fun from(isShared: Boolean): TuripTypeModel = if (isShared) TOGETHER else SOLO
    }
}
