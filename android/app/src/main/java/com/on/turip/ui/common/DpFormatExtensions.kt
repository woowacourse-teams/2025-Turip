package com.on.turip.ui.common

import android.content.res.Resources

val Int.dpToPxFloat: Float
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
