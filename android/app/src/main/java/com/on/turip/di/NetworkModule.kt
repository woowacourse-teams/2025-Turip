package com.on.turip.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.on.turip.BuildConfig
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val LOG_PREFIX = "moongjenut"

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message -> logHttpMessage(message) }.apply {
            level =
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }

    private fun logHttpMessage(message: String) {
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val prettyJson =
                    Json {
                        prettyPrint = true
                    }.encodeToString(
                        JsonElement.serializer(),
                        Json.parseToJsonElement(message),
                    )
                Timber.tag(LOG_PREFIX).d(prettyJson)
            } catch (e: Exception) {
                Timber.tag(LOG_PREFIX).d(message)
            }
        } else {
            Timber.tag(LOG_PREFIX).d(message)
        }
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(userStorageRepository: UserStorageRepository): Interceptor =
        Interceptor { chain ->
            val fid =
                runBlocking {
                    userStorageRepository.loadId().getOrNull()?.fid ?: ""
                }

            val newRequest =
                chain
                    .request()
                    .newBuilder()
                    .header("device-fid", fid)
                    .build()

            chain.proceed(newRequest)
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}
