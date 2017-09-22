package de.koalaworks.wcts.wordpresscrawler.rest

import com.mashape.unirest.http.Unirest

object RestClient {
    fun post(url: String) = Unirest.post(url)
    fun get(url:String) = Unirest.get(url)
    fun shutdown() = Unirest.shutdown()
}