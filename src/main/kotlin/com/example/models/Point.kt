package com.example.models

@kotlinx.serialization.Serializable
sealed class PacketData

@kotlinx.serialization.Serializable
data class Point(val id: Int, var x: Int, var y: Int) : PacketData()

@kotlinx.serialization.Serializable
data class Packet(
    val data: PacketData
)