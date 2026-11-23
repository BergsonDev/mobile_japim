
package com.example.japim.repository

import com.example.japim.graphql.MeQuery
import com.example.japim.remoteDataSource.UserRemoteDataSource

class UserRepository(
    private val remoteDataSource: UserRemoteDataSource
) {

    suspend fun getCurrentUser(): MeQuery.Me {
        return remoteDataSource.getCurrentUser()
    }
}
