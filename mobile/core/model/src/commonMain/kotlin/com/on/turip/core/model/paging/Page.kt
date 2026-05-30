package com.on.turip.core.model.paging

data class Page<T>(
    val items: List<T>,
    val hasNext: Boolean,
)
