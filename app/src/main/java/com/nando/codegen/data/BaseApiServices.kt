package com.nando.codegen.data

import com.apollographql.apollo.ApolloClient
import com.introdating.app.di.AuthServerApi
import com.introdating.app.di.AuthTokenServerApi
import com.introdating.app.di.BaseServerApi
import javax.inject.Inject


class BaseApiServices @Inject constructor(
    @BaseServerApi
    apolloClient: ApolloClient,
) {
    val client: ApolloClient = apolloClient
}

class AuthApiServices @Inject constructor(
    @AuthServerApi
    apolloClient: ApolloClient,
) {
    val client: ApolloClient = apolloClient
}

class AuthTokenApiServices @Inject constructor(
    @AuthTokenServerApi
    apolloClient: ApolloClient,
) {
    val client: ApolloClient = apolloClient
}
