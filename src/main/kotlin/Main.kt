import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDate

fun main(args: Array<String>) {
    val cityName = "London"
    val geo: GeoLocation? = makeRequestGeocode(cityName)

    println("Gelocation after making the request to the API: $geo")

    if (geo != null) {
        makeRequestWeatherData(geo)
    } else {
        makeRequestWeatherData(GeoLocation(emptyArray()))
    }
}

fun makeRequestGeocode(cityName: String): GeoLocation? {
    var responseGeoLocation: GeoLocation? = null;

    var geoLocation = prepareURLGeoCode(cityName)
        .httpGet()
        .responseObject<GeoLocation> { _, _, result ->
            result.success {
                responseGeoLocation = result.get();

                println("Cityname: $cityName's geolocation received! $responseGeoLocation")
            }

            result.failure {
                println("Failure finding the city. Or there might be other issue!")
                println(result.get())
            }
        }.get()

    println("returning $responseGeoLocation")
    return responseGeoLocation
}

fun makeRequestWeatherData(geoLocation: GeoLocation) {
    var responseWeatherObject = prepareURLWeatherData(geoLocation)
            .httpGet()
            .responseObject (CurrentWeather.Deserializer()) { _, _, result ->
            result.failure {
                println("Failure: " + result.get())
            }

            result.success {
                val data = result.get()
                println("API response: $data")
            }
        }.get()
}

fun prepareURLWeatherData(geoLocation: GeoLocation): String {
    var lon: Double? = 0.0;
    var lat: Double? = 0.0;

    if (geoLocation.results.isNotEmpty()) {
        lon = geoLocation.results[0].geometry.lng
        lat = geoLocation.results[0].geometry.lat
        println("lon is => $lon")
        println("lat is => $lat")
    }

    val urlWeatherAPIRequest =
        "https://api.openweathermap.org/data/3.0/onecall/day_summary?lat=${lat}&lon=${lon}&date=2024-01-22&appid=2ffa4cf4ffc5e064e73d6fef24da867f&units=metric";

    return urlWeatherAPIRequest
}

fun prepareURLGeoCode(city: String): String {
    val urlGeocodeAPIRequest =
        "https://api.opencagedata.com/geocode/v1/json?q=${city}&key=39ace01f26ab4c77b1f31ae37e526b3f&limit=1&no_annotations=1&pretty=1";

    return urlGeocodeAPIRequest
}

data class GeoLocation(
    @SerializedName(value = "results")
    val results: Array<GeoResults>
) {
    class Deserializer : ResponseDeserializable<GeoLocation> {
        override fun deserialize(content: String): GeoLocation =
            GsonBuilder()
                .setPrettyPrinting()
                .create()
                .fromJson(content, GeoLocation::class.java)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeoLocation

        return results.contentEquals(other.results)
    }

    override fun hashCode(): Int {
        return results.contentHashCode()
    }
}

data class GeoResults(
    @SerializedName(value = "geometry")
    var geometry: Geometry
)

data class Geometry(
    @SerializedName(value = "lat")
    var lat: Double,

    @SerializedName(value = "lng")
    var lng: Double
)

data class CurrentWeather(
    @SerializedName(value = "lat")
    var lat: Double? = null,

    @SerializedName(value = "lon")
    var lon: Double? = null,

    @SerializedName(value = "temperature")
    var temperature: Temperature? = null,

    @SerializedName(value = "date")
    var date: LocalDate? = null,

    @SerializedName(value = "units")
    var units: Units? = null,

    @SerializedName(value = "pressure")
    var pressure: Pressure? = null,

    @SerializedName(value = "wind")
    var wind: Wind? = null
) {
    class Deserializer : ResponseDeserializable<CurrentWeather> {
        override fun deserialize(content: String): CurrentWeather =
            GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .serializeNulls()
                .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                .setPrettyPrinting()
                .create()
                .fromJson(content, CurrentWeather::class.java)
    }
}

data class Temperature(
    @SerializedName("min")
    var min: Double? = null,

    @SerializedName("max")
    var max: Double? = null,

    @SerializedName("morning")
    var morning: Double? = null,

    @SerializedName("afternoon")
    var afternoon: Double? = null,

    @SerializedName("night")
    var night: Double? = null,

    @SerializedName("evening")
    var evening: Double? = null
)

data class Pressure(
    @SerializedName(value = "afternoon")
    var afternoon: Double? = null
)

data class Wind(
    @SerializedName(value = "max")
    var max: Max? = null
)

data class Max(
    @SerializedName(value = "speed")
    var speed: Double? = null,

    @SerializedName(value = "direction")
    var direction: Double? = null
)

class LocalDateAdapter : TypeAdapter<LocalDate>() {
    override fun write(out: JsonWriter, value: LocalDate?) {
        out.value(value.toString())
    }

    override fun read(`in`: JsonReader): LocalDate? {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        return LocalDate.parse(`in`.nextString())
    }
}

data class User(private var firstName: String, private var lastName: String) {
    constructor() : this("", "")
}





