package com.on.turip.domain.turip

enum class TuripType {
    SOLO,
    TOGETHER,
    ;

    companion object {
        fun from(isShared: Boolean): TuripType = if (isShared) TOGETHER else SOLO
    }
}
