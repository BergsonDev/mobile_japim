package com.example.japim.remoteDataSource

package com.example.japim.remoteDataSource

import com.apollographql.apollo.ApolloClient
import com.example.japim.graphql.MyCertificatesQuery

class CertificateRemoteDataSource(
    private val apolloClient: ApolloClient
) {

    suspend fun fetchMyCertificates(): List<MyCertificatesQuery.MyCertificate> {
        val response = apolloClient
            .query(MyCertificatesQuery())
            .execute()

        response.exception?.let { throw it }

        if (!response.errors.isNullOrEmpty()) {
            throw IllegalStateException(
                response.errors?.firstOrNull()?.message
                    ?: "Error fetching certificates"
            )
        }

        return response.data?.myCertificates
            ?.filterNotNull()
            .orEmpty()
    }
}