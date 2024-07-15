package rainbow.weather.data

import java.util.Date

data class ShortForecast(
    val date: Date,
    val t: Int,
    val at: Int,
    val wx: Pair<String, String>,
    val pop: Int,
    val rh: Int,
    val wd: String,
    val ws: String
)