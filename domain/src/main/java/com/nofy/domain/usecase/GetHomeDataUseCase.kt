package com.nofy.domain.usecase

import com.nofy.domain.model.HomeData
import com.nofy.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): HomeData {
        return repository.getHomeData()
    }
}
