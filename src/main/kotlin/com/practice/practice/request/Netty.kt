package com.practice.practice.request

import com.practice.practice.Logger
import com.practice.practice.Mainable
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.*
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.SslHandler
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import kotlin.jvm.Throws

/**
 * @author Stefan Liu
 */
class Netty : Mainable {
    override fun doMain() {
        val port = 8080
        HttpServer(port).start()
    }

    private class HttpServer(private val port: Int) {
        @Throws(Exception::class)
        fun start() {
            val bootstrap = ServerBootstrap()
            val group = NioEventLoopGroup()
            bootstrap.group(group).channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline()
                            .addLast("decoder", HttpRequestDecoder())
                            .addLast("encoder", HttpResponseEncoder())
                            .addLast("aggregator", HttpObjectAggregator(512 * 1024))
                            .addLast("handler", HttpHandler())
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
            bootstrap.bind(port).sync()
        }
    }

    private class HttpHandler : SimpleChannelInboundHandler<FullHttpRequest>() {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
            val content = msg.content()
            val data = content.readBytes(content.readableBytes())
            val response = DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(data)
            )
            response.headers().apply {
                add(HttpHeaderNames.CONTENT_TYPE, "${HttpHeaderValues.TEXT_PLAIN}; charset=UTF-8")
                add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes())
                add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
            }
            ctx.write(response)
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
            if (cause != null) {
                log.info(cause.message)
            }
            ctx?.close()
        }

        override fun channelReadComplete(ctx: ChannelHandlerContext?) {
            super.channelReadComplete(ctx)
            ctx?.flush()
        }

        companion object : Logger()
    }

    private class SSLChannelInitializer: ChannelInitializer<SocketChannel>() {
        private val keystoreFilePath = ""
        private val keystorePassword = ""
        private val sslContext: SslContext

        init {
            val keystore = KeyStore.getInstance("PKCS12")
            keystore.load(FileInputStream(keystoreFilePath), keystorePassword.toCharArray())
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keystore, keystorePassword.toCharArray())
            sslContext = SslContextBuilder.forServer(keyManagerFactory).build()
        }

        override fun initChannel(ch: SocketChannel) {
            val pipeline = ch.pipeline()
            val sslEngine = sslContext.newEngine(ch.alloc())
            pipeline.addLast(SslHandler(sslEngine))
                .addLast("decoder", HttpRequestDecoder())
                .addLast("encoder", HttpResponseEncoder())
                .addLast("aggregator", HttpObjectAggregator(215 * 1024))
                .addLast("handler", HttpHandler())
        }

    }
}
