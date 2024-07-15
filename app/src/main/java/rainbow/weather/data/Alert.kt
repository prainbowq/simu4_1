package rainbow.weather.data

import java.util.Date

data class Alert(
    val description: String,
    val content: String,
    val issueTime: Date,
    val startTime: Date,
    val endTime: Date
)