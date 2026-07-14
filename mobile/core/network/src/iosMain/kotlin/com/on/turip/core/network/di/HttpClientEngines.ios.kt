package com.on.turip.core.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

actual fun createDefaultEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(Darwin, config)

actual fun createNoAuthEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(Darwin, config)

actual fun createSseEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(Darwin, config)
