package com.on.turip.domain.favorite

data class PagedBookmarkContents(
    val bookmarkContents: List<BookmarkContent>,
    val loadable: Boolean,
)
