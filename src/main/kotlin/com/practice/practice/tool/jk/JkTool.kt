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
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author : liuwang
 * @date : 2020-08-19T17:18:34Z
 */
class JkTool : Mainable {
    override fun doMain() {
//        returnCoin(orderNo = "1120180124833000282")
//        getCombinationInfo("bearer b1d926b7-f535-4d81-a63c-443d4593cc07")
//        handleCombinationInfoFiles("bearer b1d926b7-f535-4d81-a63c-443d4593cc07")
        runBlocking {
            println(checkSkuConnection(220793, "bearer 313dad49-30ff-4ab6-be1d-91c5e4a9572a"))
        }
        close()
    }

    private fun returnCoin(orderNo: String) {
//        revertCoin(orderNo)
        runBlocking {
//            handleAccountId(orderNo)
            revertCoinRequest(orderNo)
        }
    }

    private fun getCombinationInfo(token: String) = runBlocking {
        val result = getCombinationActivities(token, 6, 0, 100).map { getCombinationInfo(it, token) }.flatten()
            .distinctBy { it.id }
        println("combinationIds[${result.size}]: ")
        println(JsonMapper.writeValueAsString(result.map { it.id }))
        val codes = result.map { it.skuCode }.flatten().distinct()
        println("skuCodes[${codes.size}]: ")
//        val file = File("code.txt")
//        file.createNewFile()
//        val builder = StringBuilder()
//        codes.forEach { code -> builder.append("promo-service:combination:skucode:").append(code).append("\r\n") }
//        val text = builder.toString()
//        file.writeText(text)
        println(JsonMapper.writeValueAsString(codes))
        println("combinations: ")
        println(JsonMapper.writeValueAsString(result))
    }

    private fun handleCombinationInfoFiles(token: String) = runBlocking {
        val supposed = File("code.txt")
        val supposedSkuCodes =
            supposed.readLines().map { it.substring("promo-service:combination:skucode:".length).toInt() }
        val actual = File("promo_service.txt")
        val actualSkuCodes =
            actual.readLines().map { it.substring("promo-service:combination:skucode:".length).toInt() }
        val expected = supposedSkuCodes.toMutableList()
        val unexpected = actualSkuCodes.toMutableList()
        for (skuCode in actualSkuCodes) {
            expected.remove(skuCode)
        }
        for (skuCode in supposedSkuCodes) {
            unexpected.remove(skuCode)
        }
        println(JsonMapper.writeValueAsString(expected))
        println(JsonMapper.writeValueAsString(unexpected))
        expected.forEach {
            if (checkSkuConnection(it, token)) {
                println(it)
            }
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

 suspend fun <T> Request.perform(name: String = "client", client: Async, callback: suspend (content: Content?) -> T?): T? {
     val job = ContentCallback(callback, name)
     client.execute(this, job)
     return job.getResult()
 }

private suspend fun <T> Request.perform(name: String = "client", callback: suspend (content: Content?) -> T?): T? {
    return perform(name, http, callback)
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

private suspend fun getCombinationActivities(token: String, status: Int = 6, page: Int = 0, size: Int = 10): List<Int> {
    return Request.Get("http://mgmt-gateway.internal.jianke.com/promo/combination/v2?status=$status&page=$page&size=$size")
        .addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
        .addHeader(HttpHeaders.AUTHORIZATION, token)
        .perform("getCombinationActivities") { content ->
            val node = JsonMapper.readTree(content!!.asBytes())
            node.path("content").map { it.path("id").numberValue().toInt() }
        }!!
}

private suspend fun getCombinationInfo(activityId: Int, token: String): List<CombinationInfo> {
    return Request.Get("http://mgmt-gateway.internal.jianke.com/promo/combination/v2/$activityId")
        .addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
        .addHeader(HttpHeaders.AUTHORIZATION, token)
        .perform("getCombinationInfo:$activityId") { content ->
            val node = JsonMapper.readTree(content!!.asBytes())
            val combinations = mutableListOf<CombinationInfo>()
            node.path("promoCombinationInfos").forEach { combinationInfo ->
                val combinationId = combinationInfo.path("id").numberValue().toInt()
                val skuCodes = mutableListOf<Int>()
                combinationInfo.path("combinationSkus").forEach { sku ->
                    if (sku.path("showInDetailPage").numberValue() == 1) {
                        skuCodes.add(sku.path("skuCode").numberValue().toInt())
                    }
                }
                combinations.add(CombinationInfo(combinationId, skuCodes))
            }
            combinations
        }!!
}

private suspend fun checkSkuConnection(skuCode: Int, token: String): Boolean {
    return Request.Get("http://mgmt-gateway.internal.jianke.com/v1/product/saleBody/connection?pageNum=1&productCode=$skuCode")
        .addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
        .addHeader(HttpHeaders.AUTHORIZATION, token)
        .perform("checkSkuConnection") { content ->
            val node = JsonMapper.readTree(content!!.asBytes())
            node.path("data").path("content").size() == 0
        }!!
}

private data class CombinationInfo(
    val id: Int,
    val skuCode: List<Int>
)

class ContentCallback<T>(
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
        println("${Thread.currentThread().name} $name executes ${System.currentTimeMillis() - mills} ms")
        return callback.invoke(result)
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
