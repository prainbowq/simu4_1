package rainbow.weather.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPreferencesRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("weather", Context.MODE_PRIVATE)

    suspend fun getLocations() = withContext(Dispatchers.IO) {
        sharedPreferences
            .getString("locations", null)
            ?.run {
                if (isEmpty()) null
                else split("`,`").map { it.substringBefore("`!`") to it.substringAfter("`!`") }
            }
            .orEmpty()
    }

    suspend fun setLocations(locations: List<Pair<String, String>>) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString("locations", locations.joinToString("`,`") { "${it.first}`!`${it.second}" })
            .commit()
    }

    suspend fun getAlertCodes(): MutableSet<String> = withContext(Dispatchers.IO) {
        sharedPreferences.getStringSet("alerts", emptySet())!!
    }

    suspend fun setAlertsCodes(set: Set<String>) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putStringSet("alerts", set)
            .commit()
    }
}