package com.example.japim.repository

import com.example.japim.graphql.MyCertificatesQuery
import com.example.japim.remoteDataSource.CertificateRemoteDataSource

class CertificateRepository(
    private val remoteDataSource: CertificateRemoteDataSource
) {

    suspend fun getMyCertificates(): List<MyCertificatesQuery.MyCertificate> {
        return remoteDataSource.fetchMyCertificates()
    }
}