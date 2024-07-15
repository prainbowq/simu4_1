package rainbow.weather.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import rainbow.weather.data.LongForecast
import rainbow.weather.hour
import rainbow.weather.toZhDate

class LongForecastsScreen(
    private val viewModel: MainViewModel,
    private val longForecasts: List<LongForecast>
) : Screen {
    @Composable
    override fun invoke() {
        Scaffold(topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(viewModel::pop) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                title = { Text("${viewModel.location.first}${viewModel.location.second}") },
                actions = {
                    IconButton({}) {
                        Icon(Icons.Default.Share, null)
                    }
                }
            )
        }) {
            LazyColumn(Modifier.padding(it)) {
                item {
                    Text("一週天氣預報")
                }
                longForecasts
                    .groupBy { it.date.toZhDate() }
                    .forEach { (dateString, longForecasts) ->
                        items(longForecasts) {
                            Surface(color = Color(0xFF325E94), contentColor = Color.White) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Column {
                                        Text(dateString)
                                        Text(buildAnnotatedString {
                                            append("${it.minT} / ${it.maxT}°C ")
                                            val day = it.date.hour in 6 until 18
                                            withStyle(
                                                SpanStyle(
                                                    color = if (day) Color.Yellow
                                                    else Color.Cyan
                                                )
                                            ) {
                                                append(if (day) "白天" else "晚上")
                                            }
                                        })
                                        Row {
                                            WxIcon(
                                                id = it.wx.first,
                                                date = it.date,
                                                modifier = Modifier.size(30.dp)
                                            )
                                            Text(it.wx.second)
                                        }
                                        Text("體感 ${it.minAt} / ${it.maxAt}°C")
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Column {
                                        if (it.pop != null) Text("降雨機率 ${it.pop}%")
                                        Text("相對濕度 ${it.rh}%")
                                        if (it.date.hour in 6 until 18) Text("UV指數 ${it.uvi.first}")
                                    }
                                }
                            }
                            Divider()
                        }
                        item {
                            Spacer(Modifier.height(10.dp))
                        }
                    }
            }
        }
    }
}