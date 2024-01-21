import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.DateFormat
import java.time.LocalDate
import java.util.Date

const val urlWebPage =
    "https://api.openweathermap.org/data/3.0/onecall/day_summary?lat=51.5072&lon=0.1276&date=2024-01-14&appid=2ffa4cf4ffc5e064e73d6fef24da867f&units=metric";

fun main(args: Array<String>) {
    println("Hello mom!")

    var responseWeatherObject =
        urlWebPage.httpGet().responseObject(CurrentWeather.Deserializer()) { _, _, result ->
            println("Hello there")

            result.failure {
                println("Failure: " + result.get())
            }

            result.success {
                val data = result.get()
                println("API response: $data")
            }
        }.get()


    var responseWeatherString =
        urlWebPage.httpGet().responseString { _, _, result ->
            println("Hello there")

            result.failure {
                println("Failure: " + result.get())
            }

            result.success {
                val data = result.get()
                println("API response: $data")
            }
        }.get()
}

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





