package rainbow.weather

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import rainbow.weather.data.AssetsRepository
import rainbow.weather.data.SharedPreferencesRepository
import rainbow.weather.data.WeatherRepository
import rainbow.weather.ui.MainViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForegroundService(Intent(this, AlertService::class.java))
        val viewModel by viewModels<MainViewModel> {
            val assetsRepository = AssetsRepository(this)
            MainViewModel.Factory(
                assetsRepository = assetsRepository,
                sharedPreferencesRepository = SharedPreferencesRepository(this),
                weatherRepository = WeatherRepository(assetsRepository)
            )
        }
        setContent {
            AnimatedContent(viewModel.screen) {
                it?.invoke() ?: finish()
            }
            BackHandler(onBack = viewModel::pop)
        }
    }
}