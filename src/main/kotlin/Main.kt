import instructions.register
import instructions.unRegister
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.build.Commands
import java.io.File
import java.io.IOException
import java.util.*

const val CHANNEL_PATH = "resources/channels.json"
const val TOKEN_PATH = "resources/token.txt"
const val SONG_LIST_PATH = "resources/songdata.json"
const val API_BASE_URL = "https://api.manana.kr/karaoke/"
const val API_JSON_URL = "tj.json"
const val SOUP_URL = "https://m.tjmedia.com/tjsong/song_monthNew.asp"

class Main : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.fullCommandName) {
            "ping" -> {
                val time = System.currentTimeMillis()
                event.reply("Pong!").setEphemeral(true)
                    .flatMap { _: InteractionHook? ->
                        event.hook.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
                    }.queue()
            }

            "register" -> register(event)
            "unregister" -> unRegister(event)
            else -> throw IllegalStateException("Unhandled Interaction!")
        }
    }
}

fun main() {
    val token: String
    try {
        val scan: Scanner = Scanner(File(TOKEN_PATH))
        token = scan.next()
        scan.close()
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
    println("TOKEN=$token")
    val JDA = JDABuilder.createDefault(token).build().apply {
        presence.setStatus(OnlineStatus.DO_NOT_DISTURB)
        presence.activity = Activity.competing("matonyabi")
        addEventListener(Main())
    }

    JDA.updateCommands().apply {
        addCommands(Commands.slash("ping", "calc ping time of the bot"))
        addCommands(Commands.slash("register", "이 채널에서 업데이트 정보를 받습니다."))
        addCommands(Commands.slash("unregister", "더이상 업데이트 정보를 받지 않습니다."))
    }.queue()

    songParse(JDA)
}