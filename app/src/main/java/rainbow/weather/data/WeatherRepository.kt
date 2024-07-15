package rainbow.weather.data

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import rainbow.weather.toDataDate
import java.text.SimpleDateFormat
import java.util.*

class WeatherRepository(private val assetsRepository: AssetsRepository) {
    private val okHttp = OkHttpClient()

    suspend fun getShortForecasts(location: Pair<String, String>) = withContext(Dispatchers.IO) {
        val id = assetsRepository.counties.first { it.name == location.first }.shortId
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("opendata.cwa.gov.tw")
            .addPathSegments("api/v1/rest/datastore/$id")
            .addQueryParameter("Authorization", "CWA-6AA2EEEE-CCFD-4B33-9E9F-433C8128499D")
            .addQueryParameter("format", "XML")
            .addQueryParameter("locationName", "大安區")
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        val reader = okHttp.newCall(request).execute().body!!.charStream()
        XmlPullParserFactory.newInstance().newPullParser().run {
            setInput(reader)
            val dates = mutableListOf<Date>()
            var tList = emptyList<Int>()
            var atList = emptyList<Int>()
            var wxList = emptyList<Pair<String, String>>()
            var popList = emptyList<Int>()
            var rhList = emptyList<Int>()
            var wdList = emptyList<String>()
            var wsList = emptyList<String>()
            var elementName = ""
            val values = mutableListOf<String>()
            while (true) {
                when (eventType) {
                    XmlPullParser.START_TAG -> when (name) {
                        "dataTime" ->
                            if (elementName == "T") dates += nextText().toDataDate()
                            else next()
                        "elementName" -> elementName = nextText()
                        "value" -> values += nextText()
                        else -> next()
                    }
                    XmlPullParser.END_TAG -> {
                        when (name) {
                            "weatherElement" -> {
                                when (elementName) {
                                    "T" -> tList = values.map { it.toInt() }
                                    "AT" -> atList = values.map { it.toInt() }
                                    "Wx" -> wxList = values.chunked(2) { it.last() to it.first() }
                                    "PoP6h" -> popList = values
                                        .map { it.toInt() }
                                        .flatMap { pop -> List(2) { pop } }
                                    "RH" -> rhList = values.map { it.toInt() }
                                    "WD" -> wdList = values.toList()
                                    "WS" -> wsList = values.toList()
                                }
                                println("$elementName(${values.size}): $values")
                                values.clear()
                            }
                        }
                        next()
                    }
                    XmlPullParser.END_DOCUMENT -> break
                    else -> next()
                }
            }
            reader.close()
            List(dates.size) {
                ShortForecast(
                    date = dates[it],
                    t = tList[it],
                    at = atList[it],
                    wx = wxList[it],
                    pop = popList[it],
                    rh = rhList[it],
                    wd = wdList[it],
                    ws = wsList[it]
                )
            }
        }
    }

    suspend fun getLongForecasts(location: Pair<String, String>) = withContext(Dispatchers.IO) {
        val id = assetsRepository.counties.first { it.name == location.first }.longId
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("opendata.cwa.gov.tw")
            .addPathSegments("api/v1/rest/datastore/$id")
            .addQueryParameter("Authorization", "CWA-6AA2EEEE-CCFD-4B33-9E9F-433C8128499D")
            .addQueryParameter("format", "XML")
            .addQueryParameter("locationName", "大安區")
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        val reader = okHttp.newCall(request).execute().body!!.charStream()
        XmlPullParserFactory.newInstance().newPullParser().run {
            setInput(reader)
            val dates = mutableListOf<Date>()
            var tList = emptyList<Int>()
            var maxTList = emptyList<Int>()
            var minTList = emptyList<Int>()
            var maxAtList = emptyList<Int>()
            var minAtList = emptyList<Int>()
            var wxList = emptyList<Pair<String, String>>()
            var popList = emptyList<Int?>()
            var rhList = emptyList<Int>()
            val uviList = mutableListOf(-1 to "")
            var elementName = ""
            val values = mutableListOf<String>()
            while (true) {
                when (eventType) {
                    XmlPullParser.START_TAG -> when (name) {
                        "startTime" ->
                            if (elementName == "T") dates += nextText().toDataDate()
                            else next()
                        "elementName" -> elementName = nextText()
                        "value" -> values += nextText()
                        else -> next()
                    }
                    XmlPullParser.END_TAG -> {
                        when (name) {
                            "weatherElement" -> {
                                when (elementName) {
                                    "T" -> tList = values.map { it.toInt() }
                                    "MaxT" -> maxTList = values.map { it.toInt() }
                                    "MinT" -> minTList = values.map { it.toInt() }
                                    "MaxAT" -> maxAtList = values.map { it.toInt() }
                                    "MinAT" -> minAtList = values.map { it.toInt() }
                                    "Wx" -> wxList = values.chunked(2) { it.last() to it.first() }
                                    "PoP12h" -> popList = values.map { it.toIntOrNull() }
                                    "RH" -> rhList = values.map { it.toInt() }
                                    "UVI" -> uviList += values
                                        .chunked(2) { it.first().toInt() to it.last() }
                                        .flatMap { uvi -> List(2) { uvi } }
                                }
                                values.clear()
                            }
                        }
                        next()
                    }
                    XmlPullParser.END_DOCUMENT -> break
                    else -> next()
                }
            }
            reader.close()
            List(dates.size) {
                LongForecast(
                    date = dates[it],
                    t = tList[it],
                    maxT = maxTList[it],
                    minT = minTList[it],
                    maxAt = maxAtList[it],
                    minAt = minAtList[it],
                    wx = wxList[it],
                    pop = popList[it],
                    rh = rhList[it],
                    uvi = uviList[it]
                )
            }
        }
    }

    suspend fun getAlerts(): List<Alert> = withContext(Dispatchers.IO) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("opendata.cwa.gov.tw")
            .addPathSegments("api/v1/rest/datastore/W-C0033-002")
            .addQueryParameter("Authorization", "CWA-6AA2EEEE-CCFD-4B33-9E9F-433C8128499D")
            .addQueryParameter("format", "XML")
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        val reader = okHttp.newCall(request).execute().body!!.charStream()
        XmlPullParserFactory.newInstance().newPullParser().run {
            setInput(reader)
            val alerts = mutableListOf<Alert>()
            var alert = Alert("", "", Date(), Date(), Date())
            while (true) {
                when (eventType) {
                    XmlPullParser.START_TAG -> when (name) {
                        "datasetDescription" -> alert = alert.copy(description = nextText())
                        "contentText" -> alert = alert.copy(content = nextText().trim())
                        "issueTime" -> alert = alert.copy(issueTime = nextText().toDataDate())
                        "startTime" -> alert = alert.copy(startTime = nextText().toDataDate())
                        "endTime" -> alert = alert.copy(endTime = nextText().toDataDate())
                        else -> next()
                    }
                    XmlPullParser.END_TAG -> {
                        when (name) {
                            "record" -> alerts += alert
                        }
                        next()
                    }
                    XmlPullParser.END_DOCUMENT -> break
                    else -> next()
                }
            }
            reader.close()
            alerts
        }
    }

    suspend fun getImage(url: String) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()
        okHttp
            .newCall(request)
            .execute()
            .body!!
            .bytes()
            .let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            .asImageBitmap()
    }
}