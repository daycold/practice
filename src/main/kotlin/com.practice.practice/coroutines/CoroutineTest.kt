package com.practice.practice.coroutines

import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.SocketOptions
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.*
import java.io.Closeable
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.time.Duration
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.coroutines.suspendCoroutine

/**
 * @author Stefan Liu
 */
interface RedisService : RedisAsyncCommands<String, String>, Closeable

class RedisProxy(private val redisClient: RedisClient) : InvocationHandler {
    private var connection: StatefulRedisConnection<String, String>? = null
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        if (method.declaringClass == Closeable::class.java
            || method.declaringClass == AutoCloseable::class.java
        ) {
            connection?.close()
            return redisClient.shutdown()
        }
        return if (args != null)
            method.invoke(fetchConnection().async(), *args)
        else
            method.invoke(fetchConnection().async())
    }

    private fun fetchConnection(): StatefulRedisConnection<String, String> {
        val currentConnection = connection
        if (currentConnection == null || !currentConnection.isOpen) {
            synchronized(redisClient) {
                if (currentConnection == connection) {
                    connection = redisClient.connect()
                }
            }
        }
        return connection!!

    }
}

fun lettuceMain() = runBlocking {
    val redisService = Proxy.newProxyInstance(
        RedisProxy::class.java.classLoader,
        arrayOf(RedisService::class.java),
        RedisProxy(NioTest.getRedisService())
    ) as RedisService
    val name = async {
        val nameFuture = redisService.get("name")
        while (!nameFuture.isDone) {
            delay(200)
        }
        nameFuture.get()
    }
}

class NioTest {
    companion object {
        fun getRedisService(): RedisClient {
            val timeout = Duration.ofMillis(1000)
            return RedisClient.create("redis://127.0.0.1:3306").apply {
                setDefaultTimeout(timeout)
                options = ClientOptions.builder()
                    .socketOptions(
                        SocketOptions.builder()
                            .connectTimeout(timeout)
                            .keepAlive(true)
                            .build()
                    )
                    .pingBeforeActivateConnection(true)
                    .build()
            }
        }
    }
}

class LockTest {
    private val lock = ReentrantLock()

    suspend fun compete() {
        while (!lock.tryLock()) {
            println("failed to obtain lock")
            delay(200)
            println("wakeup")
        }
        println("obtain lock successfully")
        Thread.sleep(300)
        println("quit successfully")
        lock.unlock()
        println(System.currentTimeMillis())
    }

    fun doAnotherThing() {
        println("do another thing")
        Thread.sleep(300)
        println(System.currentTimeMillis())
    }
}

class LockTestThread {
    private val lock = ReentrantLock()

    fun compete() {
        while (!lock.tryLock()) {
            println("failed to obtain lock")
            Thread.sleep(200)
        }
        println("obtain lock successfully")
        Thread.sleep(300)
        println("quit successfully")
        lock.unlock()
        println(System.currentTimeMillis())
    }

    fun doAnotherThing() {
        println("do another thing")
        Thread.sleep(300)
        println(System.currentTimeMillis())
    }
}

fun LockMain() {
    val lockTest = LockTestThread()
    val thread1 = thread(start = false) {
        runBlocking {
            launch {
                lockTest.compete()
            }
            delay(100)
            lockTest.doAnotherThing()
        }
    }
    val thread2 = thread(start = false) {
        runBlocking {
            launch {
                lockTest.compete()
            }
            delay(100)
            lockTest.doAnotherThing()
        }
    }
    println(System.currentTimeMillis())
    thread1.start()
    thread2.start()
}

fun testCoroutineOutOfThread() {
    val coroutineDispather = newFixedThreadPoolContext(2, "threadPoolContext")
    CoroutineScope(coroutineDispather).launch {
        repeat(5) {
            println(Thread.currentThread().name)
            delay(200)
        }
    }
    println(Thread.currentThread().name)
    Thread.sleep(1500)
}

fun testDefaultDispatcher() = runBlocking {
    launch {
        repeat(5) {
            println(Thread.currentThread().name)
            delay(200)
        }
    }
    launch {
        repeat(5) {
            println(Thread.currentThread().name)
            delay(200)
        }
    }
    println(Thread.currentThread().name)
}

fun main() {
    testDefaultDispatcher()
}

