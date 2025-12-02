package com.on.turip.ui.common.event

sealed interface CommonEvent {
    data object TokenExpiration : CommonEvent
}
