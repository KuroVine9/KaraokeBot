package data

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val isDM: Boolean,
    val channelId: Long
)