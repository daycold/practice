package com.practice.practice.tool.common

import com.practice.practice.tool.jk.JsonMapper
import java.lang.reflect.Field
import java.util.*
import kotlin.random.Random

/**
 * @author : liuwang
 * @date : 2020-11-05T15:19:45Z
 */
class Demo {
    var name: String = ""
    var age: Int = 0
}

class Demo2 {
    var demo: Demo? = null
    var name = ""
}

fun main() {
    val objectGenerator = ObjectGenerator(Demo2::class.java)
    println(JsonMapper.writeValueAsString(objectGenerator.generateValue()))
}

interface Generator<T> {
    fun generateValue(): T
}

class ObjectGenerator<T>(
    private val clazz: Class<T>,
    fieldMap: Map<String, Generator<*>> = emptyMap()
) : Generator<T> {
    private val filedFill: Map<Field, Generator<*>>

    init {
        val fields = clazz.declaredFields
        filedFill = mutableMapOf()
        fields.forEach {
            // todo:check array and collection
            val generator = fieldMap[it.name] ?: generatorMap[it.type]
            if (generator == null) {
                val newGenerator = ObjectGenerator(it.type)
                generatorMap[it.type] = newGenerator
                filedFill[it] = newGenerator
            } else {
                filedFill[it] = generator
            }
        }
    }

    override fun generateValue(): T {
        val obj: T = clazz.newInstance()
        filedFill.forEach {
            val field = it.key
            val generator = it.value
            field.isAccessible = true
            field.set(obj, generator.generateValue())
            field.isAccessible = false
        }
        return obj
    }
}

@Suppress("UNUSED")
inline fun <reified T> Generator<T>.generateArray(): Array<T> {
    return generateList().toTypedArray()
}

inline fun <reified T> Generator<T>.generateList(): List<T> {
    val len = Random.Default.nextInt(8) + 2
    val list = mutableListOf<T>()
    repeat(len) {
        list.add(generateValue())
    }
    return list
}

@Suppress("UNUSED")
inline fun <reified T> Generator<T>.generateSet(): Set<T> {
    val len = Random.Default.nextInt(8) + 2
    val list = mutableSetOf<T>()
    repeat(len) {
        list.add(generateValue())
    }
    return list
}

@Suppress("UNUSED")
inline fun <reified T> Generator<T>.generateCollection(): Collection<T> = generateList()

private val generatorMap = mutableMapOf<Class<*>, Generator<*>>(
    Byte::class.java to NumberGenerator.ByteGenerator,
    Char::class.java to NumberGenerator.CharGenerator,
    Short::class.java to NumberGenerator.ShortGenerator,
    Int::class.java to NumberGenerator.IntGenerator,
    Long::class.java to NumberGenerator.LongGenerator,
    Float::class.java to NumberGenerator.FloatGenerator,
    Double::class.java to NumberGenerator.DoubleGenerator,
    Boolean::class.java to BooleanGenerator,
    ByteArray::class.java to ByteArrayGenerator,
    CharArray::class.java to CharArrayGenerator,
    ShortArray::class.java to ShortArrayGenerator,
    IntArray::class.java to IntArrayGenerator,
    LongArray::class.java to LongArrayGenerator,
    FloatArray::class.java to FloatArrayGenerator,
    DoubleArray::class.java to DoubleArrayGenerator,
    BooleanArray::class.java to BooleanArrayGenerator,
    String::class.java to StringGenerator.Default,
    Date::class.java to DateGenerator,
    Any::class.java to StringGenerator.Default
)

@Suppress("UNUSED")
object BooleanGenerator : Generator<Boolean> {
    override fun generateValue(): Boolean {
        return Random.Default.nextBoolean()
    }
}

@Suppress("UNUSED")
object ByteArrayGenerator : Generator<ByteArray> {
    override fun generateValue(): ByteArray {
        val len = Random.Default.nextInt(58) + 8
        return ByteArray(len) { Random.nextInt(255).toByte() }
    }
}

