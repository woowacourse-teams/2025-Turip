package com.on.turip.ui.common

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation

fun ImageView.loadByCircle(imageUrl: String) {
    this.load(imageUrl) {
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}
