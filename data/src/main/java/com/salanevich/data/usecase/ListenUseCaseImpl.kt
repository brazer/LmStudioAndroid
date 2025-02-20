package com.salanevich.data.usecase

import com.salanevich.data.repository.ListeningRepository
import com.salanevich.domain.model.SpeechState
import com.salanevich.domain.usecase.ListenUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListenUseCaseImpl @Inject constructor(
    private val repository: ListeningRepository
): ListenUseCase {
    override fun invoke(): Flow<SpeechState> {
        return repository.listen()
    }
}