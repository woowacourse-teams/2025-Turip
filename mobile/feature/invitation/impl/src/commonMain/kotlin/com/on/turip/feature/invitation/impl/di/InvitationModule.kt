package com.on.turip.feature.invitation.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.invitation.impl.navigation.InvitationEntryNavKeyProvider
import com.on.turip.feature.invitation.impl.viewmodel.InvitationEntryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val invitationModule = module {
    viewModel { (deepLinkUrl: String) ->
        InvitationEntryViewModel(
            deepLinkUrl = deepLinkUrl,
            invitationRepository = get(),
        )
    }
    single { InvitationEntryNavKeyProvider() } bind NavKeyProvider::class
}
