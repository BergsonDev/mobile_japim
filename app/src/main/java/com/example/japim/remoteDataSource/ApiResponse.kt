package com.example.japim.remoteDataSource

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation

sealed class ApiResponse<out T> {
    data class Success<out T>(
        val data: T,
        val message: String = "Operação realizada com sucesso!"
    ) : ApiResponse<T>()

    data class Error(
        val message: String,
        val errors: List<String> = emptyList(),
        val exception: Throwable? = null
    ) : ApiResponse<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = (this as? Success)?.data

    inline fun onSuccess(action: (data: T, message: String) -> Unit): ApiResponse<T> {
        if (this is Success) action(data, message)
        return this
    }

    inline fun onError(action: (message: String, errors: List<String>) -> Unit): ApiResponse<T> {
        if (this is Error) action(message, errors)
        return this
    }
}

/**
 * Função helper para executar chamadas GraphQL via ApolloClient e retornar um [ApiResponse].
 * Extrai automaticamente os erros retornados pela API GraphQL (response.errors),
 * bem como exceções de rede/runtime.
 */
suspend inline fun <T, D : Operation.Data> executeApolloCall(
    defaultErrorMessage: String = "Erro na requisição GraphQL",
    successMessage: String = "Operação realizada com sucesso!",
    crossinline transformData: (D) -> T?,
    crossinline call: suspend () -> ApolloResponse<D>
): ApiResponse<T> {
    return try {
        val response = call()
        if (response.hasErrors()) {
            val errorList = response.errors?.map { it.message } ?: emptyList()
            val mainMessage = errorList.firstOrNull() ?: defaultErrorMessage
            ApiResponse.Error(
                message = mainMessage,
                errors = errorList
            )
        } else {
            val data = response.data?.let(transformData)
            if (data != null) {
                ApiResponse.Success(data, successMessage)
            } else {
                ApiResponse.Error(
                    message = "Nenhum dado retornado pela API",
                    errors = emptyList()
                )
            }
        }
    } catch (e: Exception) {
        ApiResponse.Error(
            message = e.localizedMessage ?: defaultErrorMessage,
            errors = listOfNotNull(e.message ?: e.toString()),
            exception = e
        )
    }
}
