package de.koalaworks.wcts.wordpresscrawler

import com.mashape.unirest.http.Unirest

fun main(args: Array<String>) {
    val result1 = Unirest
            .post("http://localhost:44284/analyse")
            .header("Content-Type", "application/json")
            .body("""{"url": "http://www.fernuni-hagen.de/KSW/portale/babw/service/"}""")
            .asJsonAsync()

    val result2 = Unirest
            .post("http://localhost:44284/analyse")
            .header("Content-Type", "application/json")
            .body("""{"url": "http://www.fernuni-hagen.de/KSW/portale/babw/service/aktuelles/"}""")
            .asJsonAsync();

    val response1 = result1.get()
    val response2 = result2.get()

    val message = if (response1.status == 201 && response2.status === 201) "Success" else "Error"
    println(message)
}
