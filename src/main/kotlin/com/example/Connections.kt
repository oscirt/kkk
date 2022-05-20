package com.example

import com.example.models.Point
import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.random.nextInt

class Connections(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
    val point = Point(
        lastId.toInt(),
        Random.nextInt(0..512),
        Random.nextInt(0..512)
    )
}