package com.on.turip.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 현재 시각을 [LocalDateTime]으로 반환합니다.
 */
@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDateTime = clock.now().toLocalDateTime(timeZone)

/**
 * 이 문자열을 ISO-8601 형식으로 [LocalDate]로 변환합니다. (예: 2026-01-04)
 *
 * @throws IllegalArgumentException 문자열이 ISO 형식과 맞지 않을 경우
 */
fun String.toIsoLocalDate(): LocalDate =
    LocalDate.parse(this.replace(" ", "T"), LocalDate.Formats.ISO)

/**
 * 이 문자열을 ISO-8601 형식으로 [LocalDateTime]로 변환합니다. (예: 2020-08-30T18:43)
 *
 * @throws IllegalArgumentException 문자열이 ISO 형식과 맞지 않을 경우
 */
fun String.toIsoLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this.replace(" ", "T"), LocalDateTime.Formats.ISO)

/**
 * [LocalDate]를 주어진 유니코드 패턴으로 문자열로 변환합니다.
 *
 * @param pattern 기본값은 `yyyy-MM-dd`
 */
fun LocalDate.formatToString(pattern: String = "yyyy-MM-dd"): String =
    format(LocalDate.Format { byUnicodePattern(pattern) })

/**
 * [LocalTime]를 주어진 유니코드 패턴으로 문자열로 변환합니다.
 *
 * @param pattern 기본값은 `HH:mm`
 */
fun LocalTime.formatToString(pattern: String = "HH:mm"): String =
    format(LocalTime.Format { byUnicodePattern(pattern) })

/**
 * [LocalDateTime]를 주어진 유니코드 패턴으로 문자열로 변환합니다.
 *
 * @param pattern 기본값은 `yyyy-MM-dd HH:mm`
 */
fun LocalDateTime.formatToString(pattern: String = "yyyy-MM-dd HH:mm"): String =
    format(LocalDateTime.Format { byUnicodePattern(pattern) })

/**
 * [LocalDate]를 00:00:00 기준의 [LocalDateTime]으로 변환합니다.
 */
fun LocalDate.atStartOfDay(): LocalDateTime = atTime(0, 0)

fun LocalDate.atEndOfDay(): LocalDateTime = atTime(23, 59, 59)
