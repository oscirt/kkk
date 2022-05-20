package com.example.plugins

import com.example.Connections
import com.example.models.Point
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val json = Json

    routing {
        val connections = ConcurrentHashMap<Int, Connections>()
        webSocket("/game") {
            val thisConnection = Connections(this)
            connections[thisConnection.point.id] = thisConnection
            try {
                send("You are connected! There are ${connections.size} user there.")
                val currentPlayerJson = json.encodeToString(
                    thisConnection.point
                )
                send(currentPlayerJson)
                connections.asSequence().filter {
                    it.key != thisConnection.point.id
                }.forEach {
                    it.value.session.send(currentPlayerJson)
                    send(json.encodeToString(
                        it.value.point
                    ))
                }
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            val packet = json.decodeFromStringOrNull<Point>(text)
                            if (packet == null) {
                                println("Got frame: $text")
                                continue
                            }
                            connections.asSequence().filter {
                                it.key != thisConnection.point.id
                            }.forEach {
                                it.value.session.send(text)
                            }
                        }
                        is Frame.Binary -> TODO()
                        is Frame.Close -> TODO()
                        is Frame.Ping -> TODO()
                        is Frame.Pong -> TODO()
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                connections.remove(thisConnection.point.id)
                println("Removing $thisConnection")
            }
        }
    }
}

inline fun <reified T> Json.decodeFromStringOrNull(string: String): T? {
    return try {
        decodeFromString(serializersModule.serializer(), string)
    } catch (e: Exception) {
        null
    }
}