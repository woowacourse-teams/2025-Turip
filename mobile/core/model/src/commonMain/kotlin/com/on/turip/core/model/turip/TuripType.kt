package com.on.turip.core.model.turip

enum class TuripType {
    SOLO,
    TOGETHER,
    ;

    companion object {
        fun from(isShared: Boolean): TuripType = if (isShared) TOGETHER else SOLO
    }
}
