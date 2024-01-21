import com.google.gson.annotations.SerializedName

enum class Units {
    @SerializedName("metric")
    METRIC,
    @SerializedName("standard")
    STANDARD,
    @SerializedName("imperial")
    IMPERIAL
}