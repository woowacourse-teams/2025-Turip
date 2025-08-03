package com.on.turip.data.settingsStorage

import com.on.turip.domain.settingsStorage.TuripDeviceIdentifier

fun String.toDomain(): TuripDeviceIdentifier = TuripDeviceIdentifier(fid = this)
