package com.motut.mo.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce

/**
 * Handler for debouncing actions.
 * Requires an external CoroutineScope to manage lifecycle properly and avoid memory leaks.
 * The scope should be tied to the lifecycle of the component using this handler (e.g., ViewModel.viewModelScope).
 *
 * @param delayMillis The delay in milliseconds before executing the action after the last call
 * @param scope The CoroutineScope to use for launching coroutines. Must be cancelled when no longer needed.
 */
class DebounceHandler(
    private val delayMillis: Long = 300L,
    private val scope: CoroutineScope
) {
    private var job: Job? = null

    fun debounce(action: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch {
            delay(delayMillis)
            action()
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}

/**
 * Handler for throttling actions.
 * Requires an external CoroutineScope to manage lifecycle properly and avoid memory leaks.
 *
 * @param intervalMillis The minimum interval in milliseconds between action executions
 * @param scope The CoroutineScope to use for launching coroutines. Must be cancelled when no longer needed.
 */
class ThrottleHandler(
    private val intervalMillis: Long = 300L,
    private val scope: CoroutineScope
) {
    private var lastExecutionTime = 0L
    private val lock = Any()

    fun throttle(action: suspend () -> Unit) {
        val currentTime = System.currentTimeMillis()
        synchronized(lock) {
            if (currentTime - lastExecutionTime >= intervalMillis) {
                lastExecutionTime = currentTime
            } else {
                return
            }
        }
        scope.launch {
            action()
        }
    }

    fun reset() {
        synchronized(lock) {
            lastExecutionTime = 0L
        }
    }
}

/**
 * Creates a debounced version of a StateFlow.
 * WARNING: This extension function requires the caller to manage the returned scope.
 * Prefer using DebounceHandler with an explicit scope instead.
 *
 * @param delayMillis The debounce delay in milliseconds
 * @param scope The CoroutineScope to use. Must be cancelled by the caller.
 * @return A new StateFlow that emits values with debouncing
 */
@Deprecated(
    message = "Prefer using DebounceHandler with explicit scope for better lifecycle management",
    replaceWith = ReplaceWith("DebounceHandler(delayMillis, scope)")
)
@kotlinx.coroutines.FlowPreview
fun <T> StateFlow<T>.debounced(
    delayMillis: Long = 300L,
    scope: CoroutineScope
): StateFlow<T> {
    val debouncedState = MutableStateFlow(value)

    scope.launch {
        this@debounced.debounce(delayMillis).collect {
            debouncedState.value = it
        }
    }

    return debouncedState.asStateFlow()
}

/**
 * Utility class for lazy loading data with coroutines.
 * Uses the provided CoroutineScope for lifecycle management.
 *
 * @param loader The suspend function to load data
 * @param scope The CoroutineScope to use for loading. Should be tied to the component lifecycle.
 */
class LazyLoader<T>(
    private val loader: suspend () -> T,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val _value = MutableStateFlow<Result<T>?>(null)
    val value: StateFlow<Result<T>?> = _value.asStateFlow()

    private var isLoading = false

    fun load() {
        if (isLoading) return
        isLoading = true
        scope.launch {
            try {
                val result = loader()
                _value.value = Result.success(result)
            } catch (e: Exception) {
                _value.value = Result.failure(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun reset() {
        _value.value = null
        isLoading = false
    }
}

/**
 * LRU Cache implementation with thread-safe operations.
 *
 * @param maxSize Maximum number of entries in the cache
 */
class Cache<K, V>(private val maxSize: Int = 100) {
    private val cache = mutableMapOf<K, V>()
    private val accessOrder = mutableListOf<K>()
    private val lock = Any()

    fun get(key: K): V? {
        synchronized(lock) {
            val value = cache[key]
            if (value != null) {
                accessOrder.remove(key)
                accessOrder.add(key)
            }
            return value
        }
    }

    fun put(key: K, value: V) {
        synchronized(lock) {
            if (cache.size >= maxSize && !cache.contains(key)) {
                val oldestKey = accessOrder.firstOrNull()
                oldestKey?.let {
                    cache.remove(it)
                    accessOrder.remove(it)
                }
            }
            cache[key] = value
            accessOrder.remove(key)
            accessOrder.add(key)
        }
    }

    fun clear() {
        synchronized(lock) {
            cache.clear()
            accessOrder.clear()
        }
    }

    fun size(): Int = cache.size
}

object AnimationUtils {
    const val FAST_ANIMATION_DURATION = 150
    const val MEDIUM_ANIMATION_DURATION = 250
    const val SLOW_ANIMATION_DURATION = 400

    const val LOW_DAMPING = 0.4f
    const val MEDIUM_DAMPING = 0.6f
    const val HIGH_DAMPING = 0.8f

    const val LOW_STIFFNESS = 200f
    const val MEDIUM_STIFFNESS = 400f
    const val HIGH_STIFFNESS = 600f
}

class FrameRateMonitor {
    private var lastFrameTime = System.nanoTime()
    private var frameCount = 0
    private var lastFpsUpdate = System.currentTimeMillis()
    private var currentFps = 60

    fun onFrame() {
        val now = System.nanoTime()
        val elapsedNanos = now - lastFrameTime
        lastFrameTime = now

        frameCount++

        val nowMillis = System.currentTimeMillis()
        if (nowMillis - lastFpsUpdate >= 1000) {
            currentFps = frameCount
            frameCount = 0
            lastFpsUpdate = nowMillis
        }
    }

    fun getFps(): Int = currentFps

    fun isLowFps(): Boolean = currentFps < 45
}

/**
 * Utility for batching rapid updates to reduce UI work.
 * Requires an external CoroutineScope for proper lifecycle management.
 *
 * @param batchInterval Time to wait before flushing pending updates
 * @param updateCallback Called with batched items when interval expires
 * @param scope The CoroutineScope to use. Must be cancelled when no longer needed.
 */
class BatchUpdater<T>(
    private val batchInterval: Long = 100L,
    private val updateCallback: (List<T>) -> Unit,
    private val scope: CoroutineScope
) {
    private val pendingUpdates = mutableListOf<T>()
    private var job: Job? = null
    private val lock = Any()

    fun add(item: T) {
        synchronized(lock) {
            pendingUpdates.add(item)
            scheduleBatchUpdate()
        }
    }

    fun addAll(items: List<T>) {
        synchronized(lock) {
            pendingUpdates.addAll(items)
            scheduleBatchUpdate()
        }
    }

    private fun scheduleBatchUpdate() {
        job?.cancel()
        job = scope.launch {
            delay(batchInterval)
            val batch: List<T>
            synchronized(lock) {
                batch = pendingUpdates.toList()
                pendingUpdates.clear()
            }
            updateCallback(batch)
        }
    }

    fun flush() {
        job?.cancel()
        val batch: List<T>
        synchronized(lock) {
            if (pendingUpdates.isEmpty()) return
            batch = pendingUpdates.toList()
            pendingUpdates.clear()
        }
        updateCallback(batch)
    }

    fun cancel() {
        job?.cancel()
        synchronized(lock) {
            pendingUpdates.clear()
        }
    }
}
