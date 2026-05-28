package com.on.turip.feature.turipdetail.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface TuripDetailIntent : UiIntent {
    data object Retry : TuripDetailIntent
    data object ShowMemberSheet : TuripDetailIntent
    data object DismissMemberSheet : TuripDetailIntent
    data object ShowMoreOptions : TuripDetailIntent
    data object DismissMoreOptions : TuripDetailIntent
    data object ShowDeleteDialog : TuripDetailIntent
    data object DismissDeleteDialog : TuripDetailIntent
    data object ConfirmDelete : TuripDetailIntent
    data object ShowRenameSheet : TuripDetailIntent
    data object DismissRenameSheet : TuripDetailIntent
    data class UpdateName(val name: String) : TuripDetailIntent
    data object ConfirmRename : TuripDetailIntent
    data class RemovePlace(val turipPlaceId: Long) : TuripDetailIntent
}