@Suppress("UNUSED")
object CharArrayGenerator : Generator<CharArray> {
    override fun generateValue(): CharArray {
        val len = Random.Default.nextInt(24) + 8
        return CharArray(len) { Random.nextInt(255).toChar() }
    }
}

@Suppress("UNUSED")
object ShortArrayGenerator : Generator<ShortArray> {
    override fun generateValue(): ShortArray {
        val len = Random.Default.nextInt(12) + 4
        return ShortArray(len) { Random.nextInt().toShort() }
    }
}

@Suppress("UNUSED")
object IntArrayGenerator : Generator<IntArray> {
    override fun generateValue(): IntArray {
        val len = Random.Default.nextInt(4) + 4
        return IntArray(len) { Random.nextInt() }
    }
}

@Suppress("UNUSED")
object LongArrayGenerator : Generator<LongArray> {
    override fun generateValue(): LongArray {
        val len = Random.Default.nextInt(4) + 4
        return LongArray(len) { Random.Default.nextLong() }
    }
}

@Suppress("UNUSED")
object FloatArrayGenerator : Generator<FloatArray> {
    override fun generateValue(): FloatArray {
        val len = Random.Default.nextInt(4) + 4
        return FloatArray(len) { Random.Default.nextFloat() }
    }
}

@Suppress("UNUSED")
object DoubleArrayGenerator : Generator<DoubleArray> {
    override fun generateValue(): DoubleArray {
        val len = Random.Default.nextInt(4) + 4
        return DoubleArray(len) { Random.Default.nextDouble() }
    }
}

@Suppress("UNUSED")
object BooleanArrayGenerator : Generator<BooleanArray> {
    override fun generateValue(): BooleanArray {
        val len = Random.Default.nextInt(4) + 4
        return BooleanArray(len) { Random.Default.nextBoolean() }
    }
}

sealed class StringGenerator : Generator<String> {
    @Suppress("UNUSED")
    object Default : StringGenerator() {
        override fun generateValue(): String {
            val len = Random.Default.nextInt(16)
            val bytes = ByteArray(len) { Random.Default.nextInt(64).plus(1).toByte() }
            return String(Base64.getEncoder().encode(bytes))
        }
    }

    @Suppress("UNUSED")
    class Prefixed(private val prefix: String) : StringGenerator() {
        override fun generateValue(): String {
            return prefix + Random.Default.nextInt()
        }
    }
}

object DateGenerator : Generator<Date> {
    override fun generateValue(): Date {
        val mills = System.currentTimeMillis().plus(Random.Default.nextInt())
        return Date(mills)
    }
}

sealed class NumberGenerator<T : Any> : Generator<T> {
    @Suppress("UNUSED")
    object IntGenerator : NumberGenerator<Int>() {
        override fun generateValue(): Int {
            return Random.Default.nextInt()
        }
    }

    @Suppress("UNUSED")
    object LongGenerator : NumberGenerator<Long>() {
        override fun generateValue(): Long {
            return Random.Default.nextLong()
        }
    }

    @Suppress("UNUSED")
    object CharGenerator : NumberGenerator<Char>() {
        override fun generateValue(): Char {
            return Random.Default.nextInt().toChar()
        }
    }

    @Suppress("UNUSED")
    object ByteGenerator : NumberGenerator<Byte>() {
        override fun generateValue(): Byte {
            return Random.Default.nextInt().toByte()
        }
    }

    @Suppress("UNUSED")
    object ShortGenerator : NumberGenerator<Short>() {
        override fun generateValue(): Short {
            return Random.nextInt().toShort()
        }
    }

    @Suppress("UNUSED")
    object FloatGenerator : NumberGenerator<Float>() {
        override fun generateValue(): Float {
            return Random.Default.nextFloat()
        }
    }

    @Suppress("UNUSED")
    object DoubleGenerator : NumberGenerator<Double>() {
        override fun generateValue(): Double {
            return Random.Default.nextDouble()
        }
    }
}