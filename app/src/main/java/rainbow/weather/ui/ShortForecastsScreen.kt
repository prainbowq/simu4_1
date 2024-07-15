package rainbow.weather.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import rainbow.weather.data.ShortForecast
import rainbow.weather.toAmPmHourString
import rainbow.weather.toZhDate

class ShortForecastsScreen(
    private val viewModel: MainViewModel,
    private val shortForecasts: List<ShortForecast>
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
                    Text("逐3小時預報")
                }
                shortForecasts
                    .groupBy { it.date.toZhDate() }
                    .forEach { (dateString, shortForecasts) ->
                        itemsIndexed(shortForecasts) { index, shortForecast ->
                            Surface(color = Color(0xFF325E94), contentColor = Color.White) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Column {
                                        if (index == 0) Text(dateString)
                                        Text(
                                            text = shortForecast.date.toAmPmHourString(),
                                            color = Color.Yellow
                                        )
                                        Text("${shortForecast.t}°C")
                                        Row {
                                            WxIcon(
                                                id = shortForecast.wx.first,
                                                date = shortForecast.date,
                                                modifier = Modifier.size(30.dp)
                                            )
                                            Text(shortForecast.wx.second)
                                        }
                                        Text("體感 ${shortForecast.at}°C")
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Column {
                                        Text("降雨機率 ${shortForecast.pop}%")
                                        Text("相對濕度 ${shortForecast.rh}%")
                                        Text("${shortForecast.wd} ${shortForecast.ws} 級")
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