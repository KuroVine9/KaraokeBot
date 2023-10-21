package util

import CHANNEL_PATH
import data.Channel
import kotlinx.serialization.json.Json

fun getChannels(): List<Channel> = Json.decodeFromString(readFile(CHANNEL_PATH))