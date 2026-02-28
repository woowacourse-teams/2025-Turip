package com.on.turip.domain.accounts

enum class Role(
    private val tag: String,
) {
    MEMBER("MEMBER"),
    GUEST("GUEST"),
    ;

    companion object {
        fun from(role: String): Role {
            val tag = role.uppercase()
            return entries.firstOrNull { it.tag == tag } ?: GUEST
        }
    }
}
