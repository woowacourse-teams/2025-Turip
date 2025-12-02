package com.on.turip.di

import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.on.turip.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideSignInWithGoogleOption(): GetSignInWithGoogleOption =
        GetSignInWithGoogleOption
            .Builder(
                serverClientId = BuildConfig.CLIENT_ID,
            ).build()

    @Provides
    fun provideGetCredentialRequest(getSignInWithGoogleOption: GetSignInWithGoogleOption): GetCredentialRequest =
        GetCredentialRequest
            .Builder()
            .addCredentialOption(getSignInWithGoogleOption)
            .build()
}
