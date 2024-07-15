package rainbow.weather.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
class LocationsScreen(private val viewModel: MainViewModel) : Screen {
    private lateinit var scope: CoroutineScope
    private val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    private var keyword by mutableStateOf("")
    private var locations = mutableStateListOf<Pair<String, String>>()
    private var viewing by mutableStateOf(true)
    private var county by mutableStateOf(viewModel.assetsRepository.counties.first())
    private var township by mutableStateOf(county.townships.first())

    init {
        viewModel.viewModelScope.launch {
            locations += viewModel.sharedPreferencesRepository.getLocations()
        }
    }

    private fun showSheet() {
        scope.launch {
            sheetState.show()
        }
    }

    private fun hideSheet() {
        scope.launch {
            sheetState.hide()
        }
    }

    private fun toggleViewing() {
        viewing = !viewing
    }

    private fun addLocation() {
        locations += county.name to township.name
        viewModel.viewModelScope.launch {
            viewModel.sharedPreferencesRepository.setLocations(locations)
        }
        hideSheet()
    }

    private fun removeLocation(location: Pair<String, String>) {
        locations -= location
        viewModel.viewModelScope.launch {
            viewModel.sharedPreferencesRepository.setLocations(locations)
        }
    }

    private fun selectLocation(location: Pair<String, String>) {
        viewModel.location = location
        viewModel.pop()
    }

    @Composable
    override fun invoke() {
        scope = rememberCoroutineScope()
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                Row {
                    TextButton(::hideSheet) {
                        Text("取消")
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(::addLocation) {
                        Text("確定")
                    }
                }
                Row(Modifier.height(160.dp)) {
                    Wheel(
                        items = viewModel.assetsRepository.counties,
                        onSelect = { county = it },
                        itemToString = { it.name },
                        modifier = Modifier.weight(1f)
                    )
                    Wheel(
                        items = county.townships,
                        onSelect = { township = it },
                        itemToString = { it.name },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        ) {
            Scaffold(topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(viewModel::pop) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    title = { Text("地點編輯") }
                )
            }) {
                Column(Modifier.padding(it)) {
                    TextField(
                        value = keyword,
                        onValueChange = ::keyword::set,
                        placeholder = { Text("搜尋") },
                        trailingIcon = {
                            IconButton({}) {
                                Icon(Icons.Default.Search, null)
                            }
                        }
                    )
                    Row {
                        Button(::showSheet) {
                            Icon(Icons.Default.Search, null)
                            Text("選擇鄉鎮")
                        }
                        Button({}) {
                            Icon(Icons.Default.Place, null)
                            Text("選擇育樂")
                        }
                    }
                    Row {
                        Text("我的最愛")
                        Spacer(Modifier.weight(1f))
                        TextButton(::toggleViewing) {
                            Text(if (viewing) "編輯" else "完成")
                        }
                    }
                    Divider()
                    LazyColumn {
                        item {
                            Surface({}) {
                                ListItem(
                                    icon = { Icon(Icons.Default.Place, null) },
                                    trailing = { Icon(Icons.Default.ArrowForward, null) }
                                ) {
                                    Text("重新定位")
                                }
                            }
                            Divider()
                        }
                        items(locations) {
                            Surface({ if (viewing) selectLocation(it) }) {
                                ListItem(
                                    icon = if (viewing) null else {
                                        {
                                            Button({ removeLocation(it) }) {
                                                Text("刪除")
                                            }
                                        }
                                    },
                                    trailing = {
                                        Icon(
                                            imageVector =
                                            if (viewing) Icons.Default.ArrowForward
                                            else Icons.Default.Menu,
                                            contentDescription = null
                                        )
                                    }
                                ) {
                                    Text("${it.first}${it.second}")
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun <T> Wheel(
        items: List<T>,
        onSelect: (T) -> Unit,
        modifier: Modifier = Modifier,
        itemToString: (T) -> String = { "$it" }
    ) {
        val state = rememberLazyListState()
        LaunchedEffect(items) {
            state.scrollToItem(0)
        }
        val item by remember(items) {
            derivedStateOf {
                items[state.firstVisibleItemIndex +
                        if (state.firstVisibleItemScrollOffset >= 90) 1 else 0]
            }
        }
        LazyColumn(
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(50.dp),
            modifier = modifier.drawWithContent {
                drawContent()
                drawPoints(
                    points = listOf(
                        Offset(10f, center.y - 50),
                        Offset(size.width - 10, center.y - 50),
                        Offset(10f, center.y + 50),
                        Offset(size.width - 10, center.y + 50)
                    ),
                    pointMode = PointMode.Lines,
                    color = Color.Gray
                )
            }
        ) {
            item {
                Text("")
            }
            items(items) {
                Text(itemToString(it))
            }
            item {
                Text("")
            }
        }
        LaunchedEffect(item) {
            onSelect(item)
        }
    }
}