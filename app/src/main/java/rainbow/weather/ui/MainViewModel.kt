package rainbow.weather.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import rainbow.weather.data.AssetsRepository
import rainbow.weather.data.SharedPreferencesRepository
import rainbow.weather.data.WeatherRepository

class MainViewModel(
    val assetsRepository: AssetsRepository,
    val sharedPreferencesRepository: SharedPreferencesRepository,
    val weatherRepository: WeatherRepository
) : ViewModel() {
    var location by mutableStateOf("臺北市" to "大安區")

    private val screens = mutableStateListOf<Screen>(HomeScreen(this))
    val screen get() = screens.lastOrNull()

    fun push(value: Screen) {
        screens += value
    }

    fun pop() {
        screens.removeLastOrNull()
    }

    class Factory(
        private val assetsRepository: AssetsRepository,
        private val sharedPreferencesRepository: SharedPreferencesRepository,
        private val weatherRepository: WeatherRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            MainViewModel(assetsRepository, sharedPreferencesRepository, weatherRepository) as T
    }
}