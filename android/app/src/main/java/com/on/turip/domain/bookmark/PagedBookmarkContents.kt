package com.on.turip.domain.bookmark

data class PagedBookmarkContents(
    val bookmarkContents: List<BookmarkContent>,
    val loadable: Boolean,
)
