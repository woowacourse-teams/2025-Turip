package com.on.turip.domain.userstorage

data class TuripDeviceIdentifier(
    val fid: String,
) {
    companion object {
        val EMPTY: TuripDeviceIdentifier = TuripDeviceIdentifier(fid = "")
    }
}
