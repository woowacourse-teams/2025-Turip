package com.on.turip.data.userStorage

import com.on.turip.domain.userStorage.TuripDeviceIdentifier

fun String.toDomain(): TuripDeviceIdentifier = TuripDeviceIdentifier(fid = this)
