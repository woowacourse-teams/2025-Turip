package com.on.turip.data.userstorage

import com.on.turip.domain.userstorage.TuripDeviceIdentifier

fun String.toDomain(): TuripDeviceIdentifier = TuripDeviceIdentifier(fid = this)
