import com.google.gson.Gson

fun main(args: Array<String>) {
    println("Hello mom!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    //Gson to JSON
    var colors = HashMap<Int, String>()
    colors[1] = "Blue"
    colors[2] = "Red"
    colors[3] = "Purple"

    var gson = Gson()

    var output = gson.toJson(colors)

    println(output);
}