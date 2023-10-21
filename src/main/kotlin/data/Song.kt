package data

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val brand: String,
    val no: String,
    val title: String,
    val singer: String,
    val composer: String,
    val lyricist: String,
    val release: String
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Song) return false

        return no == other.no && brand == other.brand
    }
}