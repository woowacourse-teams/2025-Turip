package com.on.turip.ui.common.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.collectOnStarted(
    flow: Flow<T>,
    collector: suspend (T) -> Unit,
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collector)
        }
    }
}

fun <T> Fragment.collectOnStarted(
    flow: Flow<T>,
    collector: suspend (T) -> Unit,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collector)
        }
    }
}
