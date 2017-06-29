package de.koalaworks.wcts.wordpresscrawler

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest

fun main(args: Array<String>) {
    val result: HttpResponse<JsonNode>
    if ("1" == args[0]) {
        result = Unirest
                .post("http://localhost:44284/analyse")
                .header("Content-Type", "application/json")
                .body("""{"url": "http://www.fernuni-hagen.de/KSW/portale/babw/service/"}""")
                .asJson()

        val message = if (result.status == 201) "Success" else "Error"
        println("Service: " + message)
    } else if ("2" == args[0]) {
        result = Unirest
                .post("http://localhost:44284/analyse")
                .header("Content-Type", "application/json")
                .body("""{"url": "http://www.fernuni-hagen.de/KSW/portale/babw/service/aktuelles/"}""")
                .asJson()

        val message = if (result.status == 201) "Success" else "Error"
        println("Aktuelles: " + message)
    }
}
