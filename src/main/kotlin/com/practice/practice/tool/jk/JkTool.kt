package com.practice.practice.tool.jk

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.practice.Mainable
import kotlinx.coroutines.*
import org.apache.http.HttpHeaders
import org.apache.http.client.ResponseHandler
import org.apache.http.client.fluent.Async
import org.apache.http.client.fluent.Content
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Request
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import reactor.core.publisher.Mono
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author : liuwang
 * @date : 2020-08-19T17:18:34Z
 */
class JkTool : Mainable {
    override fun doMain() {
        returnCoin(orderNo = "1120180124833000282")
        close()
    }

    private fun returnCoin(orderNo: String) {
//        revertCoin(orderNo)
        runBlocking {
//            handleAccountId(orderNo)
            revertCoinRequest(orderNo)
        }
    }
}

object JsonMapper : ObjectMapper() {
    init {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}

private const val internalToken = "Basic bWFsbF9hcHA6SkthcHAduMTz"
private const val devToken = "Basic bWFsbF9hcHA6YXBwX3sBhc3N33Jk"
private const val internalHost = "http://hello.internal.world.com"
private const val devHost = "http://hello.dev.world.com"
private val executor: java.util.concurrent.Executor = Executors.newFixedThreadPool(8)
private val http = Async.newInstance().use(executor).use(Executor.newInstance())
private val context = newFixedThreadPoolContext(5, "default")
private fun HttpRequestBase.addToken(env: Env): HttpRequestBase {
    addHeader(HttpHeaders.AUTHORIZATION, getToken(env))
    return this
}

fun close() {
    (executor as ExecutorService).shutdown()
    context.close()
}

private fun getToken(env: Env) = when (env) {
    Env.DEV -> devToken
    Env.INTERNAL -> internalToken
}

private fun getHost(env: Env) = when (env) {
    Env.DEV -> devHost
    Env.INTERNAL -> internalHost
}

private suspend fun <T> Request.perform(callback: suspend (content: Content?) -> T?): T? {
    val job = ContentCallback(callback)
    http.execute(this, job)
    return job.getResult()
}

private fun <T> Request.handle(handler: ResponseHandler<T>) = http.execute(this, handler)

private fun getAccountId(orderNo: String, env: Env): String =
    HttpClients.createDefault()
        .execute(HttpGet("${getHost(env)}/order/orders?ordersCode=$orderNo&decrypt=true").addToken(env))
        .use { res -> res.entity.content.use { `is` -> JsonMapper.readTree(`is`).path("accountId").textValue() } }

private fun revertCoin(orderNo: String, env: Env = Env.DEV) {
    val body = """{"accountId":"${getAccountId(orderNo, env)}","businessId":$orderNo,"description":$orderNo}"""
    HttpClients.createDefault().execute(HttpPost("${getHost(env)}/v2/coin/cancel").apply {
        addToken(env)
        addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
        entity = StringEntity(body)
    }).use {
        println(it.statusLine.statusCode)
    }
}

private suspend fun getAccountIdRequest(orderNo: String, env: Env) =
    Request.Get("${getHost(env)}/order/orders?ordersCode=$orderNo&decrypt=true")
        .addHeader(HttpHeaders.AUTHORIZATION, getToken(env))
        .perform { content ->
            return@perform if (content == null) {
                null
            } else {
                JsonMapper.readTree(content.asBytes()).path("accountId").textValue()
            }
        }

private suspend fun revertCoinRequest(orderNo: String, env: Env = Env.DEV) {
    val body = """{"accountId":"${getAccountIdRequest(orderNo, env)}","businessId":$orderNo,"description":$orderNo}"""
    Request.Post("${getHost(env)}/v2/coin/cancel")
        .addHeader(HttpHeaders.AUTHORIZATION, getToken(env))
        .addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
        .body(StringEntity(body, Charsets.UTF_8))
        .handle() { response ->
            println(response.statusLine.statusCode)
            response.entity.content?.close()
            if (response is CloseableHttpResponse) {
                response.close()
            }
        }
}

private suspend fun handleAccountId(orderNo: String, env: Env = Env.DEV) {
    Request.Get("${getHost(env)}/order/orders?ordersCode=$orderNo&decrypt=true")
        .addHeader(HttpHeaders.AUTHORIZATION, getToken(env))
        .perform { content ->
            val accountId = if (content == null) {
                null
            } else {
                JsonMapper.readTree(content.asBytes()).path("accountId").textValue()
            }
            handleCoinRevert(orderNo, accountId, env)
        }
}

private fun handleCoinRevert(orderNo: String, accountId: String?, env: Env) {
    val body = """{"accountId":"$accountId","businessId":$orderNo,"description":$orderNo}"""
    Request.Post("${getHost(env)}/v2/coin/cancel")
        .addHeader(HttpHeaders.AUTHORIZATION, getToken(env))
        .addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
        .body(StringEntity(body, Charsets.UTF_8))
        .handle() { response ->
            println(response.statusLine.statusCode)
            response.entity.content?.close()
            if (response is CloseableHttpResponse) {
                response.close()
            }
        }
}

class ContentCallback<T>(private val callback: suspend (content: Content?) -> T?) : FutureCallback<Content> {
    private val deferred = CompletableDeferred<T?>()

    suspend fun getResult(): T? {
        return deferred.await()
    }

    override fun cancelled() {
        deferred.cancel()
    }

    override fun completed(result: Content?) {
        CoroutineScope(context).launch {
            deferred.complete(callback.invoke(result))
        }
    }

    override fun failed(ex: Exception?) {
        CoroutineScope(context).launch {
            deferred.completeExceptionally(ex ?: java.lang.Exception("unexpected exception"))
        }
    }
}

enum class Env {
    DEV,
    INTERNAL
}
