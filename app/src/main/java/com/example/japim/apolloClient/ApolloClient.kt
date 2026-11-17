package com.example.japim.apolloClient

import com.apollographql.apollo.ApolloClient
import com.example.japim.interceptor.AuthorizationInterceptor
import com.example.japim.BuildConfig
val apolloClient = ApolloClient.Builder()
    .serverUrl(BuildConfig.GRAPHQL_SERVER_URL)
    .addInterceptor(AuthorizationInterceptor(
        tokenProvider = {
            """
                eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBqYXBpbS5jb20iLCJyb2xlcyI6ImV2ZW50cy1jb25maXJtLHBlcm1pc3Npb25zLWRlbGV0ZSxldmVudHMtcmVhZCx1c2Vycy11cGRhdGUsZXZlbnRzLXZpZXcscGVybWlzc2lvbnMtdmlldyx1c2Vycy12aWV3LHJvbGVzLXVwZGF0ZSxyb2xlcy1yZWFkLHBlcm1pc3Npb25zLWNyZWF0ZSxyb2xlcy12aWV3LGV2ZW50cy1kZWxldGUsZXZlbnRzLXN1YnNjcmliZS12b2x1bnRlZXIsZXZlbnRzLWNyZWF0ZSxldmVudHMtc3Vic2NyaWJlLXBhcnRpY2lwYW50LGV2ZW50cy11bnN1YnNjcmliZS12b2x1bnRlZXIsZXZlbnRzLWNvbmZpcm0tcHJlc2VuY2Uscm9sZXMtZGVsZXRlLHBlcm1pc3Npb25zLXVwZGF0ZSx1c2Vycy1yZWFkLGV2ZW50cy11cGRhdGUsdXNlcnMtY3JlYXRlLHJvbGVzLWNyZWF0ZSxwZXJtaXNzaW9ucy1yZWFkLGV2ZW50cy1jb25maXJtLXByZXNlbmNlLXZvbHVudGVlcnMsZXZlbnRzLXVuc3Vic2NyaWJlLXBhcnRpY2lwYW50LHVzZXJzLWRlbGV0ZSIsImlhdCI6MTc5NDk1NTg1NCwiZXhwIjoxNzk1MDQyMjU0fQ.x7tA6E9GiRwj-_L81dMaiy-vqfRSo02MKMKalrXcqrs
            """.trimIndent()
        }
    ))
    .build()