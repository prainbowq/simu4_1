package rainbow.weather.data

import org.json.JSONObject
import rainbow.weather.toList

data class County(
    val name: String,
    val shortId: String,
    val longId: String,
    val townships: List<Township>
) {
    companion object {
        fun fromJson(json: JSONObject) = County(
            name = json.getString("name"),
            shortId = json.getString("shortId"),
            longId = json.getString("longId"),
            townships = json.getJSONArray("townships").toList(Township::fromJson)
        )
    }
}