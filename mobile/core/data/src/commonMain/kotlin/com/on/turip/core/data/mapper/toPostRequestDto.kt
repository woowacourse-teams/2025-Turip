package com.on.turip.core.data.mapper

import com.on.turip.core.data.dto.turip.TuripPatchRequest
import com.on.turip.core.data.dto.turip.TuripPostRequest

fun String.toPostRequestDto(): TuripPostRequest = TuripPostRequest(name = this)

fun String.toPatchRequestDto(): TuripPatchRequest = TuripPatchRequest(name = this)
