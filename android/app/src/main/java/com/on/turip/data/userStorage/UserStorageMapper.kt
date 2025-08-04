package com.on.turip.data.userStorage

import com.on.turip.domain.settingsStorage.TuripDeviceIdentifier

fun String.toDomain(): TuripDeviceIdentifier = TuripDeviceIdentifier(fid = this)
