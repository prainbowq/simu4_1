package rainbow.weather.data

import java.util.Date

data class LongForecast(
    val date: Date,
    val t: Int,
    val maxT: Int,
    val minT: Int,
    val maxAt: Int,
    val minAt: Int,
    val wx: Pair<String, String>,
    val pop: Int?,
    val rh: Int,
    val uvi: Pair<Int, String>
) {
    val at get() = (maxAt + minAt) / 2
}