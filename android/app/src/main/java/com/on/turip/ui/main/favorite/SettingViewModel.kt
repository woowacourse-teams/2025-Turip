package com.on.turip.ui.main.favorite

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.ui.compose.common.util.SettingUtils
import com.on.turip.ui.compose.common.util.SettingUtils.EMAIL_RECIPIENT
import com.on.turip.ui.compose.common.util.SettingUtils.EMAIL_SUBJECT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
) : ViewModel() {
    private val _deviceIdentifier: MutableStateFlow<TuripDeviceIdentifier> =
        MutableStateFlow(TuripDeviceIdentifier.EMPTY)
    val deviceIdentifier: StateFlow<TuripDeviceIdentifier> = _deviceIdentifier

    init {
        loadId()
    }

    private fun loadId() {
        viewModelScope.launch {
            userStorageRepository
                .loadId()
                .onSuccess { result ->
                    _deviceIdentifier.value = result
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    fun loadInquiryUri(): Uri =
        "mailto:$EMAIL_RECIPIENT?subject=${Uri.encode(EMAIL_SUBJECT)}&body=${
            Uri.encode(SettingUtils.toEmailBody(deviceIdentifier.value.fid))
        }".toUri()

    fun loadPrivacyPolicyUri(): Uri = SettingUtils.PRIVACY_POLICY_LINK.toUri()
}
