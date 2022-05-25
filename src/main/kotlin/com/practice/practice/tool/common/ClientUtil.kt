package com.practice.practice.tool.common

import kotlinx.coroutines.*
import org.apache.http.client.ResponseHandler
import org.apache.http.client.fluent.Async
import org.apache.http.client.fluent.Content
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Request
import org.apache.http.concurrent.FutureCallback
import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class AbstractClient(
    protected val executor: java.util.concurrent.Executor,
    protected val context: CoroutineDispatcher
) : Closeable {
    protected val client: Async = Async.newInstance().use(executor).use(Executor.newInstance())

    constructor(
        httpThreadCount: Int = 16,
        coroutineThreadCount: Int = 16,
        contextName: String = "default",
    ) : this(
        Executors.newFixedThreadPool(httpThreadCount),
        newFixedThreadPoolContext(coroutineThreadCount, contextName)
    )

    protected suspend fun <T> Request.perform(name: String = "client", callback: suspend (content: Content?) -> T?): T? {
        val job = ContentCallback(callback, name)
        client.execute(this, job)
        return job.getResult()
    }

    protected fun <T> Request.handle(handler: ResponseHandler<T>) = client.execute(this, handler)

    override fun close() {
        (executor as ExecutorService).shutdown()
        (context as? ExecutorCoroutineDispatcher)?.close()
    }


    inner class ContentCallback<T>(
        private val callback: suspend (content: Content?) -> T?,
        private val name: String
    ) : FutureCallback<Content> {
        private val mills = System.currentTimeMillis()
        private val deferred = CompletableDeferred<T?>()

        suspend fun getResult(): T? {
            return deferred.await()
        }

        override fun cancelled() {
            deferred.cancel()
        }

        override fun completed(result: Content?) {
            CoroutineScope(context).launch {
                deferred.complete(doCompleted(result))
            }
        }

        private suspend fun doCompleted(result: Content?): T? {
//            println("${Thread.currentThread().name} $name executes ${System.currentTimeMillis() - mills} ms")
            return callback.invoke(result)
        }

        override fun failed(ex: Exception?) {
            CoroutineScope(context).launch {
                deferred.completeExceptionally(ex ?: java.lang.Exception("unexpected exception"))
            }
        }
    }
}