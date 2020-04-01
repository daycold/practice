package com.practice.practice.callback

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author Stefan Liu
 */
class CallBack {
    private var function = Function<Payload, Payload> { it }
    private val callbacks = mutableListOf<Consumer<Payload>>()

    fun addSupplier(supplier: Function<Payload, Payload>): CallBack {
        function = function.andThen(supplier)
        return this
    }

    fun addCallback(consumer: Consumer<Payload>): CallBack {
        callbacks.add(consumer)
        return this
    }

    fun doAction() {
        val result = TransactionHelper<Payload>()
            .runTransaction {
                function.apply(Payload.EMPTY_PAYLOAD)
            }
        callbacks.forEach { it.accept(result) }
    }
}

interface Payload {
    fun <T : Any> getParam(clazz: Class<T>): T?

    companion object {
        val EMPTY_PAYLOAD = object : Payload {
            override fun <T : Any> getParam(clazz: Class<T>): T? {
                return null
            }
        }
    }
}


@FunctionalInterface
interface ThrowableSupplier<T> : Supplier<T> {
    @Throws(Exception::class)
    override fun get(): T
}

class TransactionHelper<T> {
    fun runTransaction(supplier: () -> T): T {
        return supplier()
    }
}
