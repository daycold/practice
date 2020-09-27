package com.practice.practice.request

import com.practice.practice.Mainable
import io.undertow.Undertow
import io.undertow.server.HandlerWrapper
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.BlockingHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * @author Stefan Liu
 */
class Undertow(private val handler: HttpHandler = UndertowHandler()) : Mainable {
    override fun doMain() {
        Undertow.builder().addHttpListener(8080, "127.0.0.1")
            .setHandler(BlockingHandler(handler)).build().start()
    }

    private class UndertowHandler : HttpHandler {
        override fun handleRequest(exchange: HttpServerExchange) {
            exchange.statusCode = 200
            exchange.inputStream.use { stream ->
                exchange.outputStream.use { out ->
//                    stream.transferTo(out)
                }
            }
        }
    }
}

private val CONTEXT: CoroutineDispatcher = Executors.newFixedThreadPool(5).asCoroutineDispatcher()

private class CoroutineWrapper(private val scope: CoroutineScope) : HandlerWrapper {
    override fun wrap(handler: HttpHandler): HttpHandler {
        return HttpHandler { exchange ->
            scope.launch {
                handler.handleRequest(exchange)
            }
        }
    }
}