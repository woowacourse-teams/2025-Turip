package com.on.turip.core.model.userstorage

data class TuripDeviceIdentifier(
    val fid: String,
) {
    companion object {
        val EMPTY: TuripDeviceIdentifier = TuripDeviceIdentifier(fid = "")
    }
}
