package rainbow.weather

import java.text.SimpleDateFormat
import java.util.*

private val dataDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
fun String.toDataDate() = dataDateFormat.parse(this)!!
private val zhDayDateFormat = SimpleDateFormat("EEEEE", Locale.TAIWAN)
fun Date.toZhDay(): String = zhDayDateFormat.format(this)
private val zhDateDateFormat = SimpleDateFormat("MM/dd (EEEEE)", Locale.TAIWAN)
fun Date.toZhDate(): String = zhDateDateFormat.format(this)
private val timeDateFormat = SimpleDateFormat("hh:mm a", Locale.ROOT)
fun Date.toNowString() = "${this.toZhDate()} ${timeDateFormat.format(this)}"
private val hourDateFormat = SimpleDateFormat("H", Locale.ROOT)
val Date.hour get() = hourDateFormat.format(this).toInt()
private val alertDateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.ROOT)
fun Date.toAlertString(): String = alertDateFormat.format(this)
private val alertsDateFormat = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.ROOT)
fun Date.toAlertsString(): String = alertsDateFormat.format(this)
private val amPmHourDateFormat = SimpleDateFormat("ha", Locale.ROOT)
fun Date.toAmPmHourString(): String = amPmHourDateFormat.format(this)