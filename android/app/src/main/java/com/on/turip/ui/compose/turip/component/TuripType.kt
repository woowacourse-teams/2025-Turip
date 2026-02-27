package com.on.turip.ui.compose.turip.component

enum class TuripType {
    SOLO,
    TOGETHER,
    ;

    companion object {
        fun of(isShared: Boolean): TuripType = if (isShared) TOGETHER else SOLO
    }
}
