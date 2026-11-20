
package com.example.japim.apolloClient

object TokenManager {

    @Volatile
    var token: String? = null
        private set

    fun salvarToken(novoToken: String) {
        token = novoToken
    }

    fun limparToken() {
        token = null
    }
}
