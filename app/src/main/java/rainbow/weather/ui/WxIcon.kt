package rainbow.weather.ui

import android.annotation.SuppressLint
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import rainbow.weather.hour
import java.util.Date

@SuppressLint("DiscouragedApi")
@Composable
fun WxIcon(id: String, date: Date, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(
            LocalContext.current.resources.getIdentifier(
                "weather_${if (date.hour in 6 until 18) "day" else "night"}_$id",
                "drawable",
                "rainbow.weather"
            )
        ),
        contentDescription = null,
        modifier = modifier
    )
}