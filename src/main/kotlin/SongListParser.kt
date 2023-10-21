import data.Song
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import util.getChannels
import util.readFile
import util.writeFile
import java.util.Timer
import java.util.TimerTask

fun songParse(jda: JDA) {
    val timer = Timer()

    val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiConnect::class.java)

    timer.schedule(object: TimerTask() {
        override fun run() {
            println("Start scheduled song list check!")

            api.getSongList().enqueue(object: Callback<List<Song>> {

                override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                    if(response.isSuccessful.not()) {
                        println("Api Call Responsed but Failed!!")
                        return
                    }

                    response.body()!!.let {
                        var songList = it
                        println("songList = $songList")

                        if(japanFilter) {
                            songList = songList.filter {
                                val num = it.no.toInt()
                                num in 6000..6999 || num in 25000..29999 || num in 68000..68999
                            }
                             println("filteredSongList = $songList")
                        }

                        val savedSongList = Json.decodeFromString<List<Song>>(readFile(SONG_LIST_PATH))
                        writeFile(SONG_LIST_PATH, Json.encodeToString(songList))

                        if(savedSongList == songList || songList.isEmpty()) {
                            println("Passing...: size=${songList.size}")
                            return
                        }

                        val embed = EmbedBuilder().apply {
                            setTitle("TJ 신곡 업데이트됨!")
                            songList.forEach { addField("[${it.no}] ${it.title}", "${it.singer}, ${it.release}", false) }
                        }.build()

                        jda.awaitReady()
                        for (channel in getChannels()) {
                            val channelObj = if(channel.isDM) {
                                jda.retrieveUserById(channel.channelId).complete().openPrivateChannel().complete()
                            }
                            else {
                                jda.getTextChannelById(channel.channelId)
                            }

                            if(channelObj == null) {
                                println("Channel Connect Fail!: $channel")
                                continue
                            }
                            if(!channelObj.canTalk()){
                                println("You have no Permission to write Msg!!")
                                continue
                            }

                            channelObj.sendMessageEmbeds(embed).queue()
                            println("Send List to: $channel")
                        }
                    }

                    println("Send Finished")
                }

                override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                    println("Api Call Failure!!")
                    println(t.message)
                }

            })
        }

    }, 5000L, 86400000L)
}

interface ApiConnect {
    @GET(API_JSON_URL)
    fun getSongList(): Call<List<Song>>
}