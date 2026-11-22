package com.example.japim.repository

import com.example.japim.graphql.RolesQuery
import com.example.japim.graphql.PermissionsQuery
import com.example.japim.graphql.type.InputRoleCreate
import com.example.japim.graphql.type.InputRoleUpdate
import com.example.japim.graphql.type.InputRolePermission
import com.example.japim.remoteDataSource.RoleRemoteDataSource

class RoleRepository(
    private val remoteDataSource: RoleRemoteDataSource
) {

    suspend fun getRoles(): List<RolesQuery.Role> {
        return remoteDataSource.fetchRoles()
    }

    suspend fun getPermissions(): List<PermissionsQuery.Permission> {
        return remoteDataSource.fetchPermissions()
    }

    suspend fun createRole(input: InputRoleCreate) {
        remoteDataSource.createRole(input)
    }

    suspend fun updateRole(input: InputRoleUpdate) {
        remoteDataSource.updateRole(input)
    }

    suspend fun deleteRole(id: String) {
        remoteDataSource.deleteRole(id)
    }

    suspend fun addPermission(input: InputRolePermission) {
        remoteDataSource.addPermission(input)
    }

    suspend fun removePermission(input: InputRolePermission) {
        remoteDataSource.removePermission(input)
    }
}