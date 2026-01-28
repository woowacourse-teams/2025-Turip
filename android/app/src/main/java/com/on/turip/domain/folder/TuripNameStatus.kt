package com.on.turip.domain.folder

enum class TuripNameStatus {
    OK,
    EMPTY,
    DUPLICATE_NAME,
    DEFAULT_TURIP_NAME,
    MAX_LENGTH_TURIP_NAME,
    OUT_OF_BOUND_LENGTH,
    ;

    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 20
        private const val DEFAULT_NAME = "기본 튜립"

        fun of(
            turipName: String,
            originTuripNames: LinkedHashSet<String>,
        ): TuripNameStatus {
            val trimmedTuripName = turipName.trim()

            return when {
                trimmedTuripName.isBlank() -> EMPTY
                trimmedTuripName.length == MAX_LENGTH -> MAX_LENGTH_TURIP_NAME
                trimmedTuripName.length !in MIN_LENGTH..MAX_LENGTH -> OUT_OF_BOUND_LENGTH
                trimmedTuripName == DEFAULT_NAME -> DEFAULT_TURIP_NAME
                originTuripNames.contains(trimmedTuripName) -> DUPLICATE_NAME
                else -> OK
            }
        }
    }
}
