package com.practice.practice.data

import com.mongodb.MongoClient
import com.mongodb.client.model.Filters
import com.practice.practice.Mainable
import com.practice.practice.mq.RabbitMQ
import com.practice.practice.tool.jk.JsonMapper
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.*

/**
 * @author : liuwang
 * @date : 2020-09-10T14:20:33Z
 */
class Mongo : Mainable, Closeable {
    override fun doMain() {
        val startTime = Date()
        var lastUpdateTime = startTime
        val collection = mongoDatabase.getCollection("adEventInfo")
        while (true) {
            val result = collection.find(Filters.gte("createdDate", lastUpdateTime))
            val iterator = result.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                handleEventInfo(next)
            }
            lastUpdateTime = Date()
            Thread.sleep(5000)
        }
    }

    fun handleEventInfo(any: Any) {
        val json = JsonMapper.writeValueAsString(any)
        val adEventInfo = JsonMapper.readValue(json, AdEventInfo::class.java)
        log.info("处理事件: {}", json)
        handleEventInfo(adEventInfo)
    }

    fun handleEventInfo(event: AdEventInfo) {
        rabbitMQ.send(DEVICE_TOPIC, JsonMapper.writeValueAsString(DeviceActiveVO().apply {
            os = event.os
            deviceId = event.deviceId
            eventType = "active"
        }))
        Thread.sleep(1000)
        rabbitMQ.send(DEVICE_TOPIC, JsonMapper.writeValueAsString(DeviceActiveVO().apply {
            os = event.os
            deviceId = event.deviceId
            accountId = "1F908229-7A37-413F-8279-2246490EE10B"
            eventType = "register"
        }))
    }

        private val client = MongoClient("mongodb.tst.hello.com", 27017)
//    private val client = MongoClient("localhost", 27017)
    val mongoDatabase = client.getDatabase("open-ad")
    private val rabbitMQ = RabbitMQ()
    private val DEVICE_TOPIC = "open_ad_callback_event"
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun close() {
        client.close()
        rabbitMQ.close()
    }
}

class AdEventInfo {
    var id = ""
    var source: String = ""
    var deviceId = ""
    var os = ""
    var detail = mapOf<String, String>()
}

class DeviceActiveVO {
    var deviceId: String = ""
    var eventType: String = ""
    var accountId: String? = null
    var os: String? = null
}

fun main() {
//    Mongo().use { mongo ->
//        val mongoDatabase = mongo.mongoDatabase
//        val startTime = Date(1599696000000)
//        val collection = mongoDatabase.getCollection("adEventInfo")
//        val result = collection.find(Filters.gte("createdDate", startTime))
//        val iterator = result.iterator()
//        while (iterator.hasNext()) {
//            val next = iterator.next()
//            mongo.handleEventInfo(next)
//        }
//    }
    Mongo().use {
        it.doMain()
    }
}
