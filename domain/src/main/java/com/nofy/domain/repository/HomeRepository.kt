package com.nofy.domain.repository

import com.nofy.domain.model.HomeData

interface HomeRepository {
    suspend fun getHomeData(): HomeData
}
