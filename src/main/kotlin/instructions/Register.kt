package instructions

import CHANNEL_PATH
import data.Channel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import util.readFile
import util.writeFile
import java.awt.Color

fun register(event: SlashCommandInteractionEvent) {
    event.deferReply().queue()

    val channelText = readFile(CHANNEL_PATH)
    println("parsed text = $channelText")
    val registeredChannel = Json.decodeFromString<List<Channel>>(channelText).toMutableList()

    if(event.messageChannel.idLong in registeredChannel.map{it.channelId}) {
        val embed = EmbedBuilder().apply {
            setTitle("409 Conflict")
            setDescription("이미 등록된 채널입니다.")
            setColor(Color.RED)
        }.build()

        event.hook.sendMessageEmbeds(embed).queue()
        return
    }

    if(event.isFromGuild) {
        registeredChannel.add(Channel(false, event.messageChannel.idLong))
    }
    else {
        registeredChannel.add(Channel(true, event.user.idLong))
    }

    writeFile(CHANNEL_PATH, Json.encodeToString(registeredChannel))

    val embed = EmbedBuilder().apply {
        setTitle("200 OK")
        setDescription("등록 완료")
    }.build()

    event.hook.sendMessageEmbeds(embed).queue()
}