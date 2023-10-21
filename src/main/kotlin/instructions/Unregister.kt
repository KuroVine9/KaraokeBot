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

fun unRegister(event: SlashCommandInteractionEvent) {
    event.deferReply().queue()

    val channelText = readFile(CHANNEL_PATH)
    println("parsed text = $channelText")
    val registeredChannel = Json.decodeFromString<List<Channel>>(channelText).toMutableList()

    val result: Boolean = if(event.isFromGuild) {
        registeredChannel.remove(Channel(false, event.messageChannel.idLong))
    }
    else {
        registeredChannel.remove(Channel(true, event.user.idLong))
    }

    val embed = EmbedBuilder()

    if (result) {
        writeFile(CHANNEL_PATH, Json.encodeToString(registeredChannel))
        embed.apply {
            setTitle("200 OK")
            setDescription("삭제 완료")
        }
    }
    else {
        embed.apply {
            setTitle("404 Not Found")
            setDescription("등록되지 않은 메시지 채널!")
            setColor(Color.RED)
        }
    }
    event.hook.sendMessageEmbeds(embed.build()).queue()
}