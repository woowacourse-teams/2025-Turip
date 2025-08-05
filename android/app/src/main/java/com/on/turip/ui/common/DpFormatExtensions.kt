package com.on.turip.ui.common

import android.content.res.Resources

val Int.dpToPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)
