package com.on.turip.core.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp

actual fun createDefaultEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(OkHttp, config)

actual fun createNoAuthEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(OkHttp, config)

actual fun createSseEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(OkHttp, config)
