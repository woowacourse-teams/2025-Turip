package com.on.turip.domain.common.paging

data class Page<T>(
    val items: List<T>,
    val hasNext: Boolean,
)
