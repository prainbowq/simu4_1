package rainbow.weather.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rainbow.weather.data.Alert
import rainbow.weather.toAmPmHourString

@OptIn(ExperimentalMaterialApi::class)
class AlertsScreen(private val viewModel: MainViewModel) : Screen {
    private var alerts by mutableStateOf<List<Alert>?>(null)

    init {
        viewModel.viewModelScope.launch {
            alerts = viewModel.weatherRepository.getAlerts()
        }
    }

    @Composable
    override fun invoke() {
        Scaffold(topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(viewModel::pop) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                title = { Text("警特報列表") }
            )
        }) {
            if (alerts != null) LazyColumn(Modifier.padding(it)) {
                items(alerts!!) {
                    ListItem(
                        icon = { Icon(Icons.Default.Warning, null) },
                        secondaryText = { Text(it.issueTime.toAmPmHourString()) },
                        trailing = { Icon(Icons.Default.ArrowForward, null) }
                    ) {
                        Text(it.description)
                    }
                }
            }
        }
    }
}