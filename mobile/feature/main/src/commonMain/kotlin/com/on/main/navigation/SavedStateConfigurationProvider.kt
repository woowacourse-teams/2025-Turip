package com.on.turip.feature.main.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.on.turip.core.navigation.NavKeyProvider
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Stable
class SavedStateConfigurationProvider(
    val providers: List<NavKeyProvider>,
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
