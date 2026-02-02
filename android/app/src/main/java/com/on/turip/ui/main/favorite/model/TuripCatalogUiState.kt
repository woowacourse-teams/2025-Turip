package com.on.turip.ui.main.favorite.model

data class TuripCatalogUiState(
    val places: List<TuripPlaceModel>,
    val turipName: String,
) {
    companion object {
        val Idle: TuripCatalogUiState =
            TuripCatalogUiState(
                places = emptyList(),
                turipName = "",
            )
    }
}
