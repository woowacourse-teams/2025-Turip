package com.on.turip.feature.invitation.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.invitation.impl.InvitationEntryViewModel
import com.on.turip.feature.invitation.impl.navigation.InvitationNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val invitationModule = module {
    single { InvitationNavKeyProvider() } bind NavKeyProvider::class
    viewModel<InvitationEntryViewModel> { InvitationEntryViewModel(get(), get(), get(), get()) }
}
