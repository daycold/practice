package com.practice.practice.distribution

import com.practice.practice.coroutines.RedisService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicLong

/**
Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.

@author liuwang
@date 2021年08月03日
 */
class DList<T> {
    private val list = mutableListOf<T>()
    private val identity: AtomicLong = AtomicLong(-1)

    fun take(id: Long): T? = if (identity.get() == id) list.lastOrNull() else null

    fun put(id: Long, value: T): Boolean {
        if (identity.get() == id) {
            list.add(value)
            return true
        }
        return false
    }

    fun isLocked(): Boolean = identity.get() != -1L

    fun lock(id: Long): Boolean = identity.compareAndSet(-1, id)

    fun unlock() = identity.set(-1)
}

interface Pool<T> {
    fun getOrCreateList(id: Long): DList<T>

    fun randomUnlockedList(): DList<T>?
}

open class Schedule<T>(
    private val executor: Executor,
    private val pool: Pool<T>
) {
    fun run() {
        executor.execute(this::doSchedule)
    }

    private fun doSchedule() {
        val id = generateId()
        pool.randomUnlockedList()?.apply {
            if (lock(id)) {
                try {
                    var task: T? = take(id)
                    while (task != null) {
                        doTask(task)
                        task = take(id)
                    }
                } finally {
                    unlock()
                }
            }
        }
    }

    protected fun doTask(task: T) {
        println(task)
    }

    protected fun generateId(): Long = System.currentTimeMillis()
}

class RedisPool(
    private val redis: RedisService
) : Pool<String> {
    override fun getOrCreateList(id: Long): DList<String> {
        val key = getKey(id)
        return DList()
    }

    override fun randomUnlockedList(): DList<String>? {
        TODO("Not yet implemented")
    }

    private fun getKey(id: Long): String {
        return "redis-pool:$id"
    }
}
