package rainbow.weather.data

import android.content.Context
import org.json.JSONArray
import rainbow.weather.toList

class AssetsRepository(context: Context) {
    val counties = context.assets.open("counties.json")
        .readBytes()
        .decodeToString()
        .let(::JSONArray)
        .toList(County::fromJson)
}