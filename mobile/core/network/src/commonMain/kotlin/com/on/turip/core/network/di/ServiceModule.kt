package com.on.turip.core.network.di

import com.on.turip.core.network.service.AuthService
import com.on.turip.core.network.service.BookmarkService
import com.on.turip.core.network.service.ContentService
import com.on.turip.core.network.service.MemberService
import com.on.turip.core.network.service.RegionService
import com.on.turip.core.network.service.TuripService
import com.on.turip.core.network.service.createAuthService
import com.on.turip.core.network.service.createBookmarkService
import com.on.turip.core.network.service.createContentService
import com.on.turip.core.network.service.createMemberService
import com.on.turip.core.network.service.createRegionService
import com.on.turip.core.network.service.createTuripService
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.qualifier.named
import org.koin.dsl.module

val serviceModule = module {
    single<AuthService> { get<Ktorfit>(named(NO_AUTH_KTORFIT)).createAuthService() }
    single<MemberService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createMemberService() }
    single<TuripService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createTuripService() }
    single<ContentService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createContentService() }
    single<BookmarkService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createBookmarkService() }
    single<RegionService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createRegionService() }
}
