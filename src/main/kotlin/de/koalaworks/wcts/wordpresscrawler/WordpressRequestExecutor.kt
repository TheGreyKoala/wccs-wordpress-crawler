package de.koalaworks.wcts.wordpresscrawler

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import com.google.gson.reflect.TypeToken
import sun.misc.Request
import java.util.concurrent.Future


open class WordpressRequestExecutor(siteUrl: String, private val restClient: RestClient) {
    private val pagesUrl: String = siteUrl + "/wp-json/wp/v2/pages"
    private val postsUrl: String = siteUrl + "/wp-json/wp/v2/posts"
    private val gson: Gson = Gson()
    private val pageColelctionType = object : TypeToken<Collection<Page>>() {}.type

    private val logger = LoggerFactory.getLogger(WordpressRequestExecutor::class.java)

    open fun download(page: Int, pageSize: Int): RequestResult<String> {
        logger.debug("Downloading pageSize={}, page={}", pageSize, page)
        return RequestResult(true, 100, emptyList())
    }

    fun downloadPages(page: Int, pageSize:Int): RequestResult<Page> {
        return try {
            val response = restClient.
                    get(pagesUrl)
                    .queryString(mapOf("page" to page, "per_page" to pageSize, "context" to "embed"))
                    .asString()

            val pages: Collection<Page> = gson.fromJson(response.body, pageColelctionType)
            val totalPages = response.headers["X-WP-TotalPages"]!![0].toInt()
            RequestResult(true, totalPages, pages)
        } catch (e: Exception) {
            RequestResult(false, -1, emptyList())
        }
    }
}