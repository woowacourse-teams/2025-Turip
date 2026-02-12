package com.on.turip.ui.common.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.safeStartActivityWithToast(
    intent: Intent,
    errorToastMessage: String,
) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, errorToastMessage, Toast.LENGTH_SHORT).show()
    }
}
