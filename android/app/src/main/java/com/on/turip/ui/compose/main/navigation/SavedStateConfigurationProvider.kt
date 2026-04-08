package com.on.turip.ui.compose.main.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.on.turip.navigation.NavKeyProvider
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import javax.inject.Inject

@Stable
class SavedStateConfigurationProvider @Inject constructor(
    val providers: List<@JvmSuppressWildcards NavKeyProvider>,
) {
    val savedStateConfiguration: SavedStateConfiguration by lazy {
        SavedStateConfiguration {
            serializersModule =
                SerializersModule {
                    polymorphic(NavKey::class) {
                        providers.forEach { provider ->
                            with(provider) {
                                registerNavKeys()
                            }
                        }
                    }
                }
        }
    }
}
