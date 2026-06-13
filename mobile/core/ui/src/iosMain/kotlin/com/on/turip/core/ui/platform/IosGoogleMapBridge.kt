package com.on.turip.core.ui.platform

import platform.UIKit.UIView

typealias IosGoogleMapViewFactory = () -> UIView
typealias IosGoogleMapViewUpdater = (
    view: UIView,
    selectedLatitude: Double,
    selectedLongitude: Double,
    selectedName: String,
    placesPayload: String,
) -> Unit

object IosGoogleMapBridge {
    private var factory: IosGoogleMapViewFactory? = null
    private var updater: IosGoogleMapViewUpdater? = null

    fun register(
        factory: IosGoogleMapViewFactory,
        updater: IosGoogleMapViewUpdater,
    ) {
        this.factory = factory
        this.updater = updater
    }

    fun createView(): UIView? = factory?.invoke()

    fun updateView(
        view: UIView,
        selectedLatitude: Double,
        selectedLongitude: Double,
        selectedName: String,
        placesPayload: String,
    ) {
        updater?.invoke(view, selectedLatitude, selectedLongitude, selectedName, placesPayload)
    }
}
