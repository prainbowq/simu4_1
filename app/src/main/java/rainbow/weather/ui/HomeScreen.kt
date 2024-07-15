package rainbow.weather.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rainbow.weather.data.LongForecast
import rainbow.weather.data.ShortForecast
import rainbow.weather.toNowString
import rainbow.weather.toZhDay
import java.util.Calendar
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterialApi::class)
class HomeScreen(private val viewModel: MainViewModel) : Screen {
    private lateinit var scope: CoroutineScope
    private val dateString = Calendar.getInstance().time.toNowString()
    private val scaffoldState = ScaffoldState(DrawerState(DrawerValue.Closed), SnackbarHostState())
    private var selectedTabIndex by mutableStateOf(0)
    private var shortForecasts by mutableStateOf<List<ShortForecast>?>(null)
    private var longForecasts by mutableStateOf<List<LongForecast>?>(null)
    private var radar by mutableStateOf<ImageBitmap?>(null)
    private var satilie by mutableStateOf<ImageBitmap?>(null)
    private var rain by mutableStateOf<ImageBitmap?>(null)
    private var temperature by mutableStateOf<ImageBitmap?>(null)
    private var uv by mutableStateOf<ImageBitmap?>(null)

    init {
        viewModel.viewModelScope.launch {
            shortForecasts = viewModel.weatherRepository.getShortForecasts(viewModel.location)
            longForecasts = viewModel.weatherRepository.getLongForecasts(viewModel.location)
            radar = viewModel.weatherRepository.getImage(
                "https://cwaopendata.s3.ap-northeast-1.amazonaws.com/Observation/O-A0058-003.png"
            )
            satilie = viewModel.weatherRepository.getImage(
                "https://www.cwa.gov.tw/Data/satellite/LCC_VIS_TRGB_2750/LCC_VIS_TRGB_2750.jpg"
            )
            rain = viewModel.weatherRepository.getImage(
                "https://cwaopendata.s3.ap-northeast-1.amazonaws.com/Observation/O-A0040-002.jpg"
            )
            temperature = viewModel.weatherRepository.getImage(
                "https://cwaopendata.s3.ap-northeast-1.amazonaws.com/Observation/O-A0038-001.jpg"
            )
            uv = viewModel.weatherRepository.getImage(
                "https://www.cwa.gov.tw/Data/UVI/UVI_CWB.png"
            )
        }
    }

    private fun openDrawer() {
        scope.launch {
            scaffoldState.drawerState.open()
        }
    }

    private fun closeDrawer() {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }

    @Composable
    override fun invoke() {
        scope = rememberCoroutineScope()
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(::openDrawer) {
                            Icon(Icons.Default.Menu, null)
                        }
                    },
                    title = { Text("${viewModel.location.first}${viewModel.location.second}") },
                    actions = {
                        IconButton({ viewModel.push(LocationsScreen(viewModel)) }) {
                            Icon(Icons.Default.Place, null)
                        }
                        IconButton({ viewModel.push(AlertsScreen(viewModel)) }) {
                            Icon(Icons.Default.Warning, null)
                        }
                    }
                )
            },
            drawerContent = {
                Surface(::closeDrawer) {
                    ListItem(icon = { Icon(Icons.Default.Home, null) }) {
                        Text("首頁")
                    }
                }
            }
        ) {
            Column(Modifier.padding(it)) {
                TabRow(selectedTabIndex) {
                    listOf("現在", "預報", "觀測").forEachIndexed { index, text ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text) }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> NowPage()
                    1 -> ForecastPage()
                    2 -> ObservationPage()
                }
            }
        }
    }

    @Composable
    private fun NowPage() {
        longForecasts?.run {
            Column {
                WxIcon(first().wx.first, first().date)
                Text("${first().t}°C")
                Text(first().wx.second)
                Text("${first().at}°C")
                Text(dateString)
                Spacer(Modifier.weight(1f))
                Row {
                    groupBy { it.date.toZhDay() }.forEach { (day, longForecasts) ->
                        Column {
                            Text(day)
                            WxIcon(
                                id = longForecasts.first().wx.first,
                                date = longForecasts.first().date,
                                modifier = Modifier.size(30.dp)
                            )
                            Text("${max(longForecasts.first().maxT, longForecasts.last().maxT)}°")
                            Text("${min(longForecasts.first().minT, longForecasts.last().minT)}°")
                        }
                    }
                }
            }
        } ?: CircularProgressIndicator()
    }

    @Composable
    private fun ForecastPage() {
        Column {
            Text("逐3小時預報")
            Surface({
                shortForecasts?.let { viewModel.push(ShortForecastsScreen(viewModel, it)) }
            }) {
                Row {
                    Text("逐3小時詳細預報")
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
            Surface({
                longForecasts?.let { viewModel.push(LongForecastsScreen(viewModel, it)) }
            }) {
                Row {
                    Text("一週詳細預報")
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
        }
    }

    @Composable
    private fun ObservationPage() {
        LazyColumn {
            item {
                Column {
                    Text("雷達回波")
                    radar?.let {
                        Image(it, null)
                    } ?: CircularProgressIndicator()
                }
            }
            item {
                Column {
                    Text("衛星雲圖")
                    satilie?.let {
                        Image(it, null)
                    } ?: CircularProgressIndicator()
                }
            }
            item {
                Column {
                    Text("累積雨量圖")
                    rain?.let {
                        Image(it, null)
                    } ?: CircularProgressIndicator()
                }
            }
            item {
                Column {
                    Text("溫度分布圖")
                    temperature?.let {
                        Image(it, null)
                    } ?: CircularProgressIndicator()
                }
            }
            item {
                Column {
                    Text("紫外線測報")
                    uv?.let {
                        Image(it, null)
                    } ?: CircularProgressIndicator()
                }
            }
        }
    }
}