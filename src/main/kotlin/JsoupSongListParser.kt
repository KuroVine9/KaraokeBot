import data.SoupSong
import org.jsoup.Jsoup

fun parse(): List<SoupSong> {
    val document = Jsoup.connect(SOUP_URL).get()
    val table = document.getElementsByClass("board_type1")
        ?: throw IllegalStateException("Parsing Failed!!")

    return table.select("tr").next().map {
        val row = it.select("td")
        SoupSong(row[0].text().toInt(), row[1].text(), row[2].text())
    }
}