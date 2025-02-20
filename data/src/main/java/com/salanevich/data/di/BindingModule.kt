package com.salanevich.data.di

import com.salanevich.data.network.LmStudioAPI
import com.salanevich.data.network.LmStudioAPIImpl
import com.salanevich.data.preferences.Preferences
import com.salanevich.data.preferences.PreferencesImpl
import com.salanevich.data.network.BaseUrlSetter
import com.salanevich.data.repository.ListeningRepository
import com.salanevich.data.repository.ListeningRepositoryImpl
import com.salanevich.data.repository.LmStudioRepository
import com.salanevich.data.repository.LmStudioRepositoryImpl
import com.salanevich.data.usecase.GetMessageUseCaseImpl
import com.salanevich.data.usecase.GetModelsUseCaseImpl
import com.salanevich.data.usecase.ListenUseCaseImpl
import com.salanevich.data.usecase.preferences.GetBaseUrlUseCaseImpl
import com.salanevich.data.usecase.preferences.GetSystemPromptUseCaseImpl
import com.salanevich.data.usecase.preferences.PutBaseUrlUseCaseImpl
import com.salanevich.data.usecase.preferences.PutSystemPromptUseCaseImpl
import com.salanevich.domain.usecase.GetMessageUseCase
import com.salanevich.domain.usecase.GetModelsUseCase
import com.salanevich.domain.usecase.ListenUseCase
import com.salanevich.domain.usecase.preferences.GetBaseUrlUseCase
import com.salanevich.domain.usecase.preferences.GetSystemPromptUseCase
import com.salanevich.domain.usecase.preferences.PutBaseUrlUseCase
import com.salanevich.domain.usecase.preferences.PutSystemPromptUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BindingModule {
    @Singleton
    @Binds
    fun bindLmStudioAPI(api: LmStudioAPIImpl): LmStudioAPI
    @Singleton
    @Binds
    fun bindGetModelsUseCase(useCase: GetModelsUseCaseImpl): GetModelsUseCase
    @Singleton
    @Binds
    fun bindGetMessageUseCase(useCase: GetMessageUseCaseImpl): GetMessageUseCase
    @Singleton
    @Binds
    fun bindLmStudioRepository(repo: LmStudioRepositoryImpl): LmStudioRepository
    @Singleton
    @Binds
    fun bindPreferences(preferences: PreferencesImpl): Preferences
    @Singleton
    @Binds
    fun bindGetBaseUrlUseCase(useCase: GetBaseUrlUseCaseImpl): GetBaseUrlUseCase
    @Singleton
    @Binds
    fun bindPutBaseUrlUseCase(useCase: PutBaseUrlUseCaseImpl): PutBaseUrlUseCase
    @Singleton
    @Binds
    fun bindPutSystemPromptUseCase(useCase: PutSystemPromptUseCaseImpl): PutSystemPromptUseCase
    @Singleton
    @Binds
    fun bindGetSystemPromptUseCase(useCase: GetSystemPromptUseCaseImpl): GetSystemPromptUseCase
    @Singleton
    @Binds
    fun bindBaseUrlProvider(api: LmStudioAPIImpl): BaseUrlSetter
    @Singleton
    @Binds
    fun bindListeningRepository(repository: ListeningRepositoryImpl): ListeningRepository
    @Singleton
    @Binds
    fun bindListenUseCase(useCase: ListenUseCaseImpl): ListenUseCase
}