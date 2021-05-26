package com.example.repro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.freeze

class GreetingIos(private val greeting: Greeting) {
    private val scope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.Default
    }

    init {
        freeze()
    }

    fun greeting(): FlowWrapper<String> {
        return FlowWrapper(scope, greeting.greeting())
    }
}