package com.motut.mo.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce

class DebounceHandler(private val delayMillis: Long = 300L) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
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
    }
}

class ThrottleHandler(private val intervalMillis: Long = 300L) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var lastExecutionTime = 0L

    fun throttle(action: suspend () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastExecutionTime >= intervalMillis) {
            lastExecutionTime = currentTime
            scope.launch {
                action()
            }
        }
    }
}

fun <T> StateFlow<T>.debounced(
    delayMillis: Long = 300L
): StateFlow<T> {
    val debouncedState = MutableStateFlow(value)
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    scope.launch {
        this@debounced.debounce(delayMillis).collect {
            debouncedState.value = it
        }
    }
    
    return debouncedState.asStateFlow()
}

class LazyLoader<T>(
    private val loader: suspend () -> T,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val _value = MutableStateFlow<Result<T>?>(null)
    val value: StateFlow<Result<T>?> = _value.asStateFlow()
    
    private var isLoading = false
    
    fun load() {
        if (isLoading) return
        isLoading = true
        coroutineScope.launch {
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

class Cache<K, V>(private val maxSize: Int = 100) {
    private val cache = mutableMapOf<K, V>()
    private val accessOrder = mutableListOf<K>()
    
    fun get(key: K): V? {
        val value = cache[key]
        if (value != null) {
            accessOrder.remove(key)
            accessOrder.add(key)
        }
        return value
    }
    
    fun put(key: K, value: V) {
        if (cache.size >= maxSize && !cache.containsKey(key)) {
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
    
    fun clear() {
        cache.clear()
        accessOrder.clear()
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

class BatchUpdater<T>(
    private val batchInterval: Long = 100L,
    private val updateCallback: (List<T>) -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val pendingUpdates = mutableListOf<T>()
    private var job: Job? = null
    
    fun add(item: T) {
        pendingUpdates.add(item)
        scheduleBatchUpdate()
    }
    
    fun addAll(items: List<T>) {
        pendingUpdates.addAll(items)
        scheduleBatchUpdate()
    }
    
    private fun scheduleBatchUpdate() {
        job?.cancel()
        job = scope.launch {
            delay(batchInterval)
            val batch = pendingUpdates.toList()
            pendingUpdates.clear()
            updateCallback(batch)
        }
    }
    
    fun flush() {
        job?.cancel()
        if (pendingUpdates.isNotEmpty()) {
            val batch = pendingUpdates.toList()
            pendingUpdates.clear()
            updateCallback(batch)
        }
    }
}
