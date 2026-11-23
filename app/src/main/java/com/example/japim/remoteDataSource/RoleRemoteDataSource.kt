package com.example.japim.remoteDataSource


import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.example.japim.graphql.RolesQuery
import com.example.japim.graphql.PermissionsQuery
import com.example.japim.graphql.CreateRoleMutation
import com.example.japim.graphql.UpdateRoleMutation
import com.example.japim.graphql.DeleteRoleMutation
import com.example.japim.graphql.AddRolePermissionMutation
import com.example.japim.graphql.RemoveRolePermissionMutation
import com.example.japim.graphql.type.InputRoleCreate
import com.example.japim.graphql.type.InputRoleUpdate
import com.example.japim.graphql.type.InputRolePermission

class RoleRemoteDataSource(
    private val apolloClient: ApolloClient
) {

    suspend fun fetchRoles(): List<RolesQuery.Role> {

        val response = apolloClient
            .query(RolesQuery())
            .execute()

        validateResponse(response)

        return response.data?.roles
            ?.filterNotNull()
            .orEmpty()
    }

    suspend fun fetchPermissions(): List<PermissionsQuery.Permission> {

        val response = apolloClient
            .query(PermissionsQuery())
            .execute()

        validateResponse(response)

        return response.data?.permissions
            ?.filterNotNull()
            .orEmpty()
    }

    suspend fun createRole(input: InputRoleCreate) {

        val response = apolloClient
            .mutation(CreateRoleMutation(input))
            .execute()

        validateResponse(response)
    }

    suspend fun updateRole(input: InputRoleUpdate) {

        val response = apolloClient
            .mutation(UpdateRoleMutation(input))
            .execute()

        validateResponse(response)
    }

    suspend fun deleteRole(id: String) {

        val response = apolloClient
            .mutation(DeleteRoleMutation(id))
            .execute()

        validateResponse(response)
    }

    suspend fun addPermission(input: InputRolePermission) {

        val response = apolloClient
            .mutation(AddRolePermissionMutation(input))
            .execute()

        validateResponse(response)
    }

    suspend fun removePermission(input: InputRolePermission) {

        val response = apolloClient
            .mutation(RemoveRolePermissionMutation(input))
            .execute()

        validateResponse(response)
    }

    private fun validateResponse(response: ApolloResponse<*>) {

        response.exception?.let {
            throw it
        }

        if (!response.errors.isNullOrEmpty()) {
            throw IllegalStateException(
                response.errors?.firstOrNull()?.message
                    ?: "GraphQL operation failed"
            )
        }

        if (response.data == null) {
            throw IllegalStateException(
                "No data returned by the server"
            )
        }
    }
}