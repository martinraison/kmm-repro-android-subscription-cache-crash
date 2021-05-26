package com.example.repro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlin.native.concurrent.freeze

class FlowWrapper<T>(
    private val scope: CoroutineScope,
    private val flow: Flow<T>
) {
    init {
        freeze()
    }

    fun subscribe(
        onEach: (item: T) -> Unit,
        onComplete: () -> Unit,
        onThrow: (error: Throwable) -> Unit
    ): Job = flow
        .onEach { onEach(it.freeze()) }
        .catch { onThrow(it.freeze()) }
        .onCompletion { onComplete() }
        .launchIn(scope)
        .freeze()
}