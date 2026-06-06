package com.on.turip.core.data.mapper

import com.on.turip.core.model.userstorage.TuripDeviceIdentifier

fun String.toDomain(): TuripDeviceIdentifier = TuripDeviceIdentifier(fid = this)
