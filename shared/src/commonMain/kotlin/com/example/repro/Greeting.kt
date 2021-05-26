package com.example.repro

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Executable
import com.apollographql.apollo3.api.MergedField
import com.apollographql.apollo3.cache.normalized.CacheKey
import com.apollographql.apollo3.cache.normalized.CacheKeyResolver
import com.apollographql.apollo3.cache.normalized.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.internal.RealApolloStore
import com.apollographql.apollo3.interceptor.cache.withStore
import com.apollographql.apollo3.network.http.ApolloHttpNetworkTransport
import com.apollographql.apollo3.network.ws.ApolloWebSocketNetworkTransport
import com.example.repro.graphql.CancelTripMutation
import com.example.repro.graphql.TripsBookedSubscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch

class Greeting : CoroutineScope by MainScope() {
    private val cacheFactory: NormalizedCacheFactory = MemoryCacheFactory(50 * 1024 * 1024)

    private val cacheResolver: CacheKeyResolver = object : CacheKeyResolver() {
        override fun fromFieldRecordSet(
            field: MergedField,
            variables: Executable.Variables,
            recordSet: Map<String, Any?>
        ): CacheKey {
            return CacheKey.from(recordSet["id"] as? String ?: "")
        }

        override fun fromFieldArguments(
            field: MergedField,
            variables: Executable.Variables
        ): CacheKey {
            return CacheKey.from(field.resolveArgument("id", variables) as? String ?: "")
        }
    }

    private val store = RealApolloStore(cacheFactory, cacheResolver)


    private val apolloClient = ApolloClient(
        networkTransport = ApolloHttpNetworkTransport(
            serverUrl = "https://apollo-fullstack-tutorial.herokuapp.com/graphql",
            headers = mapOf(
                "Accept" to "application/json",
                "Content-Type" to "application/json",
            )
        ),
        subscriptionNetworkTransport = ApolloWebSocketNetworkTransport("wss://apollo-fullstack-tutorial.herokuapp.com/graphql", this)
    )
        .withStore(store)

    fun greeting(): Flow<String> {
        launch {
            for (i in 1..1000) {
                delay(1000)
                println("cancelling trip $i")
                apolloClient.mutate(CancelTripMutation())
            }
        }
        return apolloClient.subscribe(TripsBookedSubscription()).withIndex().map { response ->
            "result: ${response.value.data?.tripsBooked}, iter: ${response.index}"
        }.filterNotNull()
    }
}