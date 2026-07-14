package com.on.turip.feature.login.impl

sealed interface SocialCredential {
    data class Google(
        val idToken: String,
    ) : SocialCredential

    data class Apple(
        val idToken: String,
        val rawNonce: String,
    ) : SocialCredential
}
