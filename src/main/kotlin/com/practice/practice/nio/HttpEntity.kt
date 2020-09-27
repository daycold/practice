package com.practice.practice.nio

import java.io.InputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * @author Stefan Liu
 */
interface HttpEntity

class A(val socket: Socket) {
    fun doA() {
        socket.channel
    }
}

interface Result {
    fun isFinished(): Boolean
    fun getInputStream(): InputStream
}

fun doGet() {
    val serverSocket = ServerSocket(8665)
    val socket = serverSocket.accept()
    val inputStream = socket.getInputStream()
    var len: Int
    do {
        val byteArray = ByteArray(1024)
        len = inputStream.read(byteArray)
        println(String(byteArray))
    } while (len == 1024)
    socket.close()
    serverSocket.close()
}

fun main() {
    doubleTransit()
//    doGet()
}

fun nioGet() {
    val socketChannel = SocketChannel.open()
//    val serverChannel = ServerSocketChannel.open()
    socketChannel.configureBlocking(false)
    val socketAddress = InetSocketAddress("student-api.beta.demo.net", 80)
    socketChannel.connect(socketAddress)
    while (!socketChannel.isConnected) {
        println("waiting...")
        Thread.sleep(100)
    }
    val buffers = ByteBuffer.wrap("this is a world".toByteArray())
    socketChannel.write(buffers)
}

private class NioTest {
    private val socketChannel = SocketChannel.open().apply {
        configureBlocking(true)
    }
}

fun doubleTransit() {
    val socket = Socket("backend-service-account-api.beta.demo.net", 80)
    val body = """{"serviceName":"student-api","userType":"USER","username":"15216809700","password":"123456"}"""
    val os = """
POST /api/v1/login/app HTTP/1.1
authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1dHlwIjoiVVNFUiIsImF1ZCI6WyJzdHVkZW50LWFwaSIsImF3ai1hcGkiLCJhb2MiLCJhaS1nYWxheHkiLCJjb250ZW50LWFwaSJdLCJzdWIiOiI3ZGEyMWI2Ny1kODM5LTRmNWUtYTVkMC1iY2IyNTc0ZWU2MjUiLCJpc3MiOiJhbG83LmNvbSIsImV4cCI6MTU4Njk0MTM1NCwianRpIjoiOWEzMWQ2ZTYtNmJlYS00YjcxLTkyZjEtYWI5NjA3OWQ2NTkyIn0.f1RpXeNs7MY-d798zZ6RI___gG5zXHPvjIv-_2w1pXmDpa-I57jeAo1e9IKW_hXvY_chTyz2eLHt1dIXWkxAqHyTUW3BRgQN5GwEwpL9iw8IgYCCpYgdTz6u9ekE3REauXndpg6IyLlwlmccN4QHfRmKXT43ksy5ka-FBQ__A78
Content-Type: application/json
User-Agent: PostmanRuntime/7.22.0
Accept: */*
Cache-Control: no-cache
Postman-Token: d04ae105-918c-4776-bd24-d8c2f3204e72
Host: backend-service-account-api.beta.demo.net:80
Accept-Encoding: gzip, deflate, br
content-length: ${body.length}
Cookie: JSESSIONID=j5CU4JGHJ622Kd-oCifgGHasbnQ2gxLPUgedbvH6
Connection: keep-alive

$body
""".trimIndent()
    println(socket.localAddress.hostName)
    println(socket.localPort)
    if (socket.isConnected) {
        println("connected")
    }

    val out = socket.getOutputStream()
    out.write(os.toByteArray())
    println("send successfully")
    val inputStream = socket.getInputStream()
    while (true) {
        val byteArray = ByteArray(1024)
        val len = inputStream.read(byteArray)
        println(String(byteArray))
        if (len < 1024) break
    }
    println("shut down output")
    socket.shutdownOutput()
    println("shut down input")
    socket.shutdownInput()
    println("close output")
    out.close()
    println("close input")
    inputStream.close()
    println("close socket")
    socket.close()
}


/**
fun main() {
val url = ""
val result = nioRequest(url)
while(!result.isFinished()) {
Thread.sleep(100)
println("sleep...")
}
val inputStream = result.getInputStream()
val byteArray = ByteArray(2048)
inputStream.read(byteArray)
println(String(byteArray))
}
 */
