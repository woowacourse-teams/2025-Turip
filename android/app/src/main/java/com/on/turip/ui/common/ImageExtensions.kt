package com.on.turip.ui.common

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation

fun ImageView.loadCircularImage(imageUrl: String) {
    this.load(imageUrl) {
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.loadRoundedCornerImage(
    imageUrl: String,
    radius: Int,
) {
    this.load(imageUrl) {
        crossfade(true)
        transformations(RoundedCornersTransformation(radius.dpToPx))
    }
}
