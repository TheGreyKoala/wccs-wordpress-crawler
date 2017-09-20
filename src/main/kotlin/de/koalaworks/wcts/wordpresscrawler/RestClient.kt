package de.koalaworks.wcts.wordpresscrawler

import com.mashape.unirest.http.Unirest

class RestClient {
    fun post(url: String) = Unirest.post(url)
    fun get(url:String) = Unirest.get(url)

}